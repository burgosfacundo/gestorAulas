package org.example.repository;

import com.google.gson.Gson;
import org.example.exception.JsonNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para comunicarse con el JSON
 * @param <ID> Recibe el tipo del ID de T
 * @param <T> Recibe el tipo de Clase que se guarda en el JSON
 */
public interface JSONRepository <ID,T>{
    String getRuta();
    Gson getGson();
    void write(List<T> list) throws JsonNotFoundException;
    void save(T t) throws JsonNotFoundException;
    List<T> getAll() throws JsonNotFoundException;
    Optional<T> findById(ID id) throws JsonNotFoundException;
    void deleteById(ID id) throws JsonNotFoundException;
    void modify(T t) throws JsonNotFoundException;
}
