package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.uni.ConfigBase;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocConfig extends ConfigBase  {

    public static String PLAYER_IMG_URL;
    public static String DEF_DATEFORMAT;
    public static String DEF_ISSUER_TYPE;
    public static String DEF_ISSUER_UUID;
    public static String DEF_ISSUER_NAME;

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
            .cons((entry, map) -> DEF_DATEFORMAT = entry.getString(map))
            .req(true, false)
        );
        String iss = "default_issuer";
        entries.add(new ConfigEntry(this, iss, "type", "server")
            .info("Type of default Document Issuer (when not issued by a player in-game), set the type to 'player', if you want the UUID to be a player's.")
            .cons((entry, map) -> DEF_ISSUER_TYPE = entry.getString(map))
            .req(true, false)
        );
        entries.add(new ConfigEntry(this, iss, "uuid", "server")
            .info("ID/UUID of default Document Issuer (when not issued by a player in-game), make sure the 'type' is 'player' if you plan to use a player UUID.")
            .cons((entry, map) -> DEF_ISSUER_UUID = entry.getString(map))
            .req(true, false)
        );
        entries.add(new ConfigEntry(this, iss, "name", "server")
            .info("Name of default Document Issuer (when not issued by a player in-game).")
            .cons((entry, map) -> DEF_ISSUER_NAME = entry.getString(map))
            .req(true, false)
        );
    }

    @Override
    protected void onReload(JsonMap map){

    }

}
