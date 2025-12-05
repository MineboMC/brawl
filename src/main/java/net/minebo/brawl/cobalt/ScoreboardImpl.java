package net.minebo.brawl.cobalt;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.scoreboard.provider.ScoreboardProvider;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.ServerUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardImpl extends ScoreboardProvider {

    FileConfiguration cfg = Brawl.getInstance().getConfig();

    @Override
    public String getModernTitle(Player player){
        return cfg.getString("scoreboard.title");
    }

    @Override
    public List<String> getModernLines(Player player) {
        List<String> lines = new ArrayList<String>();

        lines.add("");

        lines.addAll(generateStatisticLines(player));
        lines.addAll(generateCooldownLines(player));

        if(player.hasMetadata("modmode")){
            lines.addAll(generateStaffLines(player));
        }

        lines.add(cfg.getString("scoreboard.url"));

        return lines;
    }

    public List<String> generateStatisticLines(Player player){
        List<String> lines = new ArrayList<>();

        BrawlProfile profile = BrawlProfile.get(player);

        if(profile == null) {
            return List.of("&cYour profile hasn't", "&cloaded properly.", "", "&cPlease relog.");
        }

        lines.add("&fKills: &e" + profile.kills);
        lines.add("&fDeaths: &e" + profile.deaths);

        if(profile.killstreak.get() > 0) {
            lines.add("&fStreak: &e" + profile.killstreak);
        }

        lines.add("Money: &2$&a" + profile.money);

        if(profile.getSelectedKit() != null) {
            lines.add("Kit: &e" + profile.getSelectedKit().getColoredName());
        }

        return lines;
    }

    public List<String> generateCooldownLines(Player player) {
        List<String> lines = new ArrayList<>();

        Cooldown pvpTag = Brawl.getInstance().getCooldownHandler().getCooldown("Combat Tag");

        if(pvpTag != null) {
            if (pvpTag.onCooldown(player)) {
                lines.add(ColorUtil.translateHexColors("&c&lPvP Tag&c: &f" + pvpTag.getRemaining(player)));
            }
        }

        // Abilities

        Cooldown flight = Brawl.getInstance().getCooldownHandler().getCooldown("Flight");

        if(flight != null) {
            if(flight.onCooldown(player)) {
                lines.add(ChatColor.of("#6E516B") + "Flight: &f" + flight.getRemaining(player));
            }
        }

        Cooldown bolt = Brawl.getInstance().getCooldownHandler().getCooldown("Bolt");

        if(bolt != null) {
            if(bolt.onCooldown(player)) {
                lines.add(ChatColor.of("#FFD700") + "Bolt: &f" + bolt.getRemaining(player));
            }
        }

        Cooldown stomp = Brawl.getInstance().getCooldownHandler().getCooldown("Stomp");

        if(stomp != null) {
            if(stomp.onCooldown(player)) {
                lines.add(ChatColor.of("#FF0000") + "Stomp: &f" + stomp.getRemaining(player));
            }
        }

        lines.add("");

        return lines;
    }

    public List<String> generateStaffLines(Player player) {
        return List.of("",
            ChatColor.AQUA + "Staff Info:",
            ChatColor.GRAY + " * " + ChatColor.RESET + "TPS: " + ServerUtil.getColoredTPS(),
            ChatColor.GRAY + " * " + ChatColor.RESET + "Vanish: " + (player.hasMetadata("vanish") ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"),
            ChatColor.GRAY + " * " + ChatColor.RESET + "Chat: " + (player.hasMetadata("toggleSC") ? ChatColor.GOLD + "Staff" : ChatColor.YELLOW + "Public"),
                ""
        );
    }

}
