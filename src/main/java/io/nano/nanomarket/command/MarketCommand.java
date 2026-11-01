// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.command.MarketCommand
package io.nano.nanomarket.command;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.ItemCatalog;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.config.Settings;
import io.nano.nanomarket.config.Settings.Market;
import io.nano.nanomarket.config.Settings.Security;
import io.nano.nanomarket.config.SoundBank;
import io.nano.nanomarket.economy.VaultEconomy;
import io.nano.nanomarket.gui.BrowseMenu;
import io.nano.nanomarket.gui.MyLotsMenu;
import io.nano.nanomarket.model.MarketStats;
import io.nano.nanomarket.model.Sale;
import io.nano.nanomarket.service.ListingRules;
import io.nano.nanomarket.service.ListingRules.Valid;
import io.nano.nanomarket.service.MarketService;
import io.nano.nanomarket.service.PayoutService;
import io.nano.nanomarket.service.PlayerSessions;
import io.nano.nanomarket.service.PlayerSessions.Session;
import io.nano.nanomarket.service.RateLimiter;
import io.nano.nanomarket.service.Result;
import io.nano.nanomarket.storage.Database;
import io.nano.nanomarket.storage.DatabaseException;
import io.nano.nanomarket.storage.LegacyImporter;
import io.nano.nanomarket.storage.SaleStore;
import io.nano.nanomarket.util.Times;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

public final class MarketCommand implements TabExecutor {

    // ---- поля ----
  private static final UUID CONSOLE;
  private static final List<String> PLAYER_SUBS;
  private static final List<String> ADMIN_SUBS;
  private final NanoMarket plugin;
  private final Map<UUID, Long> deleteRequests;

    static {
        CONSOLE = new UUID(0L, 0L);
        PLAYER_SUBS = List.of("sell", "search", "my", "history", "help");
        ADMIN_SUBS = List.of("reload", "stats", "payout", "clearcache", "import", "dbdelete", "version", "help");
    }

