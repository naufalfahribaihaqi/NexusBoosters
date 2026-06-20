package id.naufal.nexusboosters.bossbar;

import id.naufal.nexusboosters.NexusBoostersPlugin;
import id.naufal.nexusboosters.booster.ActiveBooster;
import id.naufal.nexusboosters.booster.Booster;
import id.naufal.nexusboosters.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BoosterBossBarManager {
    private final NexusBoostersPlugin plugin;
    private final Map<String, BossBar> activeBars = new ConcurrentHashMap<>();
    private final Map<String, ActiveBooster> trackingBoosters = new ConcurrentHashMap<>();
    private int rotationIndex = 0;
    private int ticksSinceRotate = 0;
    private int taskId = -1;

    public BoosterBossBarManager(NexusBoostersPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("bossbar.enabled", true)) return;

        int interval = plugin.getConfig().getInt("bossbar.update-interval-ticks", 20);
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, interval, interval).getTaskId();
        
        // Initial load of existing active boosters
        for (ActiveBooster booster : plugin.getBoosterManager().getAllActiveBoosters()) {
            updateOrAddBossBar(booster);
        }
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        for (BossBar bar : activeBars.values()) {
            bar.removeAll();
        }
        activeBars.clear();
        trackingBoosters.clear();
    }

    public void updateOrAddBossBar(ActiveBooster activeBooster) {
        if (!plugin.getConfig().getBoolean("bossbar.enabled", true)) return;
        trackingBoosters.put(activeBooster.getBoosterId(), activeBooster);
        updateBar(activeBooster);
    }

    private void updateBar(ActiveBooster activeBooster) {
        Booster booster = plugin.getBoosterRegistry().getBooster(activeBooster.getBoosterId());
        if (booster == null || activeBooster.isExpired()) {
            removeBossBar(activeBooster.getBoosterId());
            return;
        }

        BossBar bar = activeBars.computeIfAbsent(activeBooster.getBoosterId(), id -> {
            String colorStr = plugin.getConfig().getString("bossbar.color." + booster.getScope().name(), "BLUE");
            if (booster.getType().name().startsWith("PLAYERPOINTS")) {
                colorStr = plugin.getConfig().getString("bossbar.color.PLAYERPOINTS", "PURPLE");
            } else if (booster.getType().name().startsWith("SHOP")) {
                colorStr = plugin.getConfig().getString("bossbar.color.SHOP", "YELLOW");
            }
            BarColor color;
            try { color = BarColor.valueOf(colorStr); } catch (Exception e) { color = BarColor.BLUE; }

            String styleStr = plugin.getConfig().getString("bossbar.style", "SEGMENTED_10");
            BarStyle style;
            try { style = BarStyle.valueOf(styleStr); } catch (Exception e) { style = BarStyle.SEGMENTED_10; }

            return Bukkit.createBossBar("", color, style);
        });

        long timeLeftSeconds = (activeBooster.getExpiresAt() - System.currentTimeMillis()) / 1000L;
        if (timeLeftSeconds < 0) timeLeftSeconds = 0;
        long durationSeconds = booster.getDurationSeconds();
        double progress = (double) timeLeftSeconds / durationSeconds;
        if (progress > 1.0) progress = 1.0;
        if (progress < 0.0) progress = 0.0;
        
        bar.setProgress(progress);

        String format = plugin.getConfig().getString("bossbar.title-format", "&b%booster_name% &7| &f%scope% &7| &a%multiplier%x &7| &e%time_left%");
        String title = format.replace("%booster_name%", booster.getDisplayName())
                .replace("%scope%", booster.getScope().name())
                .replace("%multiplier%", String.valueOf(booster.getMultiplier()))
                .replace("%time_left%", TextUtil.formatTime(timeLeftSeconds));

        bar.setTitle(TextUtil.colorLegacy(title));

        // Manage visibility
        String mode = plugin.getConfig().getString("bossbar.mode", "ROTATE");
        boolean shouldShow = true;

        if ("ROTATE".equalsIgnoreCase(mode) && trackingBoosters.size() > 1) {
            Object[] keys = trackingBoosters.keySet().toArray();
            if (rotationIndex >= keys.length) rotationIndex = 0;
            if (!activeBooster.getBoosterId().equals(keys[rotationIndex])) {
                shouldShow = false;
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean isOwner = activeBooster.getOwnerUuid() != null && activeBooster.getOwnerUuid().equals(p.getUniqueId());
            boolean isGlobal = activeBooster.getScope() == id.naufal.nexusboosters.booster.BoosterScope.GLOBAL;
            
            if (shouldShow && (isGlobal || isOwner)) {
                if (!bar.getPlayers().contains(p)) {
                    bar.addPlayer(p);
                }
            } else {
                bar.removePlayer(p);
            }
        }
    }

    public void removeBossBar(String boosterId) {
        BossBar bar = activeBars.remove(boosterId);
        if (bar != null) {
            bar.removeAll();
        }
        trackingBoosters.remove(boosterId);
    }

    private void tick() {
        if (!plugin.getConfig().getBoolean("bossbar.enabled", true)) return;

        int rotateTicks = plugin.getConfig().getInt("bossbar.rotate-interval-seconds", 5) * 20;
        int interval = plugin.getConfig().getInt("bossbar.update-interval-ticks", 20);
        ticksSinceRotate += interval;

        if (ticksSinceRotate >= rotateTicks) {
            ticksSinceRotate = 0;
            rotationIndex++;
        }

        for (ActiveBooster active : trackingBoosters.values()) {
            updateBar(active);
        }
    }
}
