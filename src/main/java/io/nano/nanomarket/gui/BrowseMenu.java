// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.gui.BrowseMenu
package io.nano.nanomarket.gui;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.config.Settings;
import io.nano.nanomarket.config.Settings.Market;
import io.nano.nanomarket.config.SoundBank;
import io.nano.nanomarket.economy.VaultEconomy;
import io.nano.nanomarket.gui.BuyMenu;
import io.nano.nanomarket.gui.Menu;
import io.nano.nanomarket.gui.Menu.Click;
import io.nano.nanomarket.gui.MyLotsMenu;
import io.nano.nanomarket.gui.Ui;
import io.nano.nanomarket.model.Lot;
import io.nano.nanomarket.model.Page;
import io.nano.nanomarket.model.SortMode;
import io.nano.nanomarket.service.MarketService;
import io.nano.nanomarket.service.PlayerSessions;
import io.nano.nanomarket.service.PlayerSessions.Session;
import io.nano.nanomarket.service.SearchPrompts;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class BrowseMenu extends Menu {

    // ---- поля ----
  private static final int FIRST_LOT_SLOT = 9;
  private final Session session;
  private Page<Lot> data;

  private BrowseMenu(NanoMarket plugin, Player viewer, Session session, Page<Lot> data) { // было: <init>
        super(plugin, viewer);
        this.session = session;
        this.data = data;
    }

  public static void show(NanoMarket plugin, Player viewer) {
        if (plugin.ready()) {
            Session session = plugin.sessions().of(viewer.getUniqueId());
            plugin.market().browse(session.query, session.sort, session.page).whenComplete((lp0, lp1) -> lambda_show_0(plugin, viewer, session, (Page) lp0, (Throwable) lp1));
            return;
        }
        plugin.messages().send(viewer, "market-offline", new Object[0]);
    }

  private void reload() {
        plugin.market().browse(session.query, session.sort, session.page).whenComplete((lp0, lp1) -> lambda_reload_1((Page) lp0, (Throwable) lp1));
    }

  protected String title() {
        return plugin.messages().text("gui.browse.title", new Object[0]);
    }

  protected void render() {
        fillRow(0);
        fillRow(5);
        long balance = plugin.economy().available() ? plugin.economy().balanceCents(viewer) : 0L;
        String query = session.query.isEmpty() ? "—" : session.query;
        String sortName = plugin.messages().text(session.sort.messageKey(), new Object[0]);
        int pages = data.totalPages();
        set(4, Ui.button(plugin, Material.NETHER_STAR, "gui.browse.header.name", "gui.browse.header.lore", new Object[]{ "balance", Money.format(balance), "total", Integer.valueOf(data.totalItems()), "sort", sortName, "query", query }));
        int slot = 9;
        Iterator limit = data.items().iterator();
        while (limit.hasNext()) {
            Lot lot = (Lot) limit.next();
            set(slot++, Ui.lotIcon(plugin, lot, "gui.browse.lot.lore"), lp0 -> lambda_render_2(lot, (Click) lp0));
        }
        if (data.items().isEmpty()) {
            set(22, Ui.button(plugin, Material.BARRIER, "gui.browse.empty.name", "gui.browse.empty.lore", new Object[0]));
        }
        if (data.hasPrevious()) {
            set(45, Ui.button(plugin, Material.ARROW, "gui.nav.prev.name", "gui.nav.prev.lore", new Object[]{ "page", Integer.valueOf(data.page() - 1), "pages", Integer.valueOf(pages) }), lp0 -> lambda_render_3((Click) lp0));
        }
        if (data.hasNext()) {
            set(53, Ui.button(plugin, Material.ARROW, "gui.nav.next.name", "gui.nav.next.lore", new Object[]{ "page", Integer.valueOf(data.page() + 1), "pages", Integer.valueOf(pages) }), lp0 -> lambda_render_4((Click) lp0));
        }
        Object[] __obj4 = new Object[]{ "page", Integer.valueOf(data.page()), "pages", Integer.valueOf(pages), "total", Integer.valueOf(data.totalItems()) };
        set(49, Ui.button(plugin, Material.BOOK, "gui.nav.page.name", "gui.nav.page.lore", __obj4));
        limit = plugin.settings().market().maxLotsPerPlayer();
        Object[] __obj5 = new Object[]{ "lots", "…", "limit", Integer.valueOf(limit) };
        set(47, Ui.button(plugin, Material.CHEST, "gui.nav.mylots.name", "gui.nav.mylots.lore", __obj5), lp0 -> lambda_render_5((Click) lp0));
        plugin.market().activeCount(viewer.getUniqueId()).whenComplete((lp0, lp1) -> lambda_render_7(limit, (Integer) lp0, (Throwable) lp1));
        set(48, Ui.button(plugin, Material.CLOCK, "gui.nav.refresh.name", "gui.nav.refresh.lore", new Object[0]), lp0 -> lambda_render_8((Click) lp0));
        Object[] __obj6 = new Object[]{ "sort", sortName };
        set(50, Ui.button(plugin, Material.HOPPER, "gui.nav.sort.name", "gui.nav.sort.lore", __obj6), lp0 -> lambda_render_9((Click) lp0));
        if (session.query.isEmpty()) {
            set(51, Ui.button(plugin, Material.COMPASS, "gui.nav.search.name", "gui.nav.search.lore", new Object[0]), lp0 -> lambda_render_10((Click) lp0));
        } else {
            set(51, Ui.button(plugin, Material.BARRIER, "gui.nav.search-clear.name", "gui.nav.search-clear.lore", new Object[]{ "query", session.query }), lp0 -> lambda_render_11((Click) lp0));
        }
    }

  private void turn(int delta) {
        session.page = Math.max(1, session.page + delta);
        plugin.sounds().play(viewer, "page");
        reload();
    }

  private void lambda_render_11(Click click) { // было: lambda_render_11
        session.query = "";
        session.page = 1;
        plugin.messages().send(viewer, "search.cleared", new Object[0]);
        reload();
    }

  private void lambda_render_10(Click click) { // было: lambda_render_10
        plugin.prompts().begin(viewer);
    }

  private void lambda_render_9(Click click) { // было: lambda_render_9
        session.sort = session.sort.next();
        session.page = 1;
        reload();
    }

  private void lambda_render_8(Click click) { // было: lambda_render_8
        plugin.sounds().play(viewer, "page");
        reload();
    }

  private void lambda_render_7(int limit, Integer count, Throwable err) { // было: lambda_render_7
        if (err == null && isShown()) {
            set(47, Ui.button(plugin, Material.CHEST, "gui.nav.mylots.name", "gui.nav.mylots.lore", new Object[]{ "lots", count, "limit", Integer.valueOf(limit) }), lp0 -> lambda_render_6((Click) lp0));
        }
    }

  private void lambda_render_6(Click click) { // было: lambda_render_6
        MyLotsMenu.show(plugin, viewer);
    }

  private void lambda_render_5(Click click) { // было: lambda_render_5
        MyLotsMenu.show(plugin, viewer);
    }

  private void lambda_render_4(Click click) { // было: lambda_render_4
        turn(1);
    }

  private void lambda_render_3(Click click) { // было: lambda_render_3
        turn(-1);
    }

  private void lambda_render_2(Lot lot, Click click) { // было: lambda_render_2
        BuyMenu.show(plugin, viewer, lot.id());
    }

  private void lambda_reload_1(Page page, Throwable err) { // было: lambda_reload_1
        if (err == null && isShown()) {
            session.page = page.page();
            data = page;
            redraw();
            return;
        }
    }

  private static void lambda_show_0(NanoMarket plugin, Player viewer, Session session, Page page, Throwable err) { // было: lambda_show_0
        if (err == null) {
            session.page = page.page();
            new BrowseMenu(plugin, viewer, session, page).open();
            return;
        }
        plugin.getLogger().warning("Не удалось загрузить рынок: " + err);
        plugin.messages().send(viewer, "error-internal", new Object[0]);
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
