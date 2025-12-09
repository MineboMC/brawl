package net.minebo.brawl.killstreak.buttons;

import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.menu.construct.Button;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KillStreakButton extends Button {

    BrawlProfile profile;
    KillStreak killStreak;

    public KillStreakButton(Player player, KillStreak killStreak) {
        if(BrawlProfile.get(player) == null) return;

        this.profile = BrawlProfile.get(player);
        this.killStreak = killStreak;

        setName(killStreak.getColor() + killStreak.getName());
        setAmount(killStreak.getKills());

        setLines(() -> getDescription());
    }

    public List<String> getDescription() {
        List<String> description = new ArrayList<>();

        description.add(ColorUtil.translateColors("&f" + killStreak.getDescription()));
        description.add("");

        if (profile.killstreak.get() >= killStreak.getKills()) {
            description.add(ColorUtil.translateColors("&aYou've claimed this reward."));
        } else {
            int kills = killStreak.getKills();
            int progress = (kills == 0) ? 0 : (int) Math.round(((double) profile.killstreak.get() / (double) kills) * 100.0);
            description.add(ColorUtil.translateColors("&6Your Progress: &7(&e" + progress + "%&7)"));
        }

        return description;
    }

    @Override
    public ItemStack build() {
        ItemStack item = killStreak.getIcon();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(this.name.get());
            meta.setLore(this.lines.get());
            item.setItemMeta(meta);
        }

        return item;
    }
}
