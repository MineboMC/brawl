package net.minebo.brawl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.spawn.SpawnHotbar;
import net.minebo.brawl.spawn.listener.SpawnItemListener;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommands extends BaseCommand {

    @CommandAlias("kit")
    @Description("Selects a kit.")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void kitCommand(Player player, Kit kit) {
        BrawlProfile profile = BrawlProfile.get(player);

        if(!profile.isSpawnProtected()) {
            player.sendMessage(ColorUtil.translateColors("&cYou can only use this command in Spawn."));
            return;
        }

        kit.apply(player);
    }

    @CommandAlias("kits")
    @Description("Opens the kits gui.")
    public void kitsCommand(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);

        if(!profile.isSpawnProtected()) {
            player.sendMessage(ColorUtil.translateColors("&cYou can only use this command in Spawn."));
            return;
        }

        SpawnItemListener.openKitMenu(player);
    }

    @CommandAlias("clearkit|ck")
    @Description("Clears your kit.")
    public void clearKitCommand(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);

        if(!profile.isSpawnProtected()) {
            player.sendMessage(ColorUtil.translateColors("&cYou can only use this command in Spawn."));
            return;
        }

        Kit.clear(player);
        SpawnHotbar.giveItems(player);
    }

}
