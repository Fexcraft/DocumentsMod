package net.fexcraft.mod.doc;

import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.uni.inv.UniStack;
import org.lwjgl.opengl.GL11;

import net.fexcraft.lib.mc.render.FCLItemModel;
import net.fexcraft.lib.tmt.ModelRendererTurbo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class DocumentModel implements FCLItemModel {
	
	public static final DocumentModel INSTANCE = new DocumentModel();
	private static ModelRendererTurbo model = new ModelRendererTurbo(null, 0, 0, 1, 1).newBoxBuilder().setSize(1, 1, 0).removePolygons(true, true, true, true, false, true).setOffset(-0.5f, -0.5f, 0).build();
	
	@Override
	public void renderItem(TransformType type, ItemStack item, EntityLivingBase entity){
		if(item.getItem() instanceof DocumentItem == false){ return; }
		DocStackApp cap = UniStack.getApp(item, DocStackApp.class);
		if(cap == null || cap.getDocument() == null) return;
		//
		boolean rd3 = type == TransformType.THIRD_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_RIGHT_HAND;
		GL11.glPushMatrix();
		if(rd3) GL11.glTranslatef(0, 0, -.005f);
		GL11.glRotatef(180, 1, 0, 0);
		GL11.glDisable(GL11.GL_CULL_FACE);
		if(rd3) GL11.glScalef(.5f, .5f, .5f);
		Minecraft.getMinecraft().renderEngine.bindTexture(cap.getDocument().itemicon.local());
		model.render(1f);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

}