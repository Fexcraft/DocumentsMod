package net.fexcraft.mod.doc;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_TYPE;

public class DocItemMeshDef implements ItemMeshDefinition {

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack){
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTKEY_TYPE)){
			return new ModelResourceLocation(new ResourceLocation(Documents.MODID, stack.getTagCompound().getString(NBTKEY_TYPE)), "inventory");
		}
		return new ModelResourceLocation(DocumentItem.INSTANCE.getRegistryName(), "inventory");
	}

}
