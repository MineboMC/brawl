package net.minebo.brawl.cobalt.completion;

import co.aikar.commands.CommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import net.minebo.brawl.hook.impl.FancyHologramHook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StatCategoryCompletionHandler implements CommandCompletions.CommandCompletionHandler {

    @Override
    public Collection<String> getCompletions(CommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completions = new ArrayList<>();

        for (FancyHologramHook.StatCategory category : FancyHologramHook.StatCategory.values()) {
            completions.add(category.name().toLowerCase());
        }

        return completions;
    }
}