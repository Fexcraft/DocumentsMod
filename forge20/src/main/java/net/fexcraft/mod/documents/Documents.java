package net.fexcraft.mod.documents;

import com.mojang.logging.LogUtils;
import net.fexcraft.mod.documents.data.DocumentItem;
import net.fexcraft.mod.documents.gui.UiPacketReceiver;
import net.fexcraft.mod.documents.packet.GuiPacketF;
import net.fexcraft.mod.documents.packet.SyncPacketF;
import net.fexcraft.mod.fcl.util.ClientPacketPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.fexcraft.mod.documents.gui.DocEditorContainer;
import net.fexcraft.mod.documents.gui.DocViewerContainer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.io.File;

@Mod(Documents.MODID)
public class Documents {

	public static final String MODID = "documents";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
	public static final RegistryObject<Item> DOCITEM = ITEMS.register("document", () -> new DocumentItem());
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);
	public static final RegistryObject<MenuType<DocEditorContainer>> DOC_EDITOR = CONTAINERS.register("editor", () -> IForgeMenuType.create(DocEditorContainer::new));
	public static final RegistryObject<MenuType<DocViewerContainer>> DOC_VIEWER = CONTAINERS.register("viewer", () -> IForgeMenuType.create(DocViewerContainer::new));
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("documents", "channel"))
		.clientAcceptedVersions(pro -> true)
		.serverAcceptedVersions(pro -> true)
		.networkProtocolVersion(() -> "documents")
		.simpleChannel();

	public Documents(){
		IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
		eventbus.addListener(this::commonSetup);
		ITEMS.register(eventbus);
		CONTAINERS.register(eventbus);
		MinecraftForge.EVENT_BUS.register(this);
		eventbus.addListener(this::addCreative);
	}

	private void commonSetup(final FMLCommonSetupEvent event){
		DocRegistry.init();
		DocPerms.loadperms();
		CHANNEL.registerMessage(1, GuiPacketF.class, (packet, buffer) -> buffer.writeNbt(packet.com()), buffer -> new GuiPacketF(buffer.readNbt()), (packet, context) -> {
			context.get().enqueueWork(() -> {
				if(context.get().getDirection().getOriginationSide().isClient()){
					ServerPlayer player = context.get().getSender();
					((UiPacketReceiver)player.containerMenu).onPacket(packet.com(), false);
				}
				else{
					((UiPacketReceiver)ClientPacketPlayer.get().containerMenu).onPacket(packet.com(), true);
				}
			});
			context.get().setPacketHandled(true);
		});
		CHANNEL.registerMessage(2, SyncPacketF.class, (packet, buffer) -> packet.write(buffer), buffer -> SyncPacketF.read(buffer), (packet, context) -> {
			context.get().enqueueWork(() -> {
				if(context.get().getDirection().getOriginationSide().isServer()){
					Documents.LOGGER.info(packet.map().toString());
					DocRegistry.load(packet.map());
					DocRegistry.DOCS.values().forEach(doc -> doc.linktextures());
				}
			});
			context.get().setPacketHandled(true);
		});
	}

	private void addCreative(BuildCreativeModeTabContentsEvent event){
		if(event.getTabKey() == CreativeModeTabs.INGREDIENTS)
			event.accept(DOCITEM);
	}

	@Mod.EventBusSubscriber(modid = "documents")
	public static class Events {

		@SubscribeEvent
		public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
			if(event.getEntity().level().isClientSide) return;
			DocRegistry.opj(event.getEntity());
			CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), new SyncPacketF(DocRegistry.confmap));
		}

		@SubscribeEvent
		public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
			if(event.getEntity().level().isClientSide) return;
			DocRegistry.opl(event.getEntity());
		}

		@SubscribeEvent
		public static void onCmdReg(RegisterCommandsEvent event){
			event.getDispatcher().register(DocumentsCommand.get());
		}

	}

	public static void sendSyncTo(ServerPlayer player){
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncPacketF(DocRegistry.confmap));
	}

	public static MinecraftServer getCurrentServer(){
		return ServerLifecycleHooks.getCurrentServer();
	}

	public static void send(boolean toclient, CompoundTag compound, Player player){
		if(toclient){
			CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new GuiPacketF(compound));
		}
		else{
			CHANNEL.send(PacketDistributor.SERVER.noArg(), new GuiPacketF(compound));
		}
	}

	public static File getConfigDir(){
		return FMLPaths.CONFIGDIR.get().toFile();
	}

	public static void openViewer(Player player, int page){
		NetworkHooks.openScreen((ServerPlayer)player, new MenuProvider() {
			@Override
			public Component getDisplayName(){
				return Component.literal("Document Viewer");
			}

			@Override
			public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player){
				return new DocViewerContainer(i, inventory);
			}
		}, buf -> buf.writeInt(page));
	}

	public static void openViewerOrEditor(Player player, CompoundTag com){
		NetworkHooks.openScreen((ServerPlayer)player, new MenuProvider() {
			@Override
			public Component getDisplayName(){
				return Component.literal(com.contains("document:issued") ? "Document Viewer" : "Document Editor");
			}

			@Override
			public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player){
				return com.contains("document:issued") ? new DocViewerContainer(i, inventory) : new DocEditorContainer(i, inventory);
			}
		}, buf -> buf.writeInt(0));
	}

}
