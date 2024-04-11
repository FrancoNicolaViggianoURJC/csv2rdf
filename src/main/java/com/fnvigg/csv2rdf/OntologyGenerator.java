package com.fnvigg.csv2rdf;

import javafx.fxml.Initializable;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class OntologyGenerator {
    //Dados clasesUML y atributosUML, producir la ontologia

    /*------------------------------------------------------------
    Strings que dependen del proyecto
     ------------------------------------------------------------*/
    private String nombreProyecto;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    private String ingDatos = new String();
    private String ingOntologico = new String();
    private String userSesion = new String();
    private String publisher = new String();
    private String descripcion = new String();
    private String description = new String();
    private String idProyecto = AtributosSesion.getIdProyecto();

    /*------------------------------------------------------------
                                Metodos
     ------------------------------------------------------------*/
    public OntologyGenerator(String nombreProyecto, String publisher, String descripcion, String description) throws IOException {
        this.nombreProyecto = nombreProyecto;
        this.publisher = publisher;
        this.descripcion = descripcion;
        this.description = description;

        //Elimina el archivo si existe para partir de uno limpio
        limpiarArchivo();

        //Escritura metadatos
        escribirDatos();

        //Luego de esto, iterar sobre los archivos y generar la ontologia
        crearClases();

        //Luego de las clases, los atributos
        crearAtributos();

        //Information
        System.out.println("Ontologia creada.");
    }

    private void limpiarArchivo() {
        String output = System.getProperty("user.dir") + "/src/main/resources/Proyectos/"+idProyecto+"/ontology.txt";
        File archivo = new File(output);

        if(archivo.exists() && !archivo.isDirectory()){
            archivo.delete();
        }
    }

    private void escribirDatos() throws IOException {
        /*
                    File writers
         */
        String output = System.getProperty("user.dir") + "/src/main/resources/Proyectos/"+idProyecto+"/ontology.txt";

        File archivo = new File(output);
        FileWriter fw = new FileWriter(archivo, true);
        BufferedWriter bw = new BufferedWriter(fw);
        /*
                    Strings cabecera
         */
        String ontologyInformation = "<!-- \n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "    //\n" +
                "    // Ontology Information\n" +
                "    //\n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "     -->\n";

        String definedBy = "http://www.example.com/"+nombreProyecto+"/ontology.rdf";
        String preferredNamespace = "http://www.example.com/"+nombreProyecto+"";
        String[] ingenieros = proyectos.obtenerPropiedades(idProyecto);
        ingDatos = ingenieros[0];
        ingOntologico = ingenieros[1];
        userSesion = AtributosSesion.getUser();

        //Cabecera rdf
        String namespaces = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<rdf:RDF \n" +
                "    xml:base=\"http://www.example.com/" + nombreProyecto + "#\"\n" +
                "    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
                "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                "    xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n" +
                "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
                "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
                "\txmlns:vann=\"http://purl.org/vocab/vann/\"\n" +
                "\txmlns:dcterms=\"http://purl.org/dc/terms/\"\n" +
                "\txmlns:gn=\"http://www.geonames.org/ontology#\"\t\n" +
                "\txmlns:vSont=\"http://www.example.com/" + nombreProyecto + "#\">\n";

        //Descripcion
        Calendar c = new GregorianCalendar();
        String dia = Integer.toString(c.get(Calendar.DATE));
        String mes = Integer.toString(c.get(Calendar.MONTH) + 1);
        String año = Integer.toString(c.get(Calendar.YEAR));
        String fecha = año + "-" + mes + "-" + dia;

        String information = "\n" +
                "    <!-- \n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "    //\n" +
                "    // Ontology Information\n" +
                "    //\n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "     -->\n \n"+"\t<owl:Ontology rdf:about=\"http://www.example.com/"+nombreProyecto+"#\">\n" +
                "\t\t<dc:title>"+nombreProyecto+"</dc:title>\n" +
                "\t\t<rdfs:label>"+nombreProyecto+"</rdfs:label>\n" +
                "\t\t<owl:versionInfo>Version 1.0 -"+ fecha +"</owl:versionInfo>        \n" +
                "        <dc:creator>"+ingDatos+"</dc:creator>\n" +
                "\t\t<dc:creator>"+ingOntologico+"</dc:creator>\n" +
                "        <dc:description xml:lang=\"es\">"+descripcion+"</dc:description>\n" +
                "        <dc:publisher>"+this.publisher+"</dc:publisher>\n" +
                "        <dc:rights>CC BY 4.0</dc:rights>\n" +
                "        <dc:subject>"+this.descripcion+"</dc:subject>   \n" +
                "\t\t<rdfs:isDefinedBy rdf:resource=\""+definedBy+"\"/> \t\t\n" +
                "\t\t<rdfs:comment xml:lang=\"en\">"+this.description+"</rdfs:comment>\n" +
                "\t\t<dcterms:issued>"+fecha+"</dcterms:issued>\t\n" +
                "\t\t<dcterms:modified>"+fecha+"</dcterms:modified>\t\n" +
                "\t\t<vann:preferredNamespacePrefix>vSont</vann:preferredNamespacePrefix>\n" +
                "\t\t<vann:preferredNamespaceUri>"+preferredNamespace+"#</vann:preferredNamespaceUri>\n" +
                "    </owl:Ontology>\n";

        String annotations = "\n <!-- \n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "    //\n" +
                "    // Annotation properties\n" +
                "    //\n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "     -->\n" +
                "\t\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/dc/elements/1.1/title\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://www.w3.org/2000/01/rdf-schema#label\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://www.w3.org/2002/07/owl#versionInfo\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/dc/elements/1.1/creator\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/dc/elements/1.1/description\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/dc/elements/1.1/publisher\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/dc/elements/1.1/rights\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/dc/elements/1.1/subject\"/>\t\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://www.w3.org/2000/01/rdf-schema#isDefinedBy\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://www.w3.org/2000/01/rdf-schema#comment\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/dc/terms/issued\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/dc/terms/modified\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/vocab/vann/preferredNamespacePrefix\"/>\n" +
                "\t<owl:AnnotationProperty rdf:about=\"http://purl.org/vocab/vann/preferredNamespaceUri\"/>\n";

        //Escribir los strings en el archivo
        bw.write(ontologyInformation);
        bw.write(namespaces);
        bw.write(information);
        bw.write(annotations);

        bw.close();
        fw.close();
    }

    private void crearClases() throws IOException {
        String input = System.getProperty("user.dir") + "/src/main/resources/Proyectos/"+idProyecto+"/clasesUML.txt";
        FileReader fr = new FileReader(input);
        BufferedReader br = new BufferedReader(fr);

        String output = System.getProperty("user.dir") + "/src/main/resources/Proyectos/"+idProyecto+"/ontology.txt";

        File archivo = new File(output);
        FileWriter fw = new FileWriter(archivo, true);
        BufferedWriter bw = new BufferedWriter(fw);

        String header = "\n <!-- \n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "    //\n" +
                "    // Classes\n" +
                "    //\n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "     -->\n \n";
        bw.write(header);

        String linea = br.readLine();
        String[] tokens;
        if(linea != null){
            //Obtener cada clase, y escribirla
            tokens = linea.split(",");
            for(String clase : tokens){
                bw.write("<owl:Class rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+clase+"\">\n" +
                        "\t\t<rdfs:label xml:lang=\"en\">"+clase+"</rdfs:label>\n" +
                        "    </owl:Class>\n");
            }
        }

        br.close();
        fr.close();
        bw.close();
        fw.close();
    }

    private void crearAtributos() {
        //Separar los atributos en ObjectProperties y DatatypeProperties

        try {
            Pair<LinkedList<String>,LinkedList<String>> atributos = proyectos.obtenerAtributosObject(nombreProyecto);
            LinkedList<String> objects = atributos.getKey();
            LinkedList<String> datatypes = atributos.getValue();
            escribirObjects(objects);
            escribirDatatypes(datatypes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void escribirDatatypes(LinkedList<String> datatypes) throws IOException {

        String output = System.getProperty("user.dir") + "/src/main/resources/Proyectos/"+idProyecto+"/ontology.txt";

        File archivo = new File(output);
        FileWriter fw = new FileWriter(archivo, true);
        BufferedWriter bw = new BufferedWriter(fw);

        String headerData = "\t\t\t\n" +
                "    <!-- \n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "    //\n" +
                "    // Data properties\n" +
                "    //\n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "     -->\t\n" +
                "\n";
        bw.write(headerData);

        for(String tipo : datatypes){
            //nombreAtributo, tipoAtributo, clasePertenece
            String[] campos = tipo.split(",");
            if(campos[1].equals("xsd:integer")){
                bw.write("\n" +
                        "    <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#integer\"/>\n" +
                        "    </owl:DatatypeProperty>\t \n" +
                        "\t\n");
            }else if(campos[1].equals("xsd:string")){
                bw.write("\n" +
                        "    <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#string\"/>\n" +
                        "    </owl:DatatypeProperty>\t \n" +
                        "\t\n");
            }else if(campos[1].equals("xsd:float")){
                bw.write("\n" +
                        "    <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#float\"/>\n" +
                        "    </owl:DatatypeProperty>\t \n" +
                        "\t\n");
            }else if(campos[1].equals("xsd:dateTime")){
                bw.write("\n" +
                        "    <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#dateTime\"/>\n" +
                        "    </owl:DatatypeProperty>\t \n" +
                        "\t\n");
            }else if(campos[1].equals("xsd:boolean")){
                bw.write("\n" +
                        "    <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#boolean\"/>\n" +
                        "    </owl:DatatypeProperty>\t \n" +
                        "\t\n");
            }else if(campos[1].equals("xsd:decimal")){
                bw.write("\n" +
                        "    <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#decimal\"/>\n" +
                        "    </owl:DatatypeProperty>\t \n" +
                        "\t\n");
            }
        }
        bw.write("</rdf:RDF>\t ");

        bw.close();
        fw.close();
    }

    private void escribirObjects(LinkedList<String> objects) throws IOException {
        String output = System.getProperty("user.dir") + "/src/main/resources/Proyectos/"+idProyecto+"/ontology.txt";

        File archivo = new File(output);
        FileWriter fw = new FileWriter(archivo, true);
        BufferedWriter bw = new BufferedWriter(fw);

        String headerObject = "\n <!-- \n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "    //\n" +
                "    // Object Properties\n" +
                "    //\n" +
                "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
                "     -->\n \n";

        bw.write(headerObject);
        for(String objeto : objects){
            //nombre, tipo, clasePertence
            String[] campos = objeto.split(",");

            if(campos[1].equals("alt")){
                bw.write("\n" +
                        "    <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Alt\"/>\n" +
                        "    </owl:DatatypeProperty>\t \n" +
                        "\t\n");
            }else if(campos[1].equals("bag")){
                bw.write("\n" +
                        "    <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag\"/>\n" +
                        "    </owl:DatatypeProperty>\t \n" +
                        "\t\n");
            }else if(campos[1].equals("seq")){
                bw.write("\n" +
                        "    <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#Seq\"/>\n" +
                        "    </owl:DatatypeProperty>\t \n" +
                        "\t\n");
            }else{
                bw.write("\t \t \n" +
                        "\t <owl:DatatypeProperty rdf:about=\"http://www.example.com/"+nombreProyecto+"#"+campos[0]+"\">\n" +
                        "        <rdfs:domain rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[2]+"\"/>\n" +
                        "        <rdfs:range rdf:resource=\"http://www.example.com/"+nombreProyecto+"#"+campos[1]+"\"/>\n" +
                        "    </owl:DatatypeProperty>\t\n" +
                        "\t\n");
            }
        }
        bw.close();
        fw.close();
    }

}
