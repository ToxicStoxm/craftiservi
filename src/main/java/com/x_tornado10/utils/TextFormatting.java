package com.x_tornado10.utils;

public class TextFormatting {

    public String stripColorCodes(String s) {
        return s.replace("§0", "")
                .replace("§1", "")
                .replace("§2", "")
                .replace("§3", "")
                .replace("§4", "")
                .replace("§5", "")
                .replace("§6", "")
                .replace("§7", "")
                .replace("§8", "")
                .replace("§9", "")
                .replace("§a", "")
                .replace("§b", "")
                .replace("§c", "")
                .replace("§d", "")
                .replace("§e", "")
                .replace("§f", "")
                .replace("§g", "");
    }
    public String stripFormattingCodes(String s) {
        return s.replace("§u", "")
                .replace("§l", "")
                .replace("§o", "")
                .replace("§m", "")
                .replace("§k", "")
                .replace("§r", "");
    }
    public String stripColorAndFormattingCodes(String s) {
        return stripFormattingCodes(stripColorCodes(s));
    }

}
