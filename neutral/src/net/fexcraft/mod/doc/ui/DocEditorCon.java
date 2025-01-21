package net.fexcraft.mod.doc.ui;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.doc.DocCreator;
import net.fexcraft.mod.doc.DocPerms;
import net.fexcraft.mod.doc.DocRegistry;
import net.fexcraft.mod.doc.data.DocPlayerData;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.data.FieldData;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.item.UniStack;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.ui.ContainerInterface;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocEditorCon extends ContainerInterface {

    protected UniStack unistk;
    protected DocStackApp app;
    protected Document doc;
    protected boolean noadm;

    public DocEditorCon(JsonMap map, UniEntity ply, V3I pos){
        super(map, ply, pos);
        noadm = pos.y > 0;
        if(noadm){
            doc = DocRegistry.getDocumentByIndex(pos.x);
            if(doc == null){
                player.entity.send("404: doc not found");
                player.entity.closeUI();
                return;
            }
            if(!doc.autoissue){
                player.entity.send("403: doc not for auto-issue");
                player.entity.closeUI();
                return;
            }
            if(!ply.entity.isOnClient()){
                DocPlayerData dpd = DocRegistry.PLAYERS.get(player.entity.getUUID());
                if(dpd == null){
                    player.entity.send("404: player data not found");
                    player.entity.closeUI();
                    return;
                }
                if(dpd.hasReceived(doc.id.colon())){
                    player.entity.send("403: doc already issued");
                    player.entity.closeUI();
                    return;
                }
            }
            unistk = UniStack.get(DocCreator.createNewStack(doc, player.entity.getUUID()));
        }
        else{
            unistk = UniStack.get(player.entity.getHeldItem(true));
        }
        app = unistk.appended.get(DocStackApp.class);
        doc = app.getDocument();
        if(app == null || app.getDocument() == null){
            player.entity.send("error.no_doc_data");
            player.entity.closeUI();
        }
    }

    @Override
    public void packet(TagCW com, boolean client){
        switch(com.getString("cargo")){
            case "tag":{
                unistk.stack.setTag(com.getCompound("tag"));
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
                if(!client && !noadm && !DocPerms.hasPerm(player.entity, "document.edit", app.getDocument().id.colon())){
                    player.entity.send("ui.documents.editor.no_permission");
                    player.entity.closeUI();
                    return;
                }
                String key = com.getString("key");
                FieldData data = app.getDocument().fields.get(key);
                String val = com.getString("val");
                if(!data.type.editable) return;
                if(!client){
                    val = DocCreator.validate(str -> sendMsg(str), data, val);
                    if(val == null) return;
                    app.setValue(key, val);
                    com.set("val", val);
                    SEND_TO_CLIENT.accept(com, player);
                }
                else{
                    app.setValue(key, val);
                    ((DocEditorUI)ui).update();
                }
                break;
            }
            case "issue":{
                if(!client && !noadm && !DocPerms.hasPerm(player.entity, "document.issue", app.getDocument().id.colon())){
                    player.entity.send("ui.documents.editor.no_permission");
                    player.entity.closeUI();
                    return;
                }
                Object[] inc = DocCreator.getIncomplete(app);
                if((int)inc[0] > 0){
                    player.entity.send("ui.documents.editor.status.left", inc[0], inc[1]);
                    player.entity.closeUI();
                    break;
                }
                if(!client){
                    if(noadm){
                        DocCreator.issueDoc(app, player.entity.getUUID(), player.entity);
                        player.entity.addStack(unistk.stack);
                    }
                    else{
                        app.issueBy(player.entity, client);
                    }
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
