package com.fnvigg.csv2rdf;

import java.io.File;
import java.io.InputStream;

public class GraphGenerator {
    private String nombreProyecto = AtributosSesion.getNombreProyecto();

    public GraphGenerator() {
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/output.rdf";
        File rdf = new File(ruta);

        
    }
}
