package com.x_tornado10.chat.filters;

import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.utils.CDID;
import com.x_tornado10.utils.CustomData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class MsgFilter extends CustomFilter implements Listener {
    private static List<String> blockedStrings;
    public static boolean enabled;

    public void registerFilter() {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addFilter(this);
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

    @EventHandler
    public void onReload(ReloadEvent e) {
        CustomData CFilterData = e.getData(CDID.CFILTER_DATA);
        enabled = CFilterData.getB(0);
        blockedStrings = CFilterData.getLS(0);
        registerFilter();
    }
}