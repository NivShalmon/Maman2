package techbook.business;

public class Student {

    Integer id = -1;
    String name = null;
    String faculty = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;

        Student student = (Student) o;

        if (getId() != null ? !getId().equals(student.getId()) : student.getId() != null) return false;
        if (getName() != null ? !getName().equals(student.getName()) : student.getName() != null) return false;
        return getFaculty() != null ? getFaculty().equals(student.getFaculty()) : student.getFaculty() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getFaculty() != null ? getFaculty().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Student{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", faculty='").append(faculty).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static Student badStudent()
    {
        return new Student();
    }
}
