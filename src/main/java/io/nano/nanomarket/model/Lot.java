// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.model.Lot
package io.nano.nanomarket.model;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public record Lot(long id, UUID sellerId, String sellerName, String material, ItemStack template, int amount, int sold, long unitCents, long createdAt, String serverId, boolean cancelled) {

  public Lot(long id, UUID sellerId, String sellerName, String material, ItemStack template, int amount, int sold, long unitCents, long createdAt, String serverId, boolean cancelled) { // было: <init>
        super();
        this.id = id;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.material = material;
        this.template = template;
        this.amount = amount;
        this.sold = sold;
        this.unitCents = unitCents;
        this.createdAt = createdAt;
        this.serverId = serverId;
        this.cancelled = cancelled;
    }

  public int remaining() {
        return Math.max(0, amount - sold);
    }

  public boolean active() {
        return !cancelled && remaining() > 0;
    }

  public long totalCents(int units) {
        return Math.multiplyExact(unitCents, (long) units);
    }

  public long remainingValueCents() {
        return totalCents(remaining());
    }

  public ItemStack stack(int units) {
        ItemStack copy = template.clone();
        copy.setAmount(units);
        return copy;
    }

  public Lot withId(long newId) {
        return new Lot(newId, sellerId, sellerName, material, template, amount, sold, unitCents, createdAt, serverId, cancelled);
    }

  public Lot withSold(int newSold) {
        return new Lot(id, sellerId, sellerName, material, template, amount, newSold, unitCents, createdAt, serverId, cancelled);
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
