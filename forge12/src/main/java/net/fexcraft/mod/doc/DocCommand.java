package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.lib.mc.api.registry.fCommand;
import net.fexcraft.lib.mc.network.PacketHandler;
import net.fexcraft.lib.mc.network.packet.PacketNBTTagCompound;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.lib.mc.utils.Static;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.WrapperHolder;
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
			Print.chat(sender, "/documents reload-docs");
			Print.chat(sender, "&7============");
			return;
		}
		EntityW player = UniEntity.getEntity(sender);
		boolean sp = server.isSinglePlayer();
		switch(args[0]){
			case "list":{
				Print.chat(sender, "&7============");
				for(IDL str : DocRegistry.DOCUMENTS.keySet()){
					Print.chat(sender, str.colon());
				}
				return;
			}
			case "uuid":{
				Print.chat(sender, "&7============");
				Print.chat(sender, player.getUUID().toString());
				return;
			}
			case "get":{
				if(args.length < 2){
					Print.chat(sender, "missing argumment");
					return;
				}
				Document doc = DocRegistry.DOCUMENTS.get(IDLManager.getIDL(args[1]));
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
				((EntityPlayer)sender).inventory.addItemStackToInventory(stack);
				return;
			}
			case "reload-perms":{
				if(!sp && !DocPerms.hasPerm(player, "command.reload-perms") && !Static.isOp((EntityPlayer)sender)){
					Print.chat(sender, "&cno permission");
					return;
				}
				DocPerms.loadperms();
				Print.chat(sender, "&apermissions reloaded");
				return;
			}
			case "reload-docs":{
				if(!sp && !DocPerms.hasPerm(player, "command.reload-docs") && !Static.isOp((EntityPlayer)sender)){
					Print.chat(sender, "&cno permission");
					return;
				}
				DocRegistry.reload();
				NBTTagCompound com = new NBTTagCompound();
				com.setString("target_listener", "docmod");
				com.setString("task", "sync");
				com.setString("config", JsonHandler.toString(DocRegistry.CONF_MAP, PrintOption.FLAT));
				server.getPlayerList().getPlayers().forEach(splayer -> {
					PacketHandler.getInstance().sendTo(new PacketNBTTagCompound(com), splayer);
				});
				Print.chat(sender, "&adocuments reloaded");
				return;
			}
			default: return;
		}
	}

}
