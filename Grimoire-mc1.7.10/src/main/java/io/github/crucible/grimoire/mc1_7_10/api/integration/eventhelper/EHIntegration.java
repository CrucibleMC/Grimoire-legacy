package io.github.crucible.grimoire.mc1_7_10.api.integration.eventhelper;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.crucible.grimoire.common.integrations.ModIntegrationRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EHIntegration {
    static {
        if (!ModIntegrationRegistry.isInitialized())
            throw new IllegalStateException("Static class " + EHIntegration.class + " was loaded too early!");
    }

    private static final IEventHelperIntegration integration = ModIntegrationRegistry.getIntegration(IEventHelperIntegration.class);

    public static boolean canBreak(@Nonnull EntityPlayer player, int x, int y, int z) {
        return integration.canBreak(player, x, y, z);
    }

    public static boolean canBreak(@Nonnull EntityPlayer player, double x, double y, double z) {
        return integration.canBreak(player, x, y, z);
    }

    public static boolean canDamage(@Nonnull Entity attacker, @Nonnull Entity victim) {
        return integration.canDamage(attacker, victim);
    }

    public static boolean canInteract(@Nonnull EntityPlayer player, @Nullable ItemStack stack, int x, int y, int z, @Nonnull ForgeDirection side) {
        return integration.canInteract(player, stack, x, y, z, side);
    }

    public static boolean canFromTo(@Nonnull World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return integration.canFromTo(world, fromX, fromY, fromZ, toX, toY, toZ);
    }

    public static boolean canFromTo(@Nonnull World world, int fromX, int fromY, int fromZ, @Nonnull ForgeDirection direction) {
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

    public static boolean isInPrivate(@Nonnull World world, int x, int y, int z) {
        return integration.isInPrivate(world, x, y, z);
    }

    public static boolean isPrivateMember(@Nonnull EntityPlayer player, double x, double y, double z) {
        return integration.isPrivateMember(player, x, y, z);
    }

    public static boolean isPrivateMember(@Nonnull EntityPlayer player, int x, int y, int z) {
        return integration.isPrivateMember(player, x, y, z);
    }

    public static boolean isPrivateOwner(@Nonnull EntityPlayer player, double x, double y, double z) {
        return integration.isPrivateOwner(player, x, y, z);
    }

    public static boolean isPrivateOwner(@Nonnull EntityPlayer player, int x, int y, int z) {
        return integration.isPrivateOwner(player, x, y, z);
    }

    public static boolean isInPrivate(@Nonnull Entity entity) {
        return integration.isInPrivate(entity);
    }

    public static boolean hasPermission(@Nullable EntityPlayer player, @Nonnull String permission) {
        return integration.hasPermission(player, permission);
    }

    public static boolean hasPermission(@Nullable UUID playerId, @Nonnull String permission) {
        return integration.hasPermission(playerId, permission);
    }

    public static boolean hasPermission(@Nullable String playerName, @Nonnull String permission) {
        return integration.hasPermission(playerName, permission);
    }

}
