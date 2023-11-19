package com.x_tornado10.utils;

import com.x_tornado10.craftiservi;

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
    public String getAfk_players() {return "/afkPlayers.yml";}
    public String getConfig() {return plpath + "/config.yml";}
    public String getBackup_config() {return plpath + "/backups/Backup_config.yml";}

    //Config Paths
    public String getJPads_prevent_falldmg() {return "Craftiservi.Gadgets.Movement.Jump-Pads.prevent-fall-damage";}
    public String getJPads_cooldown() {return "Craftiservi.Gadgets.Movement.Jump-Pads.cooldown";}
    public String getBlockedStrings() {return "Craftiservi.Chat.Filter.BlockedStrings";}
    public String getVelocity_multiplier() {return "Craftiservi.Gadgets.Movement.Jump-Pads.velocity-multiplier";}
    public String getY_velocity() {return "Craftiservi.Gadgets.Movement.Jump-Pads.Y-velocity";}
    public String getY_velocity_g() {return "Craftiservi.Gadgets.Movement.Grappling-hook.Y-velocity";}
    public String getVersion() {return "Craftiservi.Plugin-Version";}
    public String getDisplay_debug() {return "Craftiservi.Console/Log.display-debug";}
    public String getDisable_logger() {return "Craftiservi.Console/Log.disable-logger";}
    public String getShort_prefix() {return "Craftiservi.short-Prefix";}
    public String getUse_custom_Prefix() {return "Craftiservi.use-custom-Prefix";}
    public String getCustom_Prefix() {return "Craftiservi.custom-Prefix";}
    public String getCustom_dc_Prefix() {return "Craftiservi.custom-discord-Prefix";}
    public String getChat_filter_enabled() {return "Craftiservi.Chat.Filter.Enabled";}
    public String getJump_pads_enabled() {return "Craftiservi.Gadgets.Movement.Jump-Pads.Enabled";}
    public String getGrappling_hook_enabled() {return "Craftiservi.Gadgets.Movement.Grappling-hook.Enabled";}
    public String getCommands_invsave_enabled() {return "Craftiservi.Commands.InvSave.Enabled";}
    public String getCommands_xparea_enabled() {return "Craftiservi.Commands.XpArea.Enabled";}
    public String getCommands_firstjoin_enabled() {return "Craftiservi.Commands.FirstJoin.Enabled";}
    public String getAfk_checker_enabled() {return "Craftiservi.Afk-Checker.Enabled";}
    public String getAfk_checker_afk_time() {return "Craftiservi.Afk-Checker.Afk-Time";}
    public String getAfk_checker_allow_afk_chat() {return "Craftiservi.Afk-Checker.allow-afk-chat";}
    public String getAfk_checker_exclude() {return "Craftiservi.Afk-Checker.Exclude";}
    public String getAfk_checker_exclude_enabled() {return "Craftiservi.Afk-Checker.enable-exclude";}
    public String getAfk_checker_broadcastAFK_enabled() {return "Craftiservi.Afk-Checker.BroadcastAFK";}
    public String getAfk_checker_broadcastTime_enabled() {return "Craftiservi.Afk-Checker.BroadcastAFK-Time";}
    public String getAfk_checker_personal_time_enabled() {return "Craftiservi.Afk-Checker.Display-Personal-Time";}
    public String getAfk_checker_afkEffects_enabled() {return "Craftiservi.Afk-Checker.Afk-Effects.Enabled";}
    public String getAfk_checker_effects_invincible() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invincible.Enabled";}
    public String getAfk_checker_effects_invincible_2() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invincible-2.Enabled";}
    public String getAfk_checker_effects_invincible_custom() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invincible-Custom.Enabled";}
    public String getAfk_checker_effects_invincibleC_dTypes() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invincible-Custom.Damage-types";}
    public String getAfk_checker_effects_invisible() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invisible.Enabled";}
    public String getAfk_checker_effects_noCollision() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.No-Collision.Enabled";}
    public String getAfk_checker_effects_AfkNameTag() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Afk-NameTag.Enabled";}
    public String getAfk_checker_effects_AfkNameTag_prefix() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Afk-NameTag.Prefix";}
    public String getGrappling_hook_cooldown() {return "Craftiservi.Gadgets.Movement.Grappling-hook.cooldown";}
    public String getGrappling_hook_prevent_falldmg() {return "Craftiservi.Gadgets.Movement.Grappling-hook.prevent-fall-damage";}
    public String getAfk_checker_effects_invisible_hholo() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invisible.Head-Hologram.Enabled";}
    public String getAfk_checker_effects_invisible_hholo_fullTag() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invisible.Head-Hologram.Hologram-FullPlayerNameTag";}
    public String getAfk_checker_effects_AfkNameTag_suffix() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Afk-NameTag.Suffix";}
    public String getAfk_checker_effects_invisible_usePEffect() {return "Craftiservi.Afk-Checker.Afk-Effects.Effects.Invisible.Use-Potion-Effect";}
    public String getAfk_checker_allow_hit_mob() {return "Craftiservi.Afk-Checker.allow-hit-mobs";}
    public String getAfk_checker_allow_hit_player() {return "Craftiservi.Afk-Checker.allow-hit-players";}
    public String getAfk_checker_allow_break_block() {return "Craftiservi.Afk-Checker.allow-break-blocks";}
    public String getAfk_checker_allow_place_block() {return "Craftiservi.Afk-Checker.allow-place-blocks";}
    public String getAfk_checker_stop_creeper_target() {return "Craftiservi.Afk-Checker.stop-creeper-target";}
    public String getAfk_checker_kill_creeper_on_target() {return "Craftiservi.Afk-Checker.kill-creeper-on-target";}

}