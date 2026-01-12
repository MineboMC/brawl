package net.minebo.brawl.shop.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Repair extends ShopItem {

    @Override
    public String getName() {
        return "Repair";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public String getDescription() {
        return "Repairs your armor.";
    }

    @Override
    public ItemStack getIcon() {
        return ItemStack.of(Material.ANVIL);
    }

    @Override
    public Integer getPrice() {
        return 50;
    }

    @Override
    public void givePurchase(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);
        Kit kit = profile.getSelectedKit();

        PlayerInventory inv = player.getInventory();

        inv.setHelmet(kit.getArmor().get(0));
        inv.setChestplate(kit.getArmor().get(1));
        inv.setLeggings(kit.getArmor().get(2));
        inv.setBoots(kit.getArmor().get(3));
    }

}
