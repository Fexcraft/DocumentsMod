package net.fexcraft.mod.doc;

import java.util.List;

import javax.annotation.Nullable;

import net.fexcraft.lib.common.utils.Formatter;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.uni.item.StackWrapper;
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

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY;

public class DocumentItem extends Item {

	public static DocumentItem INSTANCE;

	public DocumentItem(){
		setRegistryName(DocMod.MODID, "document");
		setTranslationKey(DocMod.MODID + ".document");
		hasSubtypes = true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag){
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTKEY)){
			DocStackApp cap = StackWrapper.wrapAndGetApp(stack, DocStackApp.class);
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
			return "item.documents." + stack.getTagCompound().getString(NBTKEY);
		}
		return getTranslationKey();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack){
		/*if(stack.hasTagCompound() && !DocRegistry0.useRS()){
			Document doc = DocRegistry0.DOCS.get(stack.getTagCompound().getString(NBTKEY));
			if(doc != null) return doc.name;
		}*/
		return super.getItemStackDisplayName(stack);
	}

	@Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		if(hand == EnumHand.MAIN_HAND){
			ItemStack stack = player.getHeldItemMainhand();
			DocStackApp cap = StackWrapper.wrapAndGetApp(stack, DocStackApp.class);
			if(cap == null || cap.getDocument() == null){
				Print.chat(player, "item.missing.doc");
				return super.onItemRightClick(world, player, hand);
			}
			player.openGui(DocMod.getInstance(), cap.isIssued() ? 1 : 0, world, 0, 0, 0);
			return new ActionResult(EnumActionResult.SUCCESS, stack);
		}
        return super.onItemRightClick(world, player, hand);
    }

}
