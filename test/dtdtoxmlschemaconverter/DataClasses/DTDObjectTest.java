/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter.DataClasses;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Goolomb
 */
public class DTDObjectTest {
    
    @Test
    public void testNullParameterToDTDObject() {
        DTDObject obj;
        
        try {
            obj = new DTDObject("name", ObjectType.ELEMENT, null);
            fail();
        }
        catch(IllegalArgumentException e) {
            //OK
        }
        
        try {
            obj = new DTDObject(null, ObjectType.ELEMENT, "some content");
            fail();
        }
        catch(IllegalArgumentException e) {
            //OK
        }
        
        try {
            obj = new DTDObject("name", null, "some content");
            fail();
        }
        catch(IllegalArgumentException e) {
            //OK
        }
    }
    
}
