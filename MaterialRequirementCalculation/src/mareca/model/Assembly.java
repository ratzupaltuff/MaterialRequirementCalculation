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

public class Assembly extends AssemblyMember {

    /**
     * hashmap for saving submembers and their quantity
     */
    HashMap<String, Integer> subMembers;

    /**
     * @param name name of assembly
     */
    Assembly(String name) {
        super(true, name);
        subMembers = new HashMap<String, Integer>();
    }

    /**
     * special method for assemblies without a name
     */
    public Assembly() {
        super(true);
        subMembers = new HashMap<String, Integer>();
    }

    /**
     * @param name          of the assembly to add
     * @param subAssemblies of the assembly to add
     * @param count         of subassembly
     * @throws UnexpectedInputException if there is an assembly in the list of child
     */
    public void createNewSubAssembly(String name, AssemblyMemberCountTupel[] subAssemblies, int count)
            throws UnexpectedInputException {
        Assembly assemblyToAdd;
        List<AssemblyMember> backupCopyAssemblyMembers = AssemblyMember.copyAssemblyMembers();
        if (AssemblyMember.isAssemblyMemberInKnownList(name)) {
            AssemblyMember assemblyMemberToOverride = AssemblyMember.getAssemblyMemberFromKnownList(name);
            if (assemblyMemberToOverride.hasSubElements()) {
                throw new UnexpectedInputException("there is already an assembly with the name " + name);
            } else {
                Element elementToConvert = (Element) assemblyMemberToOverride;
                assemblyToAdd = elementToConvert.toAssembly();
            }
        } else {
            assemblyToAdd = (Assembly) getAssemblyMember(true, name);
        }

        for (AssemblyMemberCountTupel assemblyMemberCountTupel : subAssemblies) {
            assemblyToAdd.subMembers.put(AssemblyMember
                    .getAssemblyMember(false, assemblyMemberCountTupel.getAssemblyMemberString()).getName(),
                    assemblyMemberCountTupel.getIntCount());
        }
        subMembers.put(assemblyToAdd.getName(), count);

        if (isLoopCreatedWhenAdding()) {
            AssemblyMember.setAlreadyUsedAssemblyMembers(backupCopyAssemblyMembers);
            throw new UnexpectedInputException("if you add " + name + " you are creating a loop");
        }
    }

    /**
     * @param name name of the assembly to initialize or to copy
     * @return the assembly
     */
    public static Assembly getAssembly(String name) {
        return (Assembly) getAssemblyMember(true, name);
    }

    @Override
    public String toString() {

        List<AssemblyMemberCountTupel> subMemberTupelList = new ArrayList<AssemblyMemberCountTupel>();
        subMemberTupelList = subMembersToTupelList();

        Collections.sort(subMemberTupelList, getNameComparatorComparator());

        StringBuilder output = new StringBuilder(30);
        for (AssemblyMemberCountTupel subMemberTupel : subMemberTupelList) {
            output.append(subMemberTupel.getAssemblyMemberString() + ":" + subMemberTupel.getCount() + ";");
        }

        String outputString = output.toString().substring(0, output.toString().length() - 1); // delete last semicolon
        return outputString;
    }

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

    /**
     * @param subMemberTupelList list to add the elements to
     * @param factorOfOccurence  how often the current element occurs in upper
     *                           assemblies
     * @return the list with added contents
     * @throws UnexpectedInputException if
     */
    List<AssemblyMemberCountTupel> subMembersToTupelListRecursively(List<AssemblyMemberCountTupel> subMemberTupelList,
            long factorOfOccurence) throws UnexpectedInputException {

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
        // Iterator<AssemblyMemberCountTupel> iterator = new
        // Iterator<AssemblyMemberCountTupel>();
        for (AssemblyMemberCountTupel assemblyMemberCountTupel : listToAddElement) {
            if (assemblyMemberCountTupel.getAssemblyMemberString().equals(tupelToAdd.getAssemblyMemberString())) {
                assemblyMemberCountTupel.changeCount(tupelToAdd.getCount());
                return listToAddElement;
            }
        }
        listToAddElement.add(tupelToAdd);
        return listToAddElement;
    }

