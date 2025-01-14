package net.fexcraft.mod.doc;

import com.mojang.authlib.GameProfile;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.mc.utils.Static;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.data.FieldData;
import net.fexcraft.mod.doc.data.FieldType;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.MessageSender;
import net.fexcraft.mod.uni.world.WrapperHolder;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_TYPE;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocCreator {

    public static ConcurrentHashMap<UUID, StackWrapper> CACHE = new ConcurrentHashMap<>();
    public static StackWrapper REFERENCE;

    public static void start(MessageSender sender, String pid, String doc_id){
        UUID uuid = parse(pid);
        if(uuid == null){
            sender.send("player not found / invalid uuid");
            return;
        }
        Document doc = DocRegistry.getDocument(doc_id);
        if(doc == null){
            sender.send("document type not found");
            return;
        }
        if(!hasPerm(sender, doc)) return;
        StackWrapper stack = REFERENCE.copy();
        stack.createTagIfMissing();
        stack.getTag().set(NBTKEY_TYPE, doc.id.colon());
        DocStackApp app = stack.appended.get(DocStackApp.class);
        app.setValue("uuid", uuid.toString());
        CACHE.put(uuid, stack);
        sender.send("document creation for '"+ uuid +"' started");
    }

    public static void set(MessageSender sender, String pid, String key, String val){
        UUID uuid = parse(pid);
        if(uuid == null){
            sender.send("player not found / invalid uuid");
            return;
        }
        StackWrapper stack = CACHE.get(uuid);
        if(stack == null){
            sender.send("no document in creation cache for " + uuid + "");
            return;
        }
        DocStackApp app = stack.appended.get(DocStackApp.class);
        Document doc = app.getDocument();
        if(!hasPerm(sender, doc)) return;
        if(!doc.fields.containsKey(key)){
            sender.send("field key '"+ key + "' not found in document '" + doc.id + "' for" + uuid + "");
            return;
        }
        val = DocCreator.validate(str -> sender.send(str), doc.fields.get(key), val);
        if(val == null) return;
        app.setValue(key, val);
        sender.send("field '" + key+ "' value set to " + val);
    }

    public static void issue(MessageSender sender, String pid){
        UUID uuid = parse(pid);
        if(uuid == null){
            sender.send("player not found / invalid uuid");
            return;
        }
        StackWrapper stack = CACHE.get(uuid);
        if(stack == null){
            sender.send("no document in creation cache for " + uuid + "");
            return;
        }
        DocStackApp app = stack.appended.get(DocStackApp.class);
        Document doc = app.getDocument();
        if(!hasPerm(sender, doc)) return;
        Object[] inc = DocCreator.getIncomplete(app);
        if((int)inc[0] > 0){
            sender.send(inc[0] + " fields left to fill out, ex. " + inc[1]);
            return;
        }
        if(sender instanceof EntityW) app.issueBy(sender.asEntity(), false);
        else issueDoc(app, uuid);
        EntityW player = WrapperHolder.getPlayer(uuid);
        if(player == null){
            sender.send("receiving player not found, are they online?");
            return;
        }
        player.addStack(stack);
        sender.send("document signed and delivered");
    }

    private static UUID parse(String string){
        UUID uuid = WrapperHolder.getUUIDFor(string);
        if(uuid == null){
            try{
                return UUID.fromString(string);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return uuid;
    }

    private static boolean hasPerm(MessageSender sender, Document doc){
        if(sender instanceof EntityW){
            if(!DocPerms.hasPerm(sender.asEntity(), "document.issue", doc.id.colon())){
                sender.send("no permission");
                return false;
            }
        }
        return true;
    }

    public static String validate(Consumer<String> errlog, FieldData data, String val){
        if(data.type.number()){
            try{
                if(data.type == FieldType.INTEGER){
                    if(val.contains(".")) val = val.substring(0, val.indexOf("."));
                    Integer.parseInt(val);
                }
                else{
                    Float.parseFloat(val);
                }
            }
            catch(Exception e){
                e.printStackTrace();
                errlog.accept("Error: " + e.getMessage());
                return null;
            }
        }
        else if(data.type == FieldType.DATE){
            try{
                val = (LocalDate.parse(val).toEpochDay() * 86400000) + "";
            }
            catch(Exception e){
                e.printStackTrace();
                errlog.accept("Error: " + e.getMessage());
                return null;
            }
        }
        else if(data.type == FieldType.UUID){
            try{
                UUID uuid = WrapperHolder.getUUIDFor(val);
                if(uuid == null) val = UUID.fromString(val).toString();
                else val = uuid.toString();
            }
            catch(Exception e){
                e.printStackTrace();
                errlog.accept("Error: " + e.getMessage());
                return null;
            }
        }
        return val;
    }

    public static Object[] getIncomplete(DocStackApp doc){
        int incomplete = 0;
        String eg = null;
        Document document = doc.getDocument();
        for(String str : document.fields.keySet()){
            FieldData data = document.fields.get(str);
            if(!data.type.editable) continue;
            if(data.value == null && doc.getValue(str) == null && !data.can_empty){
                incomplete++;
                if(eg == null) eg = str;
            }
        }
        return new Object[]{ incomplete, eg };
    }

    private static void issueDoc(DocStackApp app, UUID uuid){
        if(app.getValue("issuer") == null) app.setValue("issuer", DocConfig.DEF_ISSUER_UUID);
        if(app.getValue("issued") == null) app.setValue("issued", Time.getDate() + "");
        if(app.getValue("issuer_name") == null) app.setValue("issuer_name", DocConfig.DEF_ISSUER_NAME);
        if(app.getValue("issuer_type") == null) app.setValue("issuer_type", DocConfig.DEF_ISSUER_TYPE);
        if(app.getValue("player_name") == null) app.setValue("player_name", WrapperHolder.getNameFor(uuid));
    }

}
