package net.fexcraft.mod.doc;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.packet.DocPacketHandler;
import net.fexcraft.mod.doc.ui.DocUI;
import net.fexcraft.mod.fcl.FCL;
import net.fexcraft.mod.fcl.util.ExternalTextures;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.inv.UniStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Documents implements ModInitializer {

	public static final String MODID = "documents";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	//
	public static DocumentItem DOCUMENT;
	public static DocConfig CONFIG;

	@Override
	public void onInitialize(){
		CONFIG = new DocConfig(new File(FabricLoader.getInstance().getConfigDir().toFile(), "/documents_config.json"));
		DocUI.register(this);
		DocPacketHandler.INSTANCE = new DocPacketHandler21();
		UniStack.register(new DocStackApp(null));
		//
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ResourceLocation.parse("documents:document"));
		DOCUMENT = Registry.register(BuiltInRegistries.ITEM, key.location(), new DocumentItem(key));
		//
		ServerLifecycleEvents.SERVER_STARTING.register(event -> {
			DocRegistry.init(FabricLoader.getInstance().getConfigDir().toFile());
			DocPerms.loadperms();
			DocCreator.REFERENCE = UniStack.createStack(new ItemStack(DOCUMENT));
		});
		ServerPlayConnectionEvents.JOIN.register((lis, sender, server) -> {
			DocRegistry.onPlayerJoin(UniEntity.getEntity(lis.player));
		});
		ServerPlayConnectionEvents.DISCONNECT.register((lis, server) -> {
			DocRegistry.onPlayerLeave(UniEntity.getEntity(lis.player));
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) ->
			dispatcher.register(DocumentsCommand.get())
		);
	}

	public static InputStream getResource(String s){
		try{
			ResourceLocation rl = ResourceLocation.parse(s.replace("data/documents/", "documents:"));
			var opt = FCL.SERVER.get().getResourceManager().getResource(rl);
			if(opt.isPresent()) return opt.get().open();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

	public static IDL getTexture(String str){
		if(str.contains("external;")) str = str.substring(9);
		return IDLManager.getIDLCached(ExternalTextures.get(MODID, str).toString());
	}

}