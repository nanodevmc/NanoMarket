// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.config.Settings
package io.nano.nanomarket.config;

import io.nano.nanomarket.config.Settings.Db;
import io.nano.nanomarket.config.Settings.DbType;
import io.nano.nanomarket.config.Settings.Gui;
import io.nano.nanomarket.config.Settings.Logging;
import io.nano.nanomarket.config.Settings.Market;
import io.nano.nanomarket.config.Settings.Payout;
import io.nano.nanomarket.config.Settings.PayoutMode;
import io.nano.nanomarket.config.Settings.Purchase;
import io.nano.nanomarket.config.Settings.Security;
import io.nano.nanomarket.config.Settings.Style;
import io.nano.nanomarket.config.Settings.Update;
import io.nano.nanomarket.model.SortMode;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.bukkit.configuration.ConfigurationSection;

public record Settings(String serverId, Style style, Db db, Market market, Purchase purchase, Payout payout, Gui gui, Security security, Logging logging, Update update) {

    // ---- поля ----
  private static final Pattern PREFIX;

    static {
        PREFIX = Pattern.compile("^[A-Za-z0-9_]{0,24}$");
    }

  public Settings(String serverId, Style style, Db db, Market market, Purchase purchase, Payout payout, Gui gui, Security security, Logging logging, Update update) { // было: <init>
        super();
        this.serverId = serverId;
        this.style = style;
        this.db = db;
        this.market = market;
        this.purchase = purchase;
        this.payout = payout;
        this.gui = gui;
        this.security = security;
        this.logging = logging;
        this.update = update;
    }

  public static Settings from(ConfigurationSection configurationSection) {
        String serverId = configurationSection.getString("server-id", "server1");
        if (serverId == null) {
            serverId = "server1";
        } else if (serverId.isBlank()) {
            serverId = "server1";
        }
        serverId = serverId.trim();
        if (serverId.length() > 64) {
            serverId = serverId.substring(0, 64);
        }
        Style style = new Style(configurationSection.getBoolean("style.small-caps", true), configurationSection.getBoolean("style.gradient", true), configurationSection.getString("style.currency-symbol", "◈"));
        String prefix = configurationSection.getString("database.table-prefix", "nm_");
        if (prefix == null) {
            prefix = "nm_";
        } else if (!PREFIX.matcher(prefix).matches()) {
            prefix = "nm_";
        }
        Db db = new Db((DbType) parseEnum(DbType.class, configurationSection.getString("database.type", "SQLITE"), DbType.SQLITE), prefix, configurationSection.getString("database.sqlite.file", "market.db"), configurationSection.getString("database.mysql.host", "localhost"), clamp(configurationSection.getInt("database.mysql.port", 3306), 1, 65535), configurationSection.getString("database.mysql.name", "nanomarket"), configurationSection.getString("database.mysql.user", "root"), configurationSection.getString("database.mysql.password", ""), configurationSection.getBoolean("database.mysql.use-ssl", false));
        long min = cents(configurationSection.getDouble("market.min-price", 1.0), 1L);
        long max = Math.max(min, cents(configurationSection.getDouble("market.max-price", 1000000.0), 100000000L));
        List blacklist = configurationSection.getStringList("market.blacklist").stream().map(lp0 -> lambda_from_0((String) lp0)).filter(lp0 -> lambda_from_1((String) lp0)).toList();
        Market market = new Market(clamp(configurationSection.getInt("market.max-lots-per-player", 45), 1, 10000), min, max, clamp(configurationSection.getInt("market.max-amount-per-lot", 2304), 1, 1000000), SortMode.parse(configurationSection.getString("market.default-sort", "NEWEST"), SortMode.NEWEST), configurationSection.getBoolean("market.allow-custom-items", true), configurationSection.getBoolean("market.reject-obfuscated-text", true), blacklist);
        Purchase purchase = new Purchase(configurationSection.getBoolean("purchase.check-inventory-space", true), configurationSection.getBoolean("purchase.drop-if-no-space", false), clamp(configurationSection.getInt("purchase.max-per-purchase", 2304), 1, 1000000), Math.max(0L, configurationSection.getLong("purchase.cooldown-ms", 1500L)));
        Payout payout = new Payout((PayoutMode) parseEnum(PayoutMode.class, configurationSection.getString("payout.mode", "BATCH"), PayoutMode.BATCH), clamp(configurationSection.getInt("payout.interval-seconds", 60), 5, 86400), configurationSection.getBoolean("payout.announce", true), clamp(configurationSection.getInt("payout.announce-seconds", 5), 0, 300));
        Gui gui = new Gui(clamp(configurationSection.getInt("gui.page-size", 36), 1, 36), configurationSection.getBoolean("gui.purchase-animation", true), clamp(configurationSection.getInt("gui.animation-step-ticks", 6), 1, 40), clamp(configurationSection.getInt("gui.similar-offers", 5), 0, 5));
        Security security = new Security(Math.max(0L, configurationSection.getLong("security.click-cooldown-ms", 250L)), clamp(configurationSection.getInt("security.max-commands-per-minute", 30), 1, 100000), clamp(configurationSection.getInt("security.dbdelete-confirm-seconds", 30), 5, 600));
        Logging logging = new Logging(configurationSection.getBoolean("logging.console", true), configurationSection.getBoolean("logging.file", true), configurationSection.getString("logging.format", "[{time}] {buyer} купил {amount}x {item} у {seller} за {total}"));
        Update update = new Update(configurationSection.getBoolean("update-checker.enabled", false), configurationSection.getString("update-checker.version-url", ""), configurationSection.getString("update-checker.download-url", ""));
        return new Settings(serverId, style, db, market, purchase, payout, gui, security, logging, update);
    }

