package com.x_tornado10.handlers;

import com.x_tornado10.craftiservi;
import com.x_tornado10.files.FileLocations;
import com.x_tornado10.files.FileManager;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.utils.TextFormatting;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler{
    private Configuration config;
    private FileLocations fl;
    private FileManager fm;
    private craftiservi p = craftiservi.getInstance();
    private long cooldown = System.currentTimeMillis() - 6000;

    public ConfigHandler (Configuration config, FileLocations fl, FileManager fm) {

        this.config = config;
        this.fl = fl;
        this.fm = fm;

    }

    public List<String> getBlockedStrings() {

        return new ArrayList<>(config.getStringList(fl.getBlockedStrings()));

    }

    public double getVelocity_multiplier() {
        return getDouble(fl.getVelocity_multiplier());
    }
    public double getY_velocity() {
        return getDouble(fl.getY_velocity());
    }
    public double getY_velocity_g() {
        return getDouble(fl.getY_velocity_g());
    }

    public boolean getDisplay_debug() {return getBoolean(fl.getDisplay_debug());}
    public boolean getShort_prefix() {return getBoolean(fl.getShort_prefix());}
    public boolean getDisable_logger() {return !getBoolean(fl.getDisable_logger());}
    public boolean getUse_custom_Prefix() {return getBoolean(fl.getUse_custom_Prefix());}
    public String getCustom_Prefix() {return getString(fl.getCustom_Prefix());}

    public void setVersion(String version) {config.set(fl.getVersion(), version);}

    public double getDouble(String path) {
        return config.getDouble(path);
    }
    public int getInt(String path) {
        return config.getInt(path);
    }
    public String getString(String path) {
        return config.getString(path);
    }
    public boolean getBoolean(String path) {return config.getBoolean(path);}

    public void updateConfig() {

        config.options().copyDefaults(true);

    }

    public boolean reloadConfig(boolean force) {

        long timeElapsed = System.currentTimeMillis() - cooldown;

        if (timeElapsed >= 5000 || force) {

            p.reloadConfig();
            this.config = p.getConfig();

            setVersion(p.getDescription().getVersion());
            p.setPrefix(this, new TextFormatting());
            Logger.setDebug(getDisplay_debug());
            Logger.setEnabled(getDisable_logger());
            p.getMsgFilter().setBlockedStrings(getBlockedStrings());
            p.getMsgFilter().registerFilter();
            p.getJumpPads().updateValues(getY_velocity(), getVelocity_multiplier());
            p.getGraplingHookListener().updateValues(getY_velocity_g());
            p.getCustomLogger().upDateValues(p.getPrefix());
            p.getPlayerMessages().upDateValues(p.getColorprefix());

            p.saveConfig();

            this.cooldown = System.currentTimeMillis();
            return true;

        } else {
            return false;
        }

    }

    public boolean resetConfig() {
        if (backupConfig() && fm.deleteConfig()) {
            p.saveDefaultConfig();
            reloadConfig(true);
            return true;
        } else {

            return false;

        }

    }

    public boolean backupConfig() {

        FileConfiguration backup_config = new YamlConfiguration();

        try {
            backup_config.load(fl.getConfig());
            backup_config.save(fl.getBackup_config());
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            return false;
        }

    }

}
