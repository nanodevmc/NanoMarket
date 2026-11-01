// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.service.PayoutService
package io.nano.nanomarket.service;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.config.Settings;
import io.nano.nanomarket.config.Settings.Payout;
import io.nano.nanomarket.config.SoundBank;
import io.nano.nanomarket.economy.VaultEconomy;
import io.nano.nanomarket.model.Sale;
import io.nano.nanomarket.service.MarketLog;
import io.nano.nanomarket.service.MarketService;
import io.nano.nanomarket.storage.Database;
import io.nano.nanomarket.storage.SaleStore;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public final class PayoutService {

    // ---- поля ----
  private final NanoMarket plugin;
  private BukkitTask task;

  public PayoutService(NanoMarket plugin) { // было: <init>
        super();
        this.plugin = plugin;
    }

  public void start() {
        stop();
        Payout cfg = plugin.settings().payout();
        long period = (long) cfg.intervalSeconds() * 20L;
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::cycle, period, period);
    }

  public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

  private void cycle() {
        if (plugin.ready()) {
            Payout cfg = plugin.settings().payout();
            Set online = onlineIds();
            if (online.isEmpty()) {
                return;
            } else {
                if (cfg.announce() && cfg.announceSeconds() > 0) {
                    plugin.database().async(lp0 -> lambda_cycle_0(online, lp0)).whenComplete((lp0, lp1) -> lambda_cycle_4(cfg, (Map) lp0, (Throwable) lp1));
                    return;
                }
                payout(online);
                return;
            }
        }
    }

  public void payNow() {
        payout(onlineIds());
    }

  public void onJoin(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> lambda_onJoin_5(player), 40L);
    }

  private Set<UUID> onlineIds() {
        HashSet ids = new HashSet();
        Iterator iterator = Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player) iterator.next();
            ids.add(player.getUniqueId());
        }
        return ids;
    }

  private void payout(Collection<UUID> sellers) {
        if (plugin.ready() && !sellers.isEmpty()) {
            if (plugin.economy().available()) {
                plugin.database().async(lp0 -> lambda_payout_6(sellers, lp0)).whenComplete((lp0, lp1) -> lambda_payout_8((List) lp0, (Throwable) lp1));
                return;
            } else {
                return;
            }
        }
    }

  private void deposit(List<Sale> claimed) {
        LinkedHashMap bySeller = new LinkedHashMap();
        Iterator failed = claimed.iterator();
        Sale sale;
        while (failed.hasNext()) {
            sale = (Sale) failed.next();
            ((List) bySeller.computeIfAbsent(sale.sellerId(), lp0 -> lambda_deposit_9((UUID) lp0))).add(sale);
        }
        failed = new ArrayList();
        Iterator sale = bySeller.entrySet().iterator();
        while (sale.hasNext()) {
            Entry entry = (Entry) sale.next();
            long total = ((List) entry.getValue()).stream().mapToLong(Sale::totalCents).sum();
            OfflinePlayer seller = Bukkit.getOfflinePlayer((UUID) entry.getKey());
            Player online = Bukkit.getPlayer((UUID) entry.getKey());
            if (plugin.economy().deposit(seller, total)) {
                if (online != null) {
                    plugin.messages().send(online, "payout.received", new Object[]{ "total", Money.format(total), "count", Integer.valueOf(((List) entry.getValue()).size()) });
                    plugin.sounds().play(online, "payout");
                }
            } else {
                ((List) entry.getValue()).forEach(lp0 -> lambda_deposit_10(failed, (Sale) lp0));
                if (online != null) {
                    plugin.messages().send(online, "payout.failed", new Object[0]);
                }
            }
        }
        if (!failed.isEmpty()) {
            plugin.database().async(lp0 -> lambda_deposit_11(failed, lp0)).exceptionally(lp0 -> lambda_deposit_12(failed, (Throwable) lp0));
        }
    }

  private Object lambda_deposit_12(List failed, Throwable throwable) { // было: lambda_deposit_12
        plugin.marketLog().critical("Не удалось вернуть в очередь невыплаченные продажи " + failed + ": " + MarketService.rootMessage(throwable));
        return null;
    }

  private Object lambda_deposit_11(List failed, Connection connection) { // было: lambda_deposit_11
        plugin.sales().unclaim(failed);
        return null;
    }

  private static void lambda_deposit_10(List failed, Sale sale) { // было: lambda_deposit_10
        failed.add(Long.valueOf(sale.id()));
    }

  private static List lambda_deposit_9(UUID uUID) { // было: lambda_deposit_9
        return new ArrayList();
    }

  private void lambda_payout_8(List claimed, Throwable err) { // было: lambda_payout_8
        plugin.sync(() -> lambda_payout_7(err, claimed));
    }

  private void lambda_payout_7(Throwable err, List claimed) { // было: lambda_payout_7
        if (err != null) {
            plugin.getLogger().warning("Выплата: ошибка базы данных: " + MarketService.rootMessage(err));
            return;
        }
        if (!claimed.isEmpty()) {
            deposit(claimed);
        }
    }

  private List lambda_payout_6(Collection sellers, Connection connection) { // было: lambda_payout_6
        return plugin.sales().claimUnpaid(sellers, System.currentTimeMillis());
    }

  private void lambda_onJoin_5(Player player) { // было: lambda_onJoin_5
        if (player.isOnline() && plugin.ready()) {
            payout(Set.of(player.getUniqueId()));
        }
    }

  private void lambda_cycle_4(Payout cfg, Map pending, Throwable err) { // было: lambda_cycle_4
        plugin.sync(() -> lambda_cycle_3(err, pending, cfg));
    }

  private void lambda_cycle_3(Throwable err, Map pending, Payout cfg) { // было: lambda_cycle_3
        if (err == null && !pending.isEmpty()) {
            pending.forEach((lp0, lp1) -> lambda_cycle_1(cfg, (UUID) lp0, (Integer) lp1));
            Bukkit.getScheduler().runTaskLater(plugin, () -> lambda_cycle_2(), (long) cfg.announceSeconds() * 20L);
            return;
        }
    }

  private void lambda_cycle_2() { // было: lambda_cycle_2
        payout(onlineIds());
    }

  private void lambda_cycle_1(Payout cfg, UUID id, Integer count) { // было: lambda_cycle_1
        Player player = Bukkit.getPlayer(id);
        if (player != null) {
            plugin.messages().send(player, "payout.warning", new Object[]{ "seconds", Integer.valueOf(cfg.announceSeconds()), "count", count });
        }
    }

  private Map lambda_cycle_0(Set online, Connection connection) { // было: lambda_cycle_0
        return plugin.sales().pendingCounts(online);
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
