/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.Entities.Attribute;
import dtdtoxmlschemaconverter.Entities.Element;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Patrik
 */
public class Converter {
    
    public static List<Element> parseDTD(String dtd) {
        
        List<Element> elements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();
        
        dtd = dtd.substring(dtd.indexOf("<") + 1, dtd.lastIndexOf(">"));
        String[] lines = dtd.split(">\\s*<");
        
        for (String line : lines) {
            if (line.startsWith("!ELEMENT")) {
                Element elem = parseElement(line);
                elements.add(elem);
            }else if (line.startsWith("!ATTLIST")){
                attributes.addAll(parseAttributes(line));
            }
        }
        
        for (Attribute attr : attributes) {
            String elemName = attr.getElemName();
            
            for (Element elem : elements) {
                if (elemName.equals(elem.getName())) {
                    elem.addAttribute(attr);
                }
            }
        }
        
        return elements;
    }

    private static Element parseElement(String elem) {
        String[] items = elem.split(" ");
        return new Element(items[1], items[2]);
    }

    private static List<Attribute> parseAttributes(String attrs) {
        throw new UnsupportedOperationException("Not suppported yet.");
    }
    
    private static Attribute parseAttribute(String attr) {
        throw new UnsupportedOperationException("Not suppported yet.");
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
