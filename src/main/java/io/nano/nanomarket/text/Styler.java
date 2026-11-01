// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.text.Styler
package io.nano.nanomarket.text;

import io.nano.nanomarket.text.Colors;
import io.nano.nanomarket.text.Gradient;
import io.nano.nanomarket.text.SmallCaps;
import io.nano.nanomarket.text.Vars;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Styler {

    // ---- поля ----
  public static final String BRAND_TEXT = "NanoMarket";
  private final boolean smallCaps;
  private final boolean gradient;
  private final String currency;
  private final String version;
  private final String brand;

  public Styler(boolean smallCaps, boolean gradient, String currency, String version) { // было: <init>
        String temp1;
        super();
        this.smallCaps = smallCaps;
        this.gradient = gradient;
        if (currency == null) {
            temp1 = "◈";
        } else {
            temp1 = currency.isBlank() ? "◈" : currency;
        }
        temp1.currency = temp1;
        this.version = version != null ? version : "";
        if (smallCaps) {
        }
        Object name = __entry0__;
        brand = gradient ? Gradient.apply((String) name, 4120831, 10185727, true) + "§r" : Colors.translate("&#3EE0FF") + "§l" + name + "§r";
    }

  public boolean smallCapsEnabled() {
        return smallCaps;
    }

  public String brand() {
        return brand;
    }

  public String currency() {
        return currency;
    }

  public String font(String plain) {
        return smallCaps ? SmallCaps.convert(plain) : plain;
    }

  public String render(String template, Map<String, String> vars) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        String prepared = smallCaps ? SmallCaps.convertTemplate(template) : SmallCaps.removeRawMarks(template);
        StringBuilder out = new StringBuilder(prepared.length() + 32);
        StringBuilder literal = new StringBuilder();
        int n = prepared.length();
        for (i = 0; i < n; ++i) {
            char c = prepared.charAt(i);
            if (c != '{') {
                literal.append(c);
            } else {
                int close = prepared.indexOf(125, i);
                if (close <= i + 1) {
                    literal.append(c);
                } else {
                    String value = resolve(prepared.substring(i + 1, close), vars);
                    if (value == null) {
                        literal.append(c);
                    } else {
                        flush(literal, out);
                        out.append(value);
                        int i = close;
                    }
                }
            }
        }
        flush(literal, out);
        return out.toString();
    }

  public String render(String template, Object[] obj) {
        return render(template, Vars.of(obj));
    }

  public List<String> renderAll(List<String> templates, Map<String, String> vars) {
        ArrayList out = new ArrayList(templates.size());
        Iterator iterator = templates.iterator();
        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            out.add(render(string, vars));
        }
        return out;
    }

  private void flush(StringBuilder literal, StringBuilder out) {
        if (literal.length() != 0) {
            out.append(Colors.translate(literal.toString()));
            literal.setLength(0);
            return;
        }
    }

  private String resolve(String key, Map<String, String> vars) {
        String temp1;
        if (vars != null) {
            if (vars.containsKey(key)) {
                return (String) vars.get(key);
            }
            switch (n) {
                case 0:
                    temp1 = brand;
                    break;
                case 1:
                    temp1 = currency;
                    break;
                case 2:
                    temp1 = version;
                    break;
                default:
                    temp1 = null;
            }
            return temp1;
        }
        String string = key;
        switch (string) {
            case "brand":
                temp1 = brand;
                break;
            case "currency":
                temp1 = currency;
                break;
            case "version":
                temp1 = version;
                break;
            default:
                temp1 = null;
        }
        return temp1;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
