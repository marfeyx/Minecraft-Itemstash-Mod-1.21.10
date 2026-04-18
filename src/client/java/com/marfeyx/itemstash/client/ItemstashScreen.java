package com.marfeyx.itemstash.client;

import com.marfeyx.itemstash.network.ItemstashActionPayload;
import com.marfeyx.itemstash.stash.ItemstashEntry;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ItemstashScreen extends Screen {
    private static final int PANEL_WIDTH = 320;
    private static final int ROW_HEIGHT = 32;
    private static final int BUTTON_WIDTH = 76;
    private static final int BUTTON_HEIGHT = 20;
    private static final int TEXT_WHITE = 0xFFFFFFFF;
    private static final int TEXT_MUTED = 0xFFA0A0A0;
    private static final int TEXT_COUNT = 0xFFA0FFA0;
    private static final int SCREEN_OVERLAY = 0x99000000;
    private static final int PANEL_BACKGROUND = 0xCC101010;
    private static final int PANEL_BORDER = 0xFF6F6F6F;
    private static final int ROW_BACKGROUND = 0x66000000;

    private final List<ItemstashEntry> entries;
    private int scrollOffset;

    public ItemstashScreen(List<ItemstashEntry> entries) {
        super(Text.translatable("screen.itemstash.title"));
        this.entries = List.copyOf(entries);
    }

    @Override
    protected void init() {
        int left = (width - PANEL_WIDTH) / 2;
        int top = 42;
        int visibleRows = visibleRows();
        int maxOffset = maxScrollOffset();
        scrollOffset = Math.min(scrollOffset, maxOffset);

        for (int row = 0; row < visibleRows; row++) {
            int entryIndex = row + scrollOffset;
            if (entryIndex >= entries.size()) {
                break;
            }

            ItemstashEntry entry = entries.get(entryIndex);
            int y = top + 28 + row * ROW_HEIGHT;
            int firstButtonX = left + PANEL_WIDTH - BUTTON_WIDTH * 3 - 10;

            addDrawableChild(ButtonWidget.builder(Text.translatable("screen.itemstash.take_stack"), button ->
                    sendAction(entry.index(), ItemstashActionPayload.TAKE_STACK)
            ).dimensions(firstButtonX, y + 5, BUTTON_WIDTH, BUTTON_HEIGHT).build());

            addDrawableChild(ButtonWidget.builder(Text.translatable("screen.itemstash.fill_inventory"), button ->
                    sendAction(entry.index(), ItemstashActionPayload.FILL_INVENTORY)
            ).dimensions(firstButtonX + BUTTON_WIDTH + 4, y + 5, BUTTON_WIDTH, BUTTON_HEIGHT).build());

            addDrawableChild(ButtonWidget.builder(Text.translatable("screen.itemstash.drop_all"), button ->
                    sendAction(entry.index(), ItemstashActionPayload.DROP_ALL)
            ).dimensions(firstButtonX + (BUTTON_WIDTH + 4) * 2, y + 5, BUTTON_WIDTH, BUTTON_HEIGHT).build());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(0, 0, width, height, SCREEN_OVERLAY);

        int left = (width - PANEL_WIDTH) / 2;
        int top = 42;
        int panelHeight = Math.min(height - top * 2, 42 + visibleRows() * ROW_HEIGHT);

        context.fill(left - 12, top - 12, left + PANEL_WIDTH + 12, top + panelHeight, PANEL_BACKGROUND);
        drawBorder(context, left - 12, top - 12, PANEL_WIDTH + 24, panelHeight + 12);
        context.drawText(textRenderer, title, left, top, TEXT_WHITE, false);

        if (entries.isEmpty()) {
            Text empty = Text.translatable("screen.itemstash.empty");
            context.drawText(textRenderer, empty, (width - textRenderer.getWidth(empty)) / 2, height / 2, TEXT_MUTED, false);
            super.render(context, mouseX, mouseY, deltaTicks);
            return;
        }

        int visibleRows = visibleRows();
        for (int row = 0; row < visibleRows; row++) {
            int entryIndex = row + scrollOffset;
            if (entryIndex >= entries.size()) {
                break;
            }

            ItemstashEntry entry = entries.get(entryIndex);
            int y = top + 28 + row * ROW_HEIGHT;

            context.fill(left - 4, y + 2, left + PANEL_WIDTH + 4, y + ROW_HEIGHT - 2, ROW_BACKGROUND);
            context.drawItem(entry.stack(), left, y + 7);
            context.drawText(textRenderer, entry.stack().getName(), left + 24, y + 6, TEXT_WHITE, false);
            context.drawText(textRenderer, Text.translatable("screen.itemstash.count", entry.count()), left + 24, y + 18, TEXT_COUNT, false);
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int previousOffset = scrollOffset;
        int maxOffset = maxScrollOffset();

        if (verticalAmount < 0) {
            scrollOffset = Math.min(maxOffset, scrollOffset + 1);
        } else if (verticalAmount > 0) {
            scrollOffset = Math.max(0, scrollOffset - 1);
        }

        if (previousOffset != scrollOffset) {
            clearAndInit();
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private int visibleRows() {
        return Math.max(1, (height - 92) / ROW_HEIGHT);
    }

    private int maxScrollOffset() {
        return Math.max(0, entries.size() - visibleRows());
    }

    private void drawBorder(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + 1, PANEL_BORDER);
        context.fill(x, y + height - 1, x + width, y + height, PANEL_BORDER);
        context.fill(x, y, x + 1, y + height, PANEL_BORDER);
        context.fill(x + width - 1, y, x + width, y + height, PANEL_BORDER);
    }

    private void sendAction(int index, String action) {
        ClientPlayNetworking.send(new ItemstashActionPayload(index, action));
    }
}
