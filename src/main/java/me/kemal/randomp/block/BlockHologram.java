package me.kemal.randomp.block;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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

			for (int hy = 0; hy < TileHologramProjector.hologramHeight; hy++) {
				for (int hz = 0; hz < TileHologramProjector.hologramDepth; hz++) {
					for (int hx = 0; hx < TileHologramProjector.hologramWidth; hx++) {
						if (!te.isAirBlock(hx, hy, hz)) {
							double cX = (double) hx / (double) TileHologramProjector.hologramWidth;
							double cY = (double) hy / (double) TileHologramProjector.hologramHeight;
							double cZ = (double) hz / (double) TileHologramProjector.hologramDepth;

							cuboids.add(new IndexedCuboid6(
									(hz * TileHologramProjector.hologramWidth)
											+ (hy * (TileHologramProjector.hologramWidth
													* TileHologramProjector.hologramDepth))
											+ hx,
									new Cuboid6(new Vector3((double) x + cX, (double) y + cY, (double) z + cZ),
											new Vector3((double) x + cX + 0.125, (double) y + cY + 0.125,
													(double) z + cZ + 0.125))));
						}
					}
				}
			}
		}
		return RayTracer.instance().rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids,
				new BlockCoord(x, y, z), this);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		return new ItemStack(RandomPeripherals.blockHologramProjector);
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}
}
