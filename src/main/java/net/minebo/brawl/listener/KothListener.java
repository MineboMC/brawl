package net.minebo.brawl.listener;

import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.koth.koth.event.KothEndEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KothListener implements Listener {

    @EventHandler
    public void onWinKoth(KothEndEvent event) {
        if(event.getWinner() == null) return;

        Player player = event.getWinner();
        BrawlProfile profile = BrawlProfile.get(player);

        player.sendActionBar(ColorUtil.translateColors("&a+ &2$&a50 (capped koth)"));
        profile.money.add(50);
    }
}
