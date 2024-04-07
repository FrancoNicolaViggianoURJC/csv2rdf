package com.fnvigg.csv2rdf;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DslGenerator {

    private String nombreProyecto = AtributosSesion.getNombreProyecto();
    List<String> clases;

    public DslGenerator(List<String> clases) {
        //Lanzar los metodos para escribir el codigo DSL
        this.clases = clases;

        limpiarDSL();
        try {
            crearFiles();
            crearPrefixes();
            crearClases();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void crearClases() throws IOException {
        for(String clase : clases){
            String nombreClase = clase.replaceAll(".csv","");
            String rutaClase = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/" + nombreClase + "DSL.txt";
            File claseFile = new File(rutaClase);
            FileReader fr = new FileReader(claseFile);
            BufferedReader br = new BufferedReader(fr);

            String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/DSLCode.txt";
            File dslCode = new File(ruta);
            FileWriter fw = new FileWriter(dslCode, true);
            BufferedWriter bw = new BufferedWriter(fw);

            String linea = br.readLine();
            while(linea != null){
                bw.write(linea+"\n");
                linea = br.readLine();
            }

            bw.close();
            fw.close();
            br.close();
            fr.close();
        }
    }


    private void limpiarDSL() {
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/DSLCode.txt";
        File dslCode = new File(ruta);
        if(dslCode.exists() && !dslCode.isDirectory()){
            dslCode.delete();
        }
    }

    private void crearFiles() throws IOException {
        //iterar el archivo clasesUML
        //A las clases escritas solo hay que añadirles el .csv para el source
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesUML.txt";
        File clasesUML = new File(ruta);
        String rutaDSL = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/DSLCode.txt";
        File dslCode = new File(rutaDSL);
        if(clasesUML.exists() && !clasesUML.isDirectory()){
            FileReader fr = new FileReader(clasesUML);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter(dslCode, true);
            BufferedWriter bw = new BufferedWriter(fw);

            String linea = br.readLine();

            if(linea != null){
                String[] tokens = linea.split(",");
                for(String clase : tokens){
                    bw.write("FILE(#"+clase+", "+clase+".csv)\n");
                }
            }

            bw.close();
            fw.close();
            br.close();
            fr.close();
        }else{
            System.out.println("Error al leer clasesUML");
        }
        System.out.println("clases escritas");
    }

    private void crearPrefixes() throws IOException {
        String rutaDSL = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/DSLCode.txt";
        File dslCode = new File(rutaDSL);

        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("PREFIX("+nombreProyecto+", http://www.example.com/"+nombreProyecto+"#)\n");

        bw.close();
        fw.close();
    }
}
