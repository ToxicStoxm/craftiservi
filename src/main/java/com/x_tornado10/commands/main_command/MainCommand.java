package com.x_tornado10.commands.main_command;

import com.x_tornado10.craftiservi;
import com.x_tornado10.utils.Paths;
import com.x_tornado10.managers.ConfigManager;
import com.x_tornado10.logger.Logger;
import com.x_tornado10.messages.PlayerMessages;
import com.x_tornado10.utils.TextFormatting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainCommand implements CommandExecutor {

    private craftiservi plugin;
    private ConfigManager configManager;
    private Paths paths;
    private Logger logger;
    private PlayerMessages plmsg;
    private TextFormatting tf;
    private List<UUID> confirm_deletion;

    private final String helpmessage =
            """
                            \n§0##########################§r
                            §8-/craftiservi 
                            §7#displays current plugin-version and authors§r
                            
                            
                            §8-/craftiservi help 
                            §7#display this menu§r
                            
                            
                            §8-/craftiservi reloadconfig 
                            §7#reloads all config-values/variables§r
                            
                            
                            §8-/craftiservi resetconfig 
                            §7#resets the config to default-settings/values§r
                            §0##########################§r
                            """;

    public MainCommand(craftiservi plugin, ConfigManager configManager, Paths paths, Logger logger, PlayerMessages plmsg) {

        this.plugin = plugin;
        this.configManager = configManager;
        this.paths = paths;
        this.logger = logger;
        this.plmsg = plmsg;
        tf = new TextFormatting();
        confirm_deletion = new ArrayList<>();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player p = (Player) sender;
            UUID pid = p.getUniqueId();

            switch (args.length) {

                case 0 -> {

                    plmsg.msg(p,"craftiservi-" + plugin.getDescription().getVersion() + " by " + plugin.getDescription().getAuthors());
                    plmsg.msg(p,"[ /craftiservi help ] for more Information");

                }

                case 1 -> {

                    switch (args[0].toLowerCase()) {

                        case "help" -> plmsg.msg(p, helpmessage);

                        case "reloadconfig" -> {

                            plmsg.msg(p, "Reloading Config...");

                            if (configManager.reloadConfig(false)) {
                                plmsg.msg(p, "§cNote: Restart the server if any errors occur!§r");
                                plmsg.msg(p, "Successfully reloaded config!");
                                logger.info(p.getName() + " reloaded the config");
                            } else {

                                plmsg.msg(p, "§cPlease wait 5s before using this command again!§r");

                            }

                        }

                        case "resetconfig" -> {

                            if (confirm_deletion.contains(pid)) {

                                plmsg.msg(p, "Resetting...");
                                if (configManager.resetConfig()) {

                                   plmsg.msg(p, "Reset of 'config.yml' was successfully completed!");
                                   logger.info("Reset for file 'config.yml' was successfully completed!");

                                } else {

                                    plmsg.msg(p, "§cAn error occurred trying to reset 'config.yml'!");
                                    plmsg.msg(p, "§cPlease restart the server and/or delete 'config.yml' manually!");
                                    logger.severe("Reset for file 'config.yml' was not successfully completed!");
                                    logger.warning("Please restart the server to avoid any issues!");
                                }

                                confirm_deletion.remove(pid);


                            } else {

                                confirm_deletion.add(pid);
                                plmsg.msg(p, "Are you sure you want to §creset§r§7 'config_old.yml' to default?");
                                plmsg.msg(p, "Please execute the command again to confirm!");

                            }

                        }
                        case "restoreconfig" -> {
                            if (configManager.BackupConfigExists()) {
                                plmsg.msg(p,"File not found!");
                                plmsg.msg(p,"Can't restore values/variables from backup config!");
                                plmsg.msg(p,"This is normal if this is the first time running the plugin!");
                                logger.info("Config restore failed! Backup config file not found!");
                                return true;
                            }
                            plmsg.msg(p, "Restoring...");
                            if (configManager.restoreConfig()) {
                                plmsg.msg(p, "Successfully restored values/variables from the backup config!");
                                logger.info("Values from backup config were successfully restored!");

                            } else {

                                plmsg.msg(p, "§cAn error occurred trying to restore values from the backup config!");
                                plmsg.msg(p, "§cPlease restart the server to avoid any further issues!");
                                logger.severe("An error occurred trying to restore values from the backup config!");
                                logger.warning("Please restart the server to avoid any issues!");

                            }
                        }
                        default -> playerSendUsage(p);

                    }

                }
                default -> playerSendUsage(p);

            }


        } else {

            switch (args.length) {

                case 0 -> {

                    logger.info("");
                    logger.info("craftiservi-" + plugin.getDescription().getVersion() + " by " + plugin.getDescription().getAuthors());
                    logger.info("[ /craftiservi help ] for more Information");
                    logger.info("");

                }
                case 1 -> {

                    switch (args[0].toLowerCase()) {

                        case "help" -> logger.info(tf.stripColorAndFormattingCodes(helpmessage));
                        case "reloadconfig" -> {

                            logger.info("Reloading Config...");
                            logger.info("§cNote: Stop the server if any errors/issues occur!§r");
                            configManager.reloadConfig(true);
                            logger.info("Successfully reloaded config!");

                        }
                        case "resetconfig" -> {

                            logger.info("Resetting...");

                            if (configManager.resetConfig()) {

                                logger.info("Reset of 'config.yml' was successfully completed!");

                            } else {

                                logger.severe("Reset for file 'config.yml' was not successfully completed!");
                                logger.warning("Please restart the server to avoid any issues!");
                            }

                        }
                        case "restoreconfig" -> {
                            if (configManager.BackupConfigExists()) {
                                logger.warning("Config restore failed! Backup config file not found!");
                                logger.warning("This is normal if this is the first time running the plugin!");
                                return true;
                            }
                            logger.info("Restoring...");
                            if (configManager.restoreConfig()) {
                                logger.info("Values from backup config were successfully restored!");
                            } else {

                                logger.severe("An error occurred trying to restore values from the backup config!");
                                logger.warning("Please restart the server to avoid any issues!");

                            }
                        }
                        default -> sendUsage();

                    }

                }
                default -> sendUsage();

            }

        }
        return true;
    }

    private void sendUsage() {

        logger.info("Usage: /craftiservi <reloadconfig-resetconfig-help>");

    }

    private void playerSendUsage(Player p) {

        plmsg.msg(p, "Usage: /craftiservi <reloadconfig-resetconfig-help>");

    }

}
