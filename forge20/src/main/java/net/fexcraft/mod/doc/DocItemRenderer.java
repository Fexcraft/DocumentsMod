package net.fexcraft.mod.doc;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ModelRendererTurbo;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.fcl.FCL;
import net.fexcraft.mod.fcl.util.FCLRenderTypes;
import net.fexcraft.mod.fcl.util.Renderer120;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.item.UniStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.NonNullLazy;
import org.joml.Quaternionf;

import static net.fexcraft.mod.fcl.util.Renderer120.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocItemRenderer {

	private static ModelRendererTurbo model = new ModelRendererTurbo(null, 0, 0, 1, 1).newBoxBuilder().setSize(1, 1, 0).build();
	private static Document doc;
	private static IDL texture;

	public static final NonNullLazy<BlockEntityWithoutLevelRenderer> RENDERER = NonNullLazy.of(() -> new BlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()){
		@Override
		public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource src, int v0, int v1){
			DocStackApp app = UniStack.getApp(stack, DocStackApp.class);
			texture = DocRegistry.STONE;
			if(app != null){
				doc = app.getDocument();
				if(doc != null) texture = doc.itemicon;
			}
			Renderer120.set(pose, src, v0);
			FCLRenderTypes.setCutout(texture);
			pose.pushPose();
			pose.translate(0, 1, 0.5);
			pose.mulPose(new Quaternionf().rotateAxis(Static.rad180, AX));
			model.render(1f);
			pose.popPose();
		}
	});

}
