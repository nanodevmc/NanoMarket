// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.storage.Schema
package io.nano.nanomarket.storage;

import io.nano.nanomarket.storage.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class Schema {

    // ---- поля ----
  public static final int VERSION = 2;

  private Schema() { // было: <init>
        super();
    }

  public static void create(Database db) {
        db.transaction(lp0 -> lambda_create_0(db, lp0));
    }

  private static Object lambda_create_0(Database db, Connection connection) { // было: lambda_create_0
        boolean mysql = db.isMysql();
        String lots = db.table("lots");
        String sales = db.table("sales");
        String meta = db.table("meta");
        String string = mysql ? "BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY" : "INTEGER PRIMARY KEY AUTOINCREMENT";
        String longText = mysql ? "MEDIUMTEXT" : "TEXT";
        String tail = mysql ? " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4" : "";
        String lotKeys = mysql ? ", KEY idx_" + lots + "_seller (seller_uuid), KEY idx_" + lots + "_active (cancelled, sold), KEY idx_" + lots + "_material (material)" : "";
        String saleKeys = mysql ? ", KEY idx_" + sales + "_seller (seller_uuid, paid), KEY idx_" + sales + "_buyer (buyer_uuid), KEY idx_" + sales + "_time (created_at)" : "";
        Statement select = connection.createStatement();
        try {
            select.execute("CREATE TABLE IF NOT EXISTS " + lots + " (id " + string + ", seller_uuid VARCHAR(36) NOT NULL, seller_name VARCHAR(32) NOT NULL, material VARCHAR(64) NOT NULL, item_data " + longText + " NOT NULL, search_text VARCHAR(512) NOT NULL, name_key VARCHAR(128) NOT NULL, amount INT NOT NULL, sold INT NOT NULL DEFAULT 0, unit_cents BIGINT NOT NULL, created_at BIGINT NOT NULL, server_id VARCHAR(64) NOT NULL, cancelled TINYINT NOT NULL DEFAULT 0" + lotKeys + ")" + tail);
            select.execute("CREATE TABLE IF NOT EXISTS " + sales + " (id " + string + ", lot_id BIGINT NOT NULL, buyer_uuid VARCHAR(36) NOT NULL, buyer_name VARCHAR(32) NOT NULL, seller_uuid VARCHAR(36) NOT NULL, seller_name VARCHAR(32) NOT NULL, material VARCHAR(64) NOT NULL, amount INT NOT NULL, unit_cents BIGINT NOT NULL, total_cents BIGINT NOT NULL, created_at BIGINT NOT NULL, server_id VARCHAR(64) NOT NULL, paid TINYINT NOT NULL DEFAULT 0, paid_at BIGINT NOT NULL DEFAULT 0" + saleKeys + ")" + tail);
            select.execute("CREATE TABLE IF NOT EXISTS " + meta + " (k VARCHAR(64) NOT NULL PRIMARY KEY, v VARCHAR(255) NOT NULL)" + tail);
            if (!mysql) {
                select.execute("CREATE INDEX IF NOT EXISTS idx_" + lots + "_seller ON " + lots + "(seller_uuid)");
                select.execute("CREATE INDEX IF NOT EXISTS idx_" + lots + "_active ON " + lots + "(cancelled, sold)");
                select.execute("CREATE INDEX IF NOT EXISTS idx_" + lots + "_material ON " + lots + "(material)");
                select.execute("CREATE INDEX IF NOT EXISTS idx_" + sales + "_seller ON " + sales + "(seller_uuid, paid)");
                select.execute("CREATE INDEX IF NOT EXISTS idx_" + sales + "_buyer ON " + sales + "(buyer_uuid)");
                select.execute("CREATE INDEX IF NOT EXISTS idx_" + sales + "_time ON " + sales + "(created_at)");
            }
        } catch (Throwable e2) {
            if (select != null) {
                try {
                    select.close();
                } catch (Throwable insert) {
                    e2.addSuppressed(insert);
                }
            }
        }
        if (select != null) {
            select.close();
        }
        select = connection.prepareStatement("SELECT v FROM " + meta + " WHERE k = ?");
        try {
            select.setString(1, "schema_version");
            ResultSet t = select.executeQuery();
            try {
                if (!t.next()) {
                    PreparedStatement insert = connection.prepareStatement("INSERT INTO " + meta + " (k, v) VALUES (?, ?)");
                    try {
                        insert.setString(1, "schema_version");
                        insert.setString(2, Integer.toString(2));
                        insert.executeUpdate();
                    } catch (Throwable e4) {
                        if (insert == null) {
                            throw e4;
                        } else {
                            try {
                                insert.close();
                            } catch (Throwable e4) {
                                e4.addSuppressed(e4);
                                throw e4;
                            }
                            throw e4;
                        }
                    }
                    if (insert != null) {
                        insert.close();
                    }
                }
            } catch (Throwable insert) {
                if (t == null) {
                    throw insert;
                } else {
                    try {
                        t.close();
                    } catch (Throwable e6) {
                        insert.addSuppressed(e6);
                        throw insert;
                    }
                    throw insert;
                }
            }
            if (t != null) {
                t.close();
            }
        } catch (Throwable e8) {
            if (select != null) {
                try {
                    select.close();
                } catch (Throwable insert) {
                    e8.addSuppressed(insert);
                }
            }
        }
        if (select != null) {
            select.close();
        }
        return null;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
