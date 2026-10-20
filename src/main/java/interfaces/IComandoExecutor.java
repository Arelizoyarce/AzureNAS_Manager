package interfaces;

/** Capa 3: ejecutor de comandos (PowerShell). */
public interface IComandoExecutor {

    /** Resultado de ejecutar un comando. */
    final class Resultado {

        public final boolean exito;
        public final String salida;

        public Resultado(boolean exito, String salida) {
            this.exito = exito;
            this.salida = salida;
        }
    }

    Resultado ejecutar(String comando);
}
