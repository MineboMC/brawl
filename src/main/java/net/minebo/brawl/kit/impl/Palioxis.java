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

public class Palioxis extends Kit {

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.ENDER_PEARL);
    }

    @Override
    public String getName() {
        return "Palioxis";
    }

    @Override
    public ChatColor getColor() { return ChatColor.of("#95F5E3"); }

    @Override
    public String getDescription() {
        return "Use pearls to confuse your enemies!";
    }

    @Override
    public Integer getPrice() { return 0; }

    @Override
    public List<ItemStack> getDefaultItems() {
        return List.of(new ItemBuilder(Material.IRON_SWORD)
                .addEnchantment(Enchantment.SHARPNESS, 1)
                .setUnbreakable(true)
                .build(),
                new ItemStack(Material.ENDER_PEARL)
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
        return List.of(new PotionEffect(PotionEffectType.SPEED, -1, 1));
    }

}