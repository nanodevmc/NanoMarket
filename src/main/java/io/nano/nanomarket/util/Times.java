// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.util.Times
package io.nano.nanomarket.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class Times {

    // ---- поля ----
  private static final DateTimeFormatter STAMP;
  private static final DateTimeFormatter FULL;

    static {
        STAMP = DateTimeFormatter.ofPattern("dd.MM HH:mm").withZone(ZoneId.systemDefault());
        FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    }

  private Times() { // было: <init>
        super();
    }

  public static String stamp(long epochMillis) {
        return STAMP.format(Instant.ofEpochMilli(epochMillis));
    }

  public static String full(long epochMillis) {
        return FULL.format(Instant.ofEpochMilli(epochMillis));
    }

  public static String duration(long seconds) {
        if (seconds >= 60L) {
            long minutes = seconds / 60L;
            if (minutes >= 60L) {
                long hours = minutes / 60L;
                long restMinutes = minutes % 60L;
                return restMinutes != 0L ? hours + "ч " + restMinutes + "м" : hours + "ч";
            } else {
                return minutes + "м";
            }
        }
        return seconds + "с";
    }

  public static long startOfTodayMillis() {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
