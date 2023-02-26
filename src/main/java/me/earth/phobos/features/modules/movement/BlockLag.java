
/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockEndRod
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockFalling
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityExpBottle
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package me.earth.phobos.features.modules.movement;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndRod;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockLag
extends Module {
    private static BlockLag INSTANCE;
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.OBSIDIAN));
    private final Setting<Boolean> smartTp = this.register(new Setting<Boolean>("SmartTP", true));
    private final Setting<Integer> tpMin = this.register(new Setting<Integer>("TPMin", Integer.valueOf(2), Integer.valueOf(2), Integer.valueOf(10), v -> this.smartTp.getValue()));
    private final Setting<Integer> tpMax = this.register(new Setting<Integer>("TPMax", Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(40), v -> this.smartTp.getValue()));
    private final Setting<Boolean> noVoid = this.register(new Setting<Boolean>("NoVoid", Boolean.valueOf(false), v -> this.smartTp.getValue()));
    private final Setting<Integer> tpHeight = this.register(new Setting<Integer>("TPHeight", Integer.valueOf(2), Integer.valueOf(-100), Integer.valueOf(100), v -> this.smartTp.getValue() == false));
    private final Setting<Boolean> keepInside = this.register(new Setting<Boolean>("Center", true));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    private final Setting<Boolean> sneaking = this.register(new Setting<Boolean>("Sneak", false));
    private final Setting<Boolean> offground = this.register(new Setting<Boolean>("Offground", false));
    private final Setting<Boolean> chat = this.register(new Setting<Boolean>("Chat Msgs", true));
    private final Setting<Boolean> tpdebug = this.register(new Setting<Boolean>("Debug", Boolean.valueOf(false), v -> this.chat.getValue() != false && this.smartTp.getValue() != false));
    private BlockPos burrowPos;
    private int lastBlock;
    private int blockSlot;

    public BlockLag() {
        super("BlockLag", "Places a block where ur standing.", Module.Category.MOVEMENT, true, false, false);
        INSTANCE = this;
    }

    public static BlockLag getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BlockLag();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        this.burrowPos = new BlockPos(BlockLag.mc.player.posX, Math.ceil(BlockLag.mc.player.posY), BlockLag.mc.player.posZ);
        this.blockSlot = this.findBlockSlot();
        this.lastBlock = BlockLag.mc.player.inventory.currentItem;
        if (!this.doChecks() || this.blockSlot == -1) {
            this.disable();
            return;
        }
        if (this.keepInside.getValue().booleanValue()) {
            double x = BlockLag.mc.player.posX - Math.floor(BlockLag.mc.player.posX);
            double z = BlockLag.mc.player.posZ - Math.floor(BlockLag.mc.player.posZ);
            if (x <= 0.3 || x >= 0.7) {
                double d = x = x > 0.5 ? 0.69 : 0.31;
            }
            if (z < 0.3 || z > 0.7) {
                z = z > 0.5 ? 0.69 : 0.31;
            }
            BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Math.floor(BlockLag.mc.player.posX) + x, BlockLag.mc.player.posY, Math.floor(BlockLag.mc.player.posZ) + z, BlockLag.mc.player.onGround));
            BlockLag.mc.player.setPosition(Math.floor(BlockLag.mc.player.posX) + x, BlockLag.mc.player.posY, Math.floor(BlockLag.mc.player.posZ) + z);
        }
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, BlockLag.mc.player.posY + 0.41999998688698, BlockLag.mc.player.posZ, this.offground.getValue() == false));
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, BlockLag.mc.player.posY + 0.7531999805211997, BlockLag.mc.player.posZ, this.offground.getValue() == false));
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, BlockLag.mc.player.posY + 1.00133597911214, BlockLag.mc.player.posZ, this.offground.getValue() == false));
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, BlockLag.mc.player.posY + 1.16610926093821, BlockLag.mc.player.posZ, this.offground.getValue() == false));
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        if (this.sneaking.getValue().booleanValue() && !BlockLag.mc.player.isSneaking()) {
            BlockLag.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BlockLag.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
        if (this.rotate.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(BlockLag.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)this.burrowPos.getX() + 0.5f), (double)((float)this.burrowPos.getY() + 0.5f), (double)((float)this.burrowPos.getZ() + 0.5f)));
            Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
        }
        InventoryUtil.switchToHotbarSlot(this.blockSlot, false);
        BlockUtil.placeBlock(this.burrowPos, this.blockSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, true, this.sneaking.getValue());
        InventoryUtil.switchToHotbarSlot(this.lastBlock, false);
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(BlockLag.mc.player.posX, this.smartTp.getValue() != false ? (double)this.adaptiveTpHeight(false) : (double)this.tpHeight.getValue().intValue() + BlockLag.mc.player.posY, BlockLag.mc.player.posZ, this.offground.getValue() == false));
        BlockLag.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)BlockLag.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.disable();
    }

    private int findBlockSlot() {
        for (Class block : this.blockList()) {
            if (block == BlockFalling.class) {
                for (int i = 0; i < 9; ++i) {
                    Block blockFalling;
                    ItemStack item = BlockLag.mc.player.inventory.getStackInSlot(i);
                    if (!(item.getItem() instanceof ItemBlock) || !((blockFalling = Block.getBlockFromItem((Item)BlockLag.mc.player.inventory.getStackInSlot(i).getItem())) instanceof BlockFalling)) continue;
                    return i;
                }
                continue;
            }
            int slot = InventoryUtil.findHotbarBlock(block);
            if (slot != -1) {
                return slot;
            }
            if (!InventoryUtil.isBlock(BlockLag.mc.player.getHeldItemOffhand().getItem(), block)) continue;
            return -2;
        }
        if (this.chat.getValue().booleanValue()) {
            Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cNo blocks to use.");
        }
        return -1;
    }

    private List<Class> blockList() {
        ArrayList<Class> blocks = new ArrayList<Class>();
        blocks.add(this.mode.getValue().getPriorityBlock());
        for (Mode block : Mode.values()) {
            if (this.mode.getValue() == block) continue;
            blocks.add(block.getPriorityBlock());
        }
        return blocks;
    }

    private int adaptiveTpHeight(boolean first) {
        int airblock;
        int max = BlockLag.mc.player.dimension == -1 && this.noVoid.getValue() != false && this.tpMax.getValue() + this.burrowPos.getY() > 127 ? Math.abs(this.burrowPos.getY() - 127) : this.tpMax.getValue();
        int n = airblock = this.noVoid.getValue() != false && this.tpMax.getValue() * -1 + this.burrowPos.getY() < 0 ? this.burrowPos.getY() * -1 : this.tpMax.getValue() * -1;
        while (airblock < max) {
            if (Math.abs(airblock) < this.tpMin.getValue() || !BlockLag.mc.world.isAirBlock(this.burrowPos.offset(EnumFacing.UP, airblock)) || !BlockLag.mc.world.isAirBlock(this.burrowPos.offset(EnumFacing.UP, airblock + 1))) {
                ++airblock;
                continue;
            }
            if (this.tpdebug.getValue().booleanValue() && this.chat.getValue().booleanValue() && !first) {
                Command.sendMessage(Integer.toString(airblock));
            }
            return this.burrowPos.getY() + airblock;
        }
        return 69420;
    }

    private boolean doChecks() {
        if (BlockLag.fullNullCheck()) {
            return false;
        }
        if (BlockUtil.isPositionPlaceable(this.burrowPos, false, false) < 1) {
            return false;
        }
        if (this.smartTp.getValue().booleanValue() && this.adaptiveTpHeight(true) == 69420) {
            if (this.chat.getValue().booleanValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cNot enough room to rubberband.");
            }
            return false;
        }
        if (!BlockLag.mc.world.isAirBlock(this.burrowPos.offset(EnumFacing.UP, 2))) {
            if (this.chat.getValue().booleanValue()) {
                Command.sendMessage("<" + this.getDisplayName() + "> \u00a7cNot enough room to jump.");
            }
            return false;
        }
        for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.burrowPos))) {
            if (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow || entity instanceof EntityPlayer || entity instanceof EntityExpBottle) continue;
            return false;
        }
        return true;
    }

    private static enum Mode {
        OBSIDIAN(BlockObsidian.class),
        ECHEST(BlockEnderChest.class),
        FALLING(BlockFalling.class),
        ENDROD(BlockEndRod.class);

        private final Class priorityBlock;

        private Mode(Class block) {
            this.priorityBlock = block;
        }

        private Class getPriorityBlock() {
            return this.priorityBlock;
        }
    }
}

