package com.jumpmap.donation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerStateListener implements Listener {

    private final EffectManager effectManager;

    public PlayerStateListener(EffectManager effectManager) {
        this.effectManager = effectManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (event.getTo() == null) {
            return;
        }

        // 구속: 좌표 이동은 막고 시야 회전만 허용
        if (effectManager.isRooted(id)) {
            Location anchor = effectManager.getAnchor(id);
            Location to = event.getTo();

            boolean moved = anchor.getX() != to.getX()
                    || anchor.getY() != to.getY()
                    || anchor.getZ() != to.getZ();

            if (moved) {
                Location fixed = anchor.clone();
                fixed.setYaw(to.getYaw());
                fixed.setPitch(to.getPitch());
                event.setTo(fixed);
            }
            return;
        }

        // 조작키 반전: 수평 이동(x, z)만 반전, 수직 이동과 시야는 유지
        if (effectManager.isReversed(id)) {
            Location from = event.getFrom();
            Location to = event.getTo();

            double dx = to.getX() - from.getX();
            double dz = to.getZ() - from.getZ();

            if (dx != 0.0 || dz != 0.0) {
                Location inverted = from.clone();
                inverted.setX(from.getX() - dx);
                inverted.setZ(from.getZ() - dz);
                inverted.setY(to.getY());
                inverted.setYaw(to.getYaw());
                inverted.setPitch(to.getPitch());
                event.setTo(inverted);
            }
        }
    }
}
