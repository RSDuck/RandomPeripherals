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
import net.minecraft.client.renderer.GLAllocation;
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

	public TileHologramSpecialRenderer() {

		try {
			fVertexCount = Tessellator.class.getDeclaredField("vertexCount");
			fVertexCount.setAccessible(true);

			fRawBuffer = Tessellator.class.getDeclaredField("rawBuffer");
			fRawBuffer.setAccessible(true);

			fHasBrightness = Tessellator.class.getDeclaredField("hasBrightness");
			fHasBrightness.setAccessible(true);

		} catch (Exception e) {
			try {
				fVertexCount = Tessellator.class.getDeclaredField("field_78406_i");// Vertex
																					// Count
				fVertexCount.setAccessible(true);

				fRawBuffer = Tessellator.class.getDeclaredField("field_78405_h");// Rawbuffer
				fRawBuffer.setAccessible(true);

				fHasBrightness = Tessellator.class.getDeclaredField("field_78414_p");// hasBrightness
				fHasBrightness.setAccessible(true);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
		TileEntity te = tile.getWorldObj().getTileEntity(tile.xCoord, tile.yCoord - 1, tile.zCoord);
		if (te instanceof TileHologramProjector) {
			Tessellator t = Tessellator.instance;

			TileHologramProjector projector = (TileHologramProjector) te;

			if (projector.doGraphicsNeedAnUpdate()) {
				if (projector.getDisplayListID() == -1)
					projector.setDisplayListID(GLAllocation.generateDisplayLists(1));

				t.startDrawingQuads();

				IBlockAccess backupAccess = RenderBlocks.getInstance().blockAccess;
				RenderBlocks.getInstance().blockAccess = (IBlockAccess) projector;

				t.setTranslation(-(double) TileHologramProjector.hologramWidth / 2.0, 0.0,
						-(double) TileHologramProjector.hologramDepth / 2.0);

				try {
					int previousVertexCount = fVertexCount.getInt(t);

					for (int y1 = 0; y1 < TileHologramProjector.hologramHeight; y1++) {
						for (int z1 = 0; z1 < TileHologramProjector.hologramDepth; z1++) {
							for (int x1 = 0; x1 < TileHologramProjector.hologramWidth; x1++) {
								try {
									RenderBlocks.getInstance().renderBlockByRenderType(projector.getBlock(x1, y1, z1),
											x1, y1, z1);
								} catch (Exception e) {
								}
								if (fVertexCount.getInt(t) == previousVertexCount
										&& !projector.isAirBlock(x1, y1, z1)) {
									RenderBlocks.getInstance().renderStandardBlock(projector.getBlock(x1, y1, z1), x1,
											y1, z1);
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

				GL11.glTranslated(x + 0.5, y, z + 0.5);
				GL11.glScalef(0.125f, 0.125f, 0.125f);
				GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
				GL11.glRotatef((float) projector.getRotation(), 0.f, 1.f, 0.f);

				GL11.glNewList(projector.getDisplayListID(), GL11.GL_COMPILE_AND_EXECUTE);

				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
				RenderHelper.disableStandardItemLighting();

				t.draw();

				RenderHelper.enableStandardItemLighting();

				GL11.glEndList();

				GL11.glPopMatrix();

				t.setTranslation(0.0, 0.0, 0.0);

				((TileHologramProjector) projector).setGraphicsUpdate(false);
			} else if (projector.getDisplayListID() != -1) {

				GL11.glPushMatrix();

				GL11.glTranslated(x + 0.5, y, z + 0.5);
				GL11.glScalef(0.125f, 0.125f, 0.125f);
				GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
				GL11.glRotatef((float) projector.getRotation(), 0.f, 1.f, 0.f);

				GL11.glCallList(projector.getDisplayListID());

				GL11.glPopMatrix();
			}
		}
	}

}
