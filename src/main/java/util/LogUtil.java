package util;

import org.apache.commons.lang3.StringUtils;

/** Evita que un dato escrito por el usuario falsifique lineas del log (inyeccion de saltos de linea). */
public final class LogUtil {

    private LogUtil() {
    }

    public static String limpiar(String texto) {
        String t = StringUtils.defaultString(texto);
        t = StringUtils.replaceChars(t, "\r\n\t", "   ");
        return StringUtils.left(t, 200);
    }
}
