package net.fexcraft.mod.doc.data;

import java.util.List;

import javax.annotation.Nullable;

import net.fexcraft.lib.mc.utils.Formatter;
import net.fexcraft.mod.doc.DocMod;
import net.fexcraft.mod.doc.DocRegistry;
import net.fexcraft.mod.doc.cap.DocItemCapability;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DocumentItem extends Item {

	public static String NBTKEY = "documents:type";
	public static DocumentItem INSTANCE;

	public DocumentItem(){
		this.setRegistryName(DocMod.MODID, "document");
		this.hasSubtypes = true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag){
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTKEY)){
			DocItemCapability cap = stack.getCapability(DocItemCapability.CAPABILITY, null);
			if(cap != null && cap.getDocument() != null){
				for(String str : cap.getDocument().description){
					tooltip.add(Formatter.format(I18n.format(str)));
				}
			}
			else{
				tooltip.add(cap == null ? "no capability" : "no document data");
			}
		}
		else{
			tooltip.add(Formatter.format("&c&l&mNO TYPE FOUND"));
			tooltip.add(stack.getTagCompound() == null ? "no tags" : stack.getTagCompound().toString());
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items){
		if(tab == CreativeTabs.TRANSPORTATION){
			for(String str : DocRegistry.DOCS.keySet()){
				ItemStack stack = new ItemStack(this);
				NBTTagCompound com = new NBTTagCompound();
				com.setString("documents:type", str);
				stack.setTagCompound(com);
				stack.getCapability(DocItemCapability.CAPABILITY, null).reload(str);
				items.add(stack);
			}
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack){
		if(stack.hasTagCompound()){
			return "item.documents." + stack.getTagCompound().getString(NBTKEY);
		}
		return this.getTranslationKey();
	}

}
