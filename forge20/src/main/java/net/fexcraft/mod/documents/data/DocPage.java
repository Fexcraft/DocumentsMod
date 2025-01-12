package net.fexcraft.mod.documents.data;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;

import java.util.ArrayList;

public class DocPage {
	
	public ArrayList<DocPageField> fields = new ArrayList<>();
	public String texture;
	public final String id;
	public int sizex, sizey;

	public DocPage(String key, JsonMap map){
		id = key;
		texture = map.getString("texture", "main");
		map.getArray("fields").value.forEach(elm -> {
			if(elm.isArray()){
				JsonArray array = elm.asArray();
				fields.add(new DocPageField(array));
			}
			else fields.add(new DocPageField(elm.string_value()));
		});
		if(map.has("size")){
			JsonArray array = map.getArray("size");
			sizex = array.get(0).integer_value();
			sizey = array.get(1).integer_value();
			if(sizex > 256) sizex = 256;
			if(sizex < 0) sizex = 0;
			if(sizey > 256) sizey = 256;
			if(sizey < 0) sizey = 0;
		}
		else sizex = sizey = 0;
	}
	
	public static class DocPageField {
		
		public int x = -1, y = -1, sx = -1, sy = -1;
		public final String id;
		
		public DocPageField(String id){
			this.id = id;
		}

		public DocPageField(JsonArray array){
			id = array.get(0).string_value();
			if(array.size() > 1){
				x = array.get(1).integer_value();
				y = array.get(2).integer_value();
			}
			if(array.size() > 3){
				sx = array.get(3).integer_value();
				sy = array.get(4).integer_value();
			}
		}
		
	}

}
