package com.fnvigg.csv2rdf;

public class AtributosSesion {

    private static String user;
    private static String rol;
    private static String nombreProyecto;

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
        AtributosSesion.nombreProyecto = nombreProyecto;
    }
}
