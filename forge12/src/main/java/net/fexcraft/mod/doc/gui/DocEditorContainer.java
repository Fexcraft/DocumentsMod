package net.fexcraft.mod.doc.gui;

import java.time.LocalDate;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import net.fexcraft.lib.common.utils.Formatter;
import net.fexcraft.lib.mc.gui.GenericContainer;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.lib.mc.utils.Static;
import net.fexcraft.mod.doc.DocMod;
import net.fexcraft.mod.doc.DocPerms;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.data.FieldData;
import net.fexcraft.mod.doc.data.FieldType;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DocEditorContainer extends GenericContainer {
	
	protected DocStackApp app;
	protected StackWrapper stack;
	protected Document doc;
	@SideOnly(Side.CLIENT)
	protected DocEditor gui;

	public DocEditorContainer(EntityPlayer player){
		super(player);
		stack = StackWrapper.wrap(player.getHeldItemMainhand());
		if(!stack.hasTag() || !stack.getTag().has("documents:type")){
			Print.chat(player, "item.missing.type/item.invalid");
			player.closeScreen();
		}
		app = stack.appended.get(DocStackApp.class);
		if(app == null){
			Print.chat(player, "item.missing.cap");
			player.closeScreen();
		}
		if(app.getDocument() == null){
			Print.chat(player, "item.missing.doc");
			if(Static.dev()) Print.chat(player, stack.getTag());
			player.closeScreen();
		}
		doc = app.getDocument();
	}

	@Override
	protected void packet(Side side, NBTTagCompound packet, EntityPlayer player){
		if(packet.hasKey("open_page")){
			if(side.isClient()) return;
			player.openGui(DocMod.getInstance(), 1, player.world, packet.getInteger("open_page"), 0, 0);
			return;
		}
		if(packet.hasKey("issue") && packet.getBoolean("issue")){
			if(side.isServer() && !DocPerms.hasPerm(UniEntity.getEntity(player), "document.issue", app.getDocument().id.colon())){
				Print.chat(player, "&cno permission");
				return;
			}
			app.issueBy(UniEntity.getEntity(player), player.world.isRemote);
			if(side.isServer()){
				packet.setString("player_name", app.getValue("player_name"));
				send(Side.CLIENT, packet);
			}
			else{
				app.setValue("player_name", packet.getString("player_name"));
				player.closeScreen();
				Print.chat(player, Formatter.format(net.minecraft.client.resources.I18n.format("documents.editor.signed")));
			}
			return;
		}
		if(!packet.hasKey("field")) return;
		if(side.isServer() && !DocPerms.hasPerm(UniEntity.getEntity(player), "document.edit", app.getDocument().id.colon())){
			Print.chat(player, "&cno permission");
			return;
		}
		String field = packet.getString("field");
		FieldData data = doc.fields.get(field);
		String value = packet.getString("value");
		if(!data.type.editable) return;
		if(side.isServer()){
			if(data.type.number()){
				try{
					if(data.type == FieldType.INTEGER){
						if(value.contains(".")) value = value.substring(0, value.indexOf("."));
						Integer.parseInt(value);
					}
					else{
						Float.parseFloat(value);
					}
				}
				catch(Exception e){
					e.printStackTrace();
					Print.chat(player, "Error: " + e.getMessage());
					return;
				}
			}
			else if(data.type == FieldType.DATE){
				try{
					value = (LocalDate.parse(value).toEpochDay() * 86400000) + "";
				}
				catch(Exception e){
					e.printStackTrace();
					Print.chat(player, "Error: " + e.getMessage());
					return;
				}
			}
			else if(data.type == FieldType.UUID){
				try{
					GameProfile gp = Static.getServer().getPlayerProfileCache().getGameProfileForUsername(value);
					if(gp != null && gp.getId() != null && gp.getName() != null){
						value = gp.getId().toString();
					}
					else UUID.fromString(value);
				}
				catch(Exception e){
					e.printStackTrace();
					Print.chat(player, "Error: " + e.getMessage());
					return;
				}
			}
			app.setValue(field, value);
			packet.setString("value", value);
			send(Side.CLIENT, packet);
		}
		else{
			app.setValue(field, value);
			gui.statustext = null;
		}
	}

}
