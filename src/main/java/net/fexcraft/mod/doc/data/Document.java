package net.fexcraft.mod.doc.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.mc.render.ExternalTextureHelper;
import net.fexcraft.mod.doc.DocRegistry;
import net.minecraft.util.ResourceLocation;

public class Document {
	
	public final String id;
	public int sizex, sizey;
	public LinkedHashMap<String, FieldData> fields = new LinkedHashMap<>();
	public HashMap<String, ResourceLocation> textures = new HashMap<>();
	public LinkedHashMap<String, DocPage> pages = new LinkedHashMap<>();
	public HashMap<String, ArrayList<String>> enums = new HashMap<>();
	public ArrayList<String> description = new ArrayList<>();
	public ResourceLocation itemicon;

	public Document(String key, JsonMap map){
		id = key;
		if(map.has("size")){
			JsonArray array = map.getArray("size");
			sizex = array.get(0).integer_value();
			sizey = array.get(1).integer_value();
			if(sizex > 256) sizex = 256;
			if(sizex < 0) sizex = 0;
			if(sizey > 256) sizey = 256;
			if(sizey < 0) sizey = 0;
		}
		else sizex = sizey = 256;
		if(map.has("fields")){
			map.get("fields").asMap().entries().forEach(entry -> {
				fields.put(entry.getKey(), new FieldData(entry.getKey(), entry.getValue().asMap()));
			});
		}
		if(!fields.containsKey("uuid")){
			fields.put("uuid", new FieldData("uuid", FieldType.UUID));
		}
		if(!fields.containsKey("issued")){
			fields.put("issued", new FieldData("issued", FieldType.ISSUED));
		}
		if(!fields.containsKey("issuer")){
			fields.put("issuer", new FieldData("issuer", FieldType.ISSUER));
		}
		if(map.has("textures")){
			map.get("textures").asMap().entries().forEach(entry -> {
				String str = entry.getValue().string_value();
				ResourceLocation resloc = null;
				if(str.startsWith("external;")){
					str = str.substring(9);
					resloc = ExternalTextureHelper.get(str);
				}
				else resloc = new ResourceLocation(str);
				textures.put(entry.getKey(), resloc);
			});
		}
		else textures.put("main", DocRegistry.STONE);
		if(map.has("pages")){
			map.get("pages").asMap().entries().forEach(entry -> {
				pages.put(entry.getKey(), new DocPage(entry.getKey(), entry.getValue().asMap()));
			});
		}
		if(map.has("enums")){
			map.get("enums").asMap().entries().forEach(entry -> {
				ArrayList<String> vals = new ArrayList<>();
				entry.getValue().asArray().value.forEach(elm -> vals.add(elm.string_value()));
				enums.put(entry.getKey(), vals);
			});
		}
		if(map.has("description")){
			map.get("description").asArray().value.forEach(elm -> description.add(elm.string_value()));
		}
		itemicon = new ResourceLocation(map.getString("icon", "minecraft:textures/items/paper.png"));
	}

}
