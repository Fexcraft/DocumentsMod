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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class DocRegistry {
	
	public static final HashMap<String, Document> DOCS = new HashMap<>();
	public static final ConcurrentHashMap<UUID, JsonMap> PLAYERS = new ConcurrentHashMap<>();
	public static ResourceLocation STONE = new ResourceLocation("minecraft:textures/blocks/stone.png");
	public static String player_img_url = "https://crafatar.com/avatars/<UUID>?size=32";
	public static boolean use_resourcepacks = false;
	public static JsonMap confmap;
	protected static File folder;
	private static File file;

	public static void init(FMLPreInitializationEvent event){
		file = new File(folder = event.getModConfigurationDirectory(), "/documents.json");
		if(!file.exists()){
			JsonMap map = new JsonMap();
			map.add("comment", "If you need help filling out this config file, visit the wiki!");
			map.add("wiki", "https://fexcraft.net/wiki/documents");
			map.add("warning", "A copy of this file's content is sent to the clients connecting to your server. DO NOT HOLD SENSITIVE DATA IN THIS FILE.");
			map.add("player_img_url", player_img_url);
			map.add("use_resourcepacks", use_resourcepacks);
			map.addMap("documents");
			map.getMap("documents").add("example_id", JsonHandler.parse(
					  "	{\r\n"
					+ "		\"size\": [ 188, 104 ],\r\n"
					+ "		\"name\": \"Example ID Card\",\r\n"
					+ "		\"description\":[\r\n"
					+ "			\"documents.example_id.desc0\",\r\n"
					+ "			\"documents.example_id.desc1\",\r\n"
					+ "			\"documents.example_id.desc2\"\r\n"
					+ "		],\r\n"
					+ "		\"fields\": {\r\n"
					+ "			\"info1\":{\r\n"
					+ "				\"type\": \"INFO_TEXT\",\r\n"
					+ "				\"position\": [ 62, 25 ],\r\n"
					+ "				\"size\": [ 115, 5 ],\r\n"
					+ "				\"value\": \"documents.example_id.info1\",\r\n"
					+ "				\"description\": [\r\n"
					+ "					\"documents.example_id.info1.desc\"\r\n"
					+ "				],\r\n"
					+ "				\"font_scale\": 0.5\r\n"
					+ "			},\r\n"
					+ "			\"uuid\":{\r\n"
					+ "				\"type\": \"UUID\",\r\n"
					+ "				\"comment\": \"hidden technical field\",\r\n"
					+ "				\"description\": [\r\n"
					+ "					\"documents.example_id.uuid.desc\"\r\n"
					+ "				]\r\n"
					+ "			},\r\n"
					+ "			\"name\":{\r\n"
					+ "				\"type\": \"PLAYER_NAME\",\r\n"
					+ "				\"position\": [ 62, 30 ],\r\n"
					+ "				\"size\": [ 114, 8 ],\r\n"
					+ "				\"description\": [\r\n"
					+ "					\"documents.example_id.name.desc\"\r\n"
					+ "				]\r\n"
					+ "			},\r\n"
					+ "			\"info2\":{\r\n"
					+ "				\"type\": \"INFO_TEXT\",\r\n"
					+ "				\"position\": [ 62, 41 ],\r\n"
					+ "				\"size\": [ 115, 5 ],\r\n"
					+ "				\"value\": \"documents.example_id.info2\",\r\n"
					+ "				\"description\": [\r\n"
					+ "					\"documents.example_id.info2.desc\"\r\n"
					+ "				],\r\n"
					+ "				\"font_scale\": 0.5\r\n"
					+ "			},\r\n"
					+ "			\"joined\":{\r\n"
					+ "				\"type\": \"JOIN_DATE\",\r\n"
					+ "				\"position\": [ 62, 46 ],\r\n"
					+ "				\"size\": [ 114, 8 ],\r\n"
					+ "				\"description\": [\r\n"
					+ "					\"documents.example_id.joined.desc\"\r\n"
					+ "				]\r\n"
					+ "			},\r\n"
					+ "			\"info3\":{\r\n"
					+ "				\"type\": \"INFO_TEXT\",\r\n"
					+ "				\"position\": [ 62, 57 ],\r\n"
					+ "				\"size\": [ 115, 5 ],\r\n"
					+ "				\"value\": \"documents.example_id.info3\",\r\n"
					+ "				\"description\": [\r\n"
					+ "					\"documents.example_id.info2.desc\"\r\n"
					+ "				],\r\n"
					+ "				\"font_scale\": 0.5\r\n"
					+ "			},\r\n"
					+ "			\"expiry\":{\r\n"
					+ "				\"type\": \"DATE\",\r\n"
					+ "				\"position\": [ 62, 62 ],\r\n"
					+ "				\"size\": [ 114, 8 ],\r\n"
					+ "				\"description\": [\r\n"
					+ "					\"documents.example_id.expiry.desc\"\r\n"
					+ "				]\r\n"
					+ "			},\r\n"
					+ "			\"img\":{\r\n"
					+ "				\"type\": \"PLAYER_IMG\",\r\n"
					+ "				\"position\": [ 9, 9 ],\r\n"
					+ "				\"size\": [ 48, 48 ]\r\n"
					+ "			}\r\n"
					+ "		},\r\n"
					+ "		\"textures\": {\r\n"
					+ "			\"maintex\": \"documents:textures/gui/example_id.png\"\r\n"
					+ "		},\r\n"
					+ "		\"pages\": {\r\n"
					+ "			\"main\": {\r\n"
					+ "				\"fields\": [\r\n"
					+ "					\"info1\",\r\n"
					+ "					\"info2\",\r\n"
					+ "					\"info3\",\r\n"
					+ "					\"name\",\r\n"
					+ "					\"joined\",\r\n"
					+ "					\"expiry\",\r\n"
					+ "					\"img\"\r\n"
					+ "				],\r\n"
					+ "				\"texture\": \"maintex\"\r\n"
					+ "			}\r\n"
					+ "		}\r\n"
					+ "	},", true));
			JsonHandler.print(file, map, PrintOption.SPACED);
		}
		confmap = JsonHandler.parse(file);
		player_img_url = confmap.getString("player_img_url", player_img_url);
		use_resourcepacks = confmap.getBoolean("use_resourcepacks", use_resourcepacks);
		load(confmap);
	}

	public static void load(JsonMap map){
		DOCS.clear();
		if(map.has("documents")) parseDocs(map.get("documents").asMap());
		player_img_url = map.getString("player_img_url", player_img_url);
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
			else map = new JsonMap();
			PLAYERS.put(uuid, map);
		}
		return map;
	}

	public static boolean noRS(){
		return !use_resourcepacks;
	}

	public static boolean useRS(){
		return use_resourcepacks;
	}

}
