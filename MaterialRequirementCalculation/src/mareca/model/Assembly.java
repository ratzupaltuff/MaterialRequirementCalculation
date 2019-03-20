package mareca.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mareca.UnexpectedInputException;

public class Assembly extends AssemblyMember {

    /**
     * hashmap for saving submembers and their quantity
     */
    HashMap<AssemblyMember, Integer> subMembers = new HashMap<AssemblyMember, Integer>();

    /**
     * @param name name of assembly
     */
    public Assembly(String name) {
        super(true, name);
    }
    
    /**
     * @param name 
     * @param subAssemblys 
     */
    public Assembly(String name, AssemblyMemberCountTupel[] subAssemblys) {
        super(true, name);
        for (AssemblyMemberCountTupel assemblyMemberCountTupel : subAssemblys) {
            addSubMember(assemblyMemberCountTupel.getAssemblyMember(), assemblyMemberCountTupel.getCount());
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @param subMember to add
     * @param quantity  how often the submember occures in this assembly
     */
     void addSubMember(AssemblyMember subMember, int quantity) {
        subMembers.put(subMember, quantity);
    }

    /**
     * @param subMember to check
     * @return true if the assembly contains this member, else false
     */
    private boolean containsSubMember(AssemblyMember subMember) {
        Set set = subMembers.entrySet();
        Iterator i = set.iterator();
        
        while (i.hasNext()) {
           Map.Entry subMemberEntry = (Map.Entry) i.next();
           if (subMemberEntry.getKey().equals(subMember)) {
               return true;
           }
        }
        return false;
    }
    
    /**
     * @param name of the assembly which should be returned
     * @return assembly member matching that string
     * @throws UnexpectedInputException if there is no such Element
     */
    AssemblyMember getSubAssemblyMember(String name) throws UnexpectedInputException {
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
    
    private Element getÄquivalentElement() {
        return new Element(getName());
    }
    
    /**
     * @param assemblyMember to remove
     * @throws UnexpectedInputException if this assembly member is not present
     */
    void removeAssemblyMember(AssemblyMember assemblyMember) throws UnexpectedInputException {
        subMembers.remove(assemblyMember);
    }
    
    /**
     * @param assemblyMemberString to remove
     * @throws UnexpectedInputException if this assembly member is not present
     */
    void removeAssemblyMember(String assemblyMemberString) throws UnexpectedInputException {
        subMembers.remove(getSubAssemblyMember(assemblyMemberString));
    }
    
    
    
    /**
     * @return hashmap of this object
     */
    HashMap<AssemblyMember, Integer> getHashMap() {
        return subMembers;
    }
   

}
