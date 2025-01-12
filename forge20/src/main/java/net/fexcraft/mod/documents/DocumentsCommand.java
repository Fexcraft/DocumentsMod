package net.fexcraft.mod.documents;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fexcraft.mod.documents.data.Document;
import net.fexcraft.mod.documents.data.DocumentItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocumentsCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> get(){
		return Commands.literal("documents")
			.then(Commands.literal("list").executes(cmd -> {
				cmd.getSource().sendSystemMessage(Component.literal("\u00A77============"));
				for(String str : DocRegistry.DOCS.keySet()){
					cmd.getSource().sendSystemMessage(Component.literal(str));
				}
				return 0;
			}))
			.then(Commands.literal("uuid").executes(cmd -> {
				Documents.LOGGER.info(cmd.getSource().getPlayerOrException().toString());
				cmd.getSource().sendSystemMessage(Component.literal(cmd.getSource().getPlayerOrException().getGameProfile().getId().toString()));
				return 0;
			}))
			.then(Commands.literal("reload-perms").executes(cmd -> {
				Player entity = cmd.getSource().getPlayerOrException();
				if(!DocPerms.hasPerm(entity, "command.reload-perms") && !entity.hasPermissions(4)){
					cmd.getSource().sendFailure(Component.translatable("documents.cmd.no_permission"));
					return 1;
				}
				DocPerms.loadperms();
				cmd.getSource().sendSystemMessage(Component.translatable("documents.cmd.perms_reloaded"));
				return 0;
			}))
			.then(Commands.literal("reload-docs").executes(cmd -> {
				Player entity = cmd.getSource().getPlayerOrException();
				if(!DocPerms.hasPerm(entity, "command.reload-docs") && !entity.hasPermissions(4)){
					cmd.getSource().sendFailure(Component.translatable("documents.cmd.no_permission"));
					return 1;
				}
				DocRegistry.init();
				entity.getServer().getPlayerList().getPlayers().forEach(player -> {
					Documents.sendSyncTo(player);
				});
				cmd.getSource().sendSystemMessage(Component.translatable("documents.cmd.docs_reloaded"));
				return 0;
			}))
			.then(Commands.literal("get").then(Commands.argument("id", StringArgumentType.word()).executes(cmd -> {
				Document doc = DocRegistry.DOCS.get(cmd.getArgument("id", String.class));
				if(doc == null){
					cmd.getSource().sendFailure(Component.translatable("documents.cmd.doc_not_found"));
					return -1;
				}
				else{
					if(!DocPerms.hasPerm(cmd.getSource().getPlayerOrException(), "command.get", doc.id)){
						cmd.getSource().sendSystemMessage(Component.translatable("documents.cmd.no_permission"));
						return -1;
					}
					ItemStack stack = new ItemStack(DocumentItem.INSTANCE);
					CompoundTag com = stack.hasTag() ? stack.getTag() : new CompoundTag();
					com.putString(DocumentItem.NBTKEY, doc.id);
					stack.setTag(com);
					cmd.getSource().getPlayerOrException().addItem(stack);
					cmd.getSource().sendSystemMessage(Component.translatable("documents.cmd.added"));
					Documents.LOGGER.info(com.toString());
				}
				return 0;
			})))
			.executes(cmd -> {
				cmd.getSource().sendSystemMessage(Component.literal("\u00A77============"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents list"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents get"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents uuid"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents reload-perms"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents reload-docs"));
				cmd.getSource().sendSystemMessage(Component.literal("\u00A77============"));
				return 0;
			});
	}

}
