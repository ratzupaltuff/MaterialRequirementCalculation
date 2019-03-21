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
     * @return newly created assembly
     * @throws UnexpectedInputException if there is an assembly in the list of child
     */
    public static Assembly createNewAssembly(String name, AssemblyMemberCountTupel[] subAssemblies)
            throws UnexpectedInputException {
        if (AssemblyMember.isAssemblyMemberInKnownList(name)) {
            AssemblyMember assemblyMemberToOverrideAssemblyMember = AssemblyMember.getAssemblyMemberFromKnownList(name);
            if (assemblyMemberToOverrideAssemblyMember.hasSubElements()) {
                throw new UnexpectedInputException("there is already an assembly with the name " + name);
            }
        }
        AssemblyMember assemblyMemberToAdd = getAssemblyMember(true, name);
        Assembly assemblyToAdd;
        if (assemblyMemberToAdd.hasSubElements()) {
            assemblyToAdd = (Assembly) assemblyMemberToAdd;
        } else {
            Element elementToConvert = (Element) assemblyMemberToAdd;
            assemblyToAdd = elementToConvert.toAssembly();
        }
        for (AssemblyMemberCountTupel assemblyMemberCountTupel : subAssemblies) {
            assemblyToAdd.addSubMember(assemblyMemberCountTupel.getAssemblyMember(),
                    assemblyMemberCountTupel.getCount());
        }
        return assemblyToAdd;
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
        List<String> strings = new ArrayList<String>();

        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            strings.add(subMemberEntry.getKey() + ":" + subMemberEntry.getValue().toString());
        }

        Collections.sort(strings);

        StringBuilder output = new StringBuilder(30);
        for (String s : strings) {
            output.append(s + ";");
        }
        return output.toString().substring(0, output.toString().length() - 1); // output and delete last semicolon
    }

    private List<AssemblyMemberCountTupel> subMembersToTupelList() throws UnexpectedInputException {
        List<AssemblyMemberCountTupel> assemblyMemberCountTupelList = new ArrayList<AssemblyMemberCountTupel>();

        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMemberCountTupel tupelToAdd = new AssemblyMemberCountTupel(
                    getAssemblyMember(subMemberEntry.getKey()), subMemberEntry.getValue());
            addSubMembersToTupelList(assemblyMemberCountTupelList, tupelToAdd);
        }
        return assemblyMemberCountTupelList;
    }

    /**
     * @param subMemberTupelList list to add the elements to
     * @return the list with added contents
     * @throws UnexpectedInputException 
     */
    List<AssemblyMemberCountTupel> subMembersToTupelListRecursively(List<AssemblyMemberCountTupel> subMemberTupelList)
            throws UnexpectedInputException {

        Set<Map.Entry<String, Integer>> set = subMembers.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<String, Integer> subMemberEntry = i.next();
            AssemblyMember subAssemblyMember = getAssemblyMember(subMemberEntry.getKey());
            AssemblyMemberCountTupel tupelToAdd = new AssemblyMemberCountTupel(subAssemblyMember,
                    subMemberEntry.getValue());
            addSubMembersToTupelList(subMemberTupelList, tupelToAdd);
            if (subAssemblyMember.hasSubElements()) {
                Assembly subAssembly = (Assembly) subAssemblyMember;
                subAssembly.subMembersToTupelListRecursively(subMemberTupelList);
            }
        }
        return subMemberTupelList;
    }

    private List<AssemblyMemberCountTupel> addSubMembersToTupelList(List<AssemblyMemberCountTupel> listToAddElement,
            AssemblyMemberCountTupel tupelToAdd) {
        // Iterator<AssemblyMemberCountTupel> iterator = new
        // Iterator<AssemblyMemberCountTupel>();
        for (AssemblyMemberCountTupel assemblyMemberCountTupel : listToAddElement) {
            if (assemblyMemberCountTupel.getAssemblyMember().equals(tupelToAdd.getAssemblyMember())) {
                assemblyMemberCountTupel.changeCount(tupelToAdd.getCount());
                return listToAddElement;
            }
        }
        listToAddElement.add(tupelToAdd);
        return listToAddElement;
    }

    /**
     * @return a String representation of all Assemblies in this Assembly
     * @throws UnexpectedInputException
     */
    public String getAssembliesString() throws UnexpectedInputException {

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
                    return tupel1.getAssemblyMember().getName().compareTo(tupel2.getAssemblyMember().getName());
                }
            }
        };
        List<AssemblyMemberCountTupel> subMemberTupelList = new ArrayList<AssemblyMemberCountTupel>();
        subMembersToTupelListRecursively(subMemberTupelList);

        Collections.sort(subMemberTupelList, assemblyComparator);

        StringBuilder output = new StringBuilder(30);
        for (AssemblyMemberCountTupel subMemberTupel : subMemberTupelList) {
            if (subMemberTupel.getAssemblyMember().hasSubElements()) {
                output.append(subMemberTupel.getAssemblyMember().getName() + ":" + subMemberTupel.getCount() + ";");
            }
        }
        return output.toString().substring(0, output.toString().length() - 1); // output and delete last semicolon
    }

    /**
     * @param subMember to add
     * @param quantity  how often the submember occures in this assembly
     * @throws UnexpectedInputException if there is already an assembly with this
     *                                  name
     */
    public void addSubMember(AssemblyMember subMember, int quantity) throws UnexpectedInputException {
        if (!containsSubMemberRecursively(subMember)) {
            subMembers.put(subMember.getName(), quantity);
        }
    }

    /**
     * searches for occurences of the subMember, does this recursively
     * 
     * @param subMember to check
     * @return true if the assembly contains this member, else false
     * @throws UnexpectedInputException 
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
     * searches for occurences of the subMember, does this recursively
     * 
     * @param subMember to check
     * @return true if the assembly contains this member, else false
     * @throws UnexpectedInputException 
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
    void removeElementRecursively(Element elementToRemove) throws UnexpectedInputException {
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
    void removeAssemblyMember(AssemblyMember assemblyMember) throws UnexpectedInputException {
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

}
