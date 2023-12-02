package com.x_tornado10.events.custom;

import com.x_tornado10.utils.custom_data.CustomData;
import com.x_tornado10.utils.custom_data.CustomDataWrapper;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReloadEvent extends Event {

    CustomDataWrapper customDataWrapper;

    public ReloadEvent(CustomDataWrapper customDataWrapper) {
        this.customDataWrapper = customDataWrapper;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CustomData getData(int index) {
        return customDataWrapper.getCustomData(index);
    }
}