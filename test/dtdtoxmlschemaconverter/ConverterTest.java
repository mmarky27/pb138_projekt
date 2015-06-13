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
                + "            id          CDATA   #REQUIRED>\n"
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
    
     /**
     * Test of assembleXMLSchema method, of class Converter.
     */
    @Test
    public void testAssembleXMLSchema() {
        String result = Converter.assembleXMLSchema(elements);
        String result2 = Converter.assembleXMLSchema(DTDParser.output(dtdToParse));
        String result3 = Converter.assembleXMLSchema(DTDParser.output(dtdToParse2));
        String result4 = Converter.assembleXMLSchema(DTDParser.output(dtdToParse3));

        //TODO
        System.out.println(result);
        System.out.println("here");
        /*assertEquals(expected, result);
        assertEquals(expected, result2);
        assertTrue(!expected.equals(result3));
        */
        
        //TODO assemblovani s entitama
    }
    
    @Test
    public void testAssembleElem() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String expected = "<element name=\"zamestnanec\" >" + s
            + "<complexType>" + s
            + "<sequence>" + s
            + "<element ref=\"jmeno\" />" + s
            + "<element ref=\"narozen\" />"+ s
            + "</sequence>" + s
            + "<attribute name=\"id\" type=\"string\" use=\"required\"" + s
            + "<attribute name=\"id\" type=\"string\"" + s
                //jak je to s tim enumem, se mi moc nechce psat jako nejake restriction, pockam, jak to udela Patas!
            + "<attribute name=\"id\" type=\"enumeration\" values=\"muz|zena|jine\"" + s
            + "</complexType>" + s
            + "</element>" + s;
        Method method = Converter.class.getDeclaredMethod("assembleElem", new Class[]{DTDObject.class, StringBuilder.class});
        method.setAccessible(true);
        
        String result  = (String) method.invoke(new Converter(), new java.lang.Object[]{elements.get(0), new StringBuilder()});
        assertEquals(expected, result);

        expected = "<element name=\"chleba\" type=\"string\" />" + s;
        result  = (String) method.invoke(new Converter(), new java.lang.Object[]{new DTDObject("chleba", ObjectType.ELEMENT, "(#PCDATA)"), new StringBuilder()});
        assertEquals(expected, result);
        
        expected = "<element name=\"chleba\" />" + s;
        result  = (String) method.invoke(new Converter(), new java.lang.Object[]{new DTDObject("chleba", ObjectType.ELEMENT, "(EMPTY)"), new StringBuilder()});
        assertEquals(expected, result);

        expected = "<element name=\"chleba\" >" + s
            + "<complexType>" + s
            + "<sequence>" + s
            + "<any minOccurs=\"0\" />" + s
            + "</sequence>"
            + "</complexType>" + s
            + "</element>" + s;
        result  = (String) method.invoke(new Converter(), new java.lang.Object[]{new DTDObject("chleba", ObjectType.ELEMENT, "(ANY)"), new StringBuilder()});
        assertEquals(expected, result);
        
        //Mozna jeste mixed
    }

    @Test
    public void testAssembleAttr() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException { //returns String
        String expected = "<attribute name=\"plat\" type=\"string\">";
        Method method = Converter.class.getDeclaredMethod("assembleAttr", new Class[]{Attribute.class});
        method.setAccessible(true);
        
        String result = (String) method.invoke(new Converter(), new java.lang.Object[]{ attributes.get(1)});
        assertEquals(expected, result);
        
        expected = "<attribute name=\"id\" type=\"string\" use=\"required\">";
        result = (String) method.invoke(new Converter(), new java.lang.Object[]{ attributes.get(0)});
        assertEquals(expected, result);
    }

    @Test
    public void testAssembleAttrs() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String expected = "<attribute name=\"plat\" type=\"string\"\n"
                + "<attribute name=\"id\" type=\"string\" use=\"required\">";
        //To nevim, jestli bude fungovat s tim List.class, uvidime rano
        Method method = Converter.class.getDeclaredMethod("assembleAttrs", new Class[]{List.class, StringBuilder.class});
        method.setAccessible(true);
        
        StringBuilder sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{ attributes.subList(0, 2),sb });
        
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
        
        expected = "<sequence minOccurs=\"0\">" + s
            + "<element ref=\"nazev\" />" + s
            + "</sequence>" + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{sb, "(nazev?)"});
        assertEquals(expected, sb.toString());
        
        expected = "<all minOccurs=\"0\">" + s
            + "<element ref=\"nazev\" />" + s
            + "</all>" + s;
        sb = new StringBuilder();
        method.invoke(new Converter(), new java.lang.Object[]{sb, "(#PCDATA | nazev)"});
        assertEquals(expected, sb.toString());
    }

    @Test
    public void testSplitContent() throws SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = Converter.class.getDeclaredMethod("splitContent", new Class[]{String.class, String.class, String.class, String.class});
        method.setAccessible(true);
        List<String> result = (ArrayList<String>) method.invoke(new Converter(), new java.lang.Object[]{"(potato, egg, cheese)", ",", "(", ")"});
        List<String> result2 = (ArrayList<String>) method.invoke(new Converter(), new java.lang.Object[]{"(potato | (egg, cheese))", "|", "(", ")"});
        
        try {
            List<String> result3 = (ArrayList<String>) method.invoke(new Converter(), new java.lang.Object[]{"(potato | (egg, cheese))", "invalid", "(", ")"});
            fail("Invalid delimiter");
        }
        catch(IllegalArgumentException | InvocationTargetException e) {
            //OK
        }
        
        assertNotNull(result);
        assertEquals(3, result.size());
        
        assertNotNull(result2);
        assertEquals(2, result2.size());

        //TODO
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
    
}