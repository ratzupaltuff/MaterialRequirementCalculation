package mareca.model;

public class AssemblyMemberCountTupel {
    private String assemblyMember;
    private long count;

    /**
     * @param assemblyMember assembly member to initialize
     * @param count count of this assemblymember
     */
    public AssemblyMemberCountTupel(String assemblyMember, long count) {
        this.assemblyMember = assemblyMember;
        this.count = count;
    }

    /**
     * @return the assembly member
     */
    public String getAssemblyMemberString() {
        return assemblyMember;
    }

    /**
     * @return count of the assembly member
     */
    public long getCount() {
        return count;
    }

    /**
     * @return count as an integer, do not use if value of count is over
     *         int.maxValue
     */
    public int getIntCount() {
        return (int) count;
    }

    /**
     * @param changeByValue value to add to the count of the assemblyMember of this tupel
     */
    public void changeCount(long changeByValue) {
        count = count + changeByValue;
    }

}
