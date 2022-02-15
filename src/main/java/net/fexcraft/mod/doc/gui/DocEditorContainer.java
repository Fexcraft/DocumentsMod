package net.fexcraft.mod.doc.gui;

import net.fexcraft.lib.mc.gui.GenericContainer;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.mod.doc.cap.DocItemCapability;
import net.fexcraft.mod.doc.data.Document;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class DocEditorContainer extends GenericContainer {
	
	protected DocItemCapability cap;
	protected ItemStack stack;
	protected Document doc;

	public DocEditorContainer(EntityPlayer player){
		super(player);
		ItemStack stack = player.getHeldItemMainhand();
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("documents:type")){
			Print.chat(player, "item.missing.type/item.invalid");
			player.closeScreen();
		}
		cap = stack.getCapability(DocItemCapability.CAPABILITY, null);
		if(cap == null){
			Print.chat(player, "item.missing.cap");
			player.closeScreen();
		}
		if(cap.getDocument() == null){
			Print.chat(player, "item.missing.doc");
			player.closeScreen();
			
		}
	}

	@Override
	protected void packet(Side side, NBTTagCompound packet, EntityPlayer player){
		//
	}

}
