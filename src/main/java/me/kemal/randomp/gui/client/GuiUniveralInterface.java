package me.kemal.randomp.gui.client;

import java.util.List;

import javax.vecmath.Color3f;

import org.lwjgl.util.Color;

import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.client.IconRegistry;
import me.kemal.randomp.gui.container.ContainerUniversalInterface;
import me.kemal.randomp.te.TileUniversalInterface;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.TabTracker;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementButtonManaged;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementFluidTank;
import cofh.lib.gui.element.ElementSlider;
import cofh.lib.gui.element.TabBase;
import cofh.lib.render.RenderHelper;

public class GuiUniveralInterface extends GuiBase {
	private ElementEnergyStored energyDisplay;
	private TabBase maxEnergyOutputTab;
	private ElementFluidTank fluidDisplay;
	private TabEnergyConf maxOutputTab;
	private TabEnergyConf maxInputTab;

	private TileUniversalInterface te;

	public GuiUniveralInterface(InventoryPlayer playerInv, TileUniversalInterface te) {
		super(new ContainerUniversalInterface(playerInv, te), new ResourceLocation(RandomPeripherals.modid.toLowerCase()
				+ ":textures/gui/univeralInterface.png"));
		this.te = te;
	}

	@Override
	public void initGui() {
		super.initGui();
		maxEnergyOutputTab = new TabConfiguration(this, te);
		energyDisplay = new ElementEnergyStored(this, 8, 20, te.getEnergyStorage());
		fluidDisplay = new ElementFluidTank(this, 176 - (8 + 16), 8, te.getTank());
		maxOutputTab = new TabEnergyConf(this, true, te);
		maxInputTab = new TabEnergyConf(this, false, te);
		this.elements.add(energyDisplay);
		this.elements.add(fluidDisplay);
		this.tabs.add(maxEnergyOutputTab);
		//this.tabs.add(maxOutputTab);
		//this.tabs.add(maxInputTab);
		
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.universalInterface"), 8, 8, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {
		super.drawGuiContainerBackgroundLayer(partialTick, x, y);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		maxOutputTab.isShiftDown = isShiftKeyDown();
		maxInputTab.isShiftDown = isShiftKeyDown();
	}

	@Override
	public IIcon getIcon(String name) {
		return IconRegistry.getIcon(name);
	}

}
