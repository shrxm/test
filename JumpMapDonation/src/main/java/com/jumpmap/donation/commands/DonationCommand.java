package com.jumpmap.donation.commands;

import com.jumpmap.donation.DonationItem;
import com.jumpmap.donation.EffectManager;
import com.jumpmap.donation.PriceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * SSAPI-Minecraft 의 commands.donation.format 에서 호출되는 명령어.
 * 사용법: /jumpmapdonate <플레이어> <금액> [후원자닉네임]
 */
public class DonationCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final PriceManager priceManager;
    private final EffectManager effectManager;

    public DonationCommand(JavaPlugin plugin, PriceManager priceManager, EffectManager effectManager) {
        this.plugin = plugin;
        this.priceManager = priceManager;
        this.effectManager = effectManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "사용법: /jumpmapdonate <플레이어> <금액> [후원자닉네임]");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "플레이어를 찾을 수 없습니다: " + args[0]);
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "금액은 숫자여야 합니다: " + args[1]);
            return true;
        }

        String donatorName = args.length >= 3 ? args[2] : "익명";

        Optional<DonationItem> resolved = priceManager.resolve(amount);
        if (!resolved.isPresent()) {
            sender.sendMessage(ChatColor.YELLOW + "금액 " + amount + "원에 해당하는 항목이 없습니다. (match-mode: "
                    + priceManager.getMatchMode() + ")");
            return true;
        }

        DonationItem item = resolved.get();
        effectManager.apply(item, target);

        if (plugin.getConfig().getBoolean("broadcast-message", true)) {
            Bukkit.broadcastMessage(ChatColor.AQUA + "[후원] " + ChatColor.WHITE + donatorName
                    + "님의 후원으로 " + target.getName() + "님에게 '"
                    + ChatColor.GOLD + item.getDisplayName() + ChatColor.WHITE + "' 효과가 적용되었습니다! ("
                    + ChatColor.YELLOW + amount + "원" + ChatColor.WHITE + ")");
        }

        return true;
    }
}
