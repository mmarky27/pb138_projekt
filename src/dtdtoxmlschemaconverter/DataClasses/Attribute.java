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
public class Attribute {
    
    private String parent;
    private String name;
    private String content;
    
    public Attribute(String parent, String name, String content) {
        this.parent = parent;
        this.name = name;
        this.content = content;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getContent() {
        return content;
    }

    /**
     * @param type the type to set
     */
    public void setContent(String type) {
        this.content = content;
    }

    /**
     * @return the parent
     */
    public String getElemName() {
        return parent;
    }

    /**
     * @param elemName the parent to set
     */
    public void setElemName(String elemName) {
        this.parent = elemName;
    }
    
    
    
}
