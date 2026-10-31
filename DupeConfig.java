package com.dupeslot;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = DupeMod.MODID)
public class DupeConfig {

    @Config.Comment("Niveles de experiencia que cuesta cada objeto duplicado (0.01 = una centesima de nivel)")
    @Config.RangeDouble(min = 0.0, max = 100.0)
    public static double costPerItem = 0.01;

    @Config.Comment("Objetos que NO se pueden duplicar. Formato modid:nombre (ej: minecraft:bedrock)")
    public static String[] blacklist = new String[0];

    public static boolean isBlacklisted(ItemStack stack) {
        ResourceLocation rl = stack.getItem().getRegistryName();
        if (rl == null) return false;
        String id = rl.toString();
        for (String b : blacklist) {
            if (b.equals(id)) return true;
        }
        return false;
    }

    @Mod.EventBusSubscriber(modid = DupeMod.MODID)
    public static class Sync {
        @SubscribeEvent
        public static void onChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
            if (DupeMod.MODID.equals(e.getModID())) {
                ConfigManager.sync(DupeMod.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
