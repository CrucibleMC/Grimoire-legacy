package io.github.crucible.grimoire.mc1_12_2.api.integration.eventhelper;

import java.util.UUID;

import io.github.crucible.grimoire.common.integration.ModIntegrationRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * Static class that serves as a shortcut to 1.12.2-specific EventHelper integration.
 * It is important to not classload it before or during <code>FMLPreInitializationEvent</code>
 * where actual integration this class proxies calls to is not registered yet.<br/><br/>
 *
 * Most EventHelper methods this integration is made to interact with are designed
 * to fire Bukkit events that ensure player is allowed to perform certain action in certain
 * place, or have some specific permission. This is needed to make some of the modded thingies
 * work correctly with WorldGuard regions, for instance. However, since EventHelper refers to
 * Bukkit methods directly, it can only properly load on dedicated servers that support
 * Bukkit API, which makes sure mods that directly refer to its methods in their server code
 * will break in singleplayer.<br/><br/>
 *
 * This integration solves such issue. If EventHelper is present at runtime, it will proxy
 * calls from here to EventHelper; if not, dummy integration instance is created instead,
 * which returns true for all <code>canBreak/canAttack/canPlace/canWhatever</code> checks.
 *
 * @author Aizistral
 */

public class EHIntegration {
    static {
        if (!ModIntegrationRegistry.INSTANCE.isInitialized())
            throw new IllegalStateException("Static class " + EHIntegration.class + " was loaded too early!");
    }

    private static final IEventHelperIntegration integration = ModIntegrationRegistry.INSTANCE.getIntegration(IEventHelperIntegration.class);

    public static boolean canBreak(EntityPlayer player, BlockPos pos) {
        return integration.canBreak(player, pos);
    }

    public static boolean canPlace(EntityPlayer player, BlockPos pos, IBlockState blockState) {
        return integration.canPlace(player, pos, blockState);
    }

    public static boolean canReplace(EntityPlayer player, BlockPos pos, IBlockState blockState) {
        return integration.canReplace(player, pos, blockState);
    }

    public static boolean canAttack(EntityPlayer player, Entity victim) {
        return integration.canAttack(player, victim);
    }

    public static boolean canInteract(EntityPlayer player, EnumHand hand, BlockPos targetPos, EnumFacing targetSide) {
        return integration.canInteract(player, hand, targetPos, targetSide);
    }

    public static boolean canInteract(EntityPlayer player, EnumHand hand, BlockPos interactionPos, BlockPos targetPos, EnumFacing targetSide) {
        return integration.canInteract(player, hand, targetPos, targetSide);
    }

    public static boolean hasPermission(EntityPlayer player, String permission) {
        return integration.hasPermission(player, permission);
    }

    public static boolean hasPermission(UUID playerID, String permission) {
        return integration.hasPermission(playerID, permission);
    }

    public static boolean hasPermission(String playerName, String permission) {
        return integration.hasPermission(playerName, permission);
    }

}
