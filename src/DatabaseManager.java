import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseManager {
    private String url;
    Connection conn;

    DatabaseManager(){
        url = "jdbc:sqlite:./Database/iiuihelper";
    }

    public  void createConnection(){
        try {
            conn = DriverManager.getConnection(url);
          System.out.println("Connection to SQLite has been established.");
  } catch (SQLException e) {
            System.out.println(e.getMessage());
     }
    }
    public void closeConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean validateLogin(String email,String password) {
        createConnection();
        String sql = "SELECT email,password FROM students WHERE email = ? AND password = ?";
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getString(1).equals(email)&&resultSet.getString(2).equals(password)){
                    closeConnection();
                    return true;
                }
                else {
                    closeConnection();
                    return false;
                }
            }
            closeConnection();
            return false;

        } catch (SQLException e) {
            closeConnection();
            return false;
        }


    }

    public Student getStudent(String email) {

        createConnection();
        String sql = "SELECT * FROM students WHERE email = ?";
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){

                int activecourses =0;
                String coursesql ="SELECT * FROM student_courses WHERE reg_num = "+resultSet.getInt("reg_num")+" AND status ='ongoing'";
                Statement statement = conn.createStatement();
               ResultSet enrolledcourses = statement.executeQuery(coursesql);
               while (enrolledcourses.next()){
                   activecourses++;
               }

                Student student = new Student(resultSet.getInt("reg_num"),resultSet.getString("name"),activecourses,resultSet.getInt("cgpa"),resultSet.getInt("cr_hrs"));
                closeConnection();

                return student;
            }



        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }


        return null;
    }


    public DefaultTableModel loadOngoingCourseTableData(Student student ) {
        try {
            createConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT courses.c_code,courses.c_name FROM student_courses JOIN students ON student_courses.reg_num = students.reg_num JOIN courses ON student_courses.c_code = courses.c_code WHERE students.reg_num = ? AND student_courses.status ='ongoing';");
            preparedStatement.setInt(1, student.getRegnum());


            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount()+1;

            List<Object[]> rows = new ArrayList<>();

            // Retrieve column names
            String[] columnNames = new String[columnCount];
            for (int col = 0; col < columnCount-1; col++) {
                columnNames[col] = metaData.getColumnName(col + 1);

            }
            columnNames[columnCount-1] ="Details";


            // Populate rows dynamically
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int col = 0; col < columnCount-1; col++) {
                    row[col] = resultSet.getObject(col + 1);
                }

                rows.add(row);
            }

            // Convert List to Object[][]
            Object[][] data = rows.toArray(new Object[0][]);
            DefaultTableModel model= new DefaultTableModel(data, columnNames){
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Make only the "Action" column editable
                    return column == columnCount-1;
                }};



            closeConnection();
            return model;
        } catch (Exception e) {
            closeConnection();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

        return null;
    }

    public DefaultTableModel loadCompletedCourseTableData(Student student ) {
        try {
            createConnection();
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT courses.c_code,courses.c_name,student_courses.grade FROM student_courses JOIN students ON student_courses.reg_num = students.reg_num JOIN courses ON student_courses.c_code = courses.c_code WHERE students.reg_num = ? AND student_courses.status ='completed';");
            preparedStatement.setInt(1, student.getRegnum());


            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount()+1;

            List<Object[]> rows = new ArrayList<>();

            // Retrieve column names
            String[] columnNames = new String[columnCount];
            for (int col = 0; col < columnCount-1; col++) {
                columnNames[col] = metaData.getColumnName(col + 1);

            }
            columnNames[columnCount-1] ="Details";


            // Populate rows dynamically
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int col = 0; col < columnCount-1; col++) {
                    row[col] = resultSet.getObject(col + 1);
                }

                rows.add(row);
            }

            // Convert List to Object[][]
            Object[][] data = rows.toArray(new Object[0][]);
            DefaultTableModel model= new DefaultTableModel(data, columnNames){
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Make only the "Action" column editable
                    return column == columnCount-1;
                }};



            closeConnection();
            return model;
        } catch (Exception e) {
            closeConnection();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

        return null;
    }



    public String getCourseDetails(String cCode){
        createConnection();
        String courseDetails ="";
        String sql = "SELECT * FROM courses WHERE c_code = ?";
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, cCode);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
               courseDetails ="Course Code: "+resultSet.getString("c_code")+"\nCourse Title: "+resultSet.getString("c_name")
               +"\n Credit Hours: "+resultSet.getInt("cr_hrs")+"\nInstructor Name: "+resultSet.getString("instructor")+
                   "\n Instructors Contact: " + resultSet.getString("instructor_contact");
                closeConnection();
                return courseDetails;
            }



        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }


        return courseDetails;
    }

    public ArrayList<String> getAvailableCourses(Student student) {
        createConnection();

        ArrayList<String> courseTitles = new ArrayList<>();
        String sql = "SELECT c.c_name FROM courses c LEFT JOIN student_courses sc  ON c.c_code = sc.c_code AND sc.reg_num = ? WHERE sc.reg_num IS NULL;";
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(student.getRegnum()));

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
               courseTitles.add(resultSet.getString(1));


            }
            closeConnection();
            return courseTitles;



        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }


    return null;
    }
    public void addnewCourse(Student student,String c_name,String status,String grade){

        createConnection();

        try {
            String c_code = "";
            String c_codesql ="SELECT c_code FROM courses WHERE c_name = '"+c_name+"'";
            Statement statement = conn.createStatement();
           ResultSet c_coderesult = statement.executeQuery(c_codesql);
           if (c_coderesult.next()){
               c_code = c_coderesult.getString(1);
           }
            String sql = "INSERT INTO student_courses VALUES(?,?,?,?)" ;

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, student.getRegnum());
            preparedStatement.setString(2,c_code);
            preparedStatement.setString(3,status);
            preparedStatement.setString(4,grade);

            preparedStatement.executeUpdate();
            if(status.equals("ongoing")) {
                var activecourses = student.getActiveCourses();
                activecourses++;
                student.setActiveCourses(activecourses);
            }




        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();
    }

    public ArrayList<String> getEnrolledCourses(Student student) {
        createConnection();

        ArrayList<String> courseTitles = new ArrayList<>();
        String sql = "SELECT courses.c_name FROM student_courses JOIN students ON student_courses.reg_num = students.reg_num JOIN courses ON student_courses.c_code = courses.c_code WHERE students.reg_num = ? AND status = 'ongoing'";
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(student.getRegnum()));

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                courseTitles.add(resultSet.getString(1));


            }
            closeConnection();
            return courseTitles;



        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }


        return null;
}
    public ArrayList<String> getCompletedCourses(Student student) {
        createConnection();

        ArrayList<String> courseTitles = new ArrayList<>();
        String sql = "SELECT courses.c_name FROM student_courses JOIN students ON student_courses.reg_num = students.reg_num JOIN courses ON student_courses.c_code = courses.c_code WHERE students.reg_num = ? AND status = 'completed'";
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(student.getRegnum()));

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                courseTitles.add(resultSet.getString(1));


            }
            closeConnection();
            return courseTitles;



        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }


        return null;
    }
    public String getC_Code(String c_name){
        String c_code =" ";
        try {


        String c_codesql ="SELECT c_code FROM courses WHERE c_name = '"+c_name+"'";
        Statement statement = conn.createStatement();
        ResultSet c_coderesult = statement.executeQuery(c_codesql);
        if (c_coderesult.next()) {
            c_code = c_coderesult.getString(1);
        }}catch(SQLException e) {
        }
        finally {
            return c_code;
        }
    }
    public void removeCourse(Student student, String c_name,boolean active ){
        createConnection();

        try {
            String c_code = getC_Code(c_name);

            String sql = "DELETE FROM student_courses WHERE reg_num =? AND c_code = ?" ;

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, student.getRegnum());
            preparedStatement.setString(2,c_code);

            preparedStatement.executeUpdate();
            if(active) {
                var activecourses = student.getActiveCourses();
                activecourses--;
                student.setActiveCourses(activecourses);
            }


        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();
    }

    public boolean hasEvent(String s,Student student) {





        String sql = "SELECT DISTINCT classes.date AS date FROM classes  JOIN courses ON classes.c_code = courses.c_code  JOIN student_courses ON courses.c_code = student_courses.c_code  WHERE classes.reg_num = ? AND classes.date = ? AND student_courses.status='ongoing' "
                +" UNION SELECT events.date AS date FROM events   WHERE events.reg_num = ? AND events.date = ? "
                +" UNION SELECT assignments.date AS date FROM assignments   WHERE assignments.reg_num = ? AND assignments.date = ? ;";
        ResultSet resultSet;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,student.getRegnum());
            preparedStatement.setString(2,s);
            preparedStatement.setInt(3,student.getRegnum());
            preparedStatement.setString(4,s);
            preparedStatement.setInt(5,student.getRegnum());
            preparedStatement.setString(6,s);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                return true;



            }



        } catch (SQLException e) {

            e.printStackTrace();


        }


        return false;

    }

    public ResultSet getUpcomingClasses(Student student,int day,int month,int year) {

        String sql = "SELECT DISTINCT classes.class_name,classes.time, classes.date,classes.c_code FROM classes  JOIN courses ON classes.c_code = courses.c_code  JOIN student_courses ON courses.c_code = student_courses.c_code  WHERE student_courses.reg_num = ? AND classes.date = ? AND student_courses.status = 'ongoing' ;";
        ResultSet resultSet;
        String formattedDate = day + " " + month + "/" + year;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,student.getRegnum());
            preparedStatement.setString(2,formattedDate);

            resultSet = preparedStatement.executeQuery();


            return resultSet;







        } catch (SQLException e) {

            e.printStackTrace();


        }


        return null;

    }

    public ResultSet getUpcomingEvents(Student student, int day,int month,int year) {

        String sql = "SELECT events.e_title,events.time, events.date FROM events   WHERE events.reg_num = ? AND events.date = ? ;";
        ResultSet resultSet;
        String formattedDate = day + " " + month + "/" + year;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,student.getRegnum());
            preparedStatement.setString(2,formattedDate);

            resultSet = preparedStatement.executeQuery();


            return resultSet;







        } catch (SQLException e) {

            e.printStackTrace();


        }


        return null;


    }

    public ResultSet getAssignments(Student student) {


        String sql = "SELECT  * FROM assignments  WHERE reg_num = ? ;";
        ResultSet resultSet;

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,student.getRegnum());

            resultSet = preparedStatement.executeQuery();


            return resultSet;







        } catch (SQLException e) {

            e.printStackTrace();


        }


        return null;

    }


    public ResultSet getUpcomingAssignments(Student student, int day,int month,int year) {

        String sql = "SELECT assignments.a_title,assignments.time, assignments.date, assignments.a_course FROM assignments   WHERE assignments.reg_num = ? AND assignments.date = ? ;";
        ResultSet resultSet;
        String formattedDate = day + " " + month + "/" + year;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,student.getRegnum());
            preparedStatement.setString(2,formattedDate);

            resultSet = preparedStatement.executeQuery();


            return resultSet;







        } catch (SQLException e) {

            e.printStackTrace();


        }


        return null;


    }

    public void updateAssignmentStatus(int aId, String selectedItem) {


        createConnection();

        try {

            String sql = "UPDATE assignments SET status = ? WHERE a_id = ? ;" ;

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, selectedItem);
            preparedStatement.setInt(2,aId);

            preparedStatement.executeUpdate();




        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();

    }

