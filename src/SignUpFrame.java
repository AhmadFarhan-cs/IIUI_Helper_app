
import javax.swing.*;
import java.awt.*;

public class SignUpFrame extends JFrame {
    private JLabel regLabel;
    private JTextField reg_num;
    private JLabel nameLabel;
    private JTextField name ;
    private JLabel emailLabel;
    private JTextField email;
    private JLabel passwordLabel;
    private JPasswordField password ;
    private JButton signup;
    private DatabaseManager databaseManager;
    private ImageIcon logoImage;
    SignUpFrame(DatabaseManager databaseManager){

        logoImage = new ImageIcon("./Assets/IIU_Logo.png");
        this.setIconImage(logoImage.getImage());

        this.databaseManager=databaseManager;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setSize(450, 500);
        this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));

        regLabel = new JLabel("Enter your Registration Number:");
        reg_num =new JTextField();
        reg_num.setPreferredSize(new Dimension(150,25));
        JPanel regPanel =new JPanel();
        regPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        regPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        regPanel.setPreferredSize(new Dimension(400, 50));

        regPanel.add(regLabel);
        regPanel.add(reg_num);
        this.add(regPanel);
        this.add(Box.createVerticalStrut(5));

        nameLabel = new JLabel("Enter your name:");
        name=new JTextField();
        name.setPreferredSize(new Dimension(150,25));
        JPanel namePanel =new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        namePanel.setPreferredSize(new Dimension(400, 50));

        namePanel.add(nameLabel);
        namePanel.add(name);
        this.add(namePanel);
        this.add(Box.createVerticalStrut(5));


        emailLabel = new JLabel("Enter Email:");
        email =new JTextField();
        email.setPreferredSize(new Dimension(150,25));
        JPanel emailPanel =new JPanel();
        emailPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        emailPanel.setPreferredSize(new Dimension(400, 50));

        emailPanel.add(emailLabel);
        emailPanel.add(email);
        this.add(emailPanel);
        this.add(Box.createVerticalStrut(5));


        passwordLabel = new JLabel("Enter password:");
        password =new JPasswordField();
        password.setPreferredSize(new Dimension(150,25));
        JPanel passPanel =new JPanel();
        passPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        passPanel.setPreferredSize(new Dimension(400, 50));

        passPanel.add(passwordLabel);
        passPanel.add(password);
        this.add(passPanel);
        this.add(Box.createVerticalStrut(5));

        signup =new JButton("Sign up");
        signup.addActionListener(e->{
            if(!reg_num.getText().isBlank()&& !name.getText().isBlank()&&!email.getText().isBlank()&& password.getPassword().length>0){

                try {
                    int reg = Integer.parseInt( reg_num.getText());
                    if(  databaseManager.addnewStudent(reg,name.getText(),email.getText(),new String( password.getPassword()))){
                        JOptionPane.showMessageDialog(null,"Registration successful");
                        this.dispose();}
                    else {
                        JOptionPane.showMessageDialog(null,"Registration number or Email already taken!");
                    }

                }catch (NumberFormatException n){
                    JOptionPane.showMessageDialog(null,"Registration number should be numbers only");
                }





            }
            else
                JOptionPane.showMessageDialog(null,"Please fill every field");
        });
        this.add(signup);
        signup.setBackground(Color.GREEN);
        this.setVisible(true);


    }


}