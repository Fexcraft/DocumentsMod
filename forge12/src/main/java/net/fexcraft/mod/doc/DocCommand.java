package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.lib.mc.api.registry.fCommand;
import net.fexcraft.lib.mc.network.PacketHandler;
import net.fexcraft.lib.mc.network.packet.PacketNBTTagCompound;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.lib.mc.utils.Static;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.packet.DocPacketHandler;
import net.fexcraft.mod.doc.ui.DocUI;
import net.fexcraft.mod.fcl.UniFCL;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.MessageSender;
import net.fexcraft.mod.uni.world.WrapperHolder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_TYPE;
import static net.fexcraft.mod.fcl.UniFCL.LOG;

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
			Print.chat(sender, "&aGeneral:");
			Print.chat(sender, "/documents list");
			Print.chat(sender, "/documents uuid");
			Print.chat(sender, "/documents fill <doc-id>");
			Print.chat(sender, "&cOperator:");
			Print.chat(sender, "/documents get <doc-id>");
			Print.chat(sender, "/documents reload-perms");
			Print.chat(sender, "/documents reload-docs");
			Print.chat(sender, "&6Console/NPC");
			Print.chat(sender, "/documents start <player/uuid> <doc-id>");
			Print.chat(sender, "/documents set <player/uuid> <key> <value>");
			Print.chat(sender, "/documents issue <player/uuid>");
			return;
		}
		EntityW player = sender instanceof EntityPlayer ? UniEntity.getEntity(sender) : null;
		boolean sp = server.isSinglePlayer();
		switch(args[0]){
			case "list":{
				Print.chat(sender, "&7============");
				for(IDL str : DocRegistry.getDocuments().keySet()){
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
				Document doc = DocRegistry.getDocument(args[1]);
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
				com.setString(NBTKEY_TYPE, args[1]);
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
				DocRegistry.sendSync();
				Print.chat(sender, "&adocuments reloaded");
				return;
			}
			case "start":{
				DocCreator.start(player == null? LOG : player, args[1], args[2]);
				return;
			}
			case "set":{
				DocCreator.set(player == null? LOG : player, args[1], args[2], args[3]);
				return;
			}
			case "issue":{
				DocCreator.issue(player == null? LOG : player, args[1]);
				return;
			}
			case "fill":{
				int idx = DocRegistry.getDocumentIndex(args[1]);
				if(idx < 0){
					player.send("404: doc not found");
					return;
				}
				player.openUI(DocUI.EDITOR, idx, 1, 0);
				return;
			}
			default: return;
		}
	}

}
