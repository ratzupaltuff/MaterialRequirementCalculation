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
    HashMap<AssemblyMember, Integer> subMembers;

    /**
     * @param name name of assembly
     */
    Assembly(String name) {
        super(true, name);
        subMembers = new HashMap<AssemblyMember, Integer>();
    }

    /**
     * special method for assemblies without a name
     */
    Assembly() {
        super(true);
        subMembers = new HashMap<AssemblyMember, Integer>();
    }

    /**
     * @param name          of the assembly to add
     * @param subAssemblies of the assembly to add
     * @return newly created assembly
     */
    public static Assembly getAssembly(String name, AssemblyMemberCountTupel[] subAssemblies) {
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

        Set<Map.Entry<AssemblyMember, Integer>> set = subMembers.entrySet();
        Iterator<Entry<AssemblyMember, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<AssemblyMember, Integer> subMemberEntry = i.next();
            strings.add(subMemberEntry.getKey().getName() + ":" + subMemberEntry.getValue().toString());
        }

        Collections.sort(strings);

        StringBuilder output = new StringBuilder(30);
        for (String s : strings) {
            output.append(s + ";");
        }
        return output.toString().substring(0, output.toString().length() - 1); // output and delete last semicolon
    }

    private List<AssemblyMemberCountTupel> subMembersToTupelList() {
        List<AssemblyMemberCountTupel> assemblyMemberCountTupelList = new ArrayList<AssemblyMemberCountTupel>();

        Set<Map.Entry<AssemblyMember, Integer>> set = subMembers.entrySet();
        Iterator<Entry<AssemblyMember, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<AssemblyMember, Integer> subMemberEntry = i.next();
            AssemblyMemberCountTupel tupelToAdd = new AssemblyMemberCountTupel(subMemberEntry.getKey(),
                    subMemberEntry.getValue());
            assemblyMemberCountTupelList.add(tupelToAdd);
        }
        return assemblyMemberCountTupelList;
    }

    /**
     * @return a String representation of all Assemblies in this Assembly
     */
    public String getAssembliesString() {

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
        List<AssemblyMemberCountTupel> subMemberTupelList = subMembersToTupelList();
        Collections.sort(subMemberTupelList, assemblyComparator);

        StringBuilder output = new StringBuilder(30);
        for (AssemblyMemberCountTupel subMemberTupel : subMemberTupelList) {
            output.append(subMemberTupel.getAssemblyMember().getName() + ":" + subMemberTupel.getCount() + ";");
        }
        return output.toString().substring(0, output.toString().length() - 1); // output and delete last semicolon
    }

    /**
     * @param subMember to add
     * @param quantity  how often the submember occures in this assembly
     */
    public void addSubMember(AssemblyMember subMember, int quantity) {
        if (containsSubMemberRecursively(subMember)) {

        } else {
            subMembers.put(subMember, quantity);
        }
    }

    /**
     * searches for occurences of the subMember, does this recursively
     * 
     * @param subMember to check
     * @return true if the assembly contains this member, else false
     */
    boolean containsSubMemberRecursively(AssemblyMember subMember) {
        Set set = subMembers.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry subMemberEntry = (Map.Entry) i.next();
            AssemblyMember currentAssemblyMember = (AssemblyMember) subMemberEntry.getKey();
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
     */
    boolean containsSubMember(AssemblyMember subMember) {
        Set set = subMembers.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry subMemberEntry = (Map.Entry) i.next();
            AssemblyMember currentAssemblyMember = (AssemblyMember) subMemberEntry.getKey();
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
        Set set = subMembers.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry subMemberEntry = (Map.Entry) i.next();
            AssemblyMember currentAssemblyMember = (AssemblyMember) subMemberEntry.getKey();
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
    int getCountOfSubAssemblyMember(Assembly assembly) {
        Set set = subMembers.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry subMemberEntry = (Map.Entry) i.next();
            AssemblyMember currentAssemblyMember = (AssemblyMember) subMemberEntry.getKey();
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
    void removeElementRecursively(Element elementToRemove) {
        if (containsSubMember(elementToRemove)) {
            subMembers.remove(elementToRemove);
        }
        if (containsSubMemberRecursively(elementToRemove)) {
            Set<Map.Entry<AssemblyMember, Integer>> set = subMembers.entrySet();
            Iterator<Entry<AssemblyMember, Integer>> i = set.iterator();

            while (i.hasNext()) {
                Map.Entry<AssemblyMember, Integer> subMemberEntry = i.next();
                AssemblyMember currentAssemblyMember = subMemberEntry.getKey();
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
        Set<Map.Entry<AssemblyMember, Integer>> set = subMembers.entrySet();
        Iterator<Entry<AssemblyMember, Integer>> i = set.iterator();

        while (i.hasNext()) {
            Map.Entry<AssemblyMember, Integer> subMemberEntry = i.next();
            AssemblyMember currentAssemblyMember = subMemberEntry.getKey();
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
        Element elementToReplace = Element.getElement(assembly.getName());
        if (containsSubMemberRecursively(elementToReplace)) {
            Set<Map.Entry<AssemblyMember, Integer>> set = subMembers.entrySet();
            Iterator<Entry<AssemblyMember, Integer>> i = set.iterator();

            while (i.hasNext()) {
                Map.Entry<AssemblyMember, Integer> subMemberEntry = i.next();
                AssemblyMember currentAssemblyMember = subMemberEntry.getKey();
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

    /**
     * @return hashmap of this object
     */
    HashMap<AssemblyMember, Integer> getHashMap() {
        return subMembers;
    }

}
