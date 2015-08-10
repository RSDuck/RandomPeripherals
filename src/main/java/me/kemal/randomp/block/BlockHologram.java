package me.kemal.randomp.block;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.lwjgl.util.vector.Matrix3f;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.net.Packets;
import me.kemal.randomp.raytracer.BlockCoord;
import me.kemal.randomp.raytracer.Cuboid6;
import me.kemal.randomp.raytracer.IndexedCuboid6;
import me.kemal.randomp.raytracer.RayTracer;
import me.kemal.randomp.raytracer.Vector3;
import me.kemal.randomp.te.TileHologram;
import me.kemal.randomp.te.TileHologramProjector;
import me.kemal.randomp.te.TileUniversalInterface;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockHologram extends Block implements ITileEntityProvider {

	public final static String blockName = "hologram";

	private RayTracer raytracer = new RayTracer();

	public BlockHologram() {
		super(Material.glass);

		setBlockBounds(0.f, 0.f, 0.f, 1.f, 1.f, 1.f);
		setBlockUnbreakable();

		GameRegistry.registerBlock(this, blockName);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List boxes,
			Entity entity) {
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return 14;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return -1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileHologram();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX,
			float clickY, float clickZ) {
		return true;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		LinkedList<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();

		if (world.getTileEntity(x, y - 1, z) instanceof TileHologramProjector) {
			TileHologramProjector te = (TileHologramProjector) world.getTileEntity(x, y - 1, z);
			if (te.isTouchScreenFeatureAvailable()) {
				double sin = Math.sin(Math.toRadians(te.getRotation()));
				double cos = Math.cos(Math.toRadians(te.getRotation()));

				final double centerX = TileHologramProjector.hologramWidth / 2 - 0.5;
				final double centerZ = TileHologramProjector.hologramDepth / 2 - 0.5;

				for (int hy = 0; hy < TileHologramProjector.hologramHeight; hy++) {
					for (int hz = 0; hz < TileHologramProjector.hologramDepth; hz++) {
						for (int hx = 0; hx < TileHologramProjector.hologramWidth; hx++) {
							double m = (double) hx - centerX;
							double n = (double) hz - centerZ;

							int rotX = Math.round((float) ((m * cos + n * sin) + centerX));
							int rotZ = Math.round((float) ((n * cos - m * sin) + centerZ));
							int rotY = hy;

							if (!te.isAirBlock(hx, hy, hz)) {
								double cX = (double) rotX / (double) TileHologramProjector.hologramWidth;
								double cY = (double) rotY / (double) TileHologramProjector.hologramHeight;
								double cZ = (double) rotZ / (double) TileHologramProjector.hologramDepth;

								cuboids.add(new IndexedCuboid6((hx) | (hy << 8) | (hz << 16),
										new Cuboid6(new Vector3((double) x + cX, (double) y + cY, (double) z + cZ),
												new Vector3((double) x + cX + 0.125, (double) y + cY + 0.125,
														(double) z + cZ + 0.125))));
							}
						}
					}
				}
			}
		}
		return raytracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(x, y, z), this);

	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		return new ItemStack(RandomPeripherals.blockHologramProjector);
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	public RayTracer getRaytracer() {
		return raytracer;
	}
}
