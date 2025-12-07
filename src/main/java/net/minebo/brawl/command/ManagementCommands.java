package net.minebo.brawl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
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

}