  private static long cents(double value, long fallback) {
        return !Double.isNaN(value) && !Double.isInfinite(value) && value >= 0.0 ? Math.round(value * 100.0) : fallback;
    }

  private static int clamp(int n1, int n2, int n3) {
        return Math.max(n2, Math.min(n3, n1));
    }

  private static <E extends Enum<E>> E parseEnum(Class<E> type, String raw, E fallback) {
        Enum temp1;
        if (raw == null) {
            return fallback;
        }
        try {
            temp1 = Enum.valueOf(type, raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e1) {
            return fallback;
        }
        return temp1;
    }

  private static boolean lambda_from_1(String s) { // было: lambda_from_1
        return !s.isEmpty();
    }

  private static String lambda_from_0(String s) { // было: lambda_from_0
        return s.trim().toUpperCase(Locale.ROOT);
    }


    // ---- вложенный класс: Db ----
    public static record Db(DbType type, String tablePrefix, String sqliteFile, String host, int port, String name, String user, String password, boolean useSsl) {
    
      public Db(DbType type, String tablePrefix, String sqliteFile, String host, int port, String name, String user, String password, boolean useSsl) { // было: <init>
            super();
            this.type = type;
            this.tablePrefix = tablePrefix;
            this.sqliteFile = sqliteFile;
            this.host = host;
            this.port = port;
            this.name = name;
            this.user = user;
            this.password = password;
            this.useSsl = useSsl;
        }
    
    }
    
    // ---

    // ---- вложенный класс: DbType ----
    public enum DbType {
    
        SQLITE,
        MYSQL;
    
        static {
            _VALUES = _values();
        }
    
      private DbType() { // было: <init>
            // (пустое тело)
        }
    
    }
    
    // ---

    // ---- вложенный класс: Gui ----
    public static record Gui(int pageSize, boolean animation, int animationStepTicks, int similarOffers) {
    
      public Gui(int pageSize, boolean animation, int animationStepTicks, int similarOffers) { // было: <init>
            super();
            this.pageSize = pageSize;
            this.animation = animation;
            this.animationStepTicks = animationStepTicks;
            this.similarOffers = similarOffers;
        }
    
    }
    
    // ---

    // ---- вложенный класс: Logging ----
    public static record Logging(boolean console, boolean file, String format) {
    
      public Logging(boolean console, boolean file, String format) { // было: <init>
            super();
            this.console = console;
            this.file = file;
            this.format = format;
        }
    
    }
    
    // ---

    // ---- вложенный класс: Market ----
    public static record Market(int maxLotsPerPlayer, long minPriceCents, long maxPriceCents, int maxAmountPerLot, SortMode defaultSort, boolean allowCustomItems, boolean rejectObfuscated, List blacklist) {
    
      public Market(int maxLotsPerPlayer, long minPriceCents, long maxPriceCents, int maxAmountPerLot, SortMode defaultSort, boolean allowCustomItems, boolean rejectObfuscated, List<String> blacklist) { // было: <init>
            super();
            this.maxLotsPerPlayer = maxLotsPerPlayer;
            this.minPriceCents = minPriceCents;
            this.maxPriceCents = maxPriceCents;
            this.maxAmountPerLot = maxAmountPerLot;
            this.defaultSort = defaultSort;
            this.allowCustomItems = allowCustomItems;
            this.rejectObfuscated = rejectObfuscated;
            this.blacklist = blacklist;
        }
    
    }
    
    // ---

    // ---- вложенный класс: Payout ----
    public static record Payout(PayoutMode mode, int intervalSeconds, boolean announce, int announceSeconds) {
    
      public Payout(PayoutMode mode, int intervalSeconds, boolean announce, int announceSeconds) { // было: <init>
            super();
            this.mode = mode;
            this.intervalSeconds = intervalSeconds;
            this.announce = announce;
            this.announceSeconds = announceSeconds;
        }
    
    }
    
    // ---

    // ---- вложенный класс: PayoutMode ----
    public enum PayoutMode {
    
        INSTANT,
        BATCH;
    
        static {
            _VALUES = _values();
        }
    
      private PayoutMode() { // было: <init>
            // (пустое тело)
        }
    
    }
    
    // ---

    // ---- вложенный класс: Purchase ----
    public static record Purchase(boolean checkSpace, boolean dropIfNoSpace, int maxPerPurchase, long cooldownMs) {
    
      public Purchase(boolean checkSpace, boolean dropIfNoSpace, int maxPerPurchase, long cooldownMs) { // было: <init>
            super();
            this.checkSpace = checkSpace;
            this.dropIfNoSpace = dropIfNoSpace;
            this.maxPerPurchase = maxPerPurchase;
            this.cooldownMs = cooldownMs;
        }
    
    }
    
    // ---

    // ---- вложенный класс: Security ----
    public static record Security(long clickCooldownMs, int maxCommandsPerMinute, int dbdeleteConfirmSeconds) {
    
      public Security(long clickCooldownMs, int maxCommandsPerMinute, int dbdeleteConfirmSeconds) { // было: <init>
            super();
            this.clickCooldownMs = clickCooldownMs;
            this.maxCommandsPerMinute = maxCommandsPerMinute;
            this.dbdeleteConfirmSeconds = dbdeleteConfirmSeconds;
        }
    
    }
    
    // ---

    // ---- вложенный класс: Style ----
    public static record Style(boolean smallCaps, boolean gradient, String currencySymbol) {
    
      public Style(boolean smallCaps, boolean gradient, String currencySymbol) { // было: <init>
            super();
            this.smallCaps = smallCaps;
            this.gradient = gradient;
            this.currencySymbol = currencySymbol;
        }
    
    }
    
    // ---

    // ---- вложенный класс: Update ----
    public static record Update(boolean enabled, String versionUrl, String downloadUrl) {
    
      public Update(boolean enabled, String versionUrl, String downloadUrl) { // было: <init>
            super();
            this.enabled = enabled;
            this.versionUrl = versionUrl;
            this.downloadUrl = downloadUrl;
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
