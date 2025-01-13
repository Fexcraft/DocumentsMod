package net.fexcraft.mod.doc;

import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.MessageSender;
import net.fexcraft.mod.uni.world.WrapperHolder;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
        //TODO
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
        //TODO
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

}
