package me.kemal.randomp.gui.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import me.kemal.randomp.net.Packets;
import me.kemal.randomp.te.TileEnergyStorage;
import me.kemal.randomp.te.TileUniversalInterface;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.StringHelper;

public class TabEnergyConf extends TabBase {
	public static boolean enable;
	public static int defaultSide = 1;
	public static int defaultHeaderColor = 0xe1c92f;
	public static int defaultSubHeaderColor = 0xaaafb8;
	public static int defaultTextColor = 0x000000;
	public static int defaultBackgroundColor = 0xd0230a;

	public boolean isShiftDown;

	public static void initialize() {
		String category = "tab.energyconf";
	}

	TileEnergyStorage myContainer;

	boolean output;

	public TabEnergyConf(GuiBase gui, boolean output, TileEnergyStorage container) {
		this(gui, output, defaultSide, container);

	}

	public TabEnergyConf(GuiBase gui, boolean output, int side, TileEnergyStorage container) {
		super(gui, side);
		this.output = output;
		headerColor = defaultHeaderColor;
		subheaderColor = defaultSubHeaderColor;
		textColor = defaultTextColor;
		backgroundColor = defaultBackgroundColor;
		maxHeight = 92;
		maxWidth = 112;
		myContainer = container;
		isShiftDown = false;
	}

	@Override
	public void draw() {
		drawBackground();
		drawTabIcon("IconEnergy");
		if (!isFullyOpened()) {
			return;
		}
		String tabName = (output) ? StringHelper.localize("me.kemal.randomperipheral.maxOutput") : StringHelper.localize("me.kemal.randomperipheral.maxInput");
		int maxEnergyIO = (output) ? myContainer.getEnergyStorage().getMaxExtract() : myContainer.getEnergyStorage().getMaxReceive();
		String currentIO = (output) ? StringHelper.localize("me.kemal.randomperipheral.currentOutput") : StringHelper
				.localize("me.kemal.randomperipheral.currentOutput");
		getFontRenderer().drawStringWithShadow(tabName, posXOffset() + 18, posY + 6, headerColor);
		getFontRenderer().drawStringWithShadow(currentIO + ":", posXOffset() + 6, posY + 42, subheaderColor);
		getFontRenderer().drawStringWithShadow(tabName + ":", posXOffset() + 6, posY + 66, subheaderColor);
		gui.drawButton("IconButton", posX() + 38, posY + 20, 1, 1);
		gui.drawButton("IconButton", posX() + 58, posY + 20, 1, 0);
		gui.drawButton("IconPlus", posX() + 38, posY + 20, 1, 1);
		gui.drawButton("IconMinus", posX() + 58, posY + 20, 1, 0);
		getFontRenderer().drawString(maxEnergyIO + " RF/t", posXOffset() + 14, posY + 54, textColor);
		getFontRenderer().drawString("" + TileUniversalInterface.MAX_ENERGY_IO + " RF/t", posXOffset() + 14, posY + 78, textColor);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void addTooltip(List<String> list) {
		if (!isFullyOpened()) {
			if (output) {
				list.add(StringHelper.localize("me.kemal.randomperipheral.maxOutput") + " " + myContainer.getEnergyStorage().getMaxExtract() + " RF/t");
				return;
			} else {
				list.add(StringHelper.localize("me.kemal.randomperipheral.maxInput") + " " + myContainer.getEnergyStorage().getMaxReceive() + " RF/t");
				return;
			}
			// list.add(StringHelper.localize("info.cofh.enabled") + ", " +
			// StringHelper.localize("info.cofh.high"));
			// return;
		}
		int x = gui.getMouseX() - currentShiftX;
		int y = gui.getMouseY() - currentShiftY;
		String firstButtonToolTip = (output) ? StringHelper.localize("me.kemal.randomperipheral.incOutput") : StringHelper
				.localize("me.kemal.randomperipheral.incInput");
		String lastButtonToolTip = (output) ? StringHelper.localize("me.kemal.randomperipheral.decOutput") : StringHelper
				.localize("me.kemal.randomperipheral.decOutput");
		if (38 <= x && x < 54 && 20 <= y && y < 36) {
			list.add(firstButtonToolTip);
		} else if (58 <= x && x < 74 && 20 <= y && y < 36) {
			list.add(lastButtonToolTip);
		}
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {
		if (!isFullyOpened()) {
			return false;
		}
		if (side == LEFT) {
			mouseX += currentWidth;
		}
		mouseX -= currentShiftX;
		mouseY -= currentShiftY;
		if (mouseX < 24 || mouseX >= 88 || mouseY < 16 || mouseY >= 40) {
			return false;
		}
		if (38 <= mouseX && mouseX < 54 && 20 <= mouseY && mouseY < 36) {
			int add = (isShiftDown) ? 100 : 10;
			if (output) {
				int sumToDec = (myContainer.getEnergyStorage().getMaxExtract() + add >= TileUniversalInterface.MAX_ENERGY_IO) ? TileUniversalInterface.MAX_ENERGY_IO
						: myContainer.getEnergyStorage().getMaxExtract() + add;
				Packets.sendToServer(Packets.ChangeMaxPowerOutput, myContainer, sumToDec);
			} else {
				int sumToDec = (myContainer.getEnergyStorage().getMaxReceive() + add >= TileUniversalInterface.MAX_ENERGY_IO) ? TileUniversalInterface.MAX_ENERGY_IO
						: myContainer.getEnergyStorage().getMaxReceive() + add;
				Packets.sendToServer(Packets.ChangeMaxPowerInput, myContainer, sumToDec);
			}
			GuiBase.playSound("random.click", 1.0F, 0.4F);
		} else if (58 <= mouseX && mouseX < 74 && 20 <= mouseY && mouseY < 36) {
			int dec = (isShiftDown) ? 100 : 10;
			if (output) {
				int sumToDec = (myContainer.getEnergyStorage().getMaxExtract() - dec < 0) ? myContainer.getEnergyStorage().getMaxExtract() : myContainer
						.getEnergyStorage().getMaxExtract() - dec;
				Packets.sendToServer(Packets.ChangeMaxPowerOutput, myContainer, sumToDec);
			} else {
				int sumToDec = (myContainer.getEnergyStorage().getMaxReceive() - dec < 0) ? myContainer.getEnergyStorage().getMaxReceive() : myContainer
						.getEnergyStorage().getMaxReceive() - dec;
				Packets.sendToServer(Packets.ChangeMaxPowerInput, myContainer, sumToDec);
			}
			GuiBase.playSound("random.click", 1.0F, 0.6F);
		}
		return true;
	}

	@Override
	protected void drawBackground() {
		super.drawBackground();
		if (!isFullyOpened()) {
			return;
		}
		float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
		float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
		float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
		GL11.glColor4f(colorR, colorG, colorB, 1.0F);
		gui.drawTexturedModalRect(posX() + 24, posY + 16, 16, 20, 64, 24);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
