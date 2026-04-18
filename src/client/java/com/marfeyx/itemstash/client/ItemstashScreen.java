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
        renderBackground(context, mouseX, mouseY, deltaTicks);

        int left = (width - PANEL_WIDTH) / 2;
        int top = 42;
        context.drawText(textRenderer, title, left, top, 0xFFFFFF, false);

        if (entries.isEmpty()) {
            Text empty = Text.translatable("screen.itemstash.empty");
            context.drawText(textRenderer, empty, (width - textRenderer.getWidth(empty)) / 2, height / 2, 0xA0A0A0, false);
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

            context.drawItem(entry.stack(), left, y + 7);
            context.drawText(textRenderer, entry.stack().getName(), left + 24, y + 6, 0xFFFFFF, false);
            context.drawText(textRenderer, Text.translatable("screen.itemstash.count", entry.count()), left + 24, y + 18, 0xA0FFA0, false);
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

    private void sendAction(int index, String action) {
        ClientPlayNetworking.send(new ItemstashActionPayload(index, action));
    }
}
