package net.fexcraft.mod.doc;

import com.mojang.logging.LogUtils;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.packet.DocPacketHandler;
import net.fexcraft.mod.doc.ui.DocUI;
import net.fexcraft.mod.fcl.util.ExternalTextures;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.inv.UniStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Mod(Documents.MODID)
public class Documents {

	public static final String MODID = "documents";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
	public static final RegistryObject<Item> DOCITEM = ITEMS.register("document", () -> new DocumentItem());
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);
	public static DocConfig CONFIG;

	public Documents(){
		IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
		eventbus.addListener(this::commonSetup);
		ITEMS.register(eventbus);
		CONTAINERS.register(eventbus);
		MinecraftForge.EVENT_BUS.register(this);
		eventbus.addListener(this::addCreative);
		//
		CONFIG = new DocConfig(new File(FMLPaths.CONFIGDIR.get().toFile(), "/documents_config.json"));
		DocUI.register(this);
		UniStack.register(new DocStackApp(null));
	}

	private void commonSetup(final FMLCommonSetupEvent event){
		DocPacketHandler.INSTANCE = new DocPacketHandler20();
	}

	private void addCreative(BuildCreativeModeTabContentsEvent event){
		if(event.getTabKey() == CreativeModeTabs.INGREDIENTS)
			event.accept(DOCITEM);
	}

	@Mod.EventBusSubscriber(modid = "documents")
	public static class Events {

		@SubscribeEvent
		public static void onServerStarting(ServerStartingEvent event){
			DocRegistry.init(FMLPaths.CONFIGDIR.get().toFile());
			DocPerms.loadperms();
			DocCreator.REFERENCE = UniStack.createStack(new ItemStack(DocumentItem.INSTANCE));
		}

		@SubscribeEvent
		public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
			if(event.getEntity().level().isClientSide) return;
			DocRegistry.onPlayerJoin(UniEntity.getEntity(event.getEntity()));
		}

		@SubscribeEvent
		public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
			if(event.getEntity().level().isClientSide) return;
			DocRegistry.onPlayerLeave(UniEntity.getEntity(event.getEntity()));
		}

		@SubscribeEvent
		public static void onCmdReg(RegisterCommandsEvent event){
			event.getDispatcher().register(DocumentsCommand.get());
		}

	}

	public static InputStream getResource(String s){
		try{
			ResourceLocation rl = new ResourceLocation(s.replace("data/documents/", "documents:"));
			var opt = ServerLifecycleHooks.getCurrentServer().getResourceManager().getResource(rl);
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
