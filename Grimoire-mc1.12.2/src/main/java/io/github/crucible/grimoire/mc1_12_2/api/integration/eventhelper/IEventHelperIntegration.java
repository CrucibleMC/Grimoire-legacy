package io.github.crucible.grimoire.mc1_12_2.api.integration.eventhelper;

import java.util.UUID;

import javax.annotation.Nonnull;

import io.github.crucible.grimoire.common.integrations.IModIntegration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public interface IEventHelperIntegration extends IModIntegration {

    public boolean canBreak(@Nonnull EntityPlayer player, @Nonnull BlockPos pos);

    public boolean canPlace(@Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState blockState);

    public boolean canReplace(@Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState blockState);

    public boolean canAttack(@Nonnull EntityPlayer player, @Nonnull Entity victim);

    public boolean canInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide);

    public boolean canInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide);

    public boolean hasPermission(@Nonnull EntityPlayer player, @Nonnull String permission);

    public boolean hasPermission(@Nonnull UUID playerID, @Nonnull String permission);

    public boolean hasPermission(@Nonnull String playerName, @Nonnull String permission);

}
