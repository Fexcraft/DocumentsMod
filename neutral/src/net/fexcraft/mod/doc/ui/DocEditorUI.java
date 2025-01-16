package net.fexcraft.mod.doc.ui;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.doc.Documents;
import net.fexcraft.mod.doc.data.FieldData;
import net.fexcraft.mod.doc.data.FieldType;
import net.fexcraft.mod.doc.packet.DocPacketHandler;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.ui.ContainerInterface;
import net.fexcraft.mod.uni.ui.UIButton;
import net.fexcraft.mod.uni.ui.UserInterface;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.mod.uni.ui.ContainerInterface.SEND_TO_SERVER;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocEditorUI extends UserInterface {

    private ArrayList<String> keys = new ArrayList<>();
    private DocEditorCon con;
    private FieldData field;
    private int incomplete;
    private int index = 0;
    private IDL image;

    public DocEditorUI(JsonMap map, ContainerInterface container) throws Exception {
        super(map, container);
        con = (DocEditorCon)container;
    }

    @Override
    public void init(){
        keys.clear();
        for(FieldData val : con.doc.fields.values()){
            if(val.type.editable) keys.add(val.key);
        }
        update();
    }

    protected void update(){
        field = con.doc.fields.get(keys.get(index));
        for(int i = 0; i < 4; i++){
            if(i >= field.description.size()){
                texts.get("desc" + i).value("");
            }
            else{
                texts.get("desc" + i).value(field.description.get(i));
                texts.get("desc" + i).translate();
            }
        }
        String str = con.app.getValue(field.key);
        fields.get("field").text(str == null ? "" : str);
        texts.get("info").value("ui.documents.editor.info");
        texts.get("info").translate(field.key, field.type);
        updateStatus();
        if(field.type.image() && !(field.type == FieldType.PLAYER_IMG && !con.app.hasValue("uuid"))){
            String img = field.getValue(con.app);
            if(img.startsWith("http")){
                image = Documents.getTexture(img);
            }
            else if(img.startsWith("server:")){
                image = DocPacketHandler.INSTANCE.requestServerTexture(img);
            }
            else image = IDLManager.getIDLCached(img);
        }
        else image = null;
    }

    private void updateStatus(){
        incomplete = 0;
        String eg = null;
        for(String str : con.doc.fields.keySet()){
            FieldData data = con.doc.fields.get(str);
            if(!data.type.editable) continue;
            if(data.value == null && con.app.getValue(str) == null && !data.can_empty){
                incomplete++;
                if(eg == null) eg = str;
            }
        }
        if(incomplete > 0){
            texts.get("status").value("ui.documents.editor.status.left");
            texts.get("status").translate(incomplete, eg);
        }
        else{
            texts.get("status").value("ui.documents.editor.status.ok");
            texts.get("status").translate();
        }
    }

    @Override
    public void drawbackground(float ticks, int mx, int my){
        if(image != null){
            drawer.bind(image);
            drawer.drawFull(gLeft + 199, gTop + 9, 48, 48);
        }
    }

    @Override
    public boolean onAction(UIButton button, String id, int x, int y, int b){
        switch(id){
            case "prev":{
                index--;
                if(index < 0) index = keys.size() - 1;
                update();
                return true;
            }
            case "next":{
                index++;
                if(index >= keys.size()) index = 0;
                update();
                return true;
            }
            case "change":{
                TagCW com = TagCW.create();
                com.set("cargo", "change");
                com.set("key", field.key);
                com.set("val", fields.get("field").text());
                SEND_TO_SERVER.accept(com);
                return true;
            }
            case "cancel":{
                TagCW com = TagCW.create();
                com.set("cargo", "exit");
                SEND_TO_SERVER.accept(com);
                return true;
            }
            case "confirm":{
                TagCW com = TagCW.create();
                com.set("cargo", "issue");
                SEND_TO_SERVER.accept(com);
                return true;
            }
        }
        return false;
    }

    @Override
    public void getTooltip(int mx, int my, List<String> list){
        for(int i = 0; i < 4; i++){
            if(texts.get("desc" + i).hovered()) list.add(texts.get("desc" + i).value());
        }
        if(texts.get("info").hovered()) list.add(texts.get("info").value());
    }

}
