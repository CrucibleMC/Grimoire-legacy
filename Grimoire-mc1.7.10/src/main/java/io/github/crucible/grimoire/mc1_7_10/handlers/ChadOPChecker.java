package io.github.crucible.grimoire.mc1_7_10.handlers;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class ChadOPChecker {

    private ChadOPChecker() {
        // NO-OP
    }

    public static boolean isPlayerOP(EntityPlayer player) {
        return MinecraftServer.getServer() != null && MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
    }

    public static boolean isPlayerOP(UUID playerID) {
        if (MinecraftServer.getServer() != null) {
            for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                if (playerObj instanceof EntityPlayer) {
                    if (((EntityPlayer)playerObj).getUniqueID().equals(playerID))
                        return isPlayerOP((EntityPlayer)playerObj);
                }
            }
        }

        return false;
    }

    public static boolean isPlayerOP(String playerName) {
        if (MinecraftServer.getServer() != null) {
            for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                if (playerObj instanceof EntityPlayer) {
                    if (((EntityPlayer)playerObj).getGameProfile().getName().equals(playerName))
                        return isPlayerOP((EntityPlayer)playerObj);
                }
            }
        }

        return false;
    }

}
