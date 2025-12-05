package net.minebo.brawl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.spawn.listener.SpawnItemListener;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("kit")
public class KitCommands extends BaseCommand {

    @Default
    @CatchUnknown
    @HelpCommand
    public void helpCommand(CommandSender sender, CommandHelp help) {
        help.showHelp();
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

}
