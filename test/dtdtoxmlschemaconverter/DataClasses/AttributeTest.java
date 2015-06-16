/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter.DataClasses;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Goolomb
 */
public class AttributeTest {
    
    @Test
    public void testNullParameterToAttribute() {
        Attribute attr;
        
        try {
            attr = new Attribute("parent", "name", null);
            fail();
        }
        catch(IllegalArgumentException e) {
            //OK
        }
        
        try {
            attr = new Attribute("parrent", null, "some content");
            fail();
        }
        catch(IllegalArgumentException e) {
            //OK
        }
        
        try {
            attr = new Attribute(null, "name", "some content");
            fail();
        }
        catch(IllegalArgumentException e) {
            //OK
        }
    }
}
