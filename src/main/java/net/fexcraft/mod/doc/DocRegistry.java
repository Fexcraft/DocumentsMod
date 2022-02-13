package net.fexcraft.mod.doc;

import java.io.File;
import java.util.HashMap;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.app.json.JsonMap;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class DocRegistry {
	
	public static final HashMap<String, Document> DOCS = new HashMap<>();
	public static String player_img_url = "https://crafatar.com/avatars/<UUID>?size=32";
	public static boolean use_resourcepacks = true;

	public static void init(FMLPreInitializationEvent event){
		File file = new File(event.getModConfigurationDirectory(), "/documents.json");
		if(!file.exists()){
			JsonMap map = new JsonMap();
			map.add("comment", "If you need help filling out this config file, visit the wiki!");
			map.add("wiki", "https://fexcraft.net/wiki/documents");
			map.add("warning", "A copy of this file's content is sent to the clients connecting to your server. DO NOT HOLD SENSITIVE DATA IN THIS FILE.");
			map.add("player_img_url", player_img_url);
			map.add("use_resourcepacks", use_resourcepacks);
			map.addMap("documents");
			JsonHandler.print(file, map, PrintOption.SPACED);
		}
		JsonMap map = JsonHandler.parse(file);
		if(map.has("documents")) parseDocs(map.get("documents").asMap());
		player_img_url = map.getString("player_img_url", player_img_url);
		use_resourcepacks = map.getBoolean("use_resourcepacks", use_resourcepacks);
	}
	
	private static void parseDocs(JsonMap map){
		map.entries().forEach(entry -> {
			DOCS.put(entry.getKey(), new Document(entry.getKey(), entry.getValue().asMap()));
		});
	}

	public void sync(JsonMap map){
		DOCS.clear();
		parseDocs(map);
	}

}
