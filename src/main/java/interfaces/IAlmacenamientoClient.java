package interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import modelo.Recurso;

/** Capa 3: cliente de almacenamiento. Hoy hay una implementacion simulada (carpetas locales). */
public interface IAlmacenamientoClient {

    /** Sube un archivo. Devuelve los bytes escritos. */
    long subir(Recurso recurso, String ruta, InputStream datos, boolean sobrescribir) throws IOException;

    /** Descarga un archivo hacia el flujo de salida. Devuelve los bytes enviados. */
    long descargar(Recurso recurso, String ruta, OutputStream salida) throws IOException;

    /** Copia el contenido de origen a destino. Devuelve {totalObjetos, objetosCopiados}. */
    int[] sincronizar(Recurso origen, Recurso destino, boolean sobrescribir) throws IOException;

    /** Lista nombres de una carpeta; las carpetas terminan en "/". */
    List<String> listar(Recurso recurso, String ruta) throws IOException;

    long tamanoTotal(Recurso recurso) throws IOException;
}
