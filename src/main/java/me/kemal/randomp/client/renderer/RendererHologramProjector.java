package me.kemal.randomp.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.block.Block;
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

	public void renderMiniBlock(RenderBlocks renderer, Block block, int meta, double blockSize, int worldX, int worldY, int worldZ,
			int xOff, int yOff, int zOff) {
		Tessellator t = Tessellator.instance;

		double minX = (double) worldX + ((double) xOff * blockSize);
		double minY = (double) worldY + ((double) yOff * blockSize);
		double minZ = (double) worldZ + ((double) zOff * blockSize);
		double maxX = (block.getBlockBoundsMaxX() * blockSize) + (double) worldX + ((double) xOff * blockSize);
		double maxY = (block.getBlockBoundsMaxY() * blockSize) + (double) worldY + ((double) yOff * blockSize);
		double maxZ = (block.getBlockBoundsMaxZ() * blockSize) + (double) worldZ + ((double) zOff * blockSize);

		IIcon iconBottom = block.getIcon(0, meta);
		IIcon iconTop = block.getIcon(1, meta);
		IIcon iconNorth = block.getIcon(2, meta);
		IIcon iconSouth = block.getIcon(3, meta);
		IIcon iconWest = block.getIcon(4, meta);
		IIcon iconEast = block.getIcon(5, meta);

		t.addVertexWithUV(minX, minY, maxZ, iconSouth.getMinU(), iconSouth.getMinV());
		t.addVertexWithUV(maxX, minY, maxZ, iconSouth.getMaxU(), iconSouth.getMinV());
		t.addVertexWithUV(maxX, maxY, maxZ, iconSouth.getMaxU(), iconSouth.getMaxV());
		t.addVertexWithUV(minX, maxY, maxZ, iconSouth.getMinU(), iconSouth.getMaxV());

		t.addVertexWithUV(minX, minY, minZ, iconNorth.getMaxU(), iconNorth.getMinV());
		t.addVertexWithUV(minX, maxY, minZ, iconNorth.getMaxU(), iconNorth.getMaxV());
		t.addVertexWithUV(maxX, maxY, minZ, iconNorth.getMinU(), iconNorth.getMaxV());
		t.addVertexWithUV(maxX, minY, minZ, iconNorth.getMinU(), iconNorth.getMinV());
		//
		//
		//
		//
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (modelId == id) {
			// renderer.renderStandardBlock(block, x, y, z);

			Tessellator t = Tessellator.instance;

			IIcon icon = Blocks.planks.getIcon(0, 0);
			renderer.setOverrideBlockTexture(icon);

			t.setColorRGBA(255, 255, 255, 128);

			renderMiniBlock(renderer, Blocks.brick_block, 0, 0.5f, x, y, z, 0, 0, 0);

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
