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
public class DocViewerCon extends ContainerInterface {

    protected StackWrapper stack;
    protected DocStackApp doc;

    public DocViewerCon(JsonMap map, UniEntity ply, V3I pos){
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
        if(com.has("page")){
            player.entity.openUI(DocUI.VIEWER, com.getInteger("page"), 0, 0);
        }
    }

}
