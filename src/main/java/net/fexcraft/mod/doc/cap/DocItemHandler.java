package net.fexcraft.mod.doc.cap;

import static net.fexcraft.mod.doc.cap.DocItemCapability.CAPABILITY;
import static net.fexcraft.mod.doc.data.DocumentItem.NBTKEY;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.mc.utils.Static;
import net.fexcraft.mod.doc.DocRegistry;
import net.fexcraft.mod.doc.data.Document;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class DocItemHandler implements ICapabilitySerializable<NBTBase>{
	
	private DocItemCapability instance;
	
	public DocItemHandler(ItemStack stack){
		((Implementation)(instance = CAPABILITY.getDefaultInstance())).setup(stack);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		return capability != null && capability == CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		return capability != null && capability == CAPABILITY ? CAPABILITY.<T>cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT(){
		return CAPABILITY.getStorage().writeNBT(CAPABILITY, instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt){
		CAPABILITY.getStorage().readNBT(CAPABILITY, instance, null, nbt);
	}
	
	public static class Storage implements IStorage<DocItemCapability> {

		@Override
		public NBTBase writeNBT(Capability<DocItemCapability> capability, DocItemCapability instance, EnumFacing side){
			return new NBTTagCompound();
		}

		@Override
		public void readNBT(Capability<DocItemCapability> capability, DocItemCapability instance, EnumFacing side, NBTBase nbt){
			//
		}
		
	}
	
	public static class Callable implements java.util.concurrent.Callable<DocItemCapability> {

		@Override
		public DocItemCapability call() throws Exception {
			return new Implementation();
		}
		
	}
	
	public static class Implementation implements DocItemCapability {
		
		private ItemStack stack;
		private NBTTagCompound copy;
		private Document doc;

		public void setup(ItemStack stack){
			this.stack = stack;
			if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
			if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBTKEY)) return;
			doc = DocRegistry.DOCS.get(stack.getTagCompound().getString(NBTKEY));
		}

		@Override
		public void parse(NBTTagCompound compound){
			if(!compound.hasKey(NBTKEY)) return;
			doc = DocRegistry.DOCS.get(compound.getString(NBTKEY));
			copy = compound.copy();
		}

		@Override
		public Document getDocument(){
			if(doc == null){
				parse(stack.getTagCompound());
			}
			if(!stack.getTagCompound().equals(copy)){
				parse(stack.getTagCompound());
			}
			return doc;
		}

		@Override
		public boolean isIssued(){
			return stack.getTagCompound().hasKey("document:issued");
		}

		@Override
		public String getValue(String key){
			if(!stack.getTagCompound().hasKey("document:" + key)) return null;
			return stack.getTagCompound().getString("document:" + key);
		}

		@Override
		public void setValue(String key, String val){
			stack.getTagCompound().setString("document:" + key, val);
		}

		@Override
		public void issueBy(EntityPlayer player, boolean client){
			setValue("issuer", player.getGameProfile().getId().toString());
			setValue("issued", Time.getDate() + "");
			setValue("issuer_name", player.getGameProfile().getName());
			if(client) return;
			GameProfile gp = Static.getServer().getPlayerProfileCache().getProfileByUUID(UUID.fromString(getValue("uuid")));
			setValue("player_name", gp == null ? "DocError[GP]" : gp.getName());
		}
		
	}

}
