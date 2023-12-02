package com.x_tornado10;

import com.tchristofferson.configupdater.ConfigUpdater;
import com.x_tornado10.chat.filters.MsgFilter;
import com.x_tornado10.commands.admin_chat_command.AdminChatCommand;
import com.x_tornado10.commands.main_command.MainCommand;
import com.x_tornado10.commands.main_command.MainCommandTabCompletion;
import com.x_tornado10.commands.first_join_command.FirstJoinedCommand;
import com.x_tornado10.commands.first_join_command.FirstJoinedCommandTabCompletion;
import com.x_tornado10.commands.inv_save_point.InventorySavePointCommand;
import com.x_tornado10.commands.inv_save_point.InventorySavePointCommandTabCompletion;
import com.x_tornado10.commands.open_gui_command.OpenGUICommand;
import com.x_tornado10.commands.xp_save_zone_command.XpSaveZoneCommand;
import com.x_tornado10.commands.xp_save_zone_command.XpSaveZoneCommandTabCompletion;
import com.x_tornado10.events.custom.ReloadEvent;
import com.x_tornado10.events.listeners.join_listener.JoinListener;
import com.x_tornado10.events.listeners.xp_area_listener.PlayerMoveListener;
import com.x_tornado10.events.listeners.afk_checking.AFKListener;
import com.x_tornado10.events.listeners.grapling_hook.GraplingHookListener;
import com.x_tornado10.events.listeners.inventory.InventoryListener;
import com.x_tornado10.events.listeners.inventory.InventoryOpenListener;
import com.x_tornado10.events.listeners.jpads.JumpPads;
import com.x_tornado10.features.afk_protection.AFKChecker;
import com.x_tornado10.events.listeners.afk_checking.InvisPlayers;
import com.x_tornado10.features.inv_saves.InvSaveMgr;
import com.x_tornado10.message_sys.OpMessages;
import com.x_tornado10.utils.*;
import com.x_tornado10.managers.FileManager;
import com.x_tornado10.managers.ConfigManager;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.message_sys.PlayerMessages;
import com.x_tornado10.utils.custom_data.CustomDataWrapper;
import com.x_tornado10.utils.data.compare.ObjectCompare;
import com.x_tornado10.utils.data.convert.TextFormatting;
import com.x_tornado10.utils.data.convert.ToFromBase64;
import jdk.jfr.Description;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nullable;
import javax.xml.crypto.NodeSetData;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class craftiservi extends JavaPlugin {

    private static craftiservi instance;
    private long start;
    private long finish;
    private Logger logger;
    private PlayerMessages plmsg;
    private OpMessages opmsg;
    private String prefix;
    private String dc_prefix;
    private String colorprefix;
    private ConfigManager configManager;
    private HashMap<UUID, String> playerlist;
    private PluginManager pm;
    private FileManager fm;
    private ToFromBase64 toFromBase64;
    private TextFormatting txtformatting;
    private ObjectCompare HMC;
    private static HashMap<UUID, Long> afkList;
    private static ConcurrentHashMap<UUID, Long> afkPlayers;
    private static ConcurrentHashMap<UUID, Long> playersToCheck;
    private HashMap<String, List<Location>> xpsaveareas;
    private Paths paths;
    private HashMap<UUID, List<Float>> playersinsavearea;
    private JoinListener joinListener;
    private PlayerMoveListener playerMoveListener;
    private InventoryOpenListener inventoryOpenListener;
    private InventoryListener inventoryListener;
    private InvSaveMgr invSaveMgr;
    private GraplingHookListener graplingHookListener;
    private JumpPads jumpPads;
    private AFKListener afkListener;
    private HashMap<UUID, HashMap<String, Inventory>> inv_saves;
    private HashMap<Integer, Inventory> invs_review = new HashMap<>();
    public static Map<UUID, Long> FLYING_TIMEOUT = new HashMap<>();

    private List<String> blockedStrings;
    private MsgFilter msgFilter;
    private FileConfiguration config;
    private FileConfiguration backup_config;
    private AFKChecker afkChecker;
    private InvisPlayers invisPlayers;
    private java.util.logging.Logger setupLogger;
    private LuckPerms LpAPI;

    public static craftiservi getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {

        instance = this;
        start = System.currentTimeMillis();
        setupLogger = getLogger();
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(this, "config.yml", configFile, new ArrayList<>());
        } catch (IOException e) {
            setupLogger.severe("Error while trying to update config.yml!");
            setupLogger.severe("If this error persists after restarting the server please file a bug report!");
        }

        reloadConfig();

        setup();

    }


    @Override
    public void onEnable() {
        // Plugin startup logic
        logger.info("Current version: " + getDescription().getName().toLowerCase() + "-" + getDescription().getVersion() + " by " + getDescription().getAuthors().get(0));
        logger.info("Checking for updates...");

        new UpdateChecker(this, 108546).getVersion(version -> {
            if (isVersionHigher(version, getDescription().getVersion())) {
                logger.info("There is a new version available on: " + getDescription().getWebsite());
            } else {
                logger.info("No update was found! You are running the latest version!");
            }
            displayVersionDifference(getDescription().getVersion(), version, logger);
        });

        Metrics metrics = new Metrics(this, 19084);
        logger.info("Assigning metrics...");
        logger.info("Check out 'plugins/bStats/config.yml' if you want to opt out!");

        logger.debug("");
        logger.debug("§8------------Debug------------");
        logger.debug("");
        logger.debug("Registering classes...");

        finalizeSetup();

        luckPermsSetup();

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
        logger.debug("");
        logger.debug("§8------------Debug------------");
        logger.debug("");
        logger.info("Saving data...");

        saveData();

        afkChecker.clearAFK();

        unregisterHandlers();

        logger.info("Everything was saved successfully!");
        logger.info("Successfully disabled!");

    }

    private void luckPermsSetup() {
        logger.debug("Hooking into the LuckPerms API...");
        if (Objects.requireNonNull(pm.getPlugin("LuckPerms")).isEnabled()) {
            LpAPI = LuckPermsProvider.get();
            logger.debug("§aSuccessfully hooked into the LuckPerms API!");
        } else {
            logger.debug("§cAn error occurred while hooking into the LuckPerms API!");
        }
        if (LpAPI != null) {
            GroupManager groupManager = LpAPI.getGroupManager();
            if (groupManager.getGroup("afkTag") == null) {
                Group group = groupManager.createAndLoadGroup("afkTag").join();
                PrefixNode prefixNode = PrefixNode.builder("§7§o[AFK] ", 100).build();
                WeightNode weightNode = WeightNode.builder(100).build();
                group.data().add(weightNode);
                group.data().add(prefixNode);
                groupManager.saveGroup(group);
            }
            if (groupManager.getGroup("admins") == null) {
                Group group = groupManager.createAndLoadGroup("admins").join();
                WeightNode weightNode = WeightNode.builder(99).build();
                group.data().add(weightNode);
                groupManager.saveGroup(group);
            }
        }
    }

    private void unregisterHandlers() {
        HandlerList handlerList = ReloadEvent.getHandlerList();
        handlerList.unregister(logger);
        handlerList.unregister(plmsg);
        handlerList.unregister(opmsg);
        handlerList.unregister(afkChecker);
        handlerList.unregister(jumpPads);
        handlerList.unregister(inventoryListener);
        handlerList.unregister(inventoryOpenListener);
        handlerList.unregister(graplingHookListener);
        handlerList.unregister(msgFilter);
        handlerList.unregister(afkListener);
        handlerList.unregister(invisPlayers);
        handlerList.unregister(invSaveMgr);
    }

    private void setup() {
        config = getConfig();
        paths = new Paths();
        fm = new FileManager(getInstance(), paths, setupLogger);
        configManager = new ConfigManager(config, paths, fm);
        txtformatting = new TextFormatting();
        HMC = new ObjectCompare();

        setPrefix(configManager, txtformatting);

        logger = new Logger();
        plmsg = new PlayerMessages(colorprefix);
        opmsg = new OpMessages();
        playerlist = new HashMap<>();
        xpsaveareas = new HashMap<>();
        playersinsavearea = new HashMap<>();
        afkList = new HashMap<>();
        inv_saves = new HashMap<>();
        afkPlayers = new ConcurrentHashMap<>();
        playersToCheck = new ConcurrentHashMap<>();
        pm = Bukkit.getPluginManager();
        blockedStrings = new ArrayList<>();
        toFromBase64 = new ToFromBase64();
        msgFilter = new MsgFilter();
        backup_config = new YamlConfiguration();

        configManager.setVersion(getDescription().getVersion());
        Logger.setDebug(configManager.getDisplay_debug());
        Logger.setEnabled(configManager.getDisable_logger());

        saveConfig();
        reloadConfig();

        if (fm.createFiles()) {

            logger.debug("§aSuccessfully created all files with 0 Errors/Exceptions!");

        }

        logger.info("Loading data from files...");
        playerlist = fm.getPlayerListFromTextFile();
        xpsaveareas = fm.getXpSaveAreaFromTextFile();
        playersinsavearea = fm.getPlayersInSaveAreaFromTextFile();
        inv_saves = fm.getPlayerInvSavePointsFromTextFile();
    }
    private void finalizeSetup() {
        joinListener = new JoinListener();
        playerMoveListener = new PlayerMoveListener();
        inventoryOpenListener = new InventoryOpenListener();
        invSaveMgr = new InvSaveMgr();
        inventoryListener = new InventoryListener();
        graplingHookListener = new GraplingHookListener(configManager.getY_velocity_g());
        jumpPads = new JumpPads(configManager.getY_velocity(), configManager.getVelocity_multiplier());
        invisPlayers = new InvisPlayers();
        afkChecker = new AFKChecker();
        afkListener = new AFKListener();

        pm.registerEvents(joinListener, this);
        pm.registerEvents(playerMoveListener, this);
        pm.registerEvents(inventoryListener, this);
        pm.registerEvents(inventoryOpenListener, this);
        pm.registerEvents(graplingHookListener, this);
        pm.registerEvents(jumpPads, this);
        pm.registerEvents(afkListener, this);
        pm.registerEvents(logger, this);
        pm.registerEvents(plmsg, this);
        pm.registerEvents(opmsg, this);
        pm.registerEvents(afkChecker, this);
        pm.registerEvents(msgFilter, this);
        pm.registerEvents(invisPlayers, this);
        pm.registerEvents(invSaveMgr,this);
        logger.debug("Listeners..§adone");
        logger.debug("");

        logger.debug("Getting commands...");
        Bukkit.getPluginCommand("firstjoin").setExecutor(new FirstJoinedCommand());
        Bukkit.getPluginCommand("firstjoin").setTabCompleter(new FirstJoinedCommandTabCompletion());
        if (configManager.getCommands_firstjoin_enabled()) {
            logger.debug("firstjoin...§adone");
        } else {
            logger.debug("firstjoin...§cdisabled");
        }

        Bukkit.getPluginCommand("xparea").setExecutor(new XpSaveZoneCommand());
        Bukkit.getPluginCommand("xparea").setTabCompleter(new XpSaveZoneCommandTabCompletion());
        if (configManager.getCommands_xparea_enabled()) {
            logger.debug("xparea...§adone");
        } else {

            logger.debug("xparea...§cdisabled");

        }
        Bukkit.getPluginCommand("invsave").setExecutor(new InventorySavePointCommand());
        Bukkit.getPluginCommand("invsave").setTabCompleter(new InventorySavePointCommandTabCompletion());

        Bukkit.getPluginCommand("opengui").setExecutor(new OpenGUICommand());
        if (configManager.getCommands_invsave_enabled()) {

            logger.debug("invsave...§adone");
            logger.debug("opengui...§adone");

        } else {

            logger.debug("invsave...§cdisabled");
            logger.debug("opengui...§cdisabled");

        }

        Bukkit.getPluginCommand("craftiservi").setExecutor(new MainCommand(getInstance(), configManager, paths, logger, plmsg));
        Bukkit.getPluginCommand("craftiservi").setTabCompleter(new MainCommandTabCompletion());
        logger.debug("config...§adone");

        Bukkit.getPluginCommand("adminchat").setExecutor(new AdminChatCommand());

        logger.debug("");
        logger.debug("Configuring filters...");
        blockedStrings = configManager.getBlockedStrings();
        logger.debug("MsgFilter...§adone");
        logger.debug("");
        logger.debug("Applying filters to logger...");
        msgFilter.registerFilter();
        logger.debug("MsgFilter...§adone");

        logger.debug("");
        logger.debug("§8-----------------------------");
        logger.debug("");

        logger.debug("Reloading Config...");

        if (configManager.reloadConfig(true)) {

            logger.debug("Successfully reloaded config!");

        } else {

            logger.severe("Couldn't reload config. Please restart the server!");

        }

    }

    private void saveData() {
        if (!playerlist.isEmpty() && new File(paths.getPlayerlist()).exists()) {

            fm.writePlayerlistToTextFile(playerlist);

        }
        logger.debug("Playerlist...§adone");

        if (new File(paths.getPlayerlist()).exists() && !inv_saves.isEmpty()) {

            fm.writePlayerInvSavesToTextFile(inv_saves);

        }
        logger.debug("InventorySavePoints...§adone");

        if (new File(paths.getXpsaveareas()).exists()) {

            fm.writeXpSaveAreasToTextFile(xpsaveareas);

        }
        logger.debug("XpSaveAreas...§adone");

        if (new File(paths.getPlayersinsavearea()).exists()) {

            fm.writePlayersInSaveAreaToTextFile(playersinsavearea);

        }
        logger.debug("PlayersInSaveArea...§adone");
        logger.debug("");
        logger.debug("§8-----------------------------");
        logger.debug("");

        if (configManager.backupConfig()) {

            logger.debug("Successfully created backup of config.yml");

        }
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
    public void setPrefix(ConfigManager configManager, TextFormatting txtformatting) {

        if (configManager.getUse_custom_Prefix()) {

            prefix = txtformatting.stripColorAndFormattingCodes(configManager.getCustom_Prefix());
            dc_prefix = txtformatting.stripColorAndFormattingCodes(configManager.getCustom_dc_Prefix());
            colorprefix = configManager.getCustom_Prefix();

        } else {

            if (configManager.getShort_prefix()) {

                String short_prefix;
                short_prefix = "CS";
                prefix = "[" + short_prefix + "] ";
                dc_prefix = "[" + short_prefix + "-DC] ";
                colorprefix = "§1§l[§9§l" + short_prefix + "§1]§l:§r ";

            } else {

                String long_prefix;
                long_prefix = getDescription().getPrefix();
                prefix = "[" + long_prefix + "] ";
                dc_prefix = "[" + long_prefix + "-Discord] ";
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

    public HashMap<UUID, Long> getAfkList() {return afkList;}
    public ConcurrentHashMap<UUID, Long> getAfkPlayers() {return afkPlayers;}
    public AFKChecker getAfkChecker() {
        return afkChecker;
    }
    public ObjectCompare getOC() {
        return HMC;
    }
    public TextFormatting getTxtformatting() {
        return txtformatting;
    }
    public InvisPlayers getInvisPlayers() {
        return invisPlayers;
    }
    public OpMessages getOpmsg() {
        return opmsg;
    }
    public static boolean isVersionHigher(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int part1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
            int part2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;

            if (part1 > part2) {
                return true;
            } else if (part1 < part2) {
                return false;
            }
        }
        return false; // Both versions are equal
    }
    public static void displayVersionDifference(String currentVersion, String newestVersion, Logger logger) {
        String[] currentParts = currentVersion.split("\\.");
        String[] newestParts = newestVersion.split("\\.");

        int currentMajor = Integer.parseInt(currentParts[0]);
        int newestMajor = Integer.parseInt(newestParts[0]);
        int currentMinor = Integer.parseInt(currentParts[1]);
        int newestMinor = Integer.parseInt(newestParts[1]);

        int majorVersionsDifference = newestMajor - currentMajor;
        int minorVersionsDifference = newestMinor - currentMinor;

        if (majorVersionsDifference == 0 && minorVersionsDifference == 0) {return;}

        StringBuilder message = new StringBuilder("You are ");

        if (majorVersionsDifference > 0) {
            message.append(majorVersionsDifference).append(" major version").append(majorVersionsDifference > 1 ? "s" : "").append(" ");
        } else if (majorVersionsDifference < 0) {
            message.append(Math.abs(majorVersionsDifference)).append(" major version").append(majorVersionsDifference < -1 ? "s" : "").append(" ahead ");
        }

        if (minorVersionsDifference != 0) {
            message.append("and ").append(Math.abs(minorVersionsDifference)).append(" minor version").append(minorVersionsDifference != 1 ? "s" : "").append(minorVersionsDifference > 0 ? " behind" : " ahead");
        }

        message.append("!");

        logger.info(message.toString());
    }

    public ConcurrentHashMap<UUID, Long> getPlayersToCheck() {
        return playersToCheck;
    }
    public LuckPerms getLpAPI() {
        return LpAPI;
    }
    public boolean addPlayerToGroup(UUID pid, String groupName) {
        UserManager userManager = LpAPI.getUserManager();
        User user = userManager.getUser(pid);
        if (user == null) {
            return false;
        }
        GroupManager groupManager = LpAPI.getGroupManager();
        Group group = groupManager.getGroup(groupName);
        if (group == null) {
            return false;
        }

        InheritanceNode inheritanceNode = InheritanceNode.builder(group).build();
        user.data().add(inheritanceNode);
        userManager.saveUser(user);
        return true;

    }
    public boolean addSuffixToPlayer(UUID pid, String suffix) {
        UserManager userManager = LpAPI.getUserManager();
        User user = userManager.getUser(pid);
        if (user == null) {
            return false;
        }

        SuffixNode suffixNode = SuffixNode.builder(suffix,100).build();
        user.data().add(suffixNode);
        userManager.saveUser(user);
        return true;
    }
    public boolean removePlayerFromGroup(UUID pid, String groupName) {
        UserManager userManager = LpAPI.getUserManager();
        User user = userManager.getUser(pid);
        if (user == null) {
            return false;
        }
        GroupManager groupManager = LpAPI.getGroupManager();
        Group group = groupManager.getGroup(groupName);
        if (group == null) {
            return false;
        }

        Node node = InheritanceNode.builder(group).build();
        user.data().remove(node);
        userManager.saveUser(user);
        return true;
    }
    public boolean isPlayerInGroup(UUID pid, String groupName) {
        if (LpAPI == null) {LpAPI = LuckPermsProvider.get();}
        UserManager userManager = LpAPI.getUserManager();
        User user = userManager.getUser(pid);
        if (user == null) {
            return false;
        }
        GroupManager groupManager = LpAPI.getGroupManager();
        Group group = groupManager.getGroup(groupName);
        if (group == null) {
            return false;
        }

        Node node = InheritanceNode.builder(group).build();
        return user.data().contains(node, NodeEqualityPredicate.ONLY_KEY).asBoolean();
    }
    public boolean removeSuffixFromPlayer(UUID pid, String suffix) {
        UserManager userManager = LpAPI.getUserManager();
        User user = userManager.getUser(pid);
        if (user == null) {
            return false;
        }

        for (Node node : user.getNodes()) {
            if (node instanceof SuffixNode) {
                if (((SuffixNode) node).getPriority() == 100) {
                    user.data().remove(node);
                }
            }
        }
        userManager.saveUser(user);
        return true;
    }

    @Nullable
    public String getDisplayName(UUID pid, Player p) {
        UserManager userManager = LpAPI.getUserManager();
        if (!userManager.isLoaded(pid)) {
            userManager.loadUser(pid);
        }
        User user = userManager.getUser(pid);
        if (user == null) {return new String("null");}
        String formattedDisplayName = user.getCachedData().getMetaData().getPrefix() +
                p.getName() +
                user.getCachedData().getMetaData().getSuffix();
        return formattedDisplayName.replace("&", "§");
    }
    public boolean hasPermission(Player p, String permission) {
        UUID pid = p.getUniqueId();
        UserManager um = LpAPI.getUserManager();
        if (!um.isLoaded(pid)) {um.loadUser(pid);}
        User user = um.getUser(pid);
        if (user == null) {return false;}
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
    public boolean hasPermission(UUID pid, String permission) {
        UserManager um = LpAPI.getUserManager();
        if (!um.isLoaded(pid)) {um.loadUser(pid);}
        User user = um.getUser(pid);
        if (user == null) {return false;}
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public FileManager getFm() {
        return fm;
    }
    public boolean isPaperServer() {
        return Bukkit.getVersion().toLowerCase().contains("paper");
    }

    public InvSaveMgr getInvSaveMgr() {return invSaveMgr;}

    @Description("Reloads config values during runtime using a Bukkit Custom Event")
    public void reload(CustomDataWrapper customDataWrapper) {
        ReloadEvent reload = new ReloadEvent(customDataWrapper);
        Bukkit.getServer().getPluginManager().callEvent(reload);
    }
}
