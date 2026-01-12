package net.minebo.brawl.cobalt.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import net.minebo.brawl.shop.ShopItem;

public class ShopItemContextResolver implements ContextResolver<ShopItem, BukkitCommandExecutionContext> {

    @Override
    public ShopItem getContext(BukkitCommandExecutionContext commandExecutionContext) throws InvalidCommandArgument {
        String name = commandExecutionContext.popFirstArg();
        ShopItem shopItem = ShopItem.get(name);
        if (shopItem != null) return shopItem;
        throw new InvalidCommandArgument("No item matching " + name + " could be found.");
    }

}
