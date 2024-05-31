package com.fnvigg.csv2rdf;

import javafx.util.Pair;
import org.javatuples.Triplet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DslGenerator {

    private final String idProyecto;
    private Map<String, String> enumeradosPorClase;
    private String nombreProyecto = AtributosSesion.getNombreProyecto();
    private List<Pair<String, String>> clasesRutas;
    private Map<String, String> clasesPk;
    private List<Triplet<String, String, String>> atributosClases;
    private List<String> clases;

    //public DslGenerator(List<String> clases) {
    //    //Lanzar los metodos para escribir el codigo DSL
    //    this.clases = clases;
//
    //    limpiarDSL();
    //    try {
    //        crearFiles();
    //        crearPrefixes();
    //        crearClases();
    //    } catch (IOException e) {
    //        throw new RuntimeException(e);
    //    }
    //}

    public DslGenerator(String idProyecto, List<Pair<String, String>> clasesRutas,
                        Map<String, String> clasesPk, List<Triplet<String, String,
                        String>> atributosClases, Map<String, String> enumeradosPorClase) {
        this.clasesRutas = clasesRutas;
        this.clasesPk = clasesPk;
        this.atributosClases = atributosClases;
        this.idProyecto = idProyecto;
        this.clases = new ArrayList<>();
        this.enumeradosPorClase = enumeradosPorClase;
        limpiarDSL();
        try {
            crearClases();
            crearPrefixes();
            for(String clase : this.clases){
                crearSubjects(clase);
                crearPredicateObjects(clase);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void crearClases() throws IOException {

        String path = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(path);
        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        if(clasesRutas.size() != 0){
            for(Pair<String, String> claseRuta : clasesRutas) {
                String clase = claseRuta.getKey();
                clases.add(clase);
                String ruta = claseRuta.getValue();
                bw.write("FILE(#"+clase.replace(".csv", "")+" , " +ruta+")\n");
            }
        }

        bw.close();
        fw.close();
       // for(String clase : clases){
       //     String nombreClase = clase.replaceAll(".csv","");
       //     String rutaClase = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/" + nombreClase + "DSL.txt";
       //     File claseFile = new File(rutaClase);
       //     FileReader fr = new FileReader(claseFile);
       //     BufferedReader br = new BufferedReader(fr);
//
       //     String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/DSLCode.txt";
       //     File dslCode = new File(ruta);
       //     FileWriter fw = new FileWriter(dslCode, true);
       //     BufferedWriter bw = new BufferedWriter(fw);
//
       //     String linea = br.readLine();
       //     while(linea != null){
       //         bw.write(linea+"\n");
       //         linea = br.readLine();
       //     }
//
       //     bw.close();
       //     fw.close();
       //     br.close();
       //     fr.close();
       // }
    }


    private void limpiarDSL() {
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(ruta);
        if(dslCode.exists() && !dslCode.isDirectory()){
            dslCode.delete();
        }
    }

    private void crearFiles() throws IOException {


        //iterar el archivo clasesUML
        //A las clases escritas solo hay que añadirles el .csv para el source
        //String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesUML.txt";
        //File clasesUML = new File(ruta);
        //String rutaDSL = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/DSLCode.txt";
        //File dslCode = new File(rutaDSL);
        //if(clasesUML.exists() && !clasesUML.isDirectory()){
        //    FileReader fr = new FileReader(clasesUML);
        //    BufferedReader br = new BufferedReader(fr);
        //    FileWriter fw = new FileWriter(dslCode, true);
        //    BufferedWriter bw = new BufferedWriter(fw);
//
        //    String linea = br.readLine();
//
        //    if(linea != null){
        //        String[] tokens = linea.split(",");
        //        for(String clase : tokens){
        //            bw.write("FILE(#"+clase+", "+clase+".csv)\n");
        //        }
        //    }
//
        //    bw.close();
        //    fw.close();
        //    br.close();
        //    fr.close();
        //}else{
        //    System.out.println("Error al leer clasesUML");
        //}
        //System.out.println("clases escritas");
    }

    private void crearPrefixes() throws IOException {
        String rutaDSL = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(rutaDSL);

        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("PREFIX("+nombreProyecto+", http://www.example.com/"+nombreProyecto+"#)\n");

        bw.close();
        fw.close();
    }

    private void crearPredicateObjects(String clase) throws IOException {
        String rutaDSL = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(rutaDSL);

        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        for(Triplet<String, String, String> atributo : atributosClases){
            String claseAux = atributo.getValue0();
            if(clase.equals(claseAux)){
                String claseFormatted = claseAux.replace(".csv", "");
                String nombreAtt = atributo.getValue1();
                String valorAtt = atributo.getValue2();
                if(valorAtt.equals("xsd:integer") || valorAtt.equals("xsd:string") || valorAtt.equals("xsd:float") ||
                        valorAtt.equals("xsd:dateTime") || valorAtt.equals("xsd:boolean") || valorAtt.equals("xsd:decimal")){
                    bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtt+"),OBJECT("+nombreProyecto+":#"+claseFormatted+"."+nombreAtt+"))\n");
                } else if (valorAtt.equals("alt")) {
                    //Do something
                    String key = claseFormatted+nombreAtt;
                    String values = enumeradosPorClase.get(key);
                    bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtt+"),OBJECT(#"+claseFormatted+"."+nombreAtt+","+values+"\n");
                } else if (valorAtt.equals("bag")) {
                    //Do something

                } else if (valorAtt.equals("seq")) {
                    //Do something

                }else {
                    //Clase propia
                    bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtt+"),OBJECT("+nombreProyecto+":#"+claseFormatted+"."+nombreAtt+"))\n");
                }

            }

        }

        bw.close();
        fw.close();
    }

    private void crearSubjects(String clase) throws IOException {
        String rutaDSL = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(rutaDSL);

        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("SUBJECT(#"+clase.replace(".csv", "")+"."+clasesPk.get(clase)+","+nombreProyecto+":"+clase.replace(".csv", "")+")\n");

        bw.close();
        fw.close();
        //
    }
}
