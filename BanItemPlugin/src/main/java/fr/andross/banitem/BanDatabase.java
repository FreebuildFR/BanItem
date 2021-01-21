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
package fr.andross.banitem;

import fr.andross.banitem.actions.BanAction;
import fr.andross.banitem.database.Blacklist;
import fr.andross.banitem.database.Whitelist;
import fr.andross.banitem.database.items.CustomItems;
import fr.andross.banitem.database.items.Items;
import fr.andross.banitem.database.items.MetaItems;
import fr.andross.banitem.items.BannedItem;
import fr.andross.banitem.items.CustomBannedItem;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that contains all the maps
 * @version 3.0
 * @author Andross
 */
public final class BanDatabase {
    private final CustomItems customItems;
    private final MetaItems metaItems;
    private final Blacklist blacklist;
    private final Whitelist whitelist;

    /**
     * Loading the plugin database.
     * This should not be used externally.
     * Use {@link fr.andross.banitem.BanItemAPI#load(CommandSender, File)} instead.
     */
    BanDatabase(@NotNull final BanItem pl, @NotNull final CommandSender sender, @NotNull final FileConfiguration config) {
        this.customItems = new CustomItems(pl, sender);
        this.metaItems = new MetaItems(pl, sender);
        this.blacklist = new Blacklist(pl, this, sender, config.getConfigurationSection("blacklist"));
        this.whitelist = new Whitelist(pl, this, sender, config.getConfigurationSection("whitelist"));
    }

    /**
     * Getting an immutable set of used ban actions <i>({@link BanAction})</i>
     * This is actually used to register the specific listeners for the specific actions
     * @return An immutable set containing all used ban actions in the blacklist
     */
    @NotNull
    public Set<BanAction> getBlacklistActions() {
        final Set<BanAction> actions = new HashSet<>();
        blacklist.values().stream().map(Items::getAllActions).forEach(actions::addAll);
        return Collections.unmodifiableSet(actions);
    }

    /**
     * Try to add a meta item <i>({@link BannedItem})</i> into the map and the config file
     * @param metaName name of the meta item
     * @param metaItem ItemStack
     */
    public void addMetaItem(@NotNull final String metaName, @NotNull final ItemStack metaItem) {
        // Adding in map
        metaItems.put(metaName, new BannedItem(metaItem));

        // Adding in file
        final FileConfiguration config = metaItems.getConfig();
        config.set(metaName, metaItem);
        try {
            config.save(metaItems.getFile());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to remove the meta item with the said name.
     * You should check if the meta item exists before calling this method
     * @param metaItem name of the meta item
     */
    public void removeMetaItem(@NotNull final String metaItem) {
        // Removing from map
        metaItems.remove(metaItem);

        // Removing from file
        final FileConfiguration config = metaItems.getConfig();
        config.set(metaItem, null);
        try {
            config.save(metaItems.getFile());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public String getNameTest(@NotNull final BannedItem bannedItem) {
        if (bannedItem instanceof CustomBannedItem) {
            final CustomBannedItem customBannedItem = (CustomBannedItem) bannedItem;
            final String name = customItems.getKey(customBannedItem);
            if (name != null) return name;
        }
        final String metaName = metaItems.getKey(bannedItem);
        return metaName == null ? bannedItem.getType().name().toLowerCase() : metaName;
    }

    /**
     * Get the custom items map
     * @return map of custom items
     */
    @NotNull
    public CustomItems getCustomItems() {
        return customItems;
    }

    /**
     * Get the meta items map
     * @return map of meta items
     */
    @NotNull
    public MetaItems getMetaItems() {
        return metaItems;
    }

    /**
     * Get the blacklist map
     * @return map containing the blacklisted items
     */
    @NotNull
    public Blacklist getBlacklist() {
        return blacklist;
    }

    /**
     * Get the whitelist map
     * @return map containing the whitelisted items
     */
    @NotNull
    public Whitelist getWhitelist() {
        return whitelist;
    }
}
