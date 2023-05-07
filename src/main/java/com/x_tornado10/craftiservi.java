package com.x_tornado10;

import com.x_tornado10.chat.filters.MsgFilter;
import com.x_tornado10.commands.main_command.MainCommand;
import com.x_tornado10.commands.main_command.MainCommandTabCompletion;
import com.x_tornado10.commands.first_join_command.FirstJoinedCommand;
import com.x_tornado10.commands.first_join_command.FirstJoinedCommandTabCompletion;
import com.x_tornado10.commands.inv_save_point.InventorySavePointCommand;
import com.x_tornado10.commands.inv_save_point.InventorySavePointTabCompletion;
import com.x_tornado10.commands.open_gui_command.OpenGUICommand;
import com.x_tornado10.commands.xp_save_zone_command.XpSaveZoneCommand;
import com.x_tornado10.commands.xp_save_zone_command.XpSaveZoneCommandTabCompletion;
import com.x_tornado10.events.listeners.JoinListener;
import com.x_tornado10.events.listeners.PlayerMoveListener;
import com.x_tornado10.events.listeners.grapling_hook.GraplingHookListener;
import com.x_tornado10.events.listeners.inventory.InventoryListener;
import com.x_tornado10.events.listeners.inventory.InventoryOpenListener;
import com.x_tornado10.events.listeners.jpads.JumpPads;
import com.x_tornado10.files.FileLocations;
import com.x_tornado10.files.FileManager;
import com.x_tornado10.handlers.ConfigHandler;
import com.x_tornado10.handlers.DataHandler;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import com.x_tornado10.utils.TextFormatting;
import com.x_tornado10.utils.ToFromBase64;
import com.x_tornado10.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public final class craftiservi extends JavaPlugin {

    private static craftiservi instance;
    private long start;
    private long finish;
    private Logger logger;
    private PlayerMessages plmsg;
    private String prefix;
    private String colorprefix;
    private ConfigHandler configHandler;
    private HashMap<UUID, String> playerlist;
    private PluginManager pm;
    private DataHandler dataHandler;
    private FileManager fm;
    private ToFromBase64 toFromBase64;
    private TextFormatting txtformatting;

    private Boolean firstrun = false;

    private int timesstartedreloaded;

    private HashMap<String, List<Location>> xpsaveareas;
    private FileLocations fl;
    private int registeredPlayers;
    private HashMap<UUID, List<Float>> playersinsavearea;

    private JoinListener joinListener;
    private PlayerMoveListener playerMoveListener;
    private InventoryOpenListener inventoryOpenListener;
    private InventoryListener inventoryListener;
    private GraplingHookListener graplingHookListener;
    private JumpPads jumpPads;

    private HashMap<UUID, HashMap<String, Inventory>> inv_saves;
    private HashMap<Integer, Inventory> invs_review = new HashMap<>();
    public static Map<UUID, Long> FLYING_TIMEOUT = new HashMap<UUID, Long>();

    private List<String> blockedStrings;
    private MsgFilter msgFilter;
    private FileConfiguration config;
    private FileConfiguration backup_config;

    @Override
    public void onLoad() {

        instance = this;
        start = System.currentTimeMillis();
        reloadConfig();
        config = getConfig();
        fl = new FileLocations();
        fm = new FileManager(getInstance(), fl);
        configHandler = new ConfigHandler(config, fl, fm);
        txtformatting = new TextFormatting();

        setPrefix(configHandler, txtformatting);

        logger = new Logger(prefix, true, true);
        plmsg = new PlayerMessages(colorprefix);
        playerlist = new HashMap<>();
        xpsaveareas = new HashMap<>();
        playersinsavearea = new HashMap<>();
        inv_saves = new HashMap<>();
        pm = Bukkit.getPluginManager();
        blockedStrings = new ArrayList<>();
        toFromBase64 = new ToFromBase64();
        msgFilter = new MsgFilter(configHandler.getBlockedStrings());
        backup_config = new YamlConfiguration();

        configHandler.updateConfig();
        configHandler.setVersion(getDescription().getVersion());
        Logger.setDebug(configHandler.getDisplay_debug());
        Logger.setEnabled(configHandler.getDisable_logger());

        saveConfig();
        reloadConfig();

        File data = new File(fl.getData());

        if (!data.exists()) {firstrun = true;}

        timesstartedreloaded = timesstartedreloaded + 1;

        if (fm.createFiles()) {

            logger.debug("§aSuccessfully created all files with 0 Errors/Exceptions!");

        }

        dataHandler = new DataHandler(fl.getDataf(), data);

        logger.info("Loading data from files...");
        playerlist = fm.getPlayerListFromTextFile();
        xpsaveareas = fm.getXpSaveAreaFromTextFile();
        playersinsavearea = fm.getPlayersInSaveAreaFromTextFile();
        inv_saves = fm.getPlayerInvSavePointsFromTextFile();

        if (firstrun) {

            try {

                dataHandler.configure();

            } catch (IOException e) {

                logger.severe("Something went wrong during file setup! Please restart the server to avoid any issues!");

            }
        }

    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        try {

            dataHandler.setData();

        } catch (IOException e) {

            logger.severe("Something went wrong while setting the values in 'data.yml'! Please restart the server to avoid any further issues!");
            getPluginLoader().disablePlugin(this);

        } catch (InvalidConfigurationException ex) {

            try {

                dataHandler.configure();
                dataHandler.setData();
                logger.severe("§4Something went wrong while loading/setting values for 'data.yml'! All values were reset automatically!§r");
                logger.severe("§4All Values were reset successfully with 0 Errors remaining§r");

            } catch (Exception e) {

                logger.severe("§4Something went wrong while setting the values in 'data.yml'! Please restart the server to avoid any further issues!§r");
                logger.severe("§4Please reinstall the plugin if this keeps happening!§r");
                getPluginLoader().disablePlugin(this);

            }


        }

        logger.info("Current version: " + getDescription().getName().toLowerCase() + "-" + getDescription().getVersion() + " by " + getDescription().getAuthors().get(0));
        logger.info("Checking for updates...");
        new UpdateChecker(this, 108546).getVersion(version -> {

            if (getDescription().getVersion().equals(version)) {

                logger.info("No update was found! You are running the latest version!");

            } else {

                logger.info("There is a new version available on: " + getDescription().getWebsite());

            }
        });

        logger.debug("");
        logger.debug("§8------------Debug------------");
        logger.debug("");
        logger.debug("Registering classes...");
        joinListener = new JoinListener();
        playerMoveListener = new PlayerMoveListener();
        inventoryOpenListener = new InventoryOpenListener();
        inventoryListener = new InventoryListener();
        graplingHookListener = new GraplingHookListener(configHandler.getY_velocity_g());
        jumpPads = new JumpPads(configHandler.getY_velocity(), configHandler.getVelocity_multiplier());

        pm.registerEvents(joinListener, this);
        pm.registerEvents(playerMoveListener, this);
        pm.registerEvents(inventoryListener, this);
        pm.registerEvents(inventoryOpenListener, this);
        pm.registerEvents(graplingHookListener, this);
        pm.registerEvents(jumpPads, this);
        logger.debug("Listeners..§adone");
        logger.debug("");

        logger.debug("Getting commands...");
        Bukkit.getPluginCommand("firstjoin").setExecutor(new FirstJoinedCommand());
        Bukkit.getPluginCommand("firstjoin").setTabCompleter(new FirstJoinedCommandTabCompletion());
        logger.debug("firstjoin...§adone");

        Bukkit.getPluginCommand("xparea").setExecutor(new XpSaveZoneCommand());
        Bukkit.getPluginCommand("xparea").setTabCompleter(new XpSaveZoneCommandTabCompletion());
        logger.debug("xparea...§adone");

        Bukkit.getPluginCommand("invsave").setExecutor(new InventorySavePointCommand());
        Bukkit.getPluginCommand("invsave").setTabCompleter(new InventorySavePointTabCompletion());
        logger.debug("invsave...§adone");

        Bukkit.getPluginCommand("opengui").setExecutor(new OpenGUICommand());
        logger.debug("opengui...§adone");

        Bukkit.getPluginCommand("craftiservi").setExecutor(new MainCommand(getInstance(), configHandler, fl, logger, plmsg));
        Bukkit.getPluginCommand("craftiservi").setTabCompleter(new MainCommandTabCompletion());
        logger.debug("config...§adone");

        logger.debug("");
        logger.debug("Configuring filters...");
        blockedStrings = configHandler.getBlockedStrings();
        logger.debug("MsgFilter...§adone");
        logger.debug("");
        logger.debug("Applying filters to logger...");
        msgFilter.registerFilter();
        logger.debug("MsgFilter...§adone");

        logger.debug("");
        logger.debug("§8-----------------------------");
        logger.debug("");

        finish = System.currentTimeMillis();
        if (finish-start>20000) {

            logger.info("Successfully enabled! (took §c" + (finish - start) + "ms§r)");

        } else {
            logger.info("Successfully enabled! (took §a" + (finish - start) + "ms§r)");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Initializing shutdown...");
        logger.nodebug("Saving data...");
        logger.debug("");
        logger.debug("§8------------Debug------------");
        logger.debug("");
        logger.debug("Saving data to files...");
        if (!playerlist.isEmpty() && new File(fl.getPlayerlist()).exists()) {

            fm.writePlayerlistToTextFile(playerlist);

        }
        logger.debug("Playerlist...§adone");

        if (new File(fl.getPlayerlist()).exists() && !inv_saves.isEmpty()) {

            fm.writePlayerInvSavesToTextFile(inv_saves);

        }
        logger.debug("InventorySavePoints...§adone");

        if (new File(fl.getXpsaveareas()).exists()) {

            fm.writeXpSaveAreasToTextFile(xpsaveareas);

        }
        logger.debug("XpSaveAreas...§adone");

        if (new File(fl.getPlayersinsavearea()).exists()) {

            fm.writePlayersInSaveAreaToTextFile(playersinsavearea);

        }
        logger.debug("PlayersInSaveArea...§adone");
        logger.debug("");
        logger.debug("§8-----------------------------");
        logger.debug("");

        if (configHandler.backupConfig()) {

            logger.debug("Successfully created backup of config.yml");

        }

        logger.info("Everything was saved successfully!");
        logger.info("Successfully disabled!");

    }

    public static craftiservi getInstance() {
        return instance;
    }
    public HashMap<UUID, String> getPlayerlist() {

        return playerlist;

    }
    public Logger getCustomLogger() {
        return logger;
    }
    public PlayerMessages getPlayerMessages() {

        return plmsg;

    }
    public int getTimesstartedreloaded() {
        return timesstartedreloaded;
    }
    public int getRegisteredPlayers() {
        return registeredPlayers;
    }
    public HashMap<String, List<Location>> getXpsaveareas() {
        return xpsaveareas;
    }
    public HashMap<UUID, List<Float>> getPlayersinsavearea() {
        return playersinsavearea;
    }
    public HashMap<UUID, HashMap<String, Inventory>> getInv_saves() {
        return inv_saves;
    }
    public HashMap<Integer, Inventory> getInvs_review() {
        return invs_review;
    }
    public String getColorprefix() {
        return colorprefix;
    }
    public void setPrefix(ConfigHandler configHandler, TextFormatting txtformatting) {

        if (configHandler.getUse_custom_Prefix()) {

            prefix = txtformatting.stripColorAndFormattingCodes(configHandler.getCustom_Prefix());
            colorprefix = configHandler.getCustom_Prefix();

        } else {

            if (configHandler.getShort_prefix()) {

                String short_prefix;
                short_prefix = "CS";
                prefix = "[" + short_prefix + "] ";
                colorprefix = "§1§l[§9§l" + short_prefix + "§1]§l:§r ";

            } else {

                String long_prefix;
                long_prefix= getDescription().getPrefix();
                prefix = "[" + long_prefix + "] ";
                colorprefix = "§1§l[§9§l" + long_prefix + "§1]§l:§r ";

            }
        }

    }
    public MsgFilter getMsgFilter() {
        return msgFilter;
    }
    public GraplingHookListener getGraplingHookListener() {
        return graplingHookListener;
    }
    public JumpPads getJumpPads() {
        return jumpPads;
    }
    public String getPrefix() {
        return prefix;
    }
    public void earlyLog(String message) {

        ConsoleCommandSender commandSender = Bukkit.getConsoleSender();

        commandSender.sendMessage("[" + getDescription().getPrefix() + "]" + message);

    }

}
