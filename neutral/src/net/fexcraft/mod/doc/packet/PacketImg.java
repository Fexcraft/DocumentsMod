package net.fexcraft.mod.doc.packet;

import io.netty.buffer.ByteBuf;
import net.fexcraft.mod.uni.packet.PacketBase;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PacketImg implements PacketBase<PacketImg> {

    public String loc;
    public byte[] img;

    @Override
    public PacketImg fill(Object... data){
        loc = (String)data[0];
        if(data.length > 1) img = (byte[])data[1];
        return this;
    }

    @Override
    public void encode(ByteBuf buffer){
        byte[] bytes = loc.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        if(img == null){
            buffer.writeInt(0);
            return;
        }
        buffer.writeInt(img.length);
        buffer.writeBytes(img);
    }

    @Override
    public void decode(ByteBuf buffer){
        loc = buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8).toString();
        int length = buffer.readInt();
        if(length == 0) return;
        buffer.readBytes(img = new byte[length]);
    }

}
