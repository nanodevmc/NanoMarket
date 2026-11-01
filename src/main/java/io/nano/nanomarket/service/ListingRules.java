// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.service.ListingRules
package io.nano.nanomarket.service;

import io.nano.nanomarket.config.Settings.Market;
import io.nano.nanomarket.service.ListingRules.Valid;
import io.nano.nanomarket.service.Result;
import io.nano.nanomarket.text.Colors;
import io.nano.nanomarket.util.Money;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalLong;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ListingRules {

  private ListingRules() { // было: <init>
        super();
    }

  public static Result<Valid> check(Market rules, boolean bypassBlacklist, ItemStack hand, String priceArg, String amountArg) {
        String temp12;
        int temp13;
        Object[] temp14;
        Object[] temp15;
        String temp16;
        if (hand == null || hand.getType() == Material.AIR || hand.getType().isAir()) {
            return Result.fail("sell.no-item", new Object[0]);
        }
        OptionalLong parsed = Money.parse(priceArg);
        if (parsed.isEmpty()) {
            __obj1 = new Object[2];
            __obj1[0] = "input";
            if (priceArg != null) {
                temp12 = priceArg;
                temp13 = 1;
                temp14 = __obj1;
                temp15 = __obj1;
                temp16 = "sell.invalid-price";
            }
            temp14[temp13] = temp12;
            return Result.fail(temp16, temp15);
        }
        if (parsed.getAsLong() <= 0L) {
            Object[] __obj1 = new Object[2];
            __obj1[0] = "input";
            if (priceArg != null) {
                temp12 = priceArg;
                temp13 = 1;
                temp14 = __obj1;
                temp15 = __obj1;
                temp16 = "sell.invalid-price";
            }
            temp14[temp13] = temp12;
            return Result.fail(temp16, temp15);
        }
        long price = parsed.getAsLong();
        if (price < rules.minPriceCents()) {
            return Result.fail("sell.price-low", new Object[]{ "min", Money.format(rules.minPriceCents()) });
        }
        if (price > rules.maxPriceCents()) {
            return Result.fail("sell.price-high", new Object[]{ "max", Money.format(rules.maxPriceCents()) });
        }
        int amount = hand.getAmount();
        if (amountArg == null) {
            if (amount <= rules.maxAmountPerLot()) {
                if (!bypassBlacklist && rules.blacklist().contains(hand.getType().name())) {
                    return Result.fail("sell.blacklisted", new Object[0]);
                }
                ItemMeta want = hand.hasItemMeta() ? hand.getItemMeta() : null;
                if (want != null) {
                    if (rules.rejectObfuscated() && hasObfuscation((ItemMeta) want)) {
                        return Result.fail("sell.rejected", new Object[0]);
                    }
                    if (!rules.allowCustomItems() && isCustom((ItemMeta) want)) {
                        return Result.fail("sell.custom-denied", new Object[0]);
                    }
                }
                return Result.ok(new Valid(price, amount));
            }
        } else if (!amountArg.isBlank()) {
            int want;
            try {
                want = Integer.parseInt(amountArg.trim());
            } catch (NumberFormatException e1) {
                return Result.fail("sell.invalid-amount", new Object[]{ "input", amountArg });
            }
            if (want <= 0) {
                return Result.fail("sell.invalid-amount", new Object[]{ "input", amountArg });
            } else if (want <= hand.getAmount()) {
                amount = want;
                if (amount <= rules.maxAmountPerLot()) {
                    if (!bypassBlacklist && rules.blacklist().contains(hand.getType().name())) {
                        return Result.fail("sell.blacklisted", new Object[0]);
                    }
                    ItemMeta want = hand.hasItemMeta() ? hand.getItemMeta() : null;
                    if (want != null) {
                        if (rules.rejectObfuscated() && hasObfuscation((ItemMeta) want)) {
                            return Result.fail("sell.rejected", new Object[0]);
                        }
                        if (!rules.allowCustomItems() && isCustom((ItemMeta) want)) {
                            return Result.fail("sell.custom-denied", new Object[0]);
                        }
                    }
                    return Result.ok(new Valid(price, amount));
                }
            } else {
                return Result.fail("sell.not-enough-items", new Object[]{ "have", Integer.valueOf(hand.getAmount()), "want", Integer.valueOf(want) });
            }
        } else if (amount <= rules.maxAmountPerLot()) {
            if (!bypassBlacklist && rules.blacklist().contains(hand.getType().name())) {
                return Result.fail("sell.blacklisted", new Object[0]);
            }
            ItemMeta want = hand.hasItemMeta() ? hand.getItemMeta() : null;
            if (want != null) {
                if (rules.rejectObfuscated() && hasObfuscation((ItemMeta) want)) {
                    return Result.fail("sell.rejected", new Object[0]);
                }
                if (!rules.allowCustomItems() && isCustom((ItemMeta) want)) {
                    return Result.fail("sell.custom-denied", new Object[0]);
                }
            }
            return Result.ok(new Valid(price, amount));
        }
        return Result.fail("sell.amount-too-high", new Object[]{ "max", Integer.valueOf(rules.maxAmountPerLot()) });
    }

  private static boolean hasObfuscation(ItemMeta meta) {
        List temp1;
        Iterator iterator;
        String line;
        List lore;
        if (meta.hasDisplayName()) {
            if (Colors.containsObfuscation(meta.getDisplayName())) {
                return true;
            }
            lore = temp1;
            if (lore == null) {
                return false;
            } else {
                iterator = lore.iterator();
            }
            while (iterator.hasNext()) {
                line = (String) iterator.next();
                if (Colors.containsObfuscation(line)) {
                    break;
                }
            }
            return true;
        }
        List lore = meta.hasLore() ? meta.getLore() : null;
        if (lore == null) {
            return false;
        }
        Iterator iterator = lore.iterator();
        while (iterator.hasNext()) {
            String line = (String) iterator.next();
            if (Colors.containsObfuscation(line)) {
                break;
            }
        }
        return true;
    }

  private static boolean isCustom(ItemMeta meta) {
        return meta.hasDisplayName() || meta.hasLore() || meta.hasEnchants();
    }


    // ---- вложенный класс: Valid ----
    public static record Valid(long unitCents, int amount) {
    
      public Valid(long unitCents, int amount) { // было: <init>
            super();
            this.unitCents = unitCents;
            this.amount = amount;
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
