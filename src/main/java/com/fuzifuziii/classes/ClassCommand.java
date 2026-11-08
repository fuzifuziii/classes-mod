package com.fuzifuziii.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public final class ClassCommand {
    private ClassCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("class")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("class", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(ClassManager.ids(), builder))
                                        .executes(ClassCommand::set))))
                .then(Commands.literal("clear")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ClassCommand::clear)))
                .then(Commands.literal("list").executes(ClassCommand::list))
                .then(Commands.literal("reload").executes(ClassCommand::reload)));
    }

    private static int set(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String id = StringArgumentType.getString(ctx, "class");
        PlayerClass playerClass = ClassManager.get(id);
        if (playerClass == null) {
            ctx.getSource().sendFailure(Component.literal("unknown class: " + id));
            return 0;
        }

        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
        for (ServerPlayer player : targets) {
            player.setData(ModAttachments.PLAYER_CLASS, id);
            InventoryEnforcer.sweep(player);
            player.sendSystemMessage(Component.literal("your class is now " + playerClass.displayName()));
        }

        ctx.getSource().sendSuccess(() -> Component.literal(
                "set class " + playerClass.displayName() + " for " + targets.size() + " player(s)"), true);
        return targets.size();
    }

    private static int clear(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "targets");
        for (ServerPlayer player : targets) {
            player.setData(ModAttachments.PLAYER_CLASS, "");
            player.sendSystemMessage(Component.literal("your class was cleared"));
        }

        ctx.getSource().sendSuccess(() -> Component.literal(
                "cleared class for " + targets.size() + " player(s)"), true);
        return targets.size();
    }

    private static int list(CommandContext<CommandSourceStack> ctx) {
        if (ClassManager.all().isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("no classes defined"));
            return 0;
        }

        for (PlayerClass c : ClassManager.all()) {
            ctx.getSource().sendSuccess(() -> Component.literal(
                    c.id() + " (" + c.displayName() + "): blocked mods " + c.blockedMods()
                            + ", blocked items " + c.blockedItems()
                            + ", allowed " + c.allowedItems()), false);
        }
        return ClassManager.all().size();
    }

    private static int reload(CommandContext<CommandSourceStack> ctx) {
        if (!ClassManager.load()) {
            ctx.getSource().sendFailure(Component.literal("reload failed, check the log"));
            return 0;
        }

        // apply new rules to online players
        ctx.getSource().getServer().getPlayerList().getPlayers().forEach(InventoryEnforcer::sweep);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "reloaded " + ClassManager.all().size() + " class(es)"), true);
        return 1;
    }
}
