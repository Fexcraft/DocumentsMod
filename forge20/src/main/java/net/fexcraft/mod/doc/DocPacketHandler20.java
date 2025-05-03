package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.doc.packet.*;
import net.fexcraft.mod.fcl.util.ExternalTextures;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.world.EntityW;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.io.IOException;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocPacketHandler20 extends DocPacketHandler {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("documents", "channel"))
            .clientAcceptedVersions(pro -> true)
            .serverAcceptedVersions(pro -> true)
            .networkProtocolVersion(() -> "documents")
            .simpleChannel();

    public DocPacketHandler20(){
        CHANNEL.registerMessage(2, PacketSync.class, PacketSync::encode, buffer -> {
            PacketSync pkt = new PacketSync();
            pkt.decode(buffer);
            return pkt;
        }, (packet, context) -> {
            context.get().enqueueWork(() -> {
                if(context.get().getDirection().getOriginationSide().isServer()){
                    DocRegistry.parseDocs(packet.map);
                    DocRegistry.getDocuments().values().forEach(doc -> doc.linktextures());
                }
            });
            context.get().setPacketHandled(true);
        });
    }

    @Override
    public void sendSync(EntityW player, JsonMap map){
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player.local()), new PacketSync().fill(map));
    }

}
