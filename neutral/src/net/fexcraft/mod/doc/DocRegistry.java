package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.fcl.FCL;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.world.EntityW;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocRegistry {

    public static final LinkedHashMap<IDL, Document> DOCUMENTS = new LinkedHashMap<>();
    public static final ConcurrentHashMap<UUID, JsonMap> PLAYERS = new ConcurrentHashMap<>();
    public static final IDL STONE = IDLManager.getIDLCached("textures/blocks/stone.png");
    public static String NBTKEY = "documents:type";
    public static File FOLDER;
    private static File CONF_FILE;
    public static JsonMap CONF_MAP;

    public static void init(File conf){
        StackWrapper.register(new DocStackApp(null));
        FOLDER = conf;
        CONF_FILE = new File(FOLDER, "/documents.json");
        if(!CONF_FILE.exists()){
            try{
                JsonMap map = JsonHandler.parse(DocRegistry.class.getClassLoader().getResourceAsStream("data/documents/defaults/def_config.json"));
                JsonHandler.print(CONF_FILE, map, JsonHandler.PrintOption.SPACED);
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        CONF_MAP = JsonHandler.parse(CONF_FILE);
        load(CONF_MAP);
    }

    public static void load(JsonMap map){
        DOCUMENTS.clear();
        if(map.has("documents")) parseDocs(map.get("documents").asMap());
    }

    public static void reload(){
        load(CONF_MAP = JsonHandler.parse(CONF_FILE));
        if(EnvInfo.DEV) FCL.LOGGER.info(CONF_MAP);
    }

    private static void parseDocs(JsonMap map){
        map.entries().forEach(entry -> {
            DOCUMENTS.put(IDLManager.getIDLCached(entry.getKey()), new Document(entry.getKey(), entry.getValue().asMap()));
        });
    }

    public static void onPlayerJoin(EntityW player){
        File file = new File(FOLDER, "/documents/" + player.getUUID().toString() + ".json");
        JsonMap map = file.exists() ? JsonHandler.parse(file) : new JsonMap();
        if(!map.has("joined")) map.add("joined", Time.getDate());
        if(!map.has("name")) map.add("name", player.getName());
        PLAYERS.put(player.getUUID(), map);
    }

    public static void onPlayerLeave(EntityW player){
        JsonMap map = PLAYERS.remove(player.getUUID());
        if(map == null) return;
        map.add("last_on", Time.getDate());
        File file = new File(FOLDER, "/documents/" + player.getUUID() + ".json");
        if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
        JsonHandler.print(file, map, JsonHandler.PrintOption.FLAT);
    }

    public static JsonMap getPlayerData(String string){
        UUID uuid = UUID.fromString(string);
        JsonMap map = PLAYERS.get(uuid);
        if(map == null){
            File file = new File(FOLDER, "/documents/" + string + ".json");
            if(file.exists()) map = JsonHandler.parse(file);
            else map = new JsonMap();
            PLAYERS.put(uuid, map);
        }
        return map;
    }

}