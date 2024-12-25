import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

class OverVeiwPanel extends JPanel{
    private  JLabel name;
    private  JLabel regnum;
    private  JLabel activecourses;
    private  JLabel cgpa;
    private  JLabel cr_hrs;

    OverVeiwPanel(Student student){



        this.setLayout(null);
        name = new JLabel("Name: "+student.getName());
        name.setBounds(50,50,200,100);
        this.add(name);

        regnum = new JLabel("Registration number: "+ student.getRegnum());
        regnum.setBounds(50,100,200,100);
        this.add(regnum);



        activecourses = new JLabel("Active Courses: "+ student.getActiveCourses());
        activecourses.setBounds(50,150,200,100);
        this.add(activecourses);

        cgpa = new JLabel("CGPA: "+ student.getCgpa());
        cgpa.setBounds(50,200,200,100);
        this.add(cgpa);

        cr_hrs = new JLabel("Credit Hours Completed: "+ student.getCr_hrs());
        cr_hrs.setBounds(50,250,200,100);
        this.add(cr_hrs);


    }
}

class CoursesPanel extends JPanel implements  ActionListener{
    private  JTable table;
    private  JButton newCourse;
    private  JButton removeCourse;
    private JButton ongoingCourse;
    private JButton compCourse;
    private  Student student;
    private boolean  ongoing;
    JScrollPane scrollPane;
    DatabaseManager databaseManager;
    CoursesPanel(DatabaseManager databaseManager,Student student){
        ongoing =true;
        this.student =student;
        table = new JTable();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.databaseManager =databaseManager;
        JPanel courseButtonPanel = new JPanel();
        courseButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        ongoingCourse = new JButton("Ongoing");
        ongoingCourse.setBackground(Color.gray);
        ongoingCourse.addActionListener(this);

        courseButtonPanel.add(ongoingCourse);
        compCourse = new JButton("Completed");
        compCourse.setBackground(Color.gray);
        compCourse.addActionListener(this);
        courseButtonPanel.add(compCourse);
        this.add(courseButtonPanel);
         scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ongoing Courses:"));

        this.add(scrollPane,BorderLayout.CENTER);
        DefaultTableModel model =databaseManager.loadOngoingCourseTableData(student);

        table.setModel(model);
        table.getColumn("Details").setCellRenderer(new ButtonRenderer());

        // Set a custom editor for the details column
        table.getColumn("Details").setCellEditor(new ButtonEditor(new JCheckBox(),databaseManager));
        JPanel selectionButtonPanel = new JPanel();
        selectionButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        newCourse = new JButton("+ Add new course");
        newCourse.addActionListener(this);
        newCourse.setBackground(Color.gray);
        selectionButtonPanel.add(newCourse);
        removeCourse = new JButton("- Delete a course");
        removeCourse.setBackground(Color.gray);
        removeCourse.addActionListener(this);
        selectionButtonPanel.add(removeCourse);
        this.add(selectionButtonPanel);

    }
    public String coursedetails(String cCode){
        return databaseManager.getCourseDetails(cCode);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newCourse){
            if(ongoing) {
                ArrayList<String> courseTitles = databaseManager.getAvailableCourses(student);
                JComboBox<String> courseComboBox = new JComboBox<>(courseTitles.toArray(new String[0]));
                if (courseTitles.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No available courses");
                } else {
                    JOptionPane.showMessageDialog(null, courseComboBox);
                    databaseManager.addnewCourse(student, courseComboBox.getItemAt(courseComboBox.getSelectedIndex()), "ongoing", "N/A");

                    table.setModel(databaseManager.loadOngoingCourseTableData(student));
                    table.getColumn("Details").setCellRenderer(new ButtonRenderer());

                    // Set a custom editor for the details column
                    table.getColumn("Details").setCellEditor(new ButtonEditor(new JCheckBox(), databaseManager));
                }
            }
            else {
                ArrayList<String> courseTitles = databaseManager.getAvailableCourses(student);
                JComboBox<String> courseComboBox = new JComboBox<>(courseTitles.toArray(new String[0]));
                if (courseTitles.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No available courses");
                } else {
                    JOptionPane.showMessageDialog(null, courseComboBox);
                    String[]grades = {"A","B","C","D","F"};
                    JComboBox<String> gradeselection = new JComboBox<>(grades);
                    JOptionPane.showMessageDialog(null,gradeselection);
                    databaseManager.addnewCourse(student, courseComboBox.getItemAt(courseComboBox.getSelectedIndex()), "completed",(String) gradeselection.getSelectedItem());

                    table.setModel(databaseManager.loadCompletedCourseTableData(student));
                    table.getColumn("Details").setCellRenderer(new ButtonRenderer());

                    // Set a custom editor for the details column
                    table.getColumn("Details").setCellEditor(new ButtonEditor(new JCheckBox(), databaseManager));
                }
            }
        }
        if(e.getSource()==removeCourse){
            if(ongoing){
            ArrayList<String> courseTitles= databaseManager.getEnrolledCourses(student);
            JComboBox<String> courseComboBox = new JComboBox<>(courseTitles.toArray(new String[0]));
            if(courseTitles.isEmpty()){
                JOptionPane.showMessageDialog(null,"No available courses");
            }
            else {
                JOptionPane.showMessageDialog(null, courseComboBox);
                databaseManager.removeCourse(student, courseComboBox.getItemAt(courseComboBox.getSelectedIndex()),true);

                table.setModel(databaseManager.loadOngoingCourseTableData(student));
                table.getColumn("Details").setCellRenderer(new ButtonRenderer());

                // Set a custom editor for the details column
                table.getColumn("Details").setCellEditor(new ButtonEditor(new JCheckBox(),databaseManager));
            }

            }
            else {
                ArrayList<String> courseTitles= databaseManager.getCompletedCourses(student);
                JComboBox<String> courseComboBox = new JComboBox<>(courseTitles.toArray(new String[0]));
                if(courseTitles.isEmpty()){
                    JOptionPane.showMessageDialog(null,"No available courses");
                }
                else {
                    JOptionPane.showMessageDialog(null, courseComboBox);
                    databaseManager.removeCourse(student, courseComboBox.getItemAt(courseComboBox.getSelectedIndex()),false);

                    table.setModel(databaseManager.loadCompletedCourseTableData(student));
                    table.getColumn("Details").setCellRenderer(new ButtonRenderer());

                    // Set a custom editor for the details column
                    table.getColumn("Details").setCellEditor(new ButtonEditor(new JCheckBox(),databaseManager));
                }
            }


        }
        if(e.getSource()==compCourse){
            ongoing=false;
            scrollPane.setBorder(BorderFactory.createTitledBorder("Completed Courses:"));

            table.setModel(databaseManager.loadCompletedCourseTableData(student));
            table.getColumn("Details").setCellRenderer(new ButtonRenderer());

            // Set a custom editor for the details column
            table.getColumn("Details").setCellEditor(new ButtonEditor(new JCheckBox(),databaseManager));
        }
        if (e.getSource()==ongoingCourse){
            ongoing=true;
            scrollPane.setBorder(BorderFactory.createTitledBorder("Ongoing Courses:"));
            table.setModel(databaseManager.loadOngoingCourseTableData(student));
            table.getColumn("Details").setCellRenderer(new ButtonRenderer());

            // Set a custom editor for the details column
            table.getColumn("Details").setCellEditor(new ButtonEditor(new JCheckBox(),databaseManager));
        }
    }
}

