package net.fexcraft.mod.doc;

import net.fexcraft.lib.mc.api.registry.fCommand;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.lib.mc.utils.Static;
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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender){
        return sender instanceof EntityPlayer;
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0){
			Print.chat(sender, "&7============");
			Print.chat(sender, "/documents list");
			Print.chat(sender, "/documents get");
			Print.chat(sender, "/documents uuid");
			Print.chat(sender, "/documents reload-perms");
			Print.chat(sender, "&7============");
			return;
		}
		EntityPlayer player = (EntityPlayer)sender;
		boolean sp = server.isSinglePlayer();
		switch(args[0]){
			case "list":{
				Print.chat(sender, "&7============");
				for(String str : DocRegistry.DOCS.keySet()){
					Print.chat(sender, str);
				}
				return;
			}
			case "uuid":{
				Print.chat(sender, "&7============");
				Print.chat(sender, player.getGameProfile().getId().toString());
				return;
			}
			case "get":{
				if(args.length < 2){
					Print.chat(sender, "missing argumment");
					return;
				}
				Document doc = DocRegistry.DOCS.get(args[1]);
				if(doc == null){
					Print.chat(sender, "not found");
					return;
				}
				if(!sp && !DocPerms.hasPerm(player, "command.get", args[1])){
					Print.chat(sender, "&cno permission");
					return;
				}
				ItemStack stack = new ItemStack(DocumentItem.INSTANCE);
				NBTTagCompound com = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
				com.setString("documents:type", args[1]);
				stack.setTagCompound(com);
				stack.getCapability(DocItemCapability.CAPABILITY, null).reload(args[1]);
				((EntityPlayer)sender).inventory.addItemStackToInventory(stack);
				return;
			}
			case "reload-perms":{
				if(!sp && !DocPerms.hasPerm(player, "command.reload-perms") && !Static.isOp(player)){
					Print.chat(sender, "&cno permission");
					return;
				}
				DocPerms.loadperms();
				Print.chat(sender, "&apermissions reloaded");
				return;
			}
			default: return;
		}
	}

}
