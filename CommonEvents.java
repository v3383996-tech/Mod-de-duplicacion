package com.dupeslot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = DupeMod.MODID)
public class CommonEvents {

    public static DupeSlot findSlot(EntityPlayer p) {
        for (Slot s : p.inventoryContainer.inventorySlots) {
            if (s instanceof DupeSlot) return (DupeSlot) s;
        }
        return null;
    }

    /** Agrega el slot al inventario del jugador (en cliente y servidor, al final de la lista). */
    private static void ensureSlot(EntityPlayer p) {
        if (p instanceof FakePlayer || findSlot(p) != null) return;
        Container c = p.inventoryContainer;
        Slot slot = new DupeSlot(new InventoryBasic("dupeslot", false, 1));
        slot.slotNumber = c.inventorySlots.size();
        c.inventorySlots.add(slot);
        c.inventoryItemStacks.add(ItemStack.EMPTY);
    }

    /** Devuelve al jugador el objeto que quedo en el slot de duplicado. */
    public static void giveBack(EntityPlayer p) {
        if (p.world.isRemote) return;
        DupeSlot s = findSlot(p);
        if (s == null || !s.getHasStack()) return;
        ItemStack st = s.getStack();
        s.putStack(ItemStack.EMPTY);
        p.inventory.addItemStackToInventory(st);
        if (!st.isEmpty()) p.dropItem(st, false);
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof EntityPlayer) {
            ensureSlot((EntityPlayer) e.getEntity());
        }
    }

    @SubscribeEvent
    public static void onClose(PlayerContainerEvent.Close e) {
        if (e.getContainer() == e.getEntityPlayer().inventoryContainer) {
            giveBack(e.getEntityPlayer());
        }
    }

    @SubscribeEvent
    public static void onLogout(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent e) {
        giveBack(e.player);
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (e.getEntityLiving() instanceof EntityPlayerMP) {
            giveBack((EntityPlayerMP) e.getEntityLiving());
        }
    }
}
