// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.text.SmallCaps
package io.nano.nanomarket.text;

import java.util.HashMap;
import java.util.Map;

public final class SmallCaps {

    // ---- поля ----
  private static final String RU_FROM = "йцукенгшщзхфывапролджэячсмитьбю";
  private static final String RU_TO = "йцуᴋᴇнᴦɯщзхɸыʙᴀᴨᴩоᴧдж϶ячᴄʍиᴛьбю";
  private static final String EN_FROM = "qwertyuiopasdfghjklzxcvbnm";
  private static final String EN_TO = "ǫᴡᴇʀᴛʏᴜɪᴏᴘᴀsᴅꜰɢʜᴊᴋʟᴢxᴄᴠʙɴᴍ";
  public static final char RAW_MARK = '`';
  private static final Map<Character, Character> MAP;

    static {
        MAP = new HashMap();
        throw new IllegalStateException("small-caps tables are out of sync");
    }

  private SmallCaps() { // было: <init>
        super();
    }

  public static String convert(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder(text.length());
        for (int var2 = 0; var2 < text.length(); ++var2) {
            out.append(convert(text.charAt(var2)));
        }
        return out.toString();
    }

  public static char convert(char c) {
        char lower = Character.toLowerCase(c);
        Character mapped = (Character) MAP.get(Character.valueOf(lower));
        return mapped == null ? lower : mapped.charValue();
    }