class SchedulerPanel extends JPanel implements ActionListener{
    private final JPanel upcomingPanel;
    Student student;
    InteractiveCalendar interactiveCalendar;
    DatabaseManager databaseManager;
    private JPopupMenu calenderMenu;
    private String selectedDate;
    SchedulerPanel(DatabaseManager databaseManager,Student student){
        this.databaseManager=databaseManager;
        this.student =student;
        createCalenderMenu();
        upcomingPanel= new JPanel();
        upcomingPanel.setLayout(new BoxLayout(upcomingPanel,BoxLayout.Y_AXIS));
        updateUpcomingpanel();
        this.setLayout(new BorderLayout());

        interactiveCalendar.setPreferredSize(new Dimension(350,100));
        interactiveCalendar.setBorder(BorderFactory.createEmptyBorder(10, 10, 200, 10));
        this.add(interactiveCalendar,BorderLayout.WEST);
        this.add(upcomingPanel,BorderLayout.EAST);
    }

    private void createCalenderMenu() {

        calenderMenu = new JPopupMenu();

        JMenuItem showClasses = new JMenuItem("Show Classes");
        showClasses.addActionListener(e -> {
            ArrayList<String>events = databaseManager.getClassesAtDate(student, selectedDate);
            if(!events.isEmpty()) {

                String todayEvents = "Classes at "+ selectedDate+":\n";
                for(String event:events){
                    todayEvents+=event +"\n";
                }
                JOptionPane.showMessageDialog(null, todayEvents);
            }
            else
                JOptionPane.showMessageDialog(null,"No classes are scheduled on this day");});
        calenderMenu.add(showClasses);
        JMenuItem addClass = new JMenuItem("Add Class");
        addClass.addActionListener(e -> {AddClassFrame classFrame = new AddClassFrame(databaseManager,student, selectedDate,this);});
        calenderMenu.add(addClass);
        JMenuItem removeClass = new JMenuItem("Remove Class");
        removeClass.addActionListener(e -> {
            ArrayList<String>events = databaseManager.getClassesAtDate(student, selectedDate);
            if(!events.isEmpty()) {
                JComboBox<String> classtitles = new JComboBox<>(events.toArray(new String[0]));
                JOptionPane.showMessageDialog(null, classtitles);
                databaseManager.removeClass(student,(String) classtitles.getSelectedItem());
                this.updateUpcomingpanel();
                interactiveCalendar.updateCalendar();
            }
            else
                JOptionPane.showMessageDialog(null,"No classes are scheduled on this day");

        });

        calenderMenu.add(removeClass);

        JMenuItem showeve = new JMenuItem("Show Events");
        showeve.addActionListener(e -> {
            ArrayList<String>events = databaseManager.getEventsAtDate(student, selectedDate);
            if(!events.isEmpty()) {

                String todayEvents = "Events at "+ selectedDate+":\n";
                for(String event:events){
                    todayEvents+=event +"\n";
                }
                JOptionPane.showMessageDialog(null, todayEvents);
            }
            else
                JOptionPane.showMessageDialog(null,"No events are scheduled on this day");});
        calenderMenu.add(showeve);
        JMenuItem addeve = new JMenuItem("Add Event");
        addeve.addActionListener(e -> {AddEventFrame eventFrame = new AddEventFrame(databaseManager,student, selectedDate,this);});
        calenderMenu.add(addeve);
        JMenuItem removeeve = new JMenuItem("Remove Event");
        removeeve.addActionListener(e -> {
            ArrayList<String>events = databaseManager.getEventsAtDate(student, selectedDate);
            if(!events.isEmpty()) {
                JComboBox<String> eventtitles = new JComboBox<>(events.toArray(new String[0]));
                JOptionPane.showMessageDialog(null, eventtitles);
                databaseManager.removeEvent(student,(String)eventtitles.getSelectedItem());
                this.updateUpcomingpanel();
                interactiveCalendar.updateCalendar();
            }
            else
                JOptionPane.showMessageDialog(null,"No events are scheduled on this day");

        });

        calenderMenu.add(removeeve);
        interactiveCalendar =new InteractiveCalendar(databaseManager,student);
        interactiveCalendar.setDayClickListener(((day, month, year) -> {
            selectedDate = day+" "+month+"/"+year;
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            Point point =pointerInfo.getLocation();


            Point frameLocation = this.getLocationOnScreen();

            // Adjust to component-relative coordinates
            int x = point.x - frameLocation.x;
            int y = point.y - frameLocation.y;

            calenderMenu.show(this,x,y);

        }));
    }

