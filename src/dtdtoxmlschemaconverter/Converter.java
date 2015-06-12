/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.DataClasses.Attribute;
import dtdtoxmlschemaconverter.DataClasses.DTDObject;
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
    
    public static List<DTDObject> parseDTD(String dtd) {
        
        List<DTDObject> elements = new ArrayList<>();
        List<Attribute> attributes = new ArrayList<>();
        
        dtd = dtd.substring(dtd.indexOf("<") + 1, dtd.lastIndexOf(">"));
        String[] lines = dtd.split(">\\s*<");
        
        for (String line : lines) {
            if (line.startsWith("!ELEMENT")) {
                DTDObject elem = parseElement(line);
                elements.add(elem);
            }else if (line.startsWith("!ATTLIST")){
                attributes.addAll(parseAttributes(line));
            }
        }
        
        for (Attribute attr : attributes) {
            String elemName = attr.getElemName();
            
            for (DTDObject elem : elements) {
                if (elemName.equals(elem.getName())) {
                    elem.addAttribute(attr);
                }
            }
        }
        
        return elements;
    }

    private static DTDObject parseElement(String elem) {
        String[] items = elem.split(" ");
        return new DTDObject(items[1], ObjectType.fromString(items[0]), items[2]);
    }

    private static List<Attribute> parseAttributes(String attrs) {
        throw new UnsupportedOperationException("Not suppported yet.");
    }
    
    private static Attribute parseAttribute(String attr) {
        throw new UnsupportedOperationException("Not suppported yet.");
    }
    
    public static String assembleXMLSchema(List<DTDObject> objects) {
        
        StringBuilder sb = new StringBuilder();
        
        //TO DO: hlavicku a namespace
        List<DTDObject> elems = new ArrayList<>();
        List<DTDObject> notats = new ArrayList<>();
        List<DTDObject> entits = new ArrayList<>();
        
        for (DTDObject obj : objects) {
            switch (obj.getType()) {
                case ELEMENT:
                    elems.add(obj);
                    break;
                case NOTATION:
                    notats.add(obj);
                    break;
                case ENTITY:
                    entits.add(obj);
                    break;
                    
            }
        }
        
        for (DTDObject elem : elems) {
            assembleElem(elem, sb);
        }
        appendWithLineSep(sb, "");
        
        for (DTDObject notat : notats) {
            assembleNotation(notat, sb);
        }
        return sb.toString();
    }
    
    public static String assembleElem(DTDObject elem, StringBuilder sb) {
        
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
        appendWithLineSep(sb, "");
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
            ArrayList<String> subconts = splitContent(content, ",", "(", ")");
            appendWithLineSep(sb, "<sequence" + head + ">");
            for(String subcont : subconts) {
                assembleComplexContent(sb, subcont);
            }
            appendWithLineSep(sb, "</sequence>");
        }else if (content.contains("|")) {
            if (content.contains("#")) {
                content = content.replaceAll("\\s*#\\w*\\s*[|]*", "");
                ArrayList<String> subconts = splitContent(content, "|", "(", ")");
                appendWithLineSep(sb, "<all minOccurs=\"0\">");
                for(String subcont : subconts) {
                    assembleComplexContent(sb, subcont);
                }
                appendWithLineSep(sb, "</all>");
            } else { 
                ArrayList<String> subconts = splitContent(content, "|", "(", ")");
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

    private static ArrayList<String> splitContent(String content, String delimiter, String lBorder, String rBorder) {
        ArrayList<String> subconts = new ArrayList();
        
        while (!content.isEmpty()) {
            content = content.trim();
            int indOfDelim;
            if (content.startsWith(lBorder)) {
                int indOfLBorder = content.indexOf(lBorder, 1);
                int indOfRBorder = content.indexOf(rBorder, 1);
                if (indOfLBorder >= indOfRBorder || indOfLBorder == -1) {
                    indOfDelim = content.indexOf(delimiter, indOfRBorder);
                    content = trimOFSubcont(indOfDelim, subconts, content);
                }else {
                    int count = 0;
                    int temp = indOfRBorder;
                    while (indOfLBorder < indOfRBorder && indOfLBorder > 0) {
                        count++;
                        indOfLBorder = content.indexOf(lBorder, indOfLBorder +1);
                        temp = content.indexOf(rBorder, temp + 1);
                    }
                    indOfRBorder = content.indexOf(rBorder, temp);
                    indOfDelim = content.indexOf(delimiter, indOfRBorder);
                    content = trimOFSubcont(indOfDelim, subconts, content);
                }
            }else {
                indOfDelim = content.indexOf(delimiter);
                content = trimOFSubcont(indOfDelim, subconts, content);
            }
        }
        
        return subconts;
    }

    private static String trimOFSubcont(int indOfDelim, ArrayList<String> subconts, String content) {
        if (indOfDelim == -1) {
            subconts.add(content);
            content = "";
        }else {
            subconts.add(content.substring(0, indOfDelim));
            content = (indOfDelim + 1 < content.length())? content.substring(indOfDelim + 1) : "";
        }
        return content;
    }

    public static void assembleNotation(DTDObject notat, StringBuilder sb) {
        
        String content = notat.getContent();
        ArrayList<String> subconts = splitContent(content, " ", "\"", "\"");
        sb.append(MessageFormat.format("<notation name=\"{0}\" public={1}", notat.getName(), subconts.get(1)));
        if (subconts.size() == 3) {
            appendWithLineSep(sb, MessageFormat.format(" system={0} />", subconts.get(2)));
        }else {
            appendWithLineSep(sb, " />");
        }
    }
}
