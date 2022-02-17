package net.fexcraft.mod.doc;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.mod.doc.data.Document;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class DocRegistry {
	
	public static final HashMap<String, Document> DOCS = new HashMap<>();
	public static final ConcurrentHashMap<UUID, JsonMap> PLAYERS = new ConcurrentHashMap<>();
	public static String player_img_url = "https://crafatar.com/avatars/<UUID>?size=32";
	public static boolean use_resourcepacks = true;
	private static File folder;

	public static void init(FMLPreInitializationEvent event){
		File file = new File(folder = event.getModConfigurationDirectory(), "/documents.json");
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

	public static void opj(EntityPlayer player){
		File file = new File(folder, "/documents/" + player.getGameProfile().getId().toString() + ".json");
		JsonMap map = file.exists() ? JsonHandler.parse(file) : new JsonMap();
		if(!map.has("joined")) map.add("joined", Time.getDate());
		if(!map.has("name")) map.add("name", player.getGameProfile().getName());
		PLAYERS.put(player.getGameProfile().getId(), map);
	}

	public static void opl(EntityPlayer player){
		JsonMap map = PLAYERS.remove(player.getGameProfile().getId());
		if(map == null) return;
		map.add("laston", Time.getDate());
		File file = new File(folder, "/documents/" + player.getGameProfile().getId().toString() + ".json");
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		JsonHandler.print(file, map, PrintOption.FLAT);
	}

	public static JsonMap getPlayerData(String string){
		UUID uuid = UUID.fromString(string);
		JsonMap map = PLAYERS.get(uuid);
		if(map == null){
			File file = new File(folder, "/documents/" + string + ".json");
			if(file.exists()) map = JsonHandler.parse(file);
			PLAYERS.put(uuid, map);
		}
		return map;
	}

}
