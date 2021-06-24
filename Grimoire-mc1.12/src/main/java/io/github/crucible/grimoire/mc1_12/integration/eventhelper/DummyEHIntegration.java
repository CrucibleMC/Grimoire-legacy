package io.github.crucible.grimoire.mc1_12.integration.eventhelper;

import java.util.UUID;

import io.github.crucible.grimoire.mc1_12.api.integration.eventhelper.IEventHelperIntegration;
import io.github.crucible.grimoire.mc1_12.handlers.IncelOPChecker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class DummyEHIntegration implements IEventHelperIntegration {

    protected DummyEHIntegration() {
        // NO-OP
    }

    @Override
    public boolean canBreak(EntityPlayer player, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canPlace(EntityPlayer player, BlockPos pos, IBlockState blockState) {
        return true;
    }

    @Override
    public boolean canReplace(EntityPlayer player, BlockPos pos, IBlockState blockState) {
        return true;
    }

    @Override
    public boolean canAttack(EntityPlayer player, Entity victim) {
        return true;
    }

    @Override
    public boolean canInteract(EntityPlayer player, EnumHand hand, BlockPos targetPos, EnumFacing targetSide) {
        return true;
    }

    @Override
    public boolean canInteract(EntityPlayer player, EnumHand hand, BlockPos interactionPos, BlockPos targetPos, EnumFacing targetSide) {
        return true;
    }

    @Override
    public boolean hasPermission(EntityPlayer player, String permission) {
        return IncelOPChecker.isPlayerOP(player);
    }

    @Override
    public boolean hasPermission(UUID playerID, String permission) {
        return IncelOPChecker.isPlayerOP(playerID);
    }

    @Override
    public boolean hasPermission(String playerName, String permission) {
        return IncelOPChecker.isPlayerOP(playerName);
    }

}
