/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
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
    
    
    public static void createXMLSchema(String xmlSchema) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void saveFile(String file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
