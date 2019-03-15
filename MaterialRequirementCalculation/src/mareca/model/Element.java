package mareca.model;

public class Element extends AssemblyMember {

    /**
     * @param name name of the element
     */
    public Element(String name) {
        super(false, name);
    }

    @Override
    public String toString() {
        return getName();
    }
    
    private Assembly getAssembly() {
        return new Assembly(getName());
    }
}
