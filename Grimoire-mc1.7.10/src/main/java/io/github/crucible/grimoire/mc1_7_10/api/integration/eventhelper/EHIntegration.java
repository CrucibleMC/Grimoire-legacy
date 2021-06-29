package io.github.crucible.grimoire.mc1_7_10.api.integration.eventhelper;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.crucible.grimoire.common.integration.ModIntegrationRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Static class that serves as a shortcut to 1.7.10-specific EventHelper integration.
 * It is important to not classload it before or during <code>FMLPreInitializationEvent</code>
 * where actual integration this class proxies calls to is not registered yet.
 *
 * Most EventHelper methods this integration is made to interact with are designed
 * to fire Bukkit events that ensure player is allowed to perform certain action in certain
 * place, or have some specific permission. This is needed to make some of the modded thingies
 * work correctly with WorldGuard regions, for instance. However, since EventHelper refers to
 * Bukkit methods directly, it can only properly load on dedicated servers that support
 * Bukkit API, which makes sure mods that directly refer to its methods in their server code
 * will break in singleplayer.<br><br>
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

    public static boolean canBreak(@NotNull EntityPlayer player, int x, int y, int z) {
        return integration.canBreak(player, x, y, z);
    }

    public static boolean canBreak(@NotNull EntityPlayer player, double x, double y, double z) {
        return integration.canBreak(player, x, y, z);
    }

    public static boolean canDamage(@NotNull Entity attacker, @NotNull Entity victim) {
        return integration.canDamage(attacker, victim);
    }

    public static boolean canInteract(@NotNull EntityPlayer player, @Nullable ItemStack stack, int x, int y, int z, @NotNull ForgeDirection side) {
        return integration.canInteract(player, stack, x, y, z, side);
    }

    public static boolean canFromTo(@NotNull World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return integration.canFromTo(world, fromX, fromY, fromZ, toX, toY, toZ);
    }

    public static boolean canFromTo(@NotNull World world, int fromX, int fromY, int fromZ, @NotNull ForgeDirection direction) {
        return integration.canFromTo(world, fromX, fromY, fromZ, direction);
    }

    public static boolean caneleport(EntityPlayer player, World toWorld, double toX, double toY, double toZ) {
        return integration.canTeleport(player, toWorld, toX, toY, toZ);
    }

    public static boolean caneleport(EntityPlayer player, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ) {
        return integration.canTeleport(player, fromWorld, fromX, fromY, fromZ, toWorld, toX, toY, toZ);
    }

    public static boolean caneleport(Entity entity, World toWorld, double toX, double toY, double toZ) {
        return integration.canTeleport(entity, toWorld, toX, toY, toZ);
    }

    public static boolean caneleport(Entity entity, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ) {
        return integration.canTeleport(entity, fromWorld, fromX, fromY, fromZ, toWorld, toX, toY, toZ);
    }

    public static boolean isInPrivate(@NotNull World world, int x, int y, int z) {
        return integration.isInPrivate(world, x, y, z);
    }

    public static boolean isPrivateMember(@NotNull EntityPlayer player, double x, double y, double z) {
        return integration.isPrivateMember(player, x, y, z);
    }

    public static boolean isPrivateMember(@NotNull EntityPlayer player, int x, int y, int z) {
        return integration.isPrivateMember(player, x, y, z);
    }

    public static boolean isPrivateOwner(@NotNull EntityPlayer player, double x, double y, double z) {
        return integration.isPrivateOwner(player, x, y, z);
    }

    public static boolean isPrivateOwner(@NotNull EntityPlayer player, int x, int y, int z) {
        return integration.isPrivateOwner(player, x, y, z);
    }

    public static boolean isInPrivate(@NotNull Entity entity) {
        return integration.isInPrivate(entity);
    }

    public static boolean hasPermission(@Nullable EntityPlayer player, @NotNull String permission) {
        return integration.hasPermission(player, permission);
    }

    public static boolean hasPermission(@Nullable UUID playerId, @NotNull String permission) {
        return integration.hasPermission(playerId, permission);
    }

    public static boolean hasPermission(@Nullable String playerName, @NotNull String permission) {
        return integration.hasPermission(playerName, permission);
    }

}
