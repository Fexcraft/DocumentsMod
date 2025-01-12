package net.fexcraft.mod.documents.data;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.documents.DocRegistry;
import net.minecraft.world.item.ItemStack;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class FieldData {
	
	public final FieldType type;
	public int posx, posy, sizex, sizey;
	public float fontscale;
	public String value, name, key, format;
	public Integer color;
	public boolean can_empty, autoscale;
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
		color = map.has("font_color") ? Integer.parseInt(map.get("font_color").string_value().replace("#", ""), 16) : null;
		format = map.getString("format", null);
	}

	public FieldData(String key, FieldType type){
		this.type = type;
		name = this.key = key;
	}

	public String getValue(ItemStack stack){
		if(!stack.hasTag()) return null;
		String val = stack.getTag().contains("document:" + key) ? stack.getTag().getString("document:" + key) : null;
		if(val == null && value != null) val = value;
		if(type.number()) return val == null ? "0" : val;
		if(type == FieldType.JOIN_DATE){
			JsonMap pd = DocRegistry.getPlayerData(stack.getTag().getString("document:uuid"));
			try{
				return LocalDate.ofEpochDay(new Date(pd.getLong("joined", new Date().getTime())).getTime() / 86400000).toString();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else if(type == FieldType.PLAYER_NAME){
			return stack.getTag().getString("document:player_name");
		}
		else if(type == FieldType.PLAYER_IMG){
			return DocRegistry.player_img_url
				.replace("<UUID>", stack.getTag().getString("document:uuid"))
				.replace("<NAME>", stack.getTag().getString("document:player_name"));
		}
		else if((type == FieldType.DATE || type == FieldType.ISSUED) && val != null){
			try{
				return LocalDate.ofEpochDay(Long.parseLong(val) / 86400000).toString();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return val == null ? "" : val;
	}

}
