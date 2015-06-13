/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.DataClasses.Attribute;
import dtdtoxmlschemaconverter.DataClasses.DTDObject;
import dtdtoxmlschemaconverter.DataClasses.ObjectType;
import java.lang.reflect.Method;
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

    @Test
    public void testCreateNullDTDParser(){
        try {
            DTDParser parser = new DTDParser(null);
            fail();
        }
        catch (NullPointerException e) {
            //OK
        }
    }

    @Test
    public void testGetObjects() {
        DTDParser parser = new DTDParser(dtdToParse);
        try {
            parser.getObjects().remove(0);
        }
        catch (/*UnsupportedOperation*/Exception e){
            //OK
        }
    }
    
    /*@Test
    public void createObjects(String[] unparsedObjects){
        String[] input = new String[] {
            "ENTITY entity SYSTEM \"photoEntity.png\" NDATA png",
            "NOTATION jpg PUBLIC \"JPG 1.0\" \"image/jpeg\"",
            "ATTLIST sender company CDATA #FIXED \"Microsoft\"",
            "ELEMENT human (man | woman | alien)" };
        //dont know how to test private method without returning value
    }*/
}
