package mareca.model;

public class AssemblyCache extends Assembly {

    public AssemblyCache(String name) {
        super(name);
    }
    
    private void deleteAssemblyMember(String nameOfElementToDelete) {
        getSubAssemblyMember(nameOfElementToDelete);
    }

}
