package com.shop.bot.factory;

import com.shop.bot.factory.wrapper.RecordWrapper;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class InlineKeyboardMarkupFactory<T> {
    private final List<T> records;
    private final int colCount;

    public InlineKeyboardMarkup inlineKeyboardMarkup() {
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        inline.setKeyboard(rows);

        int i = 0;
        List<InlineKeyboardButton> cols = new ArrayList<>();
        for (T record : records) {
            i++;
            RecordWrapper wrapper = wrapper(record);
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(wrapper.getName());
            btn.setCallbackData(wrapper.getCommand() + wrapper.getId());
            cols.add(btn);

            if (i % colCount == 0) {
                rows.add(cols);
                cols = new ArrayList<>();
            }

        }

        if(!cols.isEmpty()) {
            rows.add(cols);
        }

        return inline;
    }

    public abstract RecordWrapper wrapper(T t);
}
