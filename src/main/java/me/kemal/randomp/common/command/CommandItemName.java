package me.kemal.randomp.common.command;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.config.GuiConfigEntries.ChatColorEntry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandItemName implements ICommand {
	public CommandItemName() {
	}

	@Override
	public int compareTo(Object other) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "itemname";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "itemname";
	}

	@Override
	public List getCommandAliases() {
		ArrayList<String> aliases = new ArrayList<String>();
		aliases.add("iname");
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) {
		EntityPlayer player = (EntityPlayer) sender;
		if (player.getHeldItem() != null) {
			sender.addChatMessage(new ChatComponentTranslation("me.kemal.randomperipheral.chat.heldItemName"));
			sender.addChatMessage(new ChatComponentText("§5"
					+ GameRegistry.findUniqueIdentifierFor(player.getHeldItem().getItem()).toString()));
		} else {
			sender.addChatMessage(new ChatComponentTranslation("me.kemal.randomperipheral.chat.noItem"));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] astring) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}

}
