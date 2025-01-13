package net.fexcraft.mod.doc.ui;

import net.fexcraft.mod.doc.Documents;
import net.fexcraft.mod.uni.UniReg;
import net.fexcraft.mod.uni.ui.UIKey;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocUI {

    public static UIKey EDITOR = new UIKey(0, "documents:editor");
    public static UIKey VIEWER = new UIKey(0, "documents:viewer");

    public static void register(Documents mod){
        UniReg.registerMod("documents", mod);
        UniReg.registerUI(EDITOR, DocEditorUI.class);
        UniReg.registerMenu(EDITOR, "documents:uis/editor", DocEditorCon.class);
    }

}
