package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.lib.mc.network.PacketHandler;
import net.fexcraft.lib.mc.network.packet.PacketNBTTagCompound;
import net.fexcraft.mod.uni.UniEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DocEventHandler {
	
	@SubscribeEvent
	public void regItems(RegistryEvent.Register<Item> event){
		event.getRegistry().register(DocumentItem.INSTANCE = new DocumentItem());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void regModels(net.minecraftforge.client.event.ModelRegistryEvent event){
		net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(DocumentItem.INSTANCE, 0, new net.minecraft.client.renderer.block.model.ModelResourceLocation("documents:models/item/document", "inventory"));
	}
	
	@SubscribeEvent
	public void onJoin(PlayerLoggedInEvent event){
		if(event.player.world.isRemote) return;
		DocRegistry.onPlayerJoin(UniEntity.getEntity(event.player));
		NBTTagCompound com = new NBTTagCompound();
		com.setString("target_listener", "docmod");
		com.setString("task", "sync");
		com.setString("config", JsonHandler.toString(DocRegistry.getSyncMap(), PrintOption.FLAT));
		PacketHandler.getInstance().sendTo(new PacketNBTTagCompound(com), (EntityPlayerMP)event.player);
	}
	
	@SubscribeEvent
	public void onJoin(PlayerLoggedOutEvent event){
		if(event.player.world.isRemote) return;
		DocRegistry.onPlayerLeave(UniEntity.getEntity(event.player));
	}

}
