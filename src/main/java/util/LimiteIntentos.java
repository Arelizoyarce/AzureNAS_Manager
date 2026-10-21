package util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limita los intentos fallidos de inicio de sesion (Google Guava Cache).
 * Despues de MAX_FALLOS fallos desde la misma IP con el mismo usuario, se bloquea
 * ese par durante MINUTOS_BLOQUEO minutos (cuenta desde el primer fallo).
 */
public final class LimiteIntentos {

    public static final int MAX_FALLOS = 5;
    public static final int MINUTOS_BLOQUEO = 5;

    private static final Cache<String, AtomicInteger> FALLOS = CacheBuilder.newBuilder()
            .expireAfterWrite(MINUTOS_BLOQUEO, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    private LimiteIntentos() {
    }

    /** Clave = IP + usuario. Asi un atacante solo bloquea su propia IP, no al usuario real. */
    public static String clave(String ip, String username) {
        return Strings.nullToEmpty(ip) + "|" + Strings.nullToEmpty(username).trim().toLowerCase(Locale.ROOT);
    }

    public static boolean bloqueado(String clave) {
        AtomicInteger n = FALLOS.getIfPresent(clave);
        return n != null && n.get() >= MAX_FALLOS;
    }

    public static void registrarFallo(String clave) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(clave), "La clave no puede estar vacia");
        try {
            FALLOS.get(clave, () -> new AtomicInteger()).incrementAndGet();
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void limpiar(String clave) {
        FALLOS.invalidate(clave);
    }
}
