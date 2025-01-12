package net.fexcraft.mod.documents.data;

import net.fexcraft.mod.documents.DocRegistry;
import net.fexcraft.mod.documents.Documents;
import net.fexcraft.mod.documents.gui.DocEditorContainer;
import net.fexcraft.mod.documents.gui.DocViewerContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DocumentItem extends Item {

	public static DocumentItem INSTANCE = null;
	public static String NBTKEY = "documents:type";

	public DocumentItem(){
		super(new Properties().fireResistant().stacksTo(1));
		INSTANCE = this;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag flag){
		if(stack.hasTag() && stack.getTag().contains(NBTKEY)){
			CompoundTag com = stack.getTag();
			Document doc = DocRegistry.DOCS.get(com.getString(NBTKEY));
			if(doc == null){
				list.add(Component.literal("no document data"));
				list.add(Component.literal(com.toString()));
			}
			else{
				for(String str : doc.description){
					list.add(Component.translatable(str));
				}
				list.add(Component.translatable(com.getBoolean("document:issued") ? "documents.item.issued" : "documents.item.blank"));
			}
		}
		else{
			list.add(Component.literal("no type data"));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		if(world.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));
		ItemStack stack = player.getItemInHand(hand);
		if(stack.hasTag() && stack.getTag().contains(NBTKEY)){
			CompoundTag com = stack.getTag();
			Document doc = DocRegistry.DOCS.get(com.getString(NBTKEY));
			if(doc == null){
				player.sendSystemMessage(Component.literal("no document data"));
				return InteractionResultHolder.fail(stack);
			}
			else{
				Documents.openViewerOrEditor(player, com);
				return InteractionResultHolder.success(stack);
			}
		}
		else return InteractionResultHolder.pass(stack);
	}

}
