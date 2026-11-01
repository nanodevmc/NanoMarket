// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.economy.EconomyBridge
package io.nano.nanomarket.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyBridge {

  public boolean available();

  public String name();

  public long balanceCents(OfflinePlayer offlinePlayer);

  public boolean has(OfflinePlayer offlinePlayer, long n);

  public boolean withdraw(OfflinePlayer offlinePlayer, long n);

  public boolean deposit(OfflinePlayer offlinePlayer, long n);

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
