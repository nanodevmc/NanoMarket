// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.model.MarketStats
package io.nano.nanomarket.model;

public record MarketStats(int activeLots, long activeUnits, int sellers, int salesTotal, long unitsSoldTotal, long turnoverCents, int salesToday, long turnoverTodayCents, int unpaidSales) {

  public MarketStats(int activeLots, long activeUnits, int sellers, int salesTotal, long unitsSoldTotal, long turnoverCents, int salesToday, long turnoverTodayCents, int unpaidSales) { // было: <init>
        super();
        this.activeLots = activeLots;
        this.activeUnits = activeUnits;
        this.sellers = sellers;
        this.salesTotal = salesTotal;
        this.unitsSoldTotal = unitsSoldTotal;
        this.turnoverCents = turnoverCents;
        this.salesToday = salesToday;
        this.turnoverTodayCents = turnoverTodayCents;
        this.unpaidSales = unpaidSales;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
