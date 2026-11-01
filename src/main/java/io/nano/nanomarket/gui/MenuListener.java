// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.gui.MenuListener
package io.nano.nanomarket.gui;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Settings;
import io.nano.nanomarket.config.Settings.Security;
import io.nano.nanomarket.gui.Menu;
import io.nano.nanomarket.gui.Menu.Click;
import io.nano.nanomarket.service.RateLimiter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

public final class MenuListener implements Listener {

    // ---- поля ----
  private final NanoMarket plugin;

  public MenuListener(NanoMarket plugin) { // было: <init>
        super();
        this.plugin = plugin;
    }

    @EventHandler(priority = "HIGHEST")
  public void onClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        InventoryHolder player = top.getHolder();
        if (player instanceof Menu menu) {
            event.setCancelled(true);
            HumanEntity humanEntity = event.getWhoClicked();
            if (humanEntity instanceof Player player) {
                if (event.getClickedInventory() != top) {
                    return;
                } else if (plugin.limiter().allowClick(player.getUniqueId(), plugin.settings().security().clickCooldownMs())) {
                    menu.handle(new Click(player, event.getClick(), event.getSlot()));
                    return;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    @EventHandler(priority = "HIGHEST")
  public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Menu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
  public void onClose(InventoryCloseEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder instanceof Menu menu) {
            menu.onClose();
        }
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