  public MarketCommand(NanoMarket plugin) { // было: <init>
        super();
        deleteRequests = new ConcurrentHashMap();
        this.plugin = plugin;
    }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String temp1;
        if (!sender.hasPermission("nanomarket.use")) {
            plugin.messages().send(sender, "no-permission", new Object[0]);
            return true;
        }
        int n;
        Player player;
        String sub;
        if (sender instanceof Player sub) {
            if (!plugin.limiter().allowCommand(sub.getUniqueId(), plugin.settings().security().maxCommandsPerMinute())) {
                plugin.messages().send(sender, "rate-limited", new Object[0]);
                return true;
            }
            sub = temp1;
            if (sub.equals("admin")) {
                admin(sender, args);
                return true;
            }
            if (sub.equals("админ")) {
                admin(sender, args);
                return true;
            }
            if (sub.equals("help")) {
                help(sender);
                return true;
            }
            if (sub.equals("помощь")) {
                help(sender);
                return true;
            }
            if (sender instanceof Player player) {
            } else {
                plugin.messages().send(sender, "players-only", new Object[0]);
                return true;
            }
            if (plugin.ready()) {
                player = sub;
                n = -1;
                switch (player) {
                    case "":
                        BrowseMenu.show(plugin, player);
                        break;
                    case "sell":
                    case "продать":
                        sell(player, args);
                        break;
                    case "search":
                    case "поиск":
                        search(player, args);
                        break;
                    case "my":
                    case "мои":
                        MyLotsMenu.show(plugin, player);
                        break;
                    case "history":
                    case "история":
                        history(player);
                        break;
                    default:
                        plugin.messages().send(player, "unknown-command", new Object[0]);
                }
            } else {
                plugin.messages().send(player, "market-offline", new Object[0]);
                return true;
            }
            return true;
        }
        String sub = args.length != 0 ? args[0].toLowerCase(Locale.ROOT) : "";
        if (sub.equals("admin")) {
            admin(sender, args);
            return true;
        }
        if (sub.equals("админ")) {
            admin(sender, args);
            return true;
        }
        if (sub.equals("help")) {
            help(sender);
            return true;
        }
        if (sub.equals("помощь")) {
            help(sender);
            return true;
        }
        if (sender instanceof Player player) {
        } else {
            plugin.messages().send(sender, "players-only", new Object[0]);
            return true;
        }
        if (plugin.ready()) {
            player = sub;
            n = -1;
            switch (player) {
                case "":
                    BrowseMenu.show(plugin, player);
                    break;
                case "sell":
                case "продать":
                    sell(player, args);
                    break;
                case "search":
                case "поиск":
                    search(player, args);
                    break;
                case "my":
                case "мои":
                    MyLotsMenu.show(plugin, player);
                    break;
                case "history":
                case "история":
                    history(player);
                    break;
                default:
                    plugin.messages().send(player, "unknown-command", new Object[0]);
            }
        } else {
            plugin.messages().send(player, "market-offline", new Object[0]);
            return true;
        }
        return true;
    }

  private void sell(Player player, String[] args) {
        String temp5;
        String temp6;
        ItemStack temp7;
        boolean temp8;
        Market temp9;
        if (!player.hasPermission("nanomarket.sell")) {
            plugin.messages().send(player, "no-permission", new Object[0]);
            return;
        }
        if (args.length >= 2) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (args.length <= 2) {
                temp5 = null;
                temp6 = args[1];
                temp7 = hand;
                temp8 = player.hasPermission("nanomarket.bypass.blacklist");
                temp9 = plugin.settings().market();
            } else {
                temp5 = args[2];
                temp6 = args[1];
                temp7 = hand;
                temp8 = player.hasPermission("nanomarket.bypass.blacklist");
                temp9 = plugin.settings().market();
            }
            Result check = ListingRules.check(temp9, temp8, temp7, temp6, temp5);
            if (check.ok()) {
                Valid valid = (Valid) check.value();
                plugin.market().list(player, hand, valid.unitCents(), valid.amount(), player.hasPermission("nanomarket.bypass.limit")).whenComplete((lp0, lp1) -> lambda_sell_0(player, valid, (Result) lp0, (Throwable) lp1));
                return;
            } else {
                plugin.messages().send(player, check.errorKey(), check.errorArgs());
                plugin.sounds().play(player, "error");
                return;
            }
        }
        plugin.messages().sendLines(player, "sell.usage", new Object[0]);
    }

  private void search(Player player, String[] args) {
        String temp3;
        Session temp4;
        Session session = plugin.sessions().of(player.getUniqueId());
        session.page = 1;
        if (args.length >= 2) {
            String query = String.join(" ", (CharSequence[]) Arrays.copyOfRange(args, 1, args.length)).trim();
            if (query.length() <= 48) {
                temp3 = query;
                temp4 = session;
            } else {
                temp3 = query.substring(0, 48);
                temp4 = session;
            }
            temp4.query = temp3;
            plugin.messages().send(player, "search.set", new Object[]{ "query", session.query });
        } else {
            session.query = "";
            plugin.messages().send(player, "search.cleared", new Object[0]);
        }
        BrowseMenu.show(plugin, player);
    }

  private void history(Player player) {
        UUID id = player.getUniqueId();
        plugin.database().async(lp0 -> lambda_history_1(id, lp0)).whenComplete((lp0, lp1) -> lambda_history_4(player, id, (List) lp0, (Throwable) lp1));
    }

  private void help(CommandSender sender) {
        sender.sendMessage(plugin.messages().text("help.header", new Object[0]));
        plugin.messages().sendLines(sender, "help.lines", new Object[0]);
        if (sender.hasPermission("nanomarket.admin")) {
            plugin.messages().sendLines(sender, "help.admin", new Object[0]);
        }
    }

  private void admin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nanomarket.admin")) {
            plugin.messages().send(sender, "no-permission", new Object[0]);
            return;
        }
        String sub = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "help";
        switch (sub) {
            case "reload":
                plugin.reloadAll();
                plugin.messages().send(sender, "admin.reloaded", new Object[0]);
                break;
            case "clearcache":
                plugin.sessions().clear();
                plugin.messages().send(sender, "admin.cache-cleared", new Object[0]);
                break;
            case "payout":
                if (requireReady(sender)) {
                    plugin.payouts().payNow();
                    plugin.messages().send(sender, "admin.payout-started", new Object[0]);
                    break;
                } else {
                    return;
                }
            case "stats":
                if (!requireReady(sender)) {
                    break;
                }
                stats(sender);
                break;
            case "import":
                if (!requireReady(sender)) {
                    break;
                }
                importLegacy(sender, args);
                break;
            case "dbdelete":
            case "purge":
                if (!requireReady(sender)) {
                    break;
                }
                dbdelete(sender, args);
                break;
            case "version":
                version(sender);
                break;
            default:
                sender.sendMessage(plugin.messages().text("help.header", new Object[0]));
                plugin.messages().sendLines(sender, "help.admin", new Object[0]);
        }
    }

  private boolean requireReady(CommandSender sender) {
        if (plugin.ready()) {
            return true;
        }
        plugin.messages().send(sender, "market-offline", new Object[0]);
        return false;
    }

  private void stats(CommandSender sender) {
        long start = Times.startOfTodayMillis();
        plugin.database().async(lp0 -> lambda_stats_5(start, lp0)).whenComplete((lp0, lp1) -> lambda_stats_7(sender, (MarketStats) lp0, (Throwable) lp1));
    }

  private void version(CommandSender sender) {
        String temp2;
        Object __obj3;
        Object __obj4;
        Object temp14;
        Object temp15;
        Object temp16;
        Object[] __obj1 = new Object[8];
        __obj1[0] = "version";
        __obj1[1] = plugin.getDescription().getVersion();
        __obj1[2] = "database";
        if (plugin.ready()) {
            temp2 = plugin.database().describe();
        } else {
            String temp8 = "нет соединения";
            temp2 = temp8;
            3 = 3;
            __obj1 = __obj1;
            temp8 = temp8;
        }
        __obj3[temp2] = temp2;
        __obj4[4] = "economy";
        String temp17 = plugin.economy().available() ? plugin.vault().name() : "нет";
        Object temp20 = __obj4;
        __obj4[5] = temp17;
        temp20[6] = "server";
        temp20[7] = plugin.settings().serverId();
        temp16.send((CommandSender) temp15, (String) temp14, (Object[]) temp20);
        plugin.messages().send(sender, "admin.channel", new Object[0]);
    }

  private void importLegacy(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            String raw = String.join(" ", (CharSequence[]) Arrays.copyOfRange(args, 2, args.length));
            File file = new File(raw);
            if (!file.isFile()) {
                file = new File(plugin.getDataFolder(), raw);
            }
            if (!file.isFile()) {
                file = new File(plugin.getDataFolder().getParentFile(), raw);
            }
            File source = file;
            plugin.database().submit(() -> lambda_importLegacy_8(source)).whenComplete((lp0, lp1) -> lambda_importLegacy_10(sender, (io.nano.nanomarket.storage.LegacyImporter.Result) lp0, (Throwable) lp1));
            return;
        }
        plugin.messages().send(sender, "admin.import.usage", new Object[0]);
    }

  private void dbdelete(CommandSender sender, String[] args) {
        UUID temp2;
        String temp4;
        temp2 = sender instanceof Player seconds ? seconds.getUniqueId() : CONSOLE;
        UUID who = temp2;
        int seconds = plugin.settings().security().dbdeleteConfirmSeconds();
        boolean confirm = args.length > 2 && args[2].equalsIgnoreCase("confirm");
        if (confirm != 0) {
            Long requested = (Long) deleteRequests.remove(who);
            if (requested != null && System.currentTimeMillis() - requested.longValue() <= (long) seconds * 1000L) {
                temp4 = sender instanceof Player var8 ? var8.getName() : "консоль";
                String by = temp4;
                plugin.database().async(int[]::new).whenComplete((lp0, lp1) -> lambda_dbdelete_13(sender, by, (int[]) lp0, (Throwable) lp1));
                return;
            }
            plugin.messages().send(sender, "admin.dbdelete.no-request", new Object[0]);
            return;
        }
        deleteRequests.put(who, Long.valueOf(System.currentTimeMillis()));
        plugin.messages().sendLines(sender, "admin.dbdelete.warning", new Object[]{ "seconds", Integer.valueOf(seconds) });
    }

  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList out = new ArrayList();
        if (!sender.hasPermission("nanomarket.use")) {
            return out;
        }
        if (args.length == 1) {
            ArrayList subs = new ArrayList(PLAYER_SUBS);
            if (sender.hasPermission("nanomarket.admin")) {
                subs.add("admin");
            }
            StringUtil.copyPartialMatches(args[0], subs, out);
        } else if (args.length == 2) {
            String subs = args[0].toLowerCase(Locale.ROOT);
            switch (subs) {
                case "sell":
                case "продать":
                    if (sender instanceof Player) {
                        Player hand = (Player) sender;
                        if (!hand.getInventory().getItemInMainHand().getType().isAir()) {
                            StringUtil.copyPartialMatches(args[1], List.of("10", "100", "1k"), out);
                        }
                    }
                    break;
                case "search":
                case "поиск":
                    out.addAll(plugin.catalog().suggest(args[1], 20));
                    break;
                case "admin":
                case "админ":
                    if (!sender.hasPermission("nanomarket.admin")) {
                        break;
                    }
                    StringUtil.copyPartialMatches(args[1], ADMIN_SUBS, out);
                    break;
                default:
            }
        } else if (args.length == 3) {
            String subs = args[0].toLowerCase(Locale.ROOT);
            if (subs.equals("sell")) {
                if (sender instanceof Player var7) {
                    ItemStack hand = n.getInventory().getItemInMainHand();
                    if (!hand.getType().isAir()) {
                        StringUtil.copyPartialMatches(args[2], List.of(Integer.toString(hand.getAmount())), out);
                    }
                } else if (subs.equals("admin")) {
                    if (args[1].equalsIgnoreCase("dbdelete") && sender.hasPermission("nanomarket.admin")) {
                        StringUtil.copyPartialMatches(args[2], List.of("confirm"), out);
                    }
                } else if (subs.equals("админ") && args[1].equalsIgnoreCase("dbdelete") && sender.hasPermission("nanomarket.admin")) {
                    StringUtil.copyPartialMatches(args[2], List.of("confirm"), out);
                }
            } else if (subs.equals("продать")) {
                if (sender instanceof Player var7) {
                    ItemStack hand = n.getInventory().getItemInMainHand();
                    if (!hand.getType().isAir()) {
                        StringUtil.copyPartialMatches(args[2], List.of(Integer.toString(hand.getAmount())), out);
                    }
                } else if (subs.equals("admin")) {
                    if (args[1].equalsIgnoreCase("dbdelete") && sender.hasPermission("nanomarket.admin")) {
                        StringUtil.copyPartialMatches(args[2], List.of("confirm"), out);
                    }
                } else if (subs.equals("админ") && args[1].equalsIgnoreCase("dbdelete") && sender.hasPermission("nanomarket.admin")) {
                    StringUtil.copyPartialMatches(args[2], List.of("confirm"), out);
                }
            } else if (subs.equals("admin")) {
                if (args[1].equalsIgnoreCase("dbdelete") && sender.hasPermission("nanomarket.admin")) {
                    StringUtil.copyPartialMatches(args[2], List.of("confirm"), out);
                }
            } else if (subs.equals("админ") && args[1].equalsIgnoreCase("dbdelete") && sender.hasPermission("nanomarket.admin")) {
                StringUtil.copyPartialMatches(args[2], List.of("confirm"), out);
            }
        }
        out.sort(String.CASE_INSENSITIVE_ORDER);
        return out;
    }

  private void lambda_dbdelete_13(CommandSender sender, String by, int[] counts, Throwable err) { // было: lambda_dbdelete_13
        plugin.sync(() -> lambda_dbdelete_12(err, sender, counts, by));
    }

  private void lambda_dbdelete_12(Throwable err, CommandSender sender, int[] counts, String by) { // было: lambda_dbdelete_12
        Iterator iterator;
        if (err == null) {
            plugin.messages().send(sender, "admin.dbdelete.done", new Object[]{ "lots", Integer.valueOf(counts[0]), "sales", Integer.valueOf(counts[1]) });
            plugin.getLogger().warning("Маркет очищен (" + by + "): лотов " + counts[0] + ", продаж " + counts[1]);
            plugin.sessions().clear();
            iterator = plugin.getServer().getOnlinePlayers().iterator();
        } else {
            plugin.messages().send(sender, "error-internal", new Object[0]);
            return;
        }
        while (iterator.hasNext()) {
            Player online = (Player) iterator.next();
            if (online.hasPermission("nanomarket.admin") && !online.equals(sender)) {
                plugin.messages().send(online, "admin.dbdelete.alert", new Object[]{ "who", by });
            }
        }
    }

  private int[] lambda_dbdelete_11(Connection connection) { // было: lambda_dbdelete_11
        return new int[]{ plugin.lots().purgeAll(), plugin.sales().purgeAll() };
    }

  private void lambda_importLegacy_10(CommandSender sender, io.nano.nanomarket.storage.LegacyImporter.Result result, Throwable err) { // было: lambda_importLegacy_10
        plugin.sync(() -> lambda_importLegacy_9(err, sender, result));
    }

  private void lambda_importLegacy_9(Throwable err, CommandSender sender, io.nano.nanomarket.storage.LegacyImporter.Result result) { // было: lambda_importLegacy_9
        if (err == null) {
            plugin.messages().send(sender, "admin.import.done", new Object[]{ "imported", Integer.valueOf(result.imported()), "skipped", Integer.valueOf(result.skipped()) });
            return;
        }
        Throwable root = err;
        for (; root.getCause() != null; root = root.getCause()) {
            if (root.getCause() == root) {
                break;
            }
        }
        plugin.messages().send(sender, "admin.import.failed", new Object[]{ "error", String.valueOf(root.getMessage()) });
    }

  private io.nano.nanomarket.storage.LegacyImporter.Result lambda_importLegacy_8(File source) { // было: lambda_importLegacy_8
        Result temp1;
        try {
            temp1 = LegacyImporter.importFrom(source, plugin.lots(), plugin.itemCodec(), plugin.catalog(), plugin.settings().serverId(), plugin.getLogger());
        } catch (SQLException e1) {
            throw new DatabaseException(e1.getMessage(), e1);
        }
        return temp1;
    }

  private void lambda_stats_7(CommandSender sender, MarketStats marketStats, Throwable err) { // было: lambda_stats_7
        plugin.sync(() -> lambda_stats_6(err, sender, marketStats));
    }

  private void lambda_stats_6(Throwable err, CommandSender sender, MarketStats marketStats) { // было: lambda_stats_6
        if (err == null) {
            plugin.messages().sendLines(sender, "admin.stats", new Object[]{ "lots", Integer.valueOf(marketStats.activeLots()), "units", Long.valueOf(marketStats.activeUnits()), "sellers", Integer.valueOf(marketStats.sellers()), "sales", Integer.valueOf(marketStats.salesTotal()), "sold-units", Long.valueOf(marketStats.unitsSoldTotal()), "turnover", Money.format(marketStats.turnoverCents()), "turnover-today", Money.format(marketStats.turnoverTodayCents()), "sales-today", Integer.valueOf(marketStats.salesToday()), "unpaid", Integer.valueOf(marketStats.unpaidSales()) });
            return;
        }
        plugin.messages().send(sender, "error-internal", new Object[0]);
    }

  private MarketStats lambda_stats_5(long start, Connection connection) { // было: lambda_stats_5
        return plugin.sales().stats(start);
    }

  private void lambda_history_4(Player player, UUID id, List sales, Throwable err) { // было: lambda_history_4
        plugin.sync(() -> lambda_history_3(err, player, sales, id));
    }

  private void lambda_history_3(Throwable err, Player player, List sales, UUID id) { // было: lambda_history_3
        if (err == null) {
            player.sendMessage(plugin.messages().text("history.header", new Object[0]));
            if (sales.isEmpty()) {
                player.sendMessage(plugin.messages().text("history.empty", new Object[0]));
                return;
            } else {
                sales.stream().sorted(Comparator.comparingLong(Sale::createdAt).reversed()).limit(10L).forEach(lp0 -> lambda_history_2(id, player, (Sale) lp0));
                return;
            }
        }
        plugin.messages().send(player, "error-internal", new Object[0]);
    }

  private void lambda_history_2(UUID id, Player player, Sale sale) { // было: lambda_history_2
        boolean sold = sale.sellerId().equals(id);
        Messages temp5 = plugin.messages();
        player.sendMessage(temp5.text(sold ? "history.sold" : "history.bought", new Object[]{ "time", Times.stamp(sale.createdAt()), "amount", Integer.valueOf(sale.amount()), "item", plugin.catalog().name(sale.material()), "buyer", sale.buyerName(), "seller", sale.sellerName(), "total", Money.format(sale.totalCents()), "status", plugin.messages().text(sale.paid() ? "history.paid" : "history.unpaid", new Object[0]) }));
    }

  private List lambda_history_1(UUID id, Connection connection) { // было: lambda_history_1
        ArrayList all = new ArrayList(plugin.sales().recentSold(id, 10));
        all.addAll(plugin.sales().recentBought(id, 10));
        return all;
    }

  private void lambda_sell_0(Player player, Valid valid, Result result, Throwable err) { // было: lambda_sell_0
        if (err != null) {
            plugin.getLogger().warning("Не удалось выставить лот: " + err);
            plugin.messages().send(player, "sell.failed", new Object[0]);
            return;
        }
        if (result.ok()) {
            plugin.messages().send(player, "sell.success", new Object[]{ "amount", Integer.valueOf(valid.amount()), "item", plugin.catalog().displayName(((Lot) result.value()).template()), "unit", Money.format(valid.unitCents()), "total", Money.format(valid.unitCents() * (long) valid.amount()) });
            plugin.sounds().play(player, "sell");
            return;
        }
        plugin.messages().send(player, result.errorKey(), result.errorArgs());
        plugin.sounds().play(player, "error");
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
