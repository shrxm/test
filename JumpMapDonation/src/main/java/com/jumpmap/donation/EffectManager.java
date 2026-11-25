package com.jumpmap.donation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EffectManager {

    private final JavaPlugin plugin;

    // 구속된 플레이어의 고정 좌표
    private final Map<UUID, Location> rootedAnchors = new HashMap<>();
    // 조작키 반전이 적용 중인 플레이어
    private final Map<UUID, Boolean> reversedPlayers = new HashMap<>();

    public EffectManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void apply(DonationItem item, Player player) {
        switch (item) {
            case INSTANT_DEATH:
                player.setHealth(0.0);
                break;
            case RESET:
                resetToSpawn(player);
                break;
            case DEBUFF_REMOVE:
                clearDebuffs(player);
                break;
            case DEBUFF_HEALTH_DOWN:
                // 체력 1칸 = 2 HP
                player.setHealth(Math.max(0.0, player.getHealth() - 2.0));
                break;
            case DEBUFF_BIND:
                bind(player);
                break;
            case DEBUFF_POISON:
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                        plugin.getConfig().getInt("settings.poison.duration-seconds", 8) * 20,
                        plugin.getConfig().getInt("settings.poison.amplifier", 1)));
                break;
            case DEBUFF_BLINDNESS:
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                        plugin.getConfig().getInt("settings.blindness.duration-seconds", 10) * 20, 0));
                break;
            case DEBUFF_HUNGER_DOWN:
                // 배고픔 1칸 = 2 food point
                player.setFoodLevel(Math.max(0, player.getFoodLevel() - 2));
                break;
            case DEBUFF_REVERSE_CONTROLS:
                reverseControls(player);
                break;
            case DEBUFF_KNOCKBACK:
                knockback(player);
                break;
            default:
                plugin.getLogger().warning("처리되지 않은 DonationItem: " + item);
        }
    }

    // 태초: 점프맵 시작 지점으로 텔레포트
    private void resetToSpawn(Player player) {
        String worldName = plugin.getConfig().getString("settings.reset.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("리셋 위치의 월드를 찾을 수 없습니다: " + worldName);
            return;
        }
        Location loc = new Location(world,
                plugin.getConfig().getDouble("settings.reset.x"),
                plugin.getConfig().getDouble("settings.reset.y"),
                plugin.getConfig().getDouble("settings.reset.z"),
                (float) plugin.getConfig().getDouble("settings.reset.yaw"),
                (float) plugin.getConfig().getDouble("settings.reset.pitch"));
        player.teleport(loc);
    }

    // 디버프 제거: 포션 효과 + 구속/조작키 반전 상태 해제
    private void clearDebuffs(Player player) {
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            player.removePotionEffect(effect.getType());
        }
        rootedAnchors.remove(player.getUniqueId());
        reversedPlayers.remove(player.getUniqueId());
    }

    private void knockback(Player player) {
        double strength = plugin.getConfig().getDouble("settings.knockback.strength", 1.5);
        Vector direction = player.getLocation().getDirection().setY(0).normalize().multiply(-strength);
        direction.setY(0.5);
        player.setVelocity(direction);
    }

    // 구속: 일정 시간 위치 고정 (시야 회전은 허용)
    private void bind(Player player) {
        UUID id = player.getUniqueId();
        int seconds = plugin.getConfig().getInt("settings.bind.duration-seconds", 5);

        rootedAnchors.put(id, player.getLocation().clone());

        new BukkitRunnable() {
            @Override
            public void run() {
                rootedAnchors.remove(id);
            }
        }.runTaskLater(plugin, seconds * 20L);
    }

    // 조작키 반전: 수평 이동 방향 반전
    private void reverseControls(Player player) {
        UUID id = player.getUniqueId();
        int seconds = plugin.getConfig().getInt("settings.reverse-controls.duration-seconds", 8);

        reversedPlayers.put(id, true);

        new BukkitRunnable() {
            @Override
            public void run() {
                reversedPlayers.remove(id);
            }
        }.runTaskLater(plugin, seconds * 20L);
    }

    public boolean isRooted(UUID id) {
        return rootedAnchors.containsKey(id);
    }

    public Location getAnchor(UUID id) {
        return rootedAnchors.get(id);
    }

    public boolean isReversed(UUID id) {
        return reversedPlayers.containsKey(id);
    }
}
