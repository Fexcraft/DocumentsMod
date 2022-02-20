package net.fexcraft.mod.doc;

import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.mc.api.registry.fCommand;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.mod.doc.cap.DocItemCapability;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.data.DocumentItem;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
			Print.chat(sender, "&7============");
			Print.chat(sender, "/documents list");
			Print.chat(sender, "/documents get");
			Print.chat(sender, "&7============");
			return;
		}
		switch(args[0]){
			case "ogui":{
				if(Static.dev()) ((EntityPlayer)sender).openGui(DocMod.getInstance(), 0, sender.getEntityWorld(), 0, 0, 0);
				return;
			}
			case "list":{
				Print.chat(sender, "&7============");
				for(String str : DocRegistry.DOCS.keySet()){
					Print.chat(sender, str);
				}
				return;
			}
			case "get":{
				Document doc = DocRegistry.DOCS.get(args[1]);
				if(doc == null){
					Print.chat(sender, "not found");
					return;
				}
				ItemStack stack = new ItemStack(DocumentItem.INSTANCE);
				NBTTagCompound com = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
				com.setString("documents:type", args[1]);
				stack.setTagCompound(com);
				stack.getCapability(DocItemCapability.CAPABILITY, null).reload(args[1]);
				Print.debug(stack.getCapability(DocItemCapability.CAPABILITY, null).getDocument().id);
				((EntityPlayer)sender).inventory.addItemStackToInventory(stack);
			}
			default: return;
		}
	}

}
