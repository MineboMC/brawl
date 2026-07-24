package net.minebo.brawl.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import net.minebo.basalt.api.BasaltAPI;
import net.minebo.basalt.models.profile.GameProfile;
import net.minebo.brawl.hook.impl.FancyHologramHook;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.pagination.PaginatedResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LeaderboardCommand extends BaseCommand {

    @CommandAlias("leaderboard|lb|top")
    @Description("View leaderboards")
    @CommandCompletion("@statcategories")
    public void leaderboardCommand(Player player, @Optional FancyHologramHook.StatCategory category, @Optional Integer targetPage) {
        if(category == null) {
            category = FancyHologramHook.StatCategory.KILLS;
        }

        int page = (targetPage != null && targetPage > 0) ? targetPage : 1;

        HashMap<String, Integer> playerStats = new HashMap<>();

        for (BrawlProfile profile : BrawlProfile.profiles.values()) {
            Integer value = category.getValue(profile);
            if (value != null && value > 0) {
                playerStats.put(profile.lastUsername, value);
            }
        }

        if (playerStats.isEmpty()) {
            player.sendMessage(ColorUtil.translateColors("<red>No data available for " + category.getTitle() + "!"));
            return;
        }

        PaginatedResult<Integer> paginated = new PaginatedResult<>(
                playerStats,
                10, // 10 entries per page
                (e1, e2) -> e2.getValue().compareTo(e1.getValue()) // descending
        );

        LinkedHashMap<String, Integer> topPage = paginated.getPage(page);
        int totalPages = paginated.getTotalPages();

        if (topPage.isEmpty()) {
            player.sendMessage(ColorUtil.translateColors("<red>Page " + page + " does not exist!"));
            return;
        }

        player.sendMessage("");
        player.sendMessage(ColorUtil.translateColors(category.getTitle() + " <reset><gray>(Page " + page + "/" + totalPages + ")"));

        int position = (page - 1) * 10 + 1;

        for (Map.Entry<String, Integer> entry : topPage.entrySet()) {
            String name = entry.getKey();
            int value = entry.getValue();

            GameProfile gp = null;
            try {
                gp = BasaltAPI.INSTANCE.quickFindProfile(
                        Bukkit.getOfflinePlayer(name).getUniqueId()
                ).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            String rankColor = gp != null
                    ? gp.getCurrentRank().getColor() : "<white>";

            String posColor = FancyHologramHook.getPositionColor(position);

            player.sendMessage(ColorUtil.translateColors(posColor + "#" + position + " " + rankColor + name + " <gray>- <yellow>" + FancyHologramHook.formatNumber(value)));

            position++;
        }

        player.sendMessage(ColorUtil.translateColors("<gray>Use /lb " + category.name().toLowerCase() + " <page> to see other pages."));
        player.sendMessage("");
    }

}