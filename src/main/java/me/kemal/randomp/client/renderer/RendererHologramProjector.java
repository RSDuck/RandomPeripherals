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

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (modelId == id) {
			// renderer.renderStandardBlock(block, x, y, z);

			Tessellator t = Tessellator.instance;

			IIcon icon = Blocks.planks.getIcon(0, 0);
			renderer.setOverrideBlockTexture(icon);
			double blockSize = 0.25f;

			t.setColorRGBA(255, 255, 255, 128);

			t.addVertexWithUV((double) x, (double) y + blockSize, (double) z + blockSize, icon.getMinU(), icon.getMinV());
			t.addVertexWithUV((double) x, (double) y, (double) z + blockSize, icon.getMinU(), icon.getMaxV());
			t.addVertexWithUV((double) x + blockSize, (double) y, (double) z + blockSize, icon.getMaxU(), icon.getMaxV());
			t.addVertexWithUV((double) x + blockSize, (double) y + blockSize, (double) z + blockSize, icon.getMaxU(), icon.getMinV());

			t.addVertexWithUV((double) x + blockSize, (double) y + blockSize, (double) z, icon.getMinU(), icon.getMinV());
			t.addVertexWithUV((double) x + blockSize, (double) y, (double) z, icon.getMinU(), icon.getMaxV());
			t.addVertexWithUV((double) x, (double) y, (double) z, icon.getMaxU(), icon.getMaxV());
			t.addVertexWithUV((double) x, (double) y + blockSize, (double) z, icon.getMaxU(), icon.getMinV());

			t.addVertexWithUV((double) x, (double) y + blockSize, (double) z + blockSize, icon.getMaxU(), icon.getMinV());
			t.addVertexWithUV((double) x, (double) y + blockSize, (double) z, icon.getMinU(), icon.getMinV());
			t.addVertexWithUV((double) x, (double) y, (double) z, icon.getMinU(), icon.getMaxV());
			t.addVertexWithUV((double) x, (double) y, (double) z + blockSize, icon.getMaxU(), icon.getMaxV());

			t.addVertexWithUV((double) x, (double) y, (double) z, icon.getMaxU(), icon.getMinV());
			t.addVertexWithUV((double) x, (double) y + blockSize, (double) z, icon.getMaxU(), icon.getMaxV());
			t.addVertexWithUV((double) x + blockSize, (double) y + blockSize, (double) z, icon.getMinU(), icon.getMaxV());
			t.addVertexWithUV((double) x + blockSize, (double) y, (double) z, icon.getMinU(), icon.getMinV());

			t.addVertexWithUV((double) x, (double) y + blockSize, (double) z, icon.getMaxU(), icon.getMinV());
			t.addVertexWithUV((double) x, (double) y + blockSize, (double) z + blockSize, icon.getMaxU(), icon.getMaxV());
			t.addVertexWithUV((double) x + blockSize, (double) y + blockSize, (double) z + blockSize, icon.getMinU(), icon.getMaxV());
			t.addVertexWithUV((double) x + blockSize, (double) y + blockSize, (double) z, icon.getMinU(), icon.getMinV());

			t.addVertexWithUV((double) x, (double) y, (double) z, icon.getMinU(), icon.getMinV());
			t.addVertexWithUV((double) x + blockSize, (double) y, (double) z, icon.getMaxU(), icon.getMinV());
			t.addVertexWithUV((double) x + blockSize, (double) y, (double) z + blockSize, icon.getMaxU(), icon.getMaxV());
			t.addVertexWithUV((double) x, (double) y, (double) z + blockSize, icon.getMinU(), icon.getMaxV());
			//
			//
			//
			//

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
