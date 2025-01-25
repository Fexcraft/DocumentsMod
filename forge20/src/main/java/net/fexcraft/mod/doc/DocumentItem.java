package net.fexcraft.mod.doc;

import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.doc.data.DocItem;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.ui.DocUI;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.inv.UniStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.function.Consumer;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_TYPE;

public class DocumentItem extends Item implements DocItem {

	public static DocumentItem INSTANCE = null;

	public DocumentItem(){
		super(new Properties().fireResistant().stacksTo(1));
		INSTANCE = this;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag flag){
		UniStack uni = UniStack.get(stack);
		if(uni == null) return;
		DocStackApp app = uni.appended.get(DocStackApp.class);
		if(app == null){
			list.add(Component.literal("no document app"));
			return;
		}
		CompoundTag com = stack.getTag();
		Document doc = app.getDocument();
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

	@Override
	public Component getName(ItemStack stack){
		if(stack.hasTag()){
			Document doc = DocRegistry.getDocument(stack.getTag().getString(NBTKEY_TYPE));
			if(doc != null) return Component.literal(doc.name);
		}
		return super.getName(stack);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		if(world.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));
		ItemStack stack = player.getItemInHand(hand);
		DocStackApp cap = UniStack.getApp(stack, DocStackApp.class);
		if(cap == null || cap.getDocument() == null){
			player.sendSystemMessage(Component.literal("no document data"));
			return InteractionResultHolder.fail(stack);
		}
		UniEntity.getEntity(player).openUI(cap.isIssued() ? DocUI.VIEWER : DocUI.EDITOR, V3I.NULL);
		return InteractionResultHolder.success(stack);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer){
		consumer.accept(new IClientItemExtensions(){
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer(){
				return DocItemRenderer.RENDERER.get();
			}
		});
	}

}
