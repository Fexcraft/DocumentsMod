package net.fexcraft.mod.doc;

import io.netty.buffer.ByteBuf;
import net.fexcraft.mod.doc.packet.PacketSync;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PacketSyncHandler implements IMessageHandler<PacketSyncHandler.I12_PacketSync, IMessage> {

    public static class I12_PacketSync extends PacketSync implements IMessage {

        @Override
        public void fromBytes(ByteBuf buf){
            decode(buf);
        }

        @Override
        public void toBytes(ByteBuf buf){
            encode(buf);
        }

    }

    @Override
    public IMessage onMessage(I12_PacketSync packet, MessageContext ctx){
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            DocRegistry.parseDocs(packet.map);
            DocRegistry.getDocuments().values().forEach(doc -> doc.linktextures());
        });
        return null;
    }

}
