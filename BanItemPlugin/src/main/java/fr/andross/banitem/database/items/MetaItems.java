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
import fr.andross.banitem.items.BannedItem;
import fr.andross.banitem.utils.DoubleMap;
import fr.andross.banitem.utils.statics.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Map that contains all the meta items
 * This is a double map <i>(include a reversed map)</i>, for easier access of
 * meta items names and their respective banned item.
 * @version 3.0
 * @author Andross
 */
public final class MetaItems extends DoubleMap<String, BannedItem> {
    private final File file;
    private final FileConfiguration config;

    /**
     * This will create a new instance of meta items map, with the items from <i>metaitems.yml</i> file.
     * This should not be used externally, as it could create two different instance of this object.
     * You should use {@link fr.andross.banitem.BanItemAPI#load(CommandSender, File)} instead.
     * @param pl main instance
     * @param sender the sender who executed this command, for debug
     */
    public MetaItems(@NotNull final BanItem pl, @NotNull final CommandSender sender) {
        this.file = new File(pl.getDataFolder(), "metaitems.yml");
        if (!file.exists()) pl.saveResource("metaitems.yml", false);
        this.config = YamlConfiguration.loadConfiguration(file);

        // Loading meta items
        for (final String key : config.getKeys(false)) {
            try {
                final ItemStack itemStack = (ItemStack) config.get(key);
                if (itemStack == null) throw new Exception();
                put(key, new BannedItem(itemStack));
            } catch (final Exception e) {
                e.printStackTrace();
                sender.sendMessage(pl.getBanConfig().getPrefix() + Chat.color("&cInvalid meta item &e" + key + "&c in metaitems.yml."));
            }
        }
    }

    /**
     * @return the file configuration used to create this instance
     */
    @NotNull
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * @return the "items.yml" file of the BanItem plugin
     */
    @NotNull
    public File getFile() {
        return file;
    }
}