// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.service.MarketService
package io.nano.nanomarket.service;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.ItemCatalog;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.config.Settings;
import io.nano.nanomarket.config.Settings.Gui;
import io.nano.nanomarket.config.Settings.Market;
import io.nano.nanomarket.config.Settings.Payout;
import io.nano.nanomarket.config.Settings.PayoutMode;
import io.nano.nanomarket.config.Settings.Purchase;
import io.nano.nanomarket.config.SoundBank;
import io.nano.nanomarket.economy.VaultEconomy;
import io.nano.nanomarket.model.Lot;
import io.nano.nanomarket.model.Page;
import io.nano.nanomarket.model.Sale;
import io.nano.nanomarket.model.SortMode;
import io.nano.nanomarket.service.MarketLog;
import io.nano.nanomarket.service.MarketService.Cancelled;
import io.nano.nanomarket.service.MarketService.Receipt;
import io.nano.nanomarket.service.PlayerSessions;
import io.nano.nanomarket.service.PlayerSessions.Session;
import io.nano.nanomarket.service.Result;
import io.nano.nanomarket.storage.Database;
import io.nano.nanomarket.storage.LotStore;
import io.nano.nanomarket.storage.SaleStore;
import io.nano.nanomarket.util.Inventories;
import java.sql.Connection;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class MarketService {

    // ---- поля ----
  private final NanoMarket plugin;

  public MarketService(NanoMarket plugin) { // было: <init>
        super();
        this.plugin = plugin;
    }

  public CompletableFuture<Page<Lot>> browse(String query, SortMode sort, int page) {
        int size = plugin.settings().gui().pageSize();
        return onMain(plugin.database().async(lp0 -> lambda_browse_0(query, sort, page, size, lp0)));
    }

  public CompletableFuture<Page<Lot>> myLots(UUID seller, int page) {
        int size = plugin.settings().gui().pageSize();
        return onMain(plugin.database().async(lp0 -> lambda_myLots_1(seller, page, size, lp0)));
    }

  public CompletableFuture<Lot> find(long id) {
        return onMain(plugin.database().async(lp0 -> lambda_find_2(id, lp0)));
    }

  public CompletableFuture<List<Lot>> similar(Lot lot, int limit) {
        return limit > 0 ? onMain(plugin.database().async(lp0 -> lambda_similar_3(lot, limit, lp0))) : CompletableFuture.completedFuture(List.of());
    }

  public CompletableFuture<Integer> activeCount(UUID seller) {
        return onMain(plugin.database().async(lp0 -> lambda_activeCount_4(seller, lp0)));
    }

  public CompletableFuture<Result<Lot>> list(Player seller, ItemStack hand, long unitCents, int amount, boolean bypassLimit) {
        Settings settings = plugin.settings();
        ItemStack template = hand.clone();
        template.setAmount(1);
        UUID id = seller.getUniqueId();
        return plugin.database().async(lp0 -> lambda_list_5(id, lp0)).handle((lp0, lp1) -> lambda_list_10(bypassLimit, settings, seller, template, amount, id, unitCents, (Integer) lp0, (Throwable) lp1)).thenCompose(lp0 -> lambda_list_11((CompletableFuture) lp0));
    }

  private void takeFromHand(Player player, ItemStack inHand, int amount) {
        if (inHand.getAmount() != amount) {
            inHand.setAmount(inHand.getAmount() - amount);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }

  public CompletableFuture<Result<Receipt>> buy(Player buyer, Lot snapshot, int units) {
        Settings settings = plugin.settings();
        Session session = plugin.sessions().of(buyer.getUniqueId());
        if (!plugin.economy().available()) {
            return done(Result.fail("economy-missing", new Object[0]));
        }
        if (buyer.getUniqueId().equals(snapshot.sellerId())) {
            return done(Result.fail("buy.own-lot", new Object[0]));
        }
        if (units >= 1 && units <= settings.purchase().maxPerPurchase()) {
            if (units <= snapshot.remaining()) {
                long now = System.currentTimeMillis();
                long wait = settings.purchase().cooldownMs() - (now - session.lastPurchaseAt);
                if (wait <= 0L) {
                    long total = snapshot.totalCents(units);
                    if (!plugin.economy().has(buyer, total)) {
                        return done(Result.fail("buy.not-enough-money", new Object[]{ "need", Money.format(total), "balance", Money.format(plugin.economy().balanceCents(buyer)) }));
                    } else if (spaceMissing(buyer, snapshot.template(), units)) {
                        return done(Result.fail("buy.no-space", new Object[0]));
                    } else {
                        session.lastPurchaseAt = now;
                        CompletableFuture out = new CompletableFuture();
                        plugin.database().async(lp0 -> lambda_buy_12(snapshot, units, lp0)).whenComplete((lp0, lp1) -> lambda_buy_14(out, snapshot, buyer, units, total, (Boolean) lp0, (Throwable) lp1));
                        return out;
                    }
                } else {
                    return done(Result.fail("purchase-cooldown", new Object[]{ "seconds", Long.valueOf(Math.max(1L, (wait + 999L) / 1000L)) }));
                }
            } else {
                return done(Result.fail("buy.not-enough-stock", new Object[]{ "available", Integer.valueOf(snapshot.remaining()) }));
            }
        }
        return done(Result.fail("buy.failed", new Object[0]));
    }

  private boolean spaceMissing(Player buyer, ItemStack template, int units) {
        Purchase purchase = plugin.settings().purchase();
        return purchase.checkSpace() && !purchase.dropIfNoSpace() && !Inventories.canHold(buyer, template, units);
    }

  private void explainRefusal(Lot snapshot, CompletableFuture<Result<Receipt>> out) {
        plugin.database().async(lp0 -> lambda_explainRefusal_15(snapshot, lp0)).whenComplete((lp0, lp1) -> lambda_explainRefusal_17(out, (Lot) lp0, (Throwable) lp1));
    }

  private void finishPurchase(Player buyer, Lot lot, int units, long total, CompletableFuture<Result<Receipt>> out) {
        long temp17;
        Object temp18;
        String temp19;
        long temp20;
        long temp21;
        long temp22;
        int temp23;
        String temp24;
        String temp25;
        UUID temp26;
        String temp27;
        UUID temp28;
        long temp29;
        long temp30;
        Sale temp32;
        if (!buyer.isOnline()) {
            releaseAsync(lot, units);
            out.complete(Result.fail("buy.failed", new Object[0]));
            return;
        }
        if (spaceMissing(buyer, lot.template(), units)) {
            releaseAsync(lot, units);
            out.complete(Result.fail("buy.no-space", new Object[0]));
            return;
        }
        if (plugin.economy().withdraw(buyer, total)) {
            Inventories.giveOrDrop(buyer, lot.template(), units);
            Payout payout = plugin.settings().payout();
            OfflinePlayer seller = Bukkit.getOfflinePlayer(lot.sellerId());
            if (payout.mode() != PayoutMode.INSTANT) {
            } else if (plugin.economy().deposit(seller, total)) {
            }
            Object paid = __entry0__;
            long now = System.currentTimeMillis();
            if (paid == 0) {
                temp17 = 0L;
                temp18 = paid;
                temp19 = plugin.settings().serverId();
                temp20 = now;
                temp21 = total;
                temp22 = lot.unitCents();
                temp23 = units;
                temp24 = lot.material();
                temp25 = lot.sellerName();
                temp26 = lot.sellerId();
                temp27 = buyer.getName();
                temp28 = buyer.getUniqueId();
                temp29 = lot.id();
                temp30 = 0L;
                temp32 = new Sale();
            } else {
                temp17 = now;
                temp18 = paid;
                temp19 = plugin.settings().serverId();
                temp20 = now;
                temp21 = total;
                temp22 = lot.unitCents();
                temp23 = units;
                temp24 = lot.material();
                temp25 = lot.sellerName();
                temp26 = lot.sellerId();
                temp27 = buyer.getName();
                temp28 = buyer.getUniqueId();
                temp29 = lot.id();
                temp30 = 0L;
                temp32 = new Sale();
            }
            Sale sale = temp32;
            plugin.database().async(lp0 -> lambda_finishPurchase_18(sale, lp0)).whenComplete((lp0, lp1) -> lambda_finishPurchase_20(sale, (Long) lp0, (Throwable) lp1));
            plugin.marketLog().sale(sale, plugin.catalog().displayName(lot.template()));
            if (paid != 0) {
                Player onlineSeller = Bukkit.getPlayer(lot.sellerId());
                if (onlineSeller != null) {
                    plugin.messages().send(onlineSeller, "sale-notify", new Object[]{ "buyer", buyer.getName(), "amount", Integer.valueOf(units), "item", plugin.catalog().displayName(lot.template()), "total", Money.format(total) });
                    plugin.sounds().play(onlineSeller, "payout");
                }
            }
            out.complete(Result.ok(new Receipt(lot, units, total)));
            return;
        }
        releaseAsync(lot, units);
        out.complete(Result.fail("buy.economy-error", new Object[0]));
    }

  private void releaseAsync(Lot lot, int units) {
        plugin.database().async(lp0 -> lambda_releaseAsync_21(lot, units, lp0)).exceptionally(lp0 -> lambda_releaseAsync_22(units, lot, (Throwable) lp0));
    }

  public CompletableFuture<Result<Cancelled>> cancel(Player player, long lotId) {
        CompletableFuture out = new CompletableFuture();
        plugin.database().async(lp0 -> lambda_cancel_23(lotId, lp0)).whenComplete((lp0, lp1) -> lambda_cancel_28(out, player, lotId, (Lot) lp0, (Throwable) lp1));
        return out;
    }

  private <T> CompletableFuture<T> onMain(CompletableFuture<T> source) {
        CompletableFuture out = new CompletableFuture();
        source.whenComplete((lp0, lp1) -> lambda_onMain_30(out, lp0, (Throwable) lp1));
        return out;
    }

  private static <T> CompletableFuture<Result<T>> done(Result<T> result) {
        return CompletableFuture.completedFuture(result);
    }

  private void logError(String what, Throwable err) {
        plugin.getLogger().log(Level.SEVERE, "Ошибка базы данных (" + what + "): " + rootMessage(err), err);
    }

  static String rootMessage(Throwable throwable) {
        Throwable t = throwable;
        for (; t.getCause() != null; t = t.getCause()) {
            if (t.getCause() == t) {
                break;
            }
        }
        return t.getMessage() != null ? t.getMessage() : t.toString();
    }

  private void lambda_onMain_30(CompletableFuture out, Object value, Throwable err) { // было: lambda_onMain_30
        plugin.sync(() -> lambda_onMain_29(err, out, value));
    }

  private static void lambda_onMain_29(Throwable err, CompletableFuture out, Object value) { // было: lambda_onMain_29
        Throwable temp3;
        CompletableFuture temp4;
        if (err == null) {
            out.complete(value);
        } else {
            if (err instanceof CompletionException) {
                if (err.getCause() == null) {
                    temp3 = err;
                    temp4 = out;
                } else {
                    temp3 = err.getCause();
                    temp4 = out;
                }
            } else {
                temp3 = err;
                temp4 = out;
            }
            temp4.completeExceptionally(temp3);
        }
    }

  private void lambda_cancel_28(CompletableFuture out, Player player, long lotId, Lot lot, Throwable err) { // было: lambda_cancel_28
        plugin.sync(() -> lambda_cancel_27(err, out, lot, player, lotId));
    }

  private void lambda_cancel_27(Throwable err, CompletableFuture out, Lot lot, Player player, long lotId) { // было: lambda_cancel_27
        if (err != null) {
            logError("find", err);
            out.complete(Result.fail("cancel.failed", new Object[0]));
            return;
        }
        if (lot == null) {
            out.complete(Result.fail("cancel.not-found", new Object[0]));
            return;
        }
        if (!lot.sellerId().equals(player.getUniqueId())) {
            out.complete(Result.fail("cancel.not-owner", new Object[0]));
            return;
        }
        if (!lot.active()) {
            out.complete(Result.fail("cancel.sold-out", new Object[0]));
            return;
        }
        if (Inventories.canHold(player, lot.template(), lot.remaining())) {
            plugin.database().async(lp0 -> lambda_cancel_24(lotId, player, lp0)).whenComplete((lp0, lp1) -> lambda_cancel_26(out, player, lot, (OptionalInt) lp0, (Throwable) lp1));
            return;
        } else {
            out.complete(Result.fail("cancel.no-space", new Object[]{ "amount", Integer.valueOf(lot.remaining()) }));
            return;
        }
    }

  private void lambda_cancel_26(CompletableFuture out, Player player, Lot lot, OptionalInt units, Throwable error) { // было: lambda_cancel_26
        plugin.sync(() -> lambda_cancel_25(error, out, units, player, lot));
    }

  private void lambda_cancel_25(Throwable error, CompletableFuture out, OptionalInt units, Player player, Lot lot) { // было: lambda_cancel_25
        if (error == null) {
            OptionalInt back = units;
            if (!back.isEmpty() && back.getAsInt() > 0) {
                Inventories.giveOrDrop(player, lot.template(), back.getAsInt());
                out.complete(Result.ok(new Cancelled(lot, back.getAsInt())));
                return;
            }
            out.complete(Result.fail("cancel.sold-out", new Object[0]));
            return;
        }
        logError("cancel", error);
        out.complete(Result.fail("cancel.failed", new Object[0]));
    }

  private OptionalInt lambda_cancel_24(long lotId, Player player, Connection connection) { // было: lambda_cancel_24
        return plugin.lots().cancel(lotId, player.getUniqueId());
    }

  private Lot lambda_cancel_23(long lotId, Connection connection) { // было: lambda_cancel_23
        return plugin.lots().find(lotId);
    }

  private Object lambda_releaseAsync_22(int units, Lot lot, Throwable throwable) { // было: lambda_releaseAsync_22
        plugin.marketLog().critical("Не удалось вернуть резерв " + units + " шт. лота #" + lot.id() + ": " + rootMessage(throwable));
        return null;
    }

  private Object lambda_releaseAsync_21(Lot lot, int units, Connection connection) { // было: lambda_releaseAsync_21
        plugin.lots().release(lot.id(), units);
        return null;
    }

  private void lambda_finishPurchase_20(Sale sale, Long id, Throwable err) { // было: lambda_finishPurchase_20
        if (err != null) {
            plugin.sync(() -> lambda_finishPurchase_19(sale, err));
        }
    }

  private void lambda_finishPurchase_19(Sale sale, Throwable err) { // было: lambda_finishPurchase_19
        plugin.marketLog().critical("Продажа не записана в БД (деньги уже переведены): " + sale + " — " + rootMessage(err));
    }

  private Long lambda_finishPurchase_18(Sale sale, Connection connection) { // было: lambda_finishPurchase_18
        return Long.valueOf(plugin.sales().insert(sale));
    }

  private void lambda_explainRefusal_17(CompletableFuture out, Lot fresh, Throwable err) { // было: lambda_explainRefusal_17
        plugin.sync(() -> lambda_explainRefusal_16(err, fresh, out));
    }

  private static void lambda_explainRefusal_16(Throwable err, Lot fresh, CompletableFuture out) { // было: lambda_explainRefusal_16
        if (err != null) {
            out.complete(Result.fail("buy.not-found", new Object[0]));
        } else if (fresh == null) {
            out.complete(Result.fail("buy.not-found", new Object[0]));
        } else if (fresh.cancelled()) {
            out.complete(Result.fail("buy.not-found", new Object[0]));
        } else if (fresh.remaining() != 0) {
            out.complete(Result.fail("buy.not-enough-stock", new Object[]{ "available", Integer.valueOf(fresh.remaining()) }));
        } else {
            out.complete(Result.fail("buy.sold-out", new Object[0]));
        }
    }

  private Lot lambda_explainRefusal_15(Lot snapshot, Connection connection) { // было: lambda_explainRefusal_15
        return plugin.lots().find(snapshot.id());
    }

  private void lambda_buy_14(CompletableFuture out, Lot snapshot, Player buyer, int units, long total, Boolean reserved, Throwable err) { // было: lambda_buy_14
        plugin.sync(() -> lambda_buy_13(err, out, reserved, snapshot, buyer, units, total));
    }

  private void lambda_buy_13(Throwable err, CompletableFuture out, Boolean reserved, Lot snapshot, Player buyer, int units, long total) { // было: lambda_buy_13
        if (err != null) {
            logError("reserve", err);
            out.complete(Result.fail("buy.failed", new Object[0]));
        } else if (reserved.booleanValue()) {
            finishPurchase(buyer, snapshot, units, total, out);
        } else {
            explainRefusal(snapshot, out);
        }
    }

  private Boolean lambda_buy_12(Lot snapshot, int units, Connection connection) { // было: lambda_buy_12
        return Boolean.valueOf(plugin.lots().reserve(snapshot.id(), units));
    }

  private static CompletionStage lambda_list_11(CompletableFuture completableFuture) { // было: lambda_list_11
        return completableFuture;
    }

  private CompletableFuture lambda_list_10(boolean bypassLimit, Settings settings, Player seller, ItemStack template, int amount, UUID id, long unitCents, Integer active, Throwable err) { // было: lambda_list_10
        CompletableFuture out = new CompletableFuture();
        plugin.sync(() -> lambda_list_9(err, out, bypassLimit, active, settings, seller, template, amount, id, unitCents));
        return out;
    }

  private void lambda_list_9(Throwable err, CompletableFuture out, boolean bypassLimit, Integer active, Settings settings, Player seller, ItemStack template, int amount, UUID id, long unitCents) { // было: lambda_list_9
        if (err != null) {
            logError("countActive", err);
            out.complete(Result.fail("sell.failed", new Object[0]));
            return;
        }
        if (!bypassLimit && active.intValue() >= settings.market().maxLotsPerPlayer()) {
            out.complete(Result.fail("sell.limit", new Object[]{ "limit", Integer.valueOf(settings.market().maxLotsPerPlayer()) }));
            return;
        }
        ItemStack now = seller.getInventory().getItemInMainHand();
        if (seller.isOnline() && !now.getType().isAir() && now.isSimilar(template) && now.getAmount() >= amount) {
            takeFromHand(seller, now, amount);
            Lot draft = new Lot(0L, id, seller.getName(), template.getType().name(), template, amount, 0, unitCents, System.currentTimeMillis(), settings.serverId(), false);
            String search = plugin.catalog().searchText(template, seller.getName());
            String nameKey = plugin.catalog().sortKey(template);
            plugin.database().async(lp0 -> lambda_list_6(draft, search, nameKey, lp0)).whenComplete((lp0, lp1) -> lambda_list_8(seller, template, amount, out, draft, (Long) lp0, (Throwable) lp1));
            return;
        }
        out.complete(Result.fail("sell.no-item", new Object[0]));
    }

  private void lambda_list_8(Player seller, ItemStack template, int amount, CompletableFuture out, Lot draft, Long newId, Throwable error) { // было: lambda_list_8
        plugin.sync(() -> lambda_list_7(error, seller, template, amount, out, draft, newId));
    }

  private void lambda_list_7(Throwable error, Player seller, ItemStack template, int amount, CompletableFuture out, Lot draft, Long newId) { // было: lambda_list_7
        if (error == null) {
            out.complete(Result.ok(draft.withId(newId.longValue())));
            return;
        }
        logError("insert lot", error);
        if (seller.isOnline()) {
            Inventories.giveOrDrop(seller, template, amount);
        } else {
            plugin.marketLog().critical("Лот не сохранён, игрок " + seller.getName() + " вышел: потеряно " + amount + "x " + template.getType());
        }
        out.complete(Result.fail("sell.failed", new Object[0]));
    }

  private Long lambda_list_6(Lot draft, String search, String nameKey, Connection connection) { // было: lambda_list_6
        return Long.valueOf(plugin.lots().insert(draft, search, nameKey));
    }

  private Integer lambda_list_5(UUID id, Connection connection) { // было: lambda_list_5
        return Integer.valueOf(plugin.lots().countActive(id));
    }

  private Integer lambda_activeCount_4(UUID seller, Connection connection) { // было: lambda_activeCount_4
        return Integer.valueOf(plugin.lots().countActive(seller));
    }

  private List lambda_similar_3(Lot lot, int limit, Connection connection) { // было: lambda_similar_3
        return plugin.lots().cheapest(lot.material(), lot.id(), limit);
    }

  private Lot lambda_find_2(long id, Connection connection) { // было: lambda_find_2
        return plugin.lots().find(id);
    }

  private Page lambda_myLots_1(UUID seller, int page, int size, Connection connection) { // было: lambda_myLots_1
        return plugin.lots().ofSeller(seller, page, size);
    }

  private Page lambda_browse_0(String query, SortMode sort, int page, int size, Connection connection) { // было: lambda_browse_0
        return plugin.lots().browse(query, sort, page, size);
    }


    // ---- вложенный класс: Cancelled ----
    public static record Cancelled(Lot lot, int units) {
    
      public Cancelled(Lot lot, int units) { // было: <init>
            super();
            this.lot = lot;
            this.units = units;
        }
    
    }
    
    // ---

    // ---- вложенный класс: Receipt ----
    public static record Receipt(Lot lot, int units, long totalCents) {
    
      public Receipt(Lot lot, int units, long totalCents) { // было: <init>
            super();
            this.lot = lot;
            this.units = units;
            this.totalCents = totalCents;
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
