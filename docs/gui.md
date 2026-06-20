# Graphical User Interface (GUI)

NexusBoosters features several menus for players and administrators. You can configure the layout and items of the player-facing menus in `gui.yml`.

## Menus
1.  **Booster Menu (`/nb open`)**: Displays the player's available booster tokens. Includes pagination if they own many boosters.
2.  **Confirm Menu**: Pops up when a player clicks a booster token, ensuring they don't accidentally activate it.
3.  **Active Menu (`/nb active`)**: Displays the boosters currently affecting the player (both global and their own personal ones).
4.  **Admin Menus (`/nb admin`)**: Backend tools for managing active boosters or giving boosters without commands.

## Configuration (`gui.yml`)
You can map out the inventory layout using character matrices (similar to standard Bukkit GUI libraries).
You can assign specific items, names, and lore to filler slots, next/previous page buttons, and back buttons.

## Bedrock Fallback
If you are running Geyser/Floodgate, Bedrock players might struggle with complex inventory GUIs. 
If `hooks.floodgate` is enabled in `config.yml`, Bedrock players attempting to open a GUI will instead receive a text-based, clickable fallback menu in chat. This ensures full cross-play compatibility without UI frustrations.
