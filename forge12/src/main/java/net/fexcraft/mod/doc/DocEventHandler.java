package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.lib.mc.network.PacketHandler;
import net.fexcraft.lib.mc.network.packet.PacketNBTTagCompound;
import net.fexcraft.mod.doc.cap.DocItemHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DocEventHandler {
	
	@SubscribeEvent
	public void onAttach(AttachCapabilitiesEvent<ItemStack> event){
		if(event.getObject().getItem() instanceof DocumentItem){
			event.addCapability(new ResourceLocation("documents:item"), new DocItemHandler(event.getObject()));
		}
	}
	
	@SubscribeEvent
	public void regItems(RegistryEvent.Register<Item> event){
		event.getRegistry().register(DocumentItem.INSTANCE = new DocumentItem());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void regModels(net.minecraftforge.client.event.ModelRegistryEvent event){
		if(DocRegistry.useRS()){
			net.minecraftforge.client.model.ModelLoader.setCustomMeshDefinition(DocumentItem.INSTANCE, new net.fexcraft.mod.doc.DocItemMeshDef());
			for(String key : DocRegistry.DOCS.keySet()){
				net.minecraft.client.renderer.block.model.ModelBakery.registerItemVariants(DocumentItem.INSTANCE, new ResourceLocation(DocMod.MODID, key));
			}
		}
		else net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(DocumentItem.INSTANCE, 0, new net.minecraft.client.renderer.block.model.ModelResourceLocation("documents:models/item/document", "inventory"));
	}
	
	@SubscribeEvent
	public void onJoin(PlayerLoggedInEvent event){
		if(event.player.world.isRemote) return;
		DocRegistry.opj(event.player);
		NBTTagCompound com = new NBTTagCompound();
		com.setString("target_listener", "docmod");
		com.setString("task", "sync");
		com.setString("config", JsonHandler.toString(DocRegistry.confmap, PrintOption.FLAT));
		PacketHandler.getInstance().sendTo(new PacketNBTTagCompound(com), (EntityPlayerMP)event.player);
	}
	
	@SubscribeEvent
	public void onJoin(PlayerLoggedOutEvent event){
		if(event.player.world.isRemote) return;
		DocRegistry.opl(event.player);
	}

}
