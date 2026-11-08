package com.fuzifuziii.classes;

import com.fuzifuziii.classes.client.ClientPayloadHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ClassesMod.MODID)
public final class ModNetwork {
    private ModNetwork() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(OpenClassScreenPayload.TYPE, OpenClassScreenPayload.CODEC,
                (payload, ctx) -> ClientPayloadHandler.open(payload));

        registrar.playToServer(SelectClassPayload.TYPE, SelectClassPayload.CODEC,
                (payload, ctx) -> ClassMenu.select((ServerPlayer) ctx.player(), payload.classId()));

        registrar.playToServer(RequestScreenPayload.TYPE, RequestScreenPayload.CODEC,
                (payload, ctx) -> ClassMenu.request((ServerPlayer) ctx.player()));
    }
}
