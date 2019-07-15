package com.lothrazar.storagenetwork.block.cablefilter;

import com.lothrazar.storagenetwork.StorageNetwork;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;


public class GuiCableFilter extends ContainerScreen<ContainerCableFilter> {
  private final ResourceLocation texture = new ResourceLocation(StorageNetwork.MODID, "textures/gui/cable.png");

//  protected GuiCableButton btnInputOutputStorage;
  ContainerCableFilter containerCableFilter;

  public GuiCableFilter(ContainerCableFilter containerCableFilter, PlayerInventory inv, ITextComponent name) {
    super(containerCableFilter, inv, name);
    this.containerCableFilter = containerCableFilter;
  }
  @Override
  public void init() {
    super.init();
  }

  @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    minecraft.getTextureManager().bindTexture(texture);
    int xCenter = (width - xSize) / 2;
    int yCenter = (height - ySize) / 2;
    blit(xCenter, yCenter, 0, 0, xSize, ySize);

  }
}