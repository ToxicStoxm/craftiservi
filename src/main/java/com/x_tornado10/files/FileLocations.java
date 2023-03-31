package com.x_tornado10.files;

import com.x_tornado10.craftiservi;

import java.io.InputStream;

public class FileLocations {

    private craftiservi plugin = craftiservi.getInstance();


    //Plugin Data Folder Path
    private String plpath = plugin.getDataFolder().getAbsolutePath();


    //File Paths
    private String data = plpath + "/data.yml";
    private String playerlist = plpath + "/playerlist.txt";
    private InputStream dataf = plugin.getResource("data.yml");
    private String xpsaveareas = plpath + "/xp_save_areas.txt";
    private String xpsaveareasbackup = plpath + "/backups/xp_save_areas.txt";
    private String playersinsavearea = plpath + "/players_in_save_area.txt";
    private String playersinsaveareabackup = plpath + "/backups/players_in_save_area.txt";
    private String player_inv_saves = plpath + "/player_inv_saves.yml";
    private String player_inv_saves_backup = plpath + "/backups/player_inv_saves_backup.yml";

    private String backupdir = plpath + "/backups";


    //YML Paths
    private String ymlInstalledOn = "stats.plugin.installed-on";
    private String ymlVersion = "stats.plugin.version";
    private String ymlTimesStartedReloaded = "stats.server.times-started-reloaded";
    private String ymlRegisteredPlayers = "stats.server.registered-players";


    public String getPlpath() {
        return plpath;
    }

    public void setPlpath(String plpath) {
        this.plpath = plpath;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPlayerlist() {
        return playerlist;
    }

    public void setPlayerlist(String playerlist) {
        this.playerlist = playerlist;
    }

    public InputStream getDataf() {
        return dataf;
    }

    public void setDataf(InputStream dataf) {
        this.dataf = dataf;
    }

    public String getYmlInstalledOn() {
        return ymlInstalledOn;
    }

    public void setYmlInstalledOn(String ymlInstalledOn) {
        this.ymlInstalledOn = ymlInstalledOn;
    }

    public String getYmlVersion() {
        return ymlVersion;
    }

    public void setYmlVersion(String ymlVersion) {
        this.ymlVersion = ymlVersion;
    }

    public String getYmlTimesStartedReloaded() {
        return ymlTimesStartedReloaded;
    }

    public void setYmlTimesStartedReloaded(String ymlTimesStartedReloaded) {
        this.ymlTimesStartedReloaded = ymlTimesStartedReloaded;
    }

    public String getYmlRegisteredPlayers() {
        return ymlRegisteredPlayers;
    }

    public void setYmlRegisteredPlayers(String ymlRegisteredPlayers) {
        this.ymlRegisteredPlayers = ymlRegisteredPlayers;
    }

    public String getXpsaveareas() {
        return xpsaveareas;
    }

    public void setXpsaveareas(String xpsaveareas) {
        this.xpsaveareas = xpsaveareas;
    }

    public String getXpsaveareasbackup() {
        return xpsaveareasbackup;
    }

    public void setXpsaveareasbackup(String xpsaveareasbackup) {
        this.xpsaveareasbackup = xpsaveareasbackup;
    }

    public String getBackupdir() {
        return backupdir;
    }

    public void setBackupdir(String backupdir) {
        this.backupdir = backupdir;
    }

    public String getPlayersinsavearea() {
        return playersinsavearea;
    }

    public void setPlayersinsavearea(String playersinsavearea) {
        this.playersinsavearea = playersinsavearea;
    }

    public String getPlayersinsaveareabackup() {
        return playersinsaveareabackup;
    }

    public void setPlayersinsaveareabackup(String playersinsaveareabackup) {
        this.playersinsaveareabackup = playersinsaveareabackup;
    }

    public String getPlayer_inv_saves() {
        return player_inv_saves;
    }

    public void setPlayer_inv_saves(String player_inv_saves) {
        this.player_inv_saves = player_inv_saves;
    }

    public String getPlayer_inv_saves_backup() {
        return player_inv_saves_backup;
    }

    public void setPlayer_inv_saves_backup(String player_inv_saves_backup) {
        this.player_inv_saves_backup = player_inv_saves_backup;
    }
}
