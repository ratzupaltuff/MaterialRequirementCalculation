package mareca.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
            assemblyToAdd.addSubMember(Element.getElement((assemblyMemberCountTupel.getAssemblyMemberString())),
                    assemblyMemberCountTupel.getCount());
        }
        addSubMember(assemblyToAdd, count);
        
        if (isLoopCreatedWhenAdding(assemblyToAdd)) {
            AssemblyMember.setAlreadyUsedAssemblyMembers(backupCopyAssemblyMembers);
            throw new UnexpectedInputException("if you add " + name + "you are creating a loop");
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

        Collections.sort(subMemberTupelList, getComparator());

        StringBuilder output = new StringBuilder(30);
        for (AssemblyMemberCountTupel subMemberTupel : subMemberTupelList) {
            output.append(subMemberTupel.getAssemblyMemberString() + ":" + subMemberTupel.getCount() + ";");
        }
        
        String outputString;
        if (output.toString().length() == 0) {
            outputString = "COMPONENT";
        } else {
            outputString = output.toString().substring(0, output.toString().length() - 1); // delete last semicolon
        }
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
            int factorOfOccurence) throws UnexpectedInputException {

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

    private Comparator<AssemblyMemberCountTupel> getComparator() {
        Comparator<AssemblyMemberCountTupel> assemblyComparator = new Comparator<AssemblyMemberCountTupel>() {
            /**
             * @param tupel1 tupel one to compare
             * @param tupel2 tupel two to compare
             * @return value <0 if tupel1 before tupel2, if they are the same 0, and >0 else
             */
            @Override
            public int compare(AssemblyMemberCountTupel tupel1, AssemblyMemberCountTupel tupel2) {

                if (tupel1.getCount() != tupel2.getCount()) {
                    return tupel2.getCount() - tupel1.getCount();
                } else {
                    return tupel1.getAssemblyMemberString().compareTo(tupel2.getAssemblyMemberString());
                }
            }
        };
        return assemblyComparator;
    }

    /**
     * @return a String representation of all Assemblies in this Assembly
     * @throws UnexpectedInputException if
     */
    public String getAssembliesString() throws UnexpectedInputException {

        Comparator<AssemblyMemberCountTupel> assemblyComparator = getComparator();

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

        Comparator<AssemblyMemberCountTupel> assemblyComparator = getComparator();

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
            subMembers.put(subMember.getName(), quantity);
        }
    }

    /**
     * searches for occurences of the subMember, does this recursively
     * 
     * @param subMember to check
     * @return true if the assembly contains this member, else false
     * @throws UnexpectedInputException if
     */
    boolean containsSubMemberRecursively(AssemblyMember subMember) throws UnexpectedInputException {
        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
            if (currentAssemblyMember.equals(subMember)) {
                return true;
            } else if (currentAssemblyMember.hasSubElements()) {
                Assembly subAssembly = (Assembly) currentAssemblyMember;
                if (subAssembly.containsSubMemberRecursively(subMember)) {
                    return true;
                }
            }
        }
        return false;
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

    /**
     * @param assemblyMember to remove
     * @throws UnexpectedInputException if this assembly member is not present
     */
    public void removeAssemblyMember(AssemblyMember assemblyMember) throws UnexpectedInputException {
        if (assemblyMember.hasSubElements()) {
            replaceSubAssemblyWithElementRecursively((Assembly) assemblyMember);
        } else {
            removeElementRecursively((Element) assemblyMember);
        }
    }

    /**
     * @param assemblyToReplace assembly to replace with an element
     * @throws UnexpectedInputException if this assembly member doesnt exist
     */
    public void replaceSubAssemblyWithElementRecursively(Assembly assemblyToReplace) throws UnexpectedInputException {
        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
            if (currentAssemblyMember.hasSubElements()) {
                Assembly assembly = (Assembly) currentAssemblyMember;
                if (assembly.equals(assemblyToReplace)) {
                    int count = subMemberEntry.getValue();
                    removeAssemblyMember(assembly);
                    addSubMember(assembly.toElement(), count);
                } else {
                    if (assembly.containsSubMemberRecursively(assemblyToReplace)) {
                        assembly.replaceSubAssemblyWithElementRecursively(assemblyToReplace);
                    }
                }
            }
        }
    }

    /**
     * @param assembly to replace the element with
     * @throws UnexpectedInputException if this element doesnt exist
     */
    public void replaceSubElementWithAssemblyRecursively(Assembly assembly) throws UnexpectedInputException {
        AssemblyMember assemblyMemberToReplace = Element.getElement(assembly.getName());
        if (assemblyMemberToReplace.hasSubElements()) {
            throw new UnexpectedInputException("AsseblyMember " + assembly.getName() + " is already a Assembly");
        }
        Element elementToReplace = (Element) assemblyMemberToReplace;
        if (containsSubMemberRecursively(elementToReplace)) {
            Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
            Iterator<Entry<String, Integer>> i = set.iterator();

            while (i.hasNext()) {
                Map.Entry<String, Integer> subMemberEntry = i.next();
                AssemblyMember currentAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
                if (currentAssemblyMember.equals(elementToReplace)) {
                    int count = subMemberEntry.getValue();
                    removeAssemblyMember(elementToReplace);
                    addSubMember(assembly.toElement(), count);
                } else if (currentAssemblyMember.hasSubElements()) {
                    Assembly subAssembly = (Assembly) currentAssemblyMember;
                    if (subAssembly.containsSubMemberRecursively(elementToReplace)) {
                        subAssembly.replaceSubElementWithAssemblyRecursively(assembly);
                    }
                }
            }
        }
    }

    private void detectLoop(String assemblyToAddString, AssemblyMemberCountTupel[] assemblyToAddChildTupel,
            Assembly parentAssembly) throws UnexpectedInputException {
        if (AssemblyMember.isAssemblyMemberInKnownList(assemblyToAddString)) {
            AssemblyMember assemblyMemberToAddAssemblyMember = AssemblyMember.getAssemblyMember(assemblyToAddString);
            if (assemblyMemberToAddAssemblyMember.hasSubElements()) {
                Assembly assemblyToAdd = (Assembly) assemblyMemberToAddAssemblyMember;
                if (assemblyToAdd.containsSubMemberRecursively(parentAssembly)) { // x-...-y-X!
                    throw new UnexpectedInputException("if you add " + assemblyToAdd.getName()
                            + "you will create a loop: " + assemblyToAdd.getName() + "-...-" + parentAssembly.getName()
                            + "-" + assemblyToAdd.getName());
                } else if (assemblyToAdd.containsSubMemberRecursively(parentAssembly)) { // ?-...-X!-...-?
                    throw new UnexpectedInputException("if you add " + assemblyToAdd.getName()
                            + "you will create a loop: " + assemblyToAdd.getName() + "-" + parentAssembly.getName()
                            + "-...-" + assemblyToAdd.getName());
                }
                // X!-...-?-...-x
                for (AssemblyMemberCountTupel assemblyMemberCountTupelToCheck : assemblyToAddChildTupel) {
                    AssemblyMember assemblyMember = AssemblyMember
                            .getAssemblyMember(assemblyMemberCountTupelToCheck.getAssemblyMemberString());
                    if (assemblyMember.hasSubElements()) {
                        Assembly assemblyToCheckAssembly = (Assembly) assemblyMember;
                        if (assemblyToCheckAssembly.containsSubMember(assemblyToAdd)) {
                            throw new UnexpectedInputException(
                                    "if you add " + assemblyToAdd.getName() + "you will create a loop: "
                                            + assemblyToAdd.getName() + "-...-" + assemblyToAdd.getName());
                        }
                    }
                }
            }
        }

    }

    private boolean isLoopCreatedWhenAdding(AssemblyMember assemblyToCheck) throws UnexpectedInputException {
        //AssemblyMember.addAssemblyMemberToKnownList(assemblyToCheck);
        List<AssemblyMember> listOfAllMembers = AssemblyMember.getAlreadyUsedAssemblyMembers();
        for (AssemblyMember assemblyMember : listOfAllMembers) {
            if (assemblyMember.hasSubElements()) {
                Assembly assemblyToCheckContains = (Assembly) assemblyMember;
                if (assemblyToCheckContains.containsSubMemberRecursively(assemblyToCheckContains)) {
                    //AssemblyMember.removeAssemblyMemberFromKnownList(assemblyToCheck.getName());
                    /*throw new UnexpectedInputException(
                            "if you add " + assemblyToCheck.getName() + " you will create a loop"
                                    + assemblyToCheckContains.getName() + "-...-" + assemblyToCheckContains.getName());
                */
                    return true;
                    }
            }
        }
        //AssemblyMember.removeAssemblyMemberFromKnownList(assemblyToCheck.getName());
        return false;
    }
}
