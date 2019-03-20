package mareca.model;

import mareca.UnexpectedInputException;

public class AssemblyCache extends Assembly {

    /**
     * @param name name of assemblycache object
     */
    public AssemblyCache(String name) {
        super(name);
    }
    
    private void deleteAssemblyMember(String nameOfElementToDelete) throws UnexpectedInputException {
        getSubAssemblyMember(nameOfElementToDelete);
    }
    
    /**
     * @param nameOfElementToAdd 
     * @throws UnexpectedInputException 
     */
    public void addAssemblyMember(String nameOfElementToAdd) throws UnexpectedInputException {
        //super.addSubMember(subMember, quantity);
    }
    
    //private AssemblyMember getAssemblyMemberByInputString()

}
