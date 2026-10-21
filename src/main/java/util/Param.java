package util;

import org.apache.commons.lang3.StringUtils;

/** Lectura segura de parametros de formulario (usa Apache Commons Lang3). */
public final class Param {

    private Param() {
    }

    public static Integer entero(String s) {
        String limpio = StringUtils.trimToNull(s);
        if (limpio == null) {
            return null;
        }
        try {
            return Integer.valueOf(limpio);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean marcado(String s) {
        return StringUtils.equalsAny(s, "on", "true", "1");
    }

    public static String texto(String s) {
        return StringUtils.trimToEmpty(s);
    }
}
