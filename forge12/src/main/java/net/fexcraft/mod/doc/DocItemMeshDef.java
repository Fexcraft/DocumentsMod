package net.fexcraft.mod.doc;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY;

public class DocItemMeshDef implements ItemMeshDefinition {

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack){
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTKEY)){
			return new ModelResourceLocation(new ResourceLocation(DocMod.MODID, stack.getTagCompound().getString(NBTKEY)), "inventory");
		}
		return new ModelResourceLocation(DocumentItem.INSTANCE.getRegistryName(), "inventory");
	}

}
