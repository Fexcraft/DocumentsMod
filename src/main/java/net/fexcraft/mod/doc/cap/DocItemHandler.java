package net.fexcraft.mod.doc.cap;

import static net.fexcraft.mod.doc.cap.DocItemCapability.CAPABILITY;
import static net.fexcraft.mod.doc.data.DocumentItem.NBTKEY;

import java.util.HashMap;
import java.util.Map;

import net.fexcraft.mod.doc.DocRegistry;
import net.fexcraft.mod.doc.data.Document;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
			return instance.write();
		}

		@Override
		public void readNBT(Capability<DocItemCapability> capability, DocItemCapability instance, EnumFacing side, NBTBase nbt){
			instance.read((NBTTagCompound)nbt);
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
		private Document doc;
		private HashMap<String, String> values = new HashMap<>();
		private NBTTagCompound save;
		private boolean done;

		public void setup(ItemStack stack){
			if(!stack.hasTagCompound()) return;
			doc = DocRegistry.DOCS.get(stack.getTagCompound().getString(NBTKEY));
		}

		@Override
		public NBTBase write(){
			NBTTagCompound compound = new NBTTagCompound();
			if(doc == null) return compound;
			compound.setString("type", doc.id);
			NBTTagList list = new NBTTagList();
			values.entrySet().forEach(entry -> {
				NBTTagCompound com = new NBTTagCompound();
				com.setString("key", entry.getKey());
				com.setString("val", entry.getValue());
				list.appendTag(com);
			});
			compound.setTag("values", list);
			compound.setBoolean("issued", done);
			return compound;
		}

		@Override
		public void read(NBTTagCompound compound){
			if(compound == null) return;
			if(doc == null && compound.hasKey("type")){
				doc = DocRegistry.DOCS.get(compound.getString("type"));
			}
			if(doc == null) return;
			save = compound;
			if(compound.hasKey("values")){
				values.clear();
				NBTTagList list = (NBTTagList)compound.getTag("values");
				for(NBTBase base : list){
					NBTTagCompound com = (NBTTagCompound)base;
					values.put(com.getString("key"), com.getString("val"));
				}
			}
			done = compound.getBoolean("issued");
		}

		@Override
		public Document getDocument(){
			return doc;
		}

		@Override
		public Map<String, String> getValues(){
			return values;
		}

		@Override
		public void reload(String type){
			this.doc = DocRegistry.DOCS.get(type);
			this.read(save);
		}

		@Override
		public boolean isIssued(){
			return done;
		}

		@Override
		public boolean isBlank(){
			return !done;
		}
		
	}

}
