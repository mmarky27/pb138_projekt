package dtdtoxmlschemaconverter;

import dtdtoxmlschemaconverter.DataClasses.Attribute;
import dtdtoxmlschemaconverter.DataClasses.DTDObject;
import dtdtoxmlschemaconverter.DataClasses.ObjectType;

import java.util.*;

/**
 * Created by jnkre_000 on 6/12/2015.
 */
public class DTDParser {

    private String unparsedDTD = null;
    private List<DTDObject> objects = new ArrayList<>();
    private List<Attribute> attributes = new ArrayList<>();


    public DTDParser(String unparsedDTD){
        if(unparsedDTD == null){
            throw new NullPointerException("Parser(constructor): unparsedDTD is null");
        }

        this.unparsedDTD = unparsedDTD;
    }

    public List<DTDObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }


    public static List<DTDObject> output(String unparsedDTD){
        DTDParser tmp = new DTDParser(unparsedDTD);
        tmp.parse();

        return tmp.getObjects();
    }


    private void parse(){
        if(unparsedDTD == null){
            throw new NullPointerException("Parser.parse: unparsedDTD is null");
        }

        //preparing the string for parsing
        unparsedDTD = unparsedDTD.substring(unparsedDTD.indexOf("<") + 2,
                                            unparsedDTD.lastIndexOf(">"));
        String[] unparsedObjects = unparsedDTD.split(">\\s*<!");

        createObjects(unparsedObjects);
        matchAttributes();

    }

    private void createObjects(String[] unparsedObjects){

        if(unparsedObjects == null){
            throw new NullPointerException("createObjects: unparsedObjects is null");
        }
        for(int i = 0; i < unparsedObjects.length; i++){
            if(unparsedObjects[i] == null){
                throw new NullPointerException("createObjects: unparsedObjects[" + i
                        +"] is null");
            }
        }

        for(String i : unparsedObjects){
            String[] temp = i.split("\\s+", 3);

            if(temp.length != 3){
                //cant think of better one
                throw new IllegalArgumentException("createObjects: wrong size after split");
            }
            
            switch(temp[0]){
                case "ENTITY":
                    objects.add(new DTDObject(temp[1],ObjectType.ENTITY,temp[2]));
                    break;
                case "ATTLIST":
                    //attributes.add(new Attribute(temp[1],temp[2]));
                    //this is all your fault, you know who
                    String[] temp2 = temp[2].split(" ", 2);
                    attributes.add(new Attribute(temp[1], temp2[0],temp2[1]));
                    break;
                case "ELEMENT":
                    objects.add(new DTDObject(temp[1],ObjectType.ELEMENT,temp[2]));
                    break;
                case "NOTATION":
                    objects.add(new DTDObject(temp[1],ObjectType.NOTATION,temp[2]));
                    break;
                default:
                    System.out.println("unrecognised object");
            }
        }
    }

    private void matchAttributes(){
        if(attributes == null){
            throw new NullPointerException("matchAttributes: \"attributes\" is null");
        }
        if(objects == null){
            throw new NullPointerException("matchAttributes: \"objects\" is null");
        }

        if(attributes.isEmpty() || objects.isEmpty()){
            return;
        }

        for(Attribute i : attributes){
            objects.stream().filter(j -> i.getParent().equals(j.getName()))
                    .forEach(j -> j.addAttribute(i));
        }
    }

    public void printObjs(){
        for(DTDObject i : objects){
            System.out.println(i);
            i.getAttributes().forEach(System.out::println);
        }
    }


}
