package net.fexcraft.mod.doc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocumentsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(){
		ClientPlayNetworking.registerGlobalReceiver(DocPacketHandler21.SYNC_PACKET_TYPE, (packet, context) -> {
			Minecraft.getInstance().schedule(() -> {
				DocRegistry.parseDocs(packet.map);
				DocRegistry.getDocuments().values().forEach(doc -> doc.linktextures());
			});
		});
	}

}