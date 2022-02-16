package net.fexcraft.mod.doc.gui;

import java.util.ArrayList;

import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.mc.gui.GenericGui;
import net.fexcraft.lib.mc.render.ExternalTextureHelper;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.lib.tmt.ModelBase;
import net.fexcraft.mod.doc.data.FieldData;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class DocEditor extends GenericGui<DocEditorContainer> {
	
	public static final ResourceLocation TEXTURE = new ResourceLocation("documents:textures/gui/editor.png");
	private BasicButton[] fieldbuttons = new BasicButton[9];
	private BasicButton[] concanbuttons = new BasicButton[3];
	private BasicText[] infotext = new BasicText[4];
	private BasicText valueinfo, status;
	private TextField field, nfield;
	private String[] fieldkeys;
	private int scroll, selected = -1;
	private FieldData data;
	//
	private static ResourceLocation tempimg;
	private static ArrayList<String> tooltip = new ArrayList<>();

	public DocEditor(EntityPlayer player){
		super(TEXTURE, new DocEditorContainer(player), player);
		xSize = 256;
		ySize = 104;
		if(container.cap == null){
			Print.bar(player, "item.missing.cap");
			player.closeScreen();
			mc.currentScreen = null;
		}
		if(container.cap.getDocument() == null){
			Print.bar(player, "item.missing.doc");
			player.closeScreen();
			mc.currentScreen = null;
		}
	}
	
	@Override
	public void init(){
		fieldkeys = container.doc.fields.keySet().toArray(new String[0]);
		for(int i = 0; i < fieldbuttons.length; i++){
			int I = i;
			this.buttons.put("f" + i, fieldbuttons[i] = new BasicButton("f" + i, guiLeft + 17, guiTop + 8 + i * 10, 17, 8 + i * 10, 48, 8, true){
				public boolean onclick(int mx, int my, int mb){
					if(I + scroll >= fieldbuttons.length) return true;
					data = container.doc.fields.get(fieldkeys[selected = I + scroll]);
					if(data.type.image()){
						if(data.value.startsWith("external;")){
							tempimg = ExternalTextureHelper.get(data.value.substring(9));
						}
						else tempimg = new ResourceLocation(data.value);
					}
					field.setVisible(false);
					nfield.setVisible(false);
					if(data.type.number()){
						nfield.setText(data.value == null ? "0" : data.value);
						nfield.setVisible(true);
					}
					else if(data.type.editable){
						field.setText(data.value == null ? "" : data.value);
						field.setVisible(true);
					}
					return true;
				}
			});
			this.texts.put("f" + i, new BasicText(guiLeft + 18, guiTop + 8 + i * 10, 46, null, "...").autoscale());
		}
		this.buttons.put("up", new BasicButton("up", guiLeft + 7, guiTop + 7, 7, 7, 7, 7, true){
			public boolean onclick(int mx, int my, int mb){
				if(scroll > 0) scroll--;
				return true;
			}
		});
		this.buttons.put("dw", new BasicButton("dw", guiLeft + 7, guiTop + 90, 7, 90, 7, 7, true){
			public boolean onclick(int mx, int my, int mb){
				if(scroll < fieldkeys.length - 1) scroll++;
				return true;
			}
		});
		for(int i = 0; i < infotext.length; i++){
			this.texts.put("i" + i, infotext[i] = new BasicText(guiLeft + 71, guiTop + 10 + i * 12, 125, null, "...", true, RGB.WHITE.packed).autoscale());
		}
		this.texts.put("info", valueinfo = new BasicText(guiLeft + 71, guiTop + 60, 175, null, "...", true, RGB.WHITE.packed).autoscale());
		this.texts.put("status", status = new BasicText(guiLeft + 69, guiTop + 87, 153, null, "...", true, RGB.BLACK.packed).autoscale());
		fields.put("field", field = new TextField(0, fontRenderer, guiLeft + 70, guiTop + 71, 166, 10));
		fields.put("nfield", nfield = new NumberField(0, fontRenderer, guiLeft + 70, guiTop + 71, 166, 10));
		field.setVisible(false);
		nfield.setVisible(false);
		buttons.put("confirm_value", concanbuttons[0] = new BasicButton("confirm_value", guiLeft + 237, guiTop + 71, 237, 71, 10, 10, true){
			public boolean onclick(int mx, int my, int mb){
				//
				return true;
			}
		});
		buttons.put("cancel", concanbuttons[1] = new BasicButton("cancel", guiLeft + 224, guiTop + 85, 224, 85, 12, 12, true){
			public boolean onclick(int mx, int my, int mb){
				player.closeScreen();
				mc.currentScreen = null;
				return true;
			}
		});
		buttons.put("confirm", concanbuttons[2] = new BasicButton("confirm", guiLeft + 237, guiTop + 85, 237, 85, 10, 10, true){
			public boolean onclick(int mx, int my, int mb){
				//
				return true;
			}
		});
	}
	
	@Override
	public void predraw(float ticks, int mx, int my){
		for(int i = 0; i < fieldbuttons.length; i++){
			int I = i + scroll;
			if(I >= container.doc.fields.size()){
				fieldbuttons[i].enabled = false;
				texts.get("f" + i).string = "";
			}
			else{
				fieldbuttons[i].enabled = true;
				texts.get("f" + i).string = container.doc.fields.get(fieldkeys[I]).name;
			}
		}
		boolean ex = selected > -1 && data != null;
		for(int i = 0; i < infotext.length; i++){
			if(ex){
				infotext[i].string = i >= data.description.size() ? "" : I18n.format(data.description.get(i));
			}
			else infotext[i].string = "";
		}
		valueinfo.string = ex ? data.value : "";//TODO type based info
		status.string = "//TODO status";
	}
	
	@Override
	public void drawbackground(float ticks, int mx, int my){
		boolean ex = selected > -1 && data != null;
		if(ex && data.type.image()){
			ModelBase.bindTexture(tempimg);
			drawScaledCustomSizeModalRect(guiLeft + 199, guiTop + 9, 0, 0, 1, 1, 48, 48, 1, 1);
			ModelBase.bindTexture(texloc);
		}
	}
	
	@Override
	public void drawlast(float ticks, int mx, int my){
		tooltip.clear();
		for(int i = 0; i < fieldbuttons.length; i++){
			if(fieldbuttons[i].hovered(mx, my)) tooltip.add(texts.get("f" + i).string);
		}
		for(int i = 0; i < infotext.length; i++){
			if(infotext[i].hovered(mx, my)) tooltip.add(infotext[i].string);
		}
		if(valueinfo.hovered(mx, my)) tooltip.add(valueinfo.string);
		if(status.hovered(mx, my)) tooltip.add(status.string);
		if(concanbuttons[0].hovered(mx, my)) tooltip.add(I18n.format("documents.editor.confirm_value"));
		if(concanbuttons[1].hovered(mx, my)) tooltip.add(I18n.format("documents.editor.cancel"));
		if(concanbuttons[2].hovered(mx, my)) tooltip.add(I18n.format("documents.editor.confirm"));
		if(tooltip.size() > 0) this.drawHoveringText(tooltip, mx, my);
	}

}
