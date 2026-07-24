package net.minebo.brawl.cobalt.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import net.minebo.brawl.hook.impl.FancyHologramHook;

public class StatCategoryContextResolver implements ContextResolver<FancyHologramHook.StatCategory, BukkitCommandExecutionContext> {

    @Override
    public FancyHologramHook.StatCategory getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String name = context.popFirstArg();

        if (name == null || name.isEmpty()) {
            throw new InvalidCommandArgument("You must specify a leaderboard category.");
        }

        // Try to match by name (case insensitive)
        for (FancyHologramHook.StatCategory category : FancyHologramHook.StatCategory.values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }

        throw new InvalidCommandArgument("No leaderboard category found matching: " + name);
    }
}