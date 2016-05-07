package me.kemal.randomp.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.command.CommandBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.event.world.WorldEvent;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.client.renderer.TileHologramSpecialRenderer;
import me.kemal.randomp.common.CommonProxy;
import me.kemal.randomp.net.Packets;
import me.kemal.randomp.te.TileHologram;
import me.kemal.randomp.te.TileHologramProjector;
import me.kemal.randomp.util.RegistryWalker;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(Pre event) {
		if (event.map.getTextureType() == 1) {
			IconRegistry.addIcon("IconConfig", "cofh:icons/Icon_Config", event.map);
			IconRegistry.addIcon("IconArrowUp", "cofh:icons/Icon_ArrowUp", event.map);
			IconRegistry.addIcon("IconArrowDown", "cofh:icons/Icon_ArrowDown", event.map);
			IconRegistry.addIcon("IconEnergy", "cofh:icons/Icon_Energy", event.map);
			IconRegistry.addIcon("IconPlus", "randomperipherals:icons/Icon_Plus", event.map);
			IconRegistry.addIcon("IconMinus", "randomperipherals:icons/Icon_Minus", event.map);
			IconRegistry.addIcon("IconButton", "cofh:icons/Icon_Button", event.map);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderer() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileHologram.class, new TileHologramSpecialRenderer());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void generateItem3DIcons(WorldEvent.Load event) {
		if (event.world.isRemote) {
			RegistryWalker walker = new RegistryWalker();
			walker.walk();

			Minecraft mc = Minecraft.getMinecraft();

			TextureManager tm = mc.getTextureManager();

			Map mapTextureObjects = ObfuscationReflectionHelper.getPrivateValue(TextureManager.class, tm, "mapTextureObjects", "field_110585_a");
			Map newTextureObjects = Maps.newHashMap();
			newTextureObjects.putAll(mapTextureObjects);

			ObfuscationReflectionHelper.setPrivateValue(TextureManager.class, tm, newTextureObjects, "mapTextureObjects", "field_110585_a");

			IconGeneratorScreen screen = new IconGeneratorScreen(new Vector<ItemStack>(walker.items.values()));

			int itemSize = 32;

			int width = mc.displayWidth, height = mc.displayHeight;
			int itemsPerRow = width / itemSize;
			int itemsPerColumn = height / itemSize;
			int itemsPerPage = itemsPerRow * itemsPerColumn;
			int pages = walker.items.size() / itemsPerPage + (walker.items.size() % itemsPerPage != 0 ? 1 : 0);

			ByteArrayOutputStream outStream = new ByteArrayOutputStream(width * height * 2);

			Vector<Integer> firstStacks = new Vector<>();
			Vector<Integer> stacksOnPage = new Vector<>();
			Vector<byte[]> imageDatas = new Vector<>();

			for (int pageI = 0; pageI < pages; pageI++) {
				try {
					Display.swapBuffers();
				} catch (Exception e) {
					e.printStackTrace();
				}

				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

				GL11.glPushMatrix();
				GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
				GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);

				GL11.glViewport(0, 0, width, height);
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				mc.entityRenderer.setupOverlayRendering();

				stacksOnPage.add(screen.drawPage(itemsPerPage * pageI, itemsPerRow, itemsPerColumn));
				firstStacks.add(itemsPerPage * pageI);

				GL11.glPopClientAttrib();
				GL11.glPopAttrib();
				GL11.glPopMatrix();

				GL11.glFlush();
				GL11.glFinish();

				ByteBuffer fb = ByteBuffer.allocateDirect(width * height * 4);
				GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, fb);

				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int i = (x + (width * y)) * 4;
						int r = fb.get(i) & 0xFF;
						int g = fb.get(i + 1) & 0xFF;
						int b = fb.get(i + 2) & 0xFF;
						int a = fb.get(i + 3) & 0xFF;
						image.setRGB(x, height - (y + 1), (a << 24) | (r << 16) | (g << 8) | b);
					}
				}

				try {
					ImageIO.write(image, "png", outStream);
					imageDatas.add(outStream.toByteArray());
					outStream.reset();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			byte[][] imageDataInBytes = new byte[imageDatas.size()][];
			for (int i = 0; i < imageDatas.size(); i++)
				imageDataInBytes[i] = new byte[imageDatas.get(i).length];

			Packets.sendImagesToServer(screen.itemsToRender.toArray(new ItemStack[screen.itemsToRender.size()]), width, height,
					firstStacks.toArray(new Integer[firstStacks.size()]), stacksOnPage.toArray(new Integer[stacksOnPage.size()]),
					imageDatas.toArray(imageDataInBytes));

			ObfuscationReflectionHelper.setPrivateValue(TextureManager.class, tm, mapTextureObjects, "mapTextureObjects", "field_110585_a");
		}
	}
}
