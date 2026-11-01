// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.NanoMarket
package io.nano.nanomarket;

import io.nano.nanomarket.command.MarketCommand;
import io.nano.nanomarket.config.ItemCatalog;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.config.Settings;
import io.nano.nanomarket.config.Settings.Market;
import io.nano.nanomarket.config.Settings.Style;
import io.nano.nanomarket.config.SoundBank;
import io.nano.nanomarket.economy.VaultEconomy;
import io.nano.nanomarket.gui.Menu;
import io.nano.nanomarket.gui.MenuListener;
import io.nano.nanomarket.listener.PlayerListener;
import io.nano.nanomarket.listener.SearchListener;
import io.nano.nanomarket.service.MarketLog;
import io.nano.nanomarket.service.MarketService;
import io.nano.nanomarket.service.PayoutService;
import io.nano.nanomarket.service.PlayerSessions;
import io.nano.nanomarket.service.RateLimiter;
import io.nano.nanomarket.service.SearchPrompts;
import io.nano.nanomarket.storage.BukkitItemCodec;
import io.nano.nanomarket.storage.Database;
import io.nano.nanomarket.storage.ItemCodec;
import io.nano.nanomarket.storage.LotStore;
import io.nano.nanomarket.storage.SaleStore;
import io.nano.nanomarket.storage.Schema;
import io.nano.nanomarket.text.Banner;
import io.nano.nanomarket.text.Banner.Info;
import io.nano.nanomarket.text.Styler;
import io.nano.nanomarket.update.UpdateChecker;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class NanoMarket extends JavaPlugin {

    // ---- поля ----
  private volatile Settings settings;
  private volatile Styler styler;
  private Messages messages;
  private ItemCatalog catalog;
  private SoundBank sounds;
  private final ItemCodec itemCodec;
  private final PlayerSessions sessions;
  private final RateLimiter limiter;
  private Database database;
  private LotStore lots;
  private SaleStore sales;
  private VaultEconomy vault;
  private MarketService market;
  private PayoutService payouts;
  private MarketLog marketLog;
  private SearchPrompts prompts;
  private UpdateChecker updater;
  private volatile boolean ready;

  public NanoMarket() { // было: <init>
        super();
        itemCodec = new BukkitItemCodec();
        sessions = new PlayerSessions();
        limiter = new RateLimiter();
    }

  public void onEnable() {
        saveDefaultConfig();
        String[] command = new String[]{ "messages.yml", "items.yml", "sounds.yml" };
        int handler = command.length;
        for (String name : command) {
            if (!new File(getDataFolder(), name).exists()) {
                saveResource(name, false);
            }
        }
        messages = new Messages(new File(getDataFolder(), "messages.yml"), () -> lambda_onEnable_0(), () -> lambda_onEnable_1(), getLogger());
        catalog = new ItemCatalog(new File(getDataFolder(), "items.yml"), () -> lambda_onEnable_2(), getLogger());
        sounds = new SoundBank(new File(getDataFolder(), "sounds.yml"), getLogger());
        reloadAll();
        vault = new VaultEconomy(getLogger());
        marketLog = new MarketLog(this);
        prompts = new SearchPrompts(this);
        market = new MarketService(this);
        payouts = new PayoutService(this);
        updater = new UpdateChecker(this);
        connectDatabase();
        Bukkit.getPluginManager().registerEvents(new MenuListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SearchListener(this), this);
        command = getCommand("market");
        if (command != null) {
            handler = new MarketCommand(this);
            command.setExecutor(handler);
            command.setTabCompleter(handler);
        }
        payouts.start();
        Objects.requireNonNull(updater);
        Bukkit.getScheduler().runTaskLater(this, updater::check, 100L);
        banner();
    }

  public void onDisable() {
        if (payouts != null) {
            payouts.stop();
        }
        Iterator iterator = Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            Player player = (Player) iterator.next();
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu) {
                player.closeInventory();
            }
        }
        if (database != null) {
            database.close();
        }
        ready = false;
    }

  public void reloadAll() {
        reloadConfig();
        settings = Settings.from(getConfig());
        Style style = settings.style();
        styler = new Styler(style.smallCaps(), style.gradient(), style.currencySymbol(), getDescription().getVersion());
        messages.reload();
        catalog.reload();
        sounds.reload();
        sessions.defaultSort(settings.market().defaultSort());
        if (payouts != null && isEnabled()) {
            payouts.start();
        }
    }

  private void connectDatabase() {
        try {
            database = new Database(settings.db(), getDataFolder(), getLogger());
            database.connect();
            Schema.create(database);
            lots = new LotStore(database, itemCodec);
            sales = new SaleStore(database);
            ready = true;
        } catch (Exception e1) {
            ready = false;
            getLogger().log(Level.SEVERE, "Не удалось подключиться к базе данных: " + e1.getMessage() + ". Маркет отключён — проверьте раздел database в config.yml.", e1);
        }
    }

  private void banner() {
        Info temp9 = new Info();
        Info info = temp9;
        Iterator iterator = Banner.lines(info).iterator();
        while (iterator.hasNext()) {
            String line = (String) iterator.next();
            Bukkit.getConsoleSender().sendMessage(line);
        }
    }

  public void sync(Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
            return;
        }
        try {
            Bukkit.getScheduler().runTask(this, task);
        } catch (IllegalPluginAccessException e1) {
        }
    }

  public boolean ready() {
        return ready;
    }

  public Settings settings() {
        return settings;
    }

  public Styler styler() {
        return styler;
    }

  public Messages messages() {
        return messages;
    }

  public ItemCatalog catalog() {
        return catalog;
    }

  public SoundBank sounds() {
        return sounds;
    }

  public ItemCodec itemCodec() {
        return itemCodec;
    }

  public PlayerSessions sessions() {
        return sessions;
    }

  public RateLimiter limiter() {
        return limiter;
    }

  public Database database() {
        return database;
    }

  public LotStore lots() {
        return lots;
    }

  public SaleStore sales() {
        return sales;
    }

  public VaultEconomy economy() {
        return vault;
    }

  public VaultEconomy vault() {
        return vault;
    }

  public MarketService market() {
        return market;
    }

  public PayoutService payouts() {
        return payouts;
    }

  public MarketLog marketLog() {
        return marketLog;
    }

  public SearchPrompts prompts() {
        return prompts;
    }

  public UpdateChecker updater() {
        return updater;
    }

  private InputStream lambda_onEnable_2() { // было: lambda_onEnable_2
        return getResource("items.yml");
    }

  private Styler lambda_onEnable_1() { // было: lambda_onEnable_1
        return styler;
    }

  private InputStream lambda_onEnable_0() { // было: lambda_onEnable_0
        return getResource("messages.yml");
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
