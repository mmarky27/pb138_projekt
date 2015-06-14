/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.DataClasses.Attribute;
import dtdtoxmlschemaconverter.DataClasses.DTDObject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *Třída obsahující metody na převod objektů DataClasses na XML schema.
 * @author Patrik
 */
public class Converter {
    
    /**
     * Sestaví XML schema z listu poskytnutých objektů a vrátí ho jako String.
     * 
     * @param objects list DTD objektů ke zpracování 
     * @return kompletní XML schema
     */
    public static String assembleXMLSchema(List<DTDObject> objects) {
        
        StringBuilder sb = new StringBuilder();
        
        //sestaví hlavičku schematu s defaultním namespace
        appendWithLineSep(sb, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        appendWithLineSep(sb, "");
        appendWithLineSep(sb, "<schema targetNamespace=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">");
        
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
        //kvůli přehlednosti výsledného schematu se zpracují nejdříve elementy, potom notation
        //a nakonec entity
        elems.stream().forEach((elem) -> {
            assembleElem(elem, sb);
        });
        appendWithLineSep(sb, "");
        
        if (!notats.isEmpty()) {

            notats.stream().forEach((notat) -> {
                assembleNotation(notat, sb);
            });
        }
        appendWithLineSep(sb, "");
        
        if (!entits.isEmpty()) {
            ArrayList<String> elemNames = new ArrayList<>();
            
            elems.stream().forEach((elem) -> {
                elemNames.add(elem.getName());
            });
            String rootElemName = "";
            //musíme najít kořenový element, kvůli syntaxi DTD
            for (String elemName : elemNames) {
                if (sb.indexOf(MessageFormat.format("ref=\"{0}\"", elemName)) == -1) {
                    rootElemName = elemName;
                    break;
                }
            }
            appendWithLineSep(sb, MessageFormat.format("<!DOCTYPE {0} [", rootElemName));
            entits.stream().forEach((entity) -> {
                assembleEntity(entity, sb);
            });
            appendWithLineSep(sb, "]>");
        }
        appendWithLineSep(sb, "");
        appendWithLineSep(sb, "</schema>");
        return sb.toString();
    }

    /**
     * Sestaví element XML schematu z objektu elem a připojí ho do StringBuilderu.
     * 
     * @param elem DTDobjekt, jehož ObjectType je ELEMENT
     * @param sb StringBuilder ,kterému se přidá string zpracovaného elementu
     */
    private static void assembleElem(DTDObject elem, StringBuilder sb) {
        
        String content = elem.getContent();
        ArrayList<Attribute> attrs = (ArrayList)elem.getAttributes();
        
        sb.append(MessageFormat.format("<element name=\"{0}\" ", elem.getName()));
        //zpracování jednoduchého elementu obsahujícího jen textová data
        switch (content) {
            case "#PCDATA":
            case "(#PCDATA)":
            case "#CDATA":
            case "(#CDATA)":
                if (elem.getAttributes().isEmpty()) {
                    appendWithLineSep(sb, "type=\"string\" />");
                //když element obsahuje attributy, je třeba složitějšího zpracování
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
                break;
                //zpracování prázdného elementu
            case "EMPTY":
            case "(EMPTY)":
                appendWithLineSep(sb, ">");
                appendWithLineSep(sb, "<complexType>");
                assembleAttrs(attrs, sb);
                appendWithLineSep(sb, "</complexType>");
                appendWithLineSep(sb, "</element>");
                break;
            //zpracování elementu, který může obsahovat jakýkoliv obsah
            case "ANY":
            case "(ANY)":
                appendWithLineSep(sb, ">");
                appendWithLineSep(sb, "<complexType>");
                appendWithLineSep(sb, "<sequence>");
                appendWithLineSep(sb, "<any minOccurs=\"0\" />");
                appendWithLineSep(sb, "</sequence>");
                assembleAttrs(attrs, sb);
                appendWithLineSep(sb, "</complexType>");
                appendWithLineSep(sb, "</element>");
                break;
            //zpracování elementu, který může obsahovat komplexní obsah (může se rekurzivně zanořovat)
            default:
                appendWithLineSep(sb, ">");
                //podmínka ověřuje mixed content, který musí být specifikovaný v této části schematu
                appendWithLineSep(sb, MessageFormat.format("<complexType{0}>", (content.contains("#"))? " mixed=\"true\"" : ""));
                assembleComplexContent(sb, content);
                assembleAttrs(attrs, sb);
                appendWithLineSep(sb, "</complexType>");
                appendWithLineSep(sb, "</element>");
                break;
        }
        appendWithLineSep(sb, "");
    }

    /**
     * Sestaví z listu attributů příslušné XML schema a výsledek napojí do 
     * StringBuilderu.
     * 
     * @param attrs list attributů
     * @param sb StringBuilder ke kterému se připojí výsledek.
     */
    private static void assembleAttrs(List<Attribute> attrs, StringBuilder sb) {
        if (!attrs.isEmpty()) {
            for (Attribute attr : attrs) {
                assembleAttr(attr.getName(), attr.getContent(), sb);
            }
        } else {
        sb.append("");
        }
    }
    
    /**
     * Sestaví atribut a připojí ho do StringBuilderu. Pokud content obsahuje 
     * více atributů, zpracuje je rekurzivně taky.
     * 
     * @param name
     * @param content
     * @param sb 
     */
    private static void assembleAttr(String name, String content, StringBuilder sb) {
        
        sb.append(MessageFormat.format("<attribute name=\"{0}\"", name));
        String type;
        content = content.replaceAll("\\s+", " ").trim();
        
        //pokud výraz začíná závorkou jedná se výčet hodnot, kterých může 
        //attribut nabývat
        if (content.startsWith("(")) {
            int indOfRBracket = content.indexOf(")");
            type = content.substring(0, indOfRBracket + 1);
            content = content.substring(indOfRBracket + 1);
            content = assembleAttrContent(content, sb);
            ArrayList<String> enums = splitContent(type.substring(1, type.length() - 1), "|", "(", ")");
            appendWithLineSep(sb, ">");
            appendWithLineSep(sb, "<simpleType>");
            appendWithLineSep(sb, "<restriction>");
            enums.stream().forEach((en) -> {
                appendWithLineSep(sb, MessageFormat.format("<enumeration value=\"{0}\"", en));
            });
            appendWithLineSep(sb, "</restriction>");
            appendWithLineSep(sb, "</simpleType>");
            appendWithLineSep(sb, "</attribute>");
        //jinak se uloží typ attributu
        }else {
            int indOfSpace = content.indexOf(" ");
            type = content.substring(0, indOfSpace);
            if (type.equals("CDATA")) {
                type = "string";
            }
            sb.append(MessageFormat.format(" type=\"{0}\"", type));
            content = content.substring(indOfSpace + 1);
            content = assembleAttrContent(content, sb);
            appendWithLineSep(sb, " />");
        }
        
        //pokud není výraz prázdný obsahuje další atribut(y)
        if (!content.isEmpty()) {
            int indOfSpace = content.indexOf(" ");
            name = content.substring(0, indOfSpace);
            content = content.substring(indOfSpace + 1);
            assembleAttr(name, content, sb);
        }
    }
    
    /**
     * Sestaví zbytkové parametry atributu.
     * 
     * @param content 
     * @param sb
     * @return 
     */
    private static String assembleAttrContent(String content, StringBuilder sb) {
        content = content.trim();
        String value;
        
        //pokud začíná uvozovkou jedná se o defaultní hodnotu
        if (content.startsWith("\"")) {
            int indOfQuote = content.indexOf("\"", 1);
            value = content.substring(0, indOfQuote + 1);
            content = content.substring(indOfQuote + 1);
            sb.append(MessageFormat.format(" default={0}", value));
        //jinak se jedná o typ omezení
        }else{
            int indOfSpace = content.indexOf(" ");
            value = content.substring(0, indOfSpace);
            content = content.substring(indOfSpace + 1).trim();
            switch (value) {
                case "#REQUIRED":
                    sb.append(MessageFormat.format(" use=\"{0}\"", "required"));
                    break;
                case "#IMPLIED":
                    sb.append(MessageFormat.format(" use=\"{0}\"", "optional"));
                    break;
                //v tomto případě ještě následuje hodnota, která musí být pevná
                case "#FIXED":
                    int indOfQuote = content.indexOf("\"", 1);
                    value = content.substring(0, indOfQuote + 1);
                    content = content.substring(indOfQuote + 1);
                    sb.append(MessageFormat.format(" fixed={0}", value));
            }
        }
        return content.trim();
    }

    /**
     * Metoda připojující ke StringBuilderu výraz s oddělovačem řádku na konci.
     * 
     * @param sb StringBulider, ke kterému se připojuje výraz 
     * @param string výraz k připojení
     */
    private static void appendWithLineSep(StringBuilder sb, String string) {
        sb.append(string).append(System.lineSeparator());
    }

    /**
     * Sestaví komplexní obsah elementu. Může se rekurzivně zanořovat a obsahovat kvantifikátory.
     * 
     * @param sb StringBuilder, ke kterému se připojí vytvořený obsah
     * @param content obsah elementu, ze kterého se vytvoří příslušný kus XML xhema kódu
     */
    private static void assembleComplexContent(StringBuilder sb, String content) {
        
        content = trimOfSurroundingBrackets(content);
        
        String quantifier = "";
        
        //podle kvantifikátoru uloží výskyty
        if (content.endsWith("+")) {
            quantifier = " maxOccurs=\"unbounded\"";
        }else if (content.endsWith("*")){
            quantifier = " minOccurs=\"0\" maxOccurs=\"unbounded\"";
        }else if (content.endsWith("?")) {
            quantifier = " minOccurs=\"0\"";           
        }
        
        //ostřihne obsah o kvantifikátor
        if (!quantifier.isEmpty()) {
            content = content.substring(0, content.length() - 1);
        }
       
        content = trimOfSurroundingBrackets(content);
        
        //výraz může obsahovat čárky jedině, když se jedná o sequence, tudíž se 
        //výraz rozstříhá na jednotlivé položky a ty se dále zpracují
        if (content.contains(",")) {
            ArrayList<String> subconts = splitContent(content, ",", "(", ")");
            appendWithLineSep(sb, "<sequence" + quantifier + ">");
            subconts.stream().forEach((subcont) -> {
                assembleComplexContent(sb, subcont);
            });
            appendWithLineSep(sb, "</sequence>");
        //taktéž může výraz obsahovat znak "|" jedině ve dvou případech
        }else if (content.contains("|")) {
            //pokud obsahuje znak "#" jedná se o mixed content a výraz "#PCDATA"
            //musí být na začátku následovaný dalšími výrazy
            if (content.contains("#")) {
                content = content.replaceAll("\\s*#\\w*\\s*|", "");
                ArrayList<String> subconts = splitContent(content, "|", "(", ")");
                appendWithLineSep(sb, "<all minOccurs=\"0\">");
                subconts.stream().forEach((subcont) -> {
                    assembleComplexContent(sb, subcont);
                });
                appendWithLineSep(sb, "</all>");
            //jinak se jedná o výběr jedné z několika možností
            } else { 
                ArrayList<String> subconts = splitContent(content, "|", "(", ")");
                appendWithLineSep(sb, "<choice>");
                subconts.stream().forEach((subcont) -> {
                    assembleComplexContent(sb, subcont);
                });
                appendWithLineSep(sb, "</choice>");
            }
        //v posledním případě se jedná jen o samotný odkaz na element
        }else {
            //pokud byl kvantifikátor specifikován musí se reference na element
            //obalit do sequence s příslušným řetězcem popisujícím výskyt elementu
            if (!quantifier.isEmpty()) {
                appendWithLineSep(sb, MessageFormat.format("<sequence {0}>", quantifier));
                appendWithLineSep(sb, MessageFormat.format("<element ref=\"{0}\" />", content));
                appendWithLineSep(sb, "</sequence>");
            }else {
                appendWithLineSep(sb, MessageFormat.format("<element ref=\"{0}\" />", content));
            }
        }
    }

    /**
     * Ořízne o počáteční a koncové bílé znaky a pokud je výraz uzavřen v kulatých závorkách, ořízne je
     * a znovu ořízne o bílé znaky.
     * @param content
     * @return 
     */
    private static String trimOfSurroundingBrackets(String content) {
        content = content.trim();
        if (content.charAt(0) == '(' && content.charAt(content.length() - 1) == ')') {
            return content.substring(1, content.length() - 1);
        }
        return content.trim();
    }

    /**
     * Rozdělí vstupní výraz content na podvýrazy, které jdou oddělěny výrazem 
     * delimiter, přičemž mohou být podvýrazy ohraničené výrazy lBorder a rBorder.
     * Takový podvýraz je pak jedním celkem i když v sobě obsahuje další podvýrazy.
     * 
     * @param content výraz potřebující rozdělit na podvýrazy
     * @param delimiter výraz oddělující od sebe jednotlivé podvýrazy
     * @param lBorder výraz ohraničující podvýraz zleva
     * @param rBorder výraz ohraničující podvýraz zprava
     * @return list podvýrazů vytvořených z původního výrazu
     */
    private static ArrayList<String> splitContent(String content, String delimiter, String lBorder, String rBorder) {
        ArrayList<String> subconts = new ArrayList();
        
        while (!content.isEmpty()) {
            int indOfDelim;
            //pokud výraz začíná levým ohraničením, musí se najít pravé ohraničení tak,
            //aby pokud mezi levým a pravým ohraničením ve výrazu existují ohraničení, 
            //musí být v páru (př.: uzavřenost kulatých závorek)
            if (content.startsWith(lBorder)) {
                int indOfLBorder = content.indexOf(lBorder, 1);
                int indOfRBorder = content.indexOf(rBorder, 1);
                //pokud se další výskyt levého ohraničení vyskytuje za pravým,
                //nebo už ve výrazu není, může se výraz ostřihnout za pravé ohraničení
                if (indOfLBorder >= indOfRBorder || indOfLBorder == -1) {
                    indOfDelim = content.indexOf(delimiter, indOfRBorder);
                    content = trimOFSubcont(indOfDelim, subconts, content);
                //jinak se musí ohraničení uvnitř výrazu napárovat
                }else {
                    int countOfRBorder = 2;
                    int countOfLBorder = 1;
                    // dokud se nebudou rovnat počty výskytů levého a pravého ohraničení:
                    //--najde se další pravé ohraničení a zvýší se počet jeho výskytů
                    //--dokud je výskyt dalšího levého oddělovače před pravým
                    //----zvyší se počet levých oddělovačů
                    while (countOfLBorder != countOfRBorder) {
                        indOfRBorder = content.indexOf(rBorder, indOfRBorder);
                        countOfRBorder++;
                        int temp = content.indexOf(lBorder, indOfLBorder);
                        while (temp <= indOfRBorder) {
                            indOfLBorder = temp;
                            countOfLBorder++;
                            temp = content.indexOf(lBorder, indOfLBorder);
                        }
                    }
                    //po spárování oddělovačů se výraz ostřihne o podvýraz
                    indOfDelim = content.indexOf(delimiter, indOfRBorder);
                    content = trimOFSubcont(indOfDelim, subconts, content);
                }
            //pokud výraz nezačíná ohraničením, může se ostřihnout až po oddělovač
            }else {
                indOfDelim = content.indexOf(delimiter);
                content = trimOFSubcont(indOfDelim, subconts, content);
            }
            content = content.trim();
        }
        
        return subconts;
    }
    
    /**
     * Metoda ořezávající content o podvýraz, který je vymezen od začátku výrazu 
     * po indOfDelim a vloží jej do subconts. Metoda vrátí ostřihnutý content.
     * 
     * @param indOfDelim index, po který se má content oříznout
     * @param subconts list podvýrazů, do kterého se přidá ostřihnutý
     * @param content ořezávaný výraz
     * @return ostřihnutý výraz content
     */
    private static String trimOFSubcont(int indOfDelim, ArrayList<String> subconts, String content) {
        //pokud se oddělovač nevyskytoval ve výrazu, přidá se celý content
        if (indOfDelim == -1) {
            subconts.add(content.trim());
            content = "";
        }else {
            subconts.add(content.substring(0, indOfDelim));
            content = content.substring(indOfDelim + 1);
        }
        return content;
    }

    /**
     * Sestaví notation a připojí ji ke StringBuilderu.
     * 
     * @param notat objekt jehož ObjectType je NOTATION
     * @param sb StringBuilder, ke kterému se připojí XML schema kód
     */
    private static void assembleNotation(DTDObject notat, StringBuilder sb) {
        
        String content = notat.getContent();
        ArrayList<String> subconts = splitContent(content, " ", "\"", "\"");
        sb.append(MessageFormat.format("<notation name=\"{0}\" public={1}", notat.getName(), subconts.get(1)));
        if (subconts.size() == 3) {
            appendWithLineSep(sb, MessageFormat.format(" system={0} />", subconts.get(2)));
        }else {
            appendWithLineSep(sb, " />");
        }
    }

    /**
     * Sestaví entitu a připojí ji ke StringBuilderu.
     * 
     * @param entity objekt, jehož ObjectType je ENTITY
     * @param sb StringBuilder, ke kterému se připojí DTD kód
     */
    private static void assembleEntity(DTDObject entity, StringBuilder sb) {
        appendWithLineSep(sb, MessageFormat.format("<!ENTITY {0} {1}", entity.getName(), entity.getContent()));
    }
}
