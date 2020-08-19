/*
 * BanItem - Lightweight, powerful & configurable per world ban item plugin
 * Copyright (C) 2020 André Sustac
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.andross.banitem.utils.item;

import fr.andross.banitem.utils.BanVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An item wrapper, which can handle matching Material/ItemStacks with their ItemMeta and not considering the amount
 * @version 2.4
 * @author Andross
 */
public final class BannedItem {
    private final Material m;
    private final ItemMeta meta;
    private final short data;
    private final boolean includeMeta;

    /**
     * Creating an item without metadata
     * @param m material
     */
    public BannedItem(@NotNull final Material m) {
        this.m = m;
        meta = null;
        data = 0;
        includeMeta = false;
    }

    /**
     * Creating an item, include the ItemMeta of the item
     * @param item itemstack
     */
    public BannedItem(@NotNull final ItemStack item) {
        this(item, true);
    }

    /**
     * Creating an item, including or not its ItemMeta
     * @param item itemstack
     * @param includeMeta if the item will consider the ItemMeta of the item
     */
    public BannedItem(@NotNull final ItemStack item, final boolean includeMeta) {
        this.m = item.getType();
        this.meta = item.getItemMeta();
        this.includeMeta = includeMeta;
        data = BanVersion.v13OrMore ? 0 : item.getDurability();
    }

    public BannedItem(@NotNull final BannedItem item, final boolean includeMeta) {
        this.m = item.getType();
        this.meta = item.getMeta();
        this.includeMeta = includeMeta;
        this.data = item.getData();
    }

    /**
     * @return the material of the item
     */
    @NotNull
    public Material getType() {
        return m;
    }

    /**
     * @return the item meta
     */
    @Nullable
    public ItemMeta getMeta() {
        return meta;
    }

    /**
     * @return the item data, ignored after 1.13+
     */
    public short getData() {
        return data;
    }

    /**
     * @return true if the item should include the itemmeta, otherwise false
     */
    public boolean isIncludeMeta() {
        return includeMeta;
    }

    /**
     * @return a cloned ItemStack representing this item
     */
    public ItemStack toItemStack() {
        final ItemStack item = new ItemStack(m);
        if (meta != null) item.setItemMeta(meta.clone());
        if (!BanVersion.v13OrMore) item.setDurability(data);
        return item;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BannedItem that = (BannedItem) o;
        if (!includeMeta) return BanVersion.v13OrMore ? m == that.m : (m == that.m && data == that.data);
        return m == that.m && Objects.equals(meta, that.meta) && (BanVersion.v13OrMore || Objects.equals(data, that.data));
    }

    @Override
    public int hashCode() {
        if (!includeMeta) return BanVersion.v13OrMore ? m.hashCode() : Objects.hash(m, data);
        return BanVersion.v13OrMore ? Objects.hash(m, meta) : Objects.hash(m, meta, data);
    }
}
