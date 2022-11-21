package me.youhavetrouble.thegreatmotivator;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class TheGreatMotivator extends JavaPlugin {

    private static Economy vaultEcon = null;

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().info("Vault not detected");
        } else {
            getLogger().info("Vault detected");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> serviceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (serviceProvider == null) return false;
        vaultEcon = serviceProvider.getProvider();
        return vaultEcon != null;
    }

    public static Economy getVaultEconomy() {
        return vaultEcon;
    }
}
