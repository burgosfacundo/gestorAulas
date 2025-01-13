package org.example.adaptadores;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*
Gson no soporta nativamente LocalDate y otros tipos de Java java.time como parte de la serialización/deserialización de JSON.
Para resolver esto, se creó un TypeAdapter personalizado para LocalDate.

Contexto de uso:
Al crear una reserva o inscripción, que tienen atributos del tipo LocalDate
Si no fuese por el adaptador, se lanzaría una excepción y no permitiría el registro de la reserva o inscripción en el Json.
 */

public class AdaptadorLocalDate extends TypeAdapter<LocalDate> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        out.value(value != null ? value.format(formatter) : null);
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        String date = in.nextString();
        return date != null ? LocalDate.parse(date, formatter) : null;
    }
}
