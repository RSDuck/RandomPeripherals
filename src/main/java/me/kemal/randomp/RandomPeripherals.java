package me.kemal.randomp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Timer;

import cofh.api.item.IToolHammer;
import cofh.api.modhelpers.ThaumcraftHelper;
import cofh.api.modhelpers.ThermalExpansionHelper;
import cofh.lib.util.ItemWrapper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.FluidHelper;
import me.kemal.randomp.block.BlockHologram;
import me.kemal.randomp.block.BlockHologramProjector;
import me.kemal.randomp.block.BlockUniversalInterface;
import me.kemal.randomp.common.CommonProxy;
import me.kemal.randomp.common.command.CommandItemName;
import me.kemal.randomp.computercraft.RandomPPeripheralProvider;
import me.kemal.randomp.computercraft.RandomPTurtleUpgrade;
import me.kemal.randomp.computercraft.TurtleUpgradeDispense;
import me.kemal.randomp.computercraft.TurtleUpgradeInventory;
import me.kemal.randomp.gui.RandomPGuiHandler;
import me.kemal.randomp.item.ItemCreativeTabDummy;
import me.kemal.randomp.net.RandomPMSG;
import me.kemal.randomp.net.ServerPacketHandler;
import me.kemal.randomp.te.TileHologramProjector;
import me.kemal.randomp.te.TileRandomPMachine;
import me.kemal.randomp.te.TileUniversalInterface;
import me.kemal.randomp.util.CCUtils;
import me.kemal.randomp.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.Action;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.toposort.ModSorter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleUpgrade;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParser;

@Mod(modid = RandomPeripherals.modid)
public class RandomPeripherals {
	public static final String modid = "RandomPeripherals";

	@SidedProxy(clientSide = "me.kemal.randomp.client.ClientProxy", serverSide = "me.kemal.randomp.common.CommonProxy")
	public static CommonProxy proxy;

	public static final String modnetworkchannel = "RandomP";
	public static SimpleNetworkWrapper networkWrapper;

	@Instance
	public static RandomPeripherals instance;

	@SideOnly(Side.CLIENT)
	public static CreativeTabs tabRandomP = new CreativeTabs("tabRandomP") {

		@Override
		public Item getTabIconItem() {
			return new ItemStack(blockUniversalInterface).getItem();
		}
	};

	Configuration config;

	public static Block blockUniversalInterface;
	public static Block blockHologramProjector;
	public static Block blockHologram;

	public static Item itemCreativeTabDummy;

	public static Logger logger;

	public static int inventoryTurtleUpgradeID;
	public static int dispenserTurtleUpgradeID;

	public static boolean forceVanillaRecipes;

	public static String[] tileEntitiesWithAutoRead;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		logger.info("Random Peripheral starts to work...");

		if (config == null) {
			config = new Configuration(event.getSuggestedConfigurationFile());
			loadConfig();
		}

		networkWrapper = new SimpleNetworkWrapper(modnetworkchannel);
		networkWrapper.registerMessage(ServerPacketHandler.class, RandomPMSG.class, 0, Side.SERVER);

		proxy.registerRenderer();

		blockUniversalInterface = new BlockUniversalInterface(Material.iron);
		blockHologramProjector = new BlockHologramProjector();
		blockHologram = new BlockHologram();

		itemCreativeTabDummy = new ItemCreativeTabDummy();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerTileEntities();

		NetworkRegistry.INSTANCE.registerGuiHandler(this.instance, new RandomPGuiHandler());
		MinecraftForge.EVENT_BUS.register(proxy);
		MinecraftForge.EVENT_BUS.register(instance);

