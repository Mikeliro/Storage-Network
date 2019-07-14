package com.lothrazar.storagenetwork.block.request;
import com.google.common.collect.Lists;
import com.lothrazar.storagenetwork.data.ItemStackMatcher;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.gui.ContainerNetworkBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;

import java.util.List;

public   class SlotCraftingNetwork extends CraftingResultSlot {

  private final ContainerNetworkBase parent;

  public SlotCraftingNetwork(ContainerNetworkBase parent,PlayerEntity player,
      CraftingInventory craftingInventory, IInventory inventoryIn,
      int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
  this.parent=parent;
  }

  private TileMaster tileMaster;

  @Override
  public ItemStack onTake(PlayerEntity playerIn, ItemStack stack) {
    if (playerIn.world.isRemote) {
      return stack;
    }
    List<ItemStack> lis = Lists.newArrayList();
    for (int i = 0; i < parent.matrix.getSizeInventory(); i++) {
      lis.add(parent.matrix.getStackInSlot(i).copy());
    }
    StorageNetwork.log("onTake crafting slot");
    super.onTake(playerIn, stack);
    parent.detectAndSendChanges();
    for (int i = 0; i < parent.matrix.getSizeInventory(); i++) {
      if (parent.matrix.getStackInSlot(i).isEmpty() && getTileMaster() != null) {
        ItemStack req = getTileMaster().request(
            !lis.get(i).isEmpty() ? new ItemStackMatcher(lis.get(i), true, false, false) : null, 1, false);
        if (!req.isEmpty()) {
          parent.matrix.setInventorySlotContents(i, req);
        }
      }
    }
    parent.detectAndSendChanges();
    return stack;
  }

  public TileMaster getTileMaster() {
    return tileMaster;
  }

  public void setTileMaster(TileMaster tileMaster) {
    this.tileMaster = tileMaster;
  }
}