package com.fuzifuziii.classes;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestScreenPayload() implements CustomPacketPayload {
    public static final Type<RequestScreenPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ClassesMod.MODID, "request_screen"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, RequestScreenPayload> CODEC =
            StreamCodec.unit(new RequestScreenPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
