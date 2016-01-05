package me.kemal.randomp.computercraft.turtle;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import me.kemal.randomp.RandomPeripherals;
import me.kemal.randomp.util.CCType;
import me.kemal.randomp.util.TurtlePeripheral;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TurtleUpgradeClicky extends RandomPTurtleUpgrade {
	EntityPlayer fakePlayer;

	public TurtleUpgradeClicky(int upgradeID) {
		super("Clicky", upgradeID);

		peripheral.AddMethod("click", "Simulates the click of an player with the currently holding item",
				new CCType[] { new CCType(Boolean.class, "rightClick", "If it should be a right or left click") }, new CCType[] {}, this);
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		createFakePlayer(turtle.getWorld());
		return new TurtlePeripheral(turtle, peripheral);
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(Items.apple);
	}

	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
		/*ItemStack i = (turtle == null) ? null : turtle.getInventory().getStackInSlot(turtle.getSelectedSlot());
		return (i == null) ? Items.apple.getIconFromDamage(0) : i.getItem().getIconFromDamage(i.getItemDamage());*/
		return Blocks.stone.getIcon(0, 0);
	}

	private void createFakePlayer(World world) {
		if (fakePlayer == null) {
			fakePlayer = new EntityPlayer(world, new GameProfile(UUID.randomUUID(), RandomPeripherals.fakePlayerName)) {
				@Override
				public void addChatMessage(IChatComponent chat) {
				}

				@Override
				public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
					return false;
				}

				@Override
				public ChunkCoordinates getPlayerCoordinates() {
					return null;
				}
			};
		}
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, String method, Object[] arguments, ITurtleAccess turtle)
			throws LuaException {
		switch (method) {
			case "click": {
				if (turtle.consumeFuel(1)) {
					boolean right = (boolean) arguments[0];
					boolean sneaking = false;

					createFakePlayer(turtle.getWorld());

					IInventory inv = turtle.getInventory();
					ForgeDirection side = ForgeDirection.getOrientation(turtle.getDirection());

					fakePlayer.worldObj = turtle.getWorld();
					fakePlayer.setCurrentItemOrArmor(0, inv.getStackInSlot(turtle.getSelectedSlot()));
					int xo = turtle.getPosition().posX + side.offsetX;
					int yo = turtle.getPosition().posY + side.offsetY;
					int zo = turtle.getPosition().posZ + side.offsetZ;
					Block b = turtle.getWorld().getBlock(xo, yo, zo);

					int x = turtle.getPosition().posX;
					int y = turtle.getPosition().posY;
					int z = turtle.getPosition().posZ;

					World world = turtle.getWorld();
					int slot = turtle.getSelectedSlot();

					if (inv.getStackInSlot(slot) != null) {
						ItemStack stack = inv.getStackInSlot(slot);
						Item theItem = stack.getItem();
						//inv.setInventorySlotContents(slot, null);

						//fakePlayer.setCurrentItemOrArmor(0, stack);

						fakePlayer.worldObj = world;
						fakePlayer.setSneaking(sneaking);

						fakePlayer.posY++;
						int useOffset = -1;

						if (!theItem.onItemUseFirst(stack, fakePlayer, world, x + side.offsetX, y + side.offsetY, z + side.offsetZ, 1, 0.5f, 0.5f,
								0.5f)) {
							if (world.isAirBlock(xo, yo, zo) || (!sneaking || theItem.doesSneakBypassUse(world, x, y, z, fakePlayer))) {
								if (!theItem.onItemUse(stack, fakePlayer, world, x + side.offsetX, y + side.offsetY - 1, z + side.offsetZ, 1, 0.5f,
										0.5f, 0.5f)) {
									boolean doRC = true;
									if (world.isAirBlock(xo, yo, zo)) {
										doRC = false;
										List<Entity> l = world.getEntitiesWithinAABBExcludingEntity((Entity) null,
												AxisAlignedBB.getBoundingBox(xo, yo, zo, xo + 1, yo + 1, zo + 1));
										if (l.size() > 0) {
											Entity toUse = l.get(0);
											if (toUse instanceof EntityLiving) {
												if (!((EntityLiving) toUse).interactFirst(fakePlayer)) {
													doRC = !theItem.itemInteractionForEntity(stack, fakePlayer, (EntityLiving) toUse);
												}

											}
										} else {
											doRC = true;
										}
									}

									if (doRC && !theItem.onItemUse(stack, fakePlayer, world, x + side.offsetX, y + side.offsetY, z + side.offsetZ, 1,
											0.5f, 0.5f, 0.5f)) {
										float ax = 0;// 0F * side.offsetX;
										float ay = 0;// 0F * side.offsetY;
										float az = 0;// 0F * side.offsetZ;

										if (side.offsetX == 0)
											ax = 0.5F;
										if (side.offsetY == 0)
											ay = 0.5F;
										if (side.offsetZ == 0)
											az = 0.5F;

										fakePlayer.posX = xo + ax;
										fakePlayer.posY = yo + ay;
										fakePlayer.posZ = zo + az;
										fakePlayer.prevPosX = xo + ax;
										fakePlayer.prevPosY = yo + ay;
										fakePlayer.prevPosZ = zo + az;

										switch (side) {
											case WEST:
												fakePlayer.rotationPitch = 0;
												fakePlayer.rotationYaw = 90;
												break;
											case NORTH:
												fakePlayer.rotationPitch = 0;
												fakePlayer.rotationYaw = 180;
												// fakePlayer.posZ += 0.9F;
												// fakePlayer.prevPosZ += 0.9F;
												break;
											case EAST:
												fakePlayer.rotationPitch = 0;
												fakePlayer.rotationYaw = -90;
												break;
											case SOUTH:
												fakePlayer.rotationPitch = 0;
												fakePlayer.rotationYaw = 1;
												fakePlayer.posZ += 0.5F;
												fakePlayer.prevPosZ += 0.5F;
												break;
											case UP:
												fakePlayer.rotationPitch = 271;
												fakePlayer.rotationYaw = 0;
												break;
											case DOWN:
												fakePlayer.rotationPitch = 90;
												fakePlayer.rotationYaw = 0;
												break;
											default:
												;
										}

										fakePlayer.setCurrentItemOrArmor(0, theItem.onItemRightClick(stack, world, fakePlayer));
									}
								}
							} else {
								tryActivateBlock(turtle, side, fakePlayer, xo, yo, zo, b);
							}
						}
						inv.setInventorySlotContents(slot, fakePlayer.getHeldItem());
					} else {
						tryActivateBlock(turtle, side, fakePlayer, xo, yo, zo, b);

						inv.setInventorySlotContents(slot, fakePlayer.getHeldItem());
					}
				}
			}
		}
		return null;
	}

	public void tryActivateBlock(ITurtleAccess turtle, ForgeDirection side, EntityPlayer fakePlayer, int x, int y, int z, Block block) {
		if (Block.blockRegistry.containsId(Block.getIdFromBlock(block))) {
			if (!block.onBlockActivated(turtle.getWorld(), x, y, z, fakePlayer, side.getOpposite().ordinal(), 0.5f, 0.5f, 0.5f)) {
				// block.onBlockClicked(turtle.getWorld(), x, y, z, fakePlayer);
			}
		}

		if (turtle.getWorld().isAirBlock(x, y, z)) {
			List<Entity> l = turtle.getWorld().getEntitiesWithinAABBExcludingEntity((Entity) null,
					AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
			if (l.size() > 0) {
				Entity toUse = l.get(0);
				if (toUse instanceof EntityLiving) {
					((EntityLiving) toUse).interactFirst(fakePlayer);
				}
			}
		}
	}
}
