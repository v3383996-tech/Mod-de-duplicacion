package com.dupeslot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSetExperience;

/** Manejo de experiencia con niveles fraccionarios (nivel + progreso de la barra). */
public final class XpUtil {
    private XpUtil() {}

    public static boolean canAfford(EntityPlayer p, double levels) {
        if (p.capabilities.isCreativeMode) return true;
        return (p.experienceLevel + (double) p.experience) + 1e-6 >= levels;
    }

    public static void spend(EntityPlayer p, double levels) {
        if (p.capabilities.isCreativeMode || levels <= 0) return;
        double total = p.experienceLevel + (double) p.experience - levels;
        if (total < 0) total = 0;
        int lvl = (int) Math.floor(total);
        float prog = (float) (total - lvl);
        p.experienceLevel = lvl;
        p.experience = prog;
        p.experienceTotal = pointsForLevel(lvl) + Math.round(prog * pointsToNext(lvl));
        if (p instanceof EntityPlayerMP) {
            ((EntityPlayerMP) p).connection.sendPacket(
                    new SPacketSetExperience(p.experience, p.experienceTotal, p.experienceLevel));
        }
    }

    public static int pointsToNext(int l) {
        if (l >= 30) return 112 + (l - 30) * 9;
        if (l >= 15) return 37 + (l - 15) * 5;
        return 7 + l * 2;
    }

    public static int pointsForLevel(int l) {
        if (l <= 16) return l * l + 6 * l;
        if (l <= 31) return (int) Math.round(2.5 * l * l - 40.5 * l + 360);
        return (int) Math.round(4.5 * l * l - 162.5 * l + 2220);
    }
}
