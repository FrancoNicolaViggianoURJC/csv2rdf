package com.fnvigg.csv2rdf;

import javafx.fxml.Initializable;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class OntologyGenerator {
    //Dados clasesUML y atributosUML, producir la ontologia

    /*------------------------------------------------------------
    *   Output/Input files
    * ------------------------------------------------------------*/
    private File archivo;
    private FileWriter fw;
    private BufferedWriter bw;
    private FileReader fr;
    private BufferedReader br;
    /*------------------------------------------------------------
    Strings que dependen del proyecto
     ------------------------------------------------------------*/
    private String nombreProyecto;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    private String ingDatos = new String();
    private String ingOntologico = new String();
    private String userSesion = new String();


    /*------------------------------------------------------------

    String estáticos
     ------------------------------------------------------------*/
    private String namespaces;
    private String information;
    private String annotations;
    private String ontologyInformation = "<!-- \n" +
            "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
            "    //\n" +
            "    // Ontology Information\n" +
            "    //\n" +
            "    ///////////////////////////////////////////////////////////////////////////////////////\n" +
            "     -->\n";


    /*------------------------------------------------------------
                                Metodos
     ------------------------------------------------------------*/
    public OntologyGenerator(String nombreProyecto) throws IOException {
        this.nombreProyecto = nombreProyecto;


        /*
                    File writers
         */
        String output = System.getProperty("user.dir") + "/src/main/resources/Proyectos/"+nombreProyecto+"/ontology.txt";

        archivo = new File(output);
        FileWriter fw = new FileWriter(archivo, true);

        /*
                    Strings cabecera
         */
        String definedBy = "http://www.example.com/visitorsSpainOnt/ontology_v1.1_COVID-Ont.rdf";
        String preferredNamespace = "http://www.example.com/visitorsSpainOnt";
        String publisher = "PEDIR PUBLISHER";
        String descripcion = "HAY QUE PEDIR LA DESCRIPCION EN ALGUN MOMENTO";
        String descripcionEng = "HAY QUE PEDIR LA DESCRIPCION EN INGLES EN ALGUN MOMENTO";
        String[] ingenieros = proyectos.obtenerPropiedades(nombreProyecto);
        ingDatos = ingenieros[0];
        ingOntologico = ingenieros[1];
        userSesion = AtributosSesion.getUser();

        //Cabecera rdf
        namespaces = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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

        information = "\n" +
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
                "        <dc:description xml:lang=\"en\">"+descripcion+"</dc:description>\n" +
                "        <dc:publisher>"+publisher+"</dc:publisher>\n" +
                "        <dc:rights>CC BY 4.0</dc:rights>\n" +
                "        <dc:subject>"+descripcion+"</dc:subject>   \n" +
                "\t\t<rdfs:isDefinedBy rdf:resource=\""+definedBy+"\"/> \t\t\n" +
                "\t\t<rdfs:comment xml:lang=\"en\">"+descripcionEng+"</rdfs:comment>\n" +
                "\t\t<dcterms:issued>"+fecha+"</dcterms:issued>\t\n" +
                "\t\t<dcterms:modified>"+fecha+"</dcterms:modified>\t\n" +
                "\t\t<vann:preferredNamespacePrefix>vSont</vann:preferredNamespacePrefix>\n" +
                "\t\t<vann:preferredNamespaceUri>"+preferredNamespace+"#</vann:preferredNamespaceUri>\n" +
                "    </owl:Ontology>\n";

        annotations = "\n <!-- \n" +
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
        bw.write(namespaces);
        bw.write(information);
        bw.write(annotations);

        //Luego de esto, iterar sobre los archivos y generar la ontologia
        crearClases();

        bw.close();
        fw.close();
        br.close();
        fr.close();
    }

    private void crearClases() throws IOException {
        String input = System.getProperty("user.dir") + "/src/main/resources/Proyectos/"+nombreProyecto+"/clasesUML.txt";
        fr = new FileReader(input);
        br = new BufferedReader(fr);

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
        }
    }

    /*
    @Override
    public void initialize(URL location, ResourceBundle resources){

    }
    */

}
