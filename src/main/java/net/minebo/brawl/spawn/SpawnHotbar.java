package net.minebo.brawl.spawn;

import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SpawnHotbar {

    public static ItemStack KIT_SELECTOR = new ItemBuilder(Material.ENCHANTED_BOOK)
            .setName(ColorUtil.translateColors("&6Kit Selector"))
            .setLore(ColorUtil.translateColors("&8Right click to select a kit."))
            .build();

    public static ItemStack LAST_KIT = new ItemBuilder(Material.CLOCK)
            .setName(ColorUtil.translateColors("&9Last Kit"))
            .setLore(ColorUtil.translateColors("&8Right click to use the last used kit."))
            .build();

    public static void giveItems(Player player) {
        PlayerInventory inv = player.getInventory();

        inv.setItem(0, SpawnHotbar.KIT_SELECTOR);

        BrawlProfile profile = BrawlProfile.get(player);

        if(profile.lastKit != null) {
            if(Kit.get(profile.lastKit) != null) {
                inv.setItem(1, SpawnHotbar.LAST_KIT);
            }
        }
    }

}
