package net.fexcraft.mod.doc.gui;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.fexcraft.lib.common.utils.Formatter;
import net.fexcraft.lib.mc.gui.GenericGui;
import net.fexcraft.lib.mc.render.ExternalTextureHelper;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.mod.doc.DocRegistry;
import net.fexcraft.mod.doc.data.DocPage;
import net.fexcraft.mod.doc.data.DocPage.DocPageField;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.data.FieldData;
import net.fexcraft.mod.doc.data.FieldType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

public class DocView extends GenericGui<DocEditorContainer> {
	
	public Document doc;
	public DocPage page;
	public String pageid;
	public int pageidx;
	public ArrayList<ResourceLocation> images = new ArrayList<>();
	public ArrayList<int[]> imgpos = new ArrayList<>();

	public DocView(EntityPlayer player, int pageidx){
		super(DocRegistry.STONE, new DocEditorContainer(player), player);
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
		doc = container.cap.getDocument();
		Entry<String, DocPage> entry = (Entry<String, DocPage>)doc.pages.entrySet().toArray()[this.pageidx = pageidx];
		page = entry.getValue();
		pageid = entry.getKey();
		xSize = page.sizex > 0 ? page.sizex : doc.sizex;
		ySize = page.sizey > 0 ? page.sizey : doc.sizey;
		texloc = doc.textures.get(page.texture);
	}
	
	@Override
	public void init(){
		for(DocPageField df : page.fields){
			FieldData field = doc.fields.get(df.id);
			int x = df.x > -1 ? df.x : field.posx;
			int y = df.y > -1 ? df.y : field.posy;
			int sx = df.sx > -1 ? df.sx : field.sizex;
			int sy = df.sy > -1 ? df.sy : field.sizey;
			if(field.type.image()){
				ResourceLocation imgloc = null;
				if(field.type == FieldType.PLAYER_IMG){
					imgloc = ExternalTextureHelper.get(field.getValue(container.cap));
				}
				else if(field.value.startsWith("external;")){
					imgloc = ExternalTextureHelper.get(field.value.substring(9));
				}
				else imgloc = new ResourceLocation(field.value);
				images.add(imgloc);
				imgpos.add(new int[]{ x, y, sx, sy });
			}
			else{
				String val = null;
				if(field.type == FieldType.ISSUER){
					val = container.cap.getValue("issuer");
				}
				else if(field.type == FieldType.ISSUER_NAME){
					val = container.cap.getValue("issuer_name");
				}
				else val = field.getValue(container.cap);
				String format = field.format == null ? "" : field.format;
				BasicText text = new BasicText(guiLeft + x, guiTop + y, sx, field.color, Formatter.format(format + I18n.format(val)));
				if(field.fontscale > 0) text.scale(field.fontscale);
				if(field.autoscale) text.autoscale();
				texts.put("field-" + df.id, text);
			}
		}
		if(pageidx > 0){
			buttons.put("prev", new PageArrow("prev", guiLeft - 30, guiTop + 8, 0, 234, 22, 22){
				public boolean onclick(int mx, int my, int mb){
					NBTTagCompound compound = new NBTTagCompound();
					compound.setInteger("open_page", pageidx - 1);
					container.send(Side.SERVER, compound);
					return true;
				}
			});
		}
		if(pageidx < doc.pages.size() - 1){
			buttons.put("next", new PageArrow("next", guiLeft + xSize + 8, guiTop + 8, 24, 234, 22, 22){
				public boolean onclick(int mx, int my, int mb){
					NBTTagCompound compound = new NBTTagCompound();
					compound.setInteger("open_page", pageidx + 1);
					container.send(Side.SERVER, compound);
					return true;
				}
			});
		}
	}
	
	@Override
	public void drawbackground(float ticks, int mx, int my){
		for(int i = 0; i < images.size(); i++){
			mc.renderEngine.bindTexture(images.get(i));
			int[] imgloc = imgpos.get(i);
			drawScaledCustomSizeModalRect(guiLeft + imgloc[0], guiTop + imgloc[1], 0, 0, 1, 1, imgloc[2], imgloc[3], 1, 1);
			mc.renderEngine.bindTexture(texloc);
		}
	}
	
	public static class PageArrow extends BasicButton {

		public PageArrow(String name, int x, int y, int tx, int ty, int sizex, int sizey){
			super(name, x, y, tx, ty, sizex, sizey, true);
		}

		public void draw(GenericGui<?> gui, float pticks, int mouseX, int mouseY){
			if(!visible) return;
			gui.mc.renderEngine.bindTexture(DocEditor.TEXTURE);
			super.draw(gui, pticks, mouseX, mouseY);
			gui.mc.renderEngine.bindTexture(gui.getTexLoc());
		}
		
	}

}
