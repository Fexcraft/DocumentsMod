package net.fexcraft.mod.doc;

import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.mc.api.registry.fCommand;
import net.fexcraft.lib.mc.utils.Print;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

@fCommand
public class DocCommand extends CommandBase {
	
	@Override
	public String getName(){ return "documents"; }

	@Override
	public String getUsage(ICommandSender sender){ return "/documents"; }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0){
			Print.chat(sender, "//todo");
		}
		switch(args[0]){
			case "ogui":{
				if(Static.dev()) ((EntityPlayer)sender).openGui(DocMod.getInstance(), 0, sender.getEntityWorld(), 0, 0, 0);
				return;
			}
			default: return;
		}
	}

}
