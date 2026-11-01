// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.update.UpdateChecker
package io.nano.nanomarket.update;

import io.nano.nanomarket.NanoMarket;
import io.nano.nanomarket.config.Settings;
import io.nano.nanomarket.config.Settings.Update;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;

public final class UpdateChecker {

    // ---- поля ----
  private final NanoMarket plugin;
  private volatile String latest;

  public UpdateChecker(NanoMarket plugin) { // было: <init>
        super();
        this.plugin = plugin;
    }

  public String latest() {
        return latest;
    }

  public void check() {
        Update cfg = plugin.settings().update();
        if (cfg.enabled() && !cfg.versionUrl().isBlank()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> lambda_check_0(cfg));
            return;
        }
    }

  private void lambda_check_0(Update cfg) { // было: lambda_check_0
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(cfg.versionUrl().trim())).timeout(Duration.ofSeconds(5L)).GET().build();
            HttpResponse response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return;
            }
            String version = ((String) ((String) response.body()).lines().findFirst().orElse("")).trim();
            if (version.isEmpty()) {
                return;
            } else {
                latest = version;
                String current = plugin.getDescription().getVersion();
                if (!version.equals(current)) {
                    if (cfg.downloadUrl().isBlank()) {
                        /* нередуцируемый переход -> 182 */
                    } else {
                        plugin.getLogger().warning("Доступна новая версия NanoMarket: " + version + " (установлена " + current + ")" + " — " + cfg.downloadUrl());
                    }
                }
            }
            return;
        } catch (Exception client) {
            plugin.getLogger().fine("Проверка обновлений не удалась: " + client.getMessage());
        }
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
