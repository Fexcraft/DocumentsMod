package net.fexcraft.mod.doc;

import net.fexcraft.lib.mc.api.packet.IPacketListener;
import net.fexcraft.lib.mc.network.packet.PacketNBTTagCompound;

public class ListenerServer implements IPacketListener<PacketNBTTagCompound> {

	@Override
	public String getId(){
		return "docmod";
	}

	@Override
	public void process(PacketNBTTagCompound packet, Object[] objs){
		String task = packet.nbt.getString("task");
		//EntityPlayerMP player = (EntityPlayerMP)objs[0];
		switch(task){
			//
			default: return;
		}
	}

}
