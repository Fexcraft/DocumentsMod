package net.fexcraft.mod.documents;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.documents.data.Document;
import net.fexcraft.mod.documents.data.DocumentItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DocRegistry {
	
	public static final HashMap<String, Document> DOCS = new HashMap<>();
	public static final ConcurrentHashMap<UUID, JsonMap> PLAYERS = new ConcurrentHashMap<>();
	public static ResourceLocation STONE = new ResourceLocation("minecraft:textures/blocks/stone.png");
	public static String player_img_url = "https://crafatar.com/avatars/<UUID>?size=32";
	public static JsonMap confmap;
	protected static File cfgfolder;
	private static File file;

	public static void init(){
		file = new File(cfgfolder = Documents.getConfigDir(), "/documents.json");
		if(!file.exists()){
			JsonMap map = new JsonMap();
			map.add("comment", "If you need help filling out this config file, visit the wiki!");
			map.add("wiki", "https://fexcraft.net/wiki/mod/documents");
			map.add("warning", "A copy of this file's content is sent to the clients connecting to your server. DO NOT HOLD SENSITIVE DATA IN THIS FILE.");
			map.add("player_img_url", player_img_url);
			//map.add("use_resourcepacks", use_resourcepacks);
			map.addMap("documents");
			JsonMap exid = new JsonMap();
			exid.add("size", new JsonArray(188, 104));
			exid.add("name", "Example ID Card");
			JsonArray desc = new JsonArray();
			exid.add("description", new JsonArray(
				"documents.example_id.desc0",
				"documents.example_id.desc1",
				"documents.example_id.desc2"
			));
			JsonMap fields = new JsonMap();
			JsonMap info1 = new JsonMap();
			info1.add("type", "INFO_TEXT");
			info1.add("position", new JsonArray(62, 25));
			info1.add("size", new JsonArray(115, 5));
			info1.add("value", "documents.example_id.info1");
			info1.add("description", new JsonArray("documents.example_id.info1.desc"));
			info1.add("font_scale", 0.5f);
			fields.add("info1", info1);
			JsonMap uuid = new JsonMap();
			uuid.add("type", "UUID");
			uuid.add("comment0", "hidden technical field");
			uuid.add("comment1", "You can enter PLAYER NAME into the field, or player UUID.");
			uuid.add("description", new JsonArray(
				"documents.example_id.uuid.desc0",
				"documents.example_id.uuid.desc1",
				"documents.example_id.uuid.desc2"
			));
			fields.add("uuid", uuid);
			JsonMap name = new JsonMap();
			name.add("type", "PLAYER_NAME");
			name.add("position", new JsonArray(62, 30));
			name.add("size", new JsonArray(114, 8));
			name.add("description", new JsonArray("documents.example_id.name.desc"));
			fields.add("name", name);
			JsonMap info2 = new JsonMap();
			info2.add("type", "INFO_TEXT");
			info2.add("position", new JsonArray(62, 41));
			info2.add("size", new JsonArray(115, 5));
			info2.add("value", "documents.example_id.info2");
			info2.add("description", new JsonArray("documents.example_id.info2.desc"));
			info2.add("font_scale", 0.5f);
			fields.add("info2", info2);
			JsonMap joined = new JsonMap();
			joined.add("type", "JOIN_DATE");
			joined.add("position", new JsonArray(62, 46));
			joined.add("size", new JsonArray(114, 8));
			joined.add("description", new JsonArray("documents.example_id.joined.desc"));
			fields.add("joined", joined);
			JsonMap info3 = new JsonMap();
			info3.add("type", "INFO_TEXT");
			info3.add("position", new JsonArray(62, 57));
			info3.add("size", new JsonArray(115, 5));
			info3.add("value", "documents.example_id.info3");
			info3.add("description", new JsonArray("documents.example_id.info3.desc"));
			info3.add("font_scale", 0.5f);
			fields.add("info3", info3);
			JsonMap expiry = new JsonMap();
			expiry.add("type", "DATE");
			expiry.add("position", new JsonArray(62, 62));
			expiry.add("size", new JsonArray(114, 8));
			expiry.add("description", new JsonArray("documents.example_id.expiry.desc"));
			fields.add("expiry", expiry);
			JsonMap image = new JsonMap();
			image.add("type", "PLAYER_IMG");
			image.add("position", new JsonArray(9, 9));
			image.add("size", new JsonArray(48, 48));
			fields.add("img", image);
			exid.add("fields", fields);
			exid.add("textures", new JsonMap("maintex", "documents:textures/gui/example_id.png"));
			JsonMap pages = new JsonMap();
			JsonMap main = new JsonMap();
			main.add("fields", new JsonArray("info1", "info2", "info3", "name", "joined", "expiry", "img"));
			main.add("texture", "maintex");
			pages.add("main", main);
			exid.add("pages", pages);
			map.getMap("documents").add("example_id", exid);
			Documents.LOGGER.info(map.toString());
			JsonHandler.print(file, map, PrintOption.SPACED);
		}
		confmap = JsonHandler.parse(file);
		player_img_url = confmap.getString("player_img_url", player_img_url);
		//use_resourcepacks = confmap.getBoolean("use_resourcepacks", use_resourcepacks);
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

	public static void opj(Player player){
		File file = new File(cfgfolder, "/documents/" + player.getGameProfile().getId().toString() + ".json");
		JsonMap map = file.exists() ? JsonHandler.parse(file) : new JsonMap();
		if(!map.has("joined")) map.add("joined", new Date().getTime());
		if(!map.has("name")) map.add("name", player.getGameProfile().getName());
		PLAYERS.put(player.getGameProfile().getId(), map);
	}

	public static void opl(Player player){
		JsonMap map = PLAYERS.remove(player.getGameProfile().getId());
		if(map == null) return;
		map.add("laston", new Date().getTime());
		File file = new File(cfgfolder, "/documents/" + player.getGameProfile().getId().toString() + ".json");
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		JsonHandler.print(file, map, PrintOption.FLAT);
	}

	public static JsonMap getPlayerData(String string){
		UUID uuid = UUID.fromString(string);
		JsonMap map = PLAYERS.get(uuid);
		if(map == null){
			File file = new File(cfgfolder, "/documents/" + string + ".json");
			if(file.exists()) map = JsonHandler.parse(file);
			else map = new JsonMap();
			PLAYERS.put(uuid, map);
		}
		return map;
	}

	public static Document get(ItemStack stack){
		if(!stack.hasTag() || !stack.getTag().contains(DocumentItem.NBTKEY)) return null;
		else return DOCS.get(stack.getTag().getString(DocumentItem.NBTKEY));
	}

}
