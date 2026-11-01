// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.service.MarketLog
package io.nano.nanomarket.service;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Settings;
import io.nano.nanomarket.config.Settings.Logging;
import io.nano.nanomarket.model.Sale;
import io.nano.nanomarket.text.Colors;
import io.nano.nanomarket.util.Money;
import io.nano.nanomarket.util.Times;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MarketLog {

    // ---- поля ----
  private final NanoMarket plugin;

  public MarketLog(NanoMarket plugin) { // было: <init>
        super();
        this.plugin = plugin;
    }

  public void sale(Sale sale, String itemName) {
        Logging cfg = plugin.settings().logging();
        if (!cfg.console() && !cfg.file()) {
            return;
        }
        String line = cfg.format().replace("{time}", Times.full(sale.createdAt())).replace("{buyer}", sale.buyerName()).replace("{seller}", sale.sellerName()).replace("{amount}", Integer.toString(sale.amount())).replace("{item}", Colors.strip(itemName)).replace("{unit}", Money.format(sale.unitCents())).replace("{total}", Money.format(sale.totalCents()));
        if (cfg.console()) {
            plugin.getLogger().info(line);
        }
        if (cfg.file()) {
            append("transactions-" + LocalDate.now() + ".log", line);
        }
    }

  public void critical(String text) {
        plugin.getLogger().severe(text);
        append("errors.log", Times.full(System.currentTimeMillis()) + " " + text);
    }

  private void append(String fileName, String line) {
        File dir = new File(plugin.getDataFolder(), "logs");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return;
            }
            return;
        }
        try {
            BufferedWriter bufferedWriter = Files.newBufferedWriter(new File(dir, fileName).toPath(), StandardCharsets.UTF_8, new OpenOption[]{ StandardOpenOption.CREATE, StandardOpenOption.APPEND });
            try {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            } catch (Throwable e2) {
                if (bufferedWriter == null) {
                    throw e2;
                } else {
                    try {
                        bufferedWriter.close();
                    } catch (Throwable e2) {
                        e2.addSuppressed(e2);
                        throw e2;
                    }
                    throw e2;
                }
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            return;
        } catch (IOException e3) {
            plugin.getLogger().log(Level.WARNING, "Не удалось записать журнал: " + e3.getMessage());
        }
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
