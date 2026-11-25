package com.jumpmap.donation;

import com.jumpmap.donation.commands.DonationCommand;
import com.jumpmap.donation.commands.PriceAdminCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class JumpMapDonationPlugin extends JavaPlugin {

    private PriceManager priceManager;
    private EffectManager effectManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.priceManager = new PriceManager(this);
        this.effectManager = new EffectManager(this);

        getServer().getPluginManager().registerEvents(new PlayerStateListener(effectManager), this);

        getCommand("jumpmapdonate").setExecutor(new DonationCommand(this, priceManager, effectManager));
        getCommand("jmprice").setExecutor(new PriceAdminCommand(this, priceManager));

        getLogger().info("JumpMapDonation 플러그인이 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        getLogger().info("JumpMapDonation 플러그인이 비활성화되었습니다.");
    }
}
