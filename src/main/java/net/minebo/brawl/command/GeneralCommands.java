package net.minebo.brawl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.cobalt.timer.SpawnTimer;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.spawn.SpawnHotbar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        new SpawnTimer(player, Brawl.getInstance());
    }

}