		ComputerCraftAPI.registerTurtleUpgrade(new TurtleUpgradeInventory(inventoryTurtleUpgradeID));
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleUpgradeDispense(dispenserTurtleUpgradeID));
		ComputerCraftAPI.registerPeripheralProvider(new RandomPPeripheralProvider());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (Loader.isModLoaded("ThermalFoundation") && Loader.isModLoaded("ThermalExpansion")) {
			try {
				Item teMaterial = GameRegistry.findItem("ThermalExpansion", "material");
				ItemStack invar = OreDictionary.getOres("ingotInvar").get(0);
				ItemStack signalumGear = OreDictionary.getOres("gearSignalum").get(0);
				ItemStack lumiumIngot = OreDictionary.getOres("ingotLumium").get(0);
				ItemStack silverIngot = OreDictionary.getOres("ingotSilver").get(0);
				ItemStack leadGear = OreDictionary.getOres("gearLead").get(0);

				GameRegistry.addRecipe(new ItemStack(blockUniversalInterface), "xyx", "yzy", "xax", 'x', invar, 'y',
						new ItemStack(teMaterial, 1, 1), 'a', new ItemStack(teMaterial), 'z', signalumGear);

				GameRegistry.addRecipe(new ItemStack(blockHologramProjector), " x ", "yiy", "zkz", 'x',
						new ItemStack(Blocks.glass), 'y', silverIngot, 'i', lumiumIngot, 'z',
						new ItemStack(teMaterial, 1, 2), 'k', new ItemStack(Items.comparator));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			GameRegistry.addRecipe(new ItemStack(blockUniversalInterface), "axa", "zyz", "zxz", 'z',
					new ItemStack(Items.iron_ingot), 'x', new ItemStack(Items.diamond), 'y',
					new ItemStack(Items.redstone), 'a', new ItemStack(Items.ender_eye));

			GameRegistry.addRecipe(new ItemStack(blockHologramProjector), " z ", "xyx", "ral", 'z',
					new ItemStack(Blocks.glass), 'x', new ItemStack(Items.iron_ingot), 'y',
					new ItemStack(Blocks.glowstone), 'r', new ItemStack(Items.redstone), 'a',
					new ItemStack(Items.comparator), 'l', new ItemStack(Blocks.redstone_torch));
		}

		logger.info("Random Peripheral has finished loading!");
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandItemName());
	}

	public final int[][] faceRotThing = { { 0, 1, 2, 3, 4, 5 }, { 0, 1, 5, 4, 2, 3 }, { 0, 1, 3, 2, 5, 4 },
			{ 0, 1, 4, 5, 3, 2 } };

	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent event) {
		if ((event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK
				|| event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
				&& event.world.getBlock(event.x, event.y, event.z) instanceof BlockHologram && !event.world.isRemote) {

			TileEntity te = event.world.getTileEntity(event.x, event.y - 1, event.z);
			if (te instanceof TileHologramProjector) {
				TileHologramProjector projector = (TileHologramProjector) te;

				MovingObjectPosition mov = ((BlockHologram) blockHologram).getRaytracer().retraceBlock(event.world,
						event.entityPlayer, event.x, event.y, event.z);

				if (mov == null)
					return;

				int subHitX = (mov.subHit & 0xff);
				int subHitY = ((mov.subHit >> 8) & 0xff);
				int subHitZ = ((mov.subHit >> 16) & 0xff);

				projector
						.onBlockClick(subHitX, subHitY, subHitZ,
								faceRotThing[(projector.getRotation() == 0) ? 0
										: projector.getRotation() / 90][event.face],
								(event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) ? 0 : 1,
								event.entityPlayer.getHeldItem());
			}
		}
	}

	@SubscribeEvent
	public void blockBreak(BreakEvent event) {
		event.setCanceled(event.block instanceof BlockHologram);
	}

	private void loadConfig() {
		inventoryTurtleUpgradeID = config.getInt("inventoryTurtleUpgrade", config.CATEGORY_GENERAL, 153, 63, 255,
				"The ID of the Inventory Turtle Upgrade", "me.kemal.randomperipheral.idOfUpgradeInventory");
		dispenserTurtleUpgradeID = config.getInt("dispenserTurtleUpgrade", config.CATEGORY_GENERAL, 154, 63, 255,
				"The ID of the Dispenser Turtle Upgrade", "me.kemal.randomperipheral.idOfUpgradeDispenser");
		tileEntitiesWithAutoRead = config.getStringList("autoWrappedPeripherals", config.CATEGORY_GENERAL,
				new String[] {},
				"If you add an block name to this list, it can be used as peripheral and you can read its NBT Data");
		forceVanillaRecipes = config.getBoolean("forceVanillaRecipes", config.CATEGORY_GENERAL, false,
				"If enabled no items of external mods will be used in crafting recipes");
		if (config.hasChanged()) {
			config.save();
		}
	}

}
