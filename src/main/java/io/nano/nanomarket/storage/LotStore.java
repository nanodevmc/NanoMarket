// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.storage.LotStore
package io.nano.nanomarket.storage;

import io.nano.nanomarket.model.Lot;
import io.nano.nanomarket.model.Page;
import io.nano.nanomarket.model.SortMode;
import io.nano.nanomarket.storage.Database;
import io.nano.nanomarket.storage.ItemCodec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class LotStore {

    // ---- поля ----
  private static final String COLUMNS = "id, seller_uuid, seller_name, material, item_data, amount, sold, unit_cents, created_at, server_id, cancelled";
  private static final String ACTIVE = "cancelled = 0 AND sold < amount";
  private final Database db;
  private final ItemCodec codec;
  private final String table;

  public LotStore(Database db, ItemCodec codec) { // было: <init>
        super();
        this.db = db;
        this.codec = codec;
        table = db.table("lots");
    }

  public long insert(Lot lot, String searchText, String nameKey) {
        return db.call(lp0 -> lambda_insert_0(lot, searchText, nameKey, lp0));
    }

  public Lot find(long id) {
        return (Lot) db.call(lp0 -> lambda_find_1(id, lp0));
    }

  public Page<Lot> browse(String query, SortMode sort, int page, int pageSize) {
        List tokens = tokens(query);
        StringBuilder where = new StringBuilder("cancelled = 0 AND sold < amount");
        for (int var7 = 0; var7 < tokens.size(); ++var7) {
            where.append(" AND search_text LIKE ? ESCAPE '!'");
        }
        return (Page) db.call(lp0 -> lambda_browse_2(where, tokens, page, pageSize, sort, lp0));
    }

  public Page<Lot> ofSeller(UUID seller, int page, int pageSize) {
        return (Page) db.call(lp0 -> lambda_ofSeller_3(seller, page, pageSize, lp0));
    }

  public List<Lot> cheapest(String material, long excludeId, int limit) {
        return (List) db.call(lp0 -> lambda_cheapest_4(material, excludeId, limit, lp0));
    }

  public int countActive(UUID seller) {
        return db.call(lp0 -> lambda_countActive_5(seller, lp0));
    }

  public boolean reserve(long id, int units) {
        return db.call(lp0 -> lambda_reserve_6(units, id, lp0));
    }

  public void release(long id, int units) {
        db.call(lp0 -> lambda_release_7(units, id, lp0));
    }

  public OptionalInt cancel(long id, UUID seller) {
        return (OptionalInt) db.transaction(lp0 -> lambda_cancel_8(id, seller, lp0));
    }

  public int purgeAll() {
        return db.call(lp0 -> lambda_purgeAll_9(lp0));
    }

  static List<String> tokens(String query) {
        ArrayList out = new ArrayList();
        int n;
        int n2;
        String[] string[];
        if (query != null) {
            string[] = query.toLowerCase(Locale.ROOT).replace('_', ' ').trim().split("\\s+");
            n = string[].length;
            n2 = 0;
        } else {
            return out;
        }
        for (; n2 < n; ++n2) {
            String part = string[][n2];
            if (!part.isEmpty() && out.size() < 6) {
                out.add(part);
            }
        }
        return out;
    }

  static String escape(String s) {
        return s.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }

  private int count(Connection connection, String where, List<String> tokens, String seller) {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE " + where);
        int idx;
        try {
            idx = 1;
            Iterator iterator = tokens.iterator();
            String string;
            while (iterator.hasNext()) {
                string = (String) iterator.next();
                preparedStatement.setString(idx++, "%" + escape(string) + "%");
            }
            if (seller != null) {
                preparedStatement.setString(idx, seller);
            }
            iterator = preparedStatement.executeQuery();
            try {
                string = iterator.next() && iterator.getInt(1);
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
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            return string;
        } catch (Throwable idx) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e4) {
                    idx.addSuppressed(e4);
                }
            }
        }
        /* нередуцируемый переход -> 178 */
        throw idx;
    }

  private void readAll(PreparedStatement preparedStatement, List<Lot> into) {
        ResultSet resultSet = preparedStatement.executeQuery();
        try {
            while (resultSet.next()) {
                Lot lot = map(resultSet);
                if (lot != null) {
                    into.add(lot);
                }
            }
        } catch (Throwable lot) {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Throwable e2) {
                    lot.addSuppressed(e2);
                }
            }
        }
        if (resultSet != null) {
            resultSet.close();
        }
    }

  private Lot map(ResultSet resultSet) {
        String material = resultSet.getString("material");
        ItemStack template = codec.decode(resultSet.getString("item_data"));
        if (template == null) {
            Material material1 = Material.getMaterial(material);
            if (material1 != null && !material1.isAir()) {
                template = new ItemStack(material1);
            }
            return null;
        }
        template = template.clone();
        template.setAmount(1);
        Lot temp25 = new Lot();
        return temp25;
    }

  private Integer lambda_purgeAll_9(Connection connection) { // было: lambda_purgeAll_9
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

  private OptionalInt lambda_cancel_8(long id, UUID seller, Connection connection) { // было: lambda_cancel_8
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + table + " SET cancelled = 1 WHERE id = ? AND seller_uuid = ? AND cancelled = 0 AND sold < amount");
        OptionalInt optionalInt;
        try {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, seller.toString());
            if (preparedStatement.executeUpdate() != 1) {
                optionalInt = OptionalInt.empty();
            } else if (preparedStatement != null) {
                preparedStatement.close();
            }
            preparedStatement = connection.prepareStatement("SELECT amount - sold FROM " + table + " WHERE id = ?");
            try {
                preparedStatement.setLong(1, id);
                optionalInt = preparedStatement.executeQuery();
                OptionalInt t;
                try {
                    t = optionalInt.next() ? OptionalInt.of(optionalInt.getInt(1)) : OptionalInt.empty();
                } catch (Throwable e2) {
                    if (optionalInt == null) {
                        throw e2;
                    } else {
                        try {
                            optionalInt.close();
                        } catch (Throwable e2) {
                            e2.addSuppressed(e2);
                            throw e2;
                        }
                        throw e2;
                    }
                }
                if (optionalInt != null) {
                    optionalInt.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                return t;
            } catch (Throwable e4) {
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (Throwable e4) {
                        e4.addSuppressed(e4);
                    }
                }
            }
            /* нередуцируемый переход -> 232 */
        } catch (Throwable e6) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e6) {
                    e6.addSuppressed(e6);
                }
            }
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return optionalInt;
    }

  private Integer lambda_release_7(int units, long id, Connection connection) { // было: lambda_release_7
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + table + " SET sold = sold - ? WHERE id = ? AND sold >= ?");
        Integer integer;
        try {
            preparedStatement.setInt(1, units);
            preparedStatement.setLong(2, id);
            preparedStatement.setInt(3, units);
            integer = Integer.valueOf(preparedStatement.executeUpdate());
        } catch (Throwable e2) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e2) {
                    e2.addSuppressed(e2);
                }
            }
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return integer;
    }

  private Boolean lambda_reserve_6(int units, long id, Connection connection) { // было: lambda_reserve_6
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + table + " SET sold = sold + ? WHERE id = ? AND cancelled = 0 AND amount - sold >= ?");
        Boolean boolean;
        try {
            preparedStatement.setInt(1, units);
            preparedStatement.setLong(2, id);
            preparedStatement.setInt(3, units);
            boolean = Boolean.valueOf(preparedStatement.executeUpdate() == 1);
        } catch (Throwable e2) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e2) {
                    e2.addSuppressed(e2);
                }
            }
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return boolean;
    }

  private Integer lambda_countActive_5(UUID seller, Connection connection) { // было: lambda_countActive_5
        return Integer.valueOf(count(connection, "cancelled = 0 AND sold < amount AND seller_uuid = ?", List.of(), seller.toString()));
    }

  private List lambda_cheapest_4(String material, long excludeId, int limit, Connection connection) { // было: lambda_cheapest_4
        ArrayList items = new ArrayList();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, seller_uuid, seller_name, material, item_data, amount, sold, unit_cents, created_at, server_id, cancelled FROM " + table + " WHERE cancelled = 0 AND sold < amount AND material = ? AND id <> ? ORDER BY unit_cents ASC, id ASC LIMIT ?");
        try {
            preparedStatement.setString(1, material);
            preparedStatement.setLong(2, excludeId);
            preparedStatement.setInt(3, limit);
            readAll(preparedStatement, items);
        } catch (Throwable e2) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e2) {
                    e2.addSuppressed(e2);
                }
            }
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return items;
    }

  private Page lambda_ofSeller_3(UUID seller, int page, int pageSize, Connection connection) { // было: lambda_ofSeller_3
        String where = "cancelled = 0 AND sold < amount AND seller_uuid = ?";
        int total = count(connection, where, List.of(), seller.toString());
        int safePage = Page.clampPage(page, total, pageSize);
        ArrayList items = new ArrayList();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, seller_uuid, seller_name, material, item_data, amount, sold, unit_cents, created_at, server_id, cancelled FROM " + table + " WHERE " + where + " ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?");
        try {
            preparedStatement.setString(1, seller.toString());
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, (safePage - 1) * pageSize);
            readAll(preparedStatement, items);
        } catch (Throwable e2) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e2) {
                    e2.addSuppressed(e2);
                }
            }
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return new Page(items, safePage, pageSize, total);
    }

  private Page lambda_browse_2(StringBuilder where, List tokens, int page, int pageSize, SortMode sort, Connection connection) { // было: lambda_browse_2
        int total = count(connection, where.toString(), tokens, null);
        int safePage = Page.clampPage(page, total, pageSize);
        String sql = "SELECT id, seller_uuid, seller_name, material, item_data, amount, sold, unit_cents, created_at, server_id, cancelled FROM " + table + " WHERE " + where + " ORDER BY " + sort.orderBy() + " LIMIT ? OFFSET ?";
        ArrayList items = new ArrayList();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            int idx = 1;
            Iterator iterator = tokens.iterator();
            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                preparedStatement.setString(idx++, "%" + escape(string) + "%");
            }
            preparedStatement.setInt(idx++, pageSize);
            preparedStatement.setInt(idx, (safePage - 1) * pageSize);
            readAll(preparedStatement, items);
        } catch (Throwable idx) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e2) {
                    idx.addSuppressed(e2);
                }
            }
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        return new Page(items, safePage, pageSize, total);
    }

  private Lot lambda_find_1(long id, Connection connection) { // было: lambda_find_1
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, seller_uuid, seller_name, material, item_data, amount, sold, unit_cents, created_at, server_id, cancelled FROM " + table + " WHERE id = ?");
        ResultSet resultSet;
        try {
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();
            Lot t;
            try {
                t = resultSet.next() ? map(resultSet) : null;
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
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            return (Lot) t;
        } catch (Throwable e4) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e4) {
                    e4.addSuppressed(e4);
                }
            }
        }
        /* нередуцируемый переход -> 110 */
        throw resultSet;
    }

  private Long lambda_insert_0(Lot lot, String searchText, String nameKey, Connection connection) { // было: lambda_insert_0
        String sql = "INSERT INTO " + table + " (seller_uuid, seller_name, material, item_data, search_text, name_key, amount, sold, unit_cents, created_at, server_id, cancelled) VALUES (?,?,?,?,?,?,?,?,?,?,?,0)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, 1);
        ResultSet keys;
        try {
            preparedStatement.setString(1, lot.sellerId().toString());
            preparedStatement.setString(2, lot.sellerName());
            preparedStatement.setString(3, lot.material());
            preparedStatement.setString(4, codec.encode(lot.template()));
            preparedStatement.setString(5, searchText);
            preparedStatement.setString(6, nameKey);
            preparedStatement.setInt(7, lot.amount());
            preparedStatement.setInt(8, lot.sold());
            preparedStatement.setLong(9, lot.unitCents());
            preparedStatement.setLong(10, lot.createdAt());
            preparedStatement.setString(11, lot.serverId());
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
                    /* нередуцируемый переход -> 278 */
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
        /* нередуцируемый переход -> 275 */
        throw keys;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
