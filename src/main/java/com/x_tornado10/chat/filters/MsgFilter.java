package com.x_tornado10.chat.filters;


import com.x_tornado10.craftiservi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.List;

public final class MsgFilter extends CustomFilter {

    private final craftiservi plugin = craftiservi.getInstance();
    private final List<String> blockedStrings = plugin.getBlockedStrings();

    public void registerFilter() {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addFilter(this);
    }

    @Override
    protected Result logResult(String string) {

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
}