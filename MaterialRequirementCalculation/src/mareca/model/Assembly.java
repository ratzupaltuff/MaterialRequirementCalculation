package mareca.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mareca.UnexpectedInputException;

/**
 * @author ratzupaltuff class for Assemblymembers with subElements, this class
 *         effectively cares how to store subAssemblies and how to interact wíth
 *         them
 */
public class Assembly extends AssemblyMember {

    private static final int COUNT_LIMIT = 1000;

    /**
     * hashmap for saving submembers and their quantity, it saves only the string
     * and not the assemblyMember object, because this way you can change the type
     * of the subassemblies without inform every Assembly containing this
     * subAssembly, integer is not in the assembly object, because logically this
     * would make no sense
     */
    HashMap<String, Integer> subMembers;

    /**
     * create a new Assembly, private because you should access Assemblymembers via
     * the custom get method to prevent creating multiple times the same object
     * 
     * @param name name of assembly
     */
    Assembly(String name) {
        super(true, name);
        subMembers = new HashMap<String, Integer>();
    }

    /**
     * get the assemblyMember with this name, and if it doesn't exist, create a new
     * assembly
     * 
     * @param name name of the element to initialize or to copy
     * @return the AssemblyMember with this name
     * @throws UnexpectedInputException if the already created assembly member
     *                                  cannot be accessed
     */
    static AssemblyMember getAssembly(String name) throws UnexpectedInputException {
        if (isAssemblyMemberInKnownList(name)) {
            AssemblyMember assemblyMember = getAssemblyMember(name);
            return assemblyMember;
        } else {
            AssemblyMember newAssemblyMember = new Assembly(name);
            return newAssemblyMember;
        }
    }

    /**
     * @param name          of the assembly to add
     * @param subAssemblies subAssembliesArray which should be added to the assembly
     *                      with the name 'name'
     * @throws UnexpectedInputException if there is an assembly in the list of child
     */
    public static void createNewAssembly(String name, AssemblyMemberCountTupel[] subAssemblies)
            throws UnexpectedInputException {
        Assembly assemblyToAdd;
        List<AssemblyMember> backupCopyAssemblyMembers = AssemblyMember.copyAssemblyMembers();
        if (AssemblyMember.isAssemblyMemberInKnownList(name)) {
            AssemblyMember assemblyMemberToOverride = AssemblyMember.getAssemblyMember(name);
            if (assemblyMemberToOverride.hasSubElements()) {
                throw new UnexpectedInputException("there is already an assembly with the name " + name);
            } else {
                Element elementToConvert = (Element) assemblyMemberToOverride;
                assemblyToAdd = elementToConvert.toAssembly();
            }
        } else {
            assemblyToAdd = (Assembly) Assembly.getAssembly(name);
        }

        for (AssemblyMemberCountTupel assemblyMemberCountTupel : subAssemblies) {
            assemblyToAdd.subMembers.put(
                    Element.getElement(assemblyMemberCountTupel.getAssemblyMemberString()).getName(),
                    assemblyMemberCountTupel.getIntCount());
        }

        if (isLoopCreatedWhenAdding()) {
            AssemblyMember.setAlreadyUsedAssemblyMembers(backupCopyAssemblyMembers);
            throw new UnexpectedInputException("if you add " + name + " you are creating a loop");
        }
    }

    @Override
    public String toString() {
        List<AssemblyMemberCountTupel> subMemberTupelList = new ArrayList<AssemblyMemberCountTupel>();
        subMemberTupelList = subMembersToTupelList();

        Collections.sort(subMemberTupelList, AssemblyMemberCountTupel.getNameComparatorComparator());

        StringBuilder output = new StringBuilder(30);
        for (AssemblyMemberCountTupel subMemberTupel : subMemberTupelList) {
            output.append(subMemberTupel.getAssemblyMemberString() + ":" + subMemberTupel.getCount() + ";");
        }

        String outputString = output.toString().substring(0, output.toString().length() - 1); // delete last semicolon
        return outputString;
    }

