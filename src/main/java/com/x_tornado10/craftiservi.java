package com.x_tornado10;

import com.x_tornado10.chat.filters.MsgFilter;
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
import com.x_tornado10.handlers.ConfigHandler;
import com.x_tornado10.handlers.DataHandler;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import com.x_tornado10.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

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

    private Boolean firstrun = false;

    private int timesstartedreloaded;

    private HashMap<String, List<Location>> xpsaveareas;
    private FileLocations fl;
    private int registeredPlayers;
    private HashMap<UUID, List<Float>> playersinsavearea;

    private HashMap<UUID, HashMap<String, Inventory>> inv_saves;
    private HashMap<Integer, Inventory> invs_review = new HashMap<>();
    public static Map<UUID, Long> FLYING_TIMEOUT = new HashMap<UUID, Long>();

    private List<String> blockedStrings;

    @Override
    public void onLoad() {

        instance = this;
        start = System.currentTimeMillis();
        prefix = "[" + getDescription().getPrefix() + "] ";
        colorprefix = "§1§l[§9§l" + getDescription().getPrefix() + "§1]§l:§r ";
        saveDefaultConfig();

        logger = new Logger(prefix);
        plmsg = new PlayerMessages(colorprefix);
        configHandler = new ConfigHandler(prefix, getConfig());
        playerlist = new HashMap<>();
        xpsaveareas = new HashMap<>();
        playersinsavearea = new HashMap<>();
        inv_saves = new HashMap<>();
        fl = new FileLocations();
        pm = Bukkit.getPluginManager();
        blockedStrings = new ArrayList<>();

        configHandler.updateConfig();
        saveConfig();
        reloadConfig();

        File data = new File(fl.getData());

        if (!data.exists()) {firstrun = true;}

        timesstartedreloaded = timesstartedreloaded + 1;

        createFiles();

        dataHandler = new DataHandler(fl.getDataf(), data);

        logger.info("Loading data from files...");
        playerlist = getPlayerListFromTextFile();
        xpsaveareas = getXpSaveAreaFromTextFile();
        playersinsavearea = getPlayersInSaveAreaFromTextFile();
        inv_saves = getPlayerInvSavePointsFromTextFile();

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

        logger.info("");
        logger.info("§8------------Debug------------");
        logger.info("");
        logger.info("Registering classes...");
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new PlayerMoveListener(), this);
        pm.registerEvents(new InventoryOpenListener(), this);
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new GraplingHookListener(), this);
        pm.registerEvents(new JumpPads(), this);
        logger.info("Listeners..§adone");
        logger.info("");

        logger.info("Getting commands...");
        Bukkit.getPluginCommand("firstjoin").setExecutor(new FirstJoinedCommand());
        Bukkit.getPluginCommand("firstjoin").setTabCompleter(new FirstJoinedCommandTabCompletion());
        logger.info("firstjoin...§adone");

        Bukkit.getPluginCommand("xparea").setExecutor(new XpSaveZoneCommand());
        Bukkit.getPluginCommand("xparea").setTabCompleter(new XpSaveZoneCommandTabCompletion());
        logger.info("xparea...§adone");

        Bukkit.getPluginCommand("invsave").setExecutor(new InventorySavePointCommand());
        Bukkit.getPluginCommand("invsave").setTabCompleter(new InventorySavePointTabCompletion());
        logger.info("invsave...§adone");

        Bukkit.getPluginCommand("opengui").setExecutor(new OpenGUICommand());
        logger.info("opengui...§adone");
        logger.info("");
        logger.info("Configuring filters...");
        blockedStrings = configHandler.getBlockedStrings();
        logger.info("MsgFilter...§adone");
        logger.info("");
        logger.info("Applying filters to logger...");
        new MsgFilter().registerFilter();
        logger.info("MsgFilter...§adone");

        logger.info("");
        logger.info("§8-----------------------------");
        logger.info("");

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

        logger.info("");
        logger.info("§8------------Debug------------");
        logger.info("");
        logger.info("Saving data to files...");
        if (!playerlist.isEmpty() && new File(fl.getPlayerlist()).exists()) {

            writePlayerlistToTextFile(playerlist);

        }
        logger.info("Playerlist...§adone");

        if (new File(fl.getPlayerlist()).exists() && !inv_saves.isEmpty()) {

            writePlayerInvSavesToTextFile(inv_saves);

        }
        logger.info("InventorySavePoints...§adone");

        if (new File(fl.getXpsaveareas()).exists()) {

            writeXpSaveAreasToTextFile(xpsaveareas);

        }
        logger.info("XpSaveAreas...§adone");

        if (new File(fl.getPlayersinsavearea()).exists()) {

            writePlayersInSaveAreaToTextFile(playersinsavearea);

        }
        logger.info("PlayersInSaveArea...§adone");
        logger.info("");
        logger.info("§8-----------------------------");
        logger.info("");

        logger.info("Everything was saved successfully!");

        logger.info("Successfully disabled!");

    }

    public static craftiservi getInstance() {
        return instance;
    }

    public String getPrefix() {
        return prefix;
    }

    public HashMap<UUID, String> getPlayerlist() {

        return playerlist;

    }

    private HashMap<UUID, String> getPlayerListFromTextFile() {

        File playerlist = new File(fl.getPlayerlist());
        HashMap<UUID, String> mapFileContents = new HashMap<>();

        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(playerlist));

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|");

                String date = parts[1].trim();
                UUID UUID = java.util.UUID.fromString(parts[0].trim());

                if (!date.equals("") && !UUID.toString().equals("")) {

                    mapFileContents.put(UUID, date);
                    registeredPlayers ++;

                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        }


        return mapFileContents;
    }


    private HashMap<UUID, List<Float>> getPlayersInSaveAreaFromTextFile() {

        File playersinsavearea = new File(fl.getPlayersinsavearea());
        HashMap<UUID, List<Float>> mapFileContents = new HashMap<>();

        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(playersinsavearea));

            String line;

            //player + "|" + f1 + "|" + f2

            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|");

                UUID p = UUID.fromString(parts[0].trim());


                float f1 = Float.parseFloat(parts[1].trim());
                float f2 = Float.parseFloat(parts[2].trim());
                List<Float> floats = new ArrayList<>();
                floats.add(f1);
                floats.add(f2);


                mapFileContents.put(p, floats);

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (br != null) {

                try {

                    br.close();

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        }


        return mapFileContents;
    }

    private HashMap<String, List<Location>> getXpSaveAreaFromTextFile() {

        File xpsavearea = new File(fl.getXpsaveareas());
        HashMap<String, List<Location>> mapFileContents = new HashMap<>();

        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(xpsavearea));

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|");

                //entry.getKey() + sep + world1 + sep + x1 + sep + y1 + sep + z1 + sep + world2 + sep + x2 + sep + y2 + sep + z2

                String name = parts[0].trim();

                double x1 = Double.parseDouble(parts[2].trim());
                double y1 = Double.parseDouble(parts[3].trim());
                double z1 = Double.parseDouble(parts[4].trim());
                World world1 = Bukkit.getWorld(parts[1].trim());

                double x2 = Double.parseDouble(parts[6].trim());
                double y2 = Double.parseDouble(parts[7].trim());
                double z2 = Double.parseDouble(parts[8].trim());
                World world2 = Bukkit.getWorld(parts[5].trim());

                Location loc1 = new Location(world1, x1, y1, z1);
                Location loc2 = new Location(world2, x2, y2, z2);

                List<Location> locs = new ArrayList<>();

                locs.add(loc1);
                locs.add(loc2);

                if (!name.equals("") && !locs.isEmpty()) {

                    mapFileContents.put(name, locs);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        }


        return mapFileContents;
    }


    private void createFiles() {

        File playerlist = new File(fl.getPlayerlist());
        File data = new File(fl.getData());
        File xpsaveareas = new File(fl.getXpsaveareas());
        File xpsaveareasbackup = new File(fl.getXpsaveareasbackup());
        File backupdir = new File(fl.getBackupdir());
        File playersinsavearea = new File(fl.getPlayersinsavearea());
        File playersinsaveareabackup = new File(fl.getPlayersinsaveareabackup());
        File player_inv_saves = new File(fl.getPlayer_inv_saves());
        File player_inv_saves_backup = new File(fl.getPlayer_inv_saves_backup());

        ArrayList<File> files = new ArrayList<>();

        files.add(playerlist);
        files.add(data);
        files.add(xpsaveareas);
        files.add(xpsaveareasbackup);
        files.add(playersinsavearea);
        files.add(playersinsaveareabackup);
        files.add(player_inv_saves);
        files.add(player_inv_saves_backup);

        if (backupdir.mkdirs()) {

            logger.info(backupdir.getName() + " was successfully created!");

        }

        for (File file : files) {

            if (!file.exists()) {

                try {

                    if(file.createNewFile()) {

                        logger.info(file.getName() + " was successfully created!");

                    }

                } catch (IOException e) {

                    logger.severe("Something went wrong while trying to create files! Please restart the server!");

                }
            }

        }

    }

    private void writePlayerInvSavesToTextFile(HashMap<UUID, HashMap<String, Inventory>> playerinvsaves) {

        FileConfiguration player_inv_saves = new YamlConfiguration();

        File player_inv_saves_file = new File(fl.getPlayer_inv_saves());
        File player_inv_saves_backup_file = new File(fl.getPlayer_inv_saves_backup());

        try {

            copyFileToFile(player_inv_saves_file, player_inv_saves_backup_file);

        } catch (Exception e) {

            e.printStackTrace();

        }

        try {

            for (Map.Entry<UUID, HashMap<String, Inventory>> entry : playerinvsaves.entrySet()) {


                UUID player = entry.getKey();

                for (Map.Entry<String, Inventory> entry1 : entry.getValue().entrySet()) {

                    player_inv_saves.createSection(player + "." + entry1.getKey());

                    player_inv_saves.set(player + "." + entry1.getKey(), toBase64(entry1.getValue()));

                }

            }

            player_inv_saves.save(fl.getPlayer_inv_saves());

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private HashMap<UUID, HashMap<String, Inventory>> getPlayerInvSavePointsFromTextFile() {

        HashMap<UUID, HashMap<String, Inventory>> mapFileContetnts = new HashMap<>();

        FileConfiguration player_inv_saves = new YamlConfiguration();

        File player_inv_saves_file = new File(fl.getPlayer_inv_saves());

        try {

            player_inv_saves.load(player_inv_saves_file);

        } catch (Exception e) {

            e.printStackTrace();

        }

        List<String> main_keys = new ArrayList<>(player_inv_saves.getKeys(false));

        HashMap<String, Inventory> invs = new HashMap<>();


        for (String mainKey : main_keys) {

            UUID pid = UUID.fromString(mainKey);

            List<String> child_keys = new ArrayList<>(player_inv_saves.getConfigurationSection(mainKey).getKeys(false));

            for (String childKey : child_keys) {

                try {

                    invs.put(childKey, fromBase64((String) player_inv_saves.getConfigurationSection(mainKey).get(childKey)));


                } catch (Exception e) {

                    e.printStackTrace();

                }
            }

            mapFileContetnts.put(pid, invs);

        }


        return mapFileContetnts;
    }

    private void writePlayersInSaveAreaToTextFile(HashMap<UUID, List<Float>> playersinsavearea) {

        File playersinsaveareafile = new File(fl.getPlayersinsavearea());
        File playersinsaveareafilebackup = new File(fl.getPlayersinsaveareabackup());

        try {

            copyFileToFile(playersinsaveareafile, playersinsaveareafilebackup);

        } catch (Exception e) {

            e.printStackTrace();

        }



        try (BufferedWriter bf = new BufferedWriter(new FileWriter(playersinsaveareafile))) {

            new FileWriter(playersinsaveareafile, false).close();

            for (Map.Entry<UUID, List<Float>> entry : playersinsavearea.entrySet()) {

                UUID player = entry.getKey();

                float f1 = entry.getValue().get(0);
                float f2 = entry.getValue().get(1);

                bf.write(player + "|" + f1 + "|" + f2);

                bf.newLine();

            }

            bf.flush();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void writePlayerlistToTextFile(HashMap<UUID, String> playerlist) {

        File playerlistfile = new File(fl.getPlayerlist());

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(playerlistfile))) {

            for (Map.Entry<UUID, String> entry : playerlist.entrySet()) {

                bf.write(entry.getKey() + "|" + entry.getValue());

                bf.newLine();

            }

            bf.flush();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    private void writeXpSaveAreasToTextFile(HashMap<String, List<Location>> xpsaveareas) {

        File xpsaveareafile = new File(fl.getXpsaveareas());
        File xpsaveareafilebackup = new File(fl.getXpsaveareasbackup());

        try {

            copyFileToFile(xpsaveareafile, xpsaveareafilebackup);

        } catch (Exception e) {

            e.printStackTrace();

        }

        try (BufferedWriter bf = new BufferedWriter(new FileWriter(xpsaveareafile))) {

            new FileWriter(xpsaveareafile, false).close();

            for (Map.Entry<String, List<Location>> entry : xpsaveareas.entrySet()) {

                double x1 = entry.getValue().get(0).getX();
                double y1 = entry.getValue().get(0).getY();
                double z1 = entry.getValue().get(0).getZ();
                String world1 = entry.getValue().get(0).getWorld().getName();

                double x2 = entry.getValue().get(1).getX();
                double y2 = entry.getValue().get(1).getY();
                double z2 = entry.getValue().get(1).getZ();
                String world2 = entry.getValue().get(1).getWorld().getName();

                String sep = "|";

                bf.write(entry.getKey() + sep + world1 + sep + x1 + sep + y1 + sep + z1 + sep + world2 + sep + x2 + sep + y2 + sep + z2);

                bf.newLine();

            }

            bf.flush();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }


    /**
     * Converts the player inventory to a String array of Base64 strings. First string is the content and second string is the armor.
     *
     * @param playerInventory to turn into an array of strings.
     * @return Array of strings: [ main content, armor content ]
     * @throws IllegalStateException
     */
    public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
        //get the main content part, this doesn't return the armor
        String content = toBase64(playerInventory);
        String armor = itemStackArrayToBase64(playerInventory.getArmorContents());

        return new String[] { content, armor };
    }

    /**
     *
     * A method to serialize an {@link ItemStack} array to Base64 String.
     *
     * <p />
     *
     * Based off of {@link #toBase64(Inventory)}.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (int i = 0; i < items.length; i++) {
                dataOutput.writeObject(items[i]);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * A method to serialize an inventory to Base64 string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param inventory to serialize
     * @return Base64 string of the provided inventory
     * @throws IllegalStateException
     */
    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            dataOutput.writeObject(inventory.getHolder());

            //dataOutput.writeObject(inventory.getType());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     *
     * A method to get an {@link Inventory} from an encoded, Base64, string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param data Base64 string of data containing an inventory.
     * @return Inventory created from the Base64 string.
     * @throws IOException
     */
    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            int size = dataInput.readInt();
            InventoryHolder holder = (InventoryHolder) dataInput.readObject();
            //InventoryType type = (InventoryType) dataInput.readObject();
            Inventory inventory = Bukkit.getServer().createInventory(null, size);

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * <p />
     *
     * Base off of {@link #fromBase64(String)}.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }



    public Logger getCustomLogger() {
        return logger;
    }

    public PlayerMessages getPlayerMessages() {

        return plmsg;

    }

    public Boolean getFirstrun() {
        return firstrun;
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

    public static void copyFileToFile(final File src, final File dest) throws IOException
    {
        copyInputStreamToFile(new FileInputStream(src), dest);
        dest.setLastModified(src.lastModified());
    }

    public static void copyInputStreamToFile(final InputStream in, final File dest)
            throws IOException
    {
        copyInputStreamToOutputStream(in, new FileOutputStream(dest));
    }


    public static void copyInputStreamToOutputStream(final InputStream in, final OutputStream out) throws IOException
    {
        try (in) {
            try (out) {
                final byte[] buffer = new byte[1024];
                int n;
                while ((n = in.read(buffer)) != -1)
                    out.write(buffer, 0, n);

            }
        }
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

    public List<String> getBlockedStrings() {
        return blockedStrings;
    }
}
