package net.fexcraft.mod.doc.data;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.mod.doc.DocRegistry;
import net.fexcraft.mod.uni.world.EntityW;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocPlayerData {

    private final JsonMap map;

    public DocPlayerData(JsonMap map, UUID uuid){
        this.map = map;
        if(!map.has("joined")) map.add("joined", Time.getDate());
        if(!map.has("uuid")) map.add("uuid", uuid.toString());
    }

    public DocPlayerData(JsonMap map, EntityW player){
        this(map, player.getUUID());
        if(!map.has("name")) map.add("name", player.getName());
    }

    public void setLastOn(){
        map.add("last_on", Time.getDate());
    }

    public JsonMap map(){
        return map;
    }

    public String getJoined(DateFormat df){
        if(map.has("joined")){
            return df.format(new Date(map.get("joined").long_value()));
        }
        else return df.format(new Date());
    }

    public boolean hasReceived(String key){
        if(!map.has("received")) map.addMap("received");
        for(String entry : map.getMap("received").value.keySet()){
            if(entry.equals(key)) return true;
        }
        return false;
    }

    public void addReceived(String key){
        if(!map.has("received")) map.addMap("received");
        map.getMap("received").add(key, Time.getDate());
        DocRegistry.save(this);
    }

}
