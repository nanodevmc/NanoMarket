// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.model.Page
package io.nano.nanomarket.model;

import java.util.List;

public record Page<T>(List items, int page, int pageSize, int totalItems) {

  public Page(List<T> items, int page, int pageSize, int totalItems) { // было: <init>
        super();
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

  public int totalPages() {
        return totalPages(totalItems, pageSize);
    }

  public boolean hasPrevious() {
        return page > 1;
    }

  public boolean hasNext() {
        return page < totalPages();
    }

  public static int totalPages(int totalItems, int pageSize) {
        return pageSize > 0 ? Math.max(1, (totalItems + pageSize - 1) / pageSize) : 1;
    }

  public static int clampPage(int page, int totalItems, int pageSize) {
        return Math.max(1, Math.min(page, totalPages(totalItems, pageSize)));
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
