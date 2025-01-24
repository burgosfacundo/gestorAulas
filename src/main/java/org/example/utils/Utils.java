package org.example.utils;

import org.example.enums.BloqueHorario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;


public class Utils {
    /**
     * Obtiene los nombres de los días de la semana en español.
     * @param diasSemana Set de DayOfWeek con los días de la semana.
     * @return String con los nombres de los días separados por coma.
     */
    public static String obtenerDiasEnEspaniol(Set<DayOfWeek> diasSemana) {
        return diasSemana.stream()
                .map(day -> day.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")))
                .map(day -> day.substring(0, 1).toUpperCase() + day.substring(1))
                .collect(Collectors.joining(", "));
    }

    //Métodos para formatear los toString
    /**
     * Añade indentación a cada línea de un texto.
     * @param text Texto al que se le aplicará la indentación.
     * @param indentSpaces Cantidad de espacios para la indentación.
     * @return String con el texto indentado.
     */
    public static String indentString(String text, int indentSpaces) {
        var indent = " ".repeat(indentSpaces);
        return Arrays.stream(text.split("\n"))
                .map(line -> indent + line)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Formatear un mapa de días de la semana y bloques horarios.
     * @param diasYBloques Mapa de DayOfWeek a Set de BloqueHorario.
     * @return String con el formato de días y bloques horarios.
     */
    public static String formatDiasYBloques(Map<DayOfWeek, Set<BloqueHorario>> diasYBloques) {
        var sb = new StringBuilder();
        diasYBloques.forEach((dia, bloques) -> {
            // Usamos obtenerDiasEnEspaniol para obtener el nombre del día en español
            var diaEnEspaniol = obtenerDiasEnEspaniol(Set.of(dia));
            var bloquesFormateados = bloques.stream()
                    .map(BloqueHorario::toString)
                    .collect(Collectors.joining(", "));

            sb.append(String.format("[ %s: %s ]\n", diaEnEspaniol, bloquesFormateados));
        });
        return sb.toString().trim(); // Eliminar último salto de línea
    }

    /**
     * Formatea listas de elementos a texto.
     * Convierte cada elemento en una cadena y lo lista con un prefijo "-".
     * @param items Lista de elementos a formatear.
     * @param <T> Tipo genérico de los elementos.
     * @return String con cada elemento en una nueva línea con un prefijo "-".
     */
    public static <T> String formatListItems(List<T> items) {
        return items.stream()
                .map(item -> String.format("   - %s", item.toString())) // Convierte cada elemento a String
                .collect(Collectors.joining("\n"));
    }

    /**
     * Verifica si dos períodos de fechas se solapan.
     * @param fechaInicio1 inicio del primer período.
     * @param fechaFin1 fin del primer período.
     * @param fechaInicio2 inicio del segundo período.
     * @param fechaFin2 fin del segundo período.
     * @return boolean si los períodos se solapan o no.
     */
    public static boolean seSolapanFechas(LocalDate fechaInicio1, LocalDate fechaFin1, LocalDate fechaInicio2, LocalDate fechaFin2) {
        return !fechaFin1.isBefore(fechaInicio2) && !fechaInicio1.isAfter(fechaFin2);
    }

    /**
     * Verifica si hay solapamiento en los días y bloques horarios entre dos mapas.
     * @param diasYBloquesReserva días y bloques horarios de una reserva existente.
     * @param diasYBloquesSolicitados días y bloques horarios solicitados para disponibilidad.
     * @return boolean si existe al menos un día y bloque horario común.
     */
    public static boolean tieneSolapamientoEnDiasYBloques(Map<DayOfWeek, Set<BloqueHorario>> diasYBloquesReserva,
                                                          Map<DayOfWeek, Set<BloqueHorario>> diasYBloquesSolicitados) {
        for (var entry : diasYBloquesSolicitados.entrySet()) {
            var diaSolicitado = entry.getKey();
            var bloquesSolicitados = entry.getValue();

            if (diasYBloquesReserva.containsKey(diaSolicitado)) {
                var bloquesReservados = diasYBloquesReserva.get(diaSolicitado);
                // Verificamos si hay al menos un bloque horario en común
                if (bloquesReservados.stream().anyMatch(bloquesSolicitados::contains)) {
                    return true;
                }
            }
        }
        return false;
    }

}

