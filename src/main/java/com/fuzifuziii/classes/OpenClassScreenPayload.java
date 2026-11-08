package com.fuzifuziii.classes;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record OpenClassScreenPayload(String current, List<ClassInfo> classes) implements CustomPacketPayload {
    public static final Type<OpenClassScreenPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ClassesMod.MODID, "open_screen"));

    public static final StreamCodec<io.netty.buffer.ByteBuf, OpenClassScreenPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenClassScreenPayload::current,
            ClassInfo.LIST_CODEC, OpenClassScreenPayload::classes,
            OpenClassScreenPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
