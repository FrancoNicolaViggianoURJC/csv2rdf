package com.fnvigg.csv2rdf;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Gestor_proyectos {

    private AtributosSesion config;
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
            br.write(nombreOntologico + "," + nombreDato + ",CIM");
            System.out.println(AtributosSesion.getUser());
            br.close();
            fr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String obtenerFase(String nombreProyecto) {
        AtributosSesion.setNombreProyecto(nombreProyecto);
        String ruta = System.getProperty("user.dir");
        String fase = "";
        try {
            File config = new File(ruta + "/src/main/resources/Proyectos/" + nombreProyecto + "/config.txt");
            FileReader lector = new FileReader(config);
            BufferedReader br = new BufferedReader(lector);
            String linea = br.readLine();
            String[] tokens;
            if (linea != null) {
                tokens = linea.split(",");
                fase = tokens[2];
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return fase;
    }

    public String[] obtenerPropiedades(String proyecto) {
        try {
            File fichero = new File("src/main/resources/Proyectos/" + proyecto + "/config.txt");
            FileReader fr = new FileReader(fichero);
            BufferedReader br = new BufferedReader(fr);
            String linea = br.readLine();
            fr.close();
            br.close();
            return linea.split(",");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void removerCampo(String ruta, int indiceCampo) {
        String rutaAux = ruta+"_copia";
        try {
            File original = new File(ruta);
            File resultado = new File(rutaAux);
            FileReader fr = new FileReader(original);
            FileWriter fw = new FileWriter(resultado, true);
            BufferedReader br = new BufferedReader(fr);
            BufferedWriter bw = new BufferedWriter(fw);
            String linea = br.readLine();
            int auxInf = 0;        //auxInf = comienzo subcadena a borrar
            int auxSup = 0;        //auxSup = la longitud de la cadena a borrar
            int contadorDel = 0;
            String frase = "";
            while (linea != null) {
                //En caso que el indice sea el primer campo, hay que sumarle uno al offset
                if(indiceCampo == 0){
                    auxSup += 1;
                }
                //Analisis del string
                for (Character c : linea.toCharArray()) {
                    if (c.equals(',')) {
                        contadorDel += 1;
                    }
                    if (contadorDel < indiceCampo) {
                        //Caracteres que preceden al primer caracter que nos interesa borrar
                        auxInf += 1;
                        auxSup += 1;
                    } else if (contadorDel == indiceCampo) {
                        //  Caracteres que nos interesan borrar
                        auxSup += 1;
                    } else if (contadorDel > indiceCampo) {
                        //Ya hemos obtenido los auxs que nos interesa
                        break;
                    }
                }
                //los auxiliares contienen los indices de comienzo y fin en la cadena
                StringBuilder sb = new StringBuilder(linea);
                frase = sb.delete(auxInf, auxSup).toString();
                linea = br.readLine();
                bw.write(frase + "\n");
                auxInf = 0;
                auxSup = 0;
                contadorDel = 0;
            }
            br.close();
            fr.close();
            original.delete();
            bw.close();
            fw.close();
            boolean exito = resultado.renameTo(original);
            if(exito){
                System.out.println("exito");
            }else{
                System.out.println("Fallo al renombrar el archivo");
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String[] obtenerCampos(String path) {
        try {
            Reader input = new FileReader(path);
            Iterable<CSVRecord> campos = CSVFormat.EXCEL.parse(input);
            String[] columnas = {""};
            //Solo leeremos la primera fila del archivo CSV, por ello el break
            for(CSVRecord campo : campos){
                columnas = campo.values();
                input.close();
                break;
            }
            return columnas;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
