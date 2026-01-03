package net.minebo.brawl.cobalt.timer;

import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.timer.Timer;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class NukeTimer extends Timer {

    private static final double NUKE_RADIUS = 25.0D;
    private static final double NUKE_DAMAGE = 100.0D;

    public NukeTimer(Plugin plugin) {
        super(10, plugin);
    }

    @Override
    protected void onStart(Player player) {
        // message to the nuke owner that their nuke countdown has started
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ColorUtil.translateColors("&c&lTactical Nuke Incoming...")));
    }

    @Override
    protected boolean onTick(Player player, int secondsLeft) {
        // Broadcast countdown message to all online players and play effects/sounds
        String countdownMessage = ColorUtil.translateColors("&c" + secondsLeft + "...");
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(countdownMessage);

            // try to play the "happy villager" effect above the nuke owner's location
            if (player.isOnline()) {
                player.getWorld().playEffect(player.getLocation().add(new Vector(0, 3, 0)),
                        Effect.SMOKE, 26, 0);
            }

            p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
        }

        // returning true keeps the timer running (matches previous behavior)
        return true;
    }

    @Override
    protected void onComplete(Player player) {
        // Detonate the nuke: damage players within radius excluding the nuke owner and spawn-protected players
        int nukedCount = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p == null || !p.isOnline()) continue;
            if (p.getUniqueId().equals(player.getUniqueId())) continue; // don't nuke the owner

            BrawlProfile profile = BrawlProfile.get(p);
            if (profile != null && profile.isSpawnProtected()) {
                continue;
            }

            // distance check
            if (p.getWorld().equals(player.getWorld()) && p.getLocation().distance(player.getLocation()) <= NUKE_RADIUS) {
                p.damage(NUKE_DAMAGE, player);
                nukedCount++;
            }
        }

        String context = nukedCount == 1 ? "player" : "players";
        Bukkit.broadcastMessage(ColorUtil.translateColors("&eThe nuke eliminated a total of &c" + nukedCount + " &e" + context + "."));
    }

}