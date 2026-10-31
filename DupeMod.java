package com.dupeslot;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = DupeMod.MODID, name = "Duplicador de Items", version = "1.0.0", acceptedMinecraftVersions = "[1.12.2]")
public class DupeMod {
    public static final String MODID = "dupeslot";
    public static SimpleNetworkWrapper NET;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        NET = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        NET.registerMessage(PacketDupe.Handler.class, PacketDupe.class, 0, Side.SERVER);
    }
}
