package net.minebo.brawl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("economy|eco")
public class EconomyCommands extends BaseCommand {

    @Default
    @CatchUnknown
    @HelpCommand
    public void helpCommand(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @CommandAlias("balance|bal")
    @Subcommand("check")
    @Description("Check the balance of a player.")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void checkBalanceCommand(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);

        player.sendMessage(ColorUtil.translateColors("<yellow>Your balance: <dark_green>$<green>" + profile.money));
    }

    @CommandAlias("balance|bal")
    @Subcommand("check")
    @Description("Check the balance of a player.")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void checkBalanceCommand(CommandSender sender, OfflinePlayer player) {
        BrawlProfile profile = BrawlProfile.get(player.getUniqueId());

        if(profile == null) {
            sender.sendMessage(ColorUtil.translateColors("<red>That player has not played this season."));
            return;
        }

        sender.sendMessage(ColorUtil.translateColors(profile.lastUsername + "<yellow>'s balance: <dark_green>$<green>" + profile.money));
    }

    @CommandAlias("pay|p2p")
    @Subcommand("send")
    @Description("Send your money to another player.")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void payBalanceCommand(OnlinePlayer sender, OfflinePlayer player, Integer money) {
        Player senderPlayer = sender.getPlayer();
        BrawlProfile senderProfile = BrawlProfile.get(player.getUniqueId());
        BrawlProfile recieverProfile = BrawlProfile.get(player.getUniqueId());

        if(recieverProfile == null) {
            senderPlayer.sendMessage(ColorUtil.translateColors("<red>That player has not played this season."));
            return;
        }

        if(senderProfile.money.get() <= money) {
            senderPlayer.sendMessage(ColorUtil.translateColors("<red>You don't have enough money."));
            return;
        }

        senderProfile.money.sub(money);
        recieverProfile.money.add(money);

        senderPlayer.sendMessage(ColorUtil.translateColors("<yellow>Sent <dark_green>$<green>" + money + "<yellow> to " + player.getName()));

        if(player.isOnline()) {
            Player recieverPlayer =  player.getPlayer();

            recieverPlayer.sendMessage(ColorUtil.translateColors("<yellow>You've recieved <dark_green>$<green>" + money + "<yellow> from " + senderPlayer.getName()));
        }
    }

    @Subcommand("set")
    @CommandPermission("brawl.manage")
    @Description("Update the balance of a player.")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void setBalanceCommand(CommandSender sender, OfflinePlayer player, Integer money) {
        BrawlProfile profile = BrawlProfile.get(player.getUniqueId());

        if(profile == null) {
            sender.sendMessage(ColorUtil.translateColors("<red>That player has not played this season."));
            return;
        }

        profile.money.set(money);
        sender.sendMessage(ColorUtil.translateColors(profile.lastUsername + "<yellow>'s balance has been updated to <dark_green>$<green>" + profile.money));
    }

    @Subcommand("add")
    @CommandPermission("brawl.manage")
    @Description("Add to the balance of a player.")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void addBalanceCommand(CommandSender sender, OfflinePlayer player, Integer money) {
        BrawlProfile profile = BrawlProfile.get(player.getUniqueId());

        if(profile == null) {
            sender.sendMessage(ColorUtil.translateColors("<red>That player has not played this season."));
            return;
        }

        profile.money.add(money);
        sender.sendMessage(ColorUtil.translateColors(profile.lastUsername + "<yellow>'s balance has been updated to <dark_green>$<green>" + profile.money));
    }

    @Subcommand("sub")
    @CommandPermission("brawl.manage")
    @Description("Subtract from the balance of a player.")
    @Syntax("<player>")
    @CommandCompletion("@players")
    public void subBalanceCommand(CommandSender sender, OfflinePlayer player, Integer money) {
        BrawlProfile profile = BrawlProfile.get(player.getUniqueId());

        if(profile == null) {
            sender.sendMessage(ColorUtil.translateColors("<red>That player has not played this season."));
            return;
        }

        profile.money.sub(money);

        // no negative balances
        if(money < 0) {
            profile.money.set(0);
        }

        sender.sendMessage(ColorUtil.translateColors(profile.lastUsername + "<yellow>'s balance has been updated to <dark_green>$<green>" + profile.money));
    }

}