    private Comparator<AssemblyMemberCountTupel> getValueBeforeNameComparatorComparator() {
        Comparator<AssemblyMemberCountTupel> assemblyComparator = new Comparator<AssemblyMemberCountTupel>() {
            /**
             * @param tupel1 tupel one to compare
             * @param tupel2 tupel two to compare
             * @return value <0 if tupel1 before tupel2, if they are the same 0, and >0 else
             */
            @Override
            public int compare(AssemblyMemberCountTupel tupel1, AssemblyMemberCountTupel tupel2) {

                if (tupel1.getCount() != tupel2.getCount()) {
                    return (int) (tupel2.getCount() - tupel1.getCount());
                } else {
                    return tupel1.getAssemblyMemberString().compareTo(tupel2.getAssemblyMemberString());
                }
            }
        };
        return assemblyComparator;
    }

    private Comparator<AssemblyMemberCountTupel> getNameComparatorComparator() {
        Comparator<AssemblyMemberCountTupel> assemblyComparator = new Comparator<AssemblyMemberCountTupel>() {
            /**
             * @param tupel1 tupel one to compare
             * @param tupel2 tupel two to compare
             * @return value <0 if tupel1 before tupel2, if they are the same 0, and >0 else
             */
            @Override
            public int compare(AssemblyMemberCountTupel tupel1, AssemblyMemberCountTupel tupel2) {
                return tupel1.getAssemblyMemberString().compareTo(tupel2.getAssemblyMemberString());
            }
        };
        return assemblyComparator;
    }

