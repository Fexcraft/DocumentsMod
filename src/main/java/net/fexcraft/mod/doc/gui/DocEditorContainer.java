package net.fexcraft.mod.doc.gui;

import java.time.LocalDate;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.fexcraft.lib.mc.gui.GenericContainer;
import net.fexcraft.lib.mc.utils.Formatter;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.lib.mc.utils.Static;
import net.fexcraft.mod.doc.DocMod;
import net.fexcraft.mod.doc.cap.DocItemCapability;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.data.DocumentItem;
import net.fexcraft.mod.doc.data.FieldData;
import net.fexcraft.mod.doc.data.FieldType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DocEditorContainer extends GenericContainer {
	
	protected DocItemCapability cap;
	protected ItemStack stack;
	protected Document doc;
	@SideOnly(Side.CLIENT)
	protected DocEditor gui;

	public DocEditorContainer(EntityPlayer player){
		super(player);
		ItemStack stack = player.getHeldItemMainhand();
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("documents:type")){
			Print.chat(player, "item.missing.type/item.invalid");
			player.closeScreen();
		}
		cap = stack.getCapability(DocItemCapability.CAPABILITY, null);
		if(cap == null){
			Print.chat(player, "item.missing.cap");
			player.closeScreen();
		}
		if(cap.getDocument() == null && stack.hasTagCompound() && stack.getTagCompound().hasKey(DocumentItem.NBTKEY)){
			cap.reload(stack.getTagCompound().getString(DocumentItem.NBTKEY));
		}
		if(cap.getDocument() == null){
			Print.chat(player, "item.missing.doc");
			Print.chat(player, stack.getTagCompound());
			player.closeScreen();
		}
		doc = cap.getDocument();
	}

	@Override
	protected void packet(Side side, NBTTagCompound packet, EntityPlayer player){
		if(packet.hasKey("open_page")){
			if(side.isClient()) return;
			player.openGui(DocMod.getInstance(), 1, player.world, packet.getInteger("open_page"), 0, 0);
			return;
		}
		if(packet.hasKey("issue") && packet.getBoolean("issue")){
			cap.issueBy(player, player.world.isRemote);
			if(side.isServer()){
				packet.setString("player_name", cap.getValues().get("player_name"));
				Print.debug(cap.getValues().keySet());
				send(Side.CLIENT, packet);
			}
			else{
				cap.getValues().put("player_name", packet.getString("player_name"));
				Print.debug(cap.getValues().keySet());
				player.closeScreen();
				Print.chat(player, Formatter.format(net.minecraft.client.resources.I18n.format("documents.editor.signed")));
			}
			return;
		}
		if(!packet.hasKey("field")) return;
		String field = packet.getString("field");
		FieldData data = doc.fields.get(field);
		String value = packet.getString("value");
		if(!data.type.editable) return;
		if(side.isServer()){
			if(data.type.number()){
				try{
					if(data.type == FieldType.INTEGER){
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
				}
			}
			cap.getValues().put(field, value);
			packet.setString("value", value);
			send(Side.CLIENT, packet);
		}
		else{
			cap.getValues().put(field, value);
			gui.statustext = null;
		}
	}

}