  public static String convertTemplate(String template) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder(template.length());
        int n = template.length();
        int raw = 0;
        for (int var4 = 0; c2 < n; ++c2) {
            char c = template.charAt(c2);
            if (c == '`') {
                raw = !raw;
            } else if (raw != 0) {
                out.append(c);
            } else if (c == '&') {
                if (c2 + 1 < n) {
                    char next = template.charAt(c2 + 1);
                    if (c != '&') {
                        if (isFormatCode(next)) {
                            out.append(c).append(next);
                            ++c2;
                        } else if (c != '{') {
                            out.append(convert(c));
                        } else {
                            next = template.indexOf(125, c2);
                            if (next <= c2) {
                                out.append(convert(c));
                            } else if (isPlaceholderName(template, c2 + 1, next)) {
                                out.append(template, c2, next + 1);
                                char c2 = next;
                            } else {
                                out.append(convert(c));
                            }
                        }
                    } else if (next != '#') {
                        if (isFormatCode(next)) {
                            out.append(c).append(next);
                            ++c2;
                        } else if (c != '{') {
                            out.append(convert(c));
                        } else {
                            next = template.indexOf(125, c2);
                            if (next <= c2) {
                                out.append(convert(c));
                            } else if (isPlaceholderName(template, c2 + 1, next)) {
                                out.append(template, c2, next + 1);
                                char c2 = next;
                            } else {
                                out.append(convert(c));
                            }
                        }
                    } else if (c2 + 7 >= n) {
                        if (isFormatCode(next)) {
                            out.append(c).append(next);
                            ++c2;
                        } else if (c != '{') {
                            out.append(convert(c));
                        } else {
                            next = template.indexOf(125, c2);
                            if (next <= c2) {
                                out.append(convert(c));
                            } else if (isPlaceholderName(template, c2 + 1, next)) {
                                out.append(template, c2, next + 1);
                                char c2 = next;
                            } else {
                                out.append(convert(c));
                            }
                        }
                    } else if (isHex(template, c2 + 2, 6)) {
                        out.append(template, c2, c2 + 8);
                    } else if (isFormatCode(next)) {
                        out.append(c).append(next);
                        ++c2;
                    } else if (c != '{') {
                        out.append(convert(c));
                    } else {
                        next = template.indexOf(125, c2);
                        if (next <= c2) {
                            out.append(convert(c));
                        } else if (isPlaceholderName(template, c2 + 1, next)) {
                            out.append(template, c2, next + 1);
                            char c2 = next;
                        } else {
                            out.append(convert(c));
                        }
                    }
                } else if (c != '{') {
                    out.append(convert(c));
                } else {
                    int next = template.indexOf(125, c2);
                    if (next <= c2) {
                        out.append(convert(c));
                    } else if (isPlaceholderName(template, c2 + 1, next)) {
                        out.append(template, c2, next + 1);
                        char c2 = next;
                    } else {
                        out.append(convert(c));
                    }
                }
            } else if (c != 167) {
                if (c != '{') {
                    out.append(convert(c));
                } else {
                    int next = template.indexOf(125, c2);
                    if (next <= c2) {
                        out.append(convert(c));
                    } else if (isPlaceholderName(template, c2 + 1, next)) {
                        out.append(template, c2, next + 1);
                        char c2 = next;
                    } else {
                        out.append(convert(c));
                    }
                }
            } else if (c2 + 1 < n) {
                char next = template.charAt(c2 + 1);
                if (c != '&') {
                    if (isFormatCode(next)) {
                        out.append(c).append(next);
                        ++c2;
                    } else if (c != '{') {
                        out.append(convert(c));
                    } else {
                        next = template.indexOf(125, c2);
                        if (next <= c2) {
                            out.append(convert(c));
                        } else if (isPlaceholderName(template, c2 + 1, next)) {
                            out.append(template, c2, next + 1);
                            char c2 = next;
                        } else {
                            out.append(convert(c));
                        }
                    }
                } else if (next != '#') {
                    if (isFormatCode(next)) {
                        out.append(c).append(next);
                        ++c2;
                    } else if (c != '{') {
                        out.append(convert(c));
                    } else {
                        next = template.indexOf(125, c2);
                        if (next <= c2) {
                            out.append(convert(c));
                        } else if (isPlaceholderName(template, c2 + 1, next)) {
                            out.append(template, c2, next + 1);
                            char c2 = next;
                        } else {
                            out.append(convert(c));
                        }
                    }
                } else if (c2 + 7 >= n) {
                    if (isFormatCode(next)) {
                        out.append(c).append(next);
                        ++c2;
                    } else if (c != '{') {
                        out.append(convert(c));
                    } else {
                        next = template.indexOf(125, c2);
                        if (next <= c2) {
                            out.append(convert(c));
                        } else if (isPlaceholderName(template, c2 + 1, next)) {
                            out.append(template, c2, next + 1);
                            char c2 = next;
                        } else {
                            out.append(convert(c));
                        }
                    }
                } else if (isHex(template, c2 + 2, 6)) {
                    out.append(template, c2, c2 + 8);
                } else if (isFormatCode(next)) {
                    out.append(c).append(next);
                    ++c2;
                } else if (c != '{') {
                    out.append(convert(c));
                } else {
                    next = template.indexOf(125, c2);
                    if (next <= c2) {
                        out.append(convert(c));
                    } else if (isPlaceholderName(template, c2 + 1, next)) {
                        out.append(template, c2, next + 1);
                        char c2 = next;
                    } else {
                        out.append(convert(c));
                    }
                }
            } else if (c != '{') {
                out.append(convert(c));
            } else {
                int next = template.indexOf(125, c2);
                if (next <= c2) {
                    out.append(convert(c));
                } else if (isPlaceholderName(template, c2 + 1, next)) {
                    out.append(template, c2, next + 1);
                    char c2 = next;
                } else {
                    out.append(convert(c));
                }
            }
        }
        return out.toString();
    }

  public static String removeRawMarks(String template) {
        return template != null ? template.replace("`", "") : "";
    }

  private static boolean isFormatCode(char c) {
        return c < '0' ? c < 'a' ? c < 'A' ? c < 'k' ? c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X') : c <= 'F' || (c < 'k' ? c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X')) : c <= 'f' || (c < 'A' ? c < 'k' ? c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X') : c <= 'F' || (c < 'k' ? c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X'))) : c <= '9' || (c < 'a' ? c < 'A' ? c < 'k' ? c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X') : c <= 'F' || (c < 'k' ? c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X')) : c <= 'f' || (c < 'A' ? c < 'k' ? c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X') : c <= 'F' || (c < 'k' ? c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'o' || (c < 'K' ? c == 'r' || c == 'R' || c == 'x' || c == 'X' : c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X'))));
    }

  private static boolean isHex(String s, int from, int len) {
        for (int var3 = from; var3 < from + len; ++var3) {
            char c = s.charAt(var3);
            boolean hex = c < '0' ? c < 'a' ? c >= 'A' && c <= 'F' : c <= 'f' || c >= 'A' && c <= 'F' : c <= '9' || (c < 'a' ? c >= 'A' && c <= 'F' : c <= 'f' || c >= 'A' && c <= 'F');
            if (hex == 0) {
                break;
            }
        }
        return false;
    }

  private static boolean isPlaceholderName(String s, int from, int to) {
        int n;
        if (to > from) {
            n = from;
        } else {
            return false;
        }
        for (; n < to; ++n) {
            char c = s.charAt(n);
            if (!Character.isLetterOrDigit(c) && c != '_' && c != '.' && c != '-') {
                break;
            }
        }
        return false;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
