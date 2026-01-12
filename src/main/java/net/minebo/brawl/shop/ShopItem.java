package net.minebo.brawl.shop;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.shop.button.ShopButton;
import net.minebo.brawl.shop.impl.*;
import net.minebo.cobalt.menu.construct.Menu;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class ShopItem implements Listener {

    abstract public String getName();
    abstract public ChatColor getColor();

    abstract public String getDescription();
    abstract public ItemStack getIcon();
    abstract public Integer getPrice();

    public String getColoredName() {
        return getColor() + getName();
    }

    public abstract void givePurchase(Player player);

    public static List<ShopItem> shopItems = List.of();

    public ShopItem() {
        Bukkit.getPluginManager().registerEvents(this, Brawl.getInstance());
    }

    public static void init() {
        shopItems = List.of(
                new Repair(),
                new Gapples(),
                new Cobwebs(),
                new Adrenaline()
        );
    }

    public static ShopItem get(String name) {
        return shopItems.stream().filter(k -> k.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void buy(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);

        if(profile.getSelectedKit() == null) {
            player.sendMessage(ColorUtil.translateColors("&cYou can only buy items with a kit selected."));
            return;
        }

        if(profile.money.get() < getPrice()) {
            player.sendMessage(ColorUtil.translateColors("&cYou don't have enough money to buy this."));
            return;
        }

        profile.money.sub(getPrice());
        givePurchase(player);

        player.sendMessage(ColorUtil.translateColors("&7You have bought " + getColoredName() + "&7."));
    }

    public static void openMenu(Player player) {
        Menu menu = new Menu().setTitle(ColorUtil.translateColors("Shop"));

        BrawlProfile profile = BrawlProfile.get(player);

        int i = 0;
        for(ShopItem shopItem : shopItems) {

            menu.setButton(i, new ShopButton(player, profile, shopItem));

            i++;
        }

        menu.openMenu(player);
    }

}
