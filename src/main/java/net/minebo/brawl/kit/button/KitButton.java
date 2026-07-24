package net.minebo.brawl.kit.button;

import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.menu.construct.Button;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KitButton extends Button {

    private final BrawlProfile profile;
    private final Kit kit;

    public KitButton(Player player, BrawlProfile profile, Kit kit) {
        this.profile = profile;
        this.kit = kit;

        setName(() -> kit.getName());
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
                    player.sendMessage(ColorUtil.translateColors("<gray>You have purchased <green>" + kit.getName() + "<gray>."));
                    profile.save();
                } else {
                    player.sendMessage(ColorUtil.translateColors("<red>You do not have enough money for this kit."));
                }
            }
        });
    }

    public List<String> getDescription() {
        List<String> description = new ArrayList<>();

        description.add("<white>" + kit.getDescription());
        description.add("");

        if (profile.ownsKit(kit)) {
            description.add("<green>You own this kit!");
            description.add("");
            description.add("<gray>Left click to equip!");
        } else {
            description.add("<red>You don't own this kit.");
            description.add("<white>Price: <dark_green>$<green>" + kit.getPrice());
            description.add("");
            description.add("<gray>Left click to purchase!");
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