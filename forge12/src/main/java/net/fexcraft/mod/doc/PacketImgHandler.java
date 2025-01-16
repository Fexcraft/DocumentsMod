package net.fexcraft.mod.doc;

import io.netty.buffer.ByteBuf;
import net.fexcraft.lib.mc.render.ExternalTextureHelper;
import net.fexcraft.mod.doc.packet.PacketImg;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.world.EntityW;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PacketImgHandler {

    public static class I12_PacketImg extends PacketImg implements IMessage {

        @Override
        public void fromBytes(ByteBuf buf){
            decode(buf);
        }

        @Override
        public void toBytes(ByteBuf buf){
            encode(buf);
        }

    }


    public static class Server implements IMessageHandler<I12_PacketImg, IMessage> {

        @Override
        public IMessage onMessage(I12_PacketImg packet, MessageContext ctx){
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                try{
                    byte[] tex = Documents.getServerTexture(packet.loc);
                    EntityW player = UniEntity.getEntity(ctx.getServerHandler().player);
                    DocPacketHandler12.INSTANCE.sendImg(player, packet.loc, tex);
                }
                catch(IOException e){
                    throw new RuntimeException(e);
                }
            });
            return null;
        }

    }


    public static class Client implements IMessageHandler<I12_PacketImg, IMessage> {

        @Override
        public IMessage onMessage(I12_PacketImg packet, MessageContext ctx){
            ExternalTextureHelper.get(packet.loc, packet.img);
            return null;
        }

    }

}
