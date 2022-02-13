package net.fexcraft.mod.doc;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = DocMod.MODID, name = DocMod.NAME, version = DocMod.VERSION)
public class DocMod {
	
    public static final String MODID = "documents";
    public static final String NAME = "Documents Mod";
    public static final String VERSION = "1.0";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        DocRegistry.init(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        //
    }
}
