package com.jumpmap.donation;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class PriceManager {

    public enum MatchMode {
        EXACT,
        THRESHOLD
    }

    private final JavaPlugin plugin;
    private final Map<DonationItem, Integer> prices = new EnumMap<>(DonationItem.class);
    private MatchMode matchMode = MatchMode.EXACT;

    public PriceManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        FileConfiguration cfg = plugin.getConfig();

        String modeStr = cfg.getString("match-mode", "EXACT");
        try {
            this.matchMode = MatchMode.valueOf(modeStr.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("알 수 없는 match-mode: " + modeStr + " -> EXACT로 대체합니다.");
            this.matchMode = MatchMode.EXACT;
        }

        for (DonationItem item : DonationItem.values()) {
            prices.put(item, cfg.getInt(item.getPath(), -1));
        }
    }

    public void setPrice(DonationItem item, int price) {
        prices.put(item, price);
        plugin.getConfig().set(item.getPath(), price);
        plugin.saveConfig();
    }

    public int getPrice(DonationItem item) {
        return prices.getOrDefault(item, -1);
    }

    public MatchMode getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(MatchMode mode) {
        this.matchMode = mode;
        plugin.getConfig().set("match-mode", mode.name());
        plugin.saveConfig();
    }

    /**
     * EXACT     : 가격이 amount 와 정확히 같은 항목
     * THRESHOLD : 가격이 amount 이하인 항목 중 가장 비싼 항목
     */
    public Optional<DonationItem> resolve(int amount) {
        DonationItem best = null;

        for (DonationItem item : DonationItem.values()) {
            int price = prices.getOrDefault(item, -1);
            if (price < 0) {
                continue;
            }

            if (matchMode == MatchMode.EXACT) {
                if (price == amount) {
                    return Optional.of(item);
                }
            } else if (price <= amount && (best == null || price > prices.get(best))) {
                best = item;
            }
        }

        return Optional.ofNullable(best);
    }
}
