package net.minebo.brawl.kit.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.kit.Kit;
import net.minebo.cobalt.util.ItemBuilder;
import net.minebo.cobalt.util.PotionBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public class Chemist extends Kit {

    @Override
    public ItemStack getIcon() { return new PotionBuilder(Material.SPLASH_POTION).setBasePotionType(PotionType.HARMING).build(); }

    @Override
    public String getName() {
        return "Chemist";
    }

    @Override
    public ChatColor getColor() { return ChatColor.DARK_PURPLE; }

    @Override
    public String getDescription() {
        return "Use potions to weaken your enemies!";
    }

    @Override
    public Integer getPrice() { return 0; }

    @Override
    public List<ItemStack> getDefaultItems() {
        return List.of(new ItemBuilder(Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.SHARPNESS, 1)
                .setUnbreakable(true)
                .build(),
                new PotionBuilder(Material.SPLASH_POTION)
                        .setBasePotionType(PotionType.STRONG_HARMING)
                        .setSize(3)
                        .build(),
                new PotionBuilder(Material.SPLASH_POTION)
                        .setBasePotionType(PotionType.STRONG_POISON)
                        .build()
        );
    }

    @Override
    public List<ItemStack> getArmor() {
        return List.of(
                new ItemBuilder(Material.CHAINMAIL_HELMET)
                        .addEnchantment(Enchantment.PROTECTION, 1)
                        .build(),
                new ItemBuilder(Material.IRON_CHESTPLATE)
                        .addEnchantment(Enchantment.PROTECTION, 1)
                        .build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS)
                        .addEnchantment(Enchantment.PROTECTION, 1)
                        .build(),
                new ItemBuilder(Material.CHAINMAIL_BOOTS)
                        .addEnchantment(Enchantment.PROTECTION, 1)
                        .build()
        );
    }

    @Override
    public List<PotionEffect> getEffects() {
        return List.of(new PotionEffect(PotionEffectType.SPEED, -1, 1));
    }

}