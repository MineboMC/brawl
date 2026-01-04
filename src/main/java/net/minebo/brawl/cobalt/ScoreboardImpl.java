package net.minebo.brawl.cobalt;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.cobalt.timer.SpawnTimer;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.scoreboard.provider.ScoreboardProvider;
import net.minebo.cobalt.timer.Timer;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.ServerUtil;
import net.minebo.cobalt.util.TimeUtil;
import net.minebo.koth.koth.Koth;
import org.bukkit.Bukkit;
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

        if(Bukkit.getPluginManager().isPluginEnabled("KoTH")) {
            if(Koth.currentKoth != null) lines.addAll(generateKothLines());
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
                lines.add(ColorUtil.translateHexColors("&cPvP Tag&c: &f" + pvpTag.getRemaining(player)));
            }
        }

        if(Brawl.getInstance().getSpawnTimer().hasTimer(player.getUniqueId())) {
            if(Brawl.getInstance().getSpawnTimer().getRemaining(player) != "0") {
                lines.add(ColorUtil.translateColors("&3Spawn: &f" + Brawl.getInstance().getSpawnTimer().getRemaining(player)));
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

        Cooldown melon = Brawl.getInstance().getCooldownHandler().getCooldown("Melon Toss");

        if(melon != null) {
            if(melon.onCooldown(player)) {
                lines.add(ChatColor.of("#7FCC19") + "Melon Toss: &f" + melon.getRemaining(player));
            }
        }

        Cooldown watergun = Brawl.getInstance().getCooldownHandler().getCooldown("Water Gun");

        if(watergun != null) {
            if(watergun.onCooldown(player)) {
                lines.add(ChatColor.AQUA + "Water Gun: &f" + watergun.getRemaining(player));
            }
        }

        Cooldown jump = Brawl.getInstance().getCooldownHandler().getCooldown("Avatar Jump");

        if(jump != null) {
            if(jump.onCooldown(player)) {
                lines.add(ChatColor.YELLOW + "Jump: &f" + jump.getRemaining(player));
            }
        }

        lines.add("");

        return lines;
    }

    public List<String> generateKothLines() {
        List<String> lines = new ArrayList<>();
        Koth koth = Koth.currentKoth;

        lines.add("&9&l" + koth.getName() + " KoTH");
        lines.add("&fTime: &e" + koth.getRemaining());
        lines.add("&fCoords: &e/koth");
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
