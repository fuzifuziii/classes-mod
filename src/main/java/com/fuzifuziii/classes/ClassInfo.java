package com.fuzifuziii.classes;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ClassInfo(
        String id,
        String displayName,
        String description,
        String icon,
        List<String> blockedMods,
        List<String> blockedItems,
        List<String> allowedItems) {

    private static final StreamCodec<ByteBuf, List<String>> STRINGS =
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list());

    public static final StreamCodec<ByteBuf, ClassInfo> CODEC = StreamCodec.of(
            (buf, v) -> {
                ByteBufCodecs.STRING_UTF8.encode(buf, v.id());
                ByteBufCodecs.STRING_UTF8.encode(buf, v.displayName());
                ByteBufCodecs.STRING_UTF8.encode(buf, v.description());
                ByteBufCodecs.STRING_UTF8.encode(buf, v.icon());
                STRINGS.encode(buf, v.blockedMods());
                STRINGS.encode(buf, v.blockedItems());
                STRINGS.encode(buf, v.allowedItems());
            },
            buf -> new ClassInfo(
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    STRINGS.decode(buf),
                    STRINGS.decode(buf),
                    STRINGS.decode(buf)));

    public static final StreamCodec<ByteBuf, List<ClassInfo>> LIST_CODEC = CODEC.apply(ByteBufCodecs.list());
}
