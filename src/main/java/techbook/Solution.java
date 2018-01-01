package techbook;

import techbook.business.*;
import techbook.data.DBConnector;
import static techbook.data.PostgreSQLErrorCodes.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static techbook.business.ReturnValue.*;

public class Solution {


    public static void createTables() {
        try (Connection c = DBConnector.getConnection();
             PreparedStatement student = c.prepareStatement("CREATE TABLE Students\n" +
                     "(\n" +
                     "    id integer NOT NULL,\n" +
                     "    name text NOT NULL,\n" +
                     "    faculty text NOT NULL,\n" +
                     "    PRIMARY KEY (id)," +
                     "    CHECK (id > 0)\n" +
                     ")");
             PreparedStatement groups = c.prepareStatement("CREATE TABLE Groups\n" +
                     "(\n" +
                     "    name text NOT NULL,\n" +
                     "    studentId integer NOT NULL,\n" +
                     "    PRIMARY KEY (name)," +
                     "    FOREIGN KEY (studentId) REFERENCES Students(id)\n" +
                     ")");) {
            student.execute();
            groups.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void clearTables() {
        try (Connection c = DBConnector.getConnection();
             PreparedStatement student = c.prepareStatement("TRUNCATE TABLE Students CASCADE");
             PreparedStatement groups = c.prepareStatement("TRUNCATE TABLE Groups CASCADE")) {
            groups.execute();
            student.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void dropTables() {
        try (Connection c = DBConnector.getConnection();
             PreparedStatement student = c.prepareStatement("DROP TABLE Students CASCADE");
             PreparedStatement groups = c.prepareStatement("DROP TABLE Groups CASCADE")) {
            student.execute();
            groups.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static PreparedStatement addToFacultyStatement(Student student, Connection c) throws SQLException{
        return  c.prepareStatement("INSERT INTO group\n" +
                String.format("VALUES(\'%s\',%s)",student.getFaculty(),student.getId()));
    }

    /**
     * Adds a student to the database. The student should join to the faculty’s group
     * input: student to be added
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * BAD_PARAMS in case of illegal parameters
     * ALREADY_EXISTS if student already exists
     * ERROR in case of database error
     */
    public static ReturnValue addStudent(Student student) {
        try(Connection c = DBConnector.getConnection();
            PreparedStatement addStudent = c.prepareStatement("INSERT INTO Students\n" +
                    String.format ("VALUES(%d,\'%s\',\'%s\');",student.getId(),student.getName(),student.getFaculty()));
            PreparedStatement addToFaculty = addToFacultyStatement(student,c)){
            addStudent.execute();
            addToFaculty.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
            if (e.getErrorCode() == NOT_NULL_VIOLATION.getValue() || e.getErrorCode() == CHECK_VIOLATION.getValue())
                return BAD_PARAMS;
            return ERROR;
        }
        return OK;

    }


    /**
     * Deletes a student from the database
     * Deleting a student will cause him\her to leave their group, delete their posts and likes history, and friendships
     * input: student
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if student does not exist
     * ERROR in case of database error
     */
    public static ReturnValue deleteStudent(Integer studentId) {
        try(Connection c = DBConnector.getConnection();
            PreparedStatement s = c.prepareStatement("DELETE FROM Students\n" +
                    String.format ("WHERE id = %d;",studentId))){
            return s.executeUpdate() != 0 ? OK : NOT_EXISTS;
        } catch (SQLException e) {
            return ERROR;
        }
    }


    /**
     * Returns the student profile by the given id
     * input: student id
     * output: The student profile in case the student exists. BadStudent otherwise
     */
    public static Student getStudentProfile(Integer studentId) {
        try(Connection c = DBConnector.getConnection();
            PreparedStatement s = c.prepareStatement("SELECT * FROM Students\n" +
                    String.format ("WHERE id=%d;",studentId))){
            ResultSet rs = s.executeQuery();
            rs.next();
            Student std = new Student();
            std.setId(studentId);
            std.setName(rs.getString("name"));
            std.setFaculty(rs.getString("faculty"));
            return std;
        } catch (SQLException e) {
            return Student.badStudent();
        }
    }


    /**
     * Updates a student faculty to the new given value.
     * The student should join the group of the new faculty, and stay in the old faculty’s group.
     * input: updated student
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if student does not exist
     * BAD_PARAMS in case of illegal parameters
     * ERROR in case of database error
     */
    public static ReturnValue updateStudentFaculty(Student student) {
        try(Connection c = DBConnector.getConnection();
            PreparedStatement addToFaculty = addToFacultyStatement(student,c)){
            addToFaculty.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == NOT_NULL_VIOLATION.getValue() || e.getErrorCode() == FOREIGN_KEY_VIOLATION.getValue())
                return BAD_PARAMS;
            return ERROR;
        }
        return OK;
    }


    /**
     * Adds a post to the database, and adds it to the relevant group if  groupName is given (i.e., it is not null)
     * When a student can write a post in a group only if he\she is one of its members
     * input: post to be posted
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * BAD_PARAMS in case of illegal parameters
     * NOT_EXISTS if student is not a member in the group
     * ALREADY_EXISTS if post already exists
     * ERROR in case of database error
     */
    public static ReturnValue addPost(Post post, String groupName) {

        return null;
    }


    /**
     * Deletes a post from the database
     * input: post to be deleted
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if post does not exist
     * ERROR in case of database error
     */
    public static ReturnValue deletePost(Integer postId) {

        return null;
    }


    /**
     * returns the post by given id
     * input: post id
     * output: Post if the post exists. BadPost otherwise
     */
    public static Post getPost(Integer postId) {

        return null;
    }

    /**
     * Updates a post’s text
     * input: updated post
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if post does not exist
     * BAD_PARAMS in case of illegal parameters
     * ERROR in case of database error
     */
    public static ReturnValue updatePost(Post post) {

        return null;
    }


    /**
     * Establishes a friendship relationship between two different students
     * input: student id 1, student id 2
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if one or two of the students do not exist
     * ALREADY_EXISTS if the students are already friends
     * BAD_PARAMS in case of illegal parameters
     * ERROR in case of database error
     */

    public static ReturnValue makeAsFriends(Integer studentId1, Integer studentId2) {

        return null;
    }


    /**
     * Removes a friendship connection of two students
     * input: student id 1, student id 2
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if one or two of the students do not exist,  or they are not labeled as friends
     * ERROR in case of database error
     */
    public static ReturnValue makeAsNotFriends(Integer studentId1, Integer studentId2) {

        return null;

    }

    /**
     * Marks a post as liked by a student
     * input: student id, liked post id
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if student or post do not exist
     * ALREADY_EXISTS if the student is already likes the post
     * ERROR in case of database error
     */
    public static ReturnValue likePost(Integer studentId, Integer postId) {
        return null;

    }

    /**
     * Removes the like marking of a post by the student
     * input: student id, unliked post id
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if student or post do not exist,  or the student did not like the post
     * ERROR in case of database error
     */
    public static ReturnValue unlikePost(Integer studentId, Integer postId) {

        return null;
    }

    /**
     * Adds a student to a group
     * input: id of student to be added, the group name the student is added to
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if the student does not exist
     * ALREADY_EXISTS if the student are already in that group
     * ERROR in case of database error
     */
    public static ReturnValue joinGroup(Integer studentId, String groupName) {
        return null;
    }

    /**
     * Removes a student from a group
     * input: student id 1, student id 2
     * output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if the student is not a member of the group
     * ERROR in case of database error
     */
    public static ReturnValue leaveGroup(Integer studentId, String groupName) {

        return null;
    }


    /**
     * Gets a list of personal posts posted by a student and his\her friends. Feed should be ordered by date and likes, both in descending order.
     * input: student id
     * output: Feed the containing the relevant posts. In case of an error, return an empty feed
     */
    public static Feed getStudentFeed(Integer id) {
        return null;
    }

    /**
     * Gets a list of posts posted in a group. Feed should be ordered by date and likes, both in descending order.
     * input: group
     * output: Feed the containing the relevant posts. In case of an error, return an empty feed
     */

    public static Feed getGroupFeed(String groupName) {
        return null;
    }

    /**
     * Gets a list of students that the given student may know.
     * Denote the given the student by s. The returned list should consist of every student x in the database that holds the following:
     * - s ≠ x.
     * - s and x are not friends.
     * - There exists a student y such that y ≠ s, y ≠ x, s and y are friends, and y and x are friends.
     * - There exists a group such that both s and x are members of.
     * input: student
     * output: an ArrayList containing the students. In case of an error, return an empty ArrayList
     */

    public static ArrayList<Student> getPeopleYouMayKnowList(Integer studentId) {
        return null;
    }

    /**
     * Returns a list of student id pairs (s1, s2) such that the degrees of separation (definition follows)
     * between s1 and s2 is at least 5.
     * To define the notion of degrees of separation let us consider a graph, called the friendship graph,
     * where its nodes are the students in the database, and there is an edge between two students iff they are friends.
     * The degrees of separation between students s1 and s2 is defined as the length of the shortest path
     * connecting s1 and s2 in the undirected friendship graph.
     * input: none
     * output: an ArrayList containing the student pairs. In case of an error, return an empty ArrayList
     */
    public static ArrayList<StudentIdPair> getRemotelyConnectedPairs() {
        return null;
    }


}

