package com.x_tornado10.utils;

import com.x_tornado10.craftiservi;

public class Paths {

    private final craftiservi plugin = craftiservi.getInstance();


    //Plugin Data Folder Path
    private final String plpath = plugin.getDataFolder().getAbsolutePath();


    //File Paths
    private final String playerlist = plpath + "/playerlist.yml";
    private final String xpsaveareas = plpath + "/xp_save_areas.yml";
    private final String xpsaveareasbackup = plpath + "/backups/xp_save_areas_backup.yml";
    private final String playersinsavearea = plpath + "/players_in_save_area.yml";
    private final String playersinsaveareabackup = plpath + "/backups/players_in_save_area_backup.yml";
    private final String player_inv_saves = plpath + "/player_inv_saves.yml";
    private final String player_inv_saves_backup = plpath + "/backups/player_inv_saves_backup.yml";
    private final String backupdir = plpath + "/backups";
    private final String config = plpath + "/config.yml";
    private final String backup_config = plpath + "/backups/Backup_config.yml";
    private final String afk_players = plpath + "/afkPlayers.yml";

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
    private final String custom_dc_Prefix = "Craftiservi.custom-discord-Prefix";
    private final String chat_filter_enabled = "Craftiservi.Chat.Filter.Enabled";
    private final String jump_pads_enabled = "Craftiservi.Gadgets.Movement.Jump-Pads.Enabled";
    private final String grappling_hook_enabled = "Craftiservi.Gadgets.Movement.Grappling-hook.Enabled";
    private final String grappling_hook_cooldown = "Craftiservi.Gadgets.Movement.Grappling-hook.cooldown";
    private final String grappling_hook_prevent_falldmg = "Craftiservi.Gadgets.Movement.Grappling-hook.prevent-fall-damage";
    private final String jPads_cooldown = "Craftiservi.Gadgets.Movement.Jump-Pads.cooldown";
    private final String jPads_prevent_falldmg = "Craftiservi.Gadgets.Movement.Jump-Pads.prevent-fall-damage";
    private final String commands_invsave_enabled = "Craftiservi.Commands.InvSave.Enabled";
    private final String commands_xparea_enabled = "Craftiservi.Commands.XpArea.Enabled";
    private final String commands_firstjoin_enabled = "Craftiservi.Commands.FirstJoin.Enabled";
    private final String afk_checker_enabled = "Craftiservi.Afk-Checker.Enabled";
    private final String afk_checker_afk_time = "Craftiservi.Afk-Checker.Afk-Time";
    private final String afk_checker_allow_afk_chat = "Craftiservi.Afk-Checker.allow-afk-chat";
    private final String afk_checker_exclude = "Craftiservi.Afk-Checker.Exclude";
    private final String afk_checker_exclude_enabled = "Craftiservi.Afk-Checker.enable-exclude";
    private final String afk_checker_broadcastAFK_enabled = "Craftiservi.Afk-Checker.BroadcastAFK";
    private final String afk_checker_broadcastTime_enabled = "Craftiservi.Afk-Checker.BroadcastAFK-Time";
    private final String afk_checker_personal_time_enabled = "Craftiservi.Afk-Checker.Display-Personal-Time";
    private final String afk_checker_afkEffects_enabled = "Craftiservi.Afk-Checker.Afk-Effects.Enabled";
    private final String afk_checker_effects_invincible = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invincible.Enabled";
    private final String afk_checker_effects_invincible_2 = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invincible-2.Enabled";
    private final String afk_checker_effects_invincible_custom = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invincible-Custom.Enabled";
    private final String afk_checker_effects_invincibleC_dTypes = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invincible-Custom.Damage-types";
    private final String afk_checker_effects_invisible = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invisible.Enabled";
    private final String afk_checker_effects_invisible_usePEffect = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invisible.Use-Potion-Effect";
    private final String afk_checker_effects_invisible_hholo = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invisible.Head-Hologram.Enabled";
    private final String afk_checker_effects_invisible_hholo_fullTag = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invisible.Head-Hologram.Hologram-FullPlayerNameTag";
    private final String afk_checker_effects_noCollision = "Craftiservi.Afk-Checker.Afk-Effects.Effects.No-Collision.Enabled";
    private final String afk_checker_effects_AfkNameTag = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Afk-NameTag.Enabled";
    private final String afk_checker_effects_AfkNameTag_prefix = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Afk-NameTag.Prefix";

