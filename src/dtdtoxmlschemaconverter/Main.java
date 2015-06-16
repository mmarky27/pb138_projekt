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

        if(args.length > 0){
            path =  args[0];
        }else{
            System.out.println("Please enter .xml file path: ");
            Scanner sc = new Scanner(System.in);
            path = sc.nextLine();
            sc.close();
            System.out.println("You entered: " + path);
        }
        //System.out.printf(path);

        String extracted = ContentManager.extractDTD(path);
        if(extracted == null){
            System.out.println("Soubor nenalezen");
            return;
        }
        //System.out.printf(extracted);


        List<DTDObject> tmp = DTDParser.output(extracted);

        
        String xmlSchema = Converter.assembleXMLSchema(tmp);

        //vytvoreni souboru .xsd:
        ContentManager.createXMLSchema(xmlSchema);
    
    }
}
