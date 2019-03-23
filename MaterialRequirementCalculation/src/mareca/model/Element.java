package mareca.model;

import mareca.UnexpectedInputException;

/**
 * @author ratzupaltuff class for an element which is a subclass of an
 *         AssemblyMember and has no subAssemblyMembers
 * 
 *         assembly and element are two different classes because this is
 *         object-oriented
 *
 */
public class Element extends AssemblyMember {

    /**
     * @param name of the element to create
     */
    Element(String name) {
        super(false, name);
    }

    /**
     * get the assembly member with this name, and if none exists, create a new
     * element with this name
     * 
     * @param name name of the element to initialize or to copy
     * @return the element
     * @throws UnexpectedInputException if it cannot access the assembly member
     */
    static AssemblyMember getElement(String name) throws UnexpectedInputException {
        if (isAssemblyMemberInKnownList(name)) {
            AssemblyMember assemblyMember = getAssemblyMember(name);
            return assemblyMember;
        } else {
            AssemblyMember newAssemblyMember = new Element(name);
            return newAssemblyMember;
        }
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
