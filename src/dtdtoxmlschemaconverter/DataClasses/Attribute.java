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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toString(){
        return "ATT: " + parent + " || " + name +
                " || " + content;
    }
    
}
