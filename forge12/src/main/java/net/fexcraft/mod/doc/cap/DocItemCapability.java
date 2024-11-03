package net.fexcraft.mod.doc.cap;

import net.fexcraft.mod.doc.data.Document;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface DocItemCapability {
	
	@CapabilityInject(DocItemCapability.class)
	public static final Capability<DocItemCapability> CAPABILITY = null;

	public void parse(NBTTagCompound nbt);

	public Document getDocument();

	public boolean isIssued();

	public String getValue(String key);

	public void setValue(String string, String string2);

	public void issueBy(EntityPlayer player, boolean client);

}
