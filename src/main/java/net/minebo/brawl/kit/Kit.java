package net.minebo.brawl.kit;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.kit.impl.*;

import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public abstract class Kit implements Listener {

    public static List<Kit> kits = new ArrayList<>();

    public abstract Material getIcon();
    public abstract String getName();
    public abstract ChatColor getColor();
    public abstract String getDescription();
    public abstract Integer getPrice();

    public abstract List<ItemStack> getDefaultItems();
    public abstract List<ItemStack> getArmor();

    public abstract List<PotionEffect> getEffects();

    public String getColoredName() {
        return getColor() + getName();
    }

    public ItemStack getAbilityItem() {
        return null;
    }

    public static Boolean freeKitMode;

    public static void init() {
        registerKits();
        registerCooldowns();

        freeKitMode = Brawl.getInstance().getConfig().getBoolean("manage.freekitsmode", false);
    }

    public Boolean hasKitOn(Player player) {
        if(BrawlProfile.get(player).getSelectedKit() == null) return false;
        return BrawlProfile.get(player).getSelectedKit().equals(this);
    }

    public static Kit get(String kitName) { return kits.stream().filter(n -> n.getName().equalsIgnoreCase(kitName)).findFirst().orElse(null); }

    public void apply(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);
        PlayerInventory inv = player.getInventory();

        clear(player);

        // wax on
        getDefaultItems().forEach(inv::addItem);

        profile.setSelectedKit(this);
        profile.setLastKit(this.getName());

        inv.setHelmet(getArmor().get(0));
        inv.setChestplate(getArmor().get(1));
        inv.setLeggings(getArmor().get(2));
        inv.setBoots(getArmor().get(3));

        fillInvWithSoup(player);

        getEffects().forEach(player::addPotionEffect);

        player.sendMessage(ColorUtil.translateColors("&7You have chosen the &a" + getColoredName() + "&7 kit."));
    }

    public static void clear(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);
        PlayerInventory inv = player.getInventory();

        profile.setSelectedKit(null);

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);

        inv.clear();
        player.clearActivePotionEffects();

        Brawl.getInstance().getCooldownHandler().cooldownMap.values().forEach(cd -> cd.removeCooldown(player));

        player.setAllowFlight(false);
        player.setFlying(false);
    }

    public static void fillInvWithSoup(Inventory inventory) {
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if(item == null) inventory.setItem(i, new ItemStack(Material.MUSHROOM_STEW));
        }

        if(inventory instanceof PlayerInventory) { // no offhand
            PlayerInventory playerInventory = (PlayerInventory) inventory;
            playerInventory.setItemInOffHand(null);
        }
    }

    public static void fillInvWithSoup(Player player) {
        PlayerInventory playerInventory = player.getInventory();

        for(int i = 0; i < playerInventory.getSize(); i++) {
            ItemStack item = playerInventory.getItem(i);
            if(item == null) playerInventory.setItem(i, new ItemStack(Material.MUSHROOM_STEW));
        }

        playerInventory.setItemInOffHand(null);
        player.updateInventory();
    }

    public void register() {
        Kit.kits.add(this);
        Bukkit.getPluginManager().registerEvents(this, Brawl.getInstance());
    }

    public static void registerKits() {
        new PvP().register();
        new Phantom().register();
        new Zeus().register();
        new Stomper().register();
        new Melon().register();
        new Avatar().register();
    }

    public static void registerCooldowns() {
        Brawl.getInstance().getCooldownHandler().registerCooldown("Flight", new Cooldown());
        Brawl.getInstance().getCooldownHandler().registerCooldown("Bolt", new Cooldown());
        Brawl.getInstance().getCooldownHandler().registerCooldown("Stomp", new Cooldown());
        Brawl.getInstance().getCooldownHandler().registerCooldown("Melon Toss", new Cooldown());
        Brawl.getInstance().getCooldownHandler().registerCooldown("Water Gun", new Cooldown());
        Brawl.getInstance().getCooldownHandler().registerCooldown("Avatar Jump", new Cooldown());
    }

}
