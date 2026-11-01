// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.listener.PlayerListener
package io.nano.nanomarket.listener;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.economy.VaultEconomy;
import io.nano.nanomarket.service.PayoutService;
import io.nano.nanomarket.service.PlayerSessions;
import io.nano.nanomarket.service.RateLimiter;
import io.nano.nanomarket.service.SearchPrompts;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

public final class PlayerListener implements Listener {

    // ---- поля ----
  private final NanoMarket plugin;

  public PlayerListener(NanoMarket plugin) { // было: <init>
        super();
        this.plugin = plugin;
    }

    @EventHandler
  public void onJoin(PlayerJoinEvent event) {
        if (plugin.ready()) {
            plugin.payouts().onJoin(event.getPlayer());
            return;
        }
        if (event.getPlayer().hasPermission("nanomarket.admin")) {
            plugin.messages().send(event.getPlayer(), "market-offline", new Object[0]);
        }
    }

    @EventHandler
  public void onQuit(PlayerQuitEvent event) {
        plugin.sessions().forget(event.getPlayer().getUniqueId());
        plugin.limiter().forget(event.getPlayer().getUniqueId());
        plugin.prompts().forget(event.getPlayer().getUniqueId());
    }

    @EventHandler
  public void onServiceRegistered(ServiceRegisterEvent event) {
        plugin.vault().invalidate();
    }

    @EventHandler
  public void onServiceUnregistered(ServiceUnregisterEvent event) {
        plugin.vault().invalidate();
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
