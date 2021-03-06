package com.lothrazar.storagenetwork.item.remote;

import java.util.List;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.EnumSortType;
import com.lothrazar.storagenetwork.api.IGuiNetwork;
import com.lothrazar.storagenetwork.gui.NetworkWidget;
import com.lothrazar.storagenetwork.jei.JeiHooks;
import com.lothrazar.storagenetwork.jei.JeiSettings;
import com.lothrazar.storagenetwork.network.ClearRecipeMessage;
import com.lothrazar.storagenetwork.network.RequestMessage;
import com.lothrazar.storagenetwork.network.SortMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiNetworkCraftingRemote extends ContainerScreen<ContainerNetworkCraftingRemote> implements IGuiNetwork {

  private static final int HEIGHT = 256;
  private static final int WIDTH = 176;
  private final ResourceLocation textureCraft = new ResourceLocation(StorageNetwork.MODID, "textures/gui/request.png");
  private final NetworkWidget network;
  private final ItemStack remote;

  public GuiNetworkCraftingRemote(ContainerNetworkCraftingRemote screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    //since the rightclick action forces only MAIN_HAND openings, is ok
    this.remote = inv.player.getHeldItem(Hand.MAIN_HAND);
    network = new NetworkWidget(this);
    network.setLines(4);
    this.xSize = WIDTH;
    this.ySize = HEIGHT;
    network.fieldHeight = 90;
  }

  @Override
  public void setStacks(List<ItemStack> stacks) {
    network.stacks = stacks;
  }

  @Override
  public boolean getDownwards() {
    return ItemRemote.getDownwards(remote);
  }

  @Override
  public void setDownwards(boolean val) {
    ItemRemote.setDownwards(remote, val);
  }

  @Override
  public EnumSortType getSort() {
    return ItemRemote.getSort(remote);
  }

  @Override
  public void setSort(EnumSortType val) {
    ItemRemote.setSort(remote, val);
  }

  @Override
  public void init() {
    super.init();
    int searchLeft = guiLeft + 81, searchTop = guiTop + 160, width = 85;
    searchTop = guiTop + 96;
    network.searchBar = new TextFieldWidget(font,
        searchLeft, searchTop,
        width, font.FONT_HEIGHT, "search");
    network.searchBar.setMaxStringLength(30);
    network.initSearchbar();
    network.initButtons();
    this.addButton(network.directionBtn);
    this.addButton(network.sortBtn);
    if (JeiSettings.isJeiLoaded()) {
      addButton(network.jeiBtn);
    }
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
    network.searchBar.render(mouseX, mouseY, partialTicks);
    network.render();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.minecraft.getTextureManager().bindTexture(textureCraft);
    int k = (this.width - this.xSize) / 2;
    int l = (this.height - this.ySize) / 2;
    RenderSystem.color3f(1, 1, 1);
    this.blit(k, l, 0, 0, this.xSize, this.ySize);
    network.applySearchTextToSlots();
    network.renderItemSlots(mouseX, mouseY, font);
  }

  @Override
  public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    network.drawGuiContainerForegroundLayer(mouseX, mouseY);
  }

  boolean isScrollable(double x, double y) {
    int scrollHeight = 135;
    return isPointInRegion(0, 0,
        this.width - 8, scrollHeight,
        x, y);
  }

  @Override
  public boolean mouseScrolled(double x, double y, double mouseButton) {
    super.mouseScrolled(x, y, mouseButton);
    //<0 going down
    // >0 going up
    if (isScrollable(x, y) && mouseButton != 0) {
      network.mouseScrolled(mouseButton);
    }
    return true;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    network.mouseClicked(mouseX, mouseY, mouseButton);
    //recipe clear thingy
    //TODO: network needs isCrafting and isPointInRegion access to refactor
    // OR make real button lol
    int rectX = 63;
    int rectY = 110;
    if (isPointInRegion(rectX, rectY, 7, 7, mouseX, mouseY)) {
      PacketRegistry.INSTANCE.sendToServer(new ClearRecipeMessage());
      PacketRegistry.INSTANCE.sendToServer(new RequestMessage(0, ItemStack.EMPTY, false, false));
      return true;
    }
    return true;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int b) {
    InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
    if (keyCode == 256) {
      minecraft.player.closeScreen();
      return true; // Forge MC-146650: Needs to return true when the key is handled.
    }
    if (network.searchBar.isFocused()) {
      if (keyCode == 259) {// BACKSPACE
        network.syncTextToJei();
      }
      network.searchBar.keyPressed(keyCode, scanCode, b);
      return true;
    }
    else if (network.stackUnderMouse.isEmpty()) {
      try {
        JeiHooks.testJeiKeybind(mouseKey, network.stackUnderMouse);
      }
      catch (Throwable e) {
        StorageNetwork.LOGGER.error("JEI compat issue ", e);
      }
    }
    //regardles of above branch, also check this
    if (minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
      minecraft.player.closeScreen();
      return true; // Forge MC-146650: Needs to return true when the key is handled.
    }
    return super.keyPressed(keyCode, scanCode, b);
  }

  @Override
  public boolean charTyped(char typedChar, int keyCode) {
    if (network.charTyped(typedChar, keyCode)) {
      return true;
    }
    return false;// super.charTyped(typedChar, keyCode);
  }

  @Override
  public void renderStackToolTip(ItemStack stack, int x, int y) {
    super.renderTooltip(stack, x, y);
  }

  @Override
  public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
    super.fillGradient(left, top, right, bottom, startColor, endColor);
  }

  @Override
  public boolean isInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
    return super.isPointInRegion(x, y, width, height, mouseX, mouseY);
  }

  @Override
  public void syncDataToServer() {
    PacketRegistry.INSTANCE.sendToServer(new SortMessage(null, getDownwards(), getSort()));
  }
}
