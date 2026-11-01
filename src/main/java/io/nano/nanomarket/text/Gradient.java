// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.text.Gradient
package io.nano.nanomarket.text;

import io.nano.nanomarket.text.Colors;

public final class Gradient {

  private Gradient() { // было: <init>
        super();
    }

  public static String apply(String text, int fromRgb, int toRgb, boolean bold) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        int visible = 0;
        int out = 0;
        for (; out < text.length(); ++out) {
            if (!Character.isWhitespace(text.charAt(out))) {
                ++visible;
            }
        }
        out = new StringBuilder(text.length() * 16);
        int index = 0;
        for (int var7 = 0; var7 < text.length(); ++var7) {
            char c = text.charAt(var7);
            if (Character.isWhitespace(c)) {
                out.append(c);
            } else {
                double var9 = visible > 1 ? (double) index / (double) (visible - 1) : 0.0;
                out.append(Colors.hex(lerp(fromRgb, toRgb, var9)));
                if (bold) {
                    out.append("§l");
                }
                out.append(c);
                ++index;
            }
        }
        return out.toString();
    }

  public static String byColumn(String line, int width, int fromRgb, int toRgb) {
        StringBuilder out = new StringBuilder(line.length() * 14);
        int last = Math.max(1, width - 1);
        for (int var6 = 0; var6 < line.length(); ++var6) {
            char c = line.charAt(var6);
            if (c != ' ') {
                out.append(Colors.hex(lerp(fromRgb, toRgb, Math.min(1.0, (double) var6 / (double) last)))).append(c);
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

  static int lerp(int n1, int n2, double d) {
        int n = channel(n1 >> 16, n2 >> 16, d);
        int n2 = channel(n1 >> 8, n2 >> 8, d);
        int n3 = channel(n1, n2, d);
        return n << 16 | n2 << 8 | n3;
    }

  private static int channel(int n1, int n2, double d) {
        int n = n1 & 0xFF;
        int n2 = n2 & 0xFF;
        return (int) Math.round((double) n + (double) (n2 - n) * d);
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
