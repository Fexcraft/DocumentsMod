package net.fexcraft.mod.documents.packet;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.minecraft.network.FriendlyByteBuf;

public record SyncPacketF(JsonMap map) {

	public FriendlyByteBuf write(FriendlyByteBuf buf){
		String string = JsonHandler.toString(map, JsonHandler.PrintOption.FLAT);
		buf.writeInt(string.length());
		buf.writeUtf(string);
		return buf;
	}

	public static SyncPacketF read(FriendlyByteBuf buf){
		return new SyncPacketF(JsonHandler.parse(buf.readUtf(buf.readInt()), true).asMap());
	}

}
