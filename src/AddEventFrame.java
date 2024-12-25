import javax.swing.*;
import java.awt.*;

class AddEventFrame extends JFrame {
    private JLabel titlelabel;
    private JTextField e_title;

    private JLabel timeLabel;
    private JComboBox<String> hrs;
    private JComboBox<String> mins;

    private Student student;
    private DatabaseManager databaseManager;
    private String date;
    private JButton confirm;
    private ImageIcon logoImage;


    AddEventFrame(DatabaseManager databaseManager, Student student, String date, SchedulerPanel schedulerPanel){
        this.databaseManager=databaseManager;
        this.student =student;
        this.date = date;

        logoImage = new ImageIcon("./Assets/IIU_Logo.png");
        this.setIconImage(logoImage.getImage());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setSize(450, 300);
        this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
        titlelabel = new JLabel("Enter Event Title:");
        e_title =new JTextField();
        e_title.setPreferredSize(new Dimension(150,25));
        JPanel titlePanel =new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        titlePanel.setPreferredSize(new Dimension(400, 50));

        titlePanel.add(titlelabel);
        titlePanel.add(e_title);
        this.add(titlePanel);
        this.add(Box.createVerticalStrut(5));

        timeLabel = new JLabel("Select Event Time:");
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

        confirm= new JButton("Confirm");
        confirm.addActionListener(e -> {
            if(!e_title.getText().isEmpty()){
                databaseManager.addnewEvent(e_title.getText(),hrs.getSelectedItem()+":"+mins.getSelectedItem(),date,student);
                schedulerPanel.updateUpcomingpanel();
                schedulerPanel.interactiveCalendar.updateCalendar();
                this.dispose();}
            else {
                JOptionPane.showMessageDialog(null,"Must provide a title ");
            }

        });
        this.add(confirm);



        this.setVisible(true);



    }
}
