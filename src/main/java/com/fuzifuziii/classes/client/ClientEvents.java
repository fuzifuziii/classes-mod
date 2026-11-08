package com.fuzifuziii.classes.client;

import com.fuzifuziii.classes.ClassesMod;
import com.fuzifuziii.classes.RequestScreenPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ClassesMod.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    public static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.classes.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.classes");

    private ClientEvents() {}

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        while (OPEN_MENU.consumeClick()) {
            // ask the server, it decides if the menu may open
            if (mc.player != null && mc.screen == null) {
                PacketDistributor.sendToServer(new RequestScreenPayload());
            }
        }
    }
}
