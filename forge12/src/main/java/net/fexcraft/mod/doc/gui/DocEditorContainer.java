package net.fexcraft.mod.doc.gui;

import net.fexcraft.lib.mc.gui.GenericContainer;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.lib.mc.utils.Static;
import net.fexcraft.mod.doc.Documents;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class DocEditorContainer extends GenericContainer {
	
	protected DocStackApp app;
	protected StackWrapper stack;
	protected Document doc;

	public DocEditorContainer(EntityPlayer player){
		super(player);
		stack = StackWrapper.wrap(player.getHeldItemMainhand());
		if(!stack.hasTag() || !stack.getTag().has("documents:type")){
			Print.chat(player, "item.missing.type/item.invalid");
			player.closeScreen();
		}
		app = stack.appended.get(DocStackApp.class);
		if(app == null){
			Print.chat(player, "item.missing.cap");
			player.closeScreen();
		}
		if(app.getDocument() == null){
			Print.chat(player, "item.missing.doc");
			if(Static.dev()) Print.chat(player, stack.getTag());
			player.closeScreen();
		}
		doc = app.getDocument();
	}

	@Override
	protected void packet(Side side, NBTTagCompound packet, EntityPlayer player){
		if(packet.hasKey("open_page")){
			if(side.isClient()) return;
			player.openGui(Documents.getInstance(), 1, player.world, packet.getInteger("open_page"), 0, 0);
		}
	}

}
