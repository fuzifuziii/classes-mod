package com.fuzifuziii.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ClassManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile Map<String, PlayerClass> classes = Map.of();
    private static volatile boolean allowChange = false;

    private static final String DEFAULT_CONFIG = """
            {
              "_comment": "blocked_mods: mod ids or * for all. blocked_items: item ids or #tags. allowed_items: whitelist, beats blocks. icon: item id shown in the menu.",
              "allow_class_change": false,
              "classes": {
                "mage": {
                  "display_name": "Mage",
                  "description": "Master of arcane arts. Cannot pick up vanilla items except books.",
                  "icon": "minecraft:blaze_rod",
                  "blocked_mods": ["minecraft"],
                  "blocked_items": [],
                  "allowed_items": ["minecraft:book", "minecraft:enchanted_book"]
                },
                "warrior": {
                  "display_name": "Warrior",
                  "description": "Front line fighter. Has no use for books or wood.",
                  "icon": "minecraft:iron_sword",
                  "blocked_mods": [],
                  "blocked_items": ["minecraft:book", "#minecraft:logs"],
                  "allowed_items": []
                }
              }
            }
            """;

    private ClassManager() {}

    public static Path configPath() {
        return FMLPaths.CONFIGDIR.get().resolve(ClassesMod.MODID).resolve("classes.json");
    }

    // returns false and keeps old classes on error
    public static boolean load() {
        Path path = configPath();
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, DEFAULT_CONFIG);
            }

            Map<String, PlayerClass> loaded = new LinkedHashMap<>();
            boolean change;
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                change = root.has("allow_class_change") && root.get("allow_class_change").getAsBoolean();
                JsonObject all = root.getAsJsonObject("classes");

                for (Map.Entry<String, JsonElement> entry : all.entrySet()) {
                    String id = entry.getKey();
                    JsonObject c = entry.getValue().getAsJsonObject();

                    loaded.put(id, new PlayerClass(
                            id,
                            readString(c, "display_name", id),
                            readString(c, "description", ""),
                            readString(c, "icon", ""),
                            readList(c, "blocked_mods"),
                            readList(c, "blocked_items"),
                            readList(c, "allowed_items")));
                }
            }

            classes = loaded;
            allowChange = change;
            ClassesMod.LOGGER.info("loaded {} player classes", loaded.size());
            return true;
        } catch (IOException | RuntimeException e) {
            ClassesMod.LOGGER.error("failed to load {}", path, e);
            return false;
        }
    }

    private static String readString(JsonObject obj, String key, String fallback) {
        return obj.has(key) ? obj.get(key).getAsString() : fallback;
    }

    private static List<String> readList(JsonObject obj, String key) {
        List<String> out = new ArrayList<>();
        if (obj.has(key)) {
            JsonArray array = obj.getAsJsonArray(key);
            for (JsonElement e : array) out.add(e.getAsString());
        }
        return List.copyOf(out);
    }

    public static PlayerClass get(String id) {
        return classes.get(id);
    }

    public static Collection<String> ids() {
        return classes.keySet();
    }

    public static Collection<PlayerClass> all() {
        return classes.values();
    }

    // null when player has no class or class was removed from config
    public static PlayerClass getClassOf(Player player) {
        String id = player.getData(ModAttachments.PLAYER_CLASS);
        return id.isEmpty() ? null : classes.get(id);
    }

    // player can pick a class when they have none or changing is allowed
    public static boolean canChoose(Player player) {
        return allowChange || getClassOf(player) == null;
    }
}
