package io.github.crucible.grimoire.mc1_12.integrations.eventhelper;

import java.util.UUID;

import com.gamerforea.eventhelper.util.EventUtils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class RealEHIntegration implements IEventHelperIntegration {

    protected RealEHIntegration() {
        // NO-OP
    }

    @Override
    public boolean canBreak(EntityPlayer player, BlockPos pos) {
        return !EventUtils.cantBreak(player, pos);
    }

    @Override
    public boolean canPlace(EntityPlayer player, BlockPos pos, IBlockState blockState) {
        return !EventUtils.cantPlace(player, pos, blockState);
    }

    @Override
    public boolean canReplace(EntityPlayer player, BlockPos pos, IBlockState blockState) {
        return !EventUtils.cantReplace(player, pos, blockState);
    }

    @Override
    public boolean canAttack(EntityPlayer player, Entity victim) {
        return !EventUtils.cantAttack(player, victim);
    }

    @Override
    public boolean canInteract(EntityPlayer player, EnumHand hand, BlockPos targetPos, EnumFacing targetSide) {
        return !EventUtils.cantInteract(player, hand, targetPos, targetSide);
    }

    @Override
    public boolean canInteract(EntityPlayer player, EnumHand hand, BlockPos interactionPos, BlockPos targetPos, EnumFacing targetSide) {
        return !EventUtils.cantInteract(player, hand, targetPos, targetSide);
    }

    @Override
    public boolean hasPermission(EntityPlayer player, String permission) {
        return EventUtils.hasPermission(player, permission);
    }

    @Override
    public boolean hasPermission(UUID playerID, String permission) {
        return EventUtils.hasPermission(playerID, permission);
    }

    @Override
    public boolean hasPermission(String playerName, String permission) {
        return EventUtils.hasPermission(playerName, permission);
    }

}
