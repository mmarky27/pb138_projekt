/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter.DataClasses;

/**
 *
 * @author Patrik
 */
public enum ObjectType {
    ELEMENT("!ELEMENT"), NOTATION("!NOTATION"), ENTITY("!ENTITY");
    
    private String value;
    
    private ObjectType(String value) {
        this.value = value;
    }
    
    public static ObjectType fromString(String text) {
        if (text != null) {
          for (ObjectType ob : ObjectType.values()) {
            if (text.equalsIgnoreCase(ob.value)) {
              return ob;
            }
          }
        }
        return null;
      }
}
