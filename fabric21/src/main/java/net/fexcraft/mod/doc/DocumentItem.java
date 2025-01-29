package net.fexcraft.mod.doc;

import net.fexcraft.lib.common.math.V3I;
import net.fexcraft.mod.doc.data.DocItem;
import net.fexcraft.mod.doc.data.DocStackApp;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.ui.DocUI;
import net.fexcraft.mod.fcl.FCL;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.inv.UniStack;
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
import net.minecraft.world.level.Level;

import java.util.List;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_TYPE;

public class DocumentItem extends Item implements DocItem {

	public DocumentItem(ResourceKey<Item> key){
		super(new Properties().setId(key).fireResistant().stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag){
		DocStackApp app = UniStack.getApp(stack, DocStackApp.class);
		if(app == null){
			list.add(Component.literal("no document app"));
			return;
		}
		CompoundTag com = stack.get(FCL.FCLTAG).getUnsafe();
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
		if(stack.has(FCL.FCLTAG)){
			Document doc = DocRegistry.getDocument(stack.get(FCL.FCLTAG).getUnsafe().getString(NBTKEY_TYPE));
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
