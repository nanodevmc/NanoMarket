// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.storage.SaleStore
package io.nano.nanomarket.storage;

import io.nano.nanomarket.model.MarketStats;
import io.nano.nanomarket.model.Sale;
import io.nano.nanomarket.storage.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SaleStore {

    // ---- поля ----
  private static final String COLUMNS = "id, lot_id, buyer_uuid, buyer_name, seller_uuid, seller_name, material, amount, unit_cents, total_cents, created_at, server_id, paid, paid_at";
  private final Database db;
  private final String table;
  private final String lots;

  public SaleStore(Database db) { // было: <init>
        super();
        this.db = db;
        table = db.table("sales");
        lots = db.table("lots");
    }

  public long insert(Sale sale) {
        return db.call(lp0 -> lambda_insert_0(sale, lp0));
    }

  public List<Sale> claimUnpaid(Collection<UUID> sellers, long now) {
        return sellers.isEmpty() ? List.of() : (List) db.transaction(lp0 -> lambda_claimUnpaid_1(sellers, now, lp0));
    }

  public void unclaim(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return;
        }
        db.transaction(lp0 -> lambda_unclaim_2(ids, lp0));
    }

  public Map<UUID, Integer> pendingCounts(Collection<UUID> sellers) {
        return sellers.isEmpty() ? Map.of() : (Map) db.call(lp0 -> lambda_pendingCounts_3(sellers, lp0));
    }

  public List<Sale> recentSold(UUID seller, int limit) {
        return recent("seller_uuid", seller, limit);
    }

  public List<Sale> recentBought(UUID buyer, int limit) {
        return recent("buyer_uuid", buyer, limit);
    }

  private List<Sale> recent(String column, UUID who, int limit) {
        return (List) db.call(lp0 -> lambda_recent_4(column, who, limit, lp0));
    }

  public MarketStats stats(long startOfToday) {
        return (MarketStats) db.call(lp0 -> lambda_stats_5(startOfToday, lp0));
    }

  public int purgeAll() {
        return db.call(lp0 -> lambda_purgeAll_6(lp0));
    }

  private static Sale map(ResultSet resultSet) {
        Sale temp29 = new Sale();
        return temp29;
    }

  private Integer lambda_purgeAll_6(Connection connection) { // было: lambda_purgeAll_6
        Statement statement = connection.createStatement();
        Integer integer;
        try {
            integer = Integer.valueOf(statement.executeUpdate("DELETE FROM " + table));
        } catch (Throwable e2) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Throwable e2) {
                    e2.addSuppressed(e2);
                }
            }
        }
        if (statement != null) {
            statement.close();
        }
        return integer;
    }

  private MarketStats lambda_stats_5(long startOfToday, Connection connection) { // было: lambda_stats_5
        PreparedStatement salesTotal = connection.prepareStatement("SELECT COUNT(*), COALESCE(SUM(amount - sold), 0), COUNT(DISTINCT seller_uuid) FROM " + lots + " WHERE cancelled = 0 AND sold < amount");
        int activeLots;
        long activeUnits;
        int sellers;
        ResultSet unitsSold;
        try {
            unitsSold = salesTotal.executeQuery();
            try {
                unitsSold.next();
                activeLots = unitsSold.getInt(1);
                activeUnits = unitsSold.getLong(2);
                sellers = unitsSold.getInt(3);
            } catch (Throwable e2) {
                if (unitsSold == null) {
                    throw e2;
                } else {
                    try {
                        unitsSold.close();
                    } catch (Throwable turnover) {
                        e2.addSuppressed(turnover);
                        throw e2;
                    }
                    throw e2;
                }
            }
            if (unitsSold != null) {
                unitsSold.close();
            }
        } catch (Throwable unitsSold) {
            if (salesTotal != null) {
                try {
                    salesTotal.close();
                } catch (Throwable e4) {
                    unitsSold.addSuppressed(e4);
                }
            }
        }
        if (salesTotal != null) {
            salesTotal.close();
        }
        PreparedStatement salesToday = connection.prepareStatement("SELECT COUNT(*), COALESCE(SUM(amount), 0), COALESCE(SUM(total_cents), 0), COALESCE(SUM(CASE WHEN paid = 0 THEN 1 ELSE 0 END), 0) FROM " + table);
        long turnover;
        ResultSet turnoverToday;
        int unpaid;
        try {
            turnoverToday = salesToday.executeQuery();
            try {
                turnoverToday.next();
                salesTotal = turnoverToday.getInt(1);
                unitsSold = turnoverToday.getLong(2);
                turnover = turnoverToday.getLong(3);
                unpaid = turnoverToday.getInt(4);
            } catch (Throwable e6) {
                if (turnoverToday == null) {
                    throw e6;
                } else {
                    try {
                        turnoverToday.close();
                    } catch (Throwable e6) {
                        e6.addSuppressed(e6);
                        throw e6;
                    }
                    throw e6;
                }
            }
            if (turnoverToday != null) {
                turnoverToday.close();
            }
        } catch (Throwable turnoverToday) {
            if (salesToday != null) {
                try {
                    salesToday.close();
                } catch (Throwable e8) {
                    turnoverToday.addSuppressed(e8);
                }
            }
        }
        if (salesToday != null) {
            salesToday.close();
        }
        PreparedStatement t = connection.prepareStatement("SELECT COUNT(*), COALESCE(SUM(total_cents), 0) FROM " + table + " WHERE created_at >= ?");
        try {
            t.setLong(1, startOfToday);
            ResultSet resultSet = t.executeQuery();
            try {
                resultSet.next();
                salesToday = resultSet.getInt(1);
                turnoverToday = resultSet.getLong(2);
            } catch (Throwable e10) {
                if (resultSet == null) {
                    throw e10;
                } else {
                    try {
                        resultSet.close();
                    } catch (Throwable e10) {
                        e10.addSuppressed(e10);
                        throw e10;
                    }
                    throw e10;
                }
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Throwable e12) {
            if (t != null) {
                try {
                    t.close();
                } catch (Throwable e12) {
                    e12.addSuppressed(e12);
                }
            }
        }
        if (t != null) {
            t.close();
        }
        return new MarketStats(activeLots, activeUnits, sellers, salesTotal, unitsSold, turnover, salesToday, turnoverToday, unpaid);
    }

  private List lambda_recent_4(String column, UUID who, int limit, Connection connection) { // было: lambda_recent_4
        ArrayList out = new ArrayList();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, lot_id, buyer_uuid, buyer_name, seller_uuid, seller_name, material, amount, unit_cents, total_cents, created_at, server_id, paid, paid_at FROM " + table + " WHERE " + column + " = ? ORDER BY id DESC LIMIT ?");
        try {
            preparedStatement.setString(1, who.toString());
            preparedStatement.setInt(2, limit);
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                while (resultSet.next()) {
                    out.add(map(resultSet));
                }
            } catch (Throwable e2) {
                if (resultSet == null) {
                    throw e2;
                } else {
                    try {
                        resultSet.close();
                    } catch (Throwable e2) {
                        e2.addSuppressed(e2);
                        throw e2;
                    }
                    throw e2;
                }
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Throwable e4) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e4) {
                    e4.addSuppressed(e4);
                }
            }
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return out;
    }

  private Map lambda_pendingCounts_3(Collection sellers, Connection connection) { // было: lambda_pendingCounts_3
        String in = "?,".repeat(sellers.size());
        in = in.substring(0, in.length() - 1);
        HashMap out = new HashMap();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT seller_uuid, COUNT(*) FROM " + table + " WHERE paid = 0 AND seller_uuid IN (" + in + ") GROUP BY seller_uuid");
        try {
            int n = 1;
            Iterator iterator = sellers.iterator();
            while (iterator.hasNext()) {
                UUID uuid = (UUID) iterator.next();
                preparedStatement.setString(n++, uuid.toString());
            }
            iterator = preparedStatement.executeQuery();
            try {
                while (iterator.next()) {
                    out.put(UUID.fromString(iterator.getString(1)), Integer.valueOf(iterator.getInt(2)));
                }
            } catch (Throwable e2) {
                if (iterator == null) {
                    throw e2;
                } else {
                    try {
                        iterator.close();
                    } catch (Throwable e2) {
                        e2.addSuppressed(e2);
                        throw e2;
                    }
                    throw e2;
                }
            }
            if (iterator != null) {
                iterator.close();
            }
        } catch (Throwable e4) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e4) {
                    e4.addSuppressed(e4);
                }
            }
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return out;
    }

  private Object lambda_unclaim_2(Collection ids, Connection connection) { // было: lambda_unclaim_2
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + table + " SET paid = 0, paid_at = 0 WHERE id = ?");
        try {
            Iterator iterator = ids.iterator();
            while (iterator.hasNext()) {
                long id = iterator.next();
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
            }
        } catch (Throwable e2) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable id) {
                    e2.addSuppressed(id);
                }
            }
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return null;
    }

  private List lambda_claimUnpaid_1(Collection sellers, long now, Connection connection) { // было: lambda_claimUnpaid_1
        String in = "?,".repeat(sellers.size());
        in = in.substring(0, in.length() - 1);
        ArrayList candidates = new ArrayList();
        PreparedStatement claimed = connection.prepareStatement("SELECT id, lot_id, buyer_uuid, buyer_name, seller_uuid, seller_name, material, amount, unit_cents, total_cents, created_at, server_id, paid, paid_at FROM " + table + " WHERE paid = 0 AND seller_uuid IN (" + in + ") ORDER BY id ASC");
        Iterator iterator;
        int up;
        UUID uuid;
        try {
            up = 1;
            iterator = sellers.iterator();
            while (iterator.hasNext()) {
                uuid = (UUID) iterator.next();
                claimed.setString(up++, uuid.toString());
            }
            iterator = claimed.executeQuery();
            try {
                while (iterator.next()) {
                    candidates.add(map(iterator));
                }
            } catch (Throwable e2) {
                if (iterator == null) {
                    throw e2;
                } else {
                    try {
                        iterator.close();
                    } catch (Throwable e2) {
                        e2.addSuppressed(e2);
                        throw e2;
                    }
                    throw e2;
                }
            }
            if (iterator != null) {
                iterator.close();
            }
        } catch (Throwable up) {
            if (claimed != null) {
                try {
                    claimed.close();
                } catch (Throwable e4) {
                    up.addSuppressed(e4);
                }
            }
        }
        if (claimed != null) {
            claimed.close();
        }
        claimed = new ArrayList();
        PreparedStatement up = connection.prepareStatement("UPDATE " + table + " SET paid = 1, paid_at = ? WHERE id = ? AND paid = 0");
        try {
            Iterator iterator = candidates.iterator();
            while (iterator.hasNext()) {
                Sale uuid = (Sale) iterator.next();
                up.setLong(1, now);
                up.setLong(2, uuid.id());
                if (up.executeUpdate() == 1) {
                    claimed.add(uuid);
                }
            }
        } catch (Throwable e6) {
            if (up != null) {
                try {
                    up.close();
                } catch (Throwable e6) {
                    e6.addSuppressed(e6);
                }
            }
        }
        if (up != null) {
            up.close();
        }
        return claimed;
    }

  private Long lambda_insert_0(Sale sale, Connection connection) { // было: lambda_insert_0
        int temp3;
        int temp4;
        PreparedStatement temp5;
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + table + " (lot_id, buyer_uuid, buyer_name, seller_uuid, seller_name, material, amount, unit_cents, total_cents, created_at, server_id, paid, paid_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
        ResultSet keys;
        try {
            preparedStatement.setLong(1, sale.lotId());
            preparedStatement.setString(2, sale.buyerId().toString());
            preparedStatement.setString(3, sale.buyerName());
            preparedStatement.setString(4, sale.sellerId().toString());
            preparedStatement.setString(5, sale.sellerName());
            preparedStatement.setString(6, sale.material());
            preparedStatement.setInt(7, sale.amount());
            preparedStatement.setLong(8, sale.unitCents());
            preparedStatement.setLong(9, sale.totalCents());
            preparedStatement.setLong(10, sale.createdAt());
            preparedStatement.setString(11, sale.serverId());
            if (sale.paid()) {
                temp3 = 1;
                temp4 = 12;
                temp5 = preparedStatement;
            } else {
                temp3 = 0;
                temp4 = 12;
                temp5 = preparedStatement;
            }
            temp5.setInt(temp4, temp3);
            preparedStatement.setLong(13, sale.paidAt());
            preparedStatement.executeUpdate();
            keys = preparedStatement.getGeneratedKeys();
            Long long;
            try {
                if (keys.next()) {
                    long = Long.valueOf(keys.getLong(1));
                } else if (keys == null) {
                    throw new SQLException("no generated key");
                } else {
                    keys.close();
                    /* нередуцируемый переход -> 289 */
                }
            } catch (Throwable e2) {
                if (keys == null) {
                    throw e2;
                } else {
                    try {
                        keys.close();
                    } catch (Throwable e2) {
                        e2.addSuppressed(e2);
                        throw e2;
                    }
                    throw e2;
                }
            }
            if (keys != null) {
                keys.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            return long;
        } catch (Throwable keys) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e4) {
                    keys.addSuppressed(e4);
                }
            }
        }
        /* нередуцируемый переход -> 286 */
        throw keys;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
