package mareca.model;

public abstract class AssemblyMember {
    private final boolean hasSubElements;
    private final String name;
    
    /**
     * @param hasSubElements whether it is element or assembly
     * @param name the name of the assembly member
     */
    public AssemblyMember(boolean hasSubElements, String name) {
        this.hasSubElements = hasSubElements;
        this.name = name;
    }
    
    /**
     * @return true if it has child-elements false if it is atomar
     */
    public boolean hasSubElements() {
        return hasSubElements;
    }
    
    /**
     * @return name of assembly member
     */
    public String getName() {
        return name;
    }
    
    /** 
     * @return string which identifies the contents of this assembly member
     */
    public String toString() {
        return name;
    }
    
    
    /**
     * @param assemblyMember object to compare with
     * @return true if they have the same name, else false
     */
    public boolean equals(Object assemblyMember) {
        if (assemblyMember instanceof AssemblyMember) {
            return name.equals(((AssemblyMember) assemblyMember).getName());
        }
        return false;
    }
}
