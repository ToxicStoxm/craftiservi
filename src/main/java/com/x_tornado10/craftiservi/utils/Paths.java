package com.x_tornado10.craftiservi.utils;

import com.x_tornado10.craftiservi.craftiservi;

public class Paths {

    private final craftiservi plugin = craftiservi.getInstance();
    private final String plpath = plugin.getDataFolder().getAbsolutePath();

    //File Paths
    public String getPlpath() {return plpath;}
    public String getPlayerlist() {return plpath + "/playerlist.yml";}
    public String getXpsaveareas() {return plpath + "/xp_save_areas.yml";}
    public String getXpsaveareasbackup() {return plpath + "/backups/xp_save_areas_backup.yml";}
    public String getBackupdir() {return plpath + "/backups";}
    public String getPlayersinsavearea() {return plpath + "/players_in_save_area.yml";}
    public String getPlayersinsaveareabackup() {return plpath + "/backups/players_in_save_area_backup.yml";}
    public String getPlayer_inv_saves() {return plpath + "/player_inv_saves.yml";}
    public String getPlayer_inv_saves_backup() {return plpath + "/backups/player_inv_saves_backup.yml";}
    public String getAfk_players() {return plpath + "/afkPlayers.yml";}
    public String getRestoreRequests() {return plpath + "/restoreRequests.yml";}
    public String getConfig() {return plpath + "/config.yml";}
    public String getBackup_config() {return plpath + "/backups/Backup_config.yml";}

    //Config Paths
    public String getBlockedStrings() {return "Craftiservi.Chat.Filter.BlockedStrings";}
    public String getVersion() {return "Craftiservi.Plugin-Version";}
    public String getDisplay_debug() {return "Craftiservi.Console/Log.display-debug";}
    public String getDisable_logger() {return "Craftiservi.Console/Log.disable-logger";}
    public String getShort_prefix() {return "Craftiservi.short-Prefix";}
    public String getUse_custom_Prefix() {return "Craftiservi.use-custom-Prefix";}
    public String getCustom_Prefix() {return "Craftiservi.custom-Prefix";}
    public String getAdmin_Chat() {return "Craftiservi.Chat.Admin-Chat.Enabled";}
    public String getAdmin_Chat_Auto_Assign() {return "Craftiservi.Chat.Admin-Chat.Auto-Assign";}
    public String getAdmin_Chat_Admins() {return "Craftiservi.Chat.Admin-Chat.Admins";}
    public String getAdmin_Chat_short_prefix() {return "Craftiservi.Chat.Admin-Chat.short-Prefix";}
    public String getAdmin_Chat_useCustomPrefix() {return "Craftiservi.Chat.Admin-Chat.use-custom-Prefix";}
    public String getAdmin_Chat_customPrefix() {return "Craftiservi.Chat.Admin-Chat.custom-Prefix";}
    public String getAdmin_Chat_queue() {return "Craftiservi.Chat.Admin-Chat.Queue.Enabled";}
    public String getAdmin_Chat_queueTime() {return "Craftiservi.Chat.Admin-Chat.Queue.Queue-Time";}
    public String getAdmin_Chat_queueLimit() {return "Craftiservi.Chat.Admin-Chat.Queue.Queue-Message-Limit";}
    public String getCustom_dc_Prefix() {return "Craftiservi.custom-discord-Prefix";}
    public String getChat_filter_enabled() {return "Craftiservi.Chat.Filter.Enabled";}
    public String getCommands_invsave_enabled() {return "Craftiservi.Commands-Features.InvSave.Enabled";}
    public String get_invsave_restore_requests() {return "Craftiservi.Commands-Features.InvSave.Restore-Requests.Enabled";}
    public String get_invsave_restore_requests_cooldown() {return "Craftiservi.Commands-Features.InvSave.Restore-Requests.Restore-Cooldown";}
    public String get_invsave_autoInvSave() {return "Craftiservi.Commands-Features.InvSave.Auto-InvSave-Death.Enabled";}
    public String get_invsave_autoInvSave_format() {return "Craftiservi.Commands-Features.InvSave.Auto-InvSave-Death.Inv-Name-Format";}
    public String getCommands_xparea_enabled() {return "Craftiservi.Commands-Features.XpArea.Enabled";}
    public String getCommands_firstjoin_enabled() {return "Craftiservi.Commands-Features.FirstJoin.Enabled";}
    public String getAfk_checker_enabled() {return "Craftiservi.Commands-Features.Afk-Checker.Enabled";}
    public String getAfk_checker_afk_time() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Time";}
    public String getAfk_checker_allow_afk_chat() {return "Craftiservi.Commands-Features.Afk-Checker.allow-afk-chat";}
    public String getAfk_checker_exclude() {return "Craftiservi.Commands-Features.Afk-Checker.Exclude";}
    public String getAfk_checker_exclude_enabled() {return "Craftiservi.Commands-Features.Afk-Checker.enable-exclude";}
    public String getAfk_checker_broadcastAFK_enabled() {return "Craftiservi.Commands-Features.Afk-Checker.BroadcastAFK";}
    public String getAfk_checker_broadcastTime_enabled() {return "Craftiservi.Commands-Features.Afk-Checker.BroadcastAFK-Time";}
    public String getAfk_checker_personal_time_enabled() {return "Craftiservi.Commands-Features.Afk-Checker.Display-Personal-Time";}
    public String getAfk_checker_afkEffects_enabled() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Enabled";}
    public String getAfk_checker_effects_invincible() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Invincible.Enabled";}
    public String getAfk_checker_effects_invincible_2() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Invincible-2.Enabled";}
    public String getAfk_checker_effects_invincible_custom() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Invincible-Custom.Enabled";}
    public String getAfk_checker_effects_invincibleC_dTypes() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Invincible-Custom.Damage-types";}
    public String getAfk_checker_effects_invisible() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Invisible.Enabled";}
    public String getAfk_checker_effects_noCollision() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.No-Collision.Enabled";}
    public String getAfk_checker_effects_AfkNameTag() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Afk-NameTag.Enabled";}
    public String getAfk_checker_effects_AfkNameTag_prefix() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Afk-NameTag.Prefix";}
    public String getAfk_checker_effects_invisible_hholo() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Invisible.Head-Hologram.Enabled";}
    public String getAfk_checker_effects_invisible_hholo_fullTag() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Invisible.Head-Hologram.Hologram-FullPlayerNameTag";}
    public String getAfk_checker_effects_AfkNameTag_suffix() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Afk-NameTag.Suffix";}
    public String getAfk_checker_effects_invisible_usePEffect() {return "Craftiservi.Commands-Features.Afk-Checker.Afk-Effects.Effects.Invisible.Use-Potion-Effect";}
    public String getAfk_checker_allow_hit_mob() {return "Craftiservi.Commands-Features.Afk-Checker.allow-hit-mobs";}
    public String getAfk_checker_allow_hit_player() {return "Craftiservi.Commands-Features.Afk-Checker.allow-hit-players";}
    public String getAfk_checker_allow_break_block() {return "Craftiservi.Commands-Features.Afk-Checker.allow-break-blocks";}
    public String getAfk_checker_allow_place_block() {return "Craftiservi.Commands-Features.Afk-Checker.allow-place-blocks";}
    public String getAfk_checker_stop_creeper_target() {return "Craftiservi.Commands-Features.Afk-Checker.stop-creeper-target";}
    public String getAfk_checker_kill_creeper_on_target() {return "Craftiservi.Commands-Features.Afk-Checker.kill-creeper-on-target";}
    public String getXpArea_LimitGroups() {return "Craftiservi.Commands-Features.XpArea.LimitGroups";}

}