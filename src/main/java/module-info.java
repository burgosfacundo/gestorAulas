module org.example.gestoraulas {
    requires org.slf4j;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.google.gson;
    requires jbcrypt;
    requires java.desktop;
    requires java.logging;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires javafx.controls;
    requires spring.core;
    requires static lombok;

    opens org.example.model to com.google.gson, javafx.base;
    opens org.example.model.dto to com.google.gson;
    opens org.example.enums to com.google.gson;
    opens org.example.controller to javafx.fxml,spring.core,spring.context,org.slf4j;
    opens org.example.config to spring.context,spring.core,spring.beans;
    opens org.example.utils to spring.beans, spring.context, spring.core;
    opens org.example.security to spring.beans, spring.context, spring.core;
    opens org.example to spring.core, spring.context, spring.beans;
    opens org.example.service to spring.beans, spring.context;
    opens org.example.repository to spring.beans, spring.context;


    exports org.example;
    exports org.example.controller;
    exports org.example.model;
    exports org.example.enums;
    exports org.example.service;
    exports org.example.repository;
    exports org.example.config;
    exports org.example.utils;
    exports org.example.security;
    exports org.example.exception;
    exports org.example.model.dto;
}
