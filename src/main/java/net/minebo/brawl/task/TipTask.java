package net.minebo.brawl.task;

import net.minebo.brawl.kit.Kit;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TipTask extends BukkitRunnable {

    String TIP_PREFIX = "&8[&6TIP&8] ";

    List<String> tips = new ArrayList<>();

    Integer currentTip;

    public TipTask() {
        currentTip = 0;
        generateTips();
    }

    public void generateTips() {
        tips.add("&eSoup heals 3.5 hearts!");
        tips.add("&eThere are free soup signs around the map!");
        tips.add("&eWalking on &bSponge &ewill shoot you upwards!");
        tips.add("&eKillstreaks will get you rewards, check out &d/ks&e!");
        if(Kit.freeKitMode) tips.add("&eAll kits are free to use!");
    }

    @Override
    public void run() {

        if (currentTip >= tips.size()) {
            currentTip = 0;
        }

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(ColorUtil.translateColors(TIP_PREFIX + tips.get(currentTip)));
        });

        currentTip++;
    }


}
