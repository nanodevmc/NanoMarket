// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.listener.SearchListener
package io.nano.nanomarket.listener;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.service.SearchPrompts;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitScheduler;

public final class SearchListener implements Listener {

    // ---- поля ----
  private final NanoMarket plugin;

  public SearchListener(NanoMarket plugin) { // было: <init>
        super();
        this.plugin = plugin;
    }

    @EventHandler(priority = "LOWEST")
  public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.prompts().consume(player.getUniqueId())) {
            event.setCancelled(true);
            String text = event.getMessage();
            Bukkit.getScheduler().runTask(plugin, () -> lambda_onChat_0(player, text));
            return;
        }
    }

  private void lambda_onChat_0(Player player, String text) { // было: lambda_onChat_0
        plugin.prompts().submit(player, text);
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
