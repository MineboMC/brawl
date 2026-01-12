package net.minebo.brawl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import net.minebo.brawl.shop.ShopItem;
import org.bukkit.entity.Player;

public class ShopCommands extends BaseCommand {

    @CommandAlias("buy")
    @Description("Purchase an item.")
    @Syntax("<item>")
    @CommandCompletion("@shopitems")
    public void buyCommand(Player player, ShopItem item) {
        item.buy(player);
    }

    @CommandAlias("shop")
    @Description("Opens the shop.")
    public void shopCommand(Player player) {
        ShopItem.openMenu(player);
    }

}
