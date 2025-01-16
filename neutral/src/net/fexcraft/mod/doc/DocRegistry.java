package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.mod.doc.data.DocPlayerData;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.packet.DocPacketHandler;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.WrapperHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocRegistry {

    private static final LinkedHashMap<IDL, Document> DOCUMENTS = new LinkedHashMap<>();
    public static final ConcurrentHashMap<UUID, DocPlayerData> PLAYERS = new ConcurrentHashMap<>();
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

    public static void parseDocs(JsonMap sync){
        //if(!WrapperHolder.isSinglePlayer()) DOCUMENTS.clear();
        sync.entries().forEach(entry -> {
            try{
                JsonMap map = entry.getValue().asMap();;
                IDL id = IDLManager.getIDLCached(map.get("id").string_value());
                DOCUMENTS.put(id, new Document(id, map));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    public static void onPlayerJoin(EntityW player){
        DocPacketHandler.INSTANCE.sendSync(player, getSyncMap());
        //
        File file = new File(CONF_FOLDER, "/documents_players/" + player.getUUID().toString() + ".json");
        DocPlayerData dpd = new DocPlayerData(file.exists() ? JsonHandler.parse(file) : new JsonMap(), player);
        PLAYERS.put(player.getUUID(), dpd);
        ArrayList<IDL> docs = new ArrayList<>();
        for(Document doc : DOCUMENTS.values()){
            if(!doc.autoissue) continue;
            if(!dpd.hasReceived(doc.id.colon())) docs.add(doc.id);
        }
        if(docs.size() == 0) return;
        for(JsonValue<?> val : DocConfig.FILL_MESSAGE.value){
            player.send(val.string_value().replace("<NAME>", player.getName()).replace("<AMOUNT>", docs.size() + ""));
        }
        String ids = "";
        for(IDL doc : docs) ids += doc.colon() + ", ";
        ids = ids.substring(0, ids.length() - 2);
        player.send("DocIDs: " + ids);
    }

    public static void onPlayerLeave(EntityW player){
        DocPlayerData dpd = PLAYERS.remove(player.getUUID());
        if(dpd == null) return;
        dpd.setLastOn();
        save(dpd);
    }

    public static DocPlayerData getPlayerData(String string){
        UUID uuid = UUID.fromString(string);
        DocPlayerData map = PLAYERS.get(uuid);
        if(map == null){
            File file = new File(CONF_FOLDER, "/documents_players/" + string + ".json");
            PLAYERS.put(uuid, new DocPlayerData(file.exists() ? JsonHandler.parse(file) : new JsonMap(), uuid));
        }
        return map;
    }

    public static JsonMap getSyncMap(){
        JsonMap map = new JsonMap();
        for(Document doc : DOCUMENTS.values()){
            map.add(doc.id.colon(), doc.confmap);
        }
        return map;
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

    public static void save(DocPlayerData dpd){
        File file = new File(CONF_FOLDER, "/documents_players/" + dpd.map().get("uuid").string_value() + ".json");
        if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
        JsonHandler.print(file, dpd.map(), JsonHandler.PrintOption.FLAT);
    }

    public static int getDocumentIndex(String key){
        int idx = 0;
        for(IDL idl : DOCUMENTS.keySet()){
            if(idl.colon().equals(key)) return idx;
            idx++;
        }
        return -1;
    }

    public static Document getDocumentByIndex(int idx){
        int index = 0;
        for(Document doc : DOCUMENTS.values()){
            if(index == idx) return doc;
            index++;
        }
        return null;
    }

}