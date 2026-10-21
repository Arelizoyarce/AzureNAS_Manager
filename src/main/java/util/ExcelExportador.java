package util;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Genera archivos Excel (.xlsx) con Apache POI.
 * Encabezado azul con letras blancas, filtros, fila congelada y anchos de columna calculados.
 * Los valores numericos se guardan como numeros y el resto como texto.
 */
public final class ExcelExportador {

    private static final int ANCHO_MIN = 10;
    private static final int ANCHO_MAX = 60;
    /** Excel no admite mas de 32767 caracteres por celda. */
    private static final int MAX_TEXTO = 32000;

    private ExcelExportador() {
    }

    public static void escribir(String nombreHoja, List<String> encabezados, List<List<Object>> filas,
            OutputStream salida) throws IOException {
        Preconditions.checkArgument(encabezados != null && !encabezados.isEmpty(), "Faltan los encabezados");
        Preconditions.checkNotNull(filas, "filas");

        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            Sheet hoja = libro.createSheet(WorkbookUtil.createSafeSheetName(nombreHoja));

            Font letraEncabezado = libro.createFont();
            letraEncabezado.setBold(true);
            letraEncabezado.setColor(IndexedColors.WHITE.getIndex());
            CellStyle estiloEncabezado = libro.createCellStyle();
            estiloEncabezado.setFont(letraEncabezado);
            estiloEncabezado.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloEncabezado.setAlignment(HorizontalAlignment.CENTER);

            CellStyle estiloDatos = libro.createCellStyle();
            estiloDatos.setBorderBottom(BorderStyle.THIN);
            estiloDatos.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

            int columnas = encabezados.size();
            int[] ancho = new int[columnas];

            Row filaEncabezado = hoja.createRow(0);
            for (int c = 0; c < columnas; c++) {
                Cell celda = filaEncabezado.createCell(c);
                celda.setCellValue(encabezados.get(c));
                celda.setCellStyle(estiloEncabezado);
                ancho[c] = encabezados.get(c).length();
            }

            int numeroFila = 1;
            for (List<Object> datos : filas) {
                Row fila = hoja.createRow(numeroFila++);
                for (int c = 0; c < columnas && c < datos.size(); c++) {
                    Object valor = datos.get(c);
                    Cell celda = fila.createCell(c);
                    celda.setCellStyle(estiloDatos);
                    if (valor == null) {
                        continue;
                    }
                    if (valor instanceof Number) {
                        celda.setCellValue(((Number) valor).doubleValue());
                        ancho[c] = Math.max(ancho[c], String.valueOf(valor).length());
                    } else {
                        String texto = StringUtils.left(String.valueOf(valor), MAX_TEXTO);
                        celda.setCellValue(texto);
                        ancho[c] = Math.max(ancho[c], texto.length());
                    }
                }
            }

            for (int c = 0; c < columnas; c++) {
                int caracteres = Math.min(ANCHO_MAX, Math.max(ANCHO_MIN, ancho[c] + 2));
                hoja.setColumnWidth(c, caracteres * 256);
            }
            hoja.createFreezePane(0, 1);
            hoja.setAutoFilter(new CellRangeAddress(0, 0, 0, columnas - 1));

            libro.write(salida);
        }
    }
}
