package net.fexcraft.mod.doc.data;

import com.mojang.authlib.GameProfile;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.mc.utils.Static;
import net.fexcraft.mod.doc.DocRegistry;
import net.fexcraft.mod.uni.Appendable;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.world.EntityW;

import java.util.UUID;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocStackApp implements Appendable<StackWrapper> {

    private final StackWrapper stack;

    public DocStackApp(StackWrapper type){
        stack = type;
        if(stack == null) return;
        stack.createTagIfMissing();
    }

    @Override
    public Appendable<StackWrapper> create(StackWrapper type){
        return new DocStackApp(type);
    }

    @Override
    public String id(){
        return "documents:item";
    }

    public Document getDocument(){
        return DocRegistry.DOCUMENTS.get(stack.getTag().getString(NBTKEY));
    }

    public boolean isIssued(){
        return stack.getTag().has("document:issued");
    }

    public String getValue(String key){
        if(!stack.getTag().has("document:" + key)) return null;
        return stack.getTag().getString("document:" + key);
    }

    public void setValue(String key, String val){
        stack.getTag().set("document:" + key, val);
    }

    public void issueBy(EntityW player, boolean client){
        setValue("issuer", player.getUUID().toString());
        setValue("issued", Time.getDate() + "");
        setValue("issuer_name", player.getName());
        if(client) return;
        try{
            GameProfile gp = Static.getServer().getPlayerProfileCache().getProfileByUUID(UUID.fromString(getValue("uuid")));
            setValue("player_name", gp.getName());
        }
        catch(Exception e){
            e.printStackTrace();
            setValue("player_name", getValue("uuid"));
        }
    }

}
