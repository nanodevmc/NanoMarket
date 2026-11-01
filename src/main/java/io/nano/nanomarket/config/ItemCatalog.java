// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.config.ItemCatalog
package io.nano.nanomarket.config;

import io.nano.nanomarket.text.Colors;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public final class ItemCatalog {

    // ---- поля ----
  private static final Map<String, String> POTIONS;
  private static final Map<String, String> ENCHANTS;
  private final File file;
  private final Supplier<InputStream> bundled;
  private final Logger log;
  private volatile Map<String, Entry> entries;

    static {
        POTIONS = Map.ofEntries(new Entry[]{ Map.entry("NIGHT_VISION", "ночное зрение"), Map.entry("INVISIBILITY", "невидимость"), Map.entry("JUMP", "прыгучесть"), Map.entry("FIRE_RESISTANCE", "огнестойкость"), Map.entry("SPEED", "скорость"), Map.entry("SLOWNESS", "замедление"), Map.entry("WATER_BREATHING", "подводное дыхание"), Map.entry("INSTANT_HEAL", "лечение"), Map.entry("INSTANT_DAMAGE", "вред"), Map.entry("POISON", "яд"), Map.entry("REGEN", "регенерация"), Map.entry("STRENGTH", "сила"), Map.entry("WEAKNESS", "слабость"), Map.entry("LUCK", "удача"), Map.entry("TURTLE_MASTER", "черепаха"), Map.entry("SLOW_FALLING", "медленное падение"), Map.entry("WATER", "вода"), Map.entry("AWKWARD", "невкусное"), Map.entry("MUNDANE", "обычное"), Map.entry("THICK", "густое") });
        Entry[] __obj2 = new Entry[]{ Map.entry("mending", "починка"), Map.entry("unbreaking", "прочность"), Map.entry("sharpness", "острота"), Map.entry("efficiency", "эффективность"), Map.entry("fortune", "удача"), Map.entry("protection", "защита"), Map.entry("looting", "добыча"), Map.entry("silk_touch", "шёлковое касание"), Map.entry("power", "сила"), Map.entry("infinity", "бесконечность"), Map.entry("fire_aspect", "заговор огня"), Map.entry("knockback", "отдача"), Map.entry("thorns", "шипы"), Map.entry("feather_falling", "невесомость"), Map.entry("respiration", "подводное дыхание"), Map.entry("aqua_affinity", "подводник") };
        ENCHANTS = Map.ofEntries(__obj2);
    }

  public ItemCatalog(File file, Supplier<InputStream> bundled, Logger log) { // было: <init>
        super();
        entries = Map.of();
        this.file = file;
        this.bundled = bundled;
        this.log = log;
    }

  public void reload() {
        YamlConfiguration yaml = new YamlConfiguration();
        InputStream in;
        String key;
        int n;
        String[] parts;
        Iterator t;
        String tag;
        ArrayList tags;
        String value;
        try {
            if (file.exists()) {
                yaml.load(file);
            }
            if (yaml.getKeys(false).isEmpty()) {
                try {
                    in = (InputStream) bundled.get();
                    try {
                        if (in != null) {
                            yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
                        }
                    } catch (Throwable e2) {
                        if (in == null) {
                            throw e2;
                        } else {
                            try {
                                in.close();
                            } catch (Throwable key) {
                                e2.addSuppressed(key);
                                throw e2;
                            }
                            throw e2;
                        }
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException in) {
                }
                /* нередуцируемый переход -> 122 */
            }
            in = new LinkedHashMap();
            t = yaml.getKeys(false).iterator();
            while (t.hasNext()) {
                key = (String) t.next();
                value = yaml.getString(key);
                if (value == null) {
                    continue;
                }
                if (value.isBlank()) {
                    continue;
                } else {
                    parts = value.split(":");
                    tags = new ArrayList();
                    n = 1;
                    for (; n < parts.length; ++n) {
                        tag = parts[n].trim();
                        if (!tag.isEmpty()) {
                            tags.add(tag);
                        }
                    }
                    in.put(key.trim().toUpperCase(Locale.ROOT), new io.nano.nanomarket.config.ItemCatalog.Entry(parts[0].trim(), List.copyOf(tags)));
                    continue;
                }
            }
            entries = in;
            return;
        } catch (IOException in) {
            log.warning("items.yml не читается: " + in.getMessage());
        }
        if (yaml.getKeys(false).isEmpty()) {
            in = (InputStream) bundled.get();
            if (in != null) {
                yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
                if (in != null) {
                    in.close();
                }
            } else if (in != null) {
                in.close();
            }
        }
        LinkedHashMap in = new LinkedHashMap();
        Iterator t = yaml.getKeys(false).iterator();
        while (t.hasNext()) {
            String key = (String) t.next();
            String value = yaml.getString(key);
            if (value == null) {
                continue;
            }
            if (value.isBlank()) {
                continue;
            } else {
                String[] parts = value.split(":");
                ArrayList tags = new ArrayList();
                int n = 1;
                for (; n < parts.length; ++n) {
                    String tag = parts[n].trim();
                    if (!tag.isEmpty()) {
                        tags.add(tag);
                    }
                }
                in.put(key.trim().toUpperCase(Locale.ROOT), new io.nano.nanomarket.config.ItemCatalog.Entry(parts[0].trim(), List.copyOf(tags)));
                continue;
            }
        }
        entries = in;
    }

  public int size() {
        return entries.size();
    }

  public io.nano.nanomarket.config.ItemCatalog.Entry entry(String material) {
        return (material != null ? (io.nano.nanomarket.config.ItemCatalog.Entry) entries.get(material.toUpperCase(Locale.ROOT)) : null);
    }

  public String name(String material) {
        io.nano.nanomarket.config.ItemCatalog.Entry entry = entry(material);
        return entry == null ? titleCase(material) : entry.name();
    }

  public String displayName(ItemStack item) {
        if (item == null) {
            return "";
        }
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : null;
        return meta != null && meta.hasDisplayName() ? meta.getDisplayName() : catalogName(item);
    }

  public String catalogName(ItemStack item) {
        String base = name(item.getType().name());
        String potion = potionSuffix(item);
        return potion.isEmpty() ? base : base + ": " + potion;
    }

  public String searchText(ItemStack item, String sellerName) {
        Map temp2;
        LinkedHashSet parts = new LinkedHashSet();
        String material = item.getType().name();
        parts.add(material.toLowerCase(Locale.ROOT));
        parts.add(material.toLowerCase(Locale.ROOT).replace('_', ' '));
        io.nano.nanomarket.config.ItemCatalog.Entry entry = entry(material);
        Iterator potion;
        String tag;
        if (entry != null) {
            parts.add(entry.name().toLowerCase(Locale.ROOT));
            potion = entry.tags().iterator();
            while (potion.hasNext()) {
                tag = (String) potion.next();
                parts.add(tag.toLowerCase(Locale.ROOT));
            }
        }
        String potion = potionSuffix(item);
        if (!potion.isEmpty()) {
            parts.add(potion.toLowerCase(Locale.ROOT));
        }
        ItemMeta tag = item.hasItemMeta() ? item.getItemMeta() : null;
        Map enchants;
        if (tag != null) {
            if (tag.hasDisplayName()) {
                parts.add(Colors.strip(tag.getDisplayName()).toLowerCase(Locale.ROOT));
            }
            temp2 = tag instanceof EnchantmentStorageMeta storage ? storage.getStoredEnchants() : tag.getEnchants();
            enchants = temp2;
            Iterator storage = enchants.keySet().iterator();
            while (storage.hasNext()) {
                Enchantment ench = (Enchantment) storage.next();
                String key = ench.getKey().getKey();
                parts.add(key);
                String ru = (String) ENCHANTS.get(key);
                if (ru != null) {
                    parts.add(ru);
                }
            }
        }
        if (sellerName != null) {
            parts.add(sellerName.toLowerCase(Locale.ROOT));
        }
        String enchants = String.join(" | ", parts);
        return enchants.length() <= 500 ? enchants : enchants.substring(0, 500);
    }

  public String sortKey(ItemStack item) {
        String key = Colors.strip(displayName(item)).toLowerCase(Locale.ROOT);
        return key.length() <= 120 ? key : key.substring(0, 120);
    }

  public List<String> suggest(String input, int limit) {
        String needle = input != null ? input.toLowerCase(Locale.ROOT) : "";
        TreeSet starts = new TreeSet();
        TreeSet contains = new TreeSet();
        Iterator out = entries.values().iterator();
        while (out.hasNext()) {
            io.nano.nanomarket.config.ItemCatalog.Entry entry = (io.nano.nanomarket.config.ItemCatalog.Entry) out.next();
            String lower = entry.name().toLowerCase(Locale.ROOT);
            if (lower.startsWith((String) needle)) {
                starts.add(entry.name());
            } else if (lower.contains((CharSequence) needle)) {
                contains.add(entry.name());
            }
        }
        ArrayList temp6 = new ArrayList();
        out = temp6;
        return out.size() <= limit ? out : out.subList(0, limit);
    }

  private String potionSuffix(ItemStack item) {
        Material type = item.getType();
        if (type != Material.POTION && type != Material.SPLASH_POTION && type != Material.LINGERING_POTION && type != Material.TIPPED_ARROW) {
            return "";
        }
        if (item.hasItemMeta()) {
            ItemMeta data = item.getItemMeta();
            if (data instanceof PotionMeta meta) {
                data = meta.getBasePotionData();
                String type0 = data.getType().name();
                StringBuilder sb = new StringBuilder((String) POTIONS.getOrDefault(type0, titleCase(type0)));
                if (data.isUpgraded()) {
                    sb.append(" II");
                }
                if (data.isExtended()) {
                    sb.append(" +");
                }
                return sb.toString();
            }
        }
        return "";
    }

  public static String titleCase(String material) {
        if (material == null || material.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        String[] string[] = material.toLowerCase(Locale.ROOT).split("_");
        int n = string[].length;
        for (String part : var2) {
            if (!part.isEmpty()) {
                if (out.length() > 0) {
                    out.append(' ');
                }
                out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
        }
        return out.toString();
    }


    // ---- вложенный класс: Entry ----
    public static record Entry(String name, List tags) {
    
      public Entry(String name, List<String> tags) { // было: <init>
            super();
            this.name = name;
            this.tags = tags;
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
