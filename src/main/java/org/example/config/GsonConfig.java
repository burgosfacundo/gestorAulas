package org.example.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.adaptadores.AdaptadorAula;
import org.example.model.Aula;


/**
 * Clase para configurar GSON
 */
public class GsonConfig {
    /**
     * Método para crear un GSON que use AulaTypeAdapter para serialización y deserialización polimórfica
     * @return Gson configurado para Aula y Laboratorio
     */
    public static Gson createGsonAulaLaboratorio() {
        GsonBuilder builder = new GsonBuilder();

        // Registrar el TypeAdapter para Aula
        builder.registerTypeAdapter(Aula.class, new AdaptadorAula(builder.create()));
        builder.setPrettyPrinting();

        return builder.create();
    }
}


