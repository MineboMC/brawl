package net.minebo.brawl.killstreak;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.killstreak.buttons.KillStreakButton;
import net.minebo.brawl.killstreak.impl.*;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.menu.construct.Menu;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class KillStreak {

    abstract public String getName();
    abstract public ChatColor getColor();

    abstract public String getDescription();
    abstract public ItemStack getIcon();
    abstract public Integer getKills();

    public abstract void doReward(Player player);

    public static List<KillStreak> killStreaks;

    public static void init() {
        killStreaks = List.of(
                new GoldenApples(),
                new Debuffs()
        );
    }

    public static KillStreak get(Integer kills) {
        return killStreaks.stream().filter(k -> k.getKills() == kills).findFirst().orElse(null);
    }

    public static void openMenu(Player player) {
        Menu menu = new Menu().setTitle(ColorUtil.translateColors("&eKill Streaks"));

        BrawlProfile profile = BrawlProfile.get(player);

        int i = 0;
        for(KillStreak killStreak : KillStreak.killStreaks) {

            menu.setButton(i, new KillStreakButton(player, killStreak));

            i++;
        }

        menu.openMenu(player);
    }

}
