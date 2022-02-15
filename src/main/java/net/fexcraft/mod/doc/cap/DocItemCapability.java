package net.fexcraft.mod.doc.cap;

import java.util.Map;

import net.fexcraft.mod.doc.data.Document;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface DocItemCapability {
	
	@CapabilityInject(DocItemCapability.class)
	public static final Capability<DocItemCapability> CAPABILITY = null;

	public NBTBase write();

	public void read(NBTTagCompound nbt);

	public Document getDocument();
	
	public Map<String, String> getValues();

	public void reload(String type);

}
