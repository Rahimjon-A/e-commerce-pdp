package com.shop.bot.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyKeyboardMarkupFactory {

    public static ReplyKeyboardMarkup replyKeyboardMarkup(List<String> list, int btnCount) {
        ReplyKeyboardMarkup reply = new ReplyKeyboardMarkup();
        reply.setResizeKeyboard(true);
        reply.setSelective(true);
        reply.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();
        reply.setKeyboard(rows);

        int i = 0;
        KeyboardRow row = new KeyboardRow();
        for (String s : list) {
            i++;
            row.add(new KeyboardButton(s));

            if(i % btnCount == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }

        if(!row.isEmpty()) {
            rows.add(row);
        }

        return reply;
    }
}
