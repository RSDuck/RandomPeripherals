package me.kemal.randomp.client.renderer;

import me.kemal.randomp.RandomPeripheral;
import me.kemal.randomp.te.TileHologramProjector;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HologramRenderer implements ISimpleBlockRenderingHandler {
	public static int rendererID;

	public HologramRenderer() {
		rendererID = RenderingRegistry.getNextAvailableRenderId();
		RandomPeripheral.logger.info("Registred Renderer");
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		if (modelId == rendererID) {

		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		if (modelId == rendererID) {
			renderer.setRenderBounds(0.f, 0.f, 0.f, 1.f, 0.6f, 1.f);
			renderer.renderStandardBlock(block, x, y, z);

			TileHologramProjector projector = (TileHologramProjector)world.getTileEntity(x, y, z);
			renderer.setOverrideBlockTexture(projector.getBlockAt(0, 0, 0).getIcon(0, 0));
			
			renderer.setRenderBounds(0.f, 0.8f, 0.f, 0.125f, 0.8f + 0.125f,
					0.125f);
			renderer.renderStandardBlock(block, x, y, z);

			renderer.clearOverrideBlockTexture();
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return rendererID;
	}

}
