package com.x_tornado10.handlers;

import org.bukkit.configuration.Configuration;

public class ConfigHandler{

    private String prefix;
    private Configuration config;

    public ConfigHandler (String prefix, Configuration config) {

        this.config = config;
        this.prefix = prefix;

    }


    public void updateConfig() {

        config.options().copyDefaults(true);

    }


}
