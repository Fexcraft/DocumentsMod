package net.fexcraft.mod.documents.gui;

import net.minecraft.nbt.CompoundTag;

public interface UiPacketReceiver {

	public void onPacket(CompoundTag com, boolean client);

}
