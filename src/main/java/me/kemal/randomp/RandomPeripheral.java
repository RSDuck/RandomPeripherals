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
import cofh.lib.util.helpers.FluidHelper;
import me.kemal.randomp.block.BlockDebugPeripheral;
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
import me.kemal.randomp.te.TileRandomPMachine;
import me.kemal.randomp.te.TileUniversalInterface;
import me.kemal.randomp.util.CCUtils;
import me.kemal.randomp.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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

@Mod(modid = RandomPeripheral.modid)
public class RandomPeripheral {
	public static final String modid = "RandomPeripherals";

	@SidedProxy(clientSide = "me.kemal.randomp.client.ClientProxy", serverSide = "me.kemal.randomp.common.CommonProxy")
	public static CommonProxy proxy;

	public static final String modnetworkchannel = "RandomP";
	public static SimpleNetworkWrapper networkWrapper;

	@Instance
	public static RandomPeripheral instance;

	@SideOnly(Side.CLIENT)
	public static CreativeTabs tabRandomP = new CreativeTabs("tabRandomP") {

		@Override
		public Item getTabIconItem() {
			return new ItemStack(blockUniversalInterface).getItem();
		}
	};

	Configuration config;

	public static Block blockUniversalInterface;
	public static Block blockDebugBlock;
	public static Block blockHologramProjector;

	public static Item itemCreativeTabDummy;

	public static Logger logger;

	public static int inventoryTurtleUpgradeID;
	public static int dispenserTurtleUpgradeID;

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
		blockDebugBlock = new BlockDebugPeripheral(Material.piston);
		blockHologramProjector = new BlockHologramProjector();

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

				GameRegistry.addRecipe(new ItemStack(blockUniversalInterface), "xyx", "yzy", "xax", 'x', invar, 'y', new ItemStack(
						teMaterial, 1, 1), 'a', new ItemStack(teMaterial), 'z', signalumGear);

				GameRegistry
						.addRecipe(new ItemStack(blockHologramProjector), " x ", "yiy", "zkz", 'x', new ItemStack(Blocks.glass), 'y',
								silverIngot, 'i', lumiumIngot, 'z', new ItemStack(teMaterial, 1, 2), 'k', new ItemStack(
										Items.comparator));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			GameRegistry.addRecipe(new ItemStack(blockUniversalInterface), "axa", "zyz", "zxz", 'z', new ItemStack(Items.iron_ingot),
					'x', new ItemStack(Items.diamond), 'y', new ItemStack(Items.redstone), 'a', new ItemStack(Items.ender_eye));

			GameRegistry.addRecipe(new ItemStack(blockHologramProjector), " z ", "xyx", "ral", 'z', new ItemStack(Blocks.glass), 'x',
					new ItemStack(Items.iron_ingot), 'y', new ItemStack(Blocks.glowstone), 'r', new ItemStack(Items.redstone), 'a',
					new ItemStack(Items.comparator), 'l', new ItemStack(Blocks.redstone_torch));
		}

		logger.info("Random Peripheral has finished loading!");
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandItemName());
	}

	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent event) {
		if (event.world.getTileEntity(event.x, event.y, event.z) instanceof TileRandomPMachine
				&& event.action == event.action.RIGHT_CLICK_BLOCK) {
			if (event.entityPlayer.inventory.getCurrentItem() != null) {
				Item usedItem = event.entityPlayer.inventory.getCurrentItem().getItem();
				if (usedItem instanceof IToolHammer) {
					// logger.info("Rotate Block!");
					((TileUniversalInterface) event.world.getTileEntity(event.x, event.y, event.z)).rotateBlock();
				}
			}
		}
	}

	private void loadConfig() {
		inventoryTurtleUpgradeID = config.getInt("inventoryTurtleUpgrade", config.CATEGORY_GENERAL, 153, 63, 255,
				"The ID of the Inventory Turtle Upgrade", "me.kemal.randomperipheral.idOfUpgradeInventory");
		dispenserTurtleUpgradeID = config.getInt("dispenserTurtleUpgrade", config.CATEGORY_GENERAL, 154, 63, 255,
				"The ID of the Dispenser Turtle Upgrade", "me.kemal.randomperipheral.idOfUpgradeDispenser");
		tileEntitiesWithAutoRead = config.getStringList("autoWrappedPeripherals", config.CATEGORY_GENERAL, new String[] {},
				"If you add an block name to this list, it can be used as peripheral and you can read its NBT Data");
		if (config.hasChanged()) {
			config.save();
		}
	}

	public static void registerTurtleUpgrade(ITurtleUpgrade upgrade) {
		if (upgrade != null) {
			logger.info("Upgrade register " + upgrade.getUnlocalisedAdjective());
		}
	}

}
