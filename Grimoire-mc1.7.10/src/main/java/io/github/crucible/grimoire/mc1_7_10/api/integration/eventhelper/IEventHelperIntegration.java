package io.github.crucible.grimoire.mc1_7_10.api.integration.eventhelper;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.crucible.grimoire.common.api.integration.IModIntegration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Interface representation of EventHelper integration, designed for
 * 1.7.10 specifically. See {@link EHIntegration} for more details on
 * methods functionality.
 *
 * @author Aizistral
 * @see EHIntegration
 */

public interface IEventHelperIntegration extends IModIntegration {

    public boolean canBreak(@NotNull EntityPlayer player, int x, int y, int z);

    public boolean canBreak(@NotNull EntityPlayer player, double x, double y, double z);

    public boolean canDamage(@NotNull Entity attacker, @NotNull Entity victim);

    public boolean canInteract(@NotNull EntityPlayer player, @Nullable ItemStack stack, int x, int y, int z, @NotNull ForgeDirection side);

    public boolean canFromTo(@NotNull World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ);

    public boolean canFromTo(@NotNull World world, int fromX, int fromY, int fromZ, @NotNull ForgeDirection direction);

    public boolean canTeleport(EntityPlayer player, World toWorld, double toX, double toY, double toZ);

    public boolean canTeleport(EntityPlayer player, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ);

    public boolean canTeleport(Entity entity, World toWorld, double toX, double toY, double toZ);

    public boolean canTeleport(Entity entity, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ);

    public boolean isInPrivate(@NotNull World world, int x, int y, int z);

    public boolean isPrivateMember(@NotNull EntityPlayer player, double x, double y, double z);

    public boolean isPrivateMember(@NotNull EntityPlayer player, int x, int y, int z);

    public boolean isPrivateOwner(@NotNull EntityPlayer player, double x, double y, double z);

    public boolean isPrivateOwner(@NotNull EntityPlayer player, int x, int y, int z);

    public boolean isInPrivate(@NotNull Entity entity);

    public boolean hasPermission(@Nullable EntityPlayer player, @NotNull String permission);

    public boolean hasPermission(@Nullable UUID playerId, @NotNull String permission);

    public boolean hasPermission(@Nullable String playerName, @NotNull String permission);

}