    public void updateUpcomingpanel(){

            upcomingPanel.removeAll();
            upcomingPanel.add(new JLabel("Upcoming Classes:"));

            databaseManager.createConnection();
        JTextArea classArea = new JTextArea();
        classArea.setLineWrap(true);
        classArea.setWrapStyleWord(true);
        classArea.setEditable(false);
        classArea.setBackground(Color.lightGray);
            for (int i = 0; i <= 7; i++) {

                // Create a Calendar instance and add 'i' days to the current date
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, i);

                // Format the date as needed (e.g., "DD MM/YYYY")
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based in Calendar
                int year = calendar.get(Calendar.YEAR);


                ResultSet upcomingClasses = databaseManager.getUpcomingClasses(student, day,month,year);
                try {
                    while (upcomingClasses.next()) {
                       classArea.append (
                                "Course: " + upcomingClasses.getString(1) +
                                        "\nTime: " + upcomingClasses.getString(2) +
                                        "\nDate: " + upcomingClasses.getString(3)+"\n"
                        );


                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        upcomingPanel.add(new JScrollPane(classArea)); // Wrap with scroll pane

        upcomingPanel.add(new JLabel("Upcoming Assignments:"));
        databaseManager.createConnection();
        JTextArea assignmentArea = new JTextArea();
        assignmentArea.setLineWrap(true);
        assignmentArea.setWrapStyleWord(true);
        assignmentArea.setEditable(false);
        assignmentArea.setBackground(Color.LIGHT_GRAY);
        for (int i = 0; i <= 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, i);

            // Format the date as needed (e.g., "DD MM/YYYY")
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based in Calendar
            int year = calendar.get(Calendar.YEAR);
            ResultSet upcomingEvents = databaseManager.getUpcomingAssignments(student, day,month,year);
            try {
                while (upcomingEvents.next()) {
                    assignmentArea.append (
                            "Assignment: " + upcomingEvents.getString(1) +
                                    "\nTime: " + upcomingEvents.getString(2) +
                                    "\nDate: " + upcomingEvents.getString(3)+"\n"
                    );


                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        upcomingPanel.add(new JScrollPane(assignmentArea)); // Wrap with scroll pane
        databaseManager.closeConnection();




            databaseManager.closeConnection();
        upcomingPanel.add(new JLabel("Upcoming Events:"));
        databaseManager.createConnection();
        JTextArea eventArea = new JTextArea();
        eventArea.setLineWrap(true);
        eventArea.setWrapStyleWord(true);
        eventArea.setEditable(false);
        eventArea.setBackground(Color.LIGHT_GRAY);
        for (int i = 0; i <= 7; i++) {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, i);

            // Format the date as needed (e.g., "DD MM/YYYY")
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based in Calendar
            int year = calendar.get(Calendar.YEAR);

            ResultSet upcomingEvents = databaseManager.getUpcomingEvents(student, day,month,year);
            try {
                while (upcomingEvents.next()) {
                    eventArea.append (
                            "Event: " + upcomingEvents.getString(1) +
                                    "\nTime: " + upcomingEvents.getString(2) +
                                    "\nDate: " + upcomingEvents.getString(3)+"\n"
                    );


                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        upcomingPanel.add(new JScrollPane(eventArea)); // Wrap with scroll pane
        databaseManager.closeConnection();

            upcomingPanel.revalidate();
            upcomingPanel.repaint();
        }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}


class AssignmentsPanel extends  JPanel implements  ActionListener {
    DatabaseManager databaseManager;
    Student student;
    JButton addAssignment;
    JButton removeAssignment;

    AssignmentsPanel(DatabaseManager databaseManager, Student student) {
        addAssignment = new JButton();
        addAssignment.setText("+ Add a new assignment");
        addAssignment.setBackground(Color.gray);
        addAssignment.addActionListener(this);

        removeAssignment = new JButton();
        removeAssignment.setText("- Remove an assignment");
        removeAssignment.setBackground(Color.gray);
        removeAssignment.addActionListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.databaseManager = databaseManager;
        this.student = student;
        updateAssignments();


    }

    public void addHeaders() {
        JPanel headers = new JPanel();
        headers.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel assignmentlabel = new JLabel("Title:");
        JLabel courselabel = new JLabel("Course:");
        JLabel priotitylabel = new JLabel("Priority:");
        JLabel statuslabel = new JLabel("Status:");
        JLabel duelabel = new JLabel("Due:");

        assignmentlabel.setPreferredSize(new Dimension(125, 25));
        courselabel.setPreferredSize(new Dimension(125, 25));
        priotitylabel.setPreferredSize(new Dimension(125, 25));
        statuslabel.setPreferredSize(new Dimension(125, 25));
        duelabel.setPreferredSize(new Dimension(125, 25));

        headers.add(assignmentlabel);
        headers.add(courselabel);
        headers.add(priotitylabel);
        headers.add(statuslabel);
        headers.add(duelabel);


        headers.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        headers.setPreferredSize(new Dimension(400, 50));
        headers.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        this.add(headers);
    }

    public void addButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addAssignment);
        buttonPanel.add(removeAssignment);


        this.add(buttonPanel);


    }

    public void updateAssignments() {
        this.removeAll();
        addHeaders();
        String[] statuses = {"Incomplete", "Completed", "Submitted", "Graded"};
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));


        databaseManager.createConnection();
        ResultSet assignments = databaseManager.getAssignments(student);
        while (true) {
            try {
                if (!assignments.next()) break;
                JPanel assignmentPanel = new JPanel();
                assignmentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                JTextField assignmenttitle = new JTextField();
                assignmenttitle.setPreferredSize(new Dimension(125, 25));
                assignmenttitle.setText(assignments.getString("a_title"));
                assignmenttitle.setEditable(false);
                assignmenttitle.setBackground(Color.lightGray);
                assignmentPanel.add(assignmenttitle);

                JTextField assignmentcourse = new JTextField();
                assignmentcourse.setPreferredSize(new Dimension(125, 25));
                assignmentcourse.setText(assignments.getString("a_course"));
                assignmentcourse.setEditable(false);
                assignmentcourse.setBackground(Color.lightGray);
                assignmentPanel.add(assignmentcourse);

                JTextField assignmentpriority = new JTextField();
                assignmentpriority.setPreferredSize(new Dimension(125, 25));
                assignmentpriority.setText(assignments.getString("priority"));
                assignmentpriority.setEditable(false);
                assignmentpriority.setBackground(Color.lightGray);
                assignmentPanel.add(assignmentpriority);

                AssignmentComboBox assignmentstatus = new AssignmentComboBox(statuses);
                assignmentstatus.setA_id(assignments.getInt("a_id"));
                assignmentstatus.setPreferredSize(new Dimension(125, 25));
                assignmentstatus.setSelectedItem(assignments.getString("status"));
                assignmentstatus.setEditable(false);
                assignmentstatus.setBackground(Color.lightGray);
                assignmentstatus.addActionListener(e -> {
                    databaseManager.updateAssignmentStatus(assignmentstatus.getA_id(), (String) assignmentstatus.getSelectedItem());
                });
                assignmentPanel.add(assignmentstatus);

                JTextField assignmentdue = new JTextField();
                assignmentdue.setPreferredSize(new Dimension(75, 25));
                assignmentdue.setText(assignments.getString("date"));
                assignmentdue.setEditable(false);
                assignmentdue.setBackground(Color.lightGray);
                assignmentPanel.add(assignmentdue);

                AssignmentButton assignmentdetails = new AssignmentButton(assignments.getString("a_desc"));
                assignmentdetails.setPreferredSize(new Dimension(75, 25));
                assignmentdetails.setText("Details");
                assignmentdetails.setBackground(Color.lightGray);
                assignmentdetails.addActionListener(e -> {
                    JOptionPane.showMessageDialog(null, assignmentdetails.getAssignment_details());
                });
                assignmentPanel.add(assignmentdetails);

                // Limit panel size to reduce space
                assignmentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                assignmentPanel.setPreferredSize(new Dimension(400, 50));
                assignmentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                assignmentPanel.setBackground(Color.gray);

                mainPanel.add(assignmentPanel);
                mainPanel.add(Box.createVerticalStrut(5)); // Add minimal spacing between panels
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        databaseManager.closeConnection();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder((BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        this.add(scrollPane);

        addButtons();
        this.repaint();
        this.revalidate();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == removeAssignment) {
            databaseManager.createConnection();
            ResultSet assignments = databaseManager.getAssignments(student);
            ArrayList<String> titles = new ArrayList<String>();
            ArrayList<Integer> ai_ids = new ArrayList<Integer>();
            while (true) {
                try {
                    if (!assignments.next()) break;
                    ai_ids.add(assignments.getInt("a_id"));
                    titles.add(assignments.getString("a_title"));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
            databaseManager.closeConnection();
            JComboBox<String> assignmentComboBox = new JComboBox<>(titles.toArray(new String[0]));
            JComboBox<Integer> aidComboBox = new JComboBox<>(ai_ids.toArray(new Integer[0]));
            JOptionPane.showMessageDialog(null, assignmentComboBox);
            databaseManager.removeAssignment(student,(Integer) aidComboBox.getItemAt(assignmentComboBox.getSelectedIndex()));
            updateAssignments();

        }
        if(e.getSource()==addAssignment){
            AddAssignmentFrame addAssignmentFrame = new AddAssignmentFrame(databaseManager,student,this);
        }

    }
}
class AssignmentButton extends JButton{
    private String assignment_details;
    AssignmentButton(String details){
        assignment_details =details;
    }

    public String getAssignment_details() {
        return assignment_details;
    }

    public void setAssignment_details(String assignment_details) {
        this.assignment_details = assignment_details;
    }
}

class  AssignmentComboBox extends  JComboBox<String>{
    private int a_id;

    public AssignmentComboBox(String[] statuses) {
        super(statuses);
    }

    public int getA_id() {
        return a_id;
    }

    public void setA_id(int a_id) {
        this.a_id = a_id;
    }
}

class ToDoPanel extends JPanel implements ActionListener{
    private DatabaseManager databaseManager;
    private Student student;
    private  JComboBox<String> courses;
    JPanel mainPanel;

    ToDoPanel(DatabaseManager databaseManager, Student student){
        this.databaseManager =databaseManager;
        this.student = student;

        this.setLayout(new BoxLayout(this ,BoxLayout.Y_AXIS));
        mainPanel = new JPanel();

        addSelection();
        updateTodo(false);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        this.add(scrollPane);

    }

    private void addSelection() {
        ArrayList<String> enrolled_courses = databaseManager.getEnrolledCourses(student);
        enrolled_courses.add("Set Filter");
        courses = new JComboBox<>(enrolled_courses.toArray(new String[0]));
        courses.setSelectedItem(enrolled_courses.getLast());
        courses.addActionListener(this);
        courses.setPreferredSize(new Dimension(175,25));
        courses.setBackground(Color.gray);
        JPanel coursespanel = new JPanel();
        coursespanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        coursespanel.add(courses);
        coursespanel.setPreferredSize(new Dimension(100,50));
        coursespanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));
        coursespanel.setBackground(Color.lightGray);
        coursespanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.add(coursespanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()== courses){
            if(courses.getItemAt(courses.getSelectedIndex()).equals( "Set Filter")){
                updateTodo(false);
            }
            else
                updateTodo(true);
        }
    }

    private void updateTodo(boolean filtered) {
        ResultSet classes;
        mainPanel.removeAll();
        databaseManager.createConnection();
        Calendar calendar = Calendar.getInstance();


        // Format the date as needed (e.g., "DD MM/YYYY")
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based in Calendar
        int year = calendar.get(Calendar.YEAR);
        classes = databaseManager.getUpcomingClasses(student, day,month,year);
        int taskcount =0;

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Vertical layout for class panels

        try {
            while (classes.next()) { // Iterate through ResultSet
               if((!filtered || databaseManager.getC_Code((String) courses.getSelectedItem()).equals(classes.getString(4)))){
                JPanel classPanel = new JPanel();
               // classesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                classPanel.setBackground(Color.lightGray);
                classPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                classPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                classPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,25));
                // Add class details to the panel
                classPanel.add(new JLabel("Attend " + classes.getString(1) + " Class at " + classes.getString(2) + " " + classes.getString(3)));
                mainPanel.add(classPanel); // Add class panel to mainPanel
                mainPanel.add(Box.createVerticalStrut(5));
                   taskcount++;
               }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseManager.closeConnection(); // Ensure connection is closed
        }

        databaseManager.createConnection();
        ResultSet assignments = databaseManager.getUpcomingAssignments(student, day ,month,year);


        try {
            while (assignments.next()) { // Iterate through ResultSet
                if(!filtered || assignments.getString(4).equals((String) courses.getSelectedItem())){
                JPanel assignmentsPanel = new JPanel();
                // classesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                assignmentsPanel.setBackground(Color.lightGray);
                assignmentsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                assignmentsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                assignmentsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,25));
                // Add class details to the panel
                assignmentsPanel.add(new JLabel("Submit Assignment : \"" + assignments.getString(1) + "\" Before " + assignments.getString(2) + " " + assignments.getString(3)));
                mainPanel.add(assignmentsPanel); // Add class panel to mainPanel
                mainPanel.add(Box.createVerticalStrut(5));
                taskcount++;
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseManager.closeConnection(); // Ensure connection is closed
        }

        if(taskcount ==0){
            JPanel emptyPanel = new JPanel();
            // classesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            emptyPanel.setBackground(Color.lightGray);
            emptyPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            emptyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            emptyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,25));
            // Add class details to the panel
            emptyPanel.add(new JLabel("Woo hoo there is nothing to do today!!!!"));
            mainPanel.add(emptyPanel); // Add class panel to mainPanel
            mainPanel.add(Box.createVerticalStrut(5));
        }

// Update scroll pane with the new mainPanel



        mainPanel.revalidate();
        mainPanel.repaint();

    }
}


public class AppFrame extends JFrame implements ActionListener {
    private final Student student;
    private final DatabaseManager databaseManager;
private final JPanel leftPanel;
private final JPanel titlepanel;
private final JLabel title;
 private final JPanel mainPanel;
 private final JSplitPane horizontalsplit;
 private final JSplitPane splitPane;
 private final JButton overview;
 private final JButton courses;
 private final JButton assignments;
 private final JButton scheduler;
 private final JButton toDo;
    private ImageIcon logoImage;
    AppFrame(Student student,DatabaseManager databaseManager){
        logoImage = new ImageIcon("./Assets/IIU_Logo.png");
        this.setIconImage(logoImage.getImage());

        this.databaseManager =databaseManager;
        this.student=student;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
      //  setLayout(null);
        setSize(900, 600);
         leftPanel = new JPanel();
        leftPanel.setBackground(Color.darkGray);
        leftPanel.setLayout(null);
         titlepanel = new JPanel();
        titlepanel.setBackground(Color.darkGray);
        title = new JLabel();
        title.setForeground(Color.white);
        titlepanel.add(title);
       mainPanel = new JPanel();
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setLayout(new BorderLayout());
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel,BoxLayout.Y_AXIS));
        JLabel welcome = new JLabel("Welcome " +student.getName()+"! ",SwingConstants.CENTER);
        JLabel welcome2 = new JLabel("To the IIUI Helper App ",SwingConstants.CENTER);

        welcome.setFont(new Font("Arial", Font.BOLD, 36));
        welcome2.setFont(new Font("Arial", Font.BOLD, 36));

        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcome2.setAlignmentX(Component.CENTER_ALIGNMENT);


        welcomePanel.add(welcome);
        welcomePanel.add(welcome2);
        mainPanel.add(welcomePanel,BorderLayout.CENTER);
        horizontalsplit= new JSplitPane(JSplitPane.VERTICAL_SPLIT,titlepanel,mainPanel);
        horizontalsplit.setDividerSize(1);
        horizontalsplit.setDividerLocation(50);
        horizontalsplit.setEnabled(false);
        // Create a JSplitPane with a vertical divider
         splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, horizontalsplit);
        splitPane.setDividerSize(1);
        // Set the initial divider location
        splitPane.setDividerLocation(200);

