// исходный (обфусцированный) внутренний класс: io.nano.nanomarket.service.PlayerSessions
package io.nano.nanomarket.service;

import io.nano.nanomarket.model.SortMode;
import io.nano.nanomarket.service.PlayerSessions.Session;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSessions {

    // ---- поля ----
  private final Map<UUID, Session> sessions;
  private volatile SortMode defaultSort;

  public PlayerSessions() { // было: <init>
        super();
        sessions = new ConcurrentHashMap();
        defaultSort = SortMode.NEWEST;
    }

  public void defaultSort(SortMode sort) {
        defaultSort = sort;
    }

  public Session of(UUID id) {
        return (Session) sessions.computeIfAbsent(id, lp0 -> lambda_of_0((UUID) lp0));
    }

  public void forget(UUID id) {
        sessions.remove(id);
    }

  public void clear() {
        sessions.clear();
    }

  private Session lambda_of_0(UUID uUID) { // было: lambda_of_0
        return new Session(defaultSort);
    }


    // ---- вложенный класс: Session ----
    public static final class Session {
    
        // ---- поля ----
      public int page;
      public int myPage;
      public String query;
      public SortMode sort;
      public long lastPurchaseAt;
      public long searchDeadline;
    
       Session(SortMode sort) { // было: <init>
            super();
            page = 1;
            myPage = 1;
            query = "";
            this.sort = sort;
        }
    
    }
    
    // ---
}

// ---
// Декомпилировано с помощью NanoDecompiler - t.me/NanoDev_mc
