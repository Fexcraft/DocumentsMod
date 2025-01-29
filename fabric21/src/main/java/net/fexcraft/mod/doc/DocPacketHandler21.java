package net.fexcraft.mod.doc;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.doc.packet.*;
import net.fexcraft.mod.fcl.util.ExternalTextures;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.world.EntityW;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocPacketHandler21 extends DocPacketHandler {

    public static final ResourceLocation IMG_PACKET = ResourceLocation.parse("documents:img");
    public static final CustomPacketPayload.Type<PacketImg21> IMG_PACKET_TYPE = new CustomPacketPayload.Type<>(IMG_PACKET);
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketImg21> IMG_PACKET_CODEC = StreamCodec.of(PacketImg21::encode, buffer -> new PacketImg21().decode(buffer));
    //
    public static final ResourceLocation SYNC_PACKET = ResourceLocation.parse("documents:sync");
    public static final CustomPacketPayload.Type<PacketSync21> SYNC_PACKET_TYPE = new CustomPacketPayload.Type<>(SYNC_PACKET);
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSync21> SYNC_PACKET_CODEC = StreamCodec.of(PacketSync21::encode, buffer -> new PacketSync21().decode(buffer));

    public DocPacketHandler21(){
        PayloadTypeRegistry.playS2C().register(IMG_PACKET_TYPE, IMG_PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(IMG_PACKET_TYPE, IMG_PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SYNC_PACKET_TYPE, SYNC_PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(IMG_PACKET_TYPE, (packet, context) -> {
            context.server().execute(() -> {
                try{
                    ExternalTextures.get(packet.loc, packet.img);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            });
        });
    }

    @Override
    public void sendSync(EntityW player, JsonMap map){
        ServerPlayNetworking.getSender((ServerPlayer)player.direct()).sendPacket(new PacketSync21(map));
    }

    @Override
    public void sendImg(EntityW player, String loc, byte[] img){
        if(player.isOnClient()){
            ClientPlayNetworking.getSender().sendPacket(new PacketImg21(loc));
        }
        else{
            ServerPlayNetworking.getSender((ServerPlayer)player.direct()).sendPacket(new PacketImg21(loc, img));
        }
    }

    @Override
    public IDL requestServerTexture(String str){
        ClientPlayNetworking.getSender().sendPacket(new PacketImg21(str));
        return IDLManager.getIDLCached(str);
    }

    public static class PacketImg21 extends PacketImg implements CustomPacketPayload {

        public PacketImg21(){}

        public PacketImg21(Object... data){
            fill(data);
        }

        public static void encode(RegistryFriendlyByteBuf buffer, PacketImg21 packet){
            packet.encode(buffer);
        }

        public PacketImg21 decode(RegistryFriendlyByteBuf buffer){
            super.decode(buffer);
            return this;
        }

        @Override
        public Type<? extends CustomPacketPayload> type(){
            return IMG_PACKET_TYPE;
        }

    }

    public static class PacketSync21 extends PacketSync implements CustomPacketPayload {

        public PacketSync21(){}

        public PacketSync21(JsonMap map){
            fill(map);
        }

        public static void encode(RegistryFriendlyByteBuf buffer, PacketSync21 packet){
            packet.encode(buffer);
        }

        public PacketSync21 decode(RegistryFriendlyByteBuf buffer){
            super.decode(buffer);
            return this;
        }

        @Override
        public Type<? extends CustomPacketPayload> type(){
            return SYNC_PACKET_TYPE;
        }

    }

}
