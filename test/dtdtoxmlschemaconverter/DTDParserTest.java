/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.DataClasses.Attribute;
import dtdtoxmlschemaconverter.DataClasses.DTDObject;
import dtdtoxmlschemaconverter.DataClasses.ObjectType;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Goolomb
 */
public class DTDParserTest {
    
    private String dtdToParse;
    private String dtdToParse2;
    private String dtdToParse3;
    private List<DTDObject> elements = new ArrayList<>();
    private List<Attribute> attributes = new ArrayList<>();
    
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
     * Test of parseDTD method, of class Converter.
     */
    @Test
    public void testOutput() {
        List<DTDObject> result = DTDParser.output(dtdToParse);
        List<DTDObject> result2 = DTDParser.output(dtdToParse2);
        List<DTDObject> result3 = DTDParser.output(dtdToParse3);
        
        assertEquals(elements.size(), result.size());
        assertArrayEquals(elements.toArray(), result.toArray());
        assertEquals(elements.size(), result2.size());
        assertArrayEquals(elements.toArray(), result2.toArray());

        assertTrue(elements.size() != result3.size());
    }

    /*@Test
    public void testParseElement() throws NoSuchMethodException, InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        Method method = Converter.class.getDeclaredMethod("parseElement", new Class[]{String.class});
        method.setAccessible(true);
        DTDObject result = (DTDObject) method.invoke(new Converter(), new java.lang.Object[]{"<!ELEMENT zamestnanec   (jmeno, prijmeni)>"});
        DTDObject result2 = (DTDObject) method.invoke(new Converter(), new java.lang.Object[]{"<!ELEMENT zamestnanec   (prijmeni, jmeno)>"});
        DTDObject result3 = (DTDObject) method.invoke(new Converter(), new java.lang.Object[]{"<!ELEMENT zamestnanec   (jmeno | prijmeni)>"});
        DTDObject result4 = (DTDObject) method.invoke(new Converter(), new java.lang.Object[]{"<!ELEMENT zamestnankyne   (jmeno, prijmeni)>"});
        
        assertEquals(elements.get(0), result);
        assertTrue(!elements.get(0).equals(result2));
        assertTrue(!elements.get(0).equals(result3));
        assertTrue(!elements.get(0).equals(result4));
    }*/

    /*@Test
    public void testParseAttributes() throws NoSuchMethodException, InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        Method method = Converter.class.getDeclaredMethod("parseAttributes", new Class[]{String.class});
        method.setAccessible(true);
        List<Attribute> result = (List<Attribute>) method.invoke(
                new Converter(),
                new java.lang.Object[]{
                    "  <!ATTLIST zamestnanec\n"
                + "            id          CDATA   #REQUIRED>\n"
                + "            plat        CDATA   #IMPLIED\n"
                + "            pohlavi     (muz | zena | jine)   #IMPLIED>"});
        
        assertEquals(attributes.size(), result.size());
        assertArrayEquals(attributes.toArray(), result.toArray());
    }*/

    /*@Test
    public void testParseAttribute() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException { //returns Attribute
        Method method = Converter.class.getDeclaredMethod("parseAttribute", new Class[]{String.class});
        method.setAccessible(true);
        Attribute a = (Attribute) method.invoke(new Converter(), new java.lang.Object[]{"zamestnanec            id          CDATA   #REQUIRED>"});
        Attribute a2 = (Attribute) method.invoke(new Converter(), new java.lang.Object[]{"zamestnanec            id          CDATA   #IMPLIED>"});
        Attribute a3 = (Attribute) method.invoke(new Converter(), new java.lang.Object[]{"zamestnavatel            id          CDATA   #REQUIRED>"});
        
        assertEquals(attributes.get(0), a);
        assertTrue(!attributes.get(0).equals(a2));
        assertTrue(!attributes.get(0).equals(a3));

    }*/
    /**
     * Test of getObjects method, of class DTDParser.
     */
    @Test
    public void testGetObjects() {
        System.out.println("getObjects");
        DTDParser instance = null;
        List<DTDObject> expResult = null;
        List<DTDObject> result = instance.getObjects();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of printObjs method, of class DTDParser.
     */
    @Test
    public void testPrintObjs() {
        System.out.println("printObjs");
        DTDParser instance = null;
        instance.printObjs();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
