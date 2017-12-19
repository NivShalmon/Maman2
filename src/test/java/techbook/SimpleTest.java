package techbook;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import techbook.business.ReturnValue;
import techbook.business.Student;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static techbook.Solution.addStudent;
import static techbook.business.ReturnValue.OK;

public class SimpleTest extends AbstractTest{



    @Test
    public void simpleTest()
    {
        Student student = new Student();
        student.setId(1);
        student.setName("student");
        student.setFaculty("CS");
        ReturnValue result = Solution.addStudent(student);
        assertEquals(OK, result);

        Student resultStudent = Solution.getStudentProfile(1);
        assertEquals(student, resultStudent);

    }
}
