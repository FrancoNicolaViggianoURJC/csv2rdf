package com.fnvigg.csv2rdf;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class DslGenerator {
    //Reglas de transformacion

    private String nombreProyecto = AtributosSesion.getNombreProyecto();
    /*
    Por cada clase OWL:
        - FILE statement con dos parametros:
            1): #nombreClase
            2): archivo fuente asociado
            FILE(#country, country.csv)

        - SUBJECT statement con dos parametros:
            1): #nombreClase.keyField
            2): ontologyAlias:className
            SUBJECT(#country.country_id, pruebaAlias.country)
    El alias de la ontologia se define por:
        - PREFIX statement con dos parametros:
            1): ontologyAlias
            2): http://www.example.com/prueba#
            PREFIX(pruebaAlias, http://www.example.com/prueba#)

    Por cada OWL Datatype Property
        - PREDICATE-OBJECT:
            1): PREDICATE (ontologyAlias:propertyName)
            2): OBJECT (nombreClase.nombrePropiedad)
            PREDICATE-OBJECT(PREDICATE(pruebaAlias)

    Por cada OWL Object Property
        - PREDICATE-OBJECT:
            1): PREDICATE (ontologyAlias:propertyName)
            2): OBJECT (ontologyAlias:#classIdentifier.propertyName)
                                            propiedad            ////               tipo_clase
            PREDICATE-OBJECT(PREDICATE(pruebaAlias:country_id), OBJECT(pruebaAlias:#Country.country_id))



        - PREDICATE-OBJECT:
            1): PREDICATE (ontologyAlias:propertyName)
            2): OBJECT (ontologyAlias:#classIdentifier.propertyName)
                                            propiedad            ////               tipo_ALT
             PREDICATEOBJECT (PREDICATE (pruebaAlias:report_type), OBJECT (#country.report_type, VALUES (1,2,3), ENUMS(Tech, finance,other)))
             //Se intenta sustituir los valores por el literal
             //-----------------------> Preguntar al usuario??

       - PREDICATE-OBJECT:
            1): PREDICATE (ontologyAlias:propertyName)
            2): OBJECT (ontologyAlias:#classIdentifier.propertyName)
                                         propiedad            ////               tipo_BAG / SEQ
            PREDICATE-OBJECT(PREDICATE(pruebaAlias:atributoBAG), OBJECT(CONTAINER(SEQ|BAG)))

     */
    Map<String, String> keyFlds;
    ArrayList<Map<String, String>> enumerados;
    LinkedList<String> clases;
    LinkedList<String> atributosEscritos;
    Gestor_proyectos proyectos = new Gestor_proyectos();
    public DslGenerator(Map<String, String> keyFlds, ArrayList<Map<String, String>> enumerados) {
        //Lanzar los metodos para escribir el codigo DSL
        this.keyFlds = keyFlds;
        this.enumerados = enumerados;
        this.clases = new LinkedList<>(); //Se rellenará en crearFiles
        this.atributosEscritos = new LinkedList<>();

        limpiarDSL();
        try {
            crearFiles();
            crearPrefixes();
            //Hay que cambiar la estructura, se tiene que crear por clases y no por tipos
            //      for(clase c : clases)do
            //          crearClase();
            //              ->crearSubjects();
            //              ->crearAtributos();
            //              ->crearEnumerados();
            //      repeat until no mas clases
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void crearClase() {
    }

    private void crearAtributos() throws IOException {
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/DSLCode.txt";
        File dslCode = new File(ruta);
        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        Pair<LinkedList<String>,LinkedList<String>> atributos = proyectos.obtenerAtributosObject(nombreProyecto);
        LinkedList<String> objects = atributos.getKey();
        LinkedList<String> datatypes = atributos.getValue();

        for(String objeto : datatypes){
            String[] tokens = objeto.split(",");
            String nombreAtr = tokens[0];
            String clase = tokens[2];
            bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtr+"),OBJECT("+nombreProyecto+":#"+clase+"."+nombreAtr+"))\n");
        }

        for(String objeto : objects){
            String[] tokens = objeto.split(",");
            String nombreAtr = tokens[0];
            String tipo = tokens[1];
            String clase = tokens[2];
            if(tipo.equals("bag")){
                //escribir collection
                bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtr+"),OBJECT(CONTAINER(BAG,#"+clase+"."+nombreAtr+")))\n");
            }else if(tipo.equals("seq")){
                //Collection ord
                bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtr+"),OBJECT(CONTAINER(SEQ,#"+clase+"."+nombreAtr+")))\n");
            }

        }
        bw.close();
        fw.close();
    }

    private void crearEnumerados() throws IOException {
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/DSLCode.txt";
        File dslCode = new File(ruta);
        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        for(int i = 0; i <= enumerados.size()-1; i++){
            String clase = clases.get(i);
            Map<String, String> mapa = enumerados.get(i);
            for(String key : mapa.keySet()){
                // key -> nombre atributo
                //valor -> VALUES(..),ENUM(...)))
                String valor = mapa.get(key);
                bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+key+"),OBJECT(#"+clase+"."+key+","+valor+"\n");
                atributosEscritos.add(key);
            }
        }

        bw.close();
        fw.close();
    }

    private void crearSubjects() throws IOException {
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/DSLCode.txt";
        File dslCode = new File(ruta);
        FileWriter fw = new FileWriter(dslCode, true);
        BufferedWriter bw = new BufferedWriter(fw);

        for(String key : keyFlds.keySet()){
            // key -> clase
            // valor -> nombreAtr
            String clave = keyFlds.get(key);
            bw.write("SUBJECT(#"+key+"."+clave+","+nombreProyecto+":"+key+")\n");
            atributosEscritos.add(clave);
        }

        bw.close();
        fw.close();
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
                    clases.add(clase);
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

        //Pedir el alias si lo quiere especifico?
        bw.write("PREFIX("+nombreProyecto+", http://www.example.com/"+nombreProyecto+"#)\n");

        bw.close();
        fw.close();
    }
}
