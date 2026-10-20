package util;

import dao.ConexionBD;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/** Cifrado AES-GCM de credenciales (clave derivada de app.clave en db.properties). */
public final class CifradoUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private CifradoUtil() {
    }

    private static SecretKeySpec clave() throws Exception {
        String k = ConexionBD.propiedad("app.clave", "clave-por-defecto");
        byte[] h = MessageDigest.getInstance("SHA-256").digest(k.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(h, "AES");
    }

    public static String cifrar(String texto) {
        try {
            byte[] iv = new byte[12];
            RANDOM.nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, clave(), new GCMParameterSpec(128, iv));
            byte[] ct = c.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array());
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cifrar la credencial", e);
        }
    }

    public static String descifrar(String base64) {
        try {
            byte[] all = Base64.getDecoder().decode(base64);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, clave(), new GCMParameterSpec(128, all, 0, 12));
            return new String(c.doFinal(all, 12, all.length - 12), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo descifrar la credencial", e);
        }
    }
}
