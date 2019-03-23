package mareca.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mareca.UnexpectedInputException;

/**
 * @author ratzupaltuff abstract class which declares how an assemblyMember
 *         should behave and which methods it should have at least, additionally
 *         it declares some default methods and cares about a static list, in
 *         which all known members are saved
 * 
 *         1. i decided not to use an interface, because then i cannot declare
 *         default methods and i don't need the advantage of implementing
 *         multiple interfaces, and converting between the objects is a little
 *         bit ugly but i thought it was better than interfaces at this point
 * 
 *         2. you could have created a separate object which holds a list of all
 *         valid AssemblyMembers, but i think it is more intuitive to access all
 *         known assemblyMembers in a static way like a database, it is kind of
 *         a singleton pattern i used, to prevent creating the "same" object
 *         multiple times
 * 
 *         3. by choosing this layout i forced myself to use statics everywhere,
 *         if i had to model this again, i probably would choose a separate
 *         class for holding the elements to prevent using static methods
 *         everywhere
 *
 */
public abstract class AssemblyMember {
    private static List<AssemblyMember> alreadyUsedAssemblyMembers = new ArrayList<AssemblyMember>();
    private final boolean hasSubElements;
    private final String name;

    /**
     * private constructor to make it impossible from outside to create new elements
     * if there is already one with this name
     * 
     * @param hasSubElements whether it is element or assembly
     * @param name           the name of the assembly member
     */
    AssemblyMember(boolean hasSubElements, String name) {
        this.hasSubElements = hasSubElements;
        this.name = name;
        addAssemblyMemberToKnownList(this);
    }

    /**
     * use with caution, if you declare another class which extends assembly member
     * directly make sure you can cast it to either assembly or element or edit the
     * assembly class
     * 
     * @param name of the element you want to get
     * @return the right assembly member object if it was created previously
     * @throws UnexpectedInputException if there is no such element in the known
     *                                  list
     */
    public static AssemblyMember getAssemblyMember(String name) throws UnexpectedInputException {
        Iterator<AssemblyMember> i = alreadyUsedAssemblyMembers.iterator();

        while (i.hasNext()) {
            AssemblyMember currentAssemblyMember = i.next();
            if (currentAssemblyMember.getName().equals(name)) {
                return currentAssemblyMember;
            }
        }
        throw new UnexpectedInputException("there is no entry for an Assembly member with the name: " + name);

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
     * @return string which identifies the (contents of this) assembly member
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
     * @param name if the element, to check if its known
     * @return true if there is already an Assembly member with this name
     */
    static boolean isAssemblyMemberInKnownList(String name) {
        Iterator<AssemblyMember> i = alreadyUsedAssemblyMembers.iterator();

        while (i.hasNext()) {
            AssemblyMember currentAssemblyMember = i.next();
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
     * @throws UnexpectedInputException if there is no element to remove with this
     *                                  name
     */
    static void removeAssemblyMemberFromKnownList(String assemblyMemberToRemove) throws UnexpectedInputException {
        alreadyUsedAssemblyMembers.remove(getAssemblyMember(assemblyMemberToRemove));
    }

    /**
     * @return the list of already used assemblyMembers
     */
    static List<AssemblyMember> getAlreadyUsedAssemblyMembers() {
        return alreadyUsedAssemblyMembers;
    }

    /**
     * @param newList list with witch to override the old one
     */
    static void setAlreadyUsedAssemblyMembers(List<AssemblyMember> newList) {
        alreadyUsedAssemblyMembers.clear();
        for (AssemblyMember assemblyMember : newList) {
            alreadyUsedAssemblyMembers.add(assemblyMember);
        }
        alreadyUsedAssemblyMembers = newList;
    }

    /**
     * @return a copy of the list
     */
    static List<AssemblyMember> copyAssemblyMembers() {
        List<AssemblyMember> copiedList = new ArrayList<AssemblyMember>();
        for (AssemblyMember assemblyMember : alreadyUsedAssemblyMembers) {
            copiedList.add(assemblyMember);
        }
        return copiedList;
    }

}
