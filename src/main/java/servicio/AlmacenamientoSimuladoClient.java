package servicio;

import dao.ConexionBD;
import interfaces.IAlmacenamientoClient;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import modelo.Recurso;
import org.apache.commons.io.FileUtils;

/**
 * Azure Storage SIMULADO: cada recurso es una carpeta local
 * {storage.base}/{cuentaId}/{nombreRecurso}/ (File Share o Blob Container).
 * Para usar Azure real, crea otra clase que implemente IAlmacenamientoClient.
 */
public class AlmacenamientoSimuladoClient implements IAlmacenamientoClient {

    private final Path raiz = Paths.get(ConexionBD.propiedad("storage.base",
            System.getProperty("user.home") + "/azurenas-simulado")).toAbsolutePath().normalize();

    private Path base(Recurso r) {
        return raiz.resolve(String.valueOf(r.getCuentaId())).resolve(r.getNombre()).normalize();
    }

    private Path resolver(Recurso r, String ruta) throws IOException {
        Path base = base(r);
        String limpia = ruta == null ? "" : ruta.replace('\\', '/');
        while (limpia.startsWith("/")) {
            limpia = limpia.substring(1);
        }
        Path p = base.resolve(limpia).normalize();
        if (!p.startsWith(base)) {
            throw new IOException("Ruta no permitida");
        }
        return p;
    }

    @Override
    public long subir(Recurso recurso, String ruta, InputStream datos, boolean sobrescribir) throws IOException {
        Path p = resolver(recurso, ruta);
        if (Files.isDirectory(p)) {
            throw new IOException("La ruta indicada es una carpeta");
        }
        if (Files.exists(p) && !sobrescribir) {
            throw new IOException("El archivo ya existe (activa 'sobrescribir' para reemplazarlo)");
        }
        Files.createDirectories(p.getParent());
        return Files.copy(datos, p, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public long descargar(Recurso recurso, String ruta, OutputStream salida) throws IOException {
        Path p = resolver(recurso, ruta);
        if (!Files.isRegularFile(p)) {
            throw new FileNotFoundException("El archivo no existe");
        }
        return Files.copy(p, salida);
    }

    @Override
    public int[] sincronizar(Recurso origen, Recurso destino, boolean sobrescribir) throws IOException {
        Path o = base(origen);
        Path d = base(destino);
        Files.createDirectories(d);
        if (!Files.isDirectory(o)) {
            return new int[]{0, 0};
        }
        int total = 0;
        int copiados = 0;
        List<Path> archivos;
        try (Stream<Path> s = Files.walk(o)) {
            archivos = s.filter(Files::isRegularFile).collect(Collectors.toList());
        }
        for (Path f : archivos) {
            total++;
            Path destinoArchivo = d.resolve(o.relativize(f).toString()).normalize();
            if (!destinoArchivo.startsWith(d)) {
                continue;
            }
            if (Files.exists(destinoArchivo) && !sobrescribir) {
                continue;
            }
            Files.createDirectories(destinoArchivo.getParent());
            Files.copy(f, destinoArchivo, StandardCopyOption.REPLACE_EXISTING);
            copiados++;
        }
        return new int[]{total, copiados};
    }

    @Override
    public List<String> listar(Recurso recurso, String ruta) throws IOException {
        Path p = resolver(recurso, ruta);
        if (!Files.isDirectory(p)) {
            return Collections.emptyList();
        }
        List<String> carpetas = new ArrayList<>();
        List<String> archivos = new ArrayList<>();
        try (Stream<Path> s = Files.list(p)) {
            s.forEach(x -> {
                String nombre = x.getFileName().toString();
                if (Files.isDirectory(x)) {
                    carpetas.add(nombre + "/");
                } else {
                    archivos.add(nombre);
                }
            });
        }
        Collections.sort(carpetas);
        Collections.sort(archivos);
        carpetas.addAll(archivos);
        return carpetas;
    }

    @Override
    public long tamanoTotal(Recurso recurso) throws IOException {
        Path b = base(recurso);
        if (!Files.isDirectory(b)) {
            return 0L;
        }
        try {
            // Apache Commons IO calcula el tamano total de la carpeta (incluye subcarpetas)
            return FileUtils.sizeOfDirectory(b.toFile());
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }
}
