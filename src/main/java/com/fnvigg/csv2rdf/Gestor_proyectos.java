package com.fnvigg.csv2rdf;

import javafx.scene.image.Image;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

            br.close();
            lector.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return fase;
    }

    public void setFase(String nombreProyecto, String fase){
        String ruta = System.getProperty("user.dir");
        try {
            File config = new File(ruta + "/src/main/resources/Proyectos/" + nombreProyecto + "/config.txt");
            File config_aux = new File(ruta + "/src/main/resources/Proyectos/" + nombreProyecto + "/config_aux.txt");

            FileReader lector = new FileReader(config);
            BufferedReader br = new BufferedReader(lector);
            FileWriter fw = new FileWriter(config_aux);
            BufferedWriter bw = new BufferedWriter(fw);

            String linea = br.readLine();
            String[] tokens = {};
            if (linea != null) {
                tokens = linea.split(",");
                tokens[2] = fase;
            }

            int aux = 1;
            for (String token : tokens){
                if(aux != tokens.length){
                bw.write(token+",");
                }else{
                    bw.write(token);
                }
                aux += 1;
            }

            bw.close();
            fw.close();
            br.close();
            lector.close();

            config.delete();
            config_aux.renameTo(config);

        }catch (IOException e){
            e.printStackTrace();
        }
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

    public void addRequerimiento(String requerimiento, String proyecto) throws IOException {
        //Obtener ruta
        String ruta = System.getProperty("user.dir");
        File f = new File(ruta + "/src/main/resources/Proyectos/" + proyecto + "/requerimientos.txt");
        //Abrirlo en modo append
        FileWriter fw = new FileWriter(f, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(requerimiento + '\n');

        bw.close();
        fw.close();
    }

    public void borrarRequerimiento(int indice, String proyecto) throws IOException {
        //Comprobar si existe el archivo
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + proyecto + "/requerimientos.txt";
        String rutaAux = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + proyecto + "/requerimientos_a.txt";
        File f = new File(ruta);
        File f_aux = new File(rutaAux);
        //Comprobar si existe
        if(f.exists() && !f.isDirectory()){
            //Borrar la linea == indice
            FileReader fr = new FileReader(f);
            FileWriter fw = new FileWriter(f_aux);

            BufferedReader reader = new BufferedReader(fr);
            BufferedWriter writer = new BufferedWriter(fw);

            //Hay que leer linea por linea el archivo, y replicar todas menos la que coincide con el indice
            int contador = 0;
            String linea = reader.readLine();
            while(linea != null){
                if(contador != indice){
                    writer.write(linea + '\n');
                }else{
                    //No hacer nada, es la linea a borrar
                }
                linea = reader.readLine();
                contador += 1;
            }

            reader.close();
            writer.close();
            fr.close();
            fw.close();

            //Borrar el archivo original, y renombrar el auxiliar al original
            f.delete();
            f_aux.renameTo(f);
        }else{
            return;
        }
    }

    public ArrayList obtenerRequerimientos(String proyecto) throws IOException {
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + proyecto + "/requerimientos.txt";
        File f = new File(ruta);
        ArrayList<String> lista = new ArrayList<>();
        //Comprobar si existe
        if(f.exists() && !f.isDirectory()){
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String linea = br.readLine();

            while(linea != null){
                lista.add(linea);
                linea = br.readLine();
            }
            fr.close();
            br.close();
        }
        return lista;
    }

    public void guardarMDO(File ficheroSeleccionado, String nombreProyecto) {
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/MDO.png";
        File ficheroDestino = new File(ruta);

        //Crear las copias de archivos para no trabajar sobre los originales
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(ficheroSeleccionado).getChannel();
            destChannel = new FileOutputStream(ficheroDestino).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void guardarEsquemaOnt(File ficheroSeleccionado, String nombreProyecto) {
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/esquemaOnt.png";
        File ficheroDestino = new File(ruta);

        //Crear las copias de archivos para no trabajar sobre los originales
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(ficheroSeleccionado).getChannel();
            destChannel = new FileOutputStream(ficheroDestino).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            sourceChannel.close();
            destChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
