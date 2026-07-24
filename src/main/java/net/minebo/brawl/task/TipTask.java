package net.minebo.brawl.task;

import net.minebo.brawl.kit.Kit;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TipTask extends BukkitRunnable {

    String TIP_PREFIX = "<dark_gray>[<gold>TIP<dark_gray>] ";

    List<String> tips = new ArrayList<>();

    Integer currentTip;

    public TipTask() {
        currentTip = 0;
        generateTips();
    }

    public void generateTips() {
        tips.add("<yellow>Soup heals 3.5 hearts!");
        tips.add("<yellow>There are free soup signs around the map!");
        tips.add("<yellow>Walking on <aqua>Sponge <yellow>will shoot you upwards!");
        tips.add("<yellow>Killstreaks will get you rewards, check out <light_purple>/ks<yellow>!");
        if(Kit.freeKitMode) tips.add("<yellow>All kits are free to use!");
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