    /**
     * @return a String representation of all Assemblies in this Assembly
     * @throws UnexpectedInputException if
     */
    public String getAssembliesString() throws UnexpectedInputException {

        Comparator<AssemblyMemberCountTupel> assemblyComparator = getValueBeforeNameComparatorComparator();

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
     * @throws UnexpectedInputException if
     */
    public String getElementsString() throws UnexpectedInputException {

        Comparator<AssemblyMemberCountTupel> assemblyComparator = getValueBeforeNameComparatorComparator();

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
     * @param subMember to add
     * @param quantity  how often the submember occures in this assembly
     * @throws UnexpectedInputException if there is already an assembly with this
     *                                  name
     */
    public void addSubMember(AssemblyMember subMember, int quantity) throws UnexpectedInputException {
        if (!containsSubMember(subMember)) {
            List<AssemblyMember> backupCopyAssemblyMembers = AssemblyMember.copyAssemblyMembers();
            subMembers.put(subMember.getName(), quantity);
            if (isLoopCreatedWhenAdding()) {
                AssemblyMember.setAlreadyUsedAssemblyMembers(backupCopyAssemblyMembers);
                throw new UnexpectedInputException("if you add " + subMember.getName() + " you are creating a loop");
            }
        } else {
            int oldQuantity = subMembers.get(subMember.getName());
            subMembers.replace(subMember.getName(), oldQuantity + quantity);
        }
    }

    /**
     * searches for occurences of the subMember, does this recursively
     * 
     * @param toSearchFor          AssemblyMember to search for
     * @param alreadyPassedMembers passed members, to detect cycles
     * @return true if the assembly contains this member, else false
     * 
     *         Queue<Assembly> queueToCheckAssemblies = new LinkedList<Assembly>();
     * @throws UnexpectedInputException if
     */
    boolean containsSubMemberRecursively(AssemblyMember toSearchFor, List<Assembly> alreadyPassedMembers)
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
                    alreadyPassedMembers.add(subAssembly);
                }
                if (subAssembly.containsSubMemberRecursively(toSearchFor, alreadyPassedMembers)) {
                    return true;
                }
                alreadyPassedMembers.remove(subAssembly);
            }
        }
        return false;
    }

    /**
     * searches for occurences of the subMember, does this recursively
     * 
     * @param toSearchFor the AssemblyMember to search for in the current Assembly
     * @return true if the assembly contains this member, else false
     * @throws UnexpectedInputException if
     */
    boolean containsSubMemberRecursively(AssemblyMember toSearchFor) throws UnexpectedInputException {
        List<Assembly> alreadyPassedMember = new LinkedList<Assembly>();
        return containsSubMemberRecursively(toSearchFor, alreadyPassedMember);
    }

    /**
     * searches for occurences of the subMember directly
     * 
     * @param subMember to check
     * @return true if the assembly contains this member, else false
     * @throws UnexpectedInputException if there is a subAssemblyMember that doesnt
     *                                  exist anymore
     */
    boolean containsSubMember(AssemblyMember subMember) throws UnexpectedInputException {
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
     * 
     * @param name of the assembly which should be returned
     * @return assembly member matching that string
     * @throws UnexpectedInputException if there is no such Element
     */
    AssemblyMember getSubAssemblyMemberRecursively(String name) throws UnexpectedInputException {
        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
            if (currentAssemblyMember.getName().equals(name)) {
                return currentAssemblyMember;
            }
        }
        throw new UnexpectedInputException("there is no element with that name: " + name);
    }

    /**
     * @param assembly subAssembly to count
     * @return count of the submember with this name
     * @throws UnexpectedInputException if the name does not correspond with any
     *                                  subassembly
     */
    int getCountOfSubAssemblyMember(Assembly assembly) throws UnexpectedInputException {
        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
            if (currentAssemblyMember.equals(assembly)) {
                return (int) subMemberEntry.getValue();
            }
        }
        return 0;
    }

    /**
     * @return corresponding element
     */
    Element toElement() {
        return new Element(getName());
    }

    /**
     * @param elementToRemove to remove
     * @throws UnexpectedInputException if this assembly member is not present
     */
    public void removeElementRecursively(Element elementToRemove) throws UnexpectedInputException {
        if (containsSubMember(elementToRemove)) {
            subMembers.remove(elementToRemove.getName());
        }
        if (containsSubMemberRecursively(elementToRemove)) {
            Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
            Iterator<Entry<String, Integer>> i = set.iterator();

            while (i.hasNext()) {
                Map.Entry<String, Integer> subMemberEntry = i.next();
                AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
                if (currentAssemblyMember.hasSubElements()) {
                    Assembly assembly = (Assembly) currentAssemblyMember;
                    if (assembly.containsSubMember(elementToRemove)) {
                        assembly.removeElementRecursively(elementToRemove);
                    }
                }
            }
        }

    }

    private void removeSubAssemblyMemberCompletely(AssemblyMember assemblyMember) throws UnexpectedInputException {
        subMembers.remove(assemblyMember.getName());
        AssemblyMember.removeAssemblyMemberFromKnownList(assemblyMember.getName());
        if (subMembers.isEmpty()) { // if the parent assembly is empty after removal, convert to element
            AssemblyMember.removeAssemblyMemberFromKnownList(getName());
            AssemblyMember.addAssemblyMemberToKnownList(new Element(getName()));
        }
        if (assemblyMember.hasSubElements()) { // if the removed assembly had child elements
            cleanUpAfterRemovalOfAssembly();
        }
    }

    /**
     * addAssembly X=2:A removePart X-1:A
     * 
     * @param assemblyMemberToRemove assemblyMemberToRemove by the amount
     * @param countToRemove          amount to remove the assemblyMember
     * @throws UnexpectedInputException if you cannot remove this amount
     */
    public void removeSubAssemblyPartially(AssemblyMember assemblyMemberToRemove, int countToRemove)
            throws UnexpectedInputException {
        int currentCountOfAssemblyMember = subMembers.get(assemblyMemberToRemove.getName());
        if (currentCountOfAssemblyMember == countToRemove) {
            removeSubAssemblyMemberCompletely(assemblyMemberToRemove);
        } else if (currentCountOfAssemblyMember > countToRemove) {
            subMembers.replace(assemblyMemberToRemove.getName(), currentCountOfAssemblyMember - countToRemove);
        } else {
            throw new UnexpectedInputException("You cannot remove " + countToRemove + " from only "
                    + currentCountOfAssemblyMember + " present subAssemblyMember(s)");
        }
    }

    private void cleanUpAfterRemovalOfAssembly() throws UnexpectedInputException {
        for (AssemblyMember currentAssemblyMember : AssemblyMember.getAlreadyUsedAssemblyMembers()) {
            if (!currentAssemblyMember.hasSubElements()) {
                // check if they are needed after removal of parent Assembly
                if (!Assembly.isElementUsedInAnotherAssembly(currentAssemblyMember)) {
                    AssemblyMember.removeAssemblyMemberFromKnownList(currentAssemblyMember.getName());
                }
            }
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
     * @param assemblyToReplace assembly to replace with an element
     * @throws UnexpectedInputException if this assembly member doesnt exist
     */
    public void replaceSubAssemblyWithElementRecursively(Assembly assemblyToReplace) throws UnexpectedInputException {
        AssemblyMember.removeAssemblyMemberFromKnownList(assemblyToReplace.getName());
        new Element(assemblyToReplace.getName());
        cleanUpAfterRemovalOfAssembly();
    }

    private boolean isLoopCreatedWhenAdding() throws UnexpectedInputException {
        // AssemblyMember.addAssemblyMemberToKnownList(assemblyToCheck);
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
