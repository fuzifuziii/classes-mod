package com.fuzifuziii.classes.client;

import com.fuzifuziii.classes.ClassInfo;
import com.fuzifuziii.classes.SelectClassPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ClassSelectScreen extends Screen {
    private static final int LEFT = 20;
    private static final int TOP = 40;
    private static final int ROW = 24;
    private static final int LIST_W = 120;

    private final String current;
    private final List<ClassInfo> classes;
    private ClassInfo selected;
    private int scroll;

    public ClassSelectScreen(String current, List<ClassInfo> classes) {
        super(Component.translatable("screen.classes.title"));
        this.current = current;
        this.classes = classes;
        this.selected = classes.stream().filter(c -> c.id().equals(current)).findFirst().orElse(null);
    }

    @Override
    protected void init() {
        int visible = Math.max(1, (height - TOP - 40) / ROW);
        scroll = Math.max(0, Math.min(scroll, Math.max(0, classes.size() - visible)));

        // build one button per visible class
        for (int i = scroll; i < Math.min(classes.size(), scroll + visible); i++) {
            ClassInfo info = classes.get(i);
            Component label = Component.literal((info == selected ? "> " : "") + info.displayName());
            addRenderableWidget(Button.builder(label, b -> {
                selected = info;
                rebuildWidgets();
            }).bounds(LEFT, TOP + (i - scroll) * ROW, LIST_W, 20).build());
        }

        Button confirm = addRenderableWidget(Button.builder(Component.translatable("screen.classes.confirm"), b -> confirm())
                .bounds(panelX(), height - 36, 120, 20).build());
        confirm.active = selected != null && !selected.id().equals(current);
    }

    private int panelX() {
        return LEFT + LIST_W + 20;
    }

    private void confirm() {
        if (selected == null) return;
        PacketDistributor.sendToServer(new SelectClassPayload(selected.id()));
        onClose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // scroll the class list
        scroll -= (int) Math.signum(scrollY);
        rebuildWidgets();
        return true;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(font, title, width / 2, 15, 0xFFFFFF);

        int x = panelX();
        int w = width - x - 20;
        int y = TOP;
        g.fill(x - 6, y - 6, x + w + 6, height - 44, 0x80000000);

        if (selected == null) {
            g.drawString(font, Component.translatable("screen.classes.pick"), x, y, 0xAAAAAA);
            return;
        }

        // draw the icon at double size
        int textX = x;
        ItemStack icon = iconOf(selected);
        if (!icon.isEmpty()) {
            g.pose().pushPose();
            g.pose().translate(x, y, 0);
            g.pose().scale(2f, 2f, 1f);
            g.renderItem(icon, 0, 0);
            g.pose().popPose();
            textX = x + 40;
        }
        g.drawString(font, selected.displayName(), textX, y + 8, 0xFFD700);
        y += 40;

        y = drawBlock(g, selected.description(), x, y, w, 0xFFFFFF);
        y = drawBlock(g, line("screen.classes.blocked_mods", selected.blockedMods()), x, y, w, 0xFF6666);
        y = drawBlock(g, line("screen.classes.blocked_items", selected.blockedItems()), x, y, w, 0xFF6666);
        drawBlock(g, line("screen.classes.allowed_items", selected.allowedItems()), x, y, w, 0x66FF66);
    }

    private static String line(String key, List<String> values) {
        return values.isEmpty() ? "" : Component.translatable(key).getString() + ": " + String.join(", ", values);
    }

    // draw wrapped text and return the next y
    private int drawBlock(GuiGraphics g, String text, int x, int y, int width, int color) {
        if (text.isEmpty()) return y;
        for (FormattedCharSequence part : font.split(Component.literal(text), width)) {
            g.drawString(font, part, x, y, color);
            y += font.lineHeight + 2;
        }
        return y + 6;
    }

    private static ItemStack iconOf(ClassInfo info) {
        ResourceLocation id = ResourceLocation.tryParse(info.icon());
        if (id == null) return ItemStack.EMPTY;
        return BuiltInRegistries.ITEM.getOptional(id).map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
