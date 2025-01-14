package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.mc.api.packet.IPacketListener;
import net.fexcraft.lib.mc.network.packet.PacketNBTTagCompound;

public class DocClientListener implements IPacketListener<PacketNBTTagCompound> {

	@Override
	public String getId(){
		return "docmod";
	}

	@Override
	public void process(PacketNBTTagCompound packet, Object[] objs){
		String task = packet.nbt.getString("task");
		switch(task){
			case "sync":{
				JsonMap map = JsonHandler.parse(packet.nbt.getString("data"), true).asMap();
				DocRegistry.parseDocs(map);
            	DocRegistry.getDocuments().values().forEach(doc -> doc.linktextures());
				return;
			}
			default: return;
		}
	}

}
