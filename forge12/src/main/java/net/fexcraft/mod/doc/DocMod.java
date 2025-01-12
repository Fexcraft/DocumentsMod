package net.fexcraft.mod.doc;

import net.fexcraft.lib.mc.network.PacketHandler;
import net.fexcraft.lib.mc.network.PacketHandler.PacketHandlerType;
import net.fexcraft.mod.doc.gui.GuiHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

@Mod(modid = DocMod.MODID, name = DocMod.NAME, version = "2.0", dependencies = "required-after:fcl")
public class DocMod {
	
    public static final String MODID = "documents";
    public static final String NAME = "Documents Mod";
    @Mod.Instance(MODID)
    private static DocMod INSTANCE;
    public static DocConfig CONFIG;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        CONFIG = new DocConfig(new File(event.getModConfigurationDirectory(), "/documents_config.json"));
        DocRegistry.init(event.getModConfigurationDirectory());
        DocPerms.loadperms();
        MinecraftForge.EVENT_BUS.register(new DocEventHandler());
        if(event.getSide().isClient()){
        	net.fexcraft.lib.mc.render.FCLItemModelLoader.addItemModel(new ResourceLocation("documents:document"), net.fexcraft.mod.doc.render.DocumentModel.INSTANCE);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
		NetworkRegistry.INSTANCE.registerGuiHandler(getInstance(), new GuiHandler());
        PacketHandler.registerListener(PacketHandlerType.NBT, Side.SERVER, new ListenerServer());
        if(event.getSide().isClient()){
        	PacketHandler.registerListener(PacketHandlerType.NBT, Side.CLIENT, new ListenerClient());
        	DocRegistry.getDocuments().values().forEach(doc -> doc.linktextures());
        }
    }

	public static DocMod getInstance(){
		return INSTANCE;
	}
	
}
