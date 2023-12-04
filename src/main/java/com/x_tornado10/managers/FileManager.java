package com.x_tornado10.managers;

import com.x_tornado10.craftiservi;
import com.x_tornado10.utils.Paths;
import com.x_tornado10.utils.custom_data.inv_request.RestoreRequest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;


import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import static com.x_tornado10.utils.data.convert.ToFromBase64.*;

public class FileManager {

    craftiservi plugin;
    Paths paths;
    Logger setupLogger;
    private final File playerlist;
    private final File xpsaveareas;
    private final File xpsaveareasbackup;
    private final File backupdir;
    private final File playersinsavearea;
    private final File playersinsaveareabackup;
    private final File player_inv_saves;
    private final File player_inv_saves_backup;
    private final File backup_config;
    private final File afkPlayers;
    private final File restoreRequests;

    /**
     * @implNote Creates specified files. Should fail if the specified file already exists.
     * @param paths
     */
    public FileManager(craftiservi plugin, Paths paths, Logger setupLogger) {

        this.plugin = plugin;
        this.paths = paths;
        this.setupLogger = setupLogger;

        playerlist = new File(paths.getPlayerlist());
        xpsaveareas = new File(paths.getXpsaveareas());
        xpsaveareasbackup = new File(paths.getXpsaveareasbackup());
        backupdir = new File(paths.getBackupdir());
        playersinsavearea = new File(paths.getPlayersinsavearea());
        playersinsaveareabackup = new File(paths.getPlayersinsaveareabackup());
        player_inv_saves = new File(paths.getPlayer_inv_saves());
        player_inv_saves_backup = new File(paths.getPlayer_inv_saves_backup());
        backup_config = new File(paths.getBackup_config());
        afkPlayers = new File(paths.getAfk_players());
        restoreRequests = new File(paths.getRestoreRequests());

    }

    public boolean createFiles() {

        ArrayList<File> files = new ArrayList<>();

        files.add(playerlist);
        files.add(xpsaveareas);
        files.add(xpsaveareasbackup);
        files.add(playersinsavearea);
        files.add(playersinsaveareabackup);
        files.add(player_inv_saves);
        files.add(player_inv_saves_backup);
        files.add(backup_config);
        files.add(afkPlayers);
        files.add(restoreRequests);

        if (backupdir.mkdirs()) {
            setupLogger.info(backupdir.getName() + " was successfully created!");
        }

        for (File file : files) {
            if (!file.exists()) {
                try {
                    if(file.createNewFile()) {
                        setupLogger.info(file.getName() + " was successfully created!");
                    }
                } catch (IOException e) {
                    setupLogger.info("§cSomething went wrong while trying to create files! Please restart the server!§r");
                    return false;
                }
            }
        }
        return true;
    }

    public boolean deleteConfig() {

        File config = new File(paths.getConfig());
        return config.delete();

    }
    public HashMap<UUID, String> getPlayerListFromTextFile() {

        File playerlist = new File(paths.getPlayerlist());
        HashMap<UUID, String> mapFileContents = new HashMap<>();

        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(playerlist));
            String line;
            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\\|");
                String date = parts[1].trim();
                UUID UUID = java.util.UUID.fromString(parts[0].trim());

                if (!date.isEmpty() && !UUID.toString().isEmpty()) {
                    mapFileContents.put(UUID, date);
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
    public HashMap<UUID, List<Float>> getPlayersInSaveAreaFromTextFile() {

        File playersinsavearea = new File(paths.getPlayersinsavearea());
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
    public HashMap<String, List<Location>> getXpSaveAreaFromTextFile() {

        File xpsavearea = new File(paths.getXpsaveareas());
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
    public void writePlayerInvSavesToTextFile(HashMap<UUID, HashMap<String, Inventory>> playerinvsaves) {

        FileConfiguration player_inv_saves = new YamlConfiguration();

        File player_inv_saves_file = new File(paths.getPlayer_inv_saves());
        File player_inv_saves_backup_file = new File(paths.getPlayer_inv_saves_backup());

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

            player_inv_saves.save(paths.getPlayer_inv_saves());

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
    public List<RestoreRequest> getRestoreRequestsFromTextFile() {

        YamlConfiguration rR_file = new YamlConfiguration();
        try {
            rR_file.load(restoreRequests);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        List<RestoreRequest> result = new ArrayList<>();
        for (String key : rR_file.getKeys(false)) {
            String[] parts = key.split("\\$");

            UUID reviewer = null;
            if (!parts[0].trim().equals("null")) {
                reviewer = UUID.fromString(parts[0].trim());
            }
            UUID requester = UUID.fromString(parts[1].trim());
            String inv_name = parts[2].trim();
            boolean reviewed = Boolean.parseBoolean(parts[3].trim());
            boolean approved = Boolean.parseBoolean(parts[3].trim());
            RestoreRequest aR0 = new RestoreRequest(requester,inv_name);
            aR0.setReviewer(reviewer);
            aR0.setApproved(approved);
            aR0.setReviewed(reviewed);
            result.add(aR0);
        }
        return result;
    }
    public HashMap<UUID, HashMap<String, Inventory>> getPlayerInvSavePointsFromTextFile() {

        HashMap<UUID, HashMap<String, Inventory>> mapFileContetnts = new HashMap<>();

        FileConfiguration player_inv_saves = new YamlConfiguration();

        File player_inv_saves_file = new File(paths.getPlayer_inv_saves());

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
    public void writePlayersInSaveAreaToTextFile(HashMap<UUID, List<Float>> playersinsavearea) {

        File playersinsaveareafile = new File(paths.getPlayersinsavearea());
        File playersinsaveareafilebackup = new File(paths.getPlayersinsaveareabackup());

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
    public void writePlayerlistToTextFile(HashMap<UUID, String> playerlist) {

        File playerlistfile = new File(paths.getPlayerlist());

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
    public void writeXpSaveAreasToTextFile(HashMap<String, List<Location>> xpsaveareas) {

        File xpsaveareafile = new File(paths.getXpsaveareas());
        File xpsaveareafilebackup = new File(paths.getXpsaveareasbackup());

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
    public void writeRestoreRequestsToTextFile(List<RestoreRequest> rRe) {

        YamlConfiguration rR_file = new YamlConfiguration();

        for (RestoreRequest rR : rRe) {
            UUID reviewer = rR.getReviewer();
            UUID requester = rR.getRequester();
            String inv_name = rR.getInvName();
            boolean reviewed = rR.isReviewed();
            boolean approved = rR.isApproved();
            String sep = "$";

            String result = reviewer + sep + requester + sep + inv_name + sep + reviewed + sep + approved;
            rR_file.createSection(result);
        }
        try {
            rR_file.save(restoreRequests);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

}
