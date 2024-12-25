import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;



class InteractiveCalendar extends JPanel implements ActionListener {
    DatabaseManager databaseManager;
    Student student;
    private JPanel calendarPanel;
    private JLabel monthYearLabel;
    private JButton[][] dayButtons;
    private int currentMonth, currentYear;
    private  DayClickListener dayClickListener;

    public InteractiveCalendar(DatabaseManager databaseManager,Student student) {
        this.databaseManager=databaseManager;
        this.student =student;
        //this.setSize(250,500);

        // Initialize components
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        calendarPanel = new JPanel(new GridLayout(7, 7));
        dayButtons = new JButton[6][7]; // 6 weeks, 7 days

        // Create navigation panel
        JPanel navPanel = new JPanel(new BorderLayout());
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        nextButton.setBackground(Color.gray);
        prevButton.setBackground(Color.gray);
        // Add listeners to navigation buttons
        prevButton.addActionListener(e -> changeMonth(-1));
        nextButton.addActionListener(e -> changeMonth(1));

        navPanel.add(prevButton, BorderLayout.WEST);
        navPanel.add(monthYearLabel, BorderLayout.CENTER);
        navPanel.add(nextButton, BorderLayout.EAST);


        this.setLayout(new BorderLayout());
        this.add(navPanel, BorderLayout.NORTH);
        this.add(calendarPanel, BorderLayout.CENTER);


        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);


        buildCalendarGrid();

        // Display current month
        updateCalendar();


    }
    public interface DayClickListener {
        void onDayClick(int day, int month, int year);
    }
    public void setDayClickListener(DayClickListener listener) {
        this.dayClickListener = listener;
    }
    private void buildCalendarGrid() {
        // Add headers for days of the week
        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String header : headers) {
            JLabel label = new JLabel(header, SwingConstants.CENTER);
            calendarPanel.add(label);
        }

        // Add buttons for each day
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                JButton dayButton = new JButton();

                dayButton.setFocusPainted(false);
                dayButtons[i][j] = dayButton;

                // Add listener for day buttons
                dayButton.addActionListener(e -> {
                    if (dayClickListener != null) {
                        JButton clickedButton = (JButton) e.getSource();
                        String dayText = clickedButton.getText();
                        if (!dayText.isEmpty()) {
                            int day = Integer.parseInt(dayText);
                            dayClickListener.onDayClick(day, currentMonth + 1, currentYear);
                }}});

                calendarPanel.add(dayButton);
            }
        }
    }

    public void updateCalendar() {
        // Clear buttons
        for (JButton[] row : dayButtons) {
            for (JButton btn : row) {
                btn.setText("");
                btn.setEnabled(false);
            }
        }

        // Set month and year label
        monthYearLabel.setText(new java.text.SimpleDateFormat("MMMM yyyy").format(new GregorianCalendar(currentYear, currentMonth, 1).getTime()));

        // Get the first day of the month and the number of days in the month
        Calendar calendar = new GregorianCalendar(currentYear, currentMonth, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Populate the calendar grid

        int row = 0, col = firstDayOfWeek;
        for (int r = 0; r < dayButtons.length; r++) {
            for (int c = 0; c < dayButtons[r].length; c++) {
                // Reset button backgrounds
                dayButtons[r][c].setBackground(null);
            }
        }

        databaseManager.createConnection();
        for (int day = 1; day <= daysInMonth; day++) {
            dayButtons[row][col].setText(String.valueOf(day));
            dayButtons[row][col].setEnabled(true);
            if (currentYear == Calendar.getInstance().get(Calendar.YEAR) && currentMonth == Calendar.getInstance().get(Calendar.MONTH) && day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
                dayButtons[row][col].setBackground(Color.GREEN);
            }
            else if(databaseManager.hasEvent(day+" " + (currentMonth + 1) + "/" + currentYear,student)){
                dayButtons[row][col].setBackground(Color.RED);
            }
            else {
                dayButtons[row][col].setBackground(Color.gray);
            }
            col++;
            if (col > 6) { // Move to the next row if column exceeds Saturday
                col = 0;
                row++;
            }
        }
        databaseManager.closeConnection();
    }

    private void changeMonth(int delta) {
        currentMonth += delta;
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        } else if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }
        updateCalendar();
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }



}
