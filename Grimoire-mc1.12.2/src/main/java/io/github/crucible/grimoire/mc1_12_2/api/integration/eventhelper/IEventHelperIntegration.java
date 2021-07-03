package io.github.crucible.grimoire.mc1_12_2.api.integration.eventhelper;

import java.util.UUID;

import javax.annotation.Nonnull;

import io.github.crucible.grimoire.common.api.integration.IModIntegration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * Interface representation of EventHelper integration, designed for
 * 1.12.2 specifically. See {@link EHIntegration} for more details on
 * methods functionality.
 *
 * @author Aizistral
 * @see EHIntegration
 */

public interface IEventHelperIntegration extends IModIntegration {

    boolean canBreak(@Nonnull EntityPlayer player, @Nonnull BlockPos pos);

    boolean canPlace(@Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState blockState);

    boolean canReplace(@Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState blockState);

    boolean canAttack(@Nonnull EntityPlayer player, @Nonnull Entity victim);

    boolean canInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide);

    boolean canInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide);

    boolean hasPermission(@Nonnull EntityPlayer player, @Nonnull String permission);

    boolean hasPermission(@Nonnull UUID playerID, @Nonnull String permission);

    boolean hasPermission(@Nonnull String playerName, @Nonnull String permission);

}
