package net.fexcraft.mod.doc;

import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.doc.data.DocItem;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.ui.DocUI;
import net.fexcraft.mod.fcl.FCL;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.inv.UniStack;
import net.fexcraft.mod.uni.tag.TagCW;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_TYPE;

public class DocumentItem extends Item implements DocItem {

	public DocumentItem(ResourceKey<Item> key){
		super(new Properties().setId(key).fireResistant().stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay disp, Consumer<Component> cons, TooltipFlag flag){
		DocStackApp app = UniStack.getApp(stack, DocStackApp.class);
		if(app == null){
			cons.accept(Component.literal("no document app"));
			return;
		}
		CompoundTag com = stack.get(FCL.FCLTAG).getUnsafe();
		Document doc = app.getDocument();
		if(doc == null){
			cons.accept(Component.literal("no document data"));
			cons.accept(Component.literal(com.toString()));
		}
		else{
			for(String str : doc.description){
				cons.accept(Component.translatable(str));
			}
			cons.accept(Component.translatable(com.getBooleanOr("document:issued", false) ? "documents.item.issued" : "documents.item.blank"));
		}
	}

	@Override
	public Component getName(ItemStack stack){
		if(stack.has(FCL.FCLTAG)){
			TagCW com = UniStack.getStack(stack).directTag();
			Document doc = DocRegistry.getDocument(com.getString(NBTKEY_TYPE));
			if(doc != null) return Component.literal(doc.name);
		}
		return super.getName(stack);
	}

	@Override
	public InteractionResult use(Level world, Player player, InteractionHand hand){
		if(world.isClientSide) return InteractionResult.PASS;
		ItemStack stack = player.getItemInHand(hand);
		DocStackApp cap = UniStack.getApp(stack, DocStackApp.class);
		if(cap == null || cap.getDocument() == null){
			((ServerPlayer)player).sendSystemMessage(Component.literal("no document data"));
			return InteractionResult.FAIL;
		}
		UniEntity.getEntity(player).openUI(cap.isIssued() ? DocUI.VIEWER : DocUI.EDITOR, V3I.NULL);
		return InteractionResult.SUCCESS;
	}

}
