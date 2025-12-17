package net.minebo.brawl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.cobalt.timer.SpawnTimer;
import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.kit.impl.Phantom;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.spawn.SpawnHotbar;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneralCommands extends BaseCommand {

    @CommandAlias("spawn")
    @Description("Takes you to spawn.")
    public void spawnCommand(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "You do not have a profile, try reconnecting or contact an administrator.");
            return;
        }

        if (player.hasPermission("basic.staff")) {
            player.sendMessage(ChatColor.YELLOW + "You bypassed the spawn timer since you are a staff member.");

            if (profile.getSelectedKit() instanceof Phantom) {
                Phantom.FlightTimer.flightTasks.remove(player.getUniqueId());
            }

            Kit.clear(player);
            player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
            SpawnHotbar.giveItems(player);

            if (!profile.isSpawnProtected()) {
                profile.spawnProtected = true;
                player.sendMessage(ChatColor.GREEN + "Your spawn protection has been enabled.");
            }
            return;
        }

        if (player.hasMetadata("frozen")) {
            player.sendMessage(ChatColor.RED + "You can't teleport while you're frozen!");
            return;
        }

        if (Brawl.getInstance().getCooldownHandler().getCooldown("Combat Tag").onCooldown(player)) {
            player.sendMessage(ChatColor.RED + "You can't teleport while you're combat tagged!");
            return;
        }

        if (SpawnTimer.spawnTasks.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already teleporting to spawn.");
            return; // dont potato and let them spam spawn
        }

        new SpawnTimer(player, Brawl.getInstance()).start();
    }

    @CommandAlias("stats|statistics")
    @Description("Check a player's stats.")
    public void statsCommand(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);
        if (profile == null) {
            return;
        }

        player.sendMessage("");
        player.sendMessage(ColorUtil.translateColors("&e&lYour Stats:"));
        player.sendMessage(ColorUtil.translateColors("&eLast Used Kit: " + Kit.get(profile.lastKit).getColoredName()));
        player.sendMessage(ColorUtil.translateColors("&eKills: &f" + profile.kills));
        player.sendMessage(ColorUtil.translateColors("&eDeaths: &f" + profile.deaths));
        player.sendMessage(ColorUtil.translateColors("&eMoney: &2$&a" + profile.money));
        player.sendMessage(ColorUtil.translateColors("&eKillstreak: &f" + profile.killstreak));
        player.sendMessage(ColorUtil.translateColors("&eHighest Killstreak: &f" + profile.highestkillstreak));
        player.sendMessage("");
    }

    @CommandAlias("stats|statistics")
    @Description("Check a player's stats.")
    @CommandCompletion("@players")
    @Syntax("<player>")
    public void statsElseCommand(CommandSender sender, OfflinePlayer offlinePlayer) {
        BrawlProfile profile = BrawlProfile.get(offlinePlayer.getUniqueId());
        if (profile == null) {
            sender.sendMessage(ColorUtil.translateColors("&cThat player has not played this Season."));
            return;
        }

        sender.sendMessage("");
        sender.sendMessage(ColorUtil.translateColors("&e&l" + offlinePlayer.getName() + "'s Stats:"));
        sender.sendMessage(ColorUtil.translateColors("&eLast Used Kit: " + Kit.get(profile.lastKit).getColoredName()));
        sender.sendMessage(ColorUtil.translateColors("&eKills: &f" + profile.kills));
        sender.sendMessage(ColorUtil.translateColors("&eDeaths: &f" + profile.deaths));
        sender.sendMessage(ColorUtil.translateColors("&eMoney: &2$&a" + profile.money));
        sender.sendMessage(ColorUtil.translateColors("&eKillstreak: &f" + profile.killstreak));
        sender.sendMessage(ColorUtil.translateColors("&eHighest Killstreak: &f" + profile.highestkillstreak));
        sender.sendMessage("");
    }

    @CommandAlias("killstreak|killstreaks|ks")
    @Description("Opens the killstreak menu.")
    public void killStreaksCommand(Player player) {
        KillStreak.openMenu(player);
    }

}


