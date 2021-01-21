/*
 * BanItem - Lightweight, powerful & configurable per world ban item plugin
 * Copyright (C) 2021 André Sustac
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your action) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.andross.banitem.database.items;

import fr.andross.banitem.BanItem;
import fr.andross.banitem.items.CustomBannedItem;
import fr.andross.banitem.utils.DoubleMap;
import fr.andross.banitem.utils.statics.Chat;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Map that contains all the custom items
 * This is a double map <i>(include a reversed map)</i>, for easier access of
 * custom items names and their respective banned item.
 * @version 3.0
 * @author Andross
 */
public final class CustomItems extends DoubleMap<String, CustomBannedItem> {
    private final File file;
    private final FileConfiguration config;

    /**
     * This will create a new instance of custom items map, with the items from <i>customitems.yml</i> file.
     * This should not be used externally, as it could create two different instance of this object.
     * You should use {@link fr.andross.banitem.BanItemAPI#load(CommandSender, File)} instead.
     * @param pl main instance
     * @param sender the sender who executed this command, for debug
     */
    public CustomItems(@NotNull final BanItem pl, @NotNull final CommandSender sender) {
        this.file = new File(pl.getDataFolder(), "customitems.yml");
        if (!file.exists()) pl.saveResource("customitems.yml", false);
        this.config = YamlConfiguration.loadConfiguration(file);

        // Loading custom items
        for (final String key : config.getKeys(false)) {
            final ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;

            final String materialName = section.getString("material");
            if (materialName == null) {
                sender.sendMessage(pl.getBanConfig().getPrefix() + Chat.color("&c[CustomItems] No material set for custom item &e" + key + "&c in customitems.yml."));
                continue;
            }

            final Material material = Material.matchMaterial(materialName);
            if (material == null) {
                sender.sendMessage(pl.getBanConfig().getPrefix() + Chat.color("&c[CustomItems] Invalid material set for custom item &e" + key + "&c in customitems.yml."));
                continue;
            }

            try {
                put(key, new CustomBannedItem(material, key.toLowerCase(), section));
            } catch (final Exception e) {
                sender.sendMessage(pl.getBanConfig().getPrefix() + Chat.color("&c[CustomItems] Invalid custom item &e" + key + "&c in customitems.yml: &c" + e.getMessage()));
            }
        }
    }

    /**
     * @return the "customitems.yml" file of the BanItem plugin
     */
    @NotNull
    public File getFile() {
        return file;
    }

    /**
     * @return the "customitems.yml" file configuration
     */
    @NotNull
    public FileConfiguration getConfig() {
        return config;
    }
}