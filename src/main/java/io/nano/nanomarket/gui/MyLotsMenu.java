// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.gui.MyLotsMenu
package io.nano.nanomarket.gui;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.config.SoundBank;
import io.nano.nanomarket.economy.VaultEconomy;
import io.nano.nanomarket.gui.BrowseMenu;
import io.nano.nanomarket.gui.Menu;
import io.nano.nanomarket.gui.Menu.Click;
import io.nano.nanomarket.gui.Ui;
import io.nano.nanomarket.model.Lot;
import io.nano.nanomarket.model.Page;
import io.nano.nanomarket.service.MarketService;
import io.nano.nanomarket.service.PlayerSessions;
import io.nano.nanomarket.service.PlayerSessions.Session;
import io.nano.nanomarket.service.Result;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public final class MyLotsMenu extends Menu {

    // ---- поля ----
  private final Session session;
  private Page<Lot> data;

  private MyLotsMenu(NanoMarket plugin, Player viewer, Session session, Page<Lot> data) { // было: <init>
        super(plugin, viewer);
        this.session = session;
        this.data = data;
    }

  public static void show(NanoMarket plugin, Player viewer) {
        if (plugin.ready()) {
            Session session = plugin.sessions().of(viewer.getUniqueId());
            plugin.market().myLots(viewer.getUniqueId(), session.myPage).whenComplete((lp0, lp1) -> lambda_show_0(plugin, viewer, session, (Page) lp0, (Throwable) lp1));
            return;
        }
        plugin.messages().send(viewer, "market-offline", new Object[0]);
    }

  private void reload() {
        plugin.market().myLots(viewer.getUniqueId(), session.myPage).whenComplete((lp0, lp1) -> lambda_reload_1((Page) lp0, (Throwable) lp1));
    }

  protected String title() {
        return plugin.messages().text("gui.mylots.title", new Object[0]);
    }

  protected void render() {
        fillRow(0);
        fillRow(5);
        long balance = plugin.economy().available() ? plugin.economy().balanceCents(viewer) : 0L;
        set(4, Ui.button(plugin, Material.ENDER_CHEST, "gui.mylots.header.name", "gui.mylots.header.lore", new Object[]{ "balance", Money.format(balance), "lots", Integer.valueOf(data.totalItems()), "limit", Integer.valueOf(plugin.settings().market().maxLotsPerPlayer()) }));
        int slot = 9;
        Iterator pages = data.items().iterator();
        while (pages.hasNext()) {
            Lot lot = (Lot) pages.next();
            set(slot++, Ui.lotIcon(plugin, lot, "gui.mylots.lot.lore"), lp0 -> lambda_render_2(lot, (Click) lp0));
        }
        if (data.items().isEmpty()) {
            set(22, Ui.button(plugin, Material.BARRIER, "gui.mylots.empty.name", "gui.mylots.empty.lore", new Object[0]));
        }
        pages = data.totalPages();
        if (data.hasPrevious()) {
            set(45, Ui.button(plugin, Material.ARROW, "gui.nav.prev.name", "gui.nav.prev.lore", new Object[]{ "page", Integer.valueOf(data.page() - 1), "pages", Integer.valueOf(pages) }), lp0 -> lambda_render_3((Click) lp0));
        }
        if (data.hasNext()) {
            set(53, Ui.button(plugin, Material.ARROW, "gui.nav.next.name", "gui.nav.next.lore", new Object[]{ "page", Integer.valueOf(data.page() + 1), "pages", Integer.valueOf(pages) }), lp0 -> lambda_render_4((Click) lp0));
        }
        set(49, Ui.button(plugin, Material.ARROW, "gui.nav.back.name", "gui.nav.back.lore", new Object[0]), lp0 -> lambda_render_5((Click) lp0));
    }

  private void turn(int delta) {
        session.myPage = Math.max(1, session.myPage + delta);
        plugin.sounds().play(viewer, "page");
        reload();
    }

  private void onLotClick(Lot lot, ClickType type) {
        if (type != ClickType.SHIFT_RIGHT) {
            plugin.messages().send(viewer, "lot-details", Ui.lotVars(plugin, lot));
            return;
        }
        plugin.market().cancel(viewer, lot.id()).whenComplete((lp0, lp1) -> lambda_onLotClick_6(lot, (Result) lp0, (Throwable) lp1));
    }

  private void lambda_onLotClick_6(Lot lot, Result result, Throwable err) { // было: lambda_onLotClick_6
        if (err != null) {
            plugin.messages().send(viewer, "error-internal", new Object[0]);
            return;
        }
        if (result.ok()) {
            plugin.messages().send(viewer, "cancel.success", new Object[]{ "amount", Integer.valueOf(((Cancelled) result.value()).units()), "item", plugin.catalog().displayName(lot.template()) });
            plugin.sounds().play(viewer, "cancel");
        } else {
            plugin.messages().send(viewer, result.errorKey(), result.errorArgs());
            plugin.sounds().play(viewer, "error");
        }
        if (isShown()) {
            reload();
        }
    }

  private void lambda_render_5(Click click) { // было: lambda_render_5
        BrowseMenu.show(plugin, viewer);
    }

  private void lambda_render_4(Click click) { // было: lambda_render_4
        turn(1);
    }

  private void lambda_render_3(Click click) { // было: lambda_render_3
        turn(-1);
    }

  private void lambda_render_2(Lot lot, Click click) { // было: lambda_render_2
        onLotClick(lot, click.type());
    }

  private void lambda_reload_1(Page page, Throwable err) { // было: lambda_reload_1
        if (err == null && isShown()) {
            session.myPage = page.page();
            data = page;
            redraw();
            return;
        }
    }

  private static void lambda_show_0(NanoMarket plugin, Player viewer, Session session, Page page, Throwable err) { // было: lambda_show_0
        if (err == null) {
            session.myPage = page.page();
            new MyLotsMenu(plugin, viewer, session, page).open();
            return;
        }
        plugin.getLogger().warning("Не удалось загрузить мои лоты: " + err);
        plugin.messages().send(viewer, "error-internal", new Object[0]);
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
