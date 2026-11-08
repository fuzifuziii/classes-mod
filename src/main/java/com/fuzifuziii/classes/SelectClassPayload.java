package com.fuzifuziii.classes;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectClassPayload(String classId) implements CustomPacketPayload {
    public static final Type<SelectClassPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ClassesMod.MODID, "select_class"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, SelectClassPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SelectClassPayload::classId,
            SelectClassPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
