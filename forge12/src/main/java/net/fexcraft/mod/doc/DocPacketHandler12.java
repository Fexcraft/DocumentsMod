package net.fexcraft.mod.doc;

import io.netty.buffer.ByteBuf;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.doc.PacketImgHandler.I12_PacketImg;
import net.fexcraft.mod.doc.PacketSyncHandler.I12_PacketSync;
import net.fexcraft.mod.doc.packet.DocPacketHandler;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.world.EntityW;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;


/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocPacketHandler12 extends DocPacketHandler {

    private static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel("documents");

    public DocPacketHandler12(){
        instance.registerMessage(PacketImgHandler.Client.class, I12_PacketImg.class, 0, Side.CLIENT);
        instance.registerMessage(PacketImgHandler.Server.class, I12_PacketImg.class, 1, Side.SERVER);
        instance.registerMessage(PacketSyncHandler.class, I12_PacketSync.class, 2, Side.CLIENT);
    }

    @Override
    public void sendSync(EntityW player, JsonMap map){
        instance.sendTo((IMessage)new I12_PacketSync().fill(map), player.local());
    }

    @Override
    public void sendImg(EntityW player, String loc, byte[] img){
        if(player.isOnClient()){
            instance.sendToServer((IMessage)new I12_PacketImg().fill(loc));
        }
        else{
            instance.sendTo((IMessage)new I12_PacketImg().fill(loc, img), player.local());
        }
    }

    @Override
    public IDL requestServerTexture(String str){
        instance.sendToServer((IMessage)new I12_PacketImg().fill(str));
        return IDLManager.getIDLCached(str);
    }

}
