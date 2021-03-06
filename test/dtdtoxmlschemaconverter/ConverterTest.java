/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.DataClasses.Attribute;
import dtdtoxmlschemaconverter.DataClasses.DTDObject;
import dtdtoxmlschemaconverter.DataClasses.ObjectType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Goolomb
 */
public class ConverterTest {

    private String dtdToParse;
    private String dtdToParse2;
    private String dtdToParse3;
    private List<DTDObject> elements = new ArrayList<>();
    private List<Attribute> attributes = new ArrayList<>();
    private String s = System.lineSeparator();

    
    @Before
    public void setUp() {
        Attribute attrId = new Attribute("zamestnanec", "id", "CDATA   #REQUIRED");
        Attribute attrPlat = new Attribute("zamestnanec", "plat", "CDATA   #IMPLIED");
        Attribute attrPohlavi = new Attribute("zamestnanec", "pohlavi", "(muz | zena | jine)   #IMPLIED");
        attributes.add(attrId);
        attributes.add(attrPlat);
        attributes.add(attrPohlavi);

        DTDObject elemZamestnanec = new DTDObject("zamestnanec", ObjectType.ELEMENT, "(jmeno, narozen)");
        DTDObject elemJmeno = new DTDObject("jmeno", ObjectType.ELEMENT, "(#PCDATA)");
        DTDObject elemNarozen = new DTDObject("narozen", ObjectType.ELEMENT, "(#PCDATA)");
        elemZamestnanec.addAttribute(attrId);
        elemZamestnanec.addAttribute(attrPlat);
        elemZamestnanec.addAttribute(attrPohlavi);
        elements.add(elemZamestnanec);
        elements.add(elemJmeno);
        elements.add(elemNarozen);

        dtdToParse = "<!ELEMENT zamestnanec (jmeno, narozen)>\n"
                + "  <!ATTLIST zamestnanec\n"
                + "            id          CDATA   #REQUIRED\n"
                + "            plat        CDATA   #IMPLIED\n"
                + "            pohlavi     (muz | zena | jine)   #IMPLIED>\n"
                + "  <!ELEMENT jmeno       (#PCDATA)>\n"
                + "  <!ELEMENT narozen     (#PCDATA)>";
        dtdToParse2 = "<!ELEMENT zamestnanec (jmeno, narozen)>\n"
                + "  <!ATTLIST zamestnanec      id          CDATA   #REQUIRED>\n"
                + "  <!ATTLIST zamestnanec      plat        CDATA   #IMPLIED>\n"
                + "  <!ATTLIST zamestnanec      pohlavi     (muz | zena | jine)   #IMPLIED>\n"
                + "  <!ELEMENT jmeno       (#PCDATA)>\n"
                + "  <!ELEMENT narozen     (#PCDATA)>";
        dtdToParse3 = "<!ELEMENT zamestnanec (jmeno, narozen)>\n"
                + "  <!ENTITY copyright SYSTEM \"http://www.w3schools.com/entities.dtd\">/n"
                + "  <!NOTATION jpg PUBLIC \"JPG 1.0\" \"image/jpeg\">\n"
                + "  <!ELEMENT jmeno       (#PCDATA)>\n"
                + "  <!ELEMENT narozen     (#CDATA)>";
    }
    
    private String trimAllWhitespaces(String s) {
        return s.replaceAll("\\s+", " ");
    }
    
    @Test
    public void testAssembleXMLSchema() {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + s
            + "<schema targetNamespace=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">" + s
            + "<element name=\"zamestnanec\" >" + s
            + "<complexType>" + s
            + "<sequence>" + s
            + "<element ref=\"jmeno\" />" + s
            + "<element ref=\"narozen\" />" + s
            + "</sequence>" + s
            + "<attribute name=\"id\" type=\"string\" use=\"required\" />" + s
            + "<attribute name=\"plat\" type=\"string\" use=\"optional\" />" + s
            + "<attribute name=\"pohlavi\" use=\"optional\">" + s
            + "<simpleType>" + s
            + "<restriction base=\"string\">" + s
            + "<enumeration value=\"muz \" />" + s
            + "<enumeration value=\"zena \" />" + s
            + "<enumeration value=\"jine\" />" + s
            + "</restriction>" + s
            + "</simpleType>" + s
            + "</attribute>" + s
            + "</complexType>" + s
            + "</element>" + s + s
            + "<element name=\"jmeno\" type=\"string\" />" + s + s
            + "<element name=\"narozen\" type=\"string\" />" + s + s + s + s + s
            + "</schema>" + s;
        String result = Converter.assembleXMLSchema(elements);
        String result2 = Converter.assembleXMLSchema(DTDParser.output(dtdToParse));
        String result3 = Converter.assembleXMLSchema(DTDParser.output(dtdToParse2));
        String result4 = Converter.assembleXMLSchema(DTDParser.output(dtdToParse3));

        assertEquals(trimAllWhitespaces(expected), trimAllWhitespaces(result));
        assertEquals(trimAllWhitespaces(expected), trimAllWhitespaces(result2));
        assertEquals(trimAllWhitespaces(expected), trimAllWhitespaces(result3));
        assertTrue(!trimAllWhitespaces(expected).equals(trimAllWhitespaces(result4)));
    }
    
