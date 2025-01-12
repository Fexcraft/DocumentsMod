package net.fexcraft.mod.documents.gui;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fexcraft.mod.documents.Documents;
import net.fexcraft.mod.documents.ExternalTextures;
import net.fexcraft.mod.documents.data.FieldData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Matrix4f;

import static net.fexcraft.mod.documents.Documents.send;

public class DocEditorScreen extends AbstractContainerScreen<DocEditorContainer> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("documents:textures/gui/editor.png");
    private GenericButton[] fieldbuttons = new GenericButton[9];
    private GenericButton[] concanbuttons = new GenericButton[3];
    private GenericText[] infotext = new GenericText[4];
    private GenericText valueinfo, status;
    private String[] fieldkeys;
    private int scroll;
    private int selected = -1;
    private FieldData data;
    private int todo;
    protected String statustext;
    protected EditBox field;
    //
    private static ResourceLocation tempimg;

    public DocEditorScreen(DocEditorContainer container, Inventory inventory, Component component){
        super(container, inventory, component);
        imageWidth = 256;
        imageHeight = 104;
        if(container.doc == null){
            inventory.player.sendSystemMessage(Component.translatable("item.missing.doc"));
            inventory.player.closeContainer();
        }
    }

    @Override
    protected void init(){
        super.init();
        menu.screen = this;
        Object[] entries = menu.doc.fields.entrySet().stream().filter(e -> e.getValue().type.editable).collect(Collectors.toList()).toArray();
        ArrayList<String> list = new ArrayList<>();
        for(Object obj : entries) list.add(((java.util.Map.Entry<String, FieldData>)obj).getKey());
        fieldkeys = list.toArray(new String[0]);
        for(int i = 0; i < fieldbuttons.length; i++){
            int I = i;
            fieldbuttons[i] = new GenericButton(leftPos + 17, topPos + 8 + i * 10, 17, 8 + i * 10, 48, 8, Component.literal("")){
                @Override
                public void onPress(){
                    if(I + scroll >= fieldkeys.length) return;
                    data = menu.doc.fields.get(fieldkeys[selected = I + scroll]);
                    if(data.type.image()){
                        String url = menu.stack.getTag().getString("document:" + data.name);
                        if(url == null || url.length() == 0) url = data.value;
                        if(url != null && url.length() > 0){
                            if(url.startsWith("external;") || url.startsWith("http")){
                                if(url.startsWith("external;")) url = url.substring(9);
                                tempimg = ExternalTextures.get(url);
                            }
                            else tempimg = new ResourceLocation(data.value);
                        }
                        else tempimg = null;
                        Documents.LOGGER.info(url + " " + tempimg);
                        field.setMaxLength(1024);
                    }
                    else field.setMaxLength(128);
                    field.setVisible(false);
                    if(data.type.number() || data.type.editable){
                        String val = data.getValue(menu.stack);
                        field.setValue(val == null ? "" : val);
                        field.setVisible(true);
                        field.setFocused(true);
                        field.setFilter(str -> {
                            if(data.type.number()){

                            }
                            else{

                            }
                            return true;
                        });
                    }
                    statustext = null;
                }
            }.text(true);
            addWidget(fieldbuttons[i]);
        }
        addWidget(new GenericButton(leftPos + 7, topPos + 7, 7, 7, 7, 7, Component.literal("up")){
            @Override
            public void onPress(){
                if(scroll > 0) scroll--;
            }
        });
        addWidget(new GenericButton(leftPos + 7, topPos + 90, 7, 90, 7, 7, Component.literal("down")){
            @Override
            public void onPress(){
                if(scroll < fieldkeys.length - 1) scroll++;
            }
        });
        for(int i = 0; i < infotext.length; i++){
            addWidget(infotext[i] = new GenericText(leftPos + 71, topPos + 10 + i * 12, 125, "").autoscale());
        }
        addWidget(valueinfo = new GenericText(leftPos + 71, topPos + 60, 175, "...").autoscale().color(0xffffff));
        addWidget(status = new GenericText(leftPos + 69, topPos + 87, 153, "...").autoscale().color(0x000000));
        addWidget(field = new EditBox(minecraft.font, leftPos + 70, topPos + 71, 166, 10, Component.translatable("...")));
        field.setVisible(false);
        addWidget(concanbuttons[0] = new GenericButton(leftPos + 237, topPos + 71, 237, 71, 10, 10, "confirm_value"){
            @Override
            public void onPress(){
                if(data == null || !data.type.editable) return;
                if(data.type.number()){
                    Float val = Float.parseFloat(field.getValue());
                    if(val == null){
                        statustext = "&cinvalid number input";
                    }
                    else{
                        CompoundTag compound = new CompoundTag();
                        compound.putString("field", fieldkeys[selected]);
                        compound.putString("value", val + "");
                        send(false, compound, menu.player);
                    }
                }
                else{
                    CompoundTag compound = new CompoundTag();
                    compound.putString("field", fieldkeys[selected]);
                    compound.putString("value", field.getValue());
                    send(false, compound, menu.player);
                }
            }
        });
        addWidget(concanbuttons[1] = new GenericButton(leftPos + 224, topPos + 85, 224, 85, 12, 12, "cancel"){
            @Override
            public void onPress(){
                menu.player.closeContainer();
                minecraft.setScreen(null);
            }
        });
        addWidget(concanbuttons[2] = new GenericButton(leftPos + 237, topPos + 85, 237, 85, 12, 12, "confirm"){
            @Override
            public void onPress(){
                if(todo > 0){
                    statustext = "documents.editor.status.incomplete";
                    return;
                }
                CompoundTag compound = new CompoundTag();
                compound.putBoolean("issue", true);
                send(false, compound, menu.player);
            }
        });
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers){
        if(pKeyCode == 256){
            this.minecraft.player.closeContainer();
            return true;
        }
        if(field.keyPressed(pKeyCode, pScanCode, pModifiers)) return true;
        if(field.isFocused()) return false;
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers){
        return field.isFocused() && field.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton){
        if(field.mouseClicked(pMouseX, pMouseY, pButton)) return true;
        for(GuiEventListener button : children()) if(button.mouseClicked(pMouseX, pMouseY, pButton)) return true;
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void renderBg(GuiGraphics matrix, float ticks, int x, int y){
        for(int i = 0; i < fieldbuttons.length; i++){
            int I = i + scroll;
            if(I >= fieldkeys.length){
                fieldbuttons[i].visible = false;
                fieldbuttons[i].setMessage(Component.literal(""));
            }
            else{
                fieldbuttons[i].visible = true;
                fieldbuttons[i].setMessage(Component.literal(menu.doc.fields.get(fieldkeys[I]).name));
            }
        }
        boolean ex = selected > -1 && data != null;
        for(int i = 0; i < infotext.length; i++){
            if(ex){
                infotext[i].setMessage(i >= data.description.size() ? Component.literal("") : Component.translatable(data.description.get(i)));
            }
            else infotext[i].setMessage(Component.literal(""));
        }
        valueinfo.setMessage(Component.literal(ex && data.value != null ? data.value : ""));
        getStatus();
        //
        matrix.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        //minecraft.textureManager.bindForSetup(TEXTURE);
        matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        //RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        for(GuiEventListener w : children()) if(w instanceof AbstractWidget) ((AbstractWidget)w).render(matrix, x, y, ticks);
        //
        RenderSystem.enableBlend();
        //RenderSystem.enableAlphaTest();
        if(ex && data.type.image() && tempimg != null){
            //minecraft.textureManager.bind(tempimg);
            draw(matrix, tempimg, leftPos + 199, topPos + 9, 48, 48);
        }
    }
    
    public static void draw(GuiGraphics gg, ResourceLocation texture, int x, int y, int w, int h){
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = gg.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, x, y + h, 0).uv(0, 1).endVertex();
        bufferbuilder.vertex(matrix, x + w, y + h, 0).uv(1, 1).endVertex();
        bufferbuilder.vertex(matrix, x + w, y, 0).uv(1, 0).endVertex();
        bufferbuilder.vertex(matrix, x, y, 0).uv(0, 0).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    private void getStatus(){
        if(statustext != null){
            status.setMessage(Component.translatable(statustext));
            return;
        }
        todo = 0;
        String eg = null;
        for(String str : fieldkeys){
            FieldData data = menu.doc.fields.get(str);
            if(!data.type.editable) continue;
            String val = data.getValue(menu.stack);
            if(val == null || val.length() == 0 && !data.can_empty){
                todo++;
                if(eg == null) eg = str;
            }
        }
        if(todo > 0){
            status.setMessage(Component.translatable("documents.editor.status.todo", todo, eg));
        }
        else{
            status.setMessage(Component.translatable("documents.editor.status.done"));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics stack, int x, int y){
        //
    }

    @Override
    public void containerTick(){
        //TODO field.tick();
    }

    public static abstract class GenericButton extends AbstractButton {

        private int tx, ty;
        private boolean text;
        private ResourceLocation texture = TEXTURE;

        public GenericButton(int x, int y, int tx, int ty,int w, int h, Component text){
            super(x, y, w, h, text);
            this.tx = tx;
            this.ty = ty;
        }

        public GenericButton(int x, int y, int tx, int ty,int w, int h, String text){
            this(x, y, tx, ty, w, h, Component.literal(text));
        }

        public GenericButton(int x, int y, int tx, int ty,int w, int h){
            this(x, y, tx, ty, w, h, "");
        }

        public GenericButton text(boolean bool){
            text = bool;
            return this;
        }

        @Override
        public void renderWidget(GuiGraphics stack, int mx, int my, float ticks){
            //Minecraft.getInstance().getTextureManager().bind(texture);
            if(isHovered) stack.setColor(0.85f, 0.7f, 0.18f, 0.75f);
            stack.blit(texture, getX(), getY(), tx, ty, width, height);
            stack.setColor(1, 1, 1, 1);
            if(text) stack.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, getFGColor());
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput neo){
            neo.add(NarratedElementType.HINT, getMessage());
        }

    }

    public static class GenericText extends AbstractWidget {

        protected Integer color = 0x636363;
        protected boolean centered;
        protected float scale = 1;
        protected String text, temp;

        public GenericText(int x, int y, int w, String text){
            super(x, y, w, 8, Component.literal(text));
            this.text = text;
        }

        public GenericText(int x, int y, int w, Component com){
            super(x, y, w, 8, com);
            this.text = com.getString();
        }

        public GenericText autoscale(){
            scale = -1;
            return this;
        }

        @Override
        public void renderWidget(GuiGraphics stack, int mx, int my, float ticks){
            temp = getMessage().getString();
            if(temp == null) return;
            if(!centered){
                stack.pose().pushPose();
                stack.pose().translate(getX(), getY(), 0);
                if(scale != 1){
                    if(scale == -1){
                        float w = (float)Minecraft.getInstance().font.width(temp);
                        if(w > 0){
                            float s = width / w;
                            if(s > 1) s = 1;
                            stack.pose().scale(s, s, s);
                        }
                    }
                    else{
                        stack.pose().scale(scale, scale, scale);
                    }
                }
                stack.drawString(Minecraft.getInstance().font, temp, 0, 0, color == null ? getFGColor() : color, false);
                stack.pose().popPose();
            }
            else{
                Font font = Minecraft.getInstance().font;
                stack.drawString(font, temp, getX() + width / 2 - (font.width(temp) / 2), getY() + (height - 8) / 2, color == null ? getFGColor() : color, false);
            }
            if(this.isHovered) renderToolTip(stack, mx, my);
        }

        public void renderToolTip(GuiGraphics stack, int mx, int my){
            if(getMessage() == null || getMessage().getString() == null) return;
            try{
                stack.renderTooltip(Minecraft.getInstance().font, getMessage(), mx, my);
            }
            catch(Exception e){
                //
            }
        }

        public GenericText color(Integer col){
            color = col;
            return this;
        }

        public GenericText centered(boolean bool){
            centered = bool;
            return this;
        }

        @Override
        public void setMessage(Component pMessage){
            super.setMessage(pMessage);
            this.text = pMessage.getString();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput neo){
            neo.add(NarratedElementType.HINT, text);
        }

        public GenericText scale(float scale){
            this.scale = scale;
            return this;
        }
    }

}
