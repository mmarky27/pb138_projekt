/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dtdtoxmlschemaconverter.DataClasses;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Patrik
 */
public class DTDObject {
    
    private String name;
    private ObjectType type;
    private String content;
    private List<Attribute> attributes;
    
    public DTDObject (String name, ObjectType type, String content){
        this.name = name;
        this.type = type;
        this.content = content;
        this.attributes = new ArrayList<>();
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
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the attributes
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }
    
    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    /**
     * @return the type
     */
    public ObjectType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ObjectType type) {
        this.type = type;
    }

    public String toString(){
        return name + " || " + type + " || " + content;

    }
}
