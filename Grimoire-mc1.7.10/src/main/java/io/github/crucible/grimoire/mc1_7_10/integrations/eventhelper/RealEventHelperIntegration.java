package io.github.crucible.grimoire.mc1_7_10.integrations.eventhelper;

import com.gamerforea.eventhelper.util.EventUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class RealEventHelperIntegration implements IEventHelperIntegration {
//    private final List<IEssentialsInjection> injections = new ArrayList<>();

    public RealEventHelperIntegration() {
        // NO-OP
    }

    @Override
    public boolean canBreak(@Nonnull EntityPlayer player, int x, int y, int z) {
        return !EventUtils.cantBreak(player, x, y, z);
    }

    @Override
    public boolean canBreak(@Nonnull EntityPlayer player, double x, double y, double z) {
        return !EventUtils.cantBreak(player, x, y, z);
    }

    @Override
    public boolean canDamage(@Nonnull Entity attacker, @Nonnull Entity victim) {
        return !EventUtils.cantDamage(attacker, victim);
    }

    @Override
    public boolean canInteract(@Nonnull EntityPlayer player, @Nullable ItemStack stack, int x, int y, int z, @Nonnull ForgeDirection side) {
        return !EventUtils.cantInteract(player, stack, x, y, z, side);
    }

    @Override
    public boolean canFromTo(@Nonnull World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return !EventUtils.cantFromTo(world, fromX, fromY, fromZ, toX, toY, toZ);
    }

    @Override
    public boolean canFromTo(@Nonnull World world, int fromX, int fromY, int fromZ, @Nonnull ForgeDirection direction) {
        return !EventUtils.cantFromTo(world, fromX, fromY, fromZ, direction);
    }

    @Override
    public boolean canTeleport(EntityPlayer player, World toWorld, double toX, double toY, double toZ) {
        return !EventUtils.cantTeleport(player, toWorld, toX, toY, toZ);
    }

    @Override
    public boolean canTeleport(EntityPlayer player, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ) {
        return !EventUtils.cantTeleport(player, fromWorld, fromX, fromY, fromZ, toWorld, toX, toY, toZ);
    }

    @Override
    public boolean canTeleport(Entity entity, World toWorld, double toX, double toY, double toZ) {
        return !EventUtils.cantTeleport(entity, toWorld, toX, toY, toZ);
    }

    @Override
    public boolean canTeleport(Entity entity, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ) {
        return !EventUtils.cantTeleport(entity, fromWorld, fromX, fromY, fromZ, toWorld, toX, toY, toZ);
    }

    @Override
    public boolean isInPrivate(@Nonnull World world, int x, int y, int z) {
        return EventUtils.isInPrivate(world, x, y, z);
    }

    @Override
    public boolean isPrivateMember(@Nonnull EntityPlayer player, double x, double y, double z) {
        return EventUtils.isPrivateMember(player, x, y, z);
    }

    @Override
    public boolean isPrivateMember(@Nonnull EntityPlayer player, int x, int y, int z) {
        return EventUtils.isPrivateMember(player, x, y, z);
    }

    @Override
    public boolean isPrivateOwner(@Nonnull EntityPlayer player, double x, double y, double z) {
        return EventUtils.isPrivateOwner(player, x, y, z);
    }

    @Override
    public boolean isPrivateOwner(@Nonnull EntityPlayer player, int x, int y, int z) {
        return EventUtils.isPrivateOwner(player, x, y, z);
    }

    @Override
    public boolean isInPrivate(@Nonnull Entity entity) {
        return EventUtils.isInPrivate(entity);
    }

    @Override
    public boolean hasPermission(@Nullable EntityPlayer player, @Nonnull String permission) {
        return EventUtils.hasPermission(player, permission);
    }

    @Override
    public boolean hasPermission(@Nullable UUID playerId, @Nonnull String permission) {
        return EventUtils.hasPermission(playerId, permission);
    }

    @Override
    public boolean hasPermission(@Nullable String playerName, @Nonnull String permission) {
        return EventUtils.hasPermission(playerName, permission);
    }

    @Override
    public boolean inGodmode(EntityPlayer player) {
//        for (IEssentialsInjection inj : this.injections)
//            if (inj.inGodMode(player))
//                return true;

        return false;
    }

    @Override
    public void initInjections() {
//        IEssentialsInjection essentialsInj = EssentialsInjection.getInjection();
//        if (essentialsInj != null) {
//            this.injections.add(essentialsInj);
//        }
    }

}
