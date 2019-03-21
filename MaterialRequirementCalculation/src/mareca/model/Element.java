package mareca.model;

public class Element extends AssemblyMember {

    /**
     * @param name name of the element
     */
    Element(String name) {
        super(false, name);
    }
    
    /**
     * @param name name of the element to initialize or to copy
     * @return the element
     */
    public static AssemblyMember getElement(String name) {
        return getAssemblyMember(false, name);
    }
    

    @Override
    public String toString() {
        return "COMPONENT";
    }
    
    /**
     * @return the corresponding assembly
     */
    Assembly toAssembly() {
        AssemblyMember.removeAssemblyMemberFromKnownList(this);
        return new Assembly(getName());
    }
}
