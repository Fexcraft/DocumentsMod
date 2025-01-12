package net.fexcraft.mod.doc.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.mod.doc.DocConfig;
import net.fexcraft.mod.doc.DocRegistry;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FieldData {

	private DateFormat df;
	public final FieldType type;
	public final String key;
	public int posx;
	public int posy;
	public int sizex;
	public int sizey;
	public float fontscale;
	public String value;
	public String name;
	public String format;
	public String prefix;
	public Integer color;
	public boolean can_empty;
	public boolean autoscale;
	public ArrayList<String> description = new ArrayList<>();

	public FieldData(String key, JsonMap map){
		type = FieldType.valueOf(map.getString("type", FieldType.TEXT.name()).toUpperCase());
		name = map.getString("name", this.key = key);
		JsonArray pos = map.getArray("position", 0);
		posx = pos.empty() ? 0 : pos.get(0).integer_value();
		posy = pos.empty() ? 0 : pos.get(1).integer_value();
		JsonArray size = map.getArray("size", 0);
		sizex = size.empty() ? 0 : size.get(0).integer_value();
		sizey = size.empty() ? 0 : size.get(1).integer_value();
		value = map.getString("value", null);
		fontscale = map.getFloat("font_scale", 0);
		can_empty = map.getBoolean("can_be_empty", false);
		if(key.equals("uuid")) can_empty = false;
		if(map.has("description")){
			map.getArray("description").value.forEach(elm -> description.add(elm.string_value()));
		}
		autoscale = map.getBoolean("auto_scale", fontscale == 0);
		color = map.has("font_color") ? new RGB(map.get("font_color").string_value()).packed : null;
		prefix = map.getString("prefix", null);
		format = map.getString("format", null);
		if(type.date()) df = format == null ? DocConfig.DATE_FORMAT : new SimpleDateFormat(format);
	}

	public FieldData(String key, FieldType type){
		this.type = type;
		name = this.key = key;
	}

	public String getValue(DocStackApp cap){
		String val = cap.getValue(key);
		if(val == null && value != null) val = value;
		if(type.number()) return val == null ? "0" : val;
		if(type == FieldType.JOIN_DATE){
			JsonMap pd = DocRegistry.getPlayerData(cap.getValue("uuid"));
			try{
				return df.format(new Date(pd.getLong("joined", Time.getDate())));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else if(type == FieldType.PLAYER_NAME){
			return cap.getValue("player_name");
		}
		else if(type == FieldType.PLAYER_IMG){
			return DocConfig.PLAYER_IMG_URL
				.replace("<UUID>", cap.getValue("uuid"))
				.replace("<NAME>", cap.getValue("player_name"));
		}
		else if((type == FieldType.DATE || type == FieldType.ISSUED) && val != null){
			try{
				return df.format(new Date(Long.parseLong(val)));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return val == null ? "" : val;
	}

}
