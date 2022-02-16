package net.fexcraft.mod.doc.data;

import java.util.ArrayList;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;

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
				fields.add(new DocPageField(array.get(0).string_value(), array.get(1).integer_value(), array.get(2).integer_value()));
			}
			else fields.add(new DocPageField(elm.string_value(), -1, -1));
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
		else sizex = sizey = 256;
	}
	
	public static class DocPageField {
		
		public int x, y;
		public final String id;
		
		public DocPageField(String id, int posx, int posy){
			this.id = id;
			this.x = posx;
			this.y = posy;
		}
		
	}

}
