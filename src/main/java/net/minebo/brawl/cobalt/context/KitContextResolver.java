package net.minebo.brawl.cobalt.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import net.minebo.brawl.kit.Kit;

public class KitContextResolver implements ContextResolver<Kit, BukkitCommandExecutionContext> {

    @Override
    public Kit getContext(BukkitCommandExecutionContext commandExecutionContext) throws InvalidCommandArgument {
        String name = commandExecutionContext.popFirstArg();
        Kit kit = Kit.get(name);
        if (kit != null) return kit;
        throw new InvalidCommandArgument("No kit matching " + name + " could be found.");
    }

}
