package me.kemal.randomp;

import java.io.File;

import cofh.api.item.IToolHammer;
import cofh.api.modhelpers.ThaumcraftHelper;
import cofh.api.modhelpers.ThermalExpansionHelper;
import cofh.lib.util.ItemWrapper;
import cofh.lib.util.helpers.FluidHelper;
import me.kemal.randomp.block.BlockDebugPeripheral;
import me.kemal.randomp.block.BlockHologramProjector;
import me.kemal.randomp.block.BlockUniversalInterface;
import me.kemal.randomp.common.CommonProxy;
import me.kemal.randomp.computercraft.RandomPPeripheralProvider;
import me.kemal.randomp.computercraft.RandomPTurtleUpgrade;
import me.kemal.randomp.computercraft.TurtleUpgradeDispense;
import me.kemal.randomp.computercraft.TurtleUpgradeInventory;
import me.kemal.randomp.gui.RandomPGuiHandler;
import me.kemal.randomp.net.RandomPMSG;
import me.kemal.randomp.net.ServerPacketHandler;
import me.kemal.randomp.te.TileRandomPMachine;
import me.kemal.randomp.te.TileUniversalInterface;
import me.kemal.randomp.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.Action;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.ComputerCraftAPI;

import org.apache.logging.log4j.Logger;

@Mod(modid = RandomPeripheral.modid)
public class RandomPeripheral {
	public static final String modid = "RandomPeripherals";

	@SidedProxy(clientSide = "me.kemal.randomp.client.ClientProxy", serverSide = "me.kemal.randomp.common.CommonProxy")
	public static CommonProxy proxy;

	public static final String modnetworkchannel = "RandomP";
	public static SimpleNetworkWrapper networkWrapper;

	@Instance
	public static RandomPeripheral instance;

	Configuration config;

	public static Block blockUniversalInterface;
	public static Block blockDebugBlock;
	public static Block blockHologramProjector;

	public static Logger logger;

	int inventoryTurtleUpgradeID;
	int dispenserTurtleUpgradeID;

	/*
	 * @SideOnly(Side.CLIENT) public CreativeTabs randompTab = new
	 * CreativeTabs("tabRandomPeripheral") {
	 * 
	 * @Override public Item getTabIconItem() { return new
	 * ItemStack(blockUniversalInterface).getItem(); } };
	 */

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		logger.info("Random Peripheral starts to work");
		
		if (config == null) {
			config = new Configuration(event.getSuggestedConfigurationFile());
			loadConfig();
		}
		
		networkWrapper = new SimpleNetworkWrapper(modnetworkchannel);
		networkWrapper.registerMessage(ServerPacketHandler.class,
				RandomPMSG.class, 0, Side.SERVER);
		
		proxy.registerRenderer();

		blockUniversalInterface = new BlockUniversalInterface(Material.iron);
		blockDebugBlock = new BlockDebugPeripheral(Material.piston);
		//blockHologramProjector = new BlockHologramProjector();

		// randompTab;
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerTileEntities();

		NetworkRegistry.INSTANCE.registerGuiHandler(this.instance,
				new RandomPGuiHandler());
		MinecraftForge.EVENT_BUS.register(proxy);
		MinecraftForge.EVENT_BUS.register(instance);

		// 153, 154
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleUpgradeInventory(
				inventoryTurtleUpgradeID));
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleUpgradeDispense(
				dispenserTurtleUpgradeID));
		ComputerCraftAPI
				.registerPeripheralProvider(new RandomPPeripheralProvider());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		GameRegistry.addRecipe(new ItemStack(blockUniversalInterface), "axa",
				"zyz", "zxz", 'z', new ItemStack(Items.iron_ingot), 'x',
				new ItemStack(Items.diamond), 'y',
				new ItemStack(Items.redstone), 'a', new ItemStack(
						Items.ender_eye));
		logger.info("Random Peripheral is finish loading!");
	}

	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent event) {
		if (event.world.getTileEntity(event.x, event.y, event.z) instanceof TileRandomPMachine
				&& event.action == event.action.RIGHT_CLICK_BLOCK) {
			if (event.entityPlayer.inventory.getCurrentItem() != null) {
				Item usedItem = event.entityPlayer.inventory.getCurrentItem()
						.getItem();
				if (usedItem instanceof IToolHammer) {
					// logger.info("Rotate Block!");
					((TileUniversalInterface) event.world.getTileEntity(
							event.x, event.y, event.z)).rotateBlock();
				}
			}
		}
	}

	private void loadConfig() {
		inventoryTurtleUpgradeID = config.getInt("dispenserTurtleUpgrade",
				config.CATEGORY_GENERAL, 154, 63, 255,
				"The ID of the Dispenser Turtle Upgrade",
				"me.kemal.randomperipheral.idOfUpgradeDispenser");
		inventoryTurtleUpgradeID = config.getInt("inventoryTurtleUpgrade",
				config.CATEGORY_GENERAL, 153, 63, 255,
				"The ID of the Inventory Turtle Upgrade",
				"me.kemal.randomperipheral.idOfUpgradeInventory");
		
		if (config.hasChanged()) {
			config.save();
		}
	}

}
