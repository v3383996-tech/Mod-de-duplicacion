package com.dupeslot;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDupe implements IMessage {
    public static final byte DUPLICATE = 0;
    public static final byte RETURN = 1;

    private byte action;

    public PacketDupe() {}

    public PacketDupe(byte action) {
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        action = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(action);
    }

    public static class Handler implements IMessageHandler<PacketDupe, IMessage> {
        @Override
        public IMessage onMessage(PacketDupe msg, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            final byte action = msg.action;
            player.getServerWorld().addScheduledTask(() -> {
                if (action == DUPLICATE) duplicate(player);
                else if (action == RETURN) CommonEvents.giveBack(player);
            });
            return null;
        }
    }

    private static void duplicate(EntityPlayerMP player) {
        DupeSlot slot = CommonEvents.findSlot(player);
        if (slot == null) return;
        ItemStack stack = slot.getStack();
        if (stack.isEmpty() || DupeConfig.isBlacklisted(stack)) return;

        double cost = stack.getCount() * DupeConfig.costPerItem;
        if (!XpUtil.canAfford(player, cost)) {
            player.sendStatusMessage(new TextComponentString("No tienes suficiente experiencia"), true);
            return;
        }
        XpUtil.spend(player, cost);

        ItemStack copy = stack.copy(); // conserva NBT y capabilities -> funciona con items de otros mods
        player.inventory.addItemStackToInventory(copy);
        if (!copy.isEmpty()) player.dropItem(copy, false);
        player.inventoryContainer.detectAndSendChanges();
    }
}
