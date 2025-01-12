package net.fexcraft.mod.doc;

import net.fexcraft.lib.mc.network.PacketHandler;
import net.fexcraft.lib.mc.network.PacketHandler.PacketHandlerType;
import net.fexcraft.lib.mc.render.ExternalTextureHelper;
import net.fexcraft.mod.doc.gui.GuiHandler;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.InputStream;

@Mod(modid = Documents.MODID, name = Documents.NAME, version = "2.0", dependencies = "required-after:fcl")
public class Documents {
	
    public static final String MODID = "documents";
    public static final String NAME = "Documents Mod";
    @Mod.Instance(MODID)
    private static Documents INSTANCE;
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
        if(event.getSide().isClient()){
        	PacketHandler.registerListener(PacketHandlerType.NBT, Side.CLIENT, new ListenerClient());
        	DocRegistry.getDocuments().values().forEach(doc -> doc.linktextures());
        }
    }

	public static Documents getInstance(){
		return INSTANCE;
	}

    public static IDL getTexture(String str){
        return IDLManager.getIDLCached(ExternalTextureHelper.get(str).toString());
    }

    public static InputStream getResource(String str){
        return DocRegistry.class.getClassLoader().getResourceAsStream(str);
    }
	
}
