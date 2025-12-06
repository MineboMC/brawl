package net.minebo.brawl.kit.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.kit.Kit;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

// use this as a template for all kits
public class PvP extends Kit {

    @Override
    public Material getIcon() {
        return Material.DIAMOND_SWORD;
    }

    @Override
    public String getName() {
        return "PvP";
    }

    @Override
    public ChatColor getColor() { return ChatColor.YELLOW; }

    @Override
    public String getDescription() {
        return "The standard pvp kit!";
    }

    @Override
    public Integer getPrice() { return 0; }

    @Override
    public List<ItemStack> getDefaultItems() {
        return List.of(new ItemBuilder(Material.DIAMOND_SWORD)
                        .addEnchantment(Enchantment.SHARPNESS, 1)
                        .setUnbreakable(true)
                        .build()
        );
    }

    @Override
    public List<ItemStack> getArmor() {
        return List.of(
                new ItemStack(Material.IRON_HELMET),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_BOOTS)
        );
    }

    @Override
    public List<PotionEffect> getEffects() {
        return List.of(new PotionEffect(PotionEffectType.SPEED, -1, 0));
    }

}