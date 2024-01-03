package com.fnvigg.csv2rdf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
                fichero.listFiles();
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

    public void borrarProyecto(String proyecto) {
        File dir = new File("src/main/resources/Proyectos/"+ proyecto);
        if (dir!=null){
            //Para borrar el directorio, hay que borrar recursivamente los ficheros contenidos
            File[] ficheros = dir.listFiles();
            if(ficheros != null){
                for(File file : ficheros){
                    Boolean report = file.delete();
                }
            }
            //Una vez vacio, lo borramos
            dir.delete();
        }else{
            System.out.println("error");
        }

    }

    public void crearProyecto(String nombreProyecto, String nombreOntologico, String nombreDato) {
        //Creacion de directorio
        File dir = new File("src/main/resources/Proyectos/"+nombreProyecto);
        dir.mkdir();
        //Creacion del fichero de configuracion
        File config = new File("src/main/resources/Proyectos/"+nombreProyecto+"/"+"config.txt");
        try {
            config.createNewFile();
            FileWriter fr = new FileWriter(config, true);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(nombreOntologico + "," + nombreDato);

            br.close();
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
