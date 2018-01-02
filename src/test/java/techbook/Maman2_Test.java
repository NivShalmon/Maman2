package techbook;

import org.junit.Ignore;
import org.junit.Test;
import techbook.business.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static techbook.business.ReturnValue.*;
import static techbook.business.Student.badStudent;

public class Maman2_Test extends AbstractTest{

    private static Student buildStudent(int id, String name, String faculty) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setFaculty(faculty);
        return student;
    }

    private static Post buildPost(int id, int author, int likes, String text) {
        return buildPost(id, author, likes, text, LocalDateTime.now());
    }
    private static Post buildPost(int id, int author, int likes, String text, LocalDateTime date) {
        Post post = new Post();
        post.setId(id);
        post.setAuthor(author);
        post.setDate(date);
        post.setLikes(likes);
        post.setText(text);
        return post;
    }

    //in case DB allready existss, just drops it
    public void initiateForTest(){
        Solution.dropTables();
    }

    @Test
    //checking database creation, data insert and minor updates, all should succeed
    public void generalSuccessTest()
    {
        Student[] students = new Student[10];
        Student s = null;
        ReturnValue res;
        String faculty = "";
        //create 10 students - check add students
        for(int i=1; i<=10; i++){
            switch(i%3){
                case 0: faculty = "CS"; break;
                case 1: faculty = "MATH"; break;
                case 2: faculty = "PHYSICS"; break;
            }
            s = buildStudent(i, String.valueOf(i), faculty);
            students[i-1] = s;
            res = Solution.addStudent(s);
            assertEquals(res, OK);
        }

        //check get profile
        for(int i=1; i<=10; i++){
            s = Solution.getStudentProfile(i);
            assertEquals(s, students[i-1]);
        }

        //check deleteStudent
        res = Solution.deleteStudent(3);
        assertEquals(res, OK);
        res = Solution.deleteStudent(5);
        assertEquals(res, OK);

        //add student back
        Solution.addStudent(buildStudent(3, "3", "CS"));
        Solution.addStudent(buildStudent(5, "5", "PHYSICS"));

        //check updateFaculty
        s = buildStudent(2,"2","Maasia&Tiul");
        res = Solution.updateStudentFaculty(s);
        assertEquals(res, OK);

        Post post = null;
        Post[] posts = new Post[20];
        //check add public post
        for(int i=1; i<=10; i++){
            post = buildPost(i, i, 0,"Im student " + i, LocalDateTime.now());
            res = Solution.addPost(post,null);
            assertEquals(res, OK);
            posts[i-1] = post;
        }

        //check add group post
        for(int i=11; i<=20; i++){
            switch((i-10)%3) {
                case 0:
                    faculty = "CS";
                    break;
                case 1:
                    faculty = "MATH";
                    break;
                case 2:
                    faculty = "PHYSICS";
                    break;
            }
            post = buildPost(i, i-10, 0,"Im student " + (i-10) + " in group",
                    LocalDateTime.now());
            res = Solution.addPost(post, faculty);
            assertEquals(res, OK);
            posts[i-1] = post;
        }

        //check getPost
        for(int i=1; i<=20; i++){
            //System.out.println(i);
            post = Solution.getPost(i);
            assertEquals(post, posts[i-1]);
        }

        //check update Post
        for(int i=1; i<=20; i+=2){
            posts[i-1].setText("post " + i + " has been updated!");
            res = Solution.updatePost(posts[i-1]);
            assertEquals(res, OK);
        }

        //check delete Post
        for(int i=1; i<=20; i+=6){
            res = Solution.deletePost(i);
            assertEquals(res, OK);
        }

        //check like post
        for(int i=1; i<=10; i+=4){
            res = Solution.likePost(i,(i+1)%20);
            assertEquals(res, OK);
        }

        //check unlike post
        for(int i=1; i<=10; i+=8){
            res = Solution.unlikePost(i,(i+1)%20);
            assertEquals(res, OK);
        }

        //check makeAsFriend
        for(int i=1; i<10; i+=2){
            res = Solution.makeAsFriends(i,i+1);
            assertEquals(res, OK);
        }

        //check makeAsNotFriend
        for(int i=1; i<10; i+=4){
            res = Solution.makeAsNotFriends(i,i+1);
            assertEquals(res, OK);
        }

        //check joinGroup - existing group
        res = Solution.joinGroup(3, "MATH");
        assertEquals(res, OK);
        res = Solution.joinGroup(6, "MATH");
        assertEquals(res, OK);

        //check joinGroup - new group
        res = Solution.joinGroup(1, "Students In Technion");
        assertEquals(res, OK);
        res = Solution.joinGroup(2, "Free Food Technion");
        assertEquals(res, OK);

        //check get Student Feed
        Solution.addPost(buildPost(100,3,0,"postpostpost", LocalDateTime.now()), null);
        Solution.addPost(buildPost(101,4,0,"12345678", LocalDateTime.now()), null);
        Solution.addPost(buildPost(102,4,0,"lalalalala", LocalDateTime.now()), null);
        Feed res_feed = Solution.getStudentFeed(3);
        int feed_size = res_feed.size();
        assertNotEquals(0,feed_size);

        //check the feeds order
        int[] likes_num = new int[feed_size];
        LocalDateTime[] dates = new LocalDateTime[feed_size];
        for(int i=0; i<feed_size; i++){
            Post curr_post = res_feed.get(i);
            likes_num[i] = curr_post.getLikes();
            dates[i] = curr_post.getDate();
            assertTrue(curr_post.getAuthor() == 3 || curr_post.getAuthor() == 4);
        }

        for(int i=0; i<feed_size-1; i++){
            assertTrue(dates[i].isAfter(dates[i+1]) ||
                    (dates[i].isEqual(dates[i+1]) && likes_num[i] >= likes_num[i+1]));
        }

        //check get Student Feed
        Solution.addPost(buildPost(100,3,0,"postpostpost", LocalDateTime.now()), null);
        Solution.addPost(buildPost(101,4,0,"12345678", LocalDateTime.now()), null);
        Solution.addPost(buildPost(102,4,0,"lalalalala", LocalDateTime.now()), null);
        Feed res_feed2 = Solution.getGroupFeed("CS");
        int feed_size2 = res_feed2.size();
        assertNotEquals(0,feed_size2);

        //check the feeds order
        int[] likes_num2 = new int[feed_size2];
        LocalDateTime[] dates2 = new LocalDateTime[feed_size2];
        for(int i=0; i<feed_size2; i++){
            Post curr_post = res_feed2.get(i);
            likes_num2[i] = curr_post.getLikes();
            dates2[i] = curr_post.getDate();
        }

        for(int i=0; i<feed_size2-1; i++){
            assertTrue(dates2[i].isAfter(dates2[i+1]) ||
                    (dates2[i].isEqual(dates2[i+1]) && likes_num2[i] >= likes_num2[i+1]));
        }

        //check getPeople you may know
        Solution.makeAsFriends(3,8);
        Solution.makeAsFriends(4,9);
        ArrayList<Student> mayKnow_3 = Solution.getPeopleYouMayKnowList(3);
        ArrayList<Student> mayKnow_4 = Solution.getPeopleYouMayKnowList(4);

        assertEquals(mayKnow_3.size(),2);
        assertEquals(mayKnow_4.size(),0);

        Solution.makeAsNotFriends(3,8);
        Solution.makeAsNotFriends(4,9);
        Solution.makeAsNotFriends(7,8);

        //friendship at this point (3,4)
        //check get remotely connected pairs
        Solution.makeAsFriends(2,1);
        Solution.makeAsFriends(2,3);
        Solution.makeAsFriends(5,4);
        Solution.makeAsFriends(5,3);
        Solution.makeAsFriends(1,6);
        Solution.makeAsFriends(7,6);
        Solution.makeAsFriends(8,9);

        ArrayList<StudentIdPair> pairs = Solution.getRemotelyConnectedPairs();
        assertEquals(pairs.size(), 2);
        StudentIdPair pair1 = pairs.get(0);
        StudentIdPair pair2 = pairs.get(1);
        assertTrue((pair1.getStudentId1() == 4 && pair1.getStudentId2() == 7) ||
                        (pair1.getStudentId1() == 5 && pair1.getStudentId2() == 7) ||
                        (pair1.getStudentId1() == 7 && pair1.getStudentId2() == 4) ||
                        (pair1.getStudentId1() == 7 && pair1.getStudentId2() == 5));
        assertTrue((pair2.getStudentId1() == 4 && pair2.getStudentId2() == 7) ||
                (pair2.getStudentId1() == 5 && pair2.getStudentId2() == 7) ||
                (pair2.getStudentId1() == 7 && pair2.getStudentId2() == 4) ||
                (pair2.getStudentId1() == 7 && pair2.getStudentId2() == 5));


    }

    @Test
    //check return values
    public void returnValuesTest(){
        //background data
        Student s = null;
        Student[] students = new Student[10];
        Post[] posts = new Post[20];
        Post p = null;
        for(int i = 1; i <= 5; i++){
            students[i-1] = buildStudent(i,String.valueOf(i),"CS");
        }
        for(int i = 6; i <= 10; i++){
            students[i-1] = buildStudent(i,String.valueOf(i),"BIO");
        }
        for(int i = 1; i <= 10; i++){
            posts[i-1] = buildPost(i,i,0,"This is the post of " + i, LocalDateTime.now());
        }
        for(int i = 11; i <= 20; i++){
            posts[i-1] = buildPost(i,i-10,0,"This is the post of " + i, LocalDateTime.now());
        }
        ReturnValue res;
        //check addStudent
        //student is null
//        res = Solution.addStudent(null);
//        assertEquals(res, BAD_PARAMS);
        //student already exists
        res = Solution.addStudent(students[0]);
        assertEquals(res, OK);
        res = Solution.addStudent(students[0]);
        assertEquals(res, ALREADY_EXISTS);
        //database vaiolation
        s = students[1];
        s.setFaculty(null);
        res = Solution.addStudent(s);
        assertEquals(res, BAD_PARAMS);
        s.setFaculty("CS");
        s.setName(null);
        res = Solution.addStudent(s);
        assertEquals(res, BAD_PARAMS);
        s.setName("2");
        s.setId(-1);
        res = Solution.addStudent(s);
        assertEquals(res, BAD_PARAMS);
        s.setId(0);
        res = Solution.addStudent(s);
        assertEquals(res, BAD_PARAMS);

        //check get profile
        s = Solution.getStudentProfile(7);
        assertEquals(s, badStudent());
        s = Solution.getStudentProfile(-1);
        assertEquals(s, badStudent());

        //check deleteStudent
        //student doesn't exists
        res =Solution.deleteStudent(7);
        assertEquals(res, NOT_EXISTS);
        res =Solution.deleteStudent(-1);
        assertEquals(res, NOT_EXISTS);

        //check update faculty
        //student doesn't exists
        Solution.addStudent(students[6]);
        Solution.deleteStudent(7);
        s = students[6];
        res = Solution.updateStudentFaculty(s);
        assertEquals(res, NOT_EXISTS);
        s.setId(-1);
        res = Solution.updateStudentFaculty(s);
        assertEquals(res, NOT_EXISTS);
        //student already in the given faculty
        s = students[0];
        res = Solution.updateStudentFaculty(s);
        assertEquals(res, ALREADY_EXISTS);
        //null faculty
        s = students[0];
        s.setFaculty(null);
        res = Solution.updateStudentFaculty(s);
        assertEquals(res, BAD_PARAMS);

        //check add post
        Solution.addStudent(students[8]);
        //student not a member in the group
        res = Solution.addPost(posts[0], "BIO");
        assertEquals(res, NOT_EXISTS);
        //student doesn't exists at all
        res = Solution.addPost(posts[4], "CS");
        assertEquals(res, NOT_EXISTS);
        //post id already exists
        Solution.addPost(posts[0], null);
        Solution.addPost(posts[10], "CS");
        res = Solution.addPost(posts[0], "CS");
        assertEquals(res, ALREADY_EXISTS);
        res = Solution.addPost(posts[10], null);
        assertEquals(res, ALREADY_EXISTS);
        //invalid post id
        p = posts[0];
        p.setId(-1);
        res = Solution.addPost(p, null);
        assertEquals(res, BAD_PARAMS);
        p.setId(0);
        res = Solution.addPost(p, null);
        assertEquals(res, BAD_PARAMS);
        //text and date are null
        p.setId(100);
        p.setDate(null);
        res = Solution.addPost(p, null);
        assertEquals(res, BAD_PARAMS);
        p.setId(1);
        p.setDate(LocalDateTime.now());
        p.setText(null);
        res = Solution.addPost(p, null);
        assertEquals(res, BAD_PARAMS);

        //check get post
        //post does'nt exists
        p = Solution.getPost(0);
        assertEquals(p, Post.badPost());
        p = Solution.getPost(100);
        assertEquals(p, Post.badPost());

        //check delete post
        //post does'nt exists
        res = Solution.deletePost(0);
        assertEquals(res, NOT_EXISTS);
        res = Solution.deletePost(100);
        assertEquals(res, NOT_EXISTS);

        //check update post
        //post doesn't exists
        p = posts[3];
        res = Solution.updatePost(p);
        assertEquals(res, NOT_EXISTS);
        //text is null
        p = posts[0];
        p.setText(null);
        res = Solution.updatePost(p);
        assertEquals(res, BAD_PARAMS);


        //here we have students 1 and 9 in the DB
        //and posts 1 and 11 in the DB

        //check like post
        //student doesn't exists
        res = Solution.likePost(2,1);
        assertEquals(res, NOT_EXISTS);
        //poset doesn't exists
        res = Solution.likePost(1,2);
        assertEquals(res, NOT_EXISTS);
        //student already liked the post
        Solution.likePost(1,1);
        res = Solution.likePost(1,1);
        assertEquals(res, ALREADY_EXISTS);
        //try to like post not in your group
        res = Solution.likePost(9,11);
        assertEquals(res, NOT_EXISTS);

        //check unlike post
        //student doesn't exists
        res = Solution.unlikePost(2,1);
        assertEquals(res, NOT_EXISTS);
        //poset doesn't exists
        res = Solution.unlikePost(1,2);
        assertEquals(res, NOT_EXISTS);
        //student didnt like the post
        res = Solution.unlikePost(1,11);
        assertEquals(res, NOT_EXISTS);

        //test make as friends
        clearTables();
        students[0].setFaculty("CS");
        students[1].setId(2);
        students[6].setId(7);
        for(int i=0; i<9; i++) Solution.addStudent(students[i]);
        //all students except 10 are in the db
        //one of the students doesn't exists
        res = Solution.makeAsFriends(1,10);
        assertEquals(res, NOT_EXISTS);
        res = Solution.makeAsFriends(10,1);
        assertEquals(res, NOT_EXISTS);
        //two of the students don't exists
        res = Solution.makeAsFriends(11,10);
        assertEquals(res, NOT_EXISTS);
        res = Solution.makeAsFriends(10,11);
        assertEquals(res, NOT_EXISTS);
        //same student
        res = Solution.makeAsFriends(1,1);
        assertEquals(res, BAD_PARAMS);
        //friendship already exists
        Solution.makeAsFriends(1,2);
        res = Solution.makeAsFriends(2,1);
        assertEquals(res, ALREADY_EXISTS);

        //check join group
        //student doesn't exists
        res = Solution.joinGroup(10, "CS");
        assertEquals(res, NOT_EXISTS);
        //student already in group
        res = Solution.joinGroup(1, "CS");
        assertEquals(res, ALREADY_EXISTS);

        //check leave group
        //student doesn't exists
        res = Solution.leaveGroup(10, "CS");
        assertEquals(res, NOT_EXISTS);
        //student not in group
        res = Solution.leaveGroup(1, "BIO");
        assertEquals(res, NOT_EXISTS);

    }

    @Test
    @Ignore
    //check advanced API functions, also kind of a stress test
    public void advancedApiTest(){
        //build database
        ReturnValue res;
        int NUM_OF_STUDENTS = 200; //if changing, erase test "assertEquals(people.size(), 42)" - NOT VALID
        String[] faculties = {"CS", "MATH", "PHYSICS", "BIO", "MED", "ELEC"};

        //add students to DB
        for(int i=1; i<NUM_OF_STUDENTS; i++){
            res = Solution.addStudent(buildStudent(i, ""+i, faculties[i%faculties.length]));
            assertEquals(res, OK);
        }

        //all even id students post in their faculty group (posts id's from 1 to NUM_OF_STUDENTS/2
        for(int i=2; i<NUM_OF_STUDENTS; i+=2){
            res = Solution.addPost(buildPost(i/2, i, 0,"student " + i +" public post" ,
                    LocalDateTime.now()), faculties[i%faculties.length]);
            assertEquals(res, OK);

        }

        //all odd id students post public (posts id's from 1 to NUM_OF_STUDENTS/2
        for(int i=1; i<NUM_OF_STUDENTS; i+=2){
            res = Solution.addPost(buildPost(NUM_OF_STUDENTS+i/2, i, 0,"student " + i +" public post" ,
                    LocalDateTime.now()), null);
            assertEquals(res, OK);

        }

        //add every fifth student to another group
        for(int i=5; i<NUM_OF_STUDENTS; i+=5){
            res = Solution.joinGroup(i, "Technion");
            assertEquals(res, OK);
        }

        //make almost every student friend with the student with the upper 10th id (1,2... with 10, 11,12... with 20 etc.)
        int upper = 10;
        for(int i=1; i<NUM_OF_STUDENTS-10; i++){
            if(i%10==0){
                upper+=10;
                continue;
            }
            res = Solution.makeAsFriends(i, upper);
            assertEquals(res, OK);
        }

        //check getStudentFeed
        Feed feed = null;
        //check Feed of every 10th student
        for(int i=10; i<NUM_OF_STUDENTS-10; i+=10){
            feed = Solution.getStudentFeed(i);
            for(int j=0; j<feed.size(); j++){
                int curr_author = feed.get(j).getAuthor();
                assertTrue(curr_author == i ||
                        curr_author%5==0 ||
                        curr_author%faculties.length == i%faculties.length ||
                        (curr_author%2==1 && (curr_author<i && curr_author>i-10)));
            }
        }

        //check getGroupFeed
        feed = Solution.getGroupFeed("CS");
        for(int j=0; j<feed.size(); j++) {
            int curr_author = feed.get(j).getAuthor();
            assertTrue(curr_author % faculties.length == 0);
        }
        feed = Solution.getGroupFeed("Technion");
        for(int j=0; j<feed.size(); j++) {
            int curr_author = feed.get(j).getAuthor();
            assertTrue(curr_author % 5 == 0);
        }

        //check getPeopleYouMayKnow
        ArrayList<Student> people = null;
        int[] sid = null;
        for(int m=0; m<NUM_OF_STUDENTS-10; m+=10) {
            for (int i = m+1; i < m+10; i++) {
                people = Solution.getPeopleYouMayKnowList(i);
                sid = new int[people.size()];
                for (int j = 0; j < people.size(); j++) {
                    sid[j] = people.get(j).getId();
                }
                for (int j = m+1; j < m+10; j++) {
                    if (i == j) continue;
                    boolean flag = false;
                    for(int k=0; k<sid.length; k++){
                        if (sid[k]==j){
                            flag=true;
                            break;
                        }
                    }
                    assertTrue( i%faculties.length != j%faculties.length || flag);
                }
            }
        }

        for(int i=10; i<NUM_OF_STUDENTS; i+=10){
            for(int j=10; j<NUM_OF_STUDENTS; j+=10){
                if(i==j) continue;
                res = Solution.makeAsFriends(i,j);
            }
        }
        people = Solution.getPeopleYouMayKnowList(10);
        System.out.println(people);
        assertEquals(people.size(), 42); //if changing NUM_OF_STUDENTS test is not valid!!!
    }

}




