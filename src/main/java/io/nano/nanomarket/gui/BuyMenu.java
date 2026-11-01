// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.gui.BuyMenu
package io.nano.nanomarket.gui;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.config.Settings;
import io.nano.nanomarket.config.Settings.Gui;
import io.nano.nanomarket.config.Settings.Purchase;
import io.nano.nanomarket.config.SoundBank;
import io.nano.nanomarket.economy.VaultEconomy;
import io.nano.nanomarket.gui.BrowseMenu;
import io.nano.nanomarket.gui.BuyMenu.Status;
import io.nano.nanomarket.gui.Menu;
import io.nano.nanomarket.gui.Menu.Click;
import io.nano.nanomarket.gui.Ui;
import io.nano.nanomarket.model.Lot;
import io.nano.nanomarket.service.MarketService;
import io.nano.nanomarket.service.MarketService.Receipt;
import io.nano.nanomarket.service.Result;
import io.nano.nanomarket.text.Styler;
import io.nano.nanomarket.util.Inventories;
import io.nano.nanomarket.util.ItemBuilder;
import io.nano.nanomarket.util.Money;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public final class BuyMenu extends Menu {

    // ---- поля ----
  private static final int SLOT_ITEM = 13;
  private static final int SLOT_AMOUNT = 22;
  private static final int SLOT_SUMMARY = 49;
  private static final int[] SIMILAR_SLOTS;
  private Lot lot;
  private List<Lot> similar;
  private int amount;
  private boolean busy;
  private String progress;

    static {
        SIMILAR_SLOTS = new int[]{ 38, 39, 40, 41, 42 };
    }

  private BuyMenu(NanoMarket plugin, Player viewer, Lot lot, List<Lot> similar) { // было: <init>
        super(plugin, viewer);
        amount = 1;
        this.lot = lot;
        this.similar = similar;
    }

  public static void show(NanoMarket plugin, Player viewer, long lotId) {
        plugin.market().find(lotId).whenComplete((lp0, lp1) -> lambda_show_1(plugin, viewer, (Lot) lp0, (Throwable) lp1));
    }

  protected String title() {
        return plugin.messages().text("gui.buy.title", new Object[0]);
    }

  private int maxAmount() {
        return Math.max(1, Math.min(lot.remaining(), plugin.settings().purchase().maxPerPurchase()));
    }

  private long total() {
        return lot.totalCents(amount);
    }

  private Status status() {
        if (viewer.getUniqueId().equals(lot.sellerId())) {
            return Status.OWN;
        }
        if (!plugin.economy().available()) {
            return Status.NO_ECONOMY;
        }
        if (plugin.economy().has(viewer, total())) {
            Purchase purchase = plugin.settings().purchase();
            return purchase.checkSpace() && !purchase.dropIfNoSpace() && !Inventories.canHold(viewer, lot.template(), amount) ? Status.NO_SPACE : Status.OK;
        }
        return Status.NO_MONEY;
    }

  protected void render() {
        String temp13;
        Messages __obj15;
        String temp23;
        Messages temp24;
        ItemBuilder temp25;
        amount = Math.max(1, Math.min(amount, maxAmount()));
        int steps = 0;
        for (; steps < 54; ++steps) {
            if (steps < 9) {
                set(steps, pane());
            } else if (steps >= 45) {
                set(steps, pane());
            } else if (steps % 9) {
                if (steps % 9 == 8) {
                    set(steps, pane());
                }
            } else {
                set(steps, pane());
            }
        }
        set(13, Ui.lotIcon(plugin, lot, "gui.buy.lot.lore"));
        int[][] __obj2 = new int[6][];
        __obj2[0] = new int[]{ 19, -64 };
        int[] __obj3 = new int[]{ 20, -16 };
        __obj2[1] = __obj3;
        int[] __obj4 = new int[]{ 21, -1 };
        __obj2[2] = __obj4;
        int[] __obj5 = new int[]{ 23, 1 };
        __obj2[3] = __obj5;
        int[] __obj6 = new int[]{ 24, 16 };
        __obj2[4] = __obj6;
        int[] __obj7 = new int[]{ 25, 64 };
        __obj2[5] = __obj7;
        steps = __obj2;
        int[][] half = steps;
        int n = half.length;
        int other = 0;
        int[] step;
        for (; other < n; ++other) {
            step = half[other];
            int delta = step[1];
            boolean plus = delta > 0;
            if (plus == 0) {
            }
            Messages temp16 = plugin.messages();
            if (plus == 0) {
                temp13 = "gui.buy.minus.name";
                __obj15 = temp16;
            } else {
                temp13 = "gui.buy.plus.name";
            }
            Object[] __obj20 = new Object[]{ "step", Integer.valueOf(Math.abs(delta)) };
            if (plus == 0) {
                temp23 = "gui.buy.minus.lore";
                temp24 = plugin.messages();
                temp25 = temp13.name(__obj15.text(temp13, __obj20));
            } else {
                temp23 = "gui.buy.plus.lore";
                temp24 = plugin.messages();
                temp25 = temp13.name(__obj15.text(temp13, __obj20));
            }
            ItemStack icon = temp25.lore(temp24.lines(temp23, new Object[0])).amount(Math.abs(delta)).build();
            set(step[0], icon, lp0 -> lambda_render_2(delta, (Click) lp0));
        }
        Object[] __obj9 = new Object[]{ "amount", Integer.valueOf(amount) };
        Object[] __obj10 = new Object[]{ "total", Money.format(total()), "left", Integer.valueOf(lot.remaining() - amount) };
        set(22, ItemBuilder.of(Material.NAME_TAG).name(plugin.messages().text("gui.buy.amount.name", __obj9)).lore(plugin.messages().lines("gui.buy.amount.lore", __obj10)).build());
        half = (maxAmount() + 1) / 2;
        preset(29, Material.COPPER_INGOT, 1, "gui.buy.preset.one.name", "gui.buy.preset.one.lore");
        preset(31, Material.IRON_INGOT, half, "gui.buy.preset.half.name", "gui.buy.preset.half.lore");
        preset(33, Material.DIAMOND, maxAmount(), "gui.buy.preset.max.name", "gui.buy.preset.max.lore");
        for (n = 0; n < SIMILAR_SLOTS.length; ++n) {
            if (n >= similar.size()) {
                break;
            }
            other = (Lot) similar.get(n);
            ItemStack step = ItemBuilder.showcase(other.template()).amount(Math.min(other.remaining(), other.template().getMaxStackSize())).lore(plugin.messages().lines("gui.buy.similar.lore", Ui.lotVars(plugin, other))).build();
            set(SIMILAR_SLOTS[n], step, lp0 -> lambda_render_3(other, (Click) lp0));
        }
        set(48, Ui.button(plugin, Material.REDSTONE_BLOCK, "gui.buy.cancel.name", "gui.buy.cancel.lore", new Object[0]), lp0 -> lambda_render_4((Click) lp0));
        Object[] __obj11 = new Object[]{ "total", Money.format(total()), "amount", Integer.valueOf(amount) };
        set(50, Ui.button(plugin, Material.EMERALD_BLOCK, "gui.buy.confirm.name", "gui.buy.confirm.lore", __obj11), lp0 -> lambda_render_5((Click) lp0));
        renderSummary();
    }

  private void preset(int slot, Material material, int value, String nameKey, String loreKey) {
        int n = Math.max(1, Math.min(value, maxAmount()));
        ItemStack icon = Ui.button(plugin, material, nameKey, loreKey, new Object[]{ "amount", Integer.valueOf(n), "total", Money.format(lot.totalCents(n)) });
        set(slot, icon, lp0 -> lambda_preset_6(n, (Click) lp0));
    }

  private void setAmount(int value) {
        if (busy) {
            return;
        }
        amount = Math.max(1, Math.min(value, maxAmount()));
        redraw();
    }

  private void renderSummary() {
        long temp3;
        String temp4;
        Status status;
        if (progress == null) {
            status = status();
            temp3 = plugin.economy().available() ? plugin.economy().balanceCents(viewer) : 0L;
        } else {
            set(49, ItemBuilder.of(Material.GOLD_INGOT).name(progress).build());
            return;
        }
        long balance = temp3;
        switch (status) {
            default:
                throw new IncompatibleClassChangeError();
            case OK:
                temp4 = plugin.messages().text("gui.buy.summary.ok", new Object[0]);
                break;
            case NO_MONEY:
                temp4 = plugin.messages().text("gui.buy.summary.no-money", new Object[]{ "missing", Money.format(Math.max(0L, total() - balance)) });
                break;
            case NO_SPACE:
                temp4 = plugin.messages().text("gui.buy.summary.no-space", new Object[0]);
                break;
            case NO_ECONOMY:
                temp4 = plugin.messages().text("gui.buy.summary.no-economy", new Object[0]);
                break;
            case OWN:
                temp4 = plugin.messages().text("gui.buy.summary.own", new Object[0]);
        }
        String statusText = temp4;
        set(49, ItemBuilder.of(Material.GOLD_INGOT).name(plugin.messages().text("gui.buy.summary.name", new Object[0])).lore(plugin.messages().lines("gui.buy.summary.lore", new Object[]{ "amount", Integer.valueOf(amount), "unit", Money.format(lot.unitCents()), "total", Money.format(total()), "balance", Money.format(balance), "status", statusText })).build());
    }

  private void confirm() {
        if (busy) {
            return;
        }
        Status status = status();
        if (status == Status.OK) {
            busy = true;
            Gui gui = plugin.settings().gui();
            if (gui.animation()) {
                long step = (long) gui.animationStepTicks();
                showProgress("gui.buy.progress.calc", 1);
                Bukkit.getScheduler().runTaskLater(plugin, () -> lambda_confirm_8(step), step);
                return;
            } else {
                execute();
                return;
            }
        }
        deny(status);
    }

  private void deny(Status status) {
        String temp2;
        switch (status) {
            case NO_MONEY:
                temp2 = "buy.not-enough-money";
                break;
            case NO_SPACE:
                temp2 = "buy.no-space";
                break;
            case NO_ECONOMY:
                temp2 = "economy-missing";
                break;
            case OWN:
                temp2 = "buy.own-lot";
                break;
            default:
                temp2 = "buy.failed";
        }
        String key = temp2;
        if (status != Status.NO_MONEY) {
            plugin.messages().send(viewer, (String) key, new Object[0]);
        } else {
            plugin.messages().send(viewer, (String) key, new Object[]{ "need", Money.format(total()), "balance", Money.format(plugin.economy().balanceCents(viewer)) });
        }
        plugin.sounds().play(viewer, "error");
    }

  private void showProgress(String textKey, int filled) {
        progress = plugin.styler().render("{text} &#3EE0FF{bar}", new Object[]{ "text", plugin.messages().text(textKey, new Object[0]), "bar", Glyphs.bar(filled, 3) });
        if (isShown()) {
            renderSummary();
        }
    }

  private void execute() {
        plugin.market().buy(viewer, lot, amount).whenComplete((lp0, lp1) -> lambda_execute_10((Result) lp0, (Throwable) lp1));
    }

  private void reloadLot() {
        plugin.market().find(lot.id()).whenComplete((lp0, lp1) -> lambda_reloadLot_12((Lot) lp0, (Throwable) lp1));
    }

  private void lambda_reloadLot_12(Lot fresh, Throwable err) { // было: lambda_reloadLot_12
        if (!isShown()) {
            return;
        }
        if (err == null && fresh != null && fresh.active()) {
            after(20L, () -> lambda_reloadLot_11(fresh));
            return;
        }
        BrowseMenu.show(plugin, viewer);
    }

  private void lambda_reloadLot_11(Lot fresh) { // было: lambda_reloadLot_11
        lot = fresh;
        progress = null;
        redraw();
    }

  private void lambda_execute_10(Result result, Throwable err) { // было: lambda_execute_10
        busy = false;
        if (err != null) {
            plugin.getLogger().warning("Покупка не удалась: " + err);
            plugin.messages().send(viewer, "error-internal", new Object[0]);
            progress = null;
            if (isShown()) {
                renderSummary();
            }
            return;
        }
        if (result.ok()) {
            Receipt receipt = (Receipt) result.value();
            plugin.messages().send(viewer, "buy.success", new Object[]{ "amount", Integer.valueOf(receipt.units()), "item", plugin.catalog().displayName(receipt.lot().template()), "seller", receipt.lot().sellerName(), "total", Money.format(receipt.totalCents()) });
            plugin.sounds().play(viewer, "purchase");
            showProgress("gui.buy.progress.done", 3);
            after(Math.max(10L, (long) plugin.settings().gui().animationStepTicks() * 2L), () -> lambda_execute_9());
            return;
        }
        plugin.messages().send(viewer, result.errorKey(), result.errorArgs());
        plugin.sounds().play(viewer, "error");
        showProgress("gui.buy.progress.fail", 0);
        reloadLot();
    }

  private void lambda_execute_9() { // было: lambda_execute_9
        BrowseMenu.show(plugin, viewer);
    }

  private void lambda_confirm_8(long step) { // было: lambda_confirm_8
        if (isShown()) {
            showProgress("gui.buy.progress.pay", 2);
            Bukkit.getScheduler().runTaskLater(plugin, () -> lambda_confirm_7(), step);
            return;
        }
        busy = false;
    }

  private void lambda_confirm_7() { // было: lambda_confirm_7
        if (isShown()) {
            execute();
        } else {
            busy = false;
        }
    }

  private void lambda_preset_6(int n, Click click) { // было: lambda_preset_6
        setAmount(n);
    }

  private void lambda_render_5(Click click) { // было: lambda_render_5
        confirm();
    }

  private void lambda_render_4(Click click) { // было: lambda_render_4
        BrowseMenu.show(plugin, viewer);
    }

  private void lambda_render_3(Lot other, Click click) { // было: lambda_render_3
        show(plugin, viewer, other.id());
    }

  private void lambda_render_2(int delta, Click click) { // было: lambda_render_2
        setAmount(amount + delta);
    }

  private static void lambda_show_1(NanoMarket plugin, Player viewer, Lot lot, Throwable err) { // было: lambda_show_1
        if (err != null) {
            plugin.messages().send(viewer, "error-internal", new Object[0]);
            return;
        }
        if (lot != null && lot.active()) {
            plugin.market().similar(lot, plugin.settings().gui().similarOffers()).whenComplete((lp0, lp1) -> lambda_show_0(plugin, viewer, lot, (List) lp0, (Throwable) lp1));
            return;
        }
        plugin.messages().send(viewer, "buy.not-found", new Object[0]);
        plugin.sounds().play(viewer, "error");
        BrowseMenu.show(plugin, viewer);
    }

  private static void lambda_show_0(NanoMarket plugin, Player viewer, Lot lot, List others, Throwable err2) { // было: lambda_show_0
        BuyMenu temp11 = new BuyMenu();
        temp11.open();
    }


    // ---- вложенный класс: Status ----
    private enum Status {
    
        OK,
        NO_MONEY,
        NO_SPACE,
        NO_ECONOMY,
        OWN;
    
        static {
            _VALUES = _values();
        }
    
      private Status() { // было: <init>
            // (пустое тело)
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
