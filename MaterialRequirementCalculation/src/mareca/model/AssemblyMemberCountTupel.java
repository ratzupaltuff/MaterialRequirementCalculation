package mareca.model;

import java.util.Comparator;

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
    
    /**
     * @return a comparator which sorts assemblyMemberCountTupel by occurence and then by name
     */
    public static Comparator<AssemblyMemberCountTupel> getValueBeforeNameComparatorComparator() {
        Comparator<AssemblyMemberCountTupel> assemblyComparator = new Comparator<AssemblyMemberCountTupel>() {
            @Override
            public int compare(AssemblyMemberCountTupel tupel1, AssemblyMemberCountTupel tupel2) {
                if (tupel1.getCount() != tupel2.getCount()) {
                    if (tupel2.getCount() > tupel1.getCount()) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    return tupel1.getAssemblyMemberString().compareTo(tupel2.getAssemblyMemberString());
                }
            }
        };
        return assemblyComparator;
    }
    
    /**
     * @return a comparator which sorts assemblymemberCountTupels by name
     */
    public static Comparator<AssemblyMemberCountTupel> getNameComparatorComparator() {
        Comparator<AssemblyMemberCountTupel> assemblyComparator = new Comparator<AssemblyMemberCountTupel>() {
            @Override
            public int compare(AssemblyMemberCountTupel tupel1, AssemblyMemberCountTupel tupel2) {
                return tupel1.getAssemblyMemberString().compareTo(tupel2.getAssemblyMemberString());
            }
        };
        return assemblyComparator;
    }

}
