package com.fnvigg.csv2rdf;

import javafx.util.Pair;
import org.javatuples.Quartet;
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

    // clase, nombreRuta , Archivos[], Atributos[]
    // Para el archivo[0], su atributo relevante es el atributos[0]
    private List<Quartet<String, String, ArrayList<String>, ArrayList<String>>> rutas;
    private List<String> clases;
    private List<Pair<String,String>> rutasAux;

    public DslGenerator(String idProyecto, List<Pair<String, String>> clasesRutas,
                        Map<String, String> clasesPk, List<Triplet<String, String,
                        String>> atributosClases, Map<String, String> enumeradosPorClase,
                        List<Quartet<String, String, ArrayList<String>, ArrayList<String>>> rutas,
                        List<Pair<String,String>> rutasAux) {
        this.clasesRutas = clasesRutas;
        this.clasesPk = clasesPk;
        this.atributosClases = atributosClases;
        this.idProyecto = idProyecto;
        this.clases = new ArrayList<>();
        this.rutas = rutas;
        this.rutasAux = rutasAux;
        this.enumeradosPorClase = enumeradosPorClase;
        limpiarDSL();
        try {
            crearClases();
            crearPrefixes();
            for(String clase : this.clases){
                crearSubjects(clase);
                crearPredicateObjects(clase);
                crearRutas(clase);
            }
            System.out.println("DSL Code generated");
            System.out.println("To visualize rdf graph: https://issemantic.net/rdf-visualizer");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void crearRutas(String clase) throws IOException {
        String rutaDSL = "./Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(rutaDSL);

        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        //RUTA POR CLASE
        for(Quartet<String, String, ArrayList<String>, ArrayList<String>> paths : rutas){
            String claseFormatted = clase.replace(".csv", "");
            String claseAux = paths.getValue0();
            if(claseFormatted.equals(claseAux)){
                String nombreAtrFinal = paths.getValue1();
                ArrayList<String> archivos = paths.getValue2();
                ArrayList<String> intermedios = paths.getValue3();
                StringBuilder cadena = new StringBuilder();
                String ultimoArchivo = "";
                for(int i = 0; i <= archivos.size()-1; i++){
                    String archivo = archivos.get(i);
                    String atributoIntermedio = intermedios.get(i);

                    if(i== archivos.size()-1){
                        //Ultimo archivo de la ruta
                        cadena.append("#" + archivo.replace(".csv", "") + "."+atributoIntermedio+"),");
                        ultimoArchivo = archivo.replace(".csv", "");
                    }else{
                        cadena.append("#" + archivo.replace(".csv", "") + "."+atributoIntermedio+",");
                    }
                }
                cadena.append(nombreProyecto+":#"+ultimoArchivo+"."+nombreAtrFinal+"))");

                bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtrFinal+"),OBJECT" +
                        "(QUERY(MATCH("+cadena+")\n");
            }

        }

        bw.close();
        fw.close();
    }


    private void crearClases() throws IOException {

        String path = "./Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(path);
        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        if(clasesRutas.size() != 0){
            for(Pair<String, String> claseRuta : clasesRutas) {
                String clase = claseRuta.getKey();
                clases.add(clase);
                String ruta = claseRuta.getValue();
                File f_Aux = new File(ruta);
                bw.write("FILE(#"+clase.replace(".csv", "")+" , " +f_Aux.getAbsolutePath()+")\n");
            }
        }

        if(rutasAux.size() != 0){
            for(Pair<String, String> claseRuta : rutasAux) {
                String clase = claseRuta.getKey();
                //clases.add(clase);
                String ruta = claseRuta.getValue();
                File f_Aux = new File(ruta);
                bw.write("FILE(#"+clase.replace(".csv", "")+" , " +f_Aux.getAbsolutePath()+")\n");
            }
        }
        bw.close();
        fw.close();

    }


    private void limpiarDSL() {
        String ruta = "./Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(ruta);
        if(dslCode.exists() && !dslCode.isDirectory()){
            dslCode.delete();
        }
    }

    private void crearPrefixes() throws IOException {
        String rutaDSL = "./Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(rutaDSL);

        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("PREFIX("+nombreProyecto+", http://www.example.com/"+nombreProyecto+"#)\n");

        bw.close();
        fw.close();
    }

    private void crearPredicateObjects(String clase) throws IOException {
        String rutaDSL = "./Proyectos/" + idProyecto + "/DSLCode.txt";
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
        String rutaDSL = "./Proyectos/" + idProyecto + "/DSLCode.txt";
        File dslCode = new File(rutaDSL);

        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("SUBJECT(#"+clase.replace(".csv", "")+"."+clasesPk.get(clase)+","+nombreProyecto+":"+clase.replace(".csv", "")+")\n");

        bw.close();
        fw.close();
        //
    }
}
