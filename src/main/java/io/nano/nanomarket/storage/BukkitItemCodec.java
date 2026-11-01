// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.storage.BukkitItemCodec
package io.nano.nanomarket.storage;

import io.nano.nanomarket.storage.ItemCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public final class BukkitItemCodec implements ItemCodec {

    // ---- поля ----
  private static final String PREFIX = "v2:";

  public String encode(ItemStack item) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try {
                BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytes);
                String string;
                try {
                    out.writeObject(item);
                    out.flush();
                    string = "v2:" + Base64.getEncoder().encodeToString(bytes.toByteArray());
                } catch (Throwable e2) {
                    try {
                        out.close();
                    } catch (Throwable e2) {
                        e2.addSuppressed(e2);
                        throw e2;
                    }
                    throw e2;
                }
                out.close();
                bytes.close();
                return string;
            } catch (Throwable out) {
                try {
                    bytes.close();
                } catch (Throwable e4) {
                    out.addSuppressed(e4);
                    throw out;
                }
                throw out;
            }
        } catch (IOException bytes) {
            throw new IllegalStateException("cannot serialise item", bytes);
        }
    }

  public ItemStack decode(String data) {
        ItemStack temp2;
        if (data == null || data.isBlank() || data.equals("null")) {
            return null;
        }
        String body = data.startsWith("v2:") ? data.substring(3) : data;
        try {
            byte[] raw = Base64.getMimeDecoder().decode(body.trim());
            BukkitObjectInputStream in = new BukkitObjectInputStream(new ByteArrayInputStream(raw));
            ItemStack stack;
            try {
                Object object = in.readObject();
                temp2 = object instanceof ItemStack stack ? stack : null;
                stack = temp2;
            } catch (Throwable e2) {
                try {
                    in.close();
                } catch (Throwable stack) {
                    e2.addSuppressed(stack);
                    throw e2;
                }
                throw e2;
            }
            in.close();
            return stack;
        } catch (IOException raw) {
            return null;
        }
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
