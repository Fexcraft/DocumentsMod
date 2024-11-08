package net.fexcraft.mod.doc;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DocItemMeshDef implements ItemMeshDefinition {

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack){
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(DocumentItem.NBTKEY)){
			return new ModelResourceLocation(new ResourceLocation(DocMod.MODID, stack.getTagCompound().getString(DocumentItem.NBTKEY)), "inventory");
		}
		return new ModelResourceLocation(DocumentItem.INSTANCE.getRegistryName(), "inventory");
	}

}
