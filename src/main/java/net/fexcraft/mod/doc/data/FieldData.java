package net.fexcraft.mod.doc.data;

import java.util.ArrayList;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;

public class FieldData {
	
	public final FieldType type;
	public int posx, posy, sizex, sizey;
	public float fontscale;
	public String value;
	public boolean can_empty, autoscale;
	public ArrayList<String> description = new ArrayList<>();

	public FieldData(JsonMap map){
		type = FieldType.valueOf(map.getString("type", FieldType.TEXT.name()));
		JsonArray pos = map.getArray("position");
		posx = pos.get(0).integer_value();
		posy = pos.get(1).integer_value();
		JsonArray size = map.getArray("size");
		sizex = size.get(0).integer_value();
		sizey = size.get(1).integer_value();
		value = map.getString("value", null);
		fontscale = map.getFloat("font_scale", 1f);
		can_empty = map.getBoolean("can_be_empty", false);
		if(map.has("description")){
			map.getArray("description").value.forEach(elm -> description.add(elm.string_value()));
		}
		autoscale = map.getBoolean("auto_scale", true);
	}

}
