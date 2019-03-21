package mareca.model;

public class AssemblyMemberCountTupel {
    private AssemblyMember assemblyMember;
    private int count;

    /**
     * @param assemblyMember 
     * @param count 
     */
    public AssemblyMemberCountTupel(AssemblyMember assemblyMember, int count) {
        this.assemblyMember = assemblyMember;
        this.count = count;
    }
    
    /**
     * @return the assembly member
     */
    public AssemblyMember getAssemblyMember() {
        return assemblyMember;
    }
    
    /**
     * @return count of the assembly member
     */
    public int getCount() {
        return count;
    }
    
    /**
     * @param changeByValue 
     */
    public void changeCount(int changeByValue) {
        count = count + changeByValue;
    }

}