    public String getJPads_cooldown() {
        return jPads_cooldown;
    }
    public String getJPads_prevent_falldmg() {
        return jPads_prevent_falldmg;
    }
    private final String afk_checker_effects_AfkNameTag_suffix = "Craftiservi.Afk-Checker.Afk-Effects.Effects.Afk-NameTag.Suffix";
    public String getPlpath() {
        return plpath;
    }
    public String getPlayerlist() {
        return playerlist;
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
    public String getCustom_dc_Prefix() {
        return custom_dc_Prefix;
    }
    public String getConfig() {
        return config;
    }
    public String getBackup_config() {
        return backup_config;
    }
    public String getChat_filter_enabled() {
        return chat_filter_enabled;
    }
    public String getJump_pads_enabled() {
        return jump_pads_enabled;
    }
    public String getGrappling_hook_enabled() {
        return grappling_hook_enabled;
    }
    public String getCommands_invsave_enabled() {
        return commands_invsave_enabled;
    }
    public String getCommands_xparea_enabled() {
        return commands_xparea_enabled;
    }
    public String getCommands_firstjoin_enabled() {
        return commands_firstjoin_enabled;
    }
    public String getAfk_checker_enabled() {
        return afk_checker_enabled;
    }
    public String getAfk_checker_afk_time() {
        return afk_checker_afk_time;
    }
    public String getAfk_checker_allow_afk_chat() {
        return afk_checker_allow_afk_chat;
    }
    public String getAfk_checker_exclude() {
        return afk_checker_exclude;
    }
    public String getAfk_checker_exclude_enabled() {
        return afk_checker_exclude_enabled;
    }
    public String getAfk_checker_broadcastAFK_enabled() {
        return afk_checker_broadcastAFK_enabled;
    }
    public String getAfk_checker_broadcastTime_enabled() {
        return afk_checker_broadcastTime_enabled;
    }
    public String getAfk_checker_personal_time_enabled() {
        return afk_checker_personal_time_enabled;
    }
    public String getAfk_players() {
        return afk_players;
    }
    public String getAfk_checker_afkEffects_enabled() {
        return afk_checker_afkEffects_enabled;
    }
    public String getAfk_checker_effects_invincible() {
        return afk_checker_effects_invincible;
    }
    public String getAfk_checker_effects_invincible_2() {
        return afk_checker_effects_invincible_2;
    }
    public String getAfk_checker_effects_invincible_custom() {
        return afk_checker_effects_invincible_custom;
    }
    public String getAfk_checker_effects_invincibleC_dTypes() {
        return afk_checker_effects_invincibleC_dTypes;
    }
    public String getAfk_checker_effects_invisible() {
        return afk_checker_effects_invisible;
    }
    public String getAfk_checker_effects_noCollision() {
        return afk_checker_effects_noCollision;
    }
    public String getAfk_checker_effects_AfkNameTag() {
        return afk_checker_effects_AfkNameTag;
    }
    public String getAfk_checker_effects_AfkNameTag_prefix() {
        return afk_checker_effects_AfkNameTag_prefix;
    }
    public String getGrappling_hook_cooldown() {
        return grappling_hook_cooldown;
    }
    public String getGrappling_hook_prevent_falldmg() {
        return grappling_hook_prevent_falldmg;
    }
    public String getAfk_checker_effects_invisible_hholo() {
        return afk_checker_effects_invisible_hholo;
    }
    public String getAfk_checker_effects_invisible_hholo_fullTag() {
        return afk_checker_effects_invisible_hholo_fullTag;
    }
    public String getAfk_checker_effects_AfkNameTag_suffix() {
        return afk_checker_effects_AfkNameTag_suffix;
    }
    public String getAfk_checker_effects_invisible_usePEffect() {
        return afk_checker_effects_invisible_usePEffect;
    }
}
