package net.fexcraft.mod.doc;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.uni.ConfigBase;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocConfig extends ConfigBase  {

    public static String PLAYER_IMG_URL;

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
    }

    @Override
    protected void onReload(JsonMap map){

    }

}
