package net.fexcraft.mod.doc.data;

import net.fexcraft.lib.common.math.Time;
import net.fexcraft.mod.doc.DocRegistry;
import net.fexcraft.mod.fcl.FCL;
import net.fexcraft.mod.uni.Appendable;
import net.fexcraft.mod.uni.inv.UniStack;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.WrapperHolder;

import java.util.UUID;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_TYPE;
import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_DATA;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocStackApp implements Appendable<UniStack> {

    private final UniStack uni;

    public DocStackApp(UniStack type){
        uni = type;
        if(uni == null) return;
        uni.stack.createTagIfMissing();
    }

    @Override
    public Appendable<UniStack> create(UniStack type){
        return type.stack.getItem().direct() instanceof DocItem ? new DocStackApp(type) : null;
    }

    @Override
    public String id(){
        return "documents:item";
    }

    public Document getDocument(){
        return DocRegistry.getDocument(uni.stack.getTag().getString(NBTKEY_TYPE));
    }

    public boolean isIssued(){
        return getValue("issued") != null;
    }

    public boolean hasValue(String key){
        TagCW com = uni.stack.getTag().getCompound(NBTKEY_DATA);
        if(com == null) return false;
        return com.has(key);
    }

    public String getValue(String key){
        TagCW com = uni.stack.getTag().getCompound(NBTKEY_DATA);
        if(com == null || !com.has(key)) return null;
        return com.getString(key);
    }

    public String getValueNN(String key){
        String val = getValue(key);
        return val == null ? "" : val;
    }

    public void setValue(String key, String val){
        if(!uni.stack.getTag().has(NBTKEY_DATA)) uni.stack.getTag().set(NBTKEY_DATA, TagCW.create());
        uni.stack.getTag().getCompound(NBTKEY_DATA).set(key, val);
    }

    public void issueBy(EntityW player, boolean client){
        setValue("issuer", player.getUUID().toString());
        setValue("issued", Time.getDate() + "");
        setValue("issuer_name", player.getName());
        setValue("issuer_type", "player");
        if(client) return;
        try{
            setValue("player_name", WrapperHolder.getNameFor(UUID.fromString(getValue("uuid"))));
        }
        catch(Exception e){
            e.printStackTrace();
            setValue("player_name", getValue("uuid"));
        }
        DocPlayerData dpd = DocRegistry.PLAYERS.get(player.getUUID());
        if(dpd != null){
            if(dpd.map().has("joined")) setValue("player_joined", dpd.map().get("joined").string_value());
            dpd.addReceived(uni.stack.getTag().getString(NBTKEY_TYPE));
        }
        else{
            setValue("player_joined", Time.getDate() + "");
            player.send("ERROR - PLAYER DATA IS NULL");
        }
    }

}
