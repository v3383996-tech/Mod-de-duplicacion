package com.dupeslot;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = DupeMod.MODID, value = Side.CLIENT)
public class ClientEvents {
    private static final int BUTTON_ID = 7731;

    // Panel (relativo al inventario)
    private static final int PANEL_X = 180, PANEL_Y = 8, PANEL_W = 60, PANEL_H = 84;
    private static final int BTN_X = 184, BTN_Y = 46, BTN_W = 52, BTN_H = 20;

    private static GuiButton button;

    @SubscribeEvent
    public static void onInit(GuiScreenEvent.InitGuiEvent.Post e) {
        if (e.getGui() instanceof GuiInventory) {
            GuiContainer gui = (GuiContainer) e.getGui();
            button = new GuiButton(BUTTON_ID, gui.getGuiLeft() + BTN_X, gui.getGuiTop() + BTN_Y,
                    BTN_W, BTN_H, "Duplicar");
            e.getButtonList().add(button);
        }
    }

    /** Mantiene el boton pegado al inventario (el libro de recetas mueve el GUI) y lo activa/desactiva. */
    @SubscribeEvent
    public static void onDrawPre(GuiScreenEvent.DrawScreenEvent.Pre e) {
        if (!(e.getGui() instanceof GuiInventory) || button == null) return;
        GuiContainer gui = (GuiContainer) e.getGui();
        button.x = gui.getGuiLeft() + BTN_X;
        button.y = gui.getGuiTop() + BTN_Y;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        DupeSlot slot = player == null ? null : CommonEvents.findSlot(player);
        boolean ok = slot != null && slot.getHasStack()
                && !DupeConfig.isBlacklisted(slot.getStack())
                && XpUtil.canAfford(player, slot.getStack().getCount() * DupeConfig.costPerItem);
        button.enabled = ok;
    }

    /** Dibuja el panel y el marco del slot a la derecha del inventario. */
    @SubscribeEvent
    public static void onBackground(GuiScreenEvent.BackgroundDrawnEvent e) {
        if (!(e.getGui() instanceof GuiInventory)) return;
        Minecraft mc = Minecraft.getMinecraft();
        GuiContainer gui = (GuiContainer) e.getGui();
        int x = gui.getGuiLeft() + PANEL_X;
        int y = gui.getGuiTop() + PANEL_Y;

        Gui.drawRect(x, y, x + PANEL_W, y + PANEL_H, 0xFF000000);
        Gui.drawRect(x + 1, y + 1, x + PANEL_W - 1, y + PANEL_H - 1, 0xFFC6C6C6);

        int sx = gui.getGuiLeft() + DupeSlot.X - 1;
        int sy = gui.getGuiTop() + DupeSlot.Y - 1;
        Gui.drawRect(sx, sy, sx + 18, sy + 18, 0xFF373737);
        Gui.drawRect(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF8B8B8B);

        mc.fontRenderer.drawString("Copiar", x + (PANEL_W - mc.fontRenderer.getStringWidth("Copiar")) / 2, y + 5, 0x404040);

        DupeSlot slot = mc.player == null ? null : CommonEvents.findSlot(mc.player);
        if (slot != null && slot.getHasStack()) {
            double cost = slot.getStack().getCount() * DupeConfig.costPerItem;
            String t = String.format("-%.2f niv.", cost);
            mc.fontRenderer.drawString(t, x + (PANEL_W - mc.fontRenderer.getStringWidth(t)) / 2, y + 71, 0x404040);
        }
    }

    @SubscribeEvent
    public static void onAction(GuiScreenEvent.ActionPerformedEvent.Post e) {
        if (e.getGui() instanceof GuiInventory && e.getButton() != null && e.getButton().id == BUTTON_ID) {
            DupeMod.NET.sendToServer(new PacketDupe(PacketDupe.DUPLICATE));
        }
    }

    /** Al cerrar el inventario, el servidor devuelve el objeto que quedo en el slot. */
    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.currentScreen instanceof GuiInventory && !(e.getGui() instanceof GuiInventory)) {
            DupeMod.NET.sendToServer(new PacketDupe(PacketDupe.RETURN));
        }
    }
}
