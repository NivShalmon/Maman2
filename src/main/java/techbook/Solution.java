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

    private static int getSQLState(SQLException e) {
        try {
            return Integer.parseInt(e.getSQLState());
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

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
                     "    FOREIGN KEY (studentId) REFERENCES Students(id),\n" +
                     "    PRIMARY KEY (name,studentId)\n" +
                     ")");
             PreparedStatement friends = c.prepareStatement("CREATE TABLE Friends\n" +
                     "(\n" +
                     "    id1 integer NOT NULL,\n" +
                     "    id2 integer NOT NULL,\n" +
                     "    FOREIGN KEY (id1) REFERENCES Students(id),\n" +
                     "    FOREIGN KEY (id2) REFERENCES Students(id),\n" +
                     "    CHECK (id1 != id2)," +
                     "    UNIQUE(id1,id2)\n" +
                     ")");
             PreparedStatement posts = c.prepareStatement("CREATE TABLE posts\n" +
                     "(\n" +
                     "    id integer,\n" +
                     "    author integer,\n" +
                     "    text text NOT NULL,\n" +
                     "    date TIMESTAMP NOT NULL,\n" +
                     "    PRIMARY KEY (id),\n" +
                     "    CHECK (id > 0),\n" +
                     "    FOREIGN KEY (author) REFERENCES Students(id)\n" +
                     ")");
             PreparedStatement postedInGroup = c.prepareStatement("CREATE TABLE postedInGroup\n" +
                     "(\n" +
                     "    postId integer,\n" +
                     "    groupName text,\n" +
                     "    studentId integer,\n" +
                     "    FOREIGN KEY (postId) REFERENCES posts(id),\n" +
                     "    FOREIGN KEY (groupName,studentId) REFERENCES Groups(name,studentId)\n" +
                     ")")) {
            student.execute();
            groups.execute();
            friends.execute();
            posts.execute();
            postedInGroup.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static PreparedStatement truncate(String table, Connection c) throws SQLException {
        return c.prepareStatement(String.format("TRUNCATE TABLE %s CASCADE", table));
    }

    public static void clearTables() {
        try (Connection c = DBConnector.getConnection();
             PreparedStatement student = truncate("Students", c);
             PreparedStatement groups = truncate("Groups", c);
             PreparedStatement friends = truncate("Friends", c);
             PreparedStatement posts = truncate("posts", c);
             PreparedStatement postedInGroup = truncate("postedInGroup", c)) {
            groups.execute();
            student.execute();
            friends.execute();
            posts.execute();
            postedInGroup.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static PreparedStatement drop(String table, Connection c) throws SQLException {
        return c.prepareStatement(String.format("DROP TABLE %s CASCADE", table));
    }

    public static void dropTables() {
        try (Connection c = DBConnector.getConnection();
             PreparedStatement student = drop("Students", c);
             PreparedStatement groups = drop("Groups", c);
             PreparedStatement friends = drop("Friends", c);
             PreparedStatement posts = drop("posts", c);
             PreparedStatement postedInGroup = drop("postedInGroup", c)) {
            groups.execute();
            student.execute();
            friends.execute();
            posts.execute();
            postedInGroup.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String makeStringForSQL(String s) {
        return s == null ? "NULL" : "\'" + s + "\'";
    }

    private static PreparedStatement addToGroup(int id, String group, Connection c) throws SQLException {
        return c.prepareStatement("INSERT INTO Groups\n" +
                String.format("VALUES(%s,%d)", makeStringForSQL(group), id));
    }

    private static PreparedStatement addStudentStatement(Student student, Connection c) throws SQLException {
        return c.prepareStatement("INSERT INTO Students\n" +
                String.format("VALUES(%d,%s,%s);", student.getId(),
                        makeStringForSQL(student.getName()), makeStringForSQL(student.getFaculty())));
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
        try (Connection c = DBConnector.getConnection();
             PreparedStatement addStudent = addStudentStatement(student, c);
             PreparedStatement addToFaculty = addToGroup(student.getId(), student.getFaculty(), c)) {
            addStudent.execute();
            addToFaculty.execute();
        } catch (SQLException e) {
            int sqlState = getSQLState(e);
            if (sqlState == UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
            if (sqlState == NOT_NULL_VIOLATION.getValue() || sqlState == CHECK_VIOLATION.getValue())
                return BAD_PARAMS;
            e.printStackTrace();
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
        try (Connection c = DBConnector.getConnection();
             PreparedStatement deleteFromGroups = c.prepareStatement("DELETE FROM Groups\n" +
                     String.format("WHERE studentId = %d", studentId));
             PreparedStatement deleteStudent = c.prepareStatement("DELETE FROM Students\n" +
                     String.format("WHERE id = %d;", studentId))) {
            deleteFromGroups.execute();
            return deleteStudent.executeUpdate() != 0 ? OK : NOT_EXISTS;
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
        try (Connection c = DBConnector.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT * FROM Students\n" +
                     String.format("WHERE id=%d;", studentId))) {
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
        try (Connection c = DBConnector.getConnection();
             PreparedStatement addToFaculty = addToGroup(student.getId(), student.getFaculty(), c)) {
            addToFaculty.execute();
        } catch (SQLException e) {
            int sqlState = getSQLState(e);
            if (sqlState == FOREIGN_KEY_VIOLATION.getValue())
                return NOT_EXISTS;
            if (sqlState == NOT_NULL_VIOLATION.getValue())
                return BAD_PARAMS;
            if (sqlState == UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
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
        try (Connection c = DBConnector.getConnection();
             PreparedStatement addPost = c.prepareStatement("INSERT INTO posts\n" +
                     String.format("VALUES (%d,%d,%s,%s)", post.getId(), post.getAuthor(),
                             makeStringForSQL(post.getText()),
                             makeStringForSQL(post.getTimeStamp() == null ? null : post.getTimeStamp().toString())));
             PreparedStatement postToGroup = c.prepareStatement("INSERT INTO postedInGroup\n" +
                     String.format("VALUES (%d,%s,%d)",post.getId(),makeStringForSQL(groupName),post.getAuthor()));
             PreparedStatement isInGroup = c.prepareStatement("SELECT COUNT(studentId)\n" +
                     "FROM Groups\n" +
                     String.format("WHERE studentId = %d AND name=%s",post.getAuthor(),makeStringForSQL(groupName)))) {
            if (groupName != null){//we need to make sure user is a member of the group
                ResultSet rs = isInGroup.executeQuery();
                rs.next();
                if(rs.getInt(1) == 0){
                    return NOT_EXISTS;
                }
            }
            addPost.execute();
            postToGroup.execute();
        } catch (SQLException e) {
            int sqlState = getSQLState(e);
            if (sqlState == CHECK_VIOLATION.getValue() || sqlState == NOT_NULL_VIOLATION.getValue())
                return BAD_PARAMS;
            if (sqlState == FOREIGN_KEY_VIOLATION.getValue())
                return NOT_EXISTS;
            if (sqlState == UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
            e.printStackTrace();
            return ERROR;
        }
        return OK;
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
        try (Connection c = DBConnector.getConnection();) {

        } catch (SQLException e) {
            e.printStackTrace();
            return ERROR;
        }
        return OK;
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

    private static int max(int i1, int i2) {
        return i1 > i2 ? i1 : i2;
    }

    private static int min(int i1, int i2) {
        return i1 < i2 ? i1 : i2;
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
        try (Connection c = DBConnector.getConnection();
             PreparedStatement s = c.prepareStatement("INSERT INTO Friendship\n" +
                     String.format("VALUES (%d,%d)"), max(studentId1, studentId2), min(studentId1, studentId2))) {
            s.execute();
        } catch (SQLException e) {
            int sqlState = getSQLState(e);
            if (sqlState == FOREIGN_KEY_VIOLATION.getValue())
                return NOT_EXISTS;
            if (sqlState == UNIQUE_VIOLATION.getValue())
                return ALREADY_EXISTS;
            if (sqlState == CHECK_VIOLATION.getValue())
                return BAD_PARAMS;
            return ERROR;
        }
        return OK;
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

