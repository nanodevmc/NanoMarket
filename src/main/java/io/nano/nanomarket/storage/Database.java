// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.storage.Database
package io.nano.nanomarket.storage;

import io.nano.nanomarket.config.Settings.Db;
import io.nano.nanomarket.config.Settings.DbType;
import io.nano.nanomarket.storage.Database.Work;
import io.nano.nanomarket.storage.DatabaseException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Database implements AutoCloseable {

    // ---- поля ----
  private final Db cfg;
  private final File dataFolder;
  private final Logger log;
  private final Object lock;
  private final ExecutorService worker;
  private Connection connection;
  private volatile boolean closed;

  public Database(Db cfg, File dataFolder, Logger log) { // было: <init>
        super();
        lock = new Object();
        this.cfg = cfg;
        this.dataFolder = dataFolder;
        this.log = log;
        worker = Executors.newSingleThreadExecutor(lp0 -> lambda_new_0(lp0));
    }

  public boolean isMysql() {
        return cfg.type() == DbType.MYSQL;
    }

  public String describe() {
        return isMysql() ? "MySQL " + cfg.host() + ":" + cfg.port() + "/" + cfg.name() : "SQLite " + cfg.sqliteFile();
    }

  public String table(String base) {
        return cfg.tablePrefix() + base;
    }

  public void connect() {
        synchronized (lock) {
            Throwable var2;
            try {
                open();
                return;
            } catch (Throwable e1) {
                while (true) {
                    var2 = e1;
                }
            }
            throw var2;
        }
    }

  private void open() {
        closeQuietly();
        try {
            if (isMysql()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://" + cfg.host() + ":" + cfg.port() + "/" + cfg.name() + "?useSSL=" + cfg.useSsl() + "&useUnicode=true&characterEncoding=UTF-8&createDatabaseIfNotExist=true&autoReconnect=true&connectTimeout=5000&socketTimeout=30000&serverTimezone=UTC";
                connection = DriverManager.getConnection(url, cfg.user(), cfg.password());
            } else {
                Class.forName("org.sqlite.JDBC");
                if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                    throw new SQLException("cannot create " + dataFolder);
                }
                File url = new File(dataFolder, cfg.sqliteFile());
                connection = DriverManager.getConnection("jdbc:sqlite:" + url.getAbsolutePath());
                Statement statement = connection.createStatement();
                try {
                    statement.execute("PRAGMA journal_mode=WAL");
                    statement.execute("PRAGMA synchronous=NORMAL");
                    statement.execute("PRAGMA busy_timeout=5000");
                } catch (Throwable e2) {
                    if (statement == null) {
                        throw e2;
                    } else {
                        try {
                            statement.close();
                        } catch (Throwable e2) {
                            e2.addSuppressed(e2);
                            throw e2;
                        }
                        throw e2;
                    }
                }
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (ClassNotFoundException url) {
            throw new SQLException("JDBC driver not found: " + url.getMessage(), url);
        }
    }

  private Connection ensure() {
        if (closed) {
            throw new SQLException("database is closed");
        }
        if (connection == null) {
            log.warning("Соединение с базой данных потеряно — переподключаюсь...");
            open();
        } else if (connection.isClosed()) {
            log.warning("Соединение с базой данных потеряно — переподключаюсь...");
            open();
        } else if (isMysql() && !connection.isValid(2)) {
            log.warning("Соединение с базой данных потеряно — переподключаюсь...");
            open();
        }
        return connection;
    }

  public <T> T call(Work<T> work) {
        Object temp1;
        synchronized (lock) {
            try {
                temp1 = work.run(ensure());
            } catch (SQLException e1) {
                throw new DatabaseException(e1.getMessage(), e1);
            }
        }
        return temp1;
    }

  public <T> T transaction(Work<T> work) {
        synchronized (lock) {
            Throwable var9;
            try {
                Object var3 = null;
                RuntimeException rollbackError;
                try {
                    var3 = ensure();
                    var3.setAutoCommit(false);
                    Object result = work.run(var3);
                    var3.commit();
                    rollbackError = result;
                } catch (SQLException result) {
                    if (var3 != null) {
                        try {
                            var3.rollback();
                        } catch (SQLException rollbackError) {
                            log.log(Level.WARNING, "rollback failed", (Throwable) rollbackError);
                            if (result instanceof RuntimeException) {
                                rollbackError = (RuntimeException) result;
                                throw rollbackError;
                            } else {
                                throw new DatabaseException(result.getMessage(), (Throwable) result);
                            }
                        }
                        if (result instanceof RuntimeException) {
                            rollbackError = (RuntimeException) result;
                            throw rollbackError;
                        } else {
                            throw new DatabaseException(result.getMessage(), (Throwable) result);
                        }
                    } else if (result instanceof RuntimeException) {
                        rollbackError = (RuntimeException) result;
                        throw rollbackError;
                    } else {
                        throw new DatabaseException(result.getMessage(), (Throwable) result);
                    }
                }
                if (var3 != null) {
                    try {
                        var3.setAutoCommit(true);
                    } catch (SQLException e3) {
                    }
                }
                return rollbackError;
            } catch (Throwable e4) {
                while (true) {
                    var9 = e4;
                }
            }
            throw var9;
        }
    }

  public <T> CompletableFuture<T> async(Work<T> work) {
        return CompletableFuture.supplyAsync(() -> lambda_async_1(work), worker);
    }

  public <T> CompletableFuture<T> asyncTx(Work<T> work) {
        return CompletableFuture.supplyAsync(() -> lambda_asyncTx_2(work), worker);
    }

  public <T> CompletableFuture<T> submit(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, worker);
    }

  public boolean isConnected() {
        boolean temp1;
        synchronized (lock) {
            try {
                temp1 = !closed && connection != null && !connection.isClosed();
            } catch (SQLException e1) {
                return false;
            }
        }
        return temp1;
    }

  public void close() {
        closed = true;
        worker.shutdown();
        try {
            if (!worker.awaitTermination(5L, TimeUnit.SECONDS)) {
                worker.shutdownNow();
            }
            synchronized (lock) {
                Throwable var2;
                try {
                    closeQuietly();
                    return;
                } catch (Throwable e1) {
                    while (true) {
                        var2 = e1;
                    }
                }
                throw var2;
            }
        } catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            worker.shutdownNow();
        }
        synchronized (lock) {
            closeQuietly();
        }
        /* нередуцируемый переход -> 83 */
    }

  private void closeQuietly() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e1) {
            }
            connection = null;
        }
    }

  private Object lambda_asyncTx_2(Work work) { // было: lambda_asyncTx_2
        return transaction(work);
    }

  private Object lambda_async_1(Work work) { // было: lambda_async_1
        return call(work);
    }

  private static Thread lambda_new_0(Runnable runnable) { // было: lambda_new_0
        Thread thread = new Thread(runnable, "NanoMarket-DB");
        thread.setDaemon(true);
        return thread;
    }


    // ---- вложенный класс: Work ----
    @FunctionalInterface
    public interface Work<T> {
    
      public T run(Connection connection);
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
