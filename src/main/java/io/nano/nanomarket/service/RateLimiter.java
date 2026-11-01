// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.service.RateLimiter
package io.nano.nanomarket.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

public final class RateLimiter {

    // ---- поля ----
  private final LongSupplier clock;
  private final Map<UUID, Long> lastClick;
  private final Map<UUID, Deque<Long>> commands;

  public RateLimiter() { // было: <init>
        this(System::currentTimeMillis);
    }

  public RateLimiter(LongSupplier clock) { // было: <init>
        super();
        lastClick = new ConcurrentHashMap();
        commands = new ConcurrentHashMap();
        this.clock = clock;
    }

  public boolean allowClick(UUID who, long cooldownMs) {
        long now = clock.getAsLong();
        Long last = (Long) lastClick.get(who);
        if (last != null && now - last.longValue() < cooldownMs) {
            return false;
        }
        lastClick.put(who, Long.valueOf(now));
        return true;
    }

  public boolean allowCommand(UUID who, int perMinute) {
        long now = clock.getAsLong();
        Deque window = (Deque) commands.computeIfAbsent(who, lp0 -> lambda_allowCommand_0((UUID) lp0));
        synchronized (window) {
            Throwable var7;
            try {
                while (!window.isEmpty()) {
                    if (now - window.peekFirst() < 60000L) {
                        break;
                    }
                    window.pollFirst();
                }
                if (window.size() < perMinute) {
                    window.addLast(Long.valueOf(now));
                    return true;
                } else {
                    return false;
                }
            } catch (Throwable e1) {
                while (true) {
                    var7 = e1;
                }
            }
            throw var7;
        }
    }

  public void forget(UUID who) {
        lastClick.remove(who);
        commands.remove(who);
    }

  private static Deque lambda_allowCommand_0(UUID uUID) { // было: lambda_allowCommand_0
        return new ArrayDeque();
    }

}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
