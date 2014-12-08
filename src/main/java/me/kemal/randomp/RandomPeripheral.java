package me.kemal.randomp;

import java.io.File;

import cofh.api.item.IToolHammer;
import cofh.api.modhelpers.ThermalExpansionHelper;
import cofh.lib.util.ItemWrapper;
import cofh.lib.util.helpers.FluidHelper;
import me.kemal.randomp.block.BlockUniversalInterface;
import me.kemal.randomp.common.CommonProxy;
import me.kemal.randomp.computercraft.RandomPPeripheralProvider;
import me.kemal.randomp.computercraft.RandomPTurtleUpgrade;
import me.kemal.randomp.computercraft.TurtleUpgradeDispense;
import me.kemal.randomp.computercraft.TurtleUpgradeInventory;
import me.kemal.randomp.gui.RandomPGuiHandler;
import me.kemal.randomp.net.RandomPMSG;
import me.kemal.randomp.net.ServerPacketHandler;
import me.kemal.randomp.te.TileUniversalInterface;
import me.kemal.randomp.util.ConfigFile;
import me.kemal.randomp.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import dan200.computercraft.api.ComputerCraftAPI;

import org.apache.logging.log4j.Logger;

@Mod(modid = RandomPeripheral.modid)
public class RandomPeripheral {
	public static final String modid = "RandomPeripherals";

	@SidedProxy(clientSide = "me.kemal.randomp.client.ClientProxy", serverSide = "me.kemal.common.randomp.CommonProxy")
	public static CommonProxy proxy;

	public static final String modnetworkchannel = "RandomP";
	public static SimpleNetworkWrapper networkWrapper;

	@Instance
	public static RandomPeripheral instance;

	public static Block blockUniversalInterface;

	public static Logger logger;
	public static ConfigFile config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		logger.info("Random Peripheral starts to work");

		config = new ConfigFile(event.getSuggestedConfigurationFile());
		config.commentMap.put("inventoryTurtleUpgrade", "ID of the Turtle Advanced Inventory Upgrade");
		config.map.put("inventoryTurtleUpgrade", "153");
		config.map.put("dispenserTurtleUpgrade", "154");
		config.read();

		networkWrapper = new SimpleNetworkWrapper(modnetworkchannel);
		networkWrapper.registerMessage(ServerPacketHandler.class, RandomPMSG.class, 0, Side.SERVER);

		blockUniversalInterface = new BlockUniversalInterface(Material.iron);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerTileEntities();
		NetworkRegistry.INSTANCE.registerGuiHandler(this.instance, new RandomPGuiHandler());
		MinecraftForge.EVENT_BUS.register(proxy);
		MinecraftForge.EVENT_BUS.register(instance);
	
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleUpgradeInventory(RandomPTurtleUpgrade.IsIDValid(config.map.get("inventoryTurtleUpgrade"), 153)));
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleUpgradeDispense(RandomPTurtleUpgrade.IsIDValid(config.map.get("dispenserTurtleUpgrade"),154)));
		ComputerCraftAPI.registerPeripheralProvider(new RandomPPeripheralProvider());
		if(ComputerCraftAPI.createResourceMount(RandomPeripheral.class, "randomperipherals", "randomperipherals/lua") == null){
			logger.info("Could not mount ressource");
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		GameRegistry.addRecipe(new ItemStack(blockUniversalInterface), "axa", "zyz", "zxz", 'z', new ItemStack(Items.iron_ingot), 'x', new ItemStack(
				Items.diamond), 'y', new ItemStack(Items.redstone), 'a', new ItemStack(Items.ender_eye));
		logger.info("Random Peripheral is finish loading!");
	}

	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent event) {
		if (event.world.getTileEntity(event.x, event.y, event.z) instanceof TileUniversalInterface && event.action == event.action.RIGHT_CLICK_BLOCK) {
			if (event.entityPlayer.inventory.getCurrentItem() != null) {
				Item usedItem = event.entityPlayer.inventory.getCurrentItem().getItem();
				if (usedItem instanceof IToolHammer) {
					//logger.info("Rotate Block!");
					((TileUniversalInterface) event.world.getTileEntity(event.x, event.y, event.z)).rotateBlock();
				}
			}
		}
	}

}
