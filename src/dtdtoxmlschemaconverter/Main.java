/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.DataClasses.DTDObject;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Patrik
 */
public class Main {
    static String path;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        /*
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("Please enter .xml file path: ");
            path = sc.nextLine();  
            System.out.println("You entered: " + path);
            
        } catch (NullPointerException ex) {
            throw new NullPointerException("error");
        }*/
        
        //String extracted = ContentManager.extractDTD(path);
        //String xmlSchema = Converter.assembleXMLSchema(Converter.parseDTD(extracted));
        //vytvoreni souboru .xsd:
        //ContentManager.createXMLSchema("<schema><taktak></taktak></schema>"); //pak tam bude xmlSchema string
        
        path = "C:\\Dropbox\\pb138\\project_final_mine\\testik.xml";

        String extracted = ContentManager.extractDTD(path);

        System.out.println("--------");

        List<DTDObject> tmp = Parser.output(extracted);

        for(DTDObject i : tmp){
            System.out.println(i);
            i.getAttributes().forEach(System.out::println);
        }
        
    }
}
