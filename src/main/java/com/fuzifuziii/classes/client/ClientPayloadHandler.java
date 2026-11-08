package com.fuzifuziii.classes.client;

import com.fuzifuziii.classes.OpenClassScreenPayload;
import net.minecraft.client.Minecraft;

public final class ClientPayloadHandler {
    private ClientPayloadHandler() {}

    public static void open(OpenClassScreenPayload payload) {
        Minecraft.getInstance().setScreen(new ClassSelectScreen(payload.current(), payload.classes()));
    }
}
