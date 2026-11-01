// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.service.SearchPrompts
package io.nano.nanomarket.service;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.gui.BrowseMenu;
import io.nano.nanomarket.service.PlayerSessions;
import io.nano.nanomarket.service.PlayerSessions.Session;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public final class SearchPrompts {

    // ---- поля ----
  private static final int SECONDS = 30;
  private static final int MAX_QUERY = 48;
  private final NanoMarket plugin;
  private final Map<UUID, Long> deadlines;

  public SearchPrompts(NanoMarket plugin) { // было: <init>
        super();
        deadlines = new ConcurrentHashMap();
        this.plugin = plugin;
    }

  public void begin(Player player) {
        long deadline = System.currentTimeMillis() + 30000L;
        deadlines.put(player.getUniqueId(), Long.valueOf(deadline));
        player.closeInventory();
        plugin.messages().sendLines(player, "search.prompt", new Object[]{ "seconds", Integer.valueOf(30) });
        Bukkit.getScheduler().runTaskLater(plugin, () -> lambda_begin_0(player, deadline), 620L);
    }

  public boolean consume(UUID id) {
        Long deadline = (Long) deadlines.remove(id);
        return deadline != null && deadline.longValue() >= System.currentTimeMillis();
    }

  public void submit(Player player, String input) {
        String text = input != null ? input.trim() : "";
        if (text.length() > 48) {
            text = text.substring(0, 48);
        }
        Session session = plugin.sessions().of(player.getUniqueId());
        if (text.isEmpty()) {
            plugin.messages().send(player, "search.cancelled", new Object[0]);
        } else if (text.equalsIgnoreCase(plugin.messages().raw("search.cancel-word"))) {
            plugin.messages().send(player, "search.cancelled", new Object[0]);
        } else {
            session.query = text;
            session.page = 1;
            plugin.messages().send(player, "search.set", new Object[]{ "query", text });
        }
        BrowseMenu.show(plugin, player);
    }

  public void forget(UUID id) {
        deadlines.remove(id);
    }

  private void lambda_begin_0(Player player, long deadline) { // было: lambda_begin_0
        Long current = (Long) deadlines.get(player.getUniqueId());
        if (current != null && current.longValue() == deadline) {
            deadlines.remove(player.getUniqueId());
            if (player.isOnline()) {
                plugin.messages().send(player, "search.timeout", new Object[0]);
            }
        }
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
