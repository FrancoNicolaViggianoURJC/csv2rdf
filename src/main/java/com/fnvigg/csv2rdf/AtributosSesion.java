package com.fnvigg.csv2rdf;

import javafx.util.Pair;

import java.util.List;

public class AtributosSesion {

    private static String user;
    private static String rol;
    private static String nombreProyecto;

    private static String ultimaPantalla;
    private static String idProyecto;

    private static List<Pair<String, String>> clasesStatic; // (idArchivo, NombreArchivo)
    private static List<Pair<String, List<Pair<String, String>>>> atributosStatic; //([Clase, ([nombreAtributo, valorAtributo])])

    public static List<Pair<String, String>> getClasesStatic() {
        return clasesStatic;
    }

    public static void setClasesStatic(List<Pair<String, String>> clasesStatic) {
        AtributosSesion.clasesStatic = clasesStatic;
    }

    public static List<Pair<String, List<Pair<String, String>>>> getAtributosStatic() {
        return atributosStatic;
    }

    public static void setAtributosStatic(List<Pair<String, List<Pair<String, String>>>> atributosStatic) {
        AtributosSesion.atributosStatic = atributosStatic;
    }

    public static void setUser(String user){
        AtributosSesion.user = user;
    }

    public static String getUser(){
        return user;
    }

    public static String getRol() {
        return rol;
    }

    public static void setRol(String rol) {
        AtributosSesion.rol = rol;
    }

    public static String getNombreProyecto() {
        return nombreProyecto;
    }

    public static void setNombreProyecto(String nombreProyecto) {
        AtributosSesion.nombreProyecto = nombreProyecto.replace(" ", "");
    }

    public static String getUltimaPantalla() {
        return ultimaPantalla;
    }

    public static void setUltimaPantalla(String ultimaPantalla) {
        AtributosSesion.ultimaPantalla = ultimaPantalla;
    }

    public static String getIdProyecto() {
        return idProyecto;
    }

    public static void setIdProyecto(String idProyecto) {
        AtributosSesion.idProyecto = idProyecto;
    }
}
