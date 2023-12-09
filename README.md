![Craftiservi Logo](https://www.crafti-servi.com/plugin-resources/craftiservi/Plugin-Logo/CSP.png)

# Craftiservi
[![Spigot Version](https://img.shields.io/spiget/version/108546?logo=spigotmc&label=release&color=%2365bfdd&logoColor=%23ffffff)](https://plugin.crafti-servi.com/)
[![Spigot Downloads](https://img.shields.io/spiget/downloads/108546?logo=spigotmc&color=%2365bfdd&logoColor=%23ffffff)]()
[![Latest Version](https://img.shields.io/github/v/release/ToxicStoxm/craftiservi.svg?logo=github&color=%2365bfdd&logoColor=%23ffffff)](https://github.com/ToxicStoxm/craftiservi/releases/latest)
[![GitHub All Releases](https://img.shields.io/github/downloads/ToxicStoxm/craftiservi/total.svg?logo=github&color=%2365bfdd&logoColor=%23ffffff)]()
[![Discord](https://img.shields.io/discord/1182474566501679206.svg?logo=discord&label=discord&color=%2365bfdd&logoColor=%23ffffff)](https://discord.crafti-servi.com/)

#### Craftiservi is a Bukkit/Spigot Plugin for your Minecraft survival server. The plugin includes the following features:

## Commands:

* ### /craftiservi
    - **Usage:** `/craftiservi <help-reloadconfig-resetconfig-restoreconfig>`  
      This is the main plugin command, used to reload, reset, and restore the plugin config without server reloads.

* ### /firstjoin
    - **Usage:** `/firstjoin <Player>`  
      Displays the date and time a player first joined the server.

* ### /xparea
    - **Usage:** `/xparea add-remove-edit-help <AreaName> <WorldName> <Location> <Location>`  
      Create XP areas; when players enter, their XP level is saved and restored when they exit. Useful for PvP arenas.

* ### /invsave
    - **Usage:** `/invsave <new-remove-rename-view-list-restore> <InvName> <NewInvName>`  
      Handles InventorySavePoints, snapshots of player inventories. Create, rename, remove, view, list, and restore snapshots. Includes a restore system where admin review is required.

* ### /adminchat
    - **Usage:** `/adminchat <Message>`  
      Sends messages in the built-in Admin Chat, also used for /invsave restore requests.

* ### /opengui
    - **Usage:** `/opengui <GUI_ID>`  
      Internal command used by the plugin for handling reviews. Only for debugging.

## Other Features:

* ### Console/Log Filtering
    - **Description:**  
      Prevents the display of /msg or /tell commands in the console to limit administrative access to player conversations. Configurable to filter other specific commands or words.

* ### Welcome Message/First Join Title
    - **Description:**  
      Greets players with a welcome message in the action bar upon joining. Additionally, upon their first entry, a large title displays the date and time of their initial join, after which only the welcome message appears in the action bar.

* ### Custom Config File
    - **Description:**  
      Includes a comprehensive config file to customize nearly every aspect of the plugin.

* ### Custom Logger/Debug Mode
    - **Description:**  
      Features a built-in debug mode for detailed plugin feedback.

* ### Update Checker
    - **Description:**  
      Automatically checks for updates on server reloads/restarts and alerts admins about new versions.


## Dependencies:
- **LuckPerms** [`Luckperms.net`](https://luckperms.net)  
  Required for AFK-checking, admin-chat, and other plugin features.


## Additional Info:
- **Issues and Bugs:**  
  For issues, join the [`Discord Server`](https://discord.crafti-servi.com/) or open an issue on this Repository.
- **Feedback and Feature Requests**  
  Provide feedback or request features on the [`Discord Server`](https://discord.crafti-servi.com/).

## Statistics (powered by [`BStats`](https://github.com/Bastian/bStats))
[![Bukkit Stats](https://bstats.org/signatures/bukkit/craftiservi.svg)]()

## License
This project is licensed under the [`GPL-3.0 license`](https://github.com/ToxicStoxm/craftiservi/blob/main/LICENSE).
