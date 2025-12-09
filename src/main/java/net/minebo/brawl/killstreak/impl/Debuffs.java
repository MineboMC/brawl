package net.minebo.brawl.killstreak.impl;

import com.github.retrooper.packetevents.protocol.potion.Potion;
import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.InventoryUtil;
import net.minebo.cobalt.util.ItemBuilder;
import net.minebo.cobalt.util.PotionBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionType;

public class Debuffs extends KillStreak {

    @Override
    public String getName() {
        return "Debuffs";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_GREEN;
    }

    @Override
    public String getDescription() {
        return "Gives you debuffs.";
    }

    @Override
    public ItemStack getIcon() {
        return new PotionBuilder(Material.SPLASH_POTION).setBasePotionType(PotionType.POISON).setSize(7).build();
    }

    @Override
    public Integer getKills() {
        return 7;
    }

    @Override
    public void doReward(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);

        if(profile == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.setItem(InventoryUtil.getFirstEmptySlot(inventory, Material.MUSHROOM_STEW, Material.BOWL), new PotionBuilder(Material.SPLASH_POTION).setBasePotionType(PotionType.POISON).build());
        inventory.setItem(InventoryUtil.getFirstEmptySlot(inventory, Material.MUSHROOM_STEW, Material.BOWL), new PotionBuilder(Material.SPLASH_POTION).setBasePotionType(PotionType.SLOWNESS).build());
    }

}
