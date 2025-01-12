package net.fexcraft.mod.documents;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.fexcraft.mod.documents.gui.DocEditorScreen;
import net.fexcraft.mod.documents.gui.DocViewerScreen;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod.EventBusSubscriber(modid = "documents", bus = MOD, value = Dist.CLIENT)
public class ClientEvents {

	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event){
		MenuScreens.register(Documents.DOC_EDITOR.get(), DocEditorScreen::new);
		MenuScreens.register(Documents.DOC_VIEWER.get(), DocViewerScreen::new);
	}

}
