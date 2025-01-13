package org.example.model;


import lombok.*;


@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Asignatura {
    @EqualsAndHashCode.Include
    private Integer id;
    @EqualsAndHashCode.Include
    private String nombre;
    @EqualsAndHashCode.Include
    private int codigo;
    boolean requiereLaboratorio;
}
