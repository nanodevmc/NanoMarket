// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.service.Result
package io.nano.nanomarket.service;

public record Result<T>(Object value, String errorKey, Object[] errorArgs) {

  public Result(T value, String errorKey, Object[] errorArgs) { // было: <init>
        super();
        this.value = value;
        this.errorKey = errorKey;
        this.errorArgs = errorArgs;
    }

  public static <T> Result<T> ok(T value) {
        return new Result(value, null, new Object[0]);
    }

  public static <T> Result<T> fail(String key, Object[] args) {
        return new Result(null, key, args);
    }

  public boolean ok() {
        return errorKey == null;
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
