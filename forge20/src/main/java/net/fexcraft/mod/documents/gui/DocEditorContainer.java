package net.fexcraft.mod.documents.gui;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import net.fexcraft.mod.documents.DocPerms;
import net.fexcraft.mod.documents.DocRegistry;
import net.fexcraft.mod.documents.Documents;
import net.fexcraft.mod.documents.data.Document;
import net.fexcraft.mod.documents.data.FieldData;
import net.fexcraft.mod.documents.data.FieldType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import static net.fexcraft.mod.documents.Documents.send;

public class DocEditorContainer extends AbstractContainerMenu implements UiPacketReceiver {

    protected ItemStack stack;
    protected Document doc;
    protected DocEditorScreen screen;
    protected Player player;

    public DocEditorContainer(int id, Inventory inv){
        super(Documents.DOC_EDITOR.get(), id);
        stack = inv.player.getItemInHand(InteractionHand.MAIN_HAND);
        player = inv.player;
        doc = DocRegistry.get(stack);
    }

    public DocEditorContainer(int id, Inventory inv, FriendlyByteBuf buffer){
        this(id, inv);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i){
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player){
        return true;
    }

    @Override
    public void onPacket(CompoundTag com, boolean client){
        if(com.contains("issue") && com.getBoolean("issue")){
            if(!client && !DocPerms.hasPerm(player, "document.issue", doc.id)){
                player.sendSystemMessage(Component.translatable("documents.editor.noperm"));
                return;
            }
            issueBy(stack.getTag(), player, client);
            if(!client){
                send(true, com, player);
            }
            else{
                player.closeContainer();
                player.sendSystemMessage(Component.translatable("documents.editor.signed"));
            }
            return;
        }
        if(!com.contains("field")) return;
        if(!client && !DocPerms.hasPerm(player, "document.edit", doc.id)){
            player.sendSystemMessage(Component.translatable("documents.editor.noperm"));
            return;
        }
        String field = com.getString("field");
        FieldData data = doc.fields.get(field);
        String value = com.getString("value");
        if(!data.type.editable) return;
        if(!client){
            if(data.type.number()){
                try{
                    if(data.type == FieldType.INTEGER){
                        Integer.parseInt(value);
                    }
                    else{
                        Float.parseFloat(value);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                    return;
                }
            }
            else if(data.type == FieldType.DATE){
                try{
                    value = (LocalDate.parse(value).toEpochDay() * 86400000) + "";
                }
                catch(Exception e){
                    e.printStackTrace();
                    player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                    return;
                }
            }
            else if(data.type == FieldType.UUID){
                try{
                    GameProfile gp = Documents.getCurrentServer().getProfileCache().get(value).get();
                    if(gp != null && gp.getId() != null && gp.getName() != null){
                        value = gp.getId().toString();
                    }
                    else UUID.fromString(value);
                }
                catch(Exception e){
                    e.printStackTrace();
                    player.sendSystemMessage(Component.literal("Error: " + e.getMessage()));
                    return;
                }
            }
            stack.getTag().putString("document:" + field, value);
            com.putString("value", value);
            send(true, com, player);
        }
        else{
            stack.getTag().putString("document:" + field, value);
            screen.statustext = null;
        }
    }

    private void issueBy(CompoundTag com, Player player, boolean client){
        com.putString("document:issuer", player.getGameProfile().getId().toString());
        com.putString("document:issued", new Date().getTime() + "");
        com.putString("document:issuer_name", player.getGameProfile().getName());
        if(client) return;
        try{
            GameProfile gp = Documents.getCurrentServer().getProfileCache().get(UUID.fromString(com.getString("document:uuid"))).get();
            com.putString("document:player_name", gp.getName());
        }
        catch(Exception e){
            e.printStackTrace();
            com.putString("document:player_name", com.getString("document:uuid"));
        }
    }
    
}
