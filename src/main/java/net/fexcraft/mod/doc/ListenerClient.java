package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.mc.api.packet.IPacketListener;
import net.fexcraft.lib.mc.network.packet.PacketNBTTagCompound;

public class ListenerClient implements IPacketListener<PacketNBTTagCompound> {

	@Override
	public String getId(){
		return "docmod";
	}

	@Override
	public void process(PacketNBTTagCompound packet, Object[] objs){
		String task = packet.nbt.getString("task");
		//EntityPlayer player = (EntityPlayer)objs[0];
		switch(task){
			case "sync":{
				JsonMap map = JsonHandler.parse(packet.nbt.getString("config"), true).asMap();
				DocRegistry.load(map);
            	DocRegistry.DOCS.values().forEach(doc -> doc.linktextures());
				return;
			}
			default: return;
		}
	}

}
