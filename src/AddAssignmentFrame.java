import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class AddAssignmentFrame extends JFrame {
    private JLabel titlelabel;
    private JTextField a_title;
    private JLabel courseLabel;
    private JComboBox<String> a_course;
    private JLabel desc_label;
    private JTextArea a_desc ;
    private JLabel prioritylabel;
    private JComboBox<String> priority;
    private JLabel timeLabel;
    private JComboBox<String> hrs;
    private JComboBox<String> mins;
    private JLabel dateLabel;
    private InteractiveCalendar selectDate;
    private Student student;
    private DatabaseManager databaseManager;
    private String date;
    private JButton confirm;
    AssignmentsPanel assignmentsPanel;

    private ImageIcon logoImage;


    AddAssignmentFrame(DatabaseManager databaseManager, Student student, AssignmentsPanel assignmentsPanel) {
        this.databaseManager=databaseManager;
        this.student =student;
        logoImage = new ImageIcon("./Assets/IIU_Logo.png");
        this.setIconImage(logoImage.getImage());


        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        //  setLayout(null);
        setSize(450, 700);
        this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
        titlelabel = new JLabel("Enter Assignment Title:");
        a_title=new JTextField();
        a_title.setPreferredSize(new Dimension(150,25));
        JPanel titlePanel =new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        titlePanel.setPreferredSize(new Dimension(400, 50));

        titlePanel.add(titlelabel);
        titlePanel.add(a_title);
        this.add(titlePanel);
        this.add(Box.createVerticalStrut(5));

        courseLabel = new JLabel("Select course:");
        ArrayList<String> courses = databaseManager.getEnrolledCourses(student);
        a_course = new JComboBox<>(courses.toArray(new String[0]));
        a_course.setPreferredSize(new Dimension(150,25));
        JPanel coursePanel =new JPanel();
        coursePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        coursePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        coursePanel.setPreferredSize(new Dimension(400, 50));
        coursePanel.add(courseLabel);
        coursePanel.add(a_course);
        this.add(coursePanel);
        this.add(Box.createVerticalStrut(5));

        desc_label = new JLabel("Enter Assignment Description:");
        a_desc = new JTextArea(5, 30); // Set rows and columns for a more suitable size
        a_desc.setLineWrap(true);
        a_desc.setWrapStyleWord(true);
        a_desc.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Add a border for better visibility

        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
        descPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Adjust height for better size
        descPanel.setPreferredSize(new Dimension(400, 150));

        desc_label.setAlignmentX(Component.LEFT_ALIGNMENT); // Align label to the left
        a_desc.setAlignmentX(Component.LEFT_ALIGNMENT); // Align text area to the left

        descPanel.add(desc_label);
        descPanel.add(Box.createVerticalStrut(5)); // Add space between label and text area
        descPanel.add(new JScrollPane(a_desc)); // Wrap text area in a scroll pane for better usability

        this.add(descPanel);
        this.add(Box.createVerticalStrut(5));

        prioritylabel = new JLabel("Select priority:");
        String[] priorities ={"Low","Medium","High"};
        priority = new JComboBox<>(priorities);
        priority.setPreferredSize(new Dimension(150,25));
        JPanel priorityPanel =new JPanel();
        priorityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        priorityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        priorityPanel.setPreferredSize(new Dimension(400, 50));
        priorityPanel.add(prioritylabel);
        priorityPanel.add(priority);
        this.add(priorityPanel);
        this.add(Box.createVerticalStrut(5));

        timeLabel = new JLabel("Select Due Time:");
        String[] hours = new String[24];
        for (int i =0;i<24;i++){
            hours[i]= String.valueOf(i+1);
        }
        String[] minutes = new String[59];
        for (int i =0;i<59;i++){
            minutes[i]= String.valueOf(i+1);
        }
        hrs=new JComboBox<>(hours);
        mins=new JComboBox<>(minutes);
        hrs.setPreferredSize(new Dimension(75,25));
        mins.setPreferredSize(new Dimension(75,25));
        JPanel timePanel =new JPanel();
        timePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        timePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        timePanel.setPreferredSize(new Dimension(400, 50));
        timePanel.add(timeLabel);
        timePanel.add(hrs);
        timePanel.add(new JLabel(":"));
        timePanel.add(mins);
        this.add(timePanel);
        this.add(Box.createVerticalStrut(5));
        dateLabel = new JLabel("Select a date:");
       JPanel datepanel = new JPanel();
       datepanel.setLayout(new FlowLayout(FlowLayout.LEFT));
       datepanel.add(dateLabel);
        selectDate = new InteractiveCalendar(databaseManager,student);
        selectDate.setDayClickListener((day, month, year) -> {
           date= day + " " + month + "/" + year;
           dateLabel.setText("Select a date: "+ date);
        });
        this.add(datepanel);
        this.add(selectDate);

        confirm= new JButton("Confirm");
        confirm.addActionListener(e -> {
            if(!a_title.getText().isEmpty() && !date.isEmpty()){
            databaseManager.addNewAssignment(student.getRegnum(),a_title.getText(),(String)a_course.getSelectedItem(),a_desc.getText(),(String)priority.getSelectedItem(),hrs.getSelectedItem()+":"+mins.getSelectedItem(),date);
            assignmentsPanel.updateAssignments();
            this.dispose();}
            else {
                JOptionPane.showMessageDialog(null,"Must provide a title and date at minimum ");
            }

        });
        confirm.setBackground(Color.GREEN);
        this.add(confirm);



        this.setVisible(true);



    }
}

