package net.fexcraft.mod.doc.ui;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.utils.Formatter;
import net.fexcraft.lib.mc.gui.GenericGui;
import net.fexcraft.mod.doc.Documents;
import net.fexcraft.mod.doc.data.DocPage;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.data.FieldData;
import net.fexcraft.mod.doc.data.FieldType;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.IDLManager;
import net.fexcraft.mod.uni.tag.TagCW;
import net.fexcraft.mod.uni.ui.*;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.mod.uni.ui.ContainerInterface.SEND_TO_SERVER;
import static net.fexcraft.mod.uni.ui.ContainerInterface.TRANSLATOR;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocViewerUI extends UserInterface {

    private ArrayList<IDL> images = new ArrayList<>();
    private ArrayList<int[]> imgpos = new ArrayList<>();
    private ArrayList<String> pages;
    private DocViewerCon con;
    private Document doc;
    private DocPage page;

    public DocViewerUI(JsonMap map, ContainerInterface container) throws Exception {
        super(map, container);
        con = (DocViewerCon)container;
        doc = con.doc.getDocument();
        pages = new ArrayList<>(doc.pages.keySet());
        page = doc.pages.get(pages.get(con.pos.x));
        UITab main = tabs.get("main");
        main.width = width = doc.sizex;
        main.height = height = doc.sizey;
        main.texture = doc.textures.get(page.texture);
        for(DocPage.DocPageField df : page.fields){
            FieldData field = doc.fields.get(df.id);
            int x = df.x > -1 ? df.x : field.posx;
            int y = df.y > -1 ? df.y : field.posy;
            int sx = df.sx > -1 ? df.sx : field.sizex;
            int sy = df.sy > -1 ? df.sy : field.sizey;
            if(field.type.image()){
                IDL imgloc = null;
                if(field.type == FieldType.PLAYER_IMG){
                    imgloc = Documents.getTexture(field.getValue(con.doc));
                }
                else if(field.value.startsWith("external;")){
                    imgloc = Documents.getTexture(field.value.substring(9));
                }
                else if(field.value.startsWith("http")){
                    imgloc = Documents.getTexture(field.value);
                }
                else imgloc = IDLManager.getIDLCached(field.value);
                images.add(imgloc);
                imgpos.add(new int[]{ x, y, sx, sy });
            }
            else{
                String val;
                if(field.type == FieldType.ISSUER){
                    val = con.doc.getValue("issuer");
                }
                else if(field.type == FieldType.ISSUER_NAME){
                    val = con.doc.getValue("issuer_name");
                }
                else val = field.getValue(con.doc);
                String prefix = field.prefix == null ? "" : field.prefix;
                JsonMap tmap = new JsonMap();
                tmap.add("pos", new JsonArray(x, y));
                tmap.add("size", new JsonArray(sx, sy));
                UIText text = UIElement.create(UIText.IMPLEMENTATION, this, tmap);
                if(field.fontscale > 0) text.scale = field.fontscale;
                if(field.autoscale) text.scale = -1;
                text.color.packed = field.color == null ? 0x636363 : field.color;
                text.value(Formatter.format(prefix + TRANSLATOR.apply(val)));
                texts.put("field_" + df.id, text);
                main.texts.put("field_" + df.id, text);
            }
        }
    }

    @Override
    public void init(){
        doc = con.doc.getDocument();
        UIButton prev = buttons.get("viewer_prev");
        prev.visible(pages.size() > 0 && con.pos.x > 0);
        prev.x = -30;
        prev.y = doc.sizey / 2 - 11;
        UIButton next = buttons.get("viewer_next");
        next.visible(pages.size() > 1 && con.pos.x < doc.pages.size() - 1);
        next.x = doc.sizex + 8;
        next.y = doc.sizey / 2 - 11;
    }

    @Override
    public void drawbackground(float ticks, int mx, int my){
        for(int i = 0; i < images.size(); i++){
            drawer.bind(images.get(i));
            int[] imgloc = imgpos.get(i);
            drawer.drawFull(gLeft + imgloc[0], gTop + imgloc[1], imgloc[2], imgloc[3]);
        }
    }

    @Override
    public boolean onAction(UIButton button, String id, int x, int y, int b){
        switch(id){
            case "viewer_prev":{
                if(con.pos.x - 1 >= 0){
                    TagCW com = TagCW.create();
                    com.set("page", con.pos.x - 1);
                    SEND_TO_SERVER.accept(com);
                }
                return true;
            }
            case "viewer_next":{
                if(con.pos.x + 1 < doc.pages.size()){
                    TagCW com = TagCW.create();
                    com.set("page", con.pos.x + 1);
                    SEND_TO_SERVER.accept(com);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void getTooltip(int mx, int my, List<String> list){
        for(UIText text : texts.values()){
            if(text.hovered() && text.value().length() > 0) list.add(text.value());
        }
    }

}
