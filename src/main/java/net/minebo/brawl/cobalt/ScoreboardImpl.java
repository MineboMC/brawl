package net.minebo.brawl.cobalt;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.cobalt.timer.SpawnTimer;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.scoreboard.animation.AnimationType;
import net.minebo.cobalt.scoreboard.animation.TextAnimation;
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

    //TextAnimation animation = new TextAnimation(Brawl.getInstance(), cfg.getString("scoreboard.title"), "<gold>", "<yellow>", AnimationType.DEFAULT, true);

    @Override
    public String getModernTitle(Player player){
        return "<gold><bold>BRAWL";
    }

    @Override
    public List<String> getModernLines(Player player) {
        List<String> lines = new ArrayList<String>();

        lines.add("");

        lines.addAll(generateStatisticLines(player));
        lines.addAll(generateCooldownLines(player));

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
            return List.of("<red>Your profile hasn't", "<red>loaded properly.", "", "<red>Please relog.");
        }

        lines.add("<white>Kills: <yellow>" + profile.kills);
        lines.add("<white>Deaths: <yellow>" + profile.deaths);

        if(profile.killstreak.get() > 0) {
            lines.add("<white>Streak: <yellow>" + profile.killstreak);
        }

        lines.add("Money: <dark_green>$<green>" + profile.money);

        if(profile.getSelectedKit() != null) {
            lines.add("Kit: <yellow>" + profile.getSelectedKit().getName());
        }

        return lines;
    }

    public List<String> generateCooldownLines(Player player) {
        List<String> lines = new ArrayList<>();

        Cooldown pvpTag = Brawl.getInstance().getCooldownHandler().getCooldown("Combat Tag");

        if(pvpTag != null) {
            if (pvpTag.onCooldown(player)) {
                lines.add(ColorUtil.translateColors("<red>PvP Tag<red>: <white>" + pvpTag.getRemaining(player)));
            }
        }

        if(Brawl.getInstance().getSpawnTimer().hasTimer(player.getUniqueId())) {
            if(Brawl.getInstance().getSpawnTimer().getRemaining(player) != "0") {
                lines.add(ColorUtil.translateColors("<dark_aqua>Spawn: <white>" + Brawl.getInstance().getSpawnTimer().getRemaining(player)));
            }
        }

        // Abilities

        Cooldown flight = Brawl.getInstance().getCooldownHandler().getCooldown("Flight");

        if(flight != null) {
            if(flight.onCooldown(player)) {
                lines.add(ChatColor.of("#6E516B") + "Flight: <white>" + flight.getRemaining(player));
            }
        }

        Cooldown bolt = Brawl.getInstance().getCooldownHandler().getCooldown("Bolt");

        if(bolt != null) {
            if(bolt.onCooldown(player)) {
                lines.add(ChatColor.of("#FFD700") + "Bolt: <white>" + bolt.getRemaining(player));
            }
        }

        Cooldown stomp = Brawl.getInstance().getCooldownHandler().getCooldown("Stomp");

        if(stomp != null) {
            if(stomp.onCooldown(player)) {
                lines.add(ChatColor.of("#FF0000") + "Stomp: <white>" + stomp.getRemaining(player));
            }
        }

        Cooldown melon = Brawl.getInstance().getCooldownHandler().getCooldown("Melon Toss");

        if(melon != null) {
            if(melon.onCooldown(player)) {
                lines.add(ChatColor.of("#7FCC19") + "Melon Toss: <white>" + melon.getRemaining(player));
            }
        }

        Cooldown watergun = Brawl.getInstance().getCooldownHandler().getCooldown("Water Gun");

        if(watergun != null) {
            if(watergun.onCooldown(player)) {
                lines.add(ChatColor.AQUA + "Water Gun: <white>" + watergun.getRemaining(player));
            }
        }

        Cooldown jump = Brawl.getInstance().getCooldownHandler().getCooldown("Avatar Jump");

        if(jump != null) {
            if(jump.onCooldown(player)) {
                lines.add(ChatColor.YELLOW + "Jump: <white>" + jump.getRemaining(player));
            }
        }

        lines.add("");

        return lines;
    }

    public List<String> generateKothLines() {
        List<String> lines = new ArrayList<>();
        Koth koth = Koth.currentKoth;

        lines.add("<blue><bold>" + koth.getName() + " KoTH");
        lines.add("<white>Time: <yellow>" + koth.getRemaining());
        lines.add("<white>Coords: <yellow>/koth");
        lines.add("");

        return lines;
    }

}
