package com.x_tornado10.handlers;

import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler{

    private String prefix;
    private Configuration config;

    public ConfigHandler (String prefix, Configuration config) {

        this.config = config;
        this.prefix = prefix;

    }

    public List<String> getBlockedStrings() {

        return new ArrayList<>(config.getStringList("BlockedStrings"));

    }


    public void updateConfig() {

        config.options().copyDefaults(true);

    }


}
