/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.Entities.Attribute;
import dtdtoxmlschemaconverter.Entities.Element;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Patrik
 */
public class Converter {
    
    public static List<Element> parseDTD(String dtd) {
        
        List<Element> elements = new ArrayList<>();

        dtd = dtd.substring(dtd.indexOf("<") + 1, dtd.indexOf(">"));
        String[] lines = dtd.split(">\\s*<");
        for (String line : lines) {
            if (line.startsWith("!ELEMENT")) {
                Element elem = parseElem(line);
                elements.add(elem);
            }else if (line.startsWith("!ATTLIST")){
                Attribute attr = parseAttr(line);
                for (Element elem : elements){
                    if (elem.getName().equals(attr.getElemName())){
                        elem.addAttribute(attr);
                    }
                }
            }
        }
        
        return null;
    }

    private static Element parseElem(String elem) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static Attribute parseAttr(String attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static String assembleXMLSchema(List<Element> elements) {
        throw new UnsupportedOperationException("Not suppported yet.");
    }
    
    private static String assembleElem(Element elem) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static String assembleAttr(Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
