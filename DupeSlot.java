package com.dupeslot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** Slot extra del inventario donde se coloca el objeto a duplicar. */
public class DupeSlot extends Slot {
    // Posicion relativa a la esquina del inventario (a la derecha del GUI vanilla)
    public static final int X = 202;
    public static final int Y = 24;

    public DupeSlot(IInventory inv) {
        super(inv, 0, X, Y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !DupeConfig.isBlacklisted(stack);
    }
}
