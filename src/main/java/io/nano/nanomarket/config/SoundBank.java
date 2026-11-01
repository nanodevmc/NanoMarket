// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.config.SoundBank
package io.nano.nanomarket.config;

import io.nano.nanomarket.config.SoundBank.Cue;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class SoundBank {

    // ---- поля ----
  public static final String OPEN = "open";
  public static final String CLICK = "click";
  public static final String PAGE = "page";
  public static final String PURCHASE = "purchase";
  public static final String SELL = "sell";
  public static final String PAYOUT = "payout";
  public static final String CANCEL = "cancel";
  public static final String ERROR = "error";
  private final File file;
  private final Logger log;
  private volatile Map<String, Cue> cues;

  public SoundBank(File file, Logger log) { // было: <init>
        super();
        cues = Map.of();
        this.file = file;
        this.log = log;
    }

  public void reload() {
        YamlConfiguration yaml = new YamlConfiguration();
        Iterator iterator;
        String key;
        HashMap loaded;
        String name;
        Sound sound;
        try {
            if (file.exists()) {
                yaml.load(file);
            }
            loaded = new HashMap();
            HashSet bad = new HashSet();
            iterator = yaml.getKeys(false).iterator();
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (yaml.getBoolean(key + ".enabled", true)) {
                    name = yaml.getString(key + ".sound", "");
                    try {
                        sound = Sound.valueOf(name.trim().toUpperCase(Locale.ROOT));
                        loaded.put(key, new Cue(sound, (float) yaml.getDouble(key + ".volume", 1.0), (float) yaml.getDouble(key + ".pitch", 1.0)));
                    } catch (IllegalArgumentException sound) {
                        if (bad.add(name)) {
                            log.warning("sounds.yml: неизвестный звук '" + name + "' (" + key + ")");
                        }
                    }
                    continue;
                } else {
                    continue;
                }
            }
            cues = loaded;
            return;
        } catch (IOException loaded) {
            log.warning("sounds.yml не читается: " + loaded.getMessage());
        }
        HashMap loaded = new HashMap();
        HashSet bad = new HashSet();
        Iterator iterator = yaml.getKeys(false).iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (yaml.getBoolean(key + ".enabled", true)) {
                String name = yaml.getString(key + ".sound", "");
                Sound sound = Sound.valueOf(name.trim().toUpperCase(Locale.ROOT));
                loaded.put(key, new Cue(sound, (float) yaml.getDouble(key + ".volume", 1.0), (float) yaml.getDouble(key + ".pitch", 1.0)));
                continue;
            } else {
                continue;
            }
        }
        cues = loaded;
    }

  public void play(Player player, String key) {
        Cue cue = (Cue) cues.get(key);
        if (cue != null && player != null && player.isOnline()) {
            player.playSound(player.getLocation(), cue.sound(), cue.volume(), cue.pitch());
            return;
        }
    }


    // ---- вложенный класс: Cue ----
    private static record Cue(Sound sound, float volume, float pitch) {
    
      private Cue(Sound sound, float volume, float pitch) { // было: <init>
            super();
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
