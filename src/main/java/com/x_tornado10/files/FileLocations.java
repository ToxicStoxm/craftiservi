package com.x_tornado10.files;

import com.x_tornado10.craftiservi;

import java.io.InputStream;

public class FileLocations {

    private final craftiservi plugin = craftiservi.getInstance();


    //Plugin Data Folder Path
    private final String plpath = plugin.getDataFolder().getAbsolutePath();


    //File Paths
    private final String data = plpath + "/data.yml";
    private final String playerlist = plpath + "/playerlist.yml";
    private final InputStream dataf = plugin.getResource("data.yml");
    private final String xpsaveareas = plpath + "/xp_save_areas.yml";
    private final String xpsaveareasbackup = plpath + "/backups/xp_save_areas_backup.yml";
    private final String playersinsavearea = plpath + "/players_in_save_area.yml";
    private final String playersinsaveareabackup = plpath + "/backups/players_in_save_area_backup.yml";
    private final String player_inv_saves = plpath + "/player_inv_saves.yml";
    private final String player_inv_saves_backup = plpath + "/backups/player_inv_saves_backup.yml";
    private final String backupdir = plpath + "/backups";
    private final String config = plpath + "/config.yml";
    private final String backup_config = plpath + "/backups/Backup_config.yml";

    //YML Paths
    private final String ymlInstalledOn = "stats.plugin.installed-on";
    private final String ymlVersion = "stats.plugin.version";
    private final String ymlTimesStartedReloaded = "stats.server.times-started-reloaded";
    private final String ymlRegisteredPlayers = "stats.server.registered-players";

    //config paths
    private final String BlockedStrings = "Craftiservi.Chat.Filter.BlockedStrings";
    private final String velocity_multiplier = "Craftiservi.Gadgets.Movement.Jump-Pads.velocity-multiplier";
    private final String Y_velocity = "Craftiservi.Gadgets.Movement.Jump-Pads.Y-velocity";
    private final String Y_velocity_g = "Craftiservi.Gadgets.Movement.Grappling-hook.Y-velocity";
    private final String Version = "Craftiservi.Plugin-Version";
    private final String display_debug = "Craftiservi.Console/Log.display-debug";
    private final String disable_logger = "Craftiservi.Console/Log.disable-logger";
    private final String short_prefix = "Craftiservi.short-Prefix";
    private final String use_custom_Prefix = "Craftiservi.use-custom-Prefix";
    private final String custom_Prefix = "Craftiservi.custom-Prefix";



    public String getPlpath() {
        return plpath;
    }
    public String getData() {
        return data;
    }
    public String getPlayerlist() {
        return playerlist;
    }
    public InputStream getDataf() {
        return dataf;
    }
    public String getYmlInstalledOn() {
        return ymlInstalledOn;
    }
    public String getYmlVersion() {
        return ymlVersion;
    }
    public String getYmlTimesStartedReloaded() {
        return ymlTimesStartedReloaded;
    }
    public String getYmlRegisteredPlayers() {
        return ymlRegisteredPlayers;
    }
    public String getXpsaveareas() {
        return xpsaveareas;
    }
    public String getXpsaveareasbackup() {
        return xpsaveareasbackup;
    }
    public String getBackupdir() {
        return backupdir;
    }
    public String getPlayersinsavearea() {
        return playersinsavearea;
    }
    public String getPlayersinsaveareabackup() {
        return playersinsaveareabackup;
    }
    public String getPlayer_inv_saves() {
        return player_inv_saves;
    }
    public String getPlayer_inv_saves_backup() {
        return player_inv_saves_backup;
    }
    public String getBlockedStrings() {
        return BlockedStrings;
    }
    public String getVelocity_multiplier() {
        return velocity_multiplier;
    }
    public String getY_velocity() {
        return Y_velocity;
    }
    public String getY_velocity_g() {
        return Y_velocity_g;
    }
    public String getVersion() {
        return Version;
    }
    public String getDisplay_debug() {
        return display_debug;
    }
    public String getDisable_logger() {
        return disable_logger;
    }
    public String getShort_prefix() {
        return short_prefix;
    }
    public String getUse_custom_Prefix() {
        return use_custom_Prefix;
    }
    public String getCustom_Prefix() {
        return custom_Prefix;
    }
    public String getConfig() {
        return config;
    }
    public String getBackup_config() {
        return backup_config;
    }
}
