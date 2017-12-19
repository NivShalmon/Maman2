package techbook.business;

import java.util.HashSet;
import java.util.Set;

public class StudentIdPair {

    Integer studentId1;
    Integer studentId2;
    Set<Integer> studentsIds = new HashSet<>();


    public Integer getStudentId1() {
        return studentId1;
    }

    public void setStudentId1(Integer studentId1) {
        if(this.studentId1==null)
        {
            this.studentId1 = studentId1;
            studentsIds.add(studentId1);
        }

    }

    public Integer getStudentId2() {
        return studentId2;
    }

    public void setStudentId2(Integer studentId2) {
        if(this.studentId2 ==null)
        {
            this.studentId2 = studentId2;
            studentsIds.add(studentId2);
        }

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentIdPair)) return false;

        StudentIdPair that = (StudentIdPair) o;

       return studentsIds.equals(that.studentsIds);
    }

    @Override
    public int hashCode() {
        int result = getStudentId1() != null ? getStudentId1().hashCode() : 0;
        result = 31 * result + (getStudentId2() != null ? getStudentId2().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StudentIdPair{");
        sb.append("studentId1=").append(studentId1);
        sb.append(", studentId2=").append(studentId2);
        sb.append('}');
        return sb.toString();
    }
}
