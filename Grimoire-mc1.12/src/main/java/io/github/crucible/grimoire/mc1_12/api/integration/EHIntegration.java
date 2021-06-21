package io.github.crucible.grimoire.mc1_12.api.integration;

import java.util.UUID;

import io.github.crucible.grimoire.common.integrations.ModIntegrationRegistry;
import io.github.crucible.grimoire.mc1_12.integrations.eventhelper.IEventHelperIntegration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class EHIntegration {
    private static final IEventHelperIntegration integration = ModIntegrationRegistry.getIntegration(IEventHelperIntegration.class);

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
