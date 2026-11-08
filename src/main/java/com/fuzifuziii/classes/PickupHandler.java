package com.fuzifuziii.classes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber(modid = ClassesMod.MODID)
public final class PickupHandler {
    private PickupHandler() {}

    @SubscribeEvent
    public static void onPickup(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        PlayerClass playerClass = ClassManager.getClassOf(player);
        if (playerClass == null) return;

        ItemStack stack = event.getItemEntity().getItem();
        // deny pickup when class rules forbid it
        if (!playerClass.canPickup(stack)) {
            event.setCanPickup(TriState.FALSE);
        }
    }
}
