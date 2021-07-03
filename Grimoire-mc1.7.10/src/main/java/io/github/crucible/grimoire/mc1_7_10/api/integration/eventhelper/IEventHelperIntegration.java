package io.github.crucible.grimoire.mc1_7_10.api.integration.eventhelper;

import io.github.crucible.grimoire.common.api.integration.IModIntegration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Interface representation of EventHelper integration, designed for
 * 1.7.10 specifically. See {@link EHIntegration} for more details on
 * methods functionality.
 *
 * @author Aizistral
 * @see EHIntegration
 */

public interface IEventHelperIntegration extends IModIntegration {

    boolean canBreak(@NotNull EntityPlayer player, int x, int y, int z);

    boolean canBreak(@NotNull EntityPlayer player, double x, double y, double z);

    boolean canDamage(@NotNull Entity attacker, @NotNull Entity victim);

    boolean canInteract(@NotNull EntityPlayer player, @Nullable ItemStack stack, int x, int y, int z, @NotNull ForgeDirection side);

    boolean canFromTo(@NotNull World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ);

    boolean canFromTo(@NotNull World world, int fromX, int fromY, int fromZ, @NotNull ForgeDirection direction);

    boolean canTeleport(EntityPlayer player, World toWorld, double toX, double toY, double toZ);

    boolean canTeleport(EntityPlayer player, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ);

    boolean canTeleport(Entity entity, World toWorld, double toX, double toY, double toZ);

    boolean canTeleport(Entity entity, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ);

    boolean isInPrivate(@NotNull World world, int x, int y, int z);

    boolean isPrivateMember(@NotNull EntityPlayer player, double x, double y, double z);

    boolean isPrivateMember(@NotNull EntityPlayer player, int x, int y, int z);

    boolean isPrivateOwner(@NotNull EntityPlayer player, double x, double y, double z);

    boolean isPrivateOwner(@NotNull EntityPlayer player, int x, int y, int z);

    boolean isInPrivate(@NotNull Entity entity);

    boolean hasPermission(@Nullable EntityPlayer player, @NotNull String permission);

    boolean hasPermission(@Nullable UUID playerId, @NotNull String permission);

    boolean hasPermission(@Nullable String playerName, @NotNull String permission);

}
