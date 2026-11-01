// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.text.Vars
package io.nano.nanomarket.text;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Vars {

  private Vars() { // было: <init>
        super();
    }

  public static Map<String, String> of(Object[] obj) {
        if (obj.length % 2) {
            throw new IllegalArgumentException("odd number of key/value arguments");
        }
        LinkedHashMap map = new LinkedHashMap();
        for (int var2 = 0; var2 < obj.length; var2 += 2) {
            map.put(String.valueOf(obj[var2]), String.valueOf(obj[var2 + 1]));
        }
        return map;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
