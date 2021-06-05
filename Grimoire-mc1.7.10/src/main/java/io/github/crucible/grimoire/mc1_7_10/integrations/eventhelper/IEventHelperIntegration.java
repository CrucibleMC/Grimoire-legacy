package io.github.crucible.grimoire.mc1_7_10.integrations.eventhelper;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IEventHelperIntegration {

    public boolean canBreak(@Nonnull EntityPlayer player, int x, int y, int z);

    public boolean canBreak(@Nonnull EntityPlayer player, double x, double y, double z);

    public boolean canDamage(@Nonnull Entity attacker, @Nonnull Entity victim);

    public boolean canInteract(@Nonnull EntityPlayer player, @Nullable ItemStack stack, int x, int y, int z, @Nonnull ForgeDirection side);

    public boolean canFromTo(@Nonnull World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ);

    public boolean canFromTo(@Nonnull World world, int fromX, int fromY, int fromZ, @Nonnull ForgeDirection direction);

    public boolean canTeleport(EntityPlayer player, World toWorld, double toX, double toY, double toZ);

    public boolean canTeleport(EntityPlayer player, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ);

    public boolean canTeleport(Entity entity, World toWorld, double toX, double toY, double toZ);

    public boolean canTeleport(Entity entity, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ);

    public boolean isInPrivate(@Nonnull World world, int x, int y, int z);

    public boolean isPrivateMember(@Nonnull EntityPlayer player, double x, double y, double z);

    public boolean isPrivateMember(@Nonnull EntityPlayer player, int x, int y, int z);

    public boolean isPrivateOwner(@Nonnull EntityPlayer player, double x, double y, double z);

    public boolean isPrivateOwner(@Nonnull EntityPlayer player, int x, int y, int z);

    public boolean isInPrivate(@Nonnull Entity entity);

    public boolean hasPermission(@Nullable EntityPlayer player, @Nonnull String permission);

    public boolean hasPermission(@Nullable UUID playerId, @Nonnull String permission);

    public boolean hasPermission(@Nullable String playerName, @Nonnull String permission);

    public void initInjections();

}
