package com.jumpmap.donation.commands;

import com.jumpmap.donation.DonationItem;
import com.jumpmap.donation.PriceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * /jmprice list | set <항목키> <가격> | mode <EXACT|THRESHOLD> | spawn
 */
public class PriceAdminCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final PriceManager priceManager;

    public PriceAdminCommand(JavaPlugin plugin, PriceManager priceManager) {
        this.plugin = plugin;
        this.priceManager = priceManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                handleList(sender);
                break;
            case "set":
                handleSet(sender, args);
                break;
            case "mode":
                handleMode(sender, args);
                break;
            case "spawn":
                handleSpawn(sender);
                break;
            default:
                sendUsage(sender);
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "=== JumpMapDonation 관리자 명령어 ===");
        sender.sendMessage(ChatColor.GRAY + "/jmprice list " + ChatColor.WHITE + "- 전체 항목/가격 확인");
        sender.sendMessage(ChatColor.GRAY + "/jmprice set <항목키> <가격> " + ChatColor.WHITE + "- 항목 가격 설정");
        sender.sendMessage(ChatColor.GRAY + "/jmprice mode <EXACT|THRESHOLD> " + ChatColor.WHITE + "- 매칭 방식 변경");
        sender.sendMessage(ChatColor.GRAY + "/jmprice spawn " + ChatColor.WHITE + "- '태초' 위치를 현재 위치로 저장");
    }

    private void handleList(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== 항목별 가격 (매칭 방식: " + priceManager.getMatchMode() + ") ===");
        for (DonationItem item : DonationItem.values()) {
            int price = priceManager.getPrice(item);
            String priceStr = price < 0 ? ChatColor.RED + "미설정" : ChatColor.GREEN + String.valueOf(price) + "원";
            sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.WHITE + item.getDisplayName()
                    + ChatColor.DARK_GRAY + " [key: " + item.getKey() + "] "
                    + ChatColor.WHITE + ": " + priceStr);
        }
    }

    private void handleSet(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "사용법: /jmprice set <항목키> <가격>");
            return;
        }

        DonationItem item = DonationItem.fromKey(args[1]);
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "알 수 없는 항목키입니다: " + args[1]
                    + " (instant-death, reset, debuff-remove, health-down, bind, poison, blindness, hunger-down, reverse-controls, knockback)");
            return;
        }

        int price;
        try {
            price = Integer.parseInt(args[2].trim());
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "가격은 숫자여야 합니다: " + args[2]);
            return;
        }

        if (price < 0) {
            sender.sendMessage(ChatColor.RED + "가격은 0 이상이어야 합니다.");
            return;
        }

        priceManager.setPrice(item, price);
        sender.sendMessage(ChatColor.GREEN + item.getDisplayName() + " 가격을 " + price + "원으로 설정했습니다.");
    }

    private void handleMode(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "사용법: /jmprice mode <EXACT|THRESHOLD>");
            return;
        }

        try {
            PriceManager.MatchMode mode = PriceManager.MatchMode.valueOf(args[1].trim().toUpperCase());
            priceManager.setMatchMode(mode);
            sender.sendMessage(ChatColor.GREEN + "매칭 방식을 " + mode + "(으)로 변경했습니다.");
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(ChatColor.RED + "알 수 없는 모드입니다. EXACT 또는 THRESHOLD 중 선택하세요.");
        }
    }

    private void handleSpawn(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "이 명령어는 게임 내에서 플레이어만 사용할 수 있습니다.");
            return;
        }

        Player player = (Player) sender;
        plugin.getConfig().set("settings.reset.world", player.getWorld().getName());
        plugin.getConfig().set("settings.reset.x", player.getLocation().getX());
        plugin.getConfig().set("settings.reset.y", player.getLocation().getY());
        plugin.getConfig().set("settings.reset.z", player.getLocation().getZ());
        plugin.getConfig().set("settings.reset.yaw", (double) player.getLocation().getYaw());
        plugin.getConfig().set("settings.reset.pitch", (double) player.getLocation().getPitch());
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GREEN + "'태초' 텔레포트 위치를 현재 위치로 저장했습니다.");
    }
}
