// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.model.SortMode
package io.nano.nanomarket.model;

import java.util.Locale;

public enum SortMode {

    NEWEST("created_at DESC, id DESC", "sort.newest"),
    PRICE_ASC("unit_cents ASC, id ASC", "sort.price-asc"),
    PRICE_DESC("unit_cents DESC, id ASC", "sort.price-desc"),
    NAME("name_key ASC, unit_cents ASC, id ASC", "sort.name");

    // ---- поля ----
  private final String orderBy;
  private final String messageKey;

    static {
        _VALUES = _values();
    }

  private SortMode(String orderBy, String messageKey) { // было: <init>
        this.orderBy = orderBy;
        this.messageKey = messageKey;
    }

  public String orderBy() {
        return orderBy;
    }

  public String messageKey() {
        return messageKey;
    }

  public SortMode next() {
        SortMode[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

  public static SortMode parse(String raw, SortMode fallback) {
        SortMode temp1;
        if (raw == null) {
            return fallback;
        }
        try {
            temp1 = valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e1) {
            return fallback;
        }
        return temp1;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
