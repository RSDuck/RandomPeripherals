package me.kemal.randomp.client.renderer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.OpenGLException;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.client.ClientProxy;
import me.kemal.randomp.te.TileHologramProjector;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class TileHologramSpecialRenderer extends TileEntitySpecialRenderer {

	private Field fVertexCount;
	private Field fRawBuffer;
	private Field fRawBufferSize;
	private Field fRawBufferIndex;
	private Field fHasColor;
	private Field fHasBrightness;

	private int displayList;

	public TileHologramSpecialRenderer() {

		try {
			fVertexCount = Tessellator.class.getDeclaredField("vertexCount");
			fVertexCount.setAccessible(true);

			fRawBuffer = Tessellator.class.getDeclaredField("rawBuffer");
			fRawBuffer.setAccessible(true);

			fRawBufferSize = Tessellator.class.getDeclaredField("rawBufferSize");
			fRawBufferSize.setAccessible(true);

			fRawBufferIndex = Tessellator.class.getDeclaredField("rawBufferIndex");
			fRawBufferIndex.setAccessible(true);

			fHasColor = Tessellator.class.getDeclaredField("hasColor");
			fHasColor.setAccessible(true);

			fHasBrightness = Tessellator.class.getDeclaredField("hasBrightness");
			fHasBrightness.setAccessible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
		TileEntity projector = tile.getWorldObj().getTileEntity(tile.xCoord, tile.yCoord - 1, tile.zCoord);
		if (projector instanceof TileHologramProjector) {
				Tessellator t = Tessellator.instance;

				t.startDrawingQuads();

				IBlockAccess backupAccess = RenderBlocks.getInstance().blockAccess;
				RenderBlocks.getInstance().blockAccess = (IBlockAccess) projector;

				try {
					int previousVertexCount = fVertexCount.getInt(t);

					for (int y1 = 0; y1 < TileHologramProjector.hologramHeight; y1++) {
						for (int z1 = 0; z1 < TileHologramProjector.hologramDepth; z1++) {
							for (int x1 = 0; x1 < TileHologramProjector.hologramWidth; x1++) {
								try {
									RenderBlocks.getInstance().renderBlockByRenderType(
											((TileHologramProjector) projector).getBlock(x1, y1, z1), x1, y1, z1);
								} catch (Exception e) {
								}
								if (fVertexCount.getInt(t) == previousVertexCount
										&& !((TileHologramProjector) projector).isAirBlock(x1, y1, z1)) {
									RenderBlocks.getInstance().renderStandardBlock(
											((TileHologramProjector) projector).getBlock(x1, y1, z1), x1, y1, z1);
								}
								previousVertexCount = fVertexCount.getInt(t);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				RenderBlocks.getInstance().blockAccess = backupAccess;

				try {
					fHasBrightness.setBoolean(t, false);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}

				GL11.glPushMatrix();

				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
				RenderHelper.disableStandardItemLighting();

				GL11.glTranslated(x, y, z);
				GL11.glScalef(0.125f, 0.125f, 0.125f);
				GL11.glColor4f(1.f, 1.f, 1.f, 1.f);

				t.draw();

				RenderHelper.enableStandardItemLighting();

				GL11.glPopMatrix();
		}
	}

}
