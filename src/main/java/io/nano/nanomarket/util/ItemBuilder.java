// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.util.ItemBuilder
package io.nano.nanomarket.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemBuilder {

    // ---- поля ----
  private final ItemStack stack;
  private String name;
  private List<String> lore;
  private boolean hideFlags;
  private final boolean keepMeta;

  private ItemBuilder(ItemStack stack, boolean keepMeta) { // было: <init>
        super();
        hideFlags = true;
        this.stack = stack;
        this.keepMeta = keepMeta;
    }

  public static ItemBuilder of(Material material) {
        return new ItemBuilder(new ItemStack(material), false);
    }

  public static ItemBuilder showcase(ItemStack item) {
        ItemBuilder builder = new ItemBuilder(item.clone(), true);
        builder.hideFlags = false;
        return builder;
    }

  public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

  public ItemBuilder lore(List<String> lines) {
        lore = new ArrayList(lines);
        return this;
    }

  public ItemBuilder amount(int amount) {
        stack.setAmount(Math.max(1, Math.min(amount, stack.getMaxStackSize())));
        return this;
    }

  public ItemStack build() {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }
        if (name != null && !keepMeta) {
            meta.setDisplayName("§r" + name);
        }
        if (lore != null) {
            ArrayList all = new ArrayList();
            if (keepMeta && meta.hasLore() && meta.getLore() != null) {
                all.addAll(meta.getLore());
            }
            Iterator iterator = lore.iterator();
            while (iterator.hasNext()) {
                String line = (String) iterator.next();
                all.add("§r" + line);
            }
            meta.setLore(all);
        }
        if (hideFlags) {
            meta.addItemFlags(ItemFlag.values());
        }
        stack.setItemMeta(meta);
        return stack;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
