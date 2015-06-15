/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Patrik
 */
public class ContentManager {
    
    public static String extractDTD(String path) throws IOException, Exception { 
        String dtd;
        String systemId = null;
        String fileString = loadFile(path);//obsah celeho .xml souboru ulozen jako string
        //hledam referenci dtd (predpoklad: .xml soubor obsahuje vzdy validni a spravne dtd)
        //1.interne (tj. systemId musi byt null) -> nactu ten interni dtd string -> predam dal (return)
        try {
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db=dbf.newDocumentBuilder();
            Document doc=db.parse(new File(path));
            systemId = doc.getDoctype().getSystemId();
          } catch (  SAXException | ParserConfigurationException | IOException e) {
              System.out.println("error");
          }
        
        if (systemId == null) {
            System.out.println("DTD je v interni podobe.");
            //hledam ve fileString, dokud nenarazim na zacatek dtd deklarace (tj. obsah retezce mezi "[" a "]")
            int firstOcc = fileString.indexOf("[");
            int lastOcc = fileString.lastIndexOf("]");
            StringBuilder builder = new StringBuilder();
            for (int i=firstOcc+1; i<lastOcc; i++) {
                builder.append(fileString.charAt(i));
            }
            dtd = builder.toString();
        }
        //2.externe -> najdu .dtd soubor, predam jeho obsah (string)
        else {
            if (!fileString.contains("[")) { 
                System.out.println("DTD je v externi podobe.");
                dtd = loadFile(systemId);
            }
            //3.oboji - interne i externe
            else {
                System.out.println("DTD je v interni i externi podobe - kombinuje oba pristupy.");
                //za interni dtd napoji externi (cele je to predano jako jeden string)
                int firstOcc = fileString.indexOf("[");
                int lastOcc = fileString.lastIndexOf("]");
                StringBuilder builder = new StringBuilder();
                for (int i=firstOcc+1; i<lastOcc; i++) {
                    builder.append(fileString.charAt(i));
                }
                String out = builder.toString();
                dtd = out.concat(loadFile(systemId)); // Tady si nejsem jista jestli to bude fungovat jak ma (Za interni out string napojuju ten z .dtd souboru)
            }
        }
        return dtd;
    }
    

    public static String loadFile(String path) throws IOException {
        File file = new File(path);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner((Readable) new BufferedReader(new FileReader(file)));
        String lineSeparator = System.getProperty("line.separator");
        try {
            while (scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
    
    
    public static void createXMLSchema(String xmlSchema) throws IOException {
        //ulozi xmlschema(string) do .xsd a pozmeni puv.xml -> ulozi ho jako novy soubor (neprepisuje!) jiz jako xsd
        String path = Main.path;
        Path p = Paths.get(path);
        Path pp = p.getParent(); //vracim se k adresari - ve stejnem jak puvodni xml bude i xsd
        try {
            File toSave = new File(pp.toString()+"/newschema.xsd");
            FileOutputStream is = new FileOutputStream(toSave);
            OutputStreamWriter osw = new OutputStreamWriter(is);    
            Writer w = new BufferedWriter(osw);
            w.write(xmlSchema);
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file newschema.xsd");
        }
        
        //TODO: V puvodním XML souboru zmenit hlavicku (misto DTD) aby se odkazoval na soubor s XML schematem
        String toInsert = loadFile(path); //z tohoto stringu budu kopirovat jen radky bez dtd do stringu toSave
        String toSave = ""; //text, ktery pak ulozim do souboru (s referenci xsd tj. bez dtd)
        //ziskam korenovy element, za nej pak napojim toAdd
        Element rootEl=null; //korenovyelement
        try {
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db=dbf.newDocumentBuilder();
            Document doc=db.parse(new File(path));
            rootEl = doc.getDocumentElement();
          } catch (  SAXException | ParserConfigurationException | IOException e) {
              System.out.println("error");
          }
       
        //toto se prida za korenovy element
        String toAdd = " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xmlns='http://www.w3.org/2001/XMLSchema' xsi:schemaLocation=\"http://www.w3.org/2001/XMLSchema newschema.xsd\" ";   
        
        //prochazim radek po radku az na konec toInsert -> vkladam do toSave, kdyz je tam dtd tak preskakuju-nevkladam
        String line;
        BufferedReader reader = new BufferedReader(new StringReader(toInsert));
        try {
            line = reader.readLine();
            while (line != null) {
                if (line.startsWith("<!DOCTYPE")) {
                    do { 
                        line = reader.readLine();
                    } while (!(line.startsWith("<"+rootEl.getTagName())));
                    line = line.replaceFirst("\\s*>", toAdd + ">");
                }
                toSave = toSave.concat(line + System.lineSeparator());
                line = reader.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        //puvodni soubor (nyni jiz pozmeneny - zmena je v tom stringu,ktery predam) ulozim jako novy soubor
        saveFile(toSave);
    }
    
    
    //saveFile predavam uz hotovy string vytvoreny createXMLSchema metodou
    public static void saveFile(String file) {
        String path = Main.path;
        Path p = Paths.get(path);
        Path pp = p.getParent(); 
        try {
            File newTextFile = new File(pp.toString()+"/newXml_WithXsd.xml");
            FileWriter fw = new FileWriter(newTextFile);
            fw.write(file);
            fw.close();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }
    
}
