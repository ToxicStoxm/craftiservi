package com.x_tornado10.handlers;


import com.x_tornado10.craftiservi;
import com.x_tornado10.files.FileLocations;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DataHandler {

    private InputStream data;

    private File datafile;

    private craftiservi plugin;

    private YamlConfiguration dataconfig;

    private FileLocations fj;

    public DataHandler(InputStream data, File datafile) {

        this.data = data;
        this.datafile = datafile;
        plugin = craftiservi.getInstance();
        dataconfig = new YamlConfiguration();
        fj = new FileLocations();

    }

    public void configure() throws IOException {

        byte[] buffer = data.readAllBytes();

        OutputStream outStream = new FileOutputStream(datafile);
        outStream.write(buffer);

        outStream.close();

   }

   public void setData() throws IOException, InvalidConfigurationException {

        dataconfig.load(datafile);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        //YML Paths
        String InstalledOn = fj.getYmlInstalledOn();
        String PluginVersion = fj.getYmlVersion();
        String TimesStartedReloaded = fj.getYmlTimesStartedReloaded();
        String RegisteredPlayers = fj.getYmlRegisteredPlayers();


        if (Objects.equals(dataconfig.getString(InstalledOn), "") || dataconfig.getString(InstalledOn) == null) {

            dataconfig.set(InstalledOn, formatter.format(date));

        }

        dataconfig.set(PluginVersion, plugin.getDescription().getVersion());

        if (dataconfig.get(TimesStartedReloaded) == null) {

            dataconfig.set(TimesStartedReloaded, 0);

        }

        dataconfig.set(TimesStartedReloaded, dataconfig.getInt(TimesStartedReloaded) + plugin.getTimesstartedreloaded());

        dataconfig.set(RegisteredPlayers, plugin.getRegisteredPlayers());

        dataconfig.save(datafile);

   }

}
