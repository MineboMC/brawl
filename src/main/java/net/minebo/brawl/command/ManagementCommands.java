package net.minebo.brawl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("brawl")
public class ManagementCommands extends BaseCommand {

    @Default
    @CatchUnknown
    @HelpCommand
    public void helpCommand(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("togglefreekits")
    @CommandPermission("brawl.manage")
    @Description("Toggles kits being free.")
    public void freeKitsCommand(CommandSender sender) {
        Kit.freeKitMode = !Kit.freeKitMode;
        Brawl.getInstance().getTipTask().generateTips();
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ColorUtil.translateColors("&eAll kits are " + (Kit.freeKitMode ? "now" : "no longer") + " free!"));
        Bukkit.broadcastMessage("");
        Brawl.getInstance().getConfig().set("manage.freekitsmode", Kit.freeKitMode);
        Brawl.getInstance().saveConfig();
    }

    @Subcommand("testks")
    @CommandPermission("brawl.manage")
    @Description("Toggles kits being free.")
    @Syntax("<value>")
    public void testKillStreakCommand(Player sender, Integer ks) {
        sender.sendMessage(ColorUtil.translateColors("&aIf there is a ks for &e" + ks + " &ait will be rewarded to " + sender.getDisplayName() + "&a!"));
        KillStreak.handleKillStreak(sender, ks);
    }

    @Subcommand("metrics")
    @CommandPermission("brawl.manage")
    @Description("Toggles kits being free.")
    public void metricsCommand(CommandSender sender) { // more soon
        sender.sendMessage(ColorUtil.translateColors("&rBrawl Metrics:"));
        sender.sendMessage(ColorUtil.translateColors("&7Profiles: &f" + BrawlProfile.profiles.size()));
        sender.sendMessage(ColorUtil.translateColors("&7Total Kills: &f" + BrawlProfile.getAllKills()));
        sender.sendMessage(ColorUtil.translateColors("&7Total Deaths: &f" + BrawlProfile.getAllDeaths()));
        sender.sendMessage(ColorUtil.translateColors("&7Total Money: &2$&a" + BrawlProfile.getAllMoney()));
    }

}
