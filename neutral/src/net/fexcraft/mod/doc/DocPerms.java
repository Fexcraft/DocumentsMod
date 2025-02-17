package net.fexcraft.mod.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.WrapperHolder;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocPerms {
	
	public static HashMap<UUID, ArrayList<String>> perms = new HashMap<>();
	
	public static void loadperms(){
		perms.clear();
		File file = new File(DocRegistry.CONF_FOLDER, "documents_perms.json");
		JsonMap map = JsonHandler.parse(file, true).asMap();
		if(map.empty() || !map.has("players")){
			map.addMap("players");
			map.getMap("players");
			JsonArray array = new JsonArray();
			array.add("command.get.*");
			array.add("command.reload-perms");
			array.add("command.reload-docs");
			array.add("document.edit.*");
			array.add("document.issue.*");
			map.getMap("players").add("61699b2e-d327-4a01-9f1e-0ea8c3f06bc6", array);
			JsonHandler.print(file, map, PrintOption.SPACED);
			return;
		}
		map.get("players").asMap().entries().forEach(entry -> {
			UUID uuid = UUID.fromString(entry.getKey());
			ArrayList<String> prms = new ArrayList<>();
			entry.getValue().asArray().elements().forEach(elm -> {
				prms.add(elm.string_value());
			});
			perms.put(uuid, prms);
		});
	}

	public static boolean hasPerm(EntityW player, String node){
		if(WrapperHolder.isSinglePlayer()) return true;
		UUID uuid = player.getUUID();
		return perms.containsKey(uuid) && perms.get(uuid).contains(node);
	}

	public static boolean hasPerm(EntityW player, String node, String suffix){
		if(WrapperHolder.isSinglePlayer()) return true;
		UUID uuid = player.getUUID();
		return perms.containsKey(uuid) && (perms.get(uuid).contains(node + ".*") || perms.get(uuid).contains(node + "." + suffix));
	}

}
