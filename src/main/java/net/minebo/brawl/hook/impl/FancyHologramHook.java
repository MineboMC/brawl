package net.minebo.brawl.hook.impl;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minebo.basalt.api.BasaltAPI;
import net.minebo.basalt.models.profile.GameProfile;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.hook.PluginHook;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.scheduler.Scheduler;
import net.minebo.cobalt.util.pagination.PaginatedResult;
import org.bukkit.Bukkit;

import java.util.*;

@Getter
@Setter
public class FancyHologramHook extends PluginHook {

    private int currentCategoryIndex = 0;
    private final StatCategory[] categories = StatCategory.values();
    private Hologram leaderboardHologram;
    private int countdown = 10;
    private String hologramName = "leaderboard";

    @Override
    public String getPluginName() {
        return "FancyHolograms";
    }

    @Override
    public void initHook() {
        // Try to get the hologram initially
        tryGetHologram();

        // Update every second (20 ticks)
        new Scheduler(Brawl.getInstance()).repeat(20L, 20L).sync(() -> {
            // Try to get hologram if null
            if (leaderboardHologram == null) {
                tryGetHologram();
            }

            // Update the hologram with current stats and countdown
            if (leaderboardHologram != null) {
                sendHoloUpdate(leaderboardHologram);
            }

            // Decrease countdown
            countdown--;

            // Switch category when countdown reaches 0
            if (countdown <= 0) {
                currentCategoryIndex = (currentCategoryIndex + 1) % categories.length;
                countdown = 10; // Reset countdown
            }
        }).run();
    }

    private void tryGetHologram() {
        try {
            Optional<Hologram> hologramOptional = FancyHologramsPlugin.get()
                    .getHologramManager()
                    .getHologram(hologramName);

            if (hologramOptional.isPresent()) {
                leaderboardHologram = hologramOptional.get();
                Brawl.getInstance().getLogger().info("Successfully found hologram: " + hologramName);
            }

        } catch (Exception e) {
            Brawl.getInstance().getLogger().severe("Error getting hologram: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void sendHoloUpdate(Hologram hologram) {
        TextHologramData data = (TextHologramData) hologram.getData();

        StatCategory currentCategory = categories[currentCategoryIndex];

        // Create a HashMap of player names to their stat values
        HashMap<String, Integer> playerStats = new HashMap<>();
        for (BrawlProfile profile : BrawlProfile.profiles.values()) {
            Integer statValue = currentCategory.getValue(profile);
            if (statValue != null && statValue > 0) { // Only include players with stats > 0
                playerStats.put(profile.lastUsername, statValue);
            }
        }

        // Build the hologram text
        List<String> holoText = new ArrayList<>();
        holoText.add(currentCategory.getTitle());
        holoText.add(""); // Empty line for spacing

        // Check if we have any data
        if (playerStats.isEmpty()) {
            holoText.add("&7No data available yet!");
        } else {
            // Create PaginatedResult with comparator to sort by value (descending)
            PaginatedResult<Integer> paginatedResult = new PaginatedResult<>(
                    playerStats,
                    10, // results per page (top 10)
                    (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()) // Sort descending
            );

            // Get the first page (top 10)
            LinkedHashMap<String, Integer> topPlayers = paginatedResult.getPage(1);

            int position = 1;
            for (Map.Entry<String, Integer> entry : topPlayers.entrySet()) {
                String color = getPositionColor(position);
                GameProfile profile = BasaltAPI.INSTANCE.quickFindProfile(Bukkit.getOfflinePlayer(entry.getKey()).getUniqueId()).get();
                holoText.add(color + "#" + position + " &f" + profile.getCurrentRank().getColor() + entry.getKey() + " &7- &e" + formatNumber(entry.getValue()));
                position++;
            }
        }

        // Add countdown footer
        holoText.add("");
        holoText.add("&7Switching in &e" + countdown + "&7...");

        // Update the hologram
        data.setText(holoText);

        hologram.queueUpdate();
    }

    private String getPositionColor(int position) {
        return switch (position) {
            case 1 -> "&6"; // Gold
            case 2 -> "&7"; // Gray (Silver)
            case 3 -> "&c"; // Red (Bronze)
            default -> "&f"; // White
        };
    }

    private String formatNumber(int number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        }
        return String.valueOf(number);
    }

    public enum StatCategory {
        KILLS("&6&lTop Kills"),
        DEATHS("&c&lTop Deaths"),
        MONEY("&a&lTop Money"),
        KILLSTREAK("&e&lCurrent Killstreak"),
        HIGHEST_KILLSTREAK("&d&lHighest Killstreak");

        private final String title;

        StatCategory(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public Integer getValue(BrawlProfile profile) {
            return switch (this) {
                case KILLS -> profile.kills.get();
                case DEATHS -> profile.deaths.get();
                case MONEY -> profile.money.get();
                case KILLSTREAK -> profile.killstreak.get();
                case HIGHEST_KILLSTREAK -> profile.highestkillstreak.get();
            };
        }
    }
}