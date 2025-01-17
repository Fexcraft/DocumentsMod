package net.fexcraft.mod.doc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.doc.data.Document;
import net.fexcraft.mod.doc.packet.DocPacketHandler;
import net.fexcraft.mod.doc.ui.DocUI;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.UniEntity;
import net.fexcraft.mod.uni.item.StackWrapper;
import net.fexcraft.mod.uni.world.EntityW;
import net.fexcraft.mod.uni.world.WrapperHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static net.fexcraft.mod.doc.DocRegistry.NBTKEY_TYPE;
import static net.fexcraft.mod.fcl.UniFCL.LOG;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DocumentsCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> get(){
		return Commands.literal("documents")
			.then(Commands.literal("list").executes(cmd -> {
				cmd.getSource().sendSystemMessage(Component.literal("\u00A77============"));
				for(IDL idl : DocRegistry.getDocuments().keySet()){
					cmd.getSource().sendSystemMessage(Component.literal(idl.colon()));
				}
				return 0;
			}))
			.then(Commands.literal("uuid").executes(cmd -> {
				Documents.LOGGER.info(cmd.getSource().getPlayerOrException().toString());
				cmd.getSource().sendSystemMessage(Component.literal(cmd.getSource().getPlayerOrException().getGameProfile().getId().toString()));
				return 0;
			}))
			.then(Commands.literal("fill")
				.then(Commands.argument("doc", StringArgumentType.greedyString())
				.executes(cmd -> {
					String id = cmd.getArgument("doc", String.class);
					Document doc = DocRegistry.getDocument(id);
					if(doc == null){
						cmd.getSource().sendFailure(Component.translatable("documents.cmd.doc_not_found"));
						return -1;
					}
					EntityW player = UniEntity.getEntity(cmd.getSource().getPlayerOrException());
					int idx = DocRegistry.getDocumentIndex(id);
					if(idx < 0){
						player.send("404: doc not found");
						return -1;
					}
					player.openUI(DocUI.EDITOR, idx, 1, 0);
					return 0;
				})))
			.then(Commands.literal("get").then(Commands.argument("id", StringArgumentType.greedyString()).executes(cmd -> {
				Document doc = DocRegistry.getDocument(cmd.getArgument("id", String.class));
				if(doc == null){
					cmd.getSource().sendFailure(Component.translatable("documents.cmd.doc_not_found"));
					return -1;
				}
				else{
					if(!DocPerms.hasPerm(UniEntity.getEntity(cmd.getSource().getPlayerOrException()), "command.get", doc.id.colon())){
						cmd.getSource().sendSystemMessage(Component.translatable("documents.cmd.no_permission"));
						return -1;
					}
					StackWrapper stack = DocCreator.REFERENCE.copy();
					stack.createTagIfMissing();
					stack.getTag().set(NBTKEY_TYPE, doc.id.colon());
					cmd.getSource().getPlayerOrException().addItem(stack.local());
					cmd.getSource().sendSystemMessage(Component.translatable("documents.cmd.added"));
					if(EnvInfo.DEV) Documents.LOGGER.info(stack.getTag().toString());
				}
				return 0;
			})))
			.then(Commands.literal("reload-perms").executes(cmd -> {
				Player entity = cmd.getSource().getPlayerOrException();
				if(!DocPerms.hasPerm(UniEntity.getEntity(entity), "command.reload-perms") && !entity.hasPermissions(4)){
					cmd.getSource().sendFailure(Component.translatable("documents.cmd.no_permission"));
					return 1;
				}
				DocPerms.loadperms();
				cmd.getSource().sendSystemMessage(Component.translatable("documents.cmd.perms_reloaded"));
				return 0;
			}))
			.then(Commands.literal("reload-docs").executes(cmd -> {
				Player entity = cmd.getSource().getPlayerOrException();
				if(!DocPerms.hasPerm(UniEntity.getEntity(entity), "command.reload-docs") && !entity.hasPermissions(4)){
					cmd.getSource().sendFailure(Component.translatable("documents.cmd.no_permission"));
					return 1;
				}
				DocRegistry.reload();
				DocRegistry.sendSync();
				cmd.getSource().sendSystemMessage(Component.translatable("documents.cmd.docs_reloaded"));
				return 0;
			}))
			.then(Commands.literal("start")
				.then(Commands.argument("player", EntityArgument.player())
				.then(Commands.argument("doc", StringArgumentType.greedyString())
				.executes(cmd -> {
				Document doc = DocRegistry.getDocument(cmd.getArgument("doc", String.class));
				if(doc == null){
					cmd.getSource().sendFailure(Component.translatable("documents.cmd.doc_not_found"));
					return -1;
				}
				Player cmdu = cmd.getSource().isPlayer() ? cmd.getSource().getPlayer() : null;
				Player player = cmd.getArgument("player", Player.class);
				DocCreator.start(cmdu == null ? LOG : UniEntity.getEntity(cmdu), player.getGameProfile().getId().toString(), doc.id.colon());
				return 0;
				}))))
			.then(Commands.literal("set")
				.then(Commands.argument("player", EntityArgument.player())
				.then(Commands.argument("key", StringArgumentType.greedyString())
				.then(Commands.argument("value", StringArgumentType.greedyString())
				.executes(cmd -> {
					Player cmdu = cmd.getSource().isPlayer() ? cmd.getSource().getPlayer() : null;
					Player player = cmd.getArgument("player", Player.class);
					DocCreator.set(cmdu == null ? LOG : UniEntity.getEntity(cmdu), player.getGameProfile().getId().toString(),
						cmd.getArgument("keu", String.class), cmd.getArgument("value", String.class));
					return 0;
				})))))
			.then(Commands.literal("issue")
				.then(Commands.argument("player", EntityArgument.player())
				.executes(cmd -> {
					Player cmdu = cmd.getSource().isPlayer() ? cmd.getSource().getPlayer() : null;
					Player player = cmd.getArgument("player", Player.class);
					DocCreator.issue(cmdu == null ? LOG : UniEntity.getEntity(cmdu), player.getGameProfile().getId().toString());
					return 0;
				})))
			.executes(cmd -> {
				cmd.getSource().sendSystemMessage(Component.literal("\u00A7aGeneral:"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents list"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents uuid"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents fill <doc-id>"));
				cmd.getSource().sendSystemMessage(Component.literal("\u00A7cOperator:"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents get"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents reload-perms"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents reload-docs"));
				cmd.getSource().sendSystemMessage(Component.literal("\u00A76Console/NPC"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents start <player> <doc-id>"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents set <player> <key> <value>"));
				cmd.getSource().sendSystemMessage(Component.literal("/documents issue <player>"));
				return 0;
			});
	}

}
