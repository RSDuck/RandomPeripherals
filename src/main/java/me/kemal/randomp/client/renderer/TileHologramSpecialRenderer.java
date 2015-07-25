package me.kemal.randomp.client.renderer;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.client.ClientProxy;
import me.kemal.randomp.te.TileHologramProjector;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldAccess;

@SideOnly(Side.CLIENT)
public class TileHologramSpecialRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
		TileEntity projector = tile.getWorldObj().getTileEntity(tile.xCoord, tile.yCoord - 1, tile.zCoord);
		if (projector instanceof TileHologramProjector) {
			Tessellator t = Tessellator.instance;

			t.startDrawingQuads();

			IBlockAccess backupAccess = RenderBlocks.getInstance().blockAccess;

			RenderBlocks.getInstance().blockAccess = (IBlockAccess) projector;

			Field vertexCount;
			try {
				vertexCount = Tessellator.class.getDeclaredField("vertexCount");

				vertexCount.setAccessible(true);

				t.setBrightness(((TileHologramProjector) projector).getLightBrightnessForSkyBlocks(0, 0, 0, 0));
				t.setColorOpaque(255, 255, 255);
				
				int previousVertexCount = vertexCount.getInt(t);
				
				for (int y1 = 0; y1 < TileHologramProjector.hologramHeight; y1++) {
					for (int z1 = 0; z1 < TileHologramProjector.hologramDepth; z1++) {
						for (int x1 = 0; x1 < TileHologramProjector.hologramWidth; x1++) {
							RenderBlocks.getInstance().renderBlockByRenderType(
									((TileHologramProjector) projector).getBlock(x1, y1, z1), x1, y1, z1);
							if (vertexCount.getInt(t) == previousVertexCount
									&& !((TileHologramProjector) projector).isAirBlock(x1, y1, z1)) {
								RenderBlocks.getInstance().renderStandardBlock(
										((TileHologramProjector) projector).getBlock(x1, y1, z1), x1, y1, z1);
								RandomPeripherals.logger.info("Fallback renderer!");
							}
						}
					}
				}
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}

			RenderBlocks.getInstance().blockAccess = backupAccess;

			GL11.glPushMatrix();

			bindTexture(RandomPeripherals.proxy.blockResLoc);

			GL11.glColor3f(1.f, 1.f, 1.f);

			GL11.glTranslated(x, y, z);
			GL11.glScalef(0.125f, 0.125f, 0.125f);
			try {
				Field hasColors = Tessellator.class.getDeclaredField("hasColor");
				hasColors.setAccessible(true);
				hasColors.set(t, false);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

			t.draw();

			GL11.glPopMatrix();
		}
	}
}
