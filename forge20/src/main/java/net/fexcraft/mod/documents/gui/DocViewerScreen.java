package net.fexcraft.mod.documents.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fexcraft.mod.documents.Documents;
import net.fexcraft.mod.documents.ExternalTextures;
import net.fexcraft.mod.documents.data.DocPage;
import net.fexcraft.mod.documents.data.DocPage.DocPageField;
import net.fexcraft.mod.documents.data.Document;
import net.fexcraft.mod.documents.data.FieldData;
import net.fexcraft.mod.documents.data.FieldType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Map.Entry;

import static net.fexcraft.mod.documents.Documents.send;

public class DocViewerScreen extends AbstractContainerScreen<DocViewerContainer> {

    private static DocViewerScreen SCREEN;
    //
    public Document doc;
    public DocPage page;
    public String pageid;
    public int pageidx;
    public ArrayList<ResourceLocation> images = new ArrayList<>();
    public ArrayList<int[]> imgpos = new ArrayList<>();
    public static ResourceLocation texloc;

    public DocViewerScreen(DocViewerContainer container, Inventory inventory, Component component){
        super(container, inventory, component);
        if(container.doc == null){
            inventory.player.sendSystemMessage(Component.translatable("item.missing.doc"));
            inventory.player.closeContainer();
        }
        doc = container.doc;
        Entry<String, DocPage> entry = (Entry<String, DocPage>)doc.pages.entrySet().toArray()[this.pageidx = container.page];
        page = entry.getValue();
        pageid = entry.getKey();
        imageWidth = page.sizex > 0 ? page.sizex : doc.sizex;
        imageHeight = page.sizey > 0 ? page.sizey : doc.sizey;
        texloc = doc.textures.get(page.texture);
        SCREEN = this;
    }

    @Override
    protected void init(){
        super.init();
        menu.screen = this;
        for(DocPageField df : page.fields){
            FieldData field = doc.fields.get(df.id);
            int x = df.x > -1 ? df.x : field.posx;
            int y = df.y > -1 ? df.y : field.posy;
            int sx = df.sx > -1 ? df.sx : field.sizex;
            int sy = df.sy > -1 ? df.sy : field.sizey;
            if(field.type.image()){
                ResourceLocation imgloc = null;
                String val = field.getValue(menu.stack);
                if(field.type == FieldType.PLAYER_IMG){
                    imgloc = ExternalTextures.get(val);
                }
                else if(val.startsWith("external;")){
                    imgloc = ExternalTextures.get(val.substring(9));
                }
                else if(val.startsWith("http")){
                    imgloc = ExternalTextures.get(val);
                }
                else imgloc = new ResourceLocation(field.value);
                images.add(imgloc);
                imgpos.add(new int[]{ x, y, sx, sy });
            }
            else{
                String val = null;
                if(field.type == FieldType.ISSUER){
                    val = menu.getValue("issuer");
                }
                else if(field.type == FieldType.ISSUER_NAME){
                    val = menu.getValue("issuer_name");
                }
                else val = field.getValue(menu.stack);
                String format = field.format == null ? "" : field.format;
                DocEditorScreen.GenericText text = new DocEditorScreen.GenericText(leftPos + x, topPos + y, sx, Component.literal(format.replace("&", "\u00A7") + I18n.get(val)));
                if(field.color != null) text.color(field.color);
                if(field.fontscale > 0) text.scale(field.fontscale);
                if(field.autoscale) text.autoscale();
                addWidget(text);
            }
        }
        if(pageidx > 0){
            addWidget(new PageArrow(leftPos - 30, topPos + 8, 0, 234, 22, 22){
                @Override
                public void onPress(){
                    CompoundTag compound = new CompoundTag();
                    compound.putInt("open_page", pageidx - 1);
                    send(false, compound, menu.player);
                }
            });
        }
        if(pageidx < doc.pages.size() - 1){
            addWidget(new PageArrow(leftPos + imageWidth + 8, topPos + 8, 24, 234, 22, 22){
                @Override
                public void onPress(){
                    CompoundTag compound = new CompoundTag();
                    compound.putInt("open_page", pageidx + 1);
                    send(false, compound, menu.player);
                }
            });
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers){
        if(pKeyCode == 256){
            minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton){
        for(GuiEventListener button : children()) if(button.mouseClicked(pMouseX, pMouseY, pButton)) return true;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void renderBg(GuiGraphics matrix, float ticks, int x, int y){
        matrix.setColor(1, 1, 1, 1);
        //minecraft.textureManager.bindForSetup(texloc);
        matrix.blit(texloc, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        //RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        for(GuiEventListener w : children()) if(w instanceof AbstractWidget) ((AbstractWidget)w).render(matrix, x, y, ticks);
        //
        RenderSystem.enableBlend();
        //RenderSystem.enableAlphaTest();
        for(int i = 0; i < images.size(); i++){
            minecraft.textureManager.bindForSetup(images.get(i));
            int[] imgloc = imgpos.get(i);
            DocEditorScreen.draw(matrix, images.get(i), leftPos + imgloc[0], topPos + imgloc[1], imgloc[2], imgloc[3]);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics stack, int x, int y){
        //
    }

    @Override
    public void containerTick(){
        //
    }

    public static abstract class PageArrow extends DocEditorScreen.GenericButton {

        public PageArrow(int x, int y, int tx, int ty, int sizex, int sizey){
            super(x, y, tx, ty, sizex, sizey);
        }

        public void renderWidget(GuiGraphics stack, int mx, int my, float ticks){
            //Minecraft.getInstance().textureManager.bind(DocEditorScreen.TEXTURE);
            super.renderWidget(stack, mx, my, ticks);
            //Minecraft.getInstance().textureManager.bind(texloc);
        }

    }

}
