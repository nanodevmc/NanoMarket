// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.model.Sale
package io.nano.nanomarket.model;

import java.util.UUID;

public record Sale(long id, long lotId, UUID buyerId, String buyerName, UUID sellerId, String sellerName, String material, int amount, long unitCents, long totalCents, long createdAt, String serverId, boolean paid, long paidAt) {

  public Sale(long id, long lotId, UUID buyerId, String buyerName, UUID sellerId, String sellerName, String material, int amount, long unitCents, long totalCents, long createdAt, String serverId, boolean paid, long paidAt) { // было: <init>
        super();
        this.id = id;
        this.lotId = lotId;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.material = material;
        this.amount = amount;
        this.unitCents = unitCents;
        this.totalCents = totalCents;
        this.createdAt = createdAt;
        this.serverId = serverId;
        this.paid = paid;
        this.paidAt = paidAt;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
