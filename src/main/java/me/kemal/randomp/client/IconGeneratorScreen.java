package me.kemal.randomp.client;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class IconGeneratorScreen extends GuiScreen {
	Minecraft mc = Minecraft.getMinecraft();

	List<ItemStack> itemsToRender;

	public IconGeneratorScreen(List<ItemStack> itemStacks) {
		itemsToRender = itemStacks;

		mc = Minecraft.getMinecraft();
		fontRendererObj = mc.fontRenderer;

		TileEntityRendererDispatcher tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
		TextureManager renderEngine = tileEntityRendererDispatcher.field_147553_e;

		if (renderEngine == null) {
			renderEngine = mc.renderEngine;
			Iterator iterator = tileEntityRendererDispatcher.mapSpecialRenderers.values().iterator();
			while (iterator.hasNext()) {
				TileEntitySpecialRenderer tileentityspecialrenderer = (TileEntitySpecialRenderer) iterator.next();
				try {
					tileentityspecialrenderer.func_147497_a(tileEntityRendererDispatcher);
				}catch(Exception e){
				}
			}
		}

	}

	public int drawPage(int listOffset, int itemsInARow, int itemsInAColumn) {
		width = itemsInARow * 32;
		height = itemsInAColumn * 32;

		GL11.glEnable(GL11.GL_COLOR_MATERIAL);

		GL11.glLineWidth(1.0F);
		short short1 = 240;
		short short2 = 240;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);

		RenderHelper.enableGUIStandardItemLighting();

		int i = listOffset;
		for (int x = 0; x < itemsInARow; x++) {
			for (int y = 0; y < itemsInAColumn && i < itemsToRender.size(); y++) {
				boolean probablyCrashs = false;

				try {
					probablyCrashs |= itemsToRender.get(i).getIconIndex() == null;
					probablyCrashs |= itemsToRender.get(i).getItem().getIcon(itemsToRender.get(i), 0) == null;
				} catch (Exception e) {
					probablyCrashs = true;
				}
				// probablyCrashs |=
				// itemsToRender.get(i).getItem().getIcon(itemsToRender.get(i),
				// 1);

				Tessellator t = Tessellator.instance;
				if (t.isDrawing)
					t.draw();
				t.setTranslation(0.0, 0.0, 0.0);				

				if (!probablyCrashs) {

					GL11.glPushMatrix();
					GL11.glTranslatef((float) (x * 16), (float) (y * 16), -3.0F + this.zLevel);

					//RandomPeripherals.logger.info("{}: {},{}", itemsToRender.get(i).getDisplayName(), x, y);

					GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_BLEND);
					RenderHelper.enableGUIStandardItemLighting();
					
					this.zLevel = 100.0F;
					itemRender.zLevel = 100.0F;
					try {
						itemsToRender.get(i).stackSize = 1;
						itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, itemsToRender.get(i), 0, 0);
					} catch (Exception e) {
						e.printStackTrace();
					}

					itemRender.zLevel = 0.0F;
					this.zLevel = 0.0F;

					GL11.glPopMatrix();
				}
				i++;
			}
		}
		return i;
	}
}
