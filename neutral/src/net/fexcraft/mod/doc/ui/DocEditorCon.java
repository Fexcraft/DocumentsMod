package net.fexcraft.mod.doc.ui;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.doc.DocPerms;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.data.FieldData;
import net.fexcraft.mod.doc.data.FieldType;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.ui.ContainerInterface;
import net.fexcraft.mod.uni.world.WrapperHolder;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocEditorCon extends ContainerInterface {

    protected StackWrapper stack;
    protected DocStackApp doc;

    public DocEditorCon(JsonMap map, UniEntity ply, V3I pos){
        super(map, ply, pos);
        stack = player.entity.getHeldItem(true);
        doc = stack.appended.get(DocStackApp.class);
        if(doc == null || doc.getDocument() == null){
            player.entity.send("error.no_doc_data");
            player.entity.closeUI();
        }
    }

    @Override
    public void packet(TagCW com, boolean client){
        switch(com.getString("cargo")){
            case "tag":{
                stack.setTag(com.getCompound("tag"));
                break;
            }
            case "msg":{
                ui.texts.get("info").value(com.getString("msg"));
                ui.texts.get("info").translate();
                break;
            }
            case "exit":{
                player.entity.closeUI();
                break;
            }
            case "change":{
                if(!client && !DocPerms.hasPerm(player.entity, "document.edit", doc.getDocument().id.colon())){
                    player.entity.send("ui.documents.editor.no_permission");
                    player.entity.closeUI();
                    return;
                }
                String key = com.getString("key");
                FieldData data = doc.getDocument().fields.get(key);
                String val = com.getString("val");
                if(!data.type.editable) return;
                if(!client){
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
                            sendMsg("Error: " + e.getMessage());
                            return;
                        }
                    }
                    else if(data.type == FieldType.DATE){
                        try{
                            val = (LocalDate.parse(val).toEpochDay() * 86400000) + "";
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            sendMsg("Error: " + e.getMessage());
                            return;
                        }
                    }
                    else if(data.type == FieldType.UUID){
                        try{
                            if(WrapperHolder.getUUIDFor(val) == null) UUID.fromString(val);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            sendMsg("Error: " + e.getMessage());
                            return;
                        }
                    }
                    doc.setValue(key, val);
                    com.set("val", val);
                    SEND_TO_CLIENT.accept(com, player);
                }
                else{
                    doc.setValue(key, val);
                    ((DocEditorUI)ui).update();
                }
                break;
            }
            case "issue":{
                if(!client && !DocPerms.hasPerm(player.entity, "document.issue", doc.getDocument().id.colon())){
                    player.entity.send("ui.documents.editor.no_permission");
                    player.entity.closeUI();
                    return;
                }
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
                if(incomplete > 0){
                    player.entity.send("ui.documents.editor.status.left", incomplete, eg);
                    player.entity.closeUI();
                    break;
                }
                if(!client){
                    doc.issueBy(player.entity, client);
                    player.entity.send("ui.documents.editor.signed");
                    player.entity.closeUI();
                }
                break;
            }
        }
    }

    private void sendMsg(String str){
        TagCW com = TagCW.create();
        com.set("cargo", "msg");
        com.set("msg", str);
        SEND_TO_CLIENT.accept(com, player);
    }

}
