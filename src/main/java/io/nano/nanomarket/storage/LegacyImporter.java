// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.storage.LegacyImporter
package io.nano.nanomarket.storage;

import io.nano.nanomarket.config.ItemCatalog;
import io.nano.nanomarket.model.Lot;
import io.nano.nanomarket.storage.ItemCodec;
import io.nano.nanomarket.storage.LegacyImporter.Result;
import io.nano.nanomarket.storage.LotStore;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class LegacyImporter {

  private LegacyImporter() { // было: <init>
        super();
    }

  public static Result importFrom(File legacyDb, LotStore lots, ItemCodec codec, ItemCatalog catalog, String serverId, Logger log) {
        int imported;
        int skipped;
        if (legacyDb.isFile()) {
            imported = 0;
            skipped = 0;
        } else {
            throw new SQLException("файл не найден: " + legacyDb);
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e1) {
            throw new SQLException("нет драйвера SQLite", e1);
        }
        Connection t = DriverManager.getConnection("jdbc:sqlite:" + legacyDb.getAbsolutePath());
        try {
            PreparedStatement preparedStatement = t.prepareStatement("SELECT seller_uuid, seller_name, item_material, item_data, price_per_unit, total_amount, sold_amount, created_at, server_id FROM market_items WHERE is_sold = 0 AND total_amount > sold_amount");
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                try {
                    while (resultSet.next()) {
                        try {
                            Material material = Material.getMaterial(resultSet.getString("item_material"));
                            if (material != null && !material.isAir()) {
                                ItemStack template = codec.decode(resultSet.getString("item_data"));
                                if (template == null) {
                                    template = new ItemStack(material);
                                }
                                template = template.clone();
                                template.setAmount(1);
                                int amount = resultSet.getInt("total_amount");
                                int sold = resultSet.getInt("sold_amount");
                                long cents = Math.max(1L, Math.round(resultSet.getDouble("price_per_unit") * 100.0));
                                String seller = resultSet.getString("seller_name");
                                Lot lot = new Lot(0L, UUID.fromString(resultSet.getString("seller_uuid")), seller, material.name(), template, amount, sold, cents, parseTime(resultSet.getString("created_at")), serverId, false);
                                lots.insert(lot, catalog.searchText(template, seller), catalog.sortKey(template));
                                ++imported;
                            }
                            ++skipped;
                            continue;
                        } catch (RuntimeException material) {
                            ++skipped;
                            log.warning("Импорт: строка пропущена (" + material.getMessage() + ")");
                            continue;
                        }
                    }
                } catch (Throwable material) {
                    if (resultSet == null) {
                        throw material;
                    } else {
                        try {
                            resultSet.close();
                        } catch (Throwable template) {
                            material.addSuppressed(template);
                            throw material;
                        }
                        throw material;
                    }
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Throwable e6) {
                if (preparedStatement == null) {
                    throw e6;
                } else {
                    try {
                        preparedStatement.close();
                    } catch (Throwable material) {
                        e6.addSuppressed(material);
                        throw e6;
                    }
                    throw e6;
                }
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (Throwable e8) {
            if (t != null) {
                try {
                    t.close();
                } catch (Throwable e8) {
                    e8.addSuppressed(e8);
                }
            }
        }
        if (t != null) {
            t.close();
        }
        return new Result(imported, skipped);
    }

  private static long parseTime(String raw) {
        long temp1;
        try {
            if (raw != null && raw.length() >= 19) {
                temp1 = LocalDateTime.parse(raw.substring(0, 19).replace(' ', 'T')).toInstant(ZoneOffset.UTC).toEpochMilli();
            }
            return System.currentTimeMillis();
        } catch (RuntimeException e1) {
        }
        return temp1;
    }


    // ---- вложенный класс: Result ----
    public static record Result(int imported, int skipped) {
    
      public Result(int imported, int skipped) { // было: <init>
            super();
            this.imported = imported;
            this.skipped = skipped;
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
