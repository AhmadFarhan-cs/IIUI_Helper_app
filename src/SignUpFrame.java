import javax.swing.*;
import java.awt.*;

public class SignUpFrame extends JFrame {
    private JLabel regLabel;
    private JTextField reg_num;
    private JLabel nameLabel;
    private JTextField name;
    private JLabel emailLabel;
    private JTextField email;
    private JLabel passwordLabel;
    private JPasswordField password;
    private JButton signup;
    private DatabaseManager databaseManager;
    private ImageIcon logoImage;

    SignUpFrame(DatabaseManager databaseManager) {
        // Set frame properties
        logoImage = new ImageIcon("./Assets/IIU_Logo.png");
        this.setIconImage(logoImage.getImage());
        this.databaseManager = databaseManager;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(450, 500);
        setTitle("Sign Up");

        // Main container layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.add(mainPanel);

        // Common font for labels and fields
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // Registration number panel
        regLabel = new JLabel("Enter your Registration Number:");
        regLabel.setFont(labelFont);
        reg_num = new JTextField();
        reg_num.setFont(fieldFont);
        reg_num.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(createInputPanel(regLabel, reg_num));

        // Name panel
        nameLabel = new JLabel("Enter your Name:");
        nameLabel.setFont(labelFont);
        name = new JTextField();
        name.setFont(fieldFont);
        name.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(createInputPanel(nameLabel, name));

        // Email panel
        emailLabel = new JLabel("Enter your Email:");
        emailLabel.setFont(labelFont);
        email = new JTextField();
        email.setFont(fieldFont);
        email.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(createInputPanel(emailLabel, email));

        // Password panel
        passwordLabel = new JLabel("Enter Password:");
        passwordLabel.setFont(labelFont);
        password = new JPasswordField();
        password.setFont(fieldFont);
        password.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(createInputPanel(passwordLabel, password));

        // Sign-up button
        signup = new JButton("Sign Up");
        signup.setFont(new Font("Arial", Font.BOLD, 14));
        signup.setBackground(new Color(70, 130, 180));
        signup.setForeground(Color.WHITE);
        signup.setFocusPainted(false);
        signup.setPreferredSize(new Dimension(120, 40));
        signup.addActionListener(e -> {
            if (!reg_num.getText().isBlank() && !name.getText().isBlank() && !email.getText().isBlank() && password.getPassword().length > 0) {
                try {
                    int reg = Integer.parseInt(reg_num.getText());
                    if (databaseManager.addnewStudent(reg, name.getText(), email.getText(), new String(password.getPassword()))) {
                        JOptionPane.showMessageDialog(null, "Registration successful");
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration number or Email already taken!");
                    }
                } catch (NumberFormatException n) {
                    JOptionPane.showMessageDialog(null, "Registration number should be numbers only");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please fill every field");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(signup);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        this.setVisible(true);
    }

    private JPanel createInputPanel(JLabel label, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.add(label);
        panel.add(textField);
        return panel;
    }

    public static void main(String[] args) {
        // Dummy DatabaseManager for testing
        DatabaseManager dbManager = new DatabaseManager();
        new SignUpFrame(dbManager);
    }
}
