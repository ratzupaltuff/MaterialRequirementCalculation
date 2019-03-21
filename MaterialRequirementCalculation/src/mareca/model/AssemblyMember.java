package mareca.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mareca.UnexpectedInputException;

public abstract class AssemblyMember {
    private static List<AssemblyMember> alreadyUsedAssemblyMembers = new LinkedList<AssemblyMember>();
    private final boolean hasSubElements;
    private final String name;

    /**
     * @param hasSubElements whether it is element or assembly
     * @param name           the name of the assembly member
     * @throws UnexpectedInputException
     */
    AssemblyMember(boolean hasSubElements, String name) {

        this.hasSubElements = hasSubElements;
        this.name = name;
        addAssemblyMemberToKnownList(this);
    }

    /**
     * special method for general assembly list constructions without a name
     * 
     * @param hasSubElements whether it is element or assembly
     * @throws UnexpectedInputException
     */
    AssemblyMember(boolean hasSubElements) {
        this.hasSubElements = hasSubElements;
        this.name = null;
    }

    /**
     * use with caution, if you declare another class which extends assembly member
     * directly
     * 
     * @param hasSubElements if it should be declared as a member with child
     *                       elements or not
     * @param name           of the assembly element
     * @return the right assembly member object
     */
    static AssemblyMember getAssemblyMember(boolean hasSubElements, String name) {
        if (isAssemblyMemberInKnownList(name)) {
            AssemblyMember assemblyMember = getAssemblyMemberFromKnownList(name);
            return assemblyMember;
        } else {
            AssemblyMember newAssemblyMember;
            if (hasSubElements) {
                newAssemblyMember = new Assembly(name);

            } else {
                newAssemblyMember = new Element(name);
            }
            addAssemblyMemberToKnownList(newAssemblyMember);
            return newAssemblyMember;
        }
    }

    /**
     * @throws UnexpectedInputException if there is no such element
     * @param hasSubElements
     * @param name           of the element you want to get
     * @return the right assembly member object
     */
    public static AssemblyMember getAssemblyMember(String name) throws UnexpectedInputException {
        if (isAssemblyMemberInKnownList(name)) {
            AssemblyMember assemblyMember = getAssemblyMemberFromKnownList(name);
            return assemblyMember;
        } else {
            throw new UnexpectedInputException("there is no entry for an Assembly member with the name: " + name);
        }
    }

    /**
     * @return true if it has child-elements false if it is atomar
     */
    public boolean hasSubElements() {
        return hasSubElements;
    }

    /**
     * @return name of assembly member
     */
    public String getName() {
        return name;
    }

    /**
     * @return string which identifies the contents of this assembly member
     */
    public String toString() {
        return name;
    }

    /**
     * @param assemblyMemberToAdd member to add to the internal list of known
     *                            assemblies
     */
    static void addAssemblyMemberToKnownList(AssemblyMember assemblyMemberToAdd) {
        alreadyUsedAssemblyMembers.add(assemblyMemberToAdd);
    }

    /**
     * @param name of the element to search for
     * @return the element if it exists
     * @throws UnexpectedInputException if there is no such element
     */
    static AssemblyMember getAssemblyMemberFromKnownList(String name)/* throws UnexpectedInputException */ {
        Iterator<AssemblyMember> i = alreadyUsedAssemblyMembers.iterator();

        while (i.hasNext()) {
            AssemblyMember currentAssemblyMember = (AssemblyMember) i.next();
            if (currentAssemblyMember.getName().equals(name)) {
                return currentAssemblyMember;
            }
        }
        return null;
        // throw new UnexpectedInputException("no such element found: " + name);
    }

    /**
     * @param name if the element, to check if its known
     * @return true if there is already an Assembly member with this name
     */
    static boolean isAssemblyMemberInKnownList(String name) {
        Iterator<AssemblyMember> i = alreadyUsedAssemblyMembers.iterator();

        while (i.hasNext()) {
            AssemblyMember currentAssemblyMember = (AssemblyMember) i.next();
            if (currentAssemblyMember.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param assemblyMember object to compare with
     * @return true if they have the same name, else false
     */
    public boolean equals(Object assemblyMember) {
        if (assemblyMember instanceof AssemblyMember) {
            return name.equals(((AssemblyMember) assemblyMember).getName());
        }
        return false;
    }
    
    /**
     * @param assemblyMemberToRemove to remove
     */
    static void removeAssemblyMemberFromKnownList(AssemblyMember assemblyMemberToRemove) {
        alreadyUsedAssemblyMembers.remove(assemblyMemberToRemove);
    }
    
}
