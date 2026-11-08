package com.fuzifuziii.classes;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public final class ClassMenu {
    private ClassMenu() {}

    // send class list to the client and open the menu
    public static void open(ServerPlayer player) {
        List<ClassInfo> infos = ClassManager.all().stream().map(PlayerClass::toInfo).toList();
        PacketDistributor.sendToPlayer(player, new OpenClassScreenPayload(player.getData(ModAttachments.PLAYER_CLASS), infos));
    }

    // handle keybind request from the client
    public static void request(ServerPlayer player) {
        if (ClassManager.all().isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.classes.no_classes"));
        } else if (ClassManager.canChoose(player)) {
            open(player);
        } else {
            player.sendSystemMessage(Component.translatable("message.classes.change_disabled"));
        }
    }

    // validate and apply the choice from the client
    public static void select(ServerPlayer player, String id) {
        PlayerClass playerClass = ClassManager.get(id);
        if (playerClass == null || !ClassManager.canChoose(player)) return;

        player.setData(ModAttachments.PLAYER_CLASS, id);
        player.sendSystemMessage(Component.translatable("message.classes.selected", playerClass.displayName()));
    }
}
