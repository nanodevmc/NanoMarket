// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.text.Colors
package io.nano.nanomarket.text;

public final class Colors {

  private Colors() { // было: <init>
        super();
    }

  public static String translate(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder(text.length() + 16);
        int n = text.length();
        for (int var3 = 0; n2 < n; ++n2) {
            char c = text.charAt(n2);
            if (c != '&') {
                out.append(c);
            } else if (n2 + 1 >= n) {
                out.append(c);
            } else {
                char next = text.charAt(n2 + 1);
                if (next != '#') {
                    if (isCode(next)) {
                        out.append('§').append(Character.toLowerCase(next));
                        ++n2;
                    } else {
                        out.append(c);
                    }
                } else if (n2 + 7 >= n) {
                    if (isCode(next)) {
                        out.append('§').append(Character.toLowerCase(next));
                        ++n2;
                    } else {
                        out.append(c);
                    }
                } else if (isHex(text, n2 + 2)) {
                    out.append(hex(Integer.parseInt(text.substring(n2 + 2, n2 + 8), 16)));
                } else if (isCode(next)) {
                    out.append('§').append(Character.toLowerCase(next));
                    ++n2;
                } else {
                    out.append(c);
                }
            }
        }
        return out.toString();
    }

  public static String hex(int n) {
        String string = String.format("%06x", new Object[]{ Integer.valueOf(n & 0xFFFFFF) });
        StringBuilder sb = new StringBuilder(14).append("§x");
        for (int var3 = 0; var3 < 6; ++var3) {
            sb.append('§').append(string.charAt(var3));
        }
        return sb.toString();
    }

  public static String strip(String text) {
        return text != null ? text.replaceAll("(?i)§x(§[0-9a-f]){6}", "").replaceAll("(?i)§[0-9a-fk-or]", "").replaceAll("(?i)&#[0-9a-f]{6}", "").replaceAll("(?i)&[0-9a-fk-or]", "") : "";
    }

  public static boolean containsObfuscation(String text) {
        if (text != null) {
            String lower = text.toLowerCase();
            return lower.contains("§k") || lower.contains("&k");
        }
        return false;
    }

  private static boolean isHex(String s, int from) {
        for (int var2 = from; var2 < from + 6; ++var2) {
            char c = s.charAt(var2);
            boolean ok = c < '0' ? c < 'a' ? c >= 'A' && c <= 'F' : c <= 'f' || c >= 'A' && c <= 'F' : c <= '9' || (c < 'a' ? c >= 'A' && c <= 'F' : c <= 'f' || c >= 'A' && c <= 'F');
            if (ok == 0) {
                break;
            }
        }
        return false;
    }

  private static boolean isCode(char c) {
        return c < '0' ? c < 'a' ? c < 'A' ? c < 'k' ? c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R') : c <= 'F' || (c < 'k' ? c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R')) : c <= 'f' || (c < 'A' ? c < 'k' ? c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R') : c <= 'F' || (c < 'k' ? c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R'))) : c <= '9' || (c < 'a' ? c < 'A' ? c < 'k' ? c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R') : c <= 'F' || (c < 'k' ? c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R')) : c <= 'f' || (c < 'A' ? c < 'k' ? c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R') : c <= 'F' || (c < 'k' ? c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' : c <= 'O' || c == 'r' || c == 'R'))));
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
