module org.example.gestoraulas {
    requires org.slf4j;
    requires org.controlsfx.controls;
    requires jbcrypt;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.core;
    requires static lombok;
    requires java.naming;
    requires jfxtras.agenda;
    requires jakarta.persistence;
    requires spring.data.jpa;
    requires spring.data.commons;
    requires spring.orm;
    requires org.hibernate.orm.core;
    requires spring.tx;


    opens org.example.model to javafx.base, org.hibernate.orm.core, spring.core, spring.beans, spring.context;
    opens org.example.controller to javafx.fxml,spring.core,spring.context,org.slf4j;
    opens org.example.utils to spring.beans, spring.context, spring.core;
    opens org.example.security to spring.beans, spring.context, spring.core;
    opens org.example to spring.core, spring.context, spring.beans;
    opens org.example.service to spring.beans, spring.context,spring.core;
    opens org.example.repository to spring.beans, spring.context;


    exports org.example;
    exports org.example.controller;
    exports org.example.model;
    exports org.example.enums;
    exports org.example.service;
    exports org.example.repository;
    exports org.example.utils;
    exports org.example.security;
    exports org.example.exception;
    exports org.example.model.dto;
    exports org.example.controller.model.espacio.filtros;
    opens org.example.controller.model.espacio.filtros to javafx.fxml, org.slf4j, spring.context, spring.core;
    exports org.example.controller.menus;
    opens org.example.controller.menus to javafx.fxml, org.slf4j, spring.context, spring.core;
    exports org.example.controller.model;
    opens org.example.controller.model to javafx.fxml, org.slf4j, spring.context, spring.core;
    exports org.example.controller.model.solicitud.crear;
    opens org.example.controller.model.solicitud.crear to javafx.fxml, org.slf4j, spring.context, spring.core;
    exports org.example.controller.model.solicitud;
    opens org.example.controller.model.solicitud to javafx.fxml, org.slf4j, spring.context, spring.core;
    exports org.example.controller.model.espacio;
    opens org.example.controller.model.espacio to javafx.fxml, org.slf4j, spring.context, spring.core;
    exports org.example.controller.model.espacio.editar;
    opens org.example.controller.model.espacio.editar to javafx.fxml, org.slf4j, spring.context, spring.core;
    exports org.example.controller.model.usuario;
    opens org.example.controller.model.usuario to javafx.fxml, org.slf4j, spring.context, spring.core;
    exports org.example.controller.model.reserva;
    opens org.example.controller.model.reserva to javafx.fxml, org.slf4j, spring.context, spring.core;
    exports org.example.controller.model.reserva.crear;
    opens org.example.controller.model.reserva.crear to javafx.fxml, org.slf4j, spring.context, spring.core;
}
