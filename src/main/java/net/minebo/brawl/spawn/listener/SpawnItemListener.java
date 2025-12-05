package net.minebo.brawl.spawn.listener;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.kit.menu.KitButton;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.spawn.SpawnHotbar;
import net.minebo.cobalt.menu.MenuHandler;
import net.minebo.cobalt.menu.construct.AbstractButton;
import net.minebo.cobalt.menu.construct.Button;
import net.minebo.cobalt.menu.construct.Menu;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.ItemListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnItemListener extends ItemListener {

    public SpawnItemListener() {

        // Kit Selector
        addHandler(SpawnHotbar.KIT_SELECTOR, (SpawnItemListener::openKitMenu));

        // Last Used Kit
        addHandler(SpawnHotbar.LAST_KIT, (player -> {
            BrawlProfile profile = BrawlProfile.get(player);
            Kit kit = Kit.get(profile.lastKit);

            kit.apply(player);
        }));

    }

    public static void openKitMenu(Player player) {
        Menu menu = new Menu().setTitle(ColorUtil.translateColors("&dChoose your kit!")).setUpdateAfterClick(true);

        BrawlProfile profile = BrawlProfile.get(player);

        int i = 0;
        for(Kit kit : Kit.kits) {

            menu.setButton(i, new KitButton(player, profile, kit));

            i++;
        }

        menu.openMenu(player);
    }

    @EventHandler
    public void onItemMove(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        if (current == null) return;

        if (current.isSimilar(SpawnHotbar.KIT_SELECTOR) || current.isSimilar(SpawnHotbar.LAST_KIT)) {
            event.setCancelled(true);
        }
    }
}
