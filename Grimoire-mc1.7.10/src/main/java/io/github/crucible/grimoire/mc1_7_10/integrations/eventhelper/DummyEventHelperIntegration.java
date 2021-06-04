package io.github.crucible.grimoire.mc1_7_10.integrations.eventhelper;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class DummyEventHelperIntegration implements IEventHelperIntegration {

    public DummyEventHelperIntegration() {
        // NO-OP
    }

    @Override
    public boolean canBreak(EntityPlayer player, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean canBreak(EntityPlayer player, double x, double y, double z) {
        return true;
    }

    @Override
    public boolean canDamage(Entity attacker, Entity victim) {
        return true;
    }

    @Override
    public boolean canInteract(EntityPlayer player, ItemStack stack, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    public boolean canFromTo(World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return true;
    }

    @Override
    public boolean canFromTo(World world, int fromX, int fromY, int fromZ, ForgeDirection direction) {
        return true;
    }

    @Override
    public boolean canTeleport(EntityPlayer player, World toWorld, double toX, double toY, double toZ) {
        return true;
    }

    @Override
    public boolean canTeleport(EntityPlayer player, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ) {
        return true;
    }

    @Override
    public boolean canTeleport(Entity entity, World toWorld, double toX, double toY, double toZ) {
        return true;
    }

    @Override
    public boolean canTeleport(Entity entity, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ) {
        return true;
    }

    @Override
    public boolean isInPrivate(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isPrivateMember(EntityPlayer player, double x, double y, double z) {
        return true;
    }

    @Override
    public boolean isPrivateMember(EntityPlayer player, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isPrivateOwner(EntityPlayer player, double x, double y, double z) {
        return true;
    }

    @Override
    public boolean isPrivateOwner(EntityPlayer player, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isInPrivate(Entity entity) {
        return false;
    }

    @Override
    public boolean hasPermission(EntityPlayer player, String permission) {
        return true;
    }

    @Override
    public boolean hasPermission(UUID playerId, String permission) {
        return true;
    }

    @Override
    public boolean hasPermission(String playerName, String permission) {
        return true;
    }

    @Override
    public boolean inGodmode(EntityPlayer player) {
        return false;
    }

    @Override
    public void initInjections() {
        // NO-OP
    }

}
