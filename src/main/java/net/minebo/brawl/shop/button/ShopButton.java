package net.minebo.brawl.shop.button;

import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.shop.ShopItem;
import net.minebo.cobalt.menu.construct.Button;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopButton extends Button {

    private final BrawlProfile profile;
    private final ShopItem shopItem;

    public ShopButton(Player player, BrawlProfile profile, ShopItem shopItem) {
        this.profile = profile;
        this.shopItem = shopItem;

        setName(() -> shopItem.getName());
        setLines(() -> getDescription());

        // Left-click action
        addClickAction(ClickType.LEFT, p -> {
            shopItem.buy(p);
        });
    }

    public List<String> getDescription() {
        List<String> description = new ArrayList<>();

        description.add("<white>" + shopItem.getDescription());
        description.add("");

        description.add("<white>Price: <dark_green>$<green>" + shopItem.getPrice());
        description.add("");
        description.add("<gray>Left click to purchase!");

        return description;
    }

    @Override
    public ItemStack build() {
        ItemStack item = shopItem.getIcon();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(this.name.get());
            meta.setLore(this.lines.get());
            item.setItemMeta(meta);
        }

        return item;
    }
}