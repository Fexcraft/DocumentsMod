package net.fexcraft.mod.doc;

import java.util.List;

import javax.annotation.Nullable;

import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.lib.common.utils.Formatter;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.mod.doc.data.DocItem;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.ui.DocUI;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.inv.UniStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_TYPE;

public class DocumentItem extends Item implements DocItem {

	public static DocumentItem INSTANCE;

	public DocumentItem(){
		setRegistryName(Documents.MODID, "document");
		setTranslationKey(Documents.MODID + ".document");
		hasSubtypes = true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag){
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTKEY_TYPE)){
			DocStackApp cap = UniStack.getApp(stack, DocStackApp.class);
			if(cap != null && cap.getDocument() != null){
				for(String str : cap.getDocument().description){
					tooltip.add(Formatter.format(I18n.format(str)));
				}
				tooltip.add(Formatter.format(I18n.format(cap.isIssued() ? "documents.item.issued" : "documents.item.blank")));
			}
			else{
				tooltip.add(cap == null ? "no capability" : "no document data");
			}
		}
		else{
			tooltip.add(Formatter.format("&c&l&mNO TYPE FOUND"));
			tooltip.add(stack.getTagCompound() == null ? "no tags" : stack.getTagCompound().toString());
		}
		//tooltip.add(stack.getTagCompound().toString());
	}

	@Override
	public String getTranslationKey(ItemStack stack){
		if(stack.hasTagCompound()){
			return "item." + stack.getTagCompound().getString(NBTKEY_TYPE);
		}
		return getTranslationKey();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack){
		if(stack.hasTagCompound()){
			Document doc = DocRegistry.getDocument(stack.getTagCompound().getString(NBTKEY_TYPE));
			if(doc != null) return doc.name;
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		if(hand == EnumHand.MAIN_HAND){
			ItemStack stack = player.getHeldItemMainhand();
			DocStackApp cap = UniStack.getApp(stack, DocStackApp.class);
			if(cap == null || cap.getDocument() == null){
				Print.chat(player, "item.missing.doc");
				return super.onItemRightClick(world, player, hand);
			}
			UniEntity.getEntity(player).openUI(cap.isIssued() ? DocUI.VIEWER : DocUI.EDITOR, V3I.NULL);
			return new ActionResult(EnumActionResult.SUCCESS, stack);
		}
        return super.onItemRightClick(world, player, hand);
    }

}
