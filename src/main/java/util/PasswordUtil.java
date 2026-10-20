package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/** Hash de contrasenas: formato guardado = salt$sha256(salt + password). */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(String password) {
        byte[] s = new byte[8];
        RANDOM.nextBytes(s);
        String salt = hex(s);
        return salt + "$" + sha256(salt + password);
    }

    public static boolean verificar(String password, String guardado) {
        if (password == null || guardado == null) {
            return false;
        }
        int i = guardado.indexOf('$');
        if (i < 0) {
            return false;
        }
        String salt = guardado.substring(0, i);
        String esperado = guardado.substring(i + 1);
        return MessageDigest.isEqual(
                sha256(salt + password).getBytes(StandardCharsets.UTF_8),
                esperado.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return hex(md.digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            sb.append(String.format("%02x", x));
        }
        return sb.toString();
    }
}
