package techbook;

import techbook.business.*;
import techbook.data.DBConnector;
import techbook.data.PostgreSQLErrorCodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static techbook.business.ReturnValue.*;

public class Solution {


    public static void createTables()
    {
    
    }


    public static void clearTables()
    {
   
    }

    

    public static void dropTables()
    {
      
    }

    

    /**
     Adds a student to the database. The student should join to the faculty’s group
     input: student to be added
     output: ReturnValue with the following conditions:
     * OK in case of success
     * BAD_PARAMS in case of illegal parameters
     * ALREADY_EXISTS if student already exists
     * ERROR in case of database error
     */
    public static ReturnValue addStudent(Student student)
    {
        
        return null;

    }


    /**
     Deletes a student from the database
     Deleting a student will cause him\her to leave their group, delete their posts and likes history, and friendships
     input: student
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if student does not exist
     * ERROR in case of database error

     */
    public static ReturnValue deleteStudent(Integer studentId)
    {
        
        return null;
    }


    /**
     *
     Returns the student profile by the given id
     input: student id
     output: The student profile in case the student exists. BadStudent otherwise

     */

    public static Student getStudentProfile(Integer studentId)
    {
        
        return null;
    }




    /**
     Updates a student faculty to the new given value.
     The student should join the group of the new faculty, and stay in the old faculty’s group.
     input: updated student
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if student does not exist
     * BAD_PARAMS in case of illegal parameters
     * ERROR in case of database error
     */
    public static ReturnValue updateStudentFaculty(Student student){

        return null;


    }


    /**
     Adds a post to the database, and adds it to the relevant group if  groupName is given (i.e., it is not null)
     When a student can write a post in a group only if he\she is one of its members
     input: post to be posted
     output: ReturnValue with the following conditions:
     * OK in case of success
     * BAD_PARAMS in case of illegal parameters
     * NOT_EXISTS if student is not a member in the group
     * ALREADY_EXISTS if post already exists
     * ERROR in case of database error


     */
    public static ReturnValue addPost(Post post, String groupName)
    {

        return null;
    }


    /**
     Deletes a post from the database
     input: post to be deleted
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if post does not exist
     * ERROR in case of database error

     */
    public static ReturnValue deletePost(Integer postId)
    {
        
        return null;
    }


    /**
     *
     returns the post by given id
     input: post id
     output: Post if the post exists. BadPost otherwise

     */
    public static Post getPost(Integer postId)
    {
        
        return null;
    }

    /**
     Updates a post’s text
     input: updated post
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if post does not exist
     * BAD_PARAMS in case of illegal parameters
     * ERROR in case of database error


     */
    public static ReturnValue updatePost(Post post)
    {
       
        return null;
    }


    /**
     Establishes a friendship relationship between two different students
     input: student id 1, student id 2
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if one or two of the students do not exist
     * ALREADY_EXISTS if the students are already friends
     * BAD_PARAMS in case of illegal parameters
     * ERROR in case of database error

     */

    public static ReturnValue makeAsFriends(Integer studentId1, Integer studentId2)
    {

        return null;
    }


    /**
     Removes a friendship connection of two students
     input: student id 1, student id 2
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if one or two of the students do not exist,  or they are not labeled as friends
     * ERROR in case of database error

     */
    public static ReturnValue makeAsNotFriends  (Integer studentId1, Integer studentId2)
    {

        return null;

    }

    /**
     Marks a post as liked by a student
     input: student id, liked post id
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if student or post do not exist
     *ALREADY_EXISTS if the student is already likes the post
     * ERROR in case of database error

     */
    public static ReturnValue likePost(Integer studentId, Integer postId)
    {
        return null;

    }

    /**
     Removes the like marking of a post by the student
     input: student id, unliked post id
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if student or post do not exist,  or the student did not like the post
     * ERROR in case of database error

     */
    public static ReturnValue unlikePost(Integer studentId, Integer postId)
    {

        return null;
    }

    /**
     *
     Adds a student to a group
     input: id of student to be added, the group name the student is added to
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if the student does not exist
     * ALREADY_EXISTS if the student are already in that group
     * ERROR in case of database error
     */
    public static ReturnValue joinGroup(Integer studentId, String groupName)
    {
        return null;
    }

    /**
     *
     Removes a student from a group
     input: student id 1, student id 2
     output: ReturnValue with the following conditions:
     * OK in case of success
     * NOT_EXISTS if the student is not a member of the group
     * ERROR in case of database error
     */
    public static ReturnValue leaveGroup(Integer studentId,String groupName)
    {
       
        return null;
    }


    /**
     *
     Gets a list of personal posts posted by a student and his\her friends. Feed should be ordered by date and likes, both in descending order.
     input: student id
     output: Feed the containing the relevant posts. In case of an error, return an empty feed

     */
    public static Feed getStudentFeed(Integer id)
    {
        return null;
    }

    /**
     *
     Gets a list of posts posted in a group. Feed should be ordered by date and likes, both in descending order.
     input: group
     output: Feed the containing the relevant posts. In case of an error, return an empty feed
     */

    public static Feed getGroupFeed(String groupName)
    {
        return null;
    }

    /**
     Gets a list of students that the given student may know.
     Denote the given the student by s. The returned list should consist of every student x in the database that holds the following:
     - s ≠ x.
     - s and x are not friends.
     - There exists a student y such that y ≠ s, y ≠ x, s and y are friends, and y and x are friends.
     - There exists a group such that both s and x are members of.
     input: student
     output: an ArrayList containing the students. In case of an error, return an empty ArrayList

     */

    public static ArrayList<Student> getPeopleYouMayKnowList(Integer studentId)
    {
        return null;
    }

    /**
     Returns a list of student id pairs (s1, s2) such that the degrees of separation (definition follows)
     between s1 and s2 is at least 5.
     To define the notion of degrees of separation let us consider a graph, called the friendship graph,
     where its nodes are the students in the database, and there is an edge between two students iff they are friends.
     The degrees of separation between students s1 and s2 is defined as the length of the shortest path
     connecting s1 and s2 in the undirected friendship graph.
     input: none
     output: an ArrayList containing the student pairs. In case of an error, return an empty ArrayList


     */
    public static ArrayList<StudentIdPair> getRemotelyConnectedPairs()
    {
        return null;
    }


 }

