package com.shop.bot.botUtil;

import java.io.File;

public class BotUtil {

    public static File createImagePath(String name) {
        String pName = name.replaceAll("\\s+", "");
        String path = "./images/" + pName + ".png";
        File file = new File(path);

        if (file.exists()) {
            return file;
        } else {
            return new File("./images/default.png");
        }
    }
}
