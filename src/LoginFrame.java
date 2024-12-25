import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame implements ActionListener{

    private DatabaseManager databaseManager;
    private ImageIcon devImage;
    private JTextField emailField;
    private JLabel emailLabel;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JButton loginButton;
    private ImageIcon logoImage;
    private JLabel logo;
    private JButton signUp;
    private JButton about;

    LoginFrame(DatabaseManager db){
        databaseManager=db;
        // Set up the JFrame
        setTitle("IIUI Helper Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        emailLabel =new JLabel("Enter University Email:");
        emailLabel.setBounds(150,250,150,50);
        this.add(emailLabel);
        emailField = new JTextField();
        emailField.setBounds(300,250,200,50);
        emailField.setToolTipText("Uni Email");
        emailField.addActionListener(this);
        this.add(emailField);
        passwordLabel =new JLabel("Enter password:");
        passwordLabel.setBounds(150,300,150,50);
        this.add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(300,300,200,50);
        passwordField.setToolTipText("Enter password");
        passwordField.addActionListener(this);
        this.add(passwordField);
        loginButton = new JButton();
        loginButton.setBounds(320,350,150,50);
        loginButton.setText("Login");
        loginButton.setBackground(Color.GREEN);
        loginButton.addActionListener(this);
        this.add(loginButton);

        signUp = new JButton("Not registered?");
        signUp.setBorderPainted(false);
        signUp.setFocusPainted(false);
        signUp.setContentAreaFilled(false);
        signUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUp.setBounds(320,400,150,50);

        // Add ActionListener to handle clicks
        signUp.addActionListener(this);

        this.add(signUp);
        devImage = new ImageIcon("./Assets/IMG_20241202_164340_081.jpg");
        logoImage = new ImageIcon("./Assets/IIU_Logo.png");

        Image scaledImage = logoImage.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        logo = new JLabel(scaledIcon);
        logo.setBounds(270,0,250,250);

        this.add(logo);



        about = new JButton();
        about.setBounds(650,500,100,50);
        about.setText("About");
        about.setBackground(Color.GREEN);
        about.addActionListener(this);
        this.add(about);



        setFocusable(true);
        this.setResizable(false);
        this.setIconImage(logoImage.getImage());
        this.setVisible(true); // Make the frame visible


    }



    public void actionPerformed(ActionEvent e){
        if(e.getSource() ==loginButton ||e.getSource()== emailField ||e.getSource()==passwordField){
            if(emailField.getText().isBlank()==false &&passwordField.getPassword().length !=0){
              if(databaseManager.validateLogin(emailField.getText(),new String(passwordField.getPassword()))){
                  JOptionPane.showMessageDialog(null,"Login successful");
                  Student student = databaseManager.getStudent(emailField.getText());

                  AppFrame appFrame =new AppFrame(student,databaseManager);
                  this.dispose();
              }
              else
                  JOptionPane.showMessageDialog(null,"Invalid email or password");


            }  else {
                JOptionPane.showMessageDialog(null,"Please enter email and password");
            }
    }else if (e.getSource()== signUp) {
            SignUpFrame signUpFrame = new SignUpFrame(databaseManager);

        }
        else if (e.getSource() == about) {
            JFrame aboutFrame = new JFrame();
            aboutFrame.setSize(new Dimension(400,600));
            aboutFrame.setLayout(new BoxLayout(aboutFrame.getContentPane(),BoxLayout.Y_AXIS));
            Image scaledImage = devImage.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
           JLabel devlogo = new JLabel(scaledIcon);
           aboutFrame.add(devlogo);
           JTextArea aboutText = new JTextArea();
           aboutText.setText("Built by Ahmad Farhan of BSCS F22 with Registration number of 4547\n" +
                   "The IIUI Helper app aims to provide an all in one scheduler,course manager and assignment manager\n" +
                   "with dynamic gpa and credit hours calculations.\n" +
                   "The IIUI Helper app mission is to bring convenience to all its users.  ");
           aboutText.setEditable(false);
           aboutFrame.add(aboutText);
           aboutFrame.setVisible(true);
            aboutFrame.setIconImage(logoImage.getImage());
           aboutFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }

    }

}
