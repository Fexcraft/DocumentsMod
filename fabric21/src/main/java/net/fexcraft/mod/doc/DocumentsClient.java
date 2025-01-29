package net.fexcraft.mod.doc;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fexcraft.mod.doc.packet.DocPacketHandler;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.world.EntityW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.BowItem;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocumentsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(){
		ClientPlayNetworking.registerGlobalReceiver(DocPacketHandler21.IMG_PACKET_TYPE, (packet, context) -> {
			try{
				byte[] tex = DocRegistry.getServerTexture(packet.loc);
				EntityW player = UniEntity.getEntity(context.responseSender());
				DocPacketHandler.INSTANCE.sendImg(player, packet.loc, tex);
			}
			catch(Exception e){
				throw new RuntimeException(e);
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(DocPacketHandler21.SYNC_PACKET_TYPE, (packet, context) -> {
			Minecraft.getInstance().schedule(() -> {
				DocRegistry.parseDocs(packet.map);
				DocRegistry.getDocuments().values().forEach(doc -> doc.linktextures());
			});
		});
	}

}