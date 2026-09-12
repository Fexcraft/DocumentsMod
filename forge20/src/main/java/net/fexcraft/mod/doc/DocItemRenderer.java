package net.fexcraft.mod.doc;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.Generator;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.fcl.util.FCLRenderTypes;
import net.fexcraft.mod.fcl.util.Renderer20;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.inv.UniStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.NonNullLazy;
import org.joml.Quaternionf;

import static net.fexcraft.lib.frl.gen.Generator.Values.WIDTH;
import static net.fexcraft.lib.frl.gen.Generator.Values.HEIGHT;
import static net.fexcraft.lib.frl.gen.Generator.Values.DEPTH;
import static net.fexcraft.mod.fcl.util.Renderer20.AX;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocItemRenderer {

	private static Polyhedron poly = new Generator(Generator.Type.CUBOID).set(WIDTH, 1f).set(HEIGHT, 1f).set(DEPTH, 0f).make().genNorm();
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
			Renderer20.set(pose, src, v0);
			FCLRenderTypes.setCutout(texture);
			pose.pushPose();
			pose.translate(0, 1, 0.5);
			pose.mulPose(new Quaternionf().rotateAxis(Static.rad180, AX));
			poly.render();
			pose.popPose();
		}
	});

}
