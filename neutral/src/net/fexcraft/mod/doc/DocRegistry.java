package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
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

    private static final LinkedHashMap<IDL, Document> DOCUMENTS = new LinkedHashMap<>();
    public static final ConcurrentHashMap<UUID, JsonMap> PLAYERS = new ConcurrentHashMap<>();
    public static final IDL STONE = IDLManager.getIDLCached("textures/blocks/stone.png");
    public static String NBTKEY_TYPE = "documents:type";
    public static String NBTKEY_DATA = "documents:data";
    public static File CONF_FOLDER;
    private static File DOCS_FOLDER;

    public static void init(File conf){
        StackWrapper.register(new DocStackApp(null));
        CONF_FOLDER = conf;
        DOCS_FOLDER = new File(CONF_FOLDER, "/documents/");
        if(!DOCS_FOLDER.exists()){
            try{
                DOCS_FOLDER.mkdirs();
                JsonMap map = JsonHandler.parse(Documents.getResource("data/documents/defaults/example_id.json"));
                JsonHandler.print(new File(DOCS_FOLDER, "/example_id.json"), map, JsonHandler.PrintOption.SPACED);
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        reload();
    }

    public static void reload(){
        DOCUMENTS.clear();
        for(File file : DOCS_FOLDER.listFiles()){
            if(file.isDirectory()) continue;
            try{
                JsonMap map = JsonHandler.parse(file);
                IDL id = IDLManager.getIDLCached(map.get("id").string_value());
                DOCUMENTS.put(id, new Document(id, map));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void parseDocs(JsonMap map){
        map.entries().forEach(entry -> {
            try{
                IDL id = IDLManager.getIDLCached(map.get("id").string_value());
                DOCUMENTS.put(id, new Document(id, entry.getValue().asMap()));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    public static void onPlayerJoin(EntityW player){
        File file = new File(CONF_FOLDER, "/documents_players/" + player.getUUID().toString() + ".json");
        JsonMap map = file.exists() ? JsonHandler.parse(file) : new JsonMap();
        if(!map.has("joined")) map.add("joined", Time.getDate());
        if(!map.has("name")) map.add("name", player.getName());
        PLAYERS.put(player.getUUID(), map);
    }

    public static void onPlayerLeave(EntityW player){
        JsonMap map = PLAYERS.remove(player.getUUID());
        if(map == null) return;
        map.add("last_on", Time.getDate());
        File file = new File(CONF_FOLDER, "/documents_players/" + player.getUUID() + ".json");
        if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
        JsonHandler.print(file, map, JsonHandler.PrintOption.FLAT);
    }

    public static JsonMap getPlayerData(String string){
        UUID uuid = UUID.fromString(string);
        JsonMap map = PLAYERS.get(uuid);
        if(map == null){
            File file = new File(CONF_FOLDER, "/documents_players/" + string + ".json");
            if(file.exists()) map = JsonHandler.parse(file);
            else map = new JsonMap();
            PLAYERS.put(uuid, map);
        }
        return map;
    }

    public static JsonMap getSyncMap(){
        return new JsonMap();//TODO
    }

    public static Document getDocument(String key){
        for(IDL idl : DOCUMENTS.keySet()){
            if(idl.colon().equals(key)) return DOCUMENTS.get(idl);
        }
        return null;
    }

    public static LinkedHashMap<IDL, Document> getDocuments(){
        return DOCUMENTS;
    }

}