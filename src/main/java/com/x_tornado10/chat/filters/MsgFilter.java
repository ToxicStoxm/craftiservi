package com.x_tornado10.chat.filters;


import com.x_tornado10.craftiservi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.List;

public class MsgFilter extends CustomFilter {

    private final craftiservi plugin = craftiservi.getInstance();
    private static List<String> blockedStrings;
    public static boolean enabled;

    public void registerFilter() {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addFilter(this);
    }

    public MsgFilter(List<String> blockedStrings) {

        setBlockedStrings(blockedStrings);

    }

    @Override
    protected Result logResult(String string) {

        if (!enabled) {return Result.NEUTRAL;}

        for (String str : blockedStrings) {

            if (string.contains(str)) {

                return Result.DENY;
            }

        }

        return Result.NEUTRAL;
    }

    @Override
    public String getName() {
        return null;
    }

    public void setBlockedStrings(List<String> blockedStrings) {

        MsgFilter.blockedStrings = blockedStrings;

    }
}