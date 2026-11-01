// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.text.Glyphs
package io.nano.nanomarket.text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class Glyphs {

    // ---- поля ----
  public static final String ARROW = "➤";
  public static final String ARROW_LEFT = "◀";
  public static final String ARROW_RIGHT = "▶";
  public static final String ARROW_FLOW = "➜";
  public static final String DOUBLE_LEFT = "«";
  public static final String DOUBLE_RIGHT = "»";
  public static final String SPARK = "❖";
  public static final String STAR = "✦";
  public static final String STAR_OUTLINE = "✧";
  public static final String STAR_FILLED = "★";
  public static final String CHECK = "✔";
  public static final String CROSS = "✘";
  public static final String WARN = "⚠";
  public static final String BULLET = "•";
  public static final String DIAMOND = "◆";
  public static final String DIAMOND_OUTLINE = "◇";
  public static final String COIN = "◈";
  public static final String DOT = "●";
  public static final String RING = "○";
  public static final String BAR_ON = "▰";
  public static final String BAR_OFF = "▱";
  public static final String LINE = "─";
  public static final String LINE_DOUBLE = "═";
  public static final String VERTICAL = "│";
  public static final String SEARCH = "⌖";
  public static final String REFRESH = "↻";
  public static final String SORT = "⇅";
  public static final String MAIL = "✉";
  public static final String COMMAND = "⌘";
  public static final String CLOCK = "◔";
  public static final String INFINITY = "∞";
  public static final String PLUS = "⊕";
  public static final String MINUS = "⊖";
  public static final String HEART = "❤";
  public static final String FLOWER = "✿";
  public static final String SNOW = "❄";
  public static final String UP = "↑";
  public static final String DOWN = "↓";

  private Glyphs() { // было: <init>
        super();
    }

  public static String bar(int filled, int total) {
        int n = Math.max(0, Math.min(filled, total));
        return "▰".repeat(n) + "▱".repeat(total - n);
    }

  public static List<String> all() {
        ArrayList list = new ArrayList();
        Field[] field[] = Glyphs.class.getDeclaredFields();
        int n = field[].length;
        for (Field var4 : var1) {
            if (var4.getType() == String.class && Modifier.isStatic(var4.getModifiers()) && Modifier.isPublic(var4.getModifiers())) {
                try {
                    list.add((String) var4.get(null));
                } catch (IllegalAccessException e1) {
                }
            }
        }
        Throwable t = __caught__;
        throw new IllegalStateException(t);
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
