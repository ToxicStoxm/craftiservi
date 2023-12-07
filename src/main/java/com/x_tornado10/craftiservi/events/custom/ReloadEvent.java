package com.x_tornado10.craftiservi.events.custom;

import com.x_tornado10.craftiservi.utils.custom_data.reload.CustomData;
import com.x_tornado10.craftiservi.utils.custom_data.reload.CustomDataWrapper;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ReloadEvent extends Event {

    CustomDataWrapper customDataWrapper;

    public ReloadEvent(CustomDataWrapper customDataWrapper) {
        this.customDataWrapper = customDataWrapper;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CustomData getData(int index) {
        return customDataWrapper.getCustomData(index);
    }
}