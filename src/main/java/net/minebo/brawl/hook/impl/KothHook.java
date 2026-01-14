package net.minebo.brawl.hook.impl;

import net.minebo.brawl.hook.PluginHook;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.koth.koth.event.KothEndEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class KothHook extends PluginHook {

    @Override
    public String getPluginName() {
        return "KoTH";
    }

    @Override
    public void initHook() {

    }

    @EventHandler
    public void onWinKoth(KothEndEvent event) {
        if(event.getWinner() == null) return;

        Player player = event.getWinner();
        BrawlProfile profile = BrawlProfile.get(player);

        player.sendActionBar(ColorUtil.translateColors("&a+ &2$&a50 (capped koth)"));
        profile.money.add(50);
    }

}
