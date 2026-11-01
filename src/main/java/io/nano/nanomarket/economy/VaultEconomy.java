// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.economy.VaultEconomy
package io.nano.nanomarket.economy;

import io.nano.nanomarket.economy.EconomyBridge;
import io.nano.nanomarket.util.Money;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public final class VaultEconomy implements EconomyBridge {

    // ---- поля ----
  private final Logger log;
  private Object provider;
  private Method has;
  private Method balance;
  private Method withdraw;
  private Method deposit;
  private Method success;
  private String providerName;

  public VaultEconomy(Logger log) { // было: <init>
        super();
        providerName = "нет";
        this.log = log;
    }

  public synchronized void invalidate() {
        provider = null;
        providerName = "нет";
    }

  private synchronized boolean resolve() {
        boolean temp6;
        if (provider != null) {
            return true;
        }
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        if (vault == null || !vault.isEnabled()) {
            return false;
        }
        try {
            Class economy = Class.forName("net.milkbowl.vault.economy.Economy", true, vault.getClass().getClassLoader());
            RegisteredServiceProvider registeredServiceProvider = Bukkit.getServicesManager().getRegistration(economy);
            if (registeredServiceProvider != null) {
                Object object = registeredServiceProvider.getProvider();
                has = economy.getMethod("has", new Class[]{ OfflinePlayer.class, Double.TYPE });
                Class[] __obj2 = new Class[]{ OfflinePlayer.class };
                balance = economy.getMethod("getBalance", __obj2);
                Class[] __obj3 = new Class[]{ OfflinePlayer.class, Double.TYPE };
                withdraw = economy.getMethod("withdrawPlayer", __obj3);
                Class[] __obj4 = new Class[]{ OfflinePlayer.class, Double.TYPE };
                deposit = economy.getMethod("depositPlayer", __obj4);
                success = null;
                provider = object;
                providerName = registeredServiceProvider.getPlugin().getName();
                temp6 = true;
            } else {
                return false;
            }
        } catch (ReflectiveOperationException economy) {
            log.log(Level.WARNING, "Не удалось подключить экономику Vault: " + economy, economy);
            provider = null;
            return false;
        }
        return temp6;
    }

  public boolean available() {
        return resolve();
    }

  public synchronized String name() {
        resolve();
        return providerName;
    }

  public long balanceCents(OfflinePlayer player) {
        long temp2;
        if (!resolve()) {
            return 0L;
        }
        try {
            temp2 = Money.fromDouble(balance.invoke(provider, new Object[]{ player }));
        } catch (ReflectiveOperationException e1) {
            log.log(Level.WARNING, "Vault: getBalance не удался", e1);
            return 0L;
        }
        return temp2;
    }

  public boolean has(OfflinePlayer player, long cents) {
        Object temp2;
        if (!resolve()) {
            return false;
        }
        try {
            temp2 = has.invoke(provider, new Object[]{ player, Double.valueOf(Money.toDouble(cents)) });
        } catch (ReflectiveOperationException e1) {
            log.log(Level.WARNING, "Vault: has не удался", e1);
            return false;
        }
        return temp2;
    }

  public boolean withdraw(OfflinePlayer player, long cents) {
        return transact(withdraw, player, cents);
    }

  public boolean deposit(OfflinePlayer player, long cents) {
        return transact(deposit, player, cents);
    }

  private boolean transact(Method method, OfflinePlayer player, long cents) {
        Object temp3;
        if (!resolve()) {
            return false;
        }
        try {
            Object response = method.invoke(provider, new Object[]{ player, Double.valueOf(Money.toDouble(cents)) });
            if (response == null) {
                return false;
            }
            synchronized (this) {
                Throwable var8;
                try {
                    if (success == null) {
                        success = response.getClass().getMethod("transactionSuccess", new Class[0]);
                    }
                    Method ok = success;
                    temp3 = ok.invoke(response, new Object[0]);
                } catch (Throwable e1) {
                    while (true) {
                        var8 = e1;
                        throw var8;
                    }
                }
                throw var8;
            }
        } catch (ReflectiveOperationException response) {
            log.log(Level.WARNING, "Vault: операция не удалась", (Throwable) response);
            return false;
        }
        return temp3;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