        // Add the split pane to the frame
        splitPane.setEnabled(false);
        this.add(splitPane);

        overview = new JButton();
        overview.addActionListener(this);
        overview.setBackground(Color.green);
        overview.setBounds(0,0,200,50);
        overview.setText("Overview");
        leftPanel.add(overview);
        courses = new JButton();
        courses.addActionListener(this);
        courses.setBackground(Color.green);
        courses.setBounds(0,50,200,50);
        courses.setText("Courses");
        leftPanel.add(courses);
        assignments =new JButton("Assignments");
        assignments.addActionListener(this);
        assignments.setBackground(Color.green);
        assignments.setBounds(0,100,200,50);
        leftPanel.add(assignments);

        scheduler =new JButton("Scheduler");
        scheduler.addActionListener(this);
        scheduler.setBackground(Color.green);
        scheduler.setBounds(0,150,200,50);
        leftPanel.add(scheduler);

        toDo =new JButton("To Do");
       toDo.addActionListener(this);
        toDo.setBackground(Color.green);
        toDo.setBounds(0,200,200,50);
        leftPanel.add(toDo);




        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == overview){
            databaseManager.updateStudent(student);
            OverVeiwPanel overVeiwPanel = new OverVeiwPanel(student);
            horizontalsplit.setRightComponent(overVeiwPanel);
            title.setText("Overview");

            horizontalsplit.revalidate();
            horizontalsplit.setDividerLocation(50);
            horizontalsplit.repaint();
                    }
        else if(e.getSource() ==courses){
            CoursesPanel coursesPanel = new CoursesPanel(databaseManager,student);
            horizontalsplit.setRightComponent(coursesPanel);
            title.setText("Courses");

            horizontalsplit.revalidate();
            horizontalsplit.setDividerLocation(50);
            horizontalsplit.repaint();
        }
        else  if(e.getSource()==assignments){
            AssignmentsPanel assignmentsPanel = new AssignmentsPanel(databaseManager,student);
            horizontalsplit.setRightComponent(assignmentsPanel);
            title.setText("Assignments");

            horizontalsplit.revalidate();
            horizontalsplit.setDividerLocation(50);
            horizontalsplit.repaint();
        }
        else  if (e.getSource() == scheduler){
            SchedulerPanel schedulerPanel = new SchedulerPanel(databaseManager,student);
            horizontalsplit.setRightComponent(schedulerPanel);
            title.setText("Scheduler");
            horizontalsplit.revalidate();
            horizontalsplit.setDividerLocation(50);
            horizontalsplit.repaint();
        }

        else  if (e.getSource() == toDo){
             ToDoPanel toDoPanel = new ToDoPanel(databaseManager,student);
            horizontalsplit.setRightComponent(toDoPanel);
            title.setText("To Do");
            horizontalsplit.revalidate();
            horizontalsplit.setDividerLocation(50);
            horizontalsplit.repaint();
        }


    }
}
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
        setBackground(Color.gray);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value != null ? value.toString() : "Details");
        return this;
    }
}

// Custom editor class to handle button clicks
class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private String label;
    private boolean isPushed;
    private final DatabaseManager databaseManager;

    public ButtonEditor(JCheckBox checkBox,DatabaseManager databaseManager) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
        this.databaseManager =databaseManager;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        label = value != null ? value.toString() : "Details";
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            JTable table = (JTable) button.getParent();
            int row = table.getEditingRow();

            // Retrieve the value from the first column (index 0) of the current row
            String valueAtFirstColumn = (String) table.getValueAt(row, 0);

            // Display the value in the first column
            JOptionPane.showMessageDialog(button,databaseManager.getCourseDetails(valueAtFirstColumn) );
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}