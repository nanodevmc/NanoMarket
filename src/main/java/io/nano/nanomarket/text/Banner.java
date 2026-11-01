// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.text.Banner
package io.nano.nanomarket.text;

import io.nano.nanomarket.text.Banner.Info;
import io.nano.nanomarket.text.Colors;
import io.nano.nanomarket.text.Gradient;
import java.util.ArrayList;
import java.util.List;

public final class Banner {

    // ---- поля ----
  public static final String CHANNEL = "t.me/NanoDev_mc";
  public static final String REPOSITORY = "github.com/nanodevmc/NanoMarket";
  public static final String AUTHOR = "NanoDev";
  public static final String AUTHOR_CONTACT = "@radoqi";
  static final String[] LOGO;
  private static final String INDENT = " ";
  private static final int LABEL_WIDTH = 12;

    static {
        LOGO = new String[]{ " _   _                       __  __               _            _   ", "| \\ | |  __ _  _ __    ___  |  \\/  |  __ _  _ __ | | __  ___  | |_ ", "|  \\| | / _` || '_ \\  / _ \\ | |\\/| | / _` || '__|| |/ / / _ \\ | __|", "| |\\  || (_| || | | || (_) || |  | || (_| || |   |   < |  __/ | |_ ", "|_| \\_| \\__,_||_| |_| \\___/ |_|  |_| \\__,_||_|   |_|\\_\\ \\___|  \\__|" };
    }

  private Banner() { // было: <init>
        super();
    }

  public static int logoWidth() {
        int width = 0;
        String[] string[] = LOGO;
        int n = string[].length;
        for (String row : var1) {
            width = Math.max(width, row.length());
        }
        return width;
    }

  public static List<String> lines(Info info) {
        String temp4;
        boolean temp6;
        String temp10;
        String __obj11;
        String __obj12;
        int __obj13;
        ArrayList out = new ArrayList();
        int width = logoWidth();
        String frame = Colors.translate("&#3A4256");
        String dim = Colors.translate("&#8B98AD");
        String text = Colors.translate("&#E6EDF7");
        String accent = Colors.translate("&#3EE0FF");
        out.add("");
        String[] rule = LOGO;
        int link = rule.length;
        for (String row : rule) {
            out.add("  " + Gradient.byColumn(row, width, 4120831, 10185727));
        }
        out.add("");
        out.add("  " + accent + "❖ " + text + "v" + info.version() + dim + "   Paper / Spigot 1.20.x   │   server-id: " + text + info.serverId());
        rule = frame + "─".repeat(Math.max(20, width - 2));
        out.add("  " + rule);
        if (info.database() == null) {
            temp6 = info.database() != null;
            "недоступна — маркет отключён, проверьте раздел database в config.yml" = "недоступна — маркет отключён, проверьте раздел database в config.yml";
        } else {
            temp4 = info.database();
            temp6 = info.database() != null;
        }
        temp6.add("  " + row("База данных", temp4, temp4, true));
        if (info.economy() == null) {
        }
        if (info.economy() == null) {
        }
        __entry3__.add("  " + row((Boolean) __entry2__, (String) __entry1__, (String) __entry0__, false));
        String temp14 = info.payoutMode();
        if (info.payoutMode().equals("BATCH")) {
            temp10 = " │ каждые " + info.payoutSeconds() + " с";
        } else {
            temp10 = "";
            __obj11 = temp14;
            __obj12 = "Выплаты";
            __obj13 = 1;
        }
        temp10.add("  " + row((Boolean) __obj13, (String) __obj12, __obj11 + temp10, true));
        out.add("  " + row(1, "Каталог", info.catalogSize() + " предметов │ шрифт " + (info.smallCaps() ? "small caps" : "обычный"), true));
        out.add("  " + rule);
        link = Colors.translate("&#7CC4FF");
        out.add("  " + accent + "➤ " + dim + "GitHub: " + link + "github.com/nanodevmc/NanoMarket");
        out.add("  " + accent + "➤ " + dim + "Канал: " + link + "t.me/NanoDev_mc");
        out.add("  " + accent + "➤ " + dim + "Автор: " + text + "NanoDev" + dim + " │ " + link + "@radoqi");
        out.add("");
        return out;
    }

  private static String row(boolean ok, String label, String value, boolean failIsError) {
        String temp4;
        if (ok) {
            temp4 = Colors.translate("&#6EE7A0") + "✔";
        } else {
            if (failIsError) {
            }
            temp4 = (failIsError ? "✘" : "⚠") + (failIsError ? "✘" : "⚠");
        }
        String mark = temp4;
        StringBuilder padded = new StringBuilder(label);
        while (padded.length() < 12) {
            padded.append(' ');
        }
        return mark + " " + Colors.translate("&#8B98AD") + padded + Colors.translate("&#E6EDF7") + value;
    }


    // ---- вложенный класс: Info ----
    public static record Info(String version, String serverId, String database, String economy, String payoutMode, int payoutSeconds, int catalogSize, boolean smallCaps) {
    
      public Info(String version, String serverId, String database, String economy, String payoutMode, int payoutSeconds, int catalogSize, boolean smallCaps) { // было: <init>
            super();
            this.version = version;
            this.serverId = serverId;
            this.database = database;
            this.economy = economy;
            this.payoutMode = payoutMode;
            this.payoutSeconds = payoutSeconds;
            this.catalogSize = catalogSize;
            this.smallCaps = smallCaps;
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
