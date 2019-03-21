package mareca.model;

import mareca.UnexpectedInputException;

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
    static AssemblyMember getElement(String name) {
        return getAssemblyMember(false, name);
    }
    

    @Override
    public String toString() {
        return "COMPONENT";
    }
    
    /**
     * @return the corresponding assembly
     * @throws UnexpectedInputException if the current element cannot be removed
     */
    Assembly toAssembly() throws UnexpectedInputException {
        AssemblyMember.removeAssemblyMemberFromKnownList(getName());
        return new Assembly(getName());
    }
}
