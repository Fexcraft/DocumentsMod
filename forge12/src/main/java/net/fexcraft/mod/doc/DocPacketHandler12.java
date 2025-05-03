package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.doc.PacketSyncHandler.I12_PacketSync;
import net.fexcraft.mod.doc.packet.DocPacketHandler;
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
        instance.registerMessage(PacketSyncHandler.class, I12_PacketSync.class, 2, Side.CLIENT);
    }

    @Override
    public void sendSync(EntityW player, JsonMap map){
        instance.sendTo((IMessage)new I12_PacketSync().fill(map), player.local());
    }

}
