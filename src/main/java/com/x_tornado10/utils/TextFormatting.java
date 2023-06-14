package com.x_tornado10.utils;

import java.util.concurrent.TimeUnit;

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

    public String getDurationBreakdown(long millis) {
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append("d ");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append("min ");
        }
        if (seconds > 0) {
            sb.append(seconds);
            sb.append("s");
        }

        return(sb.toString());
    }

}
