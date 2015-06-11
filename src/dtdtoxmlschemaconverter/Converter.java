/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.DataClasses.Attribute;
import dtdtoxmlschemaconverter.DataClasses.Object;
import dtdtoxmlschemaconverter.DataClasses.ObjectType;
import java.text.MessageFormat;
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
    
    private static final String STRINGTYPE = "type=\"string\"";
    
    public static List<Object> parseDTD(String dtd) {
        
        List<Object> elements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();
        
        dtd = dtd.substring(dtd.indexOf("<") + 1, dtd.lastIndexOf(">"));
        String[] lines = dtd.split(">\\s*<");
        
        for (String line : lines) {
            if (line.startsWith("!ELEMENT")) {
                Object elem = parseElement(line);
                elements.add(elem);
            }else if (line.startsWith("!ATTLIST")){
                attributes.addAll(parseAttributes(line));
            }
        }
        
        for (Attribute attr : attributes) {
            String elemName = attr.getElemName();
            
            for (Object elem : elements) {
                if (elemName.equals(elem.getName())) {
                    elem.addAttribute(attr);
                }
            }
        }
        
        return elements;
    }

    private static Object parseElement(String elem) {
        String[] items = elem.split(" ");
        return new Object(items[1], ObjectType.fromString(items[0]), items[2]);
    }

    private static List<Attribute> parseAttributes(String attrs) {
        throw new UnsupportedOperationException("Not suppported yet.");
    }
    
    private static Attribute parseAttribute(String attr) {
        throw new UnsupportedOperationException("Not suppported yet.");
    }
    
    public static String assembleXMLSchema(List<Object> elements) {
        
        StringBuilder sb = new StringBuilder();
        
        //TO DO: hlavicku a namespace
        
        for (Object elem : elements){
            assembleElem(elem, sb);
        }
        
        return sb.toString();
    }
    
    public static String assembleElem(Object elem, StringBuilder sb) {
        
        String content = elem.getContent();
        ArrayList<Attribute> attrs = (ArrayList)elem.getAttributes();
        
        sb.append(MessageFormat.format("<element name=\"{0}\" ", elem.getName()));
        if (content.equals("(#PCDATA)") || content.equals("(#CDATA)")) {
            if (elem.getAttributes().isEmpty()) {
                appendWithLineSep(sb, STRINGTYPE + " />");
            }else{
                appendWithLineSep(sb, ">");
                appendWithLineSep(sb, "<complexType>");
                appendWithLineSep(sb, "<simpleContent>");
                appendWithLineSep(sb, "<extension base=\"string\">");
                assembleAttrs(attrs, sb);
                appendWithLineSep(sb, "</extension>");
                appendWithLineSep(sb, "</simpleContent>");
                appendWithLineSep(sb, "</complexType>");
                appendWithLineSep(sb, "</element>");
            }
        }else if (content.equals("EMPTY") || content.equals("(EMPTY)")) {
            appendWithLineSep(sb, ">");
            appendWithLineSep(sb, "<complexType>");
            assembleAttrs(attrs, sb);
            appendWithLineSep(sb, "</complexType>");
            appendWithLineSep(sb, "</element>");
        }else if (content.equals("ANY") || content.equals("(ANY)")) {
            appendWithLineSep(sb, ">");
            appendWithLineSep(sb, "<complexType>");
            appendWithLineSep(sb, "<sequence>");
            appendWithLineSep(sb, "<any minOccurs=\"0\" />");
            appendWithLineSep(sb, "</sequence>");
            assembleAttrs(attrs, sb);
            appendWithLineSep(sb, "</complexType>");
            appendWithLineSep(sb, "</element>");
        }else {
            appendWithLineSep(sb, ">");
            appendWithLineSep(sb, MessageFormat.format("<complexType{0}>", (content.contains("#"))? " mixed=\"true\"" : ""));
            assembleComplexContent(sb, content);
            assembleAttrs(attrs, sb);
            appendWithLineSep(sb, "</complexType>");
            appendWithLineSep(sb, "</element>");
        }
        
        return sb.toString();
    }

    private static void assembleAttrs(List<Attribute> attrs, StringBuilder sb) {
        sb.append("");
    }
    
    private static String assembleAttr(Attribute attr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void appendWithLineSep(StringBuilder sb, String typestring) {
        sb.append(typestring).append(System.lineSeparator());
    }

    private static void assembleComplexContent(StringBuilder sb, String content) {
        content = content.trim();
        boolean temp = false;
        
        while (content.charAt(0) == '(' && content.charAt(content.length() - 1) == ')') {
            content = trimOfFirstAndLastChar(content);
            temp = true;
        }
        String head = "";
        
        if (content.endsWith("+")) {
            head = " maxOccurs=\"unbounded\"";
        }else if (content.endsWith("*")){
            head = " minOccurs=\"0\" maxOccurs=\"unbounded\"";
        }else if (content.endsWith("?")) {
            head = " minOccurs=\"0\"";           
        }
        
        if (!head.isEmpty()) {
            content = content.substring(0, content.length() - 1);
        }
        
        while (content.charAt(0) == '(' && content.charAt(content.length() - 1) == ')') {
            content = trimOfFirstAndLastChar(content);
        }
        
        if (content.contains(",")) {
            ArrayList<String> subconts = splitContent(content, ",");
            appendWithLineSep(sb, "<sequence" + head + ">");
            for(String subcont : subconts) {
                assembleComplexContent(sb, subcont);
            }
            appendWithLineSep(sb, "</sequence>");
        }else if (content.contains("|")) {
            if (content.contains("#")) {
                content = content.replaceAll("\\s*#\\w*\\s*[|]*", "");
                ArrayList<String> subconts = splitContent(content, "|");
                appendWithLineSep(sb, "<all minOccurs=\"0\">");
                for(String subcont : subconts) {
                    assembleComplexContent(sb, subcont);
                }
                appendWithLineSep(sb, "</all>");
            } else { 
                String[] subconts = content.split("|");
                appendWithLineSep(sb, "<choice>");
                for(String subcont : subconts) {
                    assembleComplexContent(sb, subcont);
                }
                appendWithLineSep(sb, "</choice>");
            }
        }else {
            if (temp) {
                appendWithLineSep(sb, "<sequence" + head + ">");
                appendWithLineSep(sb, "<element ref=\"" + content + "\" />");
                appendWithLineSep(sb, "</sequence>");
            }else {
                appendWithLineSep(sb, "<element ref=\"" + content + "\" />");
            }
        }
    }

    private static String trimOfFirstAndLastChar(String content) {
        return content.substring(1, content.length() - 1);
    }

    private static ArrayList<String> splitContent(String content, String delimiter) {
        ArrayList<String> subconts = new ArrayList();
        
        while (!content.isEmpty()) {
            content = content.trim();
            
            if (content.startsWith("(")) {
                int indOfLBracket = content.indexOf("(", 1);
                int indOfRBracket = content.indexOf(")");
                if (indOfLBracket > indOfRBracket || indOfLBracket == -1) {
                    subconts.add(content.substring(0, indOfRBracket + 1));
                    content = (indOfRBracket + 2 < content.length())? content.substring(indOfRBracket + 2) : "";
                }else {
                    int count = 0;
                    int temp = indOfRBracket;
                    while (indOfLBracket < indOfRBracket && indOfLBracket > 0) {
                        count++;
                        indOfLBracket = content.indexOf("(", indOfLBracket +1);
                        temp = content.indexOf(")", temp + 1);
                    }
                    indOfRBracket = content.indexOf(")", temp);
                    subconts.add(content.substring(0, indOfRBracket + 1));
                    content = (indOfRBracket + 2 < content.length())? content.substring(indOfRBracket + 2) : "";
                }
            }else {
                int indOfDelim = content.indexOf(delimiter);
                if (indOfDelim == -1) {
                    subconts.add(content);
                    content = "";
                }else {
                    subconts.add(content.substring(0, indOfDelim));
                    content = (indOfDelim + 1 < content.length())? content.substring(indOfDelim + 1) : "";
                }
            }
        }
        
        return subconts;
    }
}
