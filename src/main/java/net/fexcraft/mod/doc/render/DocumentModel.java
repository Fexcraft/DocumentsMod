package net.fexcraft.mod.doc.render;

import org.lwjgl.opengl.GL11;

import net.fexcraft.lib.mc.render.FCLItemModel;
import net.fexcraft.lib.tmt.ModelRendererTurbo;
import net.fexcraft.mod.doc.cap.DocItemCapability;
import net.fexcraft.mod.doc.data.DocumentItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class DocumentModel implements FCLItemModel {
	
	public static final DocumentModel INSTANCE = new DocumentModel();
	private static ModelRendererTurbo model = new ModelRendererTurbo(null, 0, 0, 1, 1).addBox(0, 0, 0, 1, 1, 0.001f);
	
	@Override
	public void renderItem(TransformType type, ItemStack item, EntityLivingBase entity){
		if(item.getItem() instanceof DocumentItem == false){ return; }
		DocItemCapability cap = item.getCapability(DocItemCapability.CAPABILITY, null);
		if(cap == null || cap.getDocument() == null) return;
		//
		GL11.glPushMatrix();
		GL11.glTranslatef(-.5f, .5f, 0);
		GL11.glRotatef(180, 1, 0, 0);
		Minecraft.getMinecraft().renderEngine.bindTexture(cap.getDocument().itemicon);
		model.render(1f);
		GL11.glPopMatrix();
	}

}