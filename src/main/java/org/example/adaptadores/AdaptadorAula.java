package org.example.adaptadores;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.model.Aula;
import org.example.model.Laboratorio;

import java.io.IOException;

public class AdaptadorAula extends TypeAdapter<Aula> {
    private final Gson gson;

    public AdaptadorAula(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, Aula aula) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", aula.getId());
        jsonObject.addProperty("numero", aula.getNumero());
        jsonObject.addProperty("capacidad", aula.getCapacidad());
        jsonObject.addProperty("tieneProyector", aula.isTieneProyector());
        jsonObject.addProperty("tieneTV", aula.isTieneTV());

        if (aula instanceof Laboratorio laboratorio) {
            jsonObject.addProperty("type", "Laboratorio");
            jsonObject.addProperty("computadoras", laboratorio.getComputadoras());
        } else {
            jsonObject.addProperty("type", "Aula");
        }

        gson.toJson(jsonObject, out);
    }

    @Override
    public Aula read(JsonReader in) {
        JsonObject jsonObject = JsonParser.parseReader(in).getAsJsonObject();

        // Verificar el campo `type` para determinar el subtipo
        String type = jsonObject.get("type").getAsString();

        Aula aula;
        if ("Laboratorio".equalsIgnoreCase(type)) {
            aula = gson.fromJson(jsonObject, Laboratorio.class);
        } else {
            aula = gson.fromJson(jsonObject, Aula.class);
        }

        return aula;
    }
}
