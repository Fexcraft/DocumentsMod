package net.fexcraft.mod.doc;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.doc.packet.*;
import net.fexcraft.mod.uni.world.EntityW;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocPacketHandler21 extends DocPacketHandler {

    public static final Identifier SYNC_PACKET = Identifier.parse("documents:sync");
    public static final CustomPacketPayload.Type<PacketSync21> SYNC_PACKET_TYPE = new CustomPacketPayload.Type<>(SYNC_PACKET);
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSync21> SYNC_PACKET_CODEC = StreamCodec.of(PacketSync21::encode, buffer -> new PacketSync21().decode(buffer));

    public DocPacketHandler21(){
        PayloadTypeRegistry.clientboundPlay().register(SYNC_PACKET_TYPE, SYNC_PACKET_CODEC);
    }

    @Override
    public void sendSync(EntityW player, JsonMap map){
        ServerPlayNetworking.getSender((ServerPlayer)player.direct()).sendPacket(new PacketSync21(map));
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
