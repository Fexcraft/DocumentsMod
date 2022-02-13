package net.fexcraft.mod.doc;

import net.fexcraft.lib.mc.network.PacketHandler;
import net.fexcraft.lib.mc.network.PacketHandler.PacketHandlerType;
import net.fexcraft.mod.doc.gui.GuiHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = DocMod.MODID, name = DocMod.NAME, version = DocMod.VERSION)
public class DocMod {
	
    public static final String MODID = "documents";
    public static final String NAME = "Documents Mod";
    public static final String VERSION = "1.0";
    @Mod.Instance(MODID)
    private static DocMod INSTANCE;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        DocRegistry.init(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
		NetworkRegistry.INSTANCE.registerGuiHandler(getInstance(), new GuiHandler());
        PacketHandler.registerListener(PacketHandlerType.NBT, Side.SERVER, new ListenerServer());
        if(event.getSide().isClient()){
        	PacketHandler.registerListener(PacketHandlerType.NBT, Side.CLIENT, new ListenerClient());
        }
    }

	public static DocMod getInstance(){
		return INSTANCE;
	}
}
