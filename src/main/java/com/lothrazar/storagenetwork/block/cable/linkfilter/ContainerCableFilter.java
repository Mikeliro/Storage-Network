package com.lothrazar.storagenetwork.block.cable.linkfilter;

import javax.annotation.Nullable;
import com.lothrazar.storagenetwork.api.IConnectableLink;
import com.lothrazar.storagenetwork.block.cable.ContainerCable;
import com.lothrazar.storagenetwork.capability.CapabilityConnectableLink;
import com.lothrazar.storagenetwork.registry.SsnRegistry;
import com.lothrazar.storagenetwork.registry.StorageNetworkCapabilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerCableFilter extends ContainerCable {

  public final TileCableFilter tile;
  @Nullable
  public CapabilityConnectableLink cap;

  public ContainerCableFilter(int windowId, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player) {
    super(SsnRegistry.filterContainer, windowId);
    tile = (TileCableFilter) world.getTileEntity(pos);
    IConnectableLink rawLink = tile.getCapability(StorageNetworkCapabilities.CONNECTABLE_ITEM_STORAGE_CAPABILITY, null).orElse(null);
    if (!(rawLink instanceof CapabilityConnectableLink)) {
      return;
    }
    this.cap = (CapabilityConnectableLink) rawLink;
    this.bindPlayerInvo(playerInv);
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }
}
