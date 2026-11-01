// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.gui.Ui
package io.nano.nanomarket.gui;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Messages;
import io.nano.nanomarket.model.Lot;
import io.nano.nanomarket.util.ItemBuilder;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

final class Ui {

  private Ui() { // было: <init>
        super();
    }

  static ItemStack button(NanoMarket plugin, Material material, String nameKey, String loreKey, Object[] obj) {
        return ItemBuilder.of(material).name(plugin.messages().text(nameKey, obj)).lore(loreKey != null ? plugin.messages().lines(loreKey, obj) : List.of()).build();
    }

  static ItemStack lotIcon(NanoMarket plugin, Lot lot, String loreKey) {
        ItemStack icon = lot.template();
        return ItemBuilder.showcase(icon).amount(Math.min(lot.remaining(), icon.getMaxStackSize())).lore(plugin.messages().lines(loreKey, lotVars(plugin, lot))).build();
    }

  static Object[] lotVars(NanoMarket plugin, Lot lot) {
        long ageSeconds = Math.max(0L, (System.currentTimeMillis() - lot.createdAt()) / 1000L);
        return new Object[]{ "unit", Money.format(lot.unitCents()), "total", Money.format(lot.remainingValueCents()), "stock", Integer.valueOf(lot.remaining()), "amount", Integer.valueOf(lot.amount()), "sold", Integer.valueOf(lot.sold()), "remaining", Integer.valueOf(lot.remaining()), "seller", lot.sellerName(), "age", Times.duration(ageSeconds), "date", Times.stamp(lot.createdAt()), "item", plugin.catalog().displayName(lot.template()) };
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
