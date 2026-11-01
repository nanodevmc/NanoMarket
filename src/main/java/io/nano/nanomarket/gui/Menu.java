// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.gui.Menu
package io.nano.nanomarket.gui;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.config.SoundBank;
import io.nano.nanomarket.gui.Menu.Click;
import io.nano.nanomarket.util.ItemBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class Menu implements InventoryHolder {

    // ---- поля ----
  protected final NanoMarket plugin;
  protected final Player viewer;
  private final Map<Integer, Consumer<Click>> handlers;
  private Inventory inventory;

  protected Menu(NanoMarket plugin, Player viewer) { // было: <init>
        super();
        handlers = new HashMap();
        this.plugin = plugin;
        this.viewer = viewer;
    }

  protected abstract String title();

  protected abstract void render();

  protected int size() {
        return 54;
    }

  public final void open() {
        inventory = Bukkit.createInventory(this, size(), title());
        render();
        viewer.openInventory(inventory);
        plugin.sounds().play(viewer, "open");
    }

  protected final void redraw() {
        if (inventory != null) {
            handlers.clear();
            inventory.clear();
            render();
            return;
        }
    }

  protected final boolean isShown() {
        return inventory != null && viewer.isOnline() ? viewer.getOpenInventory().getTopInventory().getHolder() == this : false;
    }

  protected final void set(int slot, ItemStack icon, Consumer<Click> onClick) {
        inventory.setItem(slot, icon);
        if (onClick == null) {
            handlers.remove(Integer.valueOf(slot));
        } else {
            handlers.put(Integer.valueOf(slot), onClick);
        }
    }

  protected final void set(int slot, ItemStack icon) {
        set(slot, icon, null);
    }

  protected final ItemStack pane() {
        return ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
    }

  protected final void fillRow(int row) {
        for (int var2 = 0; var2 < 9; ++var2) {
            set(row * 9 + var2, pane());
        }
    }

  protected final void after(long ticks, Runnable task) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> lambda_after_0(task), ticks);
    }

  public final void handle(Click click) {
        Consumer handler = (Consumer) handlers.get(Integer.valueOf(click.slot()));
        if (handler != null) {
            plugin.sounds().play(click.player(), "click");
        } else {
            return;
        }
        try {
            handler.accept(click);
        } catch (RuntimeException e1) {
            plugin.getLogger().log(Level.SEVERE, "Ошибка в меню " + getClass().getSimpleName(), e1);
            plugin.messages().send(click.player(), "error-internal", new Object[0]);
        }
    }

  public void onClose() {
        // (пустое тело)
    }

  public Inventory getInventory() {
        return inventory;
    }

  private void lambda_after_0(Runnable task) { // было: lambda_after_0
        if (isShown()) {
            task.run();
        }
    }


    // ---- вложенный класс: Click ----
    public static record Click(Player player, ClickType type, int slot) {
    
      public Click(Player player, ClickType type, int slot) { // было: <init>
            super();
            this.player = player;
            this.type = type;
            this.slot = slot;
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
