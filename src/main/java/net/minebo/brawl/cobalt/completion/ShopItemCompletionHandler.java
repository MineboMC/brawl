package net.minebo.brawl.cobalt.completion;

import co.aikar.commands.CommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.shop.ShopItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShopItemCompletionHandler implements CommandCompletions.CommandCompletionHandler {
    @Override
    public Collection<String> getCompletions(CommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();

        ShopItem.shopItems.forEach(shopItem -> completions.add(shopItem.getName()));

        return completions;
    }
}
