package net.fexcraft.mod.documents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.util.*;

public class ExternalTextures {

	private static final Map<String, ResourceLocation> MAP = new HashMap<String, ResourceLocation>();
	private static final HashSet<String> KEY = new HashSet<>();
	static{ KEY.add("documents"); }

	public static ResourceLocation get(String url){
		if(MAP.containsKey(url)) return MAP.get(url);
		ResourceLocation texture = new ResourceLocation("documents", url.replaceAll("[^a-z0-9_.-]", ""));
		MAP.put(url, texture);
		File file = new File("./temp/doc_download/" + texture.getPath());
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		file.deleteOnExit();
		Minecraft.getInstance().textureManager.register(texture, new HttpTexture(file, url, texture, false, null));
		return texture;
	}

}
