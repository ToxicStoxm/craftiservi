package com.x_tornado10.managers;

import com.x_tornado10.commands.first_join_command.FirstJoinedCommand;
import com.x_tornado10.commands.first_join_command.FirstJoinedCommandTabCompletion;
import com.x_tornado10.commands.inv_save_point.InventorySavePointCommand;
import com.x_tornado10.commands.inv_save_point.InventorySavePointCommandTabCompletion;
import com.x_tornado10.commands.open_gui_command.OpenGUICommand;
import com.x_tornado10.commands.xp_save_zone_command.XpSaveZoneCommand;
import com.x_tornado10.commands.xp_save_zone_command.XpSaveZoneCommandTabCompletion;
import com.x_tornado10.craftiservi;
import com.x_tornado10.events.listeners.afk_checking.AFKListener;
import com.x_tornado10.events.listeners.inventory.InventoryListener;
import com.x_tornado10.features.afk_protection.AFKChecker;
import com.x_tornado10.utils.custom_data.reload.CustomData;
import com.x_tornado10.utils.custom_data.reload.CustomDataWrapper;
import com.x_tornado10.utils.Paths;
import com.x_tornado10.utils.data.convert.TextFormatting;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private Configuration config;
    private final Paths paths;
    private final FileManager fm;
    private final craftiservi p = craftiservi.getInstance();
    private long cooldown = System.currentTimeMillis() - 6000;

    public ConfigManager(Configuration config, Paths paths, FileManager fm) {

        this.config = config;
        this.paths = paths;
        this.fm = fm;

    }

    public List<String> getBlockedStrings() {

        return new ArrayList<>(config.getStringList(paths.getBlockedStrings()));

    }
    public double getVelocity_multiplier() {
        return config.getDouble(paths.getVelocity_multiplier());
    }
    public double getY_velocity() {
        return config.getDouble(paths.getY_velocity());
    }
    public double getY_velocity_g() {
        return config.getDouble(paths.getY_velocity_g());
    }
    public boolean getDisplay_debug() {return config.getBoolean(paths.getDisplay_debug());}
    public boolean getShort_prefix() {return config.getBoolean(paths.getShort_prefix());}
    public boolean getDisable_logger() {return !config.getBoolean(paths.getDisable_logger());}
    public boolean getUse_custom_Prefix() {return config.getBoolean(paths.getUse_custom_Prefix());}
    public String getCustom_Prefix() {return config.getString(paths.getCustom_Prefix());}
    public String getCustom_dc_Prefix() {return config.getString(paths.getCustom_dc_Prefix());}
    public void setVersion(String version) {config.set(paths.getVersion(), version);}
    public boolean getChatFilterEnabled() {
        return config.getBoolean(paths.getChat_filter_enabled());
    }
    public boolean getJump_pads_enabled() {
        return config.getBoolean(paths.getJump_pads_enabled());
    }
    public boolean getGrappling_hook_enabled() {
        return config.getBoolean(paths.getGrappling_hook_enabled());
    }
    public boolean getCommands_invsave_enabled() {
        return config.getBoolean(paths.getCommands_invsave_enabled());
    }
    public boolean getCommands_invsave_restore_requests() {
        return config.getBoolean(paths.get_invsave_restore_requests());
    }
    public double getCommands_invsave_restore_requests_cooldown() {
        return config.getDouble(paths.get_invsave_restore_requests_cooldown());
    }
    public boolean getCommands_xparea_enabled() {
        return config.getBoolean(paths.getCommands_xparea_enabled());
    }
    public boolean getCommands_firstjoin_enabled() {
        return config.getBoolean(paths.getCommands_firstjoin_enabled());
    }
    public boolean getAfkChecker_enabled() {return config.getBoolean(paths.getAfk_checker_enabled());}
    public int getAfkChecker_afk_time() {return config.getInt(paths.getAfk_checker_afk_time());}
    public boolean getAfkChecker_allow_afk_chat() {return config.getBoolean(paths.getAfk_checker_allow_afk_chat());}
    public boolean getAfkChecker_exclude_enabled() {return config.getBoolean(paths.getAfk_checker_exclude_enabled());}
    public List<String> getAfkChecker_exclude() {
        return new ArrayList<>(getAfkChecker_exclude_enabled() ? config.getStringList(paths.getAfk_checker_exclude()) : new ArrayList<>());
    }
    public boolean getAFKChecker_broadcastAFK() {return config.getBoolean(paths.getAfk_checker_broadcastAFK_enabled());}
    public boolean getAFKChecker_broadcastTime() {return  config.getBoolean(paths.getAfk_checker_broadcastTime_enabled());}
    public boolean getAFKChecker_display_personalTime() {return config.getBoolean(paths.getAfk_checker_personal_time_enabled());}
    public boolean getAFKChecker_effects_enabled() {return config.getBoolean(paths.getAfk_checker_afkEffects_enabled());}
    public boolean getAFKChecker_effects_invincible() {return config.getBoolean(paths.getAfk_checker_effects_invincible());}
    public boolean getAFKChecker_effects_invincible2() {return config.getBoolean(paths.getAfk_checker_effects_invincible_2());}
    public boolean getAFKChecker_effects_invincibleCustom() {return config.getBoolean(paths.getAfk_checker_effects_invincible_custom());}
    public List<String> getAFKChecker_effects_DTypes() {return config.getStringList(paths.getAfk_checker_effects_invincibleC_dTypes());}
    public boolean getAFKChecker_effects_invisible() {return config.getBoolean(paths.getAfk_checker_effects_invisible());}
    public boolean getAfkChecker_effects_invisible_usePe() {return config.getBoolean(paths.getAfk_checker_effects_invisible_usePEffect());}
    public boolean getAFKChecker_effects_noCollision() {return config.getBoolean(paths.getAfk_checker_effects_noCollision());}
    public boolean getAFKChecker_effects_invisible_hholo() {return config.getBoolean(paths.getAfk_checker_effects_invisible_hholo());}
    public boolean getAFKChecker_effects_invisible_hholo_fullTag() {return config.getBoolean(paths.getAfk_checker_effects_invisible_hholo_fullTag());}
    public boolean getAFKChecker_effects_AfkNameTag() {return config.getBoolean(paths.getAfk_checker_effects_AfkNameTag());}
    public String getAFKChecker_effects_AfkNameTag_prefix() {return config.getString(paths.getAfk_checker_effects_AfkNameTag_prefix());}
    public String getAFKChecker_effects_AfkNameTag_suffix() {return config.getString(paths.getAfk_checker_effects_AfkNameTag_suffix());}
    public boolean getAFKChecker_allowHitPlayers() {return config.getBoolean(paths.getAfk_checker_allow_hit_player());}
    public boolean getAFKChecker_allowHitMobs() {return config.getBoolean(paths.getAfk_checker_allow_hit_mob());}
    public boolean getAFKChecker_allowBreakBlock() {return config.getBoolean(paths.getAfk_checker_allow_break_block());}
    public boolean getAFKChecker_allowPlaceBlock() {return config.getBoolean(paths.getAfk_checker_allow_place_block());}
    public boolean getAFKChecker_stopCreeperTarget() {return config.getBoolean(paths.getAfk_checker_stop_creeper_target());}
    public boolean getAFKChecker_killCreeperOnTarget() {return config.getBoolean(paths.getAfk_checker_kill_creeper_on_target());}
    public double getGrappling_hook_cooldown() {return config.getDouble(paths.getGrappling_hook_cooldown()) * 1000;}
    public boolean getGrappling_hook_prevent_falldmg() {return config.getBoolean(paths.getGrappling_hook_prevent_falldmg());}
    public double getJPads_cooldown() {return config.getDouble(paths.getJPads_cooldown()) * 1000;}
    public boolean getJPads_prevent_falldmg() {return config.getBoolean(paths.getJPads_prevent_falldmg());}
    public boolean getAdminChat() {return config.getBoolean(paths.getAdmin_Chat());}
    public List<String> getAdminChat_Admins() {return config.getStringList(paths.getAdmin_Chat_Admins());}
    public boolean getAdminChat_AutoAssign() {return config.getBoolean(paths.getAdmin_Chat_Auto_Assign());}
    public boolean getAdminChat_shortPrefix() {return config.getBoolean(paths.getAdmin_Chat_short_prefix());}
    public boolean getAdminChat_useCustomPrefix() {return config.getBoolean(paths.getAdmin_Chat_useCustomPrefix());}
    public String getAdminChat_customPrefix() {return config.getString(paths.getAdmin_Chat_customPrefix());}
    public boolean getAdminChat_queue() {return config.getBoolean(paths.getAdmin_Chat_queue());}
    public double getAdminChat_queueTime() {return config.getDouble(paths.getAdmin_Chat_queueTime());}
    public double getAdminChat_queueLimit() {return config.getDouble(paths.getAdmin_Chat_queueLimit()) == -1 ? -1 : Math.round(config.getDouble(paths.getAdmin_Chat_queueLimit()));}

    public boolean reloadConfig(boolean force) {

        long timeElapsed = System.currentTimeMillis() - cooldown;

        if (timeElapsed >= 5000 || force) {

            boolean err = false;

            p.reloadConfig();
            this.config = p.getConfig();

            setVersion(p.getDescription().getVersion());
            p.setPrefix(this, new TextFormatting());

            p.reload(constructWrapper());
            if (updateCommands()) {if (updateCommands()){err = true;}}

            p.saveConfig();

            this.cooldown = System.currentTimeMillis();
            return !err;
        } else {
            return false;
        }

    }



    private CustomDataWrapper constructWrapper() {
        List<CustomData> customDataList = new ArrayList<>();

        customDataList.add(constructLoggerData());
        customDataList.add(constructPlmsgData());
        customDataList.add(constructAfkCData());
        customDataList.add(constructJpData());
        customDataList.add(constructGhData());
        customDataList.add(constructCFilterData());
        customDataList.add(constructAFKLData());
        customDataList.add(constructOpmsgData());
        customDataList.add(constructInvData());

        return new CustomDataWrapper(customDataList);
    }

    /*
    private CustomData construct_____Data() {
        List<String> s = new ArrayList<>();
        List<Boolean> b = new ArrayList<>();
        List<Integer> i = new ArrayList<>();
        List<Double> d = new ArrayList<>();
        List<List<String>> lS = new ArrayList<>();

        s.add(p.getPrefix());
        b.add(getDisable_logger());
        b.add(getDisplay_debug());

        return new CustomData(s, b, i, d, lS);
    }
     */

    private CustomData constructLoggerData() {
        List<String> s = new ArrayList<>();
        List<Boolean> b = new ArrayList<>();

        s.add(p.getPrefix());
        b.add(getDisable_logger());
        b.add(getDisplay_debug());

        return new CustomData(s, b, null, null, null);
    }
    private CustomData constructPlmsgData() {
        List<String> s = new ArrayList<>();

        s.add(p.getColorprefix());

        return new CustomData(s, null, null, null, null);
    }
    private CustomData constructAfkCData() {
        List<String> s = new ArrayList<>();
        List<Boolean> b = new ArrayList<>();
        List<List<String>> lS = new ArrayList<>();
        List<Integer> i = new ArrayList<>();

        lS.add(getAfkChecker_exclude());
        b.add(getAFKChecker_broadcastAFK());
        b.add(getAFKChecker_broadcastTime());
        b.add(getAFKChecker_display_personalTime());
        b.add(getAFKChecker_effects_enabled());
        b.add(getAFKChecker_effects_invincible());
        b.add(getAFKChecker_effects_invincible2());
        b.add(getAFKChecker_effects_invincibleCustom());
        b.add(getAFKChecker_effects_invisible());
        b.add(getAFKChecker_effects_invisible_hholo());
        b.add(getAFKChecker_effects_invisible_hholo_fullTag());
        b.add(getAFKChecker_effects_noCollision());
        b.add(getAFKChecker_effects_AfkNameTag());
        b.add(getAfkChecker_enabled());
        b.add(getAfkChecker_effects_invisible_usePe());
        s.add(getAFKChecker_effects_AfkNameTag_prefix());
        s.add(getAFKChecker_effects_AfkNameTag_suffix());
        lS.add(getAFKChecker_effects_DTypes());
        i.add(getAfkChecker_afk_time());

        return new CustomData(s, b, i, null, lS);
    }
    private CustomData constructJpData() {
        List<Double> d = new ArrayList<>();
        List<Boolean> b = new ArrayList<>();

        b.add(getJump_pads_enabled());
        b.add(getJPads_prevent_falldmg());
        d.add(getY_velocity());
        d.add(getVelocity_multiplier());
        d.add(getJPads_cooldown());

        return new CustomData(null, b, null, d, null);
    }
    private CustomData constructGhData() {
        List<Boolean> b = new ArrayList<>();
        List<Double> d = new ArrayList<>();

        b.add(getGrappling_hook_enabled());
        b.add(getGrappling_hook_prevent_falldmg());
        d.add(getY_velocity_g());
        d.add(getGrappling_hook_cooldown());

        return new CustomData(null, b, null, d, null);
    }
    private CustomData constructCFilterData() {
        List<Boolean> b = new ArrayList<>();
        List<List<String>> lS = new ArrayList<>();

        b.add(getChatFilterEnabled());
        lS.add(getBlockedStrings());

        return new CustomData(null, b, null, null, lS);
    }
    private CustomData constructAFKLData() {
        List<Boolean> b = new ArrayList<>();
        List<List<String>> lS = new ArrayList<>();

        b.add(getAfkChecker_enabled());
        b.add(getAfkChecker_allow_afk_chat());
        b.add(getAFKChecker_effects_invincible());
        b.add(getAFKChecker_effects_invincible2());
        b.add(getAFKChecker_effects_invincibleCustom());
        b.add(getAFKChecker_effects_noCollision());
        b.add(getAFKChecker_allowHitMobs());
        b.add(getAFKChecker_allowHitPlayers());
        b.add(getAFKChecker_allowPlaceBlock());
        b.add(getAFKChecker_allowBreakBlock());
        b.add(getAFKChecker_stopCreeperTarget());
        b.add(getAFKChecker_killCreeperOnTarget());
        lS.add(getAFKChecker_effects_DTypes());

        return new CustomData(null, b, null, null, lS);
    }

    private CustomData constructOpmsgData() {
        List<String> s = new ArrayList<>();
        List<Boolean> b = new ArrayList<>();
        List<Double> d = new ArrayList<>();
        List<List<String>> lS = new ArrayList<>();

        s.add(getAdminChat_customPrefix());
        b.add(getAdminChat());
        b.add(getAdminChat_AutoAssign());
        b.add(getAdminChat_useCustomPrefix());
        b.add(getAdminChat_shortPrefix());
        b.add(getAdminChat_queue());
        lS.add(getAdminChat_Admins());
        d.add(getAdminChat_queueTime());
        d.add(getAdminChat_queueLimit());

        return new CustomData(s, b, null, d, lS);
    }
    private CustomData constructInvData() {
        List<Boolean> b = new ArrayList<>();
        List<Double> d = new ArrayList<>();

        d.add(getCommands_invsave_restore_requests_cooldown());
        b.add(getCommands_invsave_restore_requests());
        return new CustomData(null, b, null, d, null);
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

    private boolean updateCommands() {
        try {
            boolean fj_enabled = getCommands_firstjoin_enabled();
            boolean invs_enabled = getCommands_invsave_enabled();
            boolean xps_enabled = getCommands_xparea_enabled();
            boolean afkc_enabled = getAfkChecker_enabled();
            //
            FirstJoinedCommand.enabled = fj_enabled;
            FirstJoinedCommandTabCompletion.enabled = fj_enabled;
            //
            InventorySavePointCommand.enabled = invs_enabled;
            InventorySavePointCommandTabCompletion.enabled = invs_enabled;
            InventoryListener.enabled = invs_enabled;
            OpenGUICommand.enabled = invs_enabled;
            //
            XpSaveZoneCommand.enabled = xps_enabled;
            XpSaveZoneCommandTabCompletion.enabled = xps_enabled;
            //
            AFKChecker.enabled = afkc_enabled;
            AFKListener.enabled = afkc_enabled;
            //
            return false;
        } catch (Exception e) {
            return true;
        }

    }

    public boolean backupConfig() {

        FileConfiguration backup_config = new YamlConfiguration();
        try {
            backup_config.load(paths.getConfig());
            backup_config.save(paths.getBackup_config());
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            return false;
        }

    }

    public boolean BackupConfigExists() {
        File file = new File(paths.getBackup_config());
        return !file.exists();
    }

    public boolean restoreConfig() {
        FileConfiguration backup_config = new YamlConfiguration();
        FileConfiguration config = new YamlConfiguration();
        try {
            backup_config.load(paths.getBackup_config());
            config.load(paths.getConfig());
            backup_config.save(paths.getConfig());
            config.save(paths.getBackup_config());
            reloadConfig(true);
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            return false;
        }
    }

}
