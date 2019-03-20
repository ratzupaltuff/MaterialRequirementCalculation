package mareca.model;

import mareca.UnexpectedInputException;

public class AssemblyCache extends Assembly {

    /**
     */
    public AssemblyCache() {
        super();
    }
    
    /**
     * @param subMember to add
     * @param quantity  how often the submember occures in this assembly
     */
    @Override
    public void addSubMember(AssemblyMember subMember, int quantity) {
        
        
        if (containsSubMemberRecursively(subMember)) {
            
        }
    }
    
    
    
    
}
