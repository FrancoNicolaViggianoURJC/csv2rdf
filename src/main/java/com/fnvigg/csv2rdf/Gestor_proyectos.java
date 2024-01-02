package com.fnvigg.csv2rdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Gestor_proyectos {

    public Gestor_proyectos() {
    }

    public List<String> getProyectos() throws IOException {
        List<String> filenames = new LinkedList<String>();
        //pasar esto al gestor proyectos
        File directorio = new File("src/main/resources/Proyectos");
        File[] listado = directorio.listFiles();
        if(listado != null){
            for(File fichero : listado){
                filenames.add(fichero.getName());
            }
        }else{
            //no existe el directorio
            //crear directorio
            Path path = Paths.get("src/main/resources/Proyectos");
            Files.createDirectories(path);
        }
        return filenames;
    }

}
