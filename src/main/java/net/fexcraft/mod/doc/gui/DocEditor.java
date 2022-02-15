package net.fexcraft.mod.doc.gui;

import net.fexcraft.lib.mc.gui.GenericGui;
import net.fexcraft.lib.mc.utils.Print;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class DocEditor extends GenericGui<DocEditorContainer> {
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("documents:textures/gui/editor.png");

	public DocEditor(EntityPlayer player){
		super(TEXTURE, new DocEditorContainer(player), player);
		xSize = 256;
		ySize = 104;
		if(container.cap == null){
			Print.bar(player, "item.missing.cap");
			player.closeScreen();
			mc.currentScreen = null;
		}
		if(container.cap.getDocument() == null){
			Print.bar(player, "item.missing.doc");
			player.closeScreen();
			mc.currentScreen = null;
			
		}
	}

}
