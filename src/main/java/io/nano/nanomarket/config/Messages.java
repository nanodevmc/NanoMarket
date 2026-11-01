// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.config.Messages
package io.nano.nanomarket.config;

import io.nano.nanomarket.text.Styler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Messages {

    // ---- поля ----
  private final File file;
  private final Supplier<InputStream> bundled;
  private final Supplier<Styler> styler;
  private final Logger log;
  private final Set<String> reportedMissing;
  private volatile YamlConfiguration yaml;

  public Messages(File file, Supplier<InputStream> bundled, Supplier<Styler> styler, Logger log) { // было: <init>
        super();
        reportedMissing = new HashSet();
        yaml = new YamlConfiguration();
        this.file = file;
        this.bundled = bundled;
        this.styler = styler;
        this.log = log;
    }

  public void reload() {
        YamlConfiguration loaded = new YamlConfiguration();
        InputStream in;
        try {
            if (file.exists()) {
                loaded.load(file);
            }
            try {
                in = (InputStream) bundled.get();
                try {
                    if (in != null) {
                        loaded.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8)));
                    }
                    reportedMissing.clear();
                    yaml = loaded;
                    return;
                } catch (Throwable e2) {
                    if (in == null) {
                        throw e2;
                    } else {
                        try {
                            in.close();
                        } catch (Throwable e2) {
                            e2.addSuppressed(e2);
                            throw e2;
                        }
                        throw e2;
                    }
                }
            } catch (IOException in) {
                log.warning("Не удалось прочитать встроенный messages.yml: " + in.getMessage());
            }
            reportedMissing.clear();
            yaml = loaded;
            return;
        } catch (IOException in) {
            log.warning("messages.yml не читается (" + in.getMessage() + ") — используются встроенные тексты.");
        }
        InputStream in = (InputStream) bundled.get();
        if (in != null) {
            loaded.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8)));
            if (in != null) {
                in.close();
            }
        } else if (in != null) {
            in.close();
        }
        reportedMissing.clear();
        yaml = loaded;
    }

  public String raw(String key) {
        String value = yaml.getString(key);
        return value != null ? value : "";
    }

  public List<String> templates(String key) {
        if (yaml.isList(key)) {
            return yaml.getStringList(key);
        }
        String single = yaml.getString(key);
        if (single != null) {
            return List.of(single);
        }
        reportMissing(key);
        return List.of("[" + key + "]");
    }

  public String template(String key) {
        String value = yaml.getString(key);
        if (value != null) {
            return value;
        }
        reportMissing(key);
        return "[" + key + "]";
    }

  public String text(String key, Object[] obj) {
        return ((Styler) styler.get()).render(template(key), obj);
    }

  public List<String> lines(String key, Object[] obj) {
        Styler styler = (Styler) styler.get();
        ArrayList out = new ArrayList();
        Iterator iterator = templates(key).iterator();
        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            out.add(styler.render(string, obj));
        }
        return out;
    }

  public void send(CommandSender to, String key, Object[] obj) {
        if (to != null) {
            to.sendMessage(text("prefix", new Object[0]) + text(key, obj));
            return;
        }
    }

  public void sendLines(CommandSender to, String key, Object[] obj) {
        Iterator iterator;
        if (to != null) {
            iterator = lines(key, obj).iterator();
        } else {
            return;
        }
        while (iterator.hasNext()) {
            String line = (String) iterator.next();
            to.sendMessage(line);
        }
    }

  private void reportMissing(String key) {
        if (reportedMissing.add(key)) {
            log.warning("В messages.yml нет ключа '" + key + "'");
        }
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
