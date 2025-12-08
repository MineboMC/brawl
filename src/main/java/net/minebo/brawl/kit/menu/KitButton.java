package net.minebo.brawl.kit.menu;

import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.menu.construct.Button;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class KitButton extends Button {

    private final BrawlProfile profile;
    private final Kit kit;

    public KitButton(Player player, BrawlProfile profile, Kit kit) {
        this.profile = profile;
        this.kit = kit;

        setName(() -> kit.getColoredName());
        setLines(() -> getDescription());

        // Left-click action
        addClickAction(ClickType.LEFT, p -> {
            if (profile.ownsKit(kit)) {
                kit.apply(p);
                p.closeInventory();
            } else {
                if (profile.money.get() >= kit.getPrice()) {
                    profile.money.sub(kit.getPrice());
                    profile.ownedKits.add(kit.getName());
                    player.sendMessage(ColorUtil.translateColors("&7You have purchased &a" + kit.getColoredName() + "&7."));
                } else {
                    player.sendMessage(ColorUtil.translateColors("&cYou do not have enough money for this kit."));
                }
            }
        });
    }

    public List<String> getDescription() {
        List<String> description = new ArrayList<>();

        description.add(ColorUtil.translateColors("&f" + kit.getDescription()));
        description.add("");

        if (profile.ownsKit(kit)) {
            description.add("&aYou own this kit!");
            description.add("");
            description.add("&7Left click to equip!");
        } else {
            description.add("&cYou don't own this kit.");
            description.add("&fPrice: &2$&a" + kit.getPrice());
            description.add("");
            description.add("&7Left click to purchase!");
        }

        return description;
    }

    @Override
    public ItemStack build() {
        ItemStack item = kit.getIcon();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(this.name.get());
            meta.setLore(this.lines.get());
            item.setItemMeta(meta);
        }

        return item;
    }
}