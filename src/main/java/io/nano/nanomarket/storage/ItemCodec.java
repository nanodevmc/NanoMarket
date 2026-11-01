// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.storage.ItemCodec
package io.nano.nanomarket.storage;

import org.bukkit.inventory.ItemStack;

public interface ItemCodec {

  public String encode(ItemStack itemStack);

  public ItemStack decode(String s);

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
