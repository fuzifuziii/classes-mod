package com.fuzifuziii.classes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record PlayerClass(
        String id,
        String displayName,
        String description,
        String icon,
        List<String> blockedMods,
        List<String> blockedItems,
        List<String> allowedItems) {

    public boolean canPickup(ItemStack stack) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());

        // whitelist beats everything
        if (matchesAny(allowedItems, stack, key)) return true;
        // explicitly blocked items and tags
        if (matchesAny(blockedItems, stack, key)) return false;
        // whole mod blocked, star blocks all mods
        if (blockedMods.contains("*") || blockedMods.contains(key.getNamespace())) return false;

        return true;
    }

    public ClassInfo toInfo() {
        return new ClassInfo(id, displayName, description, icon, blockedMods, blockedItems, allowedItems);
    }

    private static boolean matchesAny(List<String> entries, ItemStack stack, ResourceLocation key) {
        for (String entry : entries) {
            if (entry.startsWith("#")) {
                ResourceLocation tagId = ResourceLocation.tryParse(entry.substring(1));
                if (tagId != null && stack.is(TagKey.<Item>create(Registries.ITEM, tagId))) return true;
            } else if (entry.equals(key.toString())) {
                return true;
            }
        }
        return false;
    }
}
