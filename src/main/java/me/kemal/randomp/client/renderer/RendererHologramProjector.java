package me.kemal.randomp.client.renderer;

import java.security.spec.ECFieldF2m;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.te.TileHologramProjector;
import me.kemal.randomp.te.TileUniversalInterface_;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RendererHologramProjector implements ISimpleBlockRenderingHandler {
	public static int id;

	public RendererHologramProjector() {
		id = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		// TODO Auto-generated method stub

	}

	public static void renderMiniBlock(Block block, int meta, double blockSize, int worldX, int worldY, int worldZ,
			int xOff, int yOff, int zOff) {
		Tessellator t = Tessellator.instance;

		double minX = (double) worldX + ((double) xOff * blockSize) + (block.getBlockBoundsMinX() * blockSize);
		double minY = (double) worldY + ((double) yOff * blockSize) + (block.getBlockBoundsMinY() * blockSize);
		double minZ = (double) worldZ + ((double) zOff * blockSize) + (block.getBlockBoundsMinZ() * blockSize);
		double maxX = (block.getBlockBoundsMaxX() * blockSize) + (double) worldX + ((double) xOff * blockSize);
		double maxY = (block.getBlockBoundsMaxY() * blockSize) + (double) worldY + ((double) yOff * blockSize);
		double maxZ = (block.getBlockBoundsMaxZ() * blockSize) + (double) worldZ + ((double) zOff * blockSize);

		IIcon iconBottom = block.getIcon(0, meta);
		IIcon iconTop = block.getIcon(1, meta);
		IIcon iconNorth = block.getIcon(2, meta);
		IIcon iconSouth = block.getIcon(3, meta);
		IIcon iconWest = block.getIcon(4, meta);
		IIcon iconEast = block.getIcon(5, meta);

		float bMinX = (float) block.getBlockBoundsMinX();
		float bMinY = (float) block.getBlockBoundsMinY();
		float bMinZ = (float) block.getBlockBoundsMinZ();
		float bMaxX = (float) block.getBlockBoundsMaxX();
		float bMaxY = (float) block.getBlockBoundsMaxY();
		float bMaxZ = (float) block.getBlockBoundsMaxZ();

		if (iconSouth != null) {
			t.addVertexWithUV(minX, minY, maxZ, iconSouth.getMinU(), iconSouth.getMaxV());
			t.addVertexWithUV(maxX, minY, maxZ, iconSouth.getMaxU(), iconSouth.getMaxV());
			t.addVertexWithUV(maxX, maxY, maxZ, iconSouth.getMaxU(), iconSouth.getMinV());
			t.addVertexWithUV(minX, maxY, maxZ, iconSouth.getMinU(), iconSouth.getMinV());
		}

		if (iconNorth != null) {
			t.addVertexWithUV(minX, minY, minZ, iconNorth.getMaxU(), iconNorth.getMaxV());
			t.addVertexWithUV(minX, maxY, minZ, iconNorth.getMaxU(), iconNorth.getMinV());
			t.addVertexWithUV(maxX, maxY, minZ, iconNorth.getMinU(), iconNorth.getMinV());
			t.addVertexWithUV(maxX, minY, minZ, iconNorth.getMinU(), iconNorth.getMaxV());
		}

		if (iconTop != null) {
			t.addVertexWithUV(minX, maxY, minZ, iconTop.getMinU(), iconTop.getMinV());
			t.addVertexWithUV(minX, maxY, maxZ, iconTop.getMinU(), iconTop.getMaxV());
			t.addVertexWithUV(maxX, maxY, maxZ, iconTop.getMaxU(), iconTop.getMaxV());
			t.addVertexWithUV(maxX, maxY, minZ, iconTop.getMaxU(), iconTop.getMinV());
		}

		if (iconBottom != null) {
			t.addVertexWithUV(minX, minY, minZ, iconBottom.getMaxU(), iconBottom.getMinV());
			t.addVertexWithUV(maxX, minY, minZ, iconBottom.getMinU(), iconBottom.getMinV());
			t.addVertexWithUV(maxX, minY, maxZ, iconBottom.getMinU(), iconBottom.getMaxV());
			t.addVertexWithUV(minX, minY, maxZ, iconBottom.getMaxU(), iconBottom.getMaxV());
		}

		if (iconEast != null) {
			t.addVertexWithUV(maxX, minY, minZ, iconEast.getMaxU(), iconEast.getMaxV());
			t.addVertexWithUV(maxX, maxY, minZ, iconEast.getMaxU(), iconEast.getMinV());
			t.addVertexWithUV(maxX, maxY, maxZ, iconEast.getMinU(), iconEast.getMinV());
			t.addVertexWithUV(maxX, minY, maxZ, iconEast.getMinU(), iconEast.getMaxV());
		}

		if (iconWest != null) {
			t.addVertexWithUV(minX, minY, minZ, iconWest.getMinU(), iconWest.getMaxV());
			t.addVertexWithUV(minX, minY, maxZ, iconWest.getMaxU(), iconWest.getMaxV());
			t.addVertexWithUV(minX, maxY, maxZ, iconWest.getMaxU(), iconWest.getMinV());
			t.addVertexWithUV(minX, maxY, minZ, iconWest.getMinU(), iconWest.getMinV());
		}

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			RenderBlocks renderer) {
		if (modelId == id) {
			// renderer.renderStandardBlock(block, x, y, z);

			Tessellator t = Tessellator.instance;

			renderer.renderStandardBlock(block, x, y, z);

			t.addTranslation(0.f, 0.7f, 0.f);

			t.setColorRGBA(0, 191, 255, 255);

			TileHologramProjector tile = (TileHologramProjector) world.getTileEntity(x, y, z);
			try {

				for (int zI = 0; zI < 8; zI++) {
					for (int yI = 0; yI < 8; yI++) {
						for (int xI = 0; xI < 8; xI++) {

							renderMiniBlock(tile.getBlock(xI, yI, zI), tile.getBlockMetadata(xI, yI, zI), 1.f / 8, x,
									y, z, xI, yI, zI);
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			t.addTranslation(0.f, -0.7f, 0.f);

			return true;
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRenderId() {
		return id;
	}

}
