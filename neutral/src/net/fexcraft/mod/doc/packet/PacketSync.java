package net.fexcraft.mod.doc.packet;

import io.netty.buffer.ByteBuf;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.uni.packet.PacketBase;

import java.nio.charset.StandardCharsets;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PacketSync implements PacketBase<PacketSync> {

    public JsonMap map;

    @Override
    public PacketSync fill(Object... data){
        map = (JsonMap)data[0];
        return this;
    }

    @Override
    public void encode(ByteBuf buffer){
        String str = JsonHandler.toString(map, JsonHandler.PrintOption.FLAT);
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    @Override
    public void decode(ByteBuf buffer){
        String str = buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8).toString();
        map = JsonHandler.parse(str, true).asMap();
    }

}
