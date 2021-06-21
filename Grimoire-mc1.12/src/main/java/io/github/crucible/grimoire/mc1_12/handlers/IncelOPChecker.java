package io.github.crucible.grimoire.mc1_12.handlers;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class IncelOPChecker {

    private IncelOPChecker() {
        // NO-OP
    }

    public static boolean isPlayerOP(EntityPlayer player) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        return server != null ? server.getPlayerList().getOppedPlayers().getEntry(player.getGameProfile()) != null : false;
    }

    public static boolean isPlayerOP(UUID playerID) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (server != null) {
            for (EntityPlayer player : server.getPlayerList().getPlayers()) {
                if (player.getUniqueID().equals(playerID))
                    return isPlayerOP(player);
            }
        }

        return false;
    }

    public static boolean isPlayerOP(String playerName) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (server != null) {
            for (EntityPlayer player : server.getPlayerList().getPlayers()) {
                if (player.getGameProfile().getName().equals(playerName))
                    return isPlayerOP(player);
            }
        }

        return false;
    }

}