    /**
     * internal method to get a list of all direct members of this assembly
     * 
     * @return list of all directly contained assemblymembers
     */
    private List<AssemblyMemberCountTupel> subMembersToTupelList() {
        List<AssemblyMemberCountTupel> assemblyMemberCountTupelList = new ArrayList<AssemblyMemberCountTupel>();

        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMemberCountTupel tupelToAdd = new AssemblyMemberCountTupel(subMemberEntry.getKey(),
                    subMemberEntry.getValue());
            addSubMembersToTupelList(assemblyMemberCountTupelList, tupelToAdd);
        }
        return assemblyMemberCountTupelList;
    }

    private List<AssemblyMemberCountTupel> subMembersToTupelListRecursively(
            List<AssemblyMemberCountTupel> subMemberTupelList, long factorOfOccurence) throws UnexpectedInputException {

        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMember subAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
            AssemblyMemberCountTupel tupelToAdd = new AssemblyMemberCountTupel(subAssemblyMember.getName(),
                    subMemberEntry.getValue() * factorOfOccurence);
            addSubMembersToTupelList(subMemberTupelList, tupelToAdd);
            if (subAssemblyMember.hasSubElements()) {
                Assembly subAssembly = (Assembly) subAssemblyMember;
                subAssembly.subMembersToTupelListRecursively(subMemberTupelList,
                        subMemberEntry.getValue() * factorOfOccurence);
            }
        }
        return subMemberTupelList;
    }

    private List<AssemblyMemberCountTupel> addSubMembersToTupelList(List<AssemblyMemberCountTupel> listToAddElement,
            AssemblyMemberCountTupel tupelToAdd) {
        for (AssemblyMemberCountTupel assemblyMemberCountTupel : listToAddElement) {
            if (assemblyMemberCountTupel.getAssemblyMemberString().equals(tupelToAdd.getAssemblyMemberString())) {
                assemblyMemberCountTupel.changeCount(tupelToAdd.getCount());
                return listToAddElement;
            }
        }
        listToAddElement.add(tupelToAdd);
        return listToAddElement;
    }

    /**
     * @return a String representation of all Assemblies in this Assembly
     * @throws UnexpectedInputException if an element cannot be accessed
     */
    public String getAssembliesString() throws UnexpectedInputException {

        Comparator<AssemblyMemberCountTupel> assemblyComparator = AssemblyMemberCountTupel
                .getValueBeforeNameComparatorComparator();

        List<AssemblyMemberCountTupel> subMemberTupelList = new ArrayList<AssemblyMemberCountTupel>();
        subMembersToTupelListRecursively(subMemberTupelList, 1);

        Collections.sort(subMemberTupelList, assemblyComparator);

        StringBuilder output = new StringBuilder(30);
        for (AssemblyMemberCountTupel subMemberTupel : subMemberTupelList) {
            AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberTupel.getAssemblyMemberString());
            if (currentAssemblyMember.hasSubElements()) {
                output.append(currentAssemblyMember.getName() + ":" + subMemberTupel.getCount() + ";");
            }
        }
        String outputString;
        if (output.toString().length() == 0) {
            outputString = "EMPTY";
        } else {
            outputString = output.toString().substring(0, output.toString().length() - 1); // delete last semicolon
        }
        return outputString;
    }

    /**
     * @return a String representation of all Assemblies in this Assembly
     * @throws UnexpectedInputException if an element cannot be accessed
     */
    public String getElementsString() throws UnexpectedInputException {

        Comparator<AssemblyMemberCountTupel> assemblyComparator = AssemblyMemberCountTupel
                .getValueBeforeNameComparatorComparator();

        List<AssemblyMemberCountTupel> subMemberTupelList = new ArrayList<AssemblyMemberCountTupel>();
        subMembersToTupelListRecursively(subMemberTupelList, 1);

        Collections.sort(subMemberTupelList, assemblyComparator);

        StringBuilder output = new StringBuilder(30);
        for (AssemblyMemberCountTupel subMemberTupel : subMemberTupelList) {
            AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberTupel.getAssemblyMemberString());
            if (!currentAssemblyMember.hasSubElements()) {
                output.append(currentAssemblyMember.getName() + ":" + subMemberTupel.getCount() + ";");
            }
        }
        String outputString;
        if (output.toString().length() == 0) {
            outputString = "EMPTY";
        } else {
            outputString = output.toString().substring(0, output.toString().length() - 1); // delete last semicolon
        }
        return outputString;
    }

    /**
     * @param subMemberString to add
     * @param quantity        how often the submember occures in this assembly
     * @throws UnexpectedInputException if there is already an assembly with this
     *                                  name
     */
    public void addSubMember(String subMemberString, int quantity) throws UnexpectedInputException {
        List<AssemblyMember> backupCopyAssemblyMembers = AssemblyMember.copyAssemblyMembers();
        AssemblyMember subMember = Element.getElement(subMemberString);
        if (!containsSubMember(subMember)) {
            subMembers.put(subMember.getName(), quantity);
            boolean isLoopCreated = false;
            try {
                isLoopCreated = isLoopCreatedWhenAdding();
            } catch (UnexpectedInputException e) {
                isLoopCreated = true; // to remove all previously created assemblyMembers
            }
            if (isLoopCreated) {
                AssemblyMember.setAlreadyUsedAssemblyMembers(backupCopyAssemblyMembers);
                subMembers.remove(subMember.getName());
                throw new UnexpectedInputException("if you add " + subMember.getName() + " you are creating a loop");
            }
        } else {
            int oldQuantity = subMembers.get(subMember.getName());
            if (oldQuantity + quantity > 1000) {
                AssemblyMember.setAlreadyUsedAssemblyMembers(backupCopyAssemblyMembers);
                throw new UnexpectedInputException(
                        "the addition of this element would end in an absolute value higher than " + COUNT_LIMIT);
            }
            subMembers.replace(subMember.getName(), oldQuantity + quantity);
        }
    }

    private boolean containsSubMemberRecursively(AssemblyMember toSearchFor, List<Assembly> alreadyPassedMembers)
            throws UnexpectedInputException {
        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
            if (currentAssemblyMember.equals(toSearchFor)) {
                return true;
            } else if (currentAssemblyMember.hasSubElements()) {
                Assembly subAssembly = (Assembly) currentAssemblyMember;
                if (alreadyPassedMembers.contains(subAssembly)) {
                    throw new UnexpectedInputException("loop detected");
                } else {
                    //switch to a lower layer
                    alreadyPassedMembers.add(subAssembly);
                }
                if (subAssembly.containsSubMemberRecursively(toSearchFor, alreadyPassedMembers)) {
                    return true;
                }
                //switch to an upper layer
                alreadyPassedMembers.remove(subAssembly);
            }
        }
        return false;
    }

    /**
     * @param toSearchFor element to search for
     * @return whether the element is in this assembly or not
     * @throws UnexpectedInputException if a loop is detected
     */
    private boolean containsSubMemberRecursively(AssemblyMember toSearchFor) throws UnexpectedInputException {
        // you need to do this with a list to go to the leaves layer by layer, rather
        // than recursively where you might end in a loop
        List<Assembly> alreadyPassedMember = new LinkedList<Assembly>();
        return containsSubMemberRecursively(toSearchFor, alreadyPassedMember);
    }

    private boolean containsSubMember(AssemblyMember subMember) throws UnexpectedInputException {
        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
            if (currentAssemblyMember.equals(subMember)) {
                return true;
            }
        }
        return false;
    }

    /**
     * create a new element with same name and remove this assembly
     * 
     * @param assemblyToReplace assembly to delete and with which name to create the
     *                          new element
     * @throws UnexpectedInputException if the assembly cannot be removed
     */
    private static void toElement(Assembly assemblyToReplace) throws UnexpectedInputException {
        AssemblyMember.removeAssemblyMemberFromKnownList(assemblyToReplace.getName());
        new Element(assemblyToReplace.getName());
    }

    private void removeSubAssemblyMemberCompletely(AssemblyMember assemblyMember) throws UnexpectedInputException {
        subMembers.remove(assemblyMember.getName());

        if (subMembers.isEmpty()) { // if the parent assembly is empty after removal, convert to element
            toElement(this);
        }
        cleanUpAfterRemovalOfAssembly();
    }

    /**
     * remove x times the specifies subAssembly member, if it is nothing left,
     * convert the assembly containing this subAssembly to an element
     * 
     * @param assemblyMemberToRemove assemblyMemberToRemove by the amount
     * @param countToRemove          amount to remove the assemblyMember
     * @throws UnexpectedInputException if you cannot remove this amount
     */
    public void removeSubAssemblyPartially(AssemblyMember assemblyMemberToRemove, int countToRemove)
            throws UnexpectedInputException {
        if (containsSubMember(assemblyMemberToRemove)) {
            int currentCountOfAssemblyMember = subMembers.get(assemblyMemberToRemove.getName());
            if (currentCountOfAssemblyMember == countToRemove) {
                removeSubAssemblyMemberCompletely(assemblyMemberToRemove);
            } else if (currentCountOfAssemblyMember > countToRemove) {
                subMembers.replace(assemblyMemberToRemove.getName(), currentCountOfAssemblyMember - countToRemove);
            } else {
                throw new UnexpectedInputException("You cannot remove " + countToRemove + " from only "
                        + currentCountOfAssemblyMember + " present subAssemblyMember(s)");
            }
        } else {
            throw new UnexpectedInputException(assemblyMemberToRemove.getName() + " is not in " + getName());
        }
    }

    /**
     * method to delete all known elements, which are not used by any assemblies
     * 
     * @throws UnexpectedInputException if the method cant get the information
     *                                  wether a element is used or not
     */
    private static void cleanUpAfterRemovalOfAssembly() throws UnexpectedInputException {
        List<String> toRemoveElementStrings = new LinkedList<String>();
        for (AssemblyMember currentAssemblyMember : AssemblyMember.getAlreadyUsedAssemblyMembers()) {
            if (!currentAssemblyMember.hasSubElements()) {
                // check if they are needed after removal of parent Assembly
                if (!Assembly.isElementUsedInAnotherAssembly(currentAssemblyMember)) {
                    toRemoveElementStrings.add(currentAssemblyMember.getName());
                }
            }
        }
        for (String string : toRemoveElementStrings) {
            AssemblyMember.removeAssemblyMemberFromKnownList(string);
        }
    }

    private static boolean isElementUsedInAnotherAssembly(AssemblyMember assemblyMemberToCheck)
            throws UnexpectedInputException {
        for (AssemblyMember currentAssemblyMember : AssemblyMember.getAlreadyUsedAssemblyMembers()) {
            if (currentAssemblyMember.hasSubElements()) {
                Assembly assemblyToSearchInAssembly = (Assembly) currentAssemblyMember;
                if (assemblyToSearchInAssembly.containsSubMember(assemblyMemberToCheck)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * method to remove the function of an assembly from a assembly and leaves an
     * element
     * 
     * @param assemblyToReplace assembly to replace with an element
     * @throws UnexpectedInputException if this assembly member doesn't exist
     */
    public static void replaceSubAssemblyWithElementRecursively(Assembly assemblyToReplace)
            throws UnexpectedInputException {
        toElement(assemblyToReplace);
        cleanUpAfterRemovalOfAssembly();
    }

    /**
     * method which is called when an assembly is added which may created a loop,
     * then this method detects a loop, by checking if any Assembly contains itself
     * 
     * @return true if a loop can be detected, false if everything is all right
     * @throws UnexpectedInputException if the method cannot determine if an assembly contains another
     */
    private static boolean isLoopCreatedWhenAdding() throws UnexpectedInputException {
        Object[] listOfAllMembers = AssemblyMember.getAlreadyUsedAssemblyMembers().toArray();
        for (int numberToCheck = listOfAllMembers.length - 1; numberToCheck > 0; numberToCheck--) {
            AssemblyMember assemblyMember = (AssemblyMember) listOfAllMembers[numberToCheck];
            if (assemblyMember.hasSubElements()) {
                Assembly assemblyToCheckContains = (Assembly) assemblyMember;
                if (assemblyToCheckContains.containsSubMemberRecursively(assemblyToCheckContains)) {
                    return true;
                }
            }
        }
        return false;
    }
}
