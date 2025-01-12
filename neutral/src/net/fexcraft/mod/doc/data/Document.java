package net.fexcraft.mod.doc.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.doc.DocRegistry;
import net.fexcraft.mod.doc.Documents;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Document {
	
	public final IDL id;
	public int sizex, sizey;
	public LinkedHashMap<String, FieldData> fields = new LinkedHashMap<>();
	public HashMap<String, IDL> textures = new HashMap<>();
	public HashMap<String, String> rawtextures = new HashMap<>();
	public LinkedHashMap<String, DocPage> pages = new LinkedHashMap<>();
	public HashMap<String, ArrayList<String>> enums = new HashMap<>();
	public ArrayList<String> description = new ArrayList<>();
	public IDL itemicon;
	public String icon;
	public String name;

	public Document(IDL key, JsonMap map){
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
				rawtextures.put(entry.getKey(), entry.getValue().string_value());
			});
		}
		else rawtextures.put("main", DocRegistry.STONE.colon());
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
		icon = map.getString("icon", "minecraft:textures/items/paper.png");
		name = map.getString("name", "Unnamed Document");
	}
	
	public void linktextures(){
		if(icon.startsWith("external;")) itemicon = Documents.getTexture(icon.substring(9));
		else itemicon = IDLManager.getIDLCached(icon);
		//
		for(Entry<String, String> entry : rawtextures.entrySet()){
			IDL resloc = null;
			String str = entry.getValue();
			if(str.startsWith("external;")){
				str = str.substring(9);
				resloc = Documents.getTexture(str);
			}
			else resloc = IDLManager.getIDLCached(str);
			textures.put(entry.getKey(), resloc);
		}
	}

}
