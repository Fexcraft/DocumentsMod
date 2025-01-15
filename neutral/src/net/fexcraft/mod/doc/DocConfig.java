package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.fcl.UniFCL;
import net.fexcraft.mod.uni.ConfigBase;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocConfig extends ConfigBase  {

    public static String PLAYER_IMG_URL;
    public static String DEF_DATEFORMAT;
    public static String DEF_ISSUER_TYPE;
    public static String DEF_ISSUER_UUID;
    public static String DEF_ISSUER_NAME;
    public static DateFormat DATE_FORMAT;
    public static JsonArray FILL_MESSAGE;

    public DocConfig(File fl){
        super(fl, "Custom Documents Mod");
    }

    @Override
    protected void fillInfo(JsonMap map){
        map.add("info0", "In this file you can configure general DocumentsMod settings.");
        map.add("info1", "Documents are defined in '/config/documents.json' or via data-packs.");
        map.add("wiki", "https://fexcraft.net/wiki/mod/documents");
    }

    @Override
    protected void fillEntries(){
        String gen = "general";
        entries.add(new ConfigEntry(this, gen, "player_img_url", "https://crafatar.com/avatars/<UUID>?size=32")
            .info("Service URL with which Player Images are retrieved. <UUID> gets auto replaced with the player's UUID, <NAME> with the name.")
            .cons((entry, map) -> PLAYER_IMG_URL = entry.getString(map))
            .req(true, false)
        );
        entries.add(new ConfigEntry(this, gen, "date_format", "dd.MMM.yyyy")
            .info("Default (Java) date format to be used on documents fields without an own format specified.")
            .cons((entry, map) -> {
                DEF_DATEFORMAT = entry.getString(map);
                DATE_FORMAT = new SimpleDateFormat(DEF_DATEFORMAT);
            })
            .req(true, false)
        );
        entries.add(new ConfigEntry(this, gen, "fill_message", new JsonArray(
                "You have <AMOUNT> form/s to fill out to receive your documents.",
                    "Use '/documents fill <doc-id>' to start."
                ))
                .info("Multi-line greeting that displays when a player joins without having all documents filled/received yet.")
                .cons((entry, map) -> FILL_MESSAGE = entry.getJson(map).asArray())
                .req(true, false)
        );
        String iss = "default_issuer";
        entries.add(new ConfigEntry(this, iss, "type", "server")
            .info("Type of default Document Issuer (when not issued by a player in-game), set the type to 'player', if you want the UUID to be a player's.")
            .cons((entry, map) -> DEF_ISSUER_TYPE = entry.getString(map))
            .req(true, false)
        );
        entries.add(new ConfigEntry(this, iss, "uuid", "00000000-0000-0000-0000-000000000000")
            .info("ID/UUID of default Document Issuer (when not issued by a player in-game), make sure the 'type' is 'player' if you plan to use a player UUID.")
            .cons((entry, map) -> DEF_ISSUER_UUID = entry.getString(map))
            .req(true, false)
        );
        entries.add(new ConfigEntry(this, iss, "name", "Server")
            .info("Name of default Document Issuer (when not issued by a player in-game).")
            .cons((entry, map) -> DEF_ISSUER_NAME = entry.getString(map))
            .req(true, false)
        );
    }

    @Override
    protected void onReload(JsonMap map){

    }

}
