package net.fexcraft.mod.doc.data;

import java.util.ArrayList;

import net.fexcraft.app.json.JsonMap;

public class DocPage {
	
	public ArrayList<String> fields = new ArrayList<>();
	public String texture;
	public final String id;

	public DocPage(String key, JsonMap value){
		id = key;
		texture = value.getString("texture", "main");
		value.getArray("fields").value.forEach(elm -> fields.add(elm.string_value()));
	}

}
