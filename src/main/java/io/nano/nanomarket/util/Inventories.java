// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.util.Inventories
package io.nano.nanomarket.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class Inventories {

  private Inventories() { // было: <init>
        super();
    }

  public static int capacityFor(ItemStack[] storage, ItemStack template) {
        int max = template.getMaxStackSize();
        int space = 0;
        ItemStack[] itemStack[] = storage;
        int n = itemStack[].length;
        for (ItemStack slot : var4) {
            if (slot == null) {
                space += max;
            } else if (slot.getType() == Material.AIR) {
                space += max;
            } else if (slot.isSimilar(template) && slot.getAmount() < max) {
                space += max - slot.getAmount();
            }
        }
        return space;
    }

  public static boolean canHold(Player player, ItemStack template, int units) {
        return capacityFor(player.getInventory().getStorageContents(), template) >= units;
    }

  public static int give(Player player, ItemStack template, int units) {
        int left = units;
        PlayerInventory inv = player.getInventory();
        int max = template.getMaxStackSize();
        int chunk;
        HashMap rest;
        for (; left > 0; left -= chunk) {
            chunk = Math.min(left, max);
            ItemStack stack = template.clone();
            stack.setAmount(chunk);
            rest = new HashMap(inv.addItem(new ItemStack[]{ stack }));
            if (!rest.isEmpty()) {
                break;
            }
        }
        int notAdded = rest.values().stream().mapToInt(ItemStack::getAmount).sum();
        return left - chunk + notAdded;
    }

  public static void giveOrDrop(Player player, ItemStack template, int units) {
        int rest = give(player, template, units);
        int max = template.getMaxStackSize();
        for (; rest > 0; rest -= chunk) {
            int chunk = Math.min(rest, max);
            ItemStack stack = template.clone();
            stack.setAmount(chunk);
            player.getWorld().dropItem(player.getLocation(), stack);
        }
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
