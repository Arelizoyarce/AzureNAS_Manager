package servicio;

import dao.ConexionBD;
import interfaces.IComandoExecutor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Ejecuta cmdlets de PowerShell en el servidor. Por seguridad solo se permiten cmdlets Get-*
 * con parametros simples (sin ; | & $ ` > < ni saltos de linea).
 */
public class PowerShellExecutor implements IComandoExecutor {

    private static final Pattern PERMITIDO = Pattern.compile("^Get-[A-Za-z]+( +[-A-Za-z0-9_.:/\\\\]+)*$");
    private static final int MAX_SALIDA = 20000;

    @Override
    public Resultado ejecutar(String comando) {
        if (!"true".equalsIgnoreCase(ConexionBD.propiedad("powershell.habilitado", "false"))) {
            return new Resultado(false, "Ejecutor PowerShell deshabilitado (powershell.habilitado=false en db.properties)");
        }
        String cmd = comando == null ? "" : comando.trim();
        if (cmd.length() > 300 || !PERMITIDO.matcher(cmd).matches()) {
            return new Resultado(false, "Comando no permitido. Solo cmdlets Get-* con parametros simples.");
        }
        String exe = System.getProperty("os.name").toLowerCase().contains("win") ? "powershell.exe" : "pwsh";
        ProcessBuilder pb = new ProcessBuilder(exe, "-NoProfile", "-NonInteractive", "-Command", cmd);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            CompletableFuture<String> salida = CompletableFuture.supplyAsync(() -> leer(p.getInputStream()));
            if (!p.waitFor(20, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                return new Resultado(false, "Tiempo de espera agotado (20 s)");
            }
            String texto = salida.get(3, TimeUnit.SECONDS);
            return new Resultado(p.exitValue() == 0, texto);
        } catch (IOException e) {
            return new Resultado(false, "No se pudo iniciar " + exe + ": " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Resultado(false, "Ejecucion interrumpida");
        } catch (Exception e) {
            return new Resultado(false, "Error al ejecutar: " + e.getMessage());
        }
    }

    private static String leer(InputStream in) {
        try {
            byte[] datos = in.readNBytes(MAX_SALIDA);
            return new String(datos, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