//    ,a_title TEXT,a_course TEXT,reg_num INT, a_desc TEXT, time TEXT, date TEXT, priority TEXT, status TEXT,
//    FOREIGN KEY(reg_num) REFERENCES students (reg_num));

    public void addNewAssignment(int regnum, String a_title, String a_course, String a_desc, String priority, String time, String date) {


        createConnection();

        try {
            String a_idsql ="SELECT a_id FROM assignments;";
            Statement statement = conn.createStatement();
            ResultSet a_ids = statement.executeQuery(a_idsql);
            int highestaid =0;
            while (a_ids.next()){
                if (a_ids.getInt(1) >=highestaid){
                    highestaid = a_ids.getInt(1);
                }
            }

            highestaid++;



            String sql = "INSERT INTO assignments(a_id,a_title,a_course,reg_num,a_desc,time,date,priority,status )VALUES(?,?,?,?,?,?,?,?,?);" ;

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,highestaid);
            preparedStatement.setString(2, a_title);
            preparedStatement.setString(3, a_course);
            preparedStatement.setInt(4,regnum);
            preparedStatement.setString(5, a_desc);
            preparedStatement.setString(6, time);
            preparedStatement.setString(7, date);
            preparedStatement.setString(8,priority );
            preparedStatement.setString(9, "Incomplete");
            preparedStatement.executeUpdate();




        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();





    }

    public void removeAssignment(Student student, Integer a_id) {

        createConnection();

        try {

            String sql = "DELETE FROM assignments WHERE reg_num =? AND a_id = ?" ;

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, student.getRegnum());
            preparedStatement.setInt(2,a_id);

            preparedStatement.executeUpdate();



        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();

    }

    public void addnewEvent(String e_title, String time, String date, Student student) {



        createConnection();

        try {
            String a_idsql ="SELECT e_id FROM events;";
            Statement statement = conn.createStatement();
            ResultSet e_ids = statement.executeQuery(a_idsql);
            int highesteid =0;
            while (e_ids.next()){
                if (e_ids.getInt(1) >= highesteid){
                    highesteid = e_ids.getInt(1);
                }
            }

            highesteid++;



            String sql = "INSERT INTO events(e_id,e_title,time,date,reg_num )VALUES(?,?,?,?,?);" ;

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, highesteid);
            preparedStatement.setString(2, e_title);
            preparedStatement.setString(3, time);
            preparedStatement.setString(4, date);
            preparedStatement.setInt(5,student.getRegnum() );

            preparedStatement.executeUpdate();




        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();



    }

    public ArrayList<String> getEventsAtDate(Student student, String selecetedDate) {
        createConnection();
        String sql = "SELECT  e_title FROM events  WHERE reg_num = ? AND date =? ;";
        ResultSet resultSet;

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,student.getRegnum());
            preparedStatement.setString(2,selecetedDate);

            resultSet = preparedStatement.executeQuery();
            ArrayList<String> event_titles = new ArrayList<>();
            while (resultSet.next()){
                event_titles.add(resultSet.getString(1));
            }
            closeConnection();
            return event_titles;










        } catch (SQLException e) {

            e.printStackTrace();


        }



        closeConnection();
        return null;
    }

    public void removeEvent(Student student, String e_title) {


        createConnection();

        try {

            String sql = "DELETE FROM events WHERE reg_num =? AND e_title = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, student.getRegnum());
            preparedStatement.setString(2, e_title);

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();
    }

    public boolean addnewStudent(int reg, String name, String email, String password) {
        createConnection();

        try {
            String check ="SELECT reg_num,email FROM students WHERE reg_num =? OR email =? ;";
            PreparedStatement checkStatement = conn.prepareStatement(check);

            checkStatement.setInt(1,reg);
            checkStatement.setString(2,email);
           ResultSet resultSet = checkStatement.executeQuery();
            if(resultSet.next()){
                closeConnection();
                return false;
            }

            String sql = "INSERT INTO students VALUES(?,?,?,?,0,0)";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, reg);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);

            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException e) {

            e.printStackTrace();


        }

        closeConnection();
        return false;

    }


    public void updateStudent(Student student) {

        createConnection();

        try {

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT courses.cr_hrs,student_courses.grade FROM student_courses JOIN students ON student_courses.reg_num = students.reg_num JOIN courses ON student_courses.c_code = courses.c_code WHERE students.reg_num = ? AND student_courses.status ='completed';");

            preparedStatement.setInt(1, student.getRegnum());

         ResultSet  resultSet = preparedStatement.executeQuery();
         int crshrs = 0;
         float gradepoints =0;

             student.setCr_hrs(0);
             student.setCgpa(0);


         while (resultSet.next()){
             crshrs+= resultSet.getInt("cr_hrs");
             switch (resultSet.getString("grade")){
                 case "A":
                     gradepoints +=  resultSet.getInt("cr_hrs") * 4;
                     break;
                 case "B":
                     gradepoints +=  resultSet.getInt("cr_hrs") * 3;
                     break;
                 case "C":
                     gradepoints +=  resultSet.getInt("cr_hrs") * 2;
                     break;
                     case "D":
                     gradepoints +=  resultSet.getInt("cr_hrs") * 1;
                     break;
                 case "F":
                     gradepoints +=  resultSet.getInt("cr_hrs") * 0;


             }
             student.setCr_hrs(crshrs);
             student.setCgpa(gradepoints/crshrs);





         }





        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();


    }

    public ArrayList<String> getClassesAtDate(Student student, String selectedDate) {
        createConnection();
        String sql = "SELECT  class_name FROM classes  WHERE reg_num = ? AND date =? ;";
        ResultSet resultSet;

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,student.getRegnum());
            preparedStatement.setString(2,selectedDate);

            resultSet = preparedStatement.executeQuery();
            ArrayList<String> class_titles = new ArrayList<>();
            while (resultSet.next()){
                class_titles.add(resultSet.getString(1));
            }
            closeConnection();
            return class_titles;










        } catch (SQLException e) {

            e.printStackTrace();


        }



        closeConnection();
        return null;
    }

    public void removeClass(Student student, String classname) {



        createConnection();

        try {

            String sql = "DELETE FROM classes WHERE reg_num =? AND class_name = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, student.getRegnum());
            preparedStatement.setString(2, classname);

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();
    }

    public void addnewclass(String class_name, String time, String date, Student student, String course_title) {
        createConnection();
        String c_code = getC_Code(course_title);




        try {
            String c_idsql ="SELECT class_id FROM classes;";
            Statement statement = conn.createStatement();
            ResultSet c_ids = statement.executeQuery(c_idsql);
            int highesteid =0;
            while (c_ids.next()){
                if (c_ids.getInt(1) >= highesteid){
                    highesteid = c_ids.getInt(1);
                }
            }

            highesteid++;



            String sql = "INSERT INTO classes(class_id,c_code,class_name,time,date,reg_num )VALUES(?,?,?,?,?,?);" ;

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, highesteid);
            preparedStatement.setString(2, c_code);
            preparedStatement.setString(3,class_name);
            preparedStatement.setString(4, time);
            preparedStatement.setString(5, date);
            preparedStatement.setInt(6,student.getRegnum() );

            preparedStatement.executeUpdate();




        } catch (SQLException e) {
            closeConnection();
            e.printStackTrace();


        }

        closeConnection();



    }
}


