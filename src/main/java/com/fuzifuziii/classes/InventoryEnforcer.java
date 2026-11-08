package com.fuzifuziii.classes;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ClassesMod.MODID)
public final class InventoryEnforcer {
    private InventoryEnforcer() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        // sweep every half second
        if (event.getEntity() instanceof ServerPlayer player && player.tickCount % 10 == 0) {
            sweep(player);
        }
    }

    // drop every item the player class may not hold
    public static void sweep(ServerPlayer player) {
        if (!ClassManager.dropForbidden()) return;
        if (player.isCreative() || player.isSpectator()) return;

        PlayerClass playerClass = ClassManager.getClassOf(player);
        if (playerClass == null) return;

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty() || playerClass.canPickup(stack)) continue;

            inventory.setItem(i, ItemStack.EMPTY);
            player.drop(stack, false);
            player.displayClientMessage(Component.translatable("message.classes.dropped", stack.getHoverName()), true);
        }
    }
}