    @Test
    public void testAssembleElem() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String expected = "<element name=\"zamestnanec\" >" + s
            + "<complexType>" + s
            + "<sequence>" + s
            + "<element ref=\"jmeno\" />" + s
            + "<element ref=\"narozen\" />" + s
            + "</sequence>" + s
            + "<attribute name=\"id\" type=\"string\" use=\"required\" />" + s
            + "<attribute name=\"plat\" type=\"string\" use=\"optional\" />" + s
            + "<attribute name=\"pohlavi\" use=\"optional\">" + s
            + "<simpleType>" + s
            + "<restriction base=\"string\">" + s
            + "<enumeration value=\"muz \" />" + s
            + "<enumeration value=\"zena \" />" + s
            + "<enumeration value=\"jine\" />" + s
            + "</restriction>" + s
            + "</simpleType>" + s
            + "</attribute>" + s
            + "</complexType>" + s
            + "</element>" + s + s;
        Method method = Converter.class.getDeclaredMethod("assembleElem", new Class[]{DTDObject.class, StringBuilder.class});
        method.setAccessible(true);
        
        StringBuilder sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{elements.get(0), sb});
        assertEquals(expected, sb.toString());

        sb = new StringBuilder();
        expected = "<element name=\"chleba\" type=\"string\" />" + s + s;
        method.invoke(new Converter(), new java.lang.Object[]{new DTDObject("chleba", ObjectType.ELEMENT, "#PCDATA"), sb});
        assertEquals(expected, sb.toString());
        
        expected = "<element name=\"chleba\" >" + s
            + "<complexType>" + s
            + "<sequence>" + s
            + "<any minOccurs=\"0\" />" + s
            + "</sequence>" + s
            + "</complexType>" + s
            + "</element>" + s + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{new DTDObject("chleba", ObjectType.ELEMENT, "(ANY)"), sb});
        assertEquals(expected, sb.toString());
    }

    @Test
    public void testAssembleAttr() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException { //returns String
        String expected = "<attribute name=\"plat\" type=\"string\" use=\"optional\" />" + s;
        Method method = Converter.class.getDeclaredMethod("assembleAttr", new Class[]{String.class, String.class, StringBuilder.class});
        method.setAccessible(true);

        StringBuilder sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{ attributes.get(1).getName(), attributes.get(1).getContent(), sb});
        assertEquals(expected, sb.toString());
        
        expected = "<attribute name=\"id\" type=\"string\" use=\"required\" />" + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{ attributes.get(0).getName(), attributes.get(0).getContent(), sb});
        assertEquals(expected, sb.toString());
        
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{ "payment", "(check|cash) \"cash\"", sb});
        expected = "<attribute name=\"payment\" default=\"cash\">" + s
            + "<simpleType>" + s
            + "<restriction base=\"string\">" + s
            + "<enumeration value=\"check\" />" + s
            + "<enumeration value=\"cash\" />" + s
            + "</restriction>" + s
            + "</simpleType>" + s
            + "</attribute>" + s;
        assertEquals(expected, sb.toString());
    }

    @Test
    public void testAssembleAttrs() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String expected = "<attribute name=\"id\" type=\"string\" use=\"required\" />" + s
            + "<attribute name=\"plat\" type=\"string\" use=\"optional\" />" + s;
        Method method = Converter.class.getDeclaredMethod("assembleAttrs", new Class[]{List.class, StringBuilder.class});
        method.setAccessible(true);
        
        StringBuilder sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{ attributes.subList(0, 2),sb });
        assertEquals(expected, sb.toString());
    }
    
    @Test
    public void testAssembleAttrContent() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String expected = " default=\"defaultValue\"";
        Method method = Converter.class.getDeclaredMethod("assembleAttrContent", new Class[]{String.class, StringBuilder.class});
        method.setAccessible(true);
        
        StringBuilder sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{"\"defaultValue\"", sb});
        assertEquals(expected, sb.toString());

        expected = " use=\"required\"";
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{"#REQUIRED", sb});
        assertEquals(expected, sb.toString());

        expected = " use=\"optional\"";
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{"#IMPLIED", sb});
        assertEquals(expected, sb.toString());

        expected = " fixed=\"fixedValue\"";
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{"#FIXED \"fixedValue\"", sb});
        assertEquals(expected, sb.toString());
    }
    
    @Test
    public void testAssembleComplexContent() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String expected = "<sequence minOccurs=\"0\" maxOccurs=\"unbounded\">" + s
                + "<element ref=\"nazev\" />" + s
                + "<element ref=\"autor\" />" + s
                + "</sequence>" + s;
        Method method = Converter.class.getDeclaredMethod("assembleComplexContent", new Class[]{StringBuilder.class, String.class});
        method.setAccessible(true);
        StringBuilder sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{sb, "(nazev, autor*)"});        
        assertEquals(expected, sb.toString());
        
        expected = "<choice>" + s
                + "<element ref=\"nazev\" />" + s
                + "<element ref=\"autor\" />" + s
                + "</choice>" + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{sb, "(nazev | autor)"});
        assertEquals(expected, sb.toString());
        
        expected = "<sequence maxOccurs=\"unbounded\">" + s
            + "<element ref=\"nazev\" />" + s
            + "<element ref=\"autor\" />" + s
            + "</sequence>" + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{sb, "(nazev, autor)+"});
        assertEquals(expected, sb.toString());
        
        expected = "<sequence  minOccurs=\"0\">" + s
            + "<element ref=\"nazev\" />" + s
            + "</sequence>" + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{sb, "(nazev?)"});
        assertEquals(expected, sb.toString());
        
        expected = "<all minOccurs=\"0\">" + s
            + "<element ref=\"nazev\" />" + s
            + "</all>" + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{sb, "(#PCDATA | nazev)*"});
        assertEquals(expected, sb.toString());
    }

    @Test
    public void testSplitContent() throws SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = Converter.class.getDeclaredMethod("splitContent", new Class[]{String.class, String.class, String.class, String.class});
        method.setAccessible(true);
        
        List<String> result = (ArrayList<String>) method.invoke(new Converter(), new java.lang.Object[]{"(potato, egg), cheese", ",", "(", ")"});
        List<String> result2 = (ArrayList<String>) method.invoke(new Converter(), new java.lang.Object[]{"potato | (egg, cheese) | bacon", "|", "(", ")"});
        List<String> result3 = (ArrayList<String>) method.invoke(new Converter(), new java.lang.Object[]{"(potato, apple) | (banana, steak) | (pineapple, cheese) | anotherFood", "|", "(", ")" });

        List<String> expected = new ArrayList<String>();
        expected.add("(potato, egg)"); expected.add("cheese");
        assertEquals(2, result.size());
        assertArrayEquals(expected.toArray(), result.toArray());

        expected.clear();
        expected.add("potato "); expected.add("(egg, cheese) "); expected.add("bacon");
        assertEquals(3, result2.size());
        assertArrayEquals(expected.toArray(), result2.toArray());
        
        expected.clear();
        expected.add("(potato, apple) "); expected.add("(banana, steak) "); expected.add("(pineapple, cheese) "); expected.add("anotherFood");
        assertEquals(4, result3.size());
        assertArrayEquals(expected.toArray(), result3.toArray());
    }

    @Test
    public void testAssembleNotation() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DTDObject notation = new DTDObject("jpg", ObjectType.NOTATION, "PUBLIC \"JPG 1.0\"");
        String expected = "<notation name=\"jpg\" public=\"JPG 1.0\" />" + s;
        Method method = Converter.class.getDeclaredMethod("assembleNotation", new Class[]{DTDObject.class, StringBuilder.class});
        method.setAccessible(true);
        
        StringBuilder sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{ notation, sb });
        assertEquals(expected, sb.toString());
        
        notation.setContent("PUBLIC \"JPG 1.0\" \"image/jpeg\"");
        expected = "<notation name=\"jpg\" public=\"JPG 1.0\" system=\"image/jpeg\" />" + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{ notation, sb });
        assertEquals(expected, sb.toString());

        notation.setContent("jpg SYSTEM \"image/jpeg\"");
        expected = "<notation name=\"jpg\" public=SYSTEM system=\"image/jpeg\" />" + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{ notation, sb });
        assertEquals(expected, sb.toString());
    }
    
    @Test
    public void testAssembleEntity() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        DTDObject entity = new DTDObject("entity", ObjectType.ENTITY, "SYSTEM \"photoEntity.png\" NDATA png>");
        String expected = "<!ENTITY entity SYSTEM \"photoEntity.png\" NDATA png>" + s;
        Method method = Converter.class.getDeclaredMethod("assembleEntity", new Class[]{DTDObject.class, StringBuilder.class});
        method.setAccessible(true);
        
        StringBuilder sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{ entity, sb });
        assertEquals(expected, sb.toString());
    }
}