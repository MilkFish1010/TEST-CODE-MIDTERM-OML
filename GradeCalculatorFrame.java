import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GradeCalculatorFrame extends JFrame {

    // Global notification label
    private JLabel notificationLabel;

    // ----------------- Lecture Tab Components -----------------
    private JTextField lecExamField, lecEssayField, lecPvmField, lecJavaField, lecJsField;
    private JCheckBox lecAttendanceJan, lecAttendanceFeb3, lecAttendanceFeb10, lecAttendanceFeb17;
    private JCheckBox lecShowSteps;
    // Use a JTextPane (HTML) for output
    private JTextPane lecStepPane;
    private JLabel lecErrorLabel;

    // ----------------- Lab Tab Components -----------------
    private JTextField labJava1Field, labJava2Field, labJs1Field, labJs2Field;
    private JTextField labMp1Field, labMp2Field, labMp3Field, labMp3dField;
    private JCheckBox labAttendanceJan, labAttendanceFeb3, labAttendanceFeb10, labAttendanceFeb17;
    private JCheckBox labShowSteps;
    // Use a JTextPane for HTML output
    private JTextPane labStepPane;
    private JLabel labErrorLabel;

    public GradeCalculatorFrame() {
        setTitle("Programming 2 Prelim Grade Calculator 2025");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Start full screen and set minimum size
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 700));

        // Create main container with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 20, 20)); // dark background

        // Header
        JLabel headerLabel = new JLabel("😋 Programming 2 Prelim Grade Calculator 2025 😋", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 22));
        headerLabel.setForeground(new Color(240, 255, 240));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(51, 119, 48));
        headerLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Notification label
        notificationLabel = new JLabel("", SwingConstants.CENTER);
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        notificationLabel.setForeground(Color.WHITE);
        notificationLabel.setOpaque(true);
        notificationLabel.setBackground(new Color(51, 119, 48));
        notificationLabel.setVisible(false);
        mainPanel.add(notificationLabel, BorderLayout.SOUTH);

        // Tabbed Pane for Lecture and Lab Calculators
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Lecture Calculator", createLecturePanel());
        tabbedPane.addTab("Lab Calculator", createLabPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    // ---------------------- Lecture Panel -----------------------
    private JPanel createLecturePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(220, 220, 220));

        // Main content panel using BoxLayout for vertical stacking
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // CSV upload/download panel
        JPanel csvPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        csvPanel.setOpaque(false);
        JButton uploadBtn = new JButton("🔼 Upload CSV");
        uploadBtn.addActionListener(e -> uploadLectureCSV());
        JButton downloadBtn = new JButton("🔽 Download CSV");
        downloadBtn.addActionListener(e -> downloadLectureCSV());
        csvPanel.add(uploadBtn);
        csvPanel.add(downloadBtn);
        csvPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(csvPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Random generator button
        JButton randomBtn = new JButton("🎲 Random Grade Generator");
        randomBtn.addActionListener(e -> setRandomValidLecture());
        randomBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(randomBtn);
        contentPanel.add(Box.createVerticalStrut(10));

        // Input fields with Perfect Score buttons
        lecExamField = createLabeledField(contentPanel, "Exam Score (0 - 100):", 100);
        lecEssayField = createLabeledField(contentPanel, "Essay Score (0 - 100):", 100);
        lecPvmField   = createLabeledField(contentPanel, "PVM Score (0 - 60):", 60);
        lecJavaField  = createLabeledField(contentPanel, "Java Score (0 - 40):", 40);
        lecJsField    = createLabeledField(contentPanel, "JavaScript Score (0 - 40):", 40);

        // Attendance checkboxes
        JPanel attendancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        attendancePanel.setOpaque(false);
        attendancePanel.setBorder(new TitledBorder("Attendance (mark dates attended)"));
        lecAttendanceJan = new JCheckBox("Jan 27", true);
        lecAttendanceFeb3 = new JCheckBox("Feb 3", true);
        lecAttendanceFeb10 = new JCheckBox("Feb 10", true);
        lecAttendanceFeb17 = new JCheckBox("Feb 17", true);
        attendancePanel.add(lecAttendanceJan);
        attendancePanel.add(lecAttendanceFeb3);
        attendancePanel.add(lecAttendanceFeb10);
        attendancePanel.add(lecAttendanceFeb17);
        attendancePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(attendancePanel);

        // Show Steps checkbox
        lecShowSteps = new JCheckBox("Show Step-by-Step Calculation", true);
        lecShowSteps.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lecShowSteps);
        contentPanel.add(Box.createVerticalStrut(10));

        // Buttons for perfect score, calculate, reset
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        JButton perfectBtn = new JButton("🌟 I aced this course!");
        perfectBtn.addActionListener(e -> perfectLectureGrade());
        JButton calculateBtn = new JButton("Calculate Lecture Grade");
        calculateBtn.addActionListener(e -> calculateLectureGrade());
        JButton resetBtn = new JButton("🔁 Reset Grades");
        resetBtn.addActionListener(e -> resetLectureGrades());
        buttonPanel.add(perfectBtn);
        buttonPanel.add(calculateBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(buttonPanel);

        // Error/Result label
        lecErrorLabel = new JLabel(" ");
        lecErrorLabel.setForeground(Color.RED);
        lecErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(lecErrorLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Use a JTextPane for HTML output
        lecStepPane = new JTextPane();
        lecStepPane.setContentType("text/html");
        lecStepPane.setEditable(false);
        lecStepPane.setText("<html><body style='font-size:18pt; font-family: monospace;'></body></html>");
        JScrollPane stepScroll = new JScrollPane(lecStepPane);
        stepScroll.setBorder(new TitledBorder("Calculation Steps"));
        stepScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(stepScroll);

        contentPanel.add(Box.createVerticalGlue());
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    // ---------------------- Lab Panel -----------------------
    private JPanel createLabPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(220, 220, 220));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // CSV upload/download panel for Lab
        JPanel csvPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        csvPanel.setOpaque(false);
        JButton uploadBtn = new JButton("Upload CSV");
        uploadBtn.addActionListener(e -> uploadLabCSV());
        JButton downloadBtn = new JButton("Download CSV");
        downloadBtn.addActionListener(e -> downloadLabCSV());
        csvPanel.add(uploadBtn);
        csvPanel.add(downloadBtn);
        csvPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(csvPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Random generator button
        JButton randomBtn = new JButton("🎲 Random Grade Generator");
        randomBtn.addActionListener(e -> setRandomValidLab());
        randomBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(randomBtn);
        contentPanel.add(Box.createVerticalStrut(10));

        // Input fields for exams
        labJava1Field = createLabeledField(contentPanel, "Java 1 (0 - 100):", 100);
        labJava2Field = createLabeledField(contentPanel, "Java 2 (0 - 100):", 100);
        labJs1Field   = createLabeledField(contentPanel, "JavaScript 1 (0 - 100):", 100);
        labJs2Field   = createLabeledField(contentPanel, "JavaScript 2 (0 - 100):", 100);

        // Input fields for lab works
        labMp1Field = createLabeledField(contentPanel, "MP1 (0 - 100):", 100);
        labMp2Field = createLabeledField(contentPanel, "MP2 (0 - 100):", 100);
        labMp3Field = createLabeledField(contentPanel, "MP3 (0 - 100):", 100);
        labMp3dField = createLabeledField(contentPanel, "MP3 (Documentation) (0 - 100):", 100);

        // Attendance checkboxes for Lab
        JPanel attendancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        attendancePanel.setOpaque(false);
        attendancePanel.setBorder(new TitledBorder("Attendance (mark dates attended)"));
        labAttendanceJan = new JCheckBox("Jan 27", true);
        labAttendanceFeb3 = new JCheckBox("Feb 3", true);
        labAttendanceFeb10 = new JCheckBox("Feb 10", true);
        labAttendanceFeb17 = new JCheckBox("Feb 17", true);
        attendancePanel.add(labAttendanceJan);
        attendancePanel.add(labAttendanceFeb3);
        attendancePanel.add(labAttendanceFeb10);
        attendancePanel.add(labAttendanceFeb17);
        attendancePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(attendancePanel);

        // Show Steps checkbox
        labShowSteps = new JCheckBox("Show Step-by-Step Calculation", true);
        labShowSteps.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(labShowSteps);
        contentPanel.add(Box.createVerticalStrut(10));

        // Buttons for perfect grade, calculate, reset
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        JButton perfectBtn = new JButton("🌟 I aced this course!");
        perfectBtn.addActionListener(e -> perfectLabGrade());
        JButton calculateBtn = new JButton("🧮 Calculate Lab Grade");
        calculateBtn.addActionListener(e -> calculateLabGrade());
        JButton resetBtn = new JButton("🔁 Reset Grades");
        resetBtn.addActionListener(e -> resetLabGrades());
        buttonPanel.add(perfectBtn);
        buttonPanel.add(calculateBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(buttonPanel);

        // Error/Result label for lab
        labErrorLabel = new JLabel(" ");
        labErrorLabel.setForeground(Color.RED);
        labErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(labErrorLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Use a JTextPane for HTML output for lab
        labStepPane = new JTextPane();
        labStepPane.setContentType("text/html");
        labStepPane.setEditable(false);
        labStepPane.setText("<html><body style='font-size:18pt; font-family: monospace;'></body></html>");
        JScrollPane stepScroll = new JScrollPane(labStepPane);
        stepScroll.setBorder(new TitledBorder("Calculation Steps"));
        stepScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(stepScroll);

        contentPanel.add(Box.createVerticalGlue());
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    // ------------------ Utility Methods --------------------
    // Create a panel with a label, text field and a "Perfect Score" button.
    private JTextField createLabeledField(JPanel parent, String labelText, int maxScore) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        JTextField textField = new JTextField(5);
        JButton perfectBtn = new JButton("Perfect");
        perfectBtn.addActionListener(e -> textField.setText(String.valueOf(maxScore)));
        panel.add(label);
        panel.add(textField);
        panel.add(perfectBtn);
        parent.add(panel);
        return textField;
    }

    // Show a temporary notification message
    private void showNotification(String message) {
        notificationLabel.setText(message);
        notificationLabel.setVisible(true);
        javax.swing.Timer timer = new javax.swing.Timer(3000, e -> notificationLabel.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }

    // ----------------- Lecture Functionality -----------------
    private List<String> validateLecture() {
        List<String> errors = new ArrayList<>();
        Object[][] fields = {
            { lecExamField, "Exam Score", 0, 100 },
            { lecEssayField, "Essay Score", 0, 100 },
            { lecPvmField,   "PVM Score", 0, 60 },
            { lecJavaField,  "Java Score", 0, 40 },
            { lecJsField,    "JavaScript Score", 0, 40 }
        };
        for (Object[] fieldInfo : fields) {
            JTextField field = (JTextField) fieldInfo[0];
            String label = (String) fieldInfo[1];
            int min = (int) fieldInfo[2];
            int max = (int) fieldInfo[3];
            String text = field.getText().trim();
            if (text.isEmpty()) {
                errors.add(label + " is empty.");
            } else {
                try {
                    double val = Double.parseDouble(text);
                    if (val < min || val > max) {
                        errors.add(label + " must be between " + min + " and " + max + ".");
                    }
                } catch (NumberFormatException ex) {
                    errors.add(label + " is not a valid number.");
                }
            }
        }
        return errors;
    }

    private void calculateLectureGrade() {
        lecErrorLabel.setText(" ");
        lecStepPane.setText("<html><body style='font-size:18pt; font-family: monospace;'></body></html>");

        List<String> errors = validateLecture();
        if (!errors.isEmpty()) {
            StringBuilder errMsg = new StringBuilder("<html>");
            for (String err : errors) {
                errMsg.append(err).append("<br>");
            }
            errMsg.append("</html>");
            lecErrorLabel.setText(errMsg.toString());
            return;
        }

        int absences = 0;
        if (!lecAttendanceJan.isSelected()) absences++;
        if (!lecAttendanceFeb3.isSelected()) absences++;
        if (!lecAttendanceFeb10.isSelected()) absences++;
        if (!lecAttendanceFeb17.isSelected()) absences++;
        if (absences >= 4) {
            lecErrorLabel.setText("FAIL: Absences are 4 or more.");
            return;
        }

        double exam = Double.parseDouble(lecExamField.getText().trim());
        double essay = Double.parseDouble(lecEssayField.getText().trim());
        double pvm   = Double.parseDouble(lecPvmField.getText().trim());
        double java  = Double.parseDouble(lecJavaField.getText().trim());
        double js    = Double.parseDouble(lecJsField.getText().trim());

        double essayPercent = essay; 
        double pvmPercent   = (pvm / 60.0) * 100;
        double javaPercent  = (java / 40.0) * 100;
        double jsPercent    = (js / 40.0) * 100;
        double quizSum = essayPercent + pvmPercent + javaPercent + jsPercent;
        double avgQuizzes   = quizSum / 4.0;
        double attendance   = Math.max(100 - (absences * 10), 0);
        double prelimClassStanding = (avgQuizzes * 0.60) + (attendance * 0.40);
        double prelimGrade = (exam * 0.60) + (prelimClassStanding * 0.40);

        // Create a stamp: if grade >=75 then PASSED (green), otherwise FAILED (red)
        String stamp;
        if (prelimGrade >= 75) {
            stamp = "<span style='display:inline-block; margin-left:10px; font-weight:bold; color:#28a745; border:2px solid #28a745; border-radius:5px; padding:5px;'>PASSED</span>";
        } else {
            stamp = "<span style='display:inline-block; margin-left:10px; font-weight:bold; color:#dc3545; border:2px solid #dc3545; border-radius:5px; padding:5px;'>FAILED</span>";
        }

        // Build detailed HTML output
        StringBuilder html = new StringBuilder("<html><body style='font-size:18pt; font-family: monospace;'>");
        html.append("<b>Quiz Calculations:</b><br>");
        html.append("Essay: (").append(essay).append("/100)*100 = ").append(String.format("%.2f", essayPercent)).append("%<br>");
        html.append("PVM: (").append(pvm).append("/60)*100 = ").append(String.format("%.2f", pvmPercent)).append("%<br>");
        html.append("Java: (").append(java).append("/40)*100 = ").append(String.format("%.2f", javaPercent)).append("%<br>");
        html.append("JS: (").append(js).append("/40)*100 = ").append(String.format("%.2f", jsPercent)).append("%<br>");
        html.append("Total Quiz Percentage = ").append(String.format("%.2f", quizSum)).append("%<br>");
        html.append("Average Quizzes = Total / 4 = ").append(String.format("%.2f", avgQuizzes)).append("%<br><br>");

        html.append("<b>Attendance Calculation:</b><br>");
        html.append("Attendance = 100 - (").append(absences).append(" * 10) = ").append(String.format("%.2f", attendance)).append("%<br><br>");

        html.append("<b>Prelim Class Standing:</b><br>");
        html.append("= (Average Quizzes * 0.60) + (Attendance * 0.40)<br>");
        html.append("= (").append(String.format("%.2f", avgQuizzes)).append(" * 0.60) + (").append(String.format("%.2f", attendance))
            .append(" * 0.40) = ").append(String.format("%.2f", prelimClassStanding)).append("%<br><br>");

        html.append("<b>Final Lecture Grade:</b><br>");
        html.append("= (Exam * 0.60) + (Prelim Class Standing * 0.40)<br>");
        html.append("= (").append(exam).append(" * 0.60) + (").append(String.format("%.2f", prelimClassStanding))
            .append(" * 0.40) = ").append(String.format("%.2f", prelimGrade)).append("%<br>");
        if (prelimGrade % 1 != 0) {
            int roundUp = (int) Math.ceil(prelimGrade);
            int roundDown = (int) Math.floor(prelimGrade);
            html.append("Rounded Up: ").append(roundUp).append(", Rounded Down: ").append(roundDown).append("<br>");
        }
        // Append the stamp
        html.append(stamp);
        html.append("</body></html>");
        lecStepPane.setText(html.toString());
        showNotification("calculate time!🤑");
    }

    private void setRandomValidLecture() {
        Random rand = new Random();
        lecExamField.setText(String.valueOf(rand.nextInt(101)));
        lecEssayField.setText(String.valueOf(rand.nextInt(101)));
        lecPvmField.setText(String.valueOf(rand.nextInt(61)));
        lecJavaField.setText(String.valueOf(rand.nextInt(41)));
        lecJsField.setText(String.valueOf(rand.nextInt(41)));

        List<JCheckBox> boxes = Arrays.asList(lecAttendanceJan, lecAttendanceFeb3, lecAttendanceFeb10, lecAttendanceFeb17);
        Collections.shuffle(boxes);
        int absences = rand.nextInt(4);
        for (int i = 0; i < boxes.size(); i++) {
            boxes.get(i).setSelected(i >= absences);
        }
    }

    private void perfectLectureGrade() {
        lecExamField.setText("100");
        lecEssayField.setText("100");
        lecPvmField.setText("60");
        lecJavaField.setText("40");
        lecJsField.setText("40");
        lecAttendanceJan.setSelected(true);
        lecAttendanceFeb3.setSelected(true);
        lecAttendanceFeb10.setSelected(true);
        lecAttendanceFeb17.setSelected(true);
        calculateLectureGrade();
    }

    private void resetLectureGrades() {
        lecExamField.setText("");
        lecEssayField.setText("");
        lecPvmField.setText("");
        lecJavaField.setText("");
        lecJsField.setText("");
        lecAttendanceJan.setSelected(true);
        lecAttendanceFeb3.setSelected(true);
        lecAttendanceFeb10.setSelected(true);
        lecAttendanceFeb17.setSelected(true);
        lecErrorLabel.setText(" ");
        lecStepPane.setText("<html><body style='font-size:18pt; font-family: monospace;'></body></html>");
    }

    private void uploadLectureCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String headerLine = br.readLine();
                String dataLine = br.readLine();
                if (headerLine == null || dataLine == null) {
                    JOptionPane.showMessageDialog(this, "CSV must have header and at least one data row.");
                    return;
                }
                String[] headers = headerLine.split(",");
                String[] data = dataLine.split(",");
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < headers.length && i < data.length; i++) {
                    map.put(headers[i].trim().toLowerCase(), data[i].trim());
                }
                if (!map.containsKey("exam") || !map.containsKey("essay") ||
                    !map.containsKey("pvm") || !map.containsKey("java") ||
                    !map.containsKey("js")) {
                    JOptionPane.showMessageDialog(this, "Missing one or more required headers.");
                    return;
                }
                lecExamField.setText(map.get("exam"));
                lecEssayField.setText(map.get("essay"));
                lecPvmField.setText(map.get("pvm"));
                lecJavaField.setText(map.get("java"));
                lecJsField.setText(map.get("js"));
                calculateLectureGrade();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void downloadLectureCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Lecture CSV");
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            try (PrintWriter pw = new PrintWriter(file)) {
                String headers = "exam,essay,pvm,java,js";
                String row = lecExamField.getText() + "," +
                             lecEssayField.getText() + "," +
                             lecPvmField.getText() + "," +
                             lecJavaField.getText() + "," +
                             lecJsField.getText();
                pw.println(headers);
                pw.println(row);
                JOptionPane.showMessageDialog(this, "Lecture CSV saved successfully.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // ----------------- Lab Functionality -----------------
    private List<String> validateLab() {
        List<String> errors = new ArrayList<>();
        Object[][] fields = {
            { labJava1Field, "Java 1", 0, 100 },
            { labJava2Field, "Java 2", 0, 100 },
            { labJs1Field,   "JS 1", 0, 100 },
            { labJs2Field,   "JS 2", 0, 100 },
            { labMp1Field,   "MP1", 0, 100 },
            { labMp2Field,   "MP2", 0, 100 },
            { labMp3Field,   "MP3", 0, 100 },
            { labMp3dField,  "MP3d", 0, 100 }
        };
        for (Object[] fieldInfo : fields) {
            JTextField field = (JTextField) fieldInfo[0];
            String label = (String) fieldInfo[1];
            int min = (int) fieldInfo[2];
            int max = (int) fieldInfo[3];
            String text = field.getText().trim();
            if (text.isEmpty()) {
                errors.add(label + " is empty.");
            } else {
                try {
                    double val = Double.parseDouble(text);
                    if (val < min || val > max) {
                        errors.add(label + " must be between " + min + " and " + max + ".");
                    }
                } catch (NumberFormatException ex) {
                    errors.add(label + " is not a valid number.");
                }
            }
        }
        return errors;
    }

    private void calculateLabGrade() {
        labErrorLabel.setText(" ");
        labStepPane.setText("<html><body style='font-size:18pt; font-family: monospace;'></body></html>");

        List<String> errors = validateLab();
        if (!errors.isEmpty()) {
            StringBuilder errMsg = new StringBuilder();
            for (String err : errors) {
                errMsg.append(err).append("<br>");
            }
            labErrorLabel.setText("<html>" + errMsg.toString() + "</html>");
            return;
        }

        int absences = 0;
        if (!labAttendanceJan.isSelected()) absences++;
        if (!labAttendanceFeb3.isSelected()) absences++;
        if (!labAttendanceFeb10.isSelected()) absences++;
        if (!labAttendanceFeb17.isSelected()) absences++;
        if (absences >= 4) {
            labErrorLabel.setText("FAIL: Absences are 4 or more.");
            return;
        }

        double java1 = Double.parseDouble(labJava1Field.getText().trim());
        double java2 = Double.parseDouble(labJava2Field.getText().trim());
        double js1   = Double.parseDouble(labJs1Field.getText().trim());
        double js2   = Double.parseDouble(labJs2Field.getText().trim());
        double mp1   = Double.parseDouble(labMp1Field.getText().trim());
        double mp2   = Double.parseDouble(labMp2Field.getText().trim());
        double mp3   = Double.parseDouble(labMp3Field.getText().trim());
        double mp3d  = Double.parseDouble(labMp3dField.getText().trim());

        double labExam = (java1 * 0.20) + (java2 * 0.30) + (js1 * 0.20) + (js2 * 0.30);
        double labWorkSum = mp1 + mp2 + mp3 + mp3d;
        double labWork = labWorkSum / 4.0;
        double attendance = Math.max(100 - (absences * 10), 0);
        double labClassStanding = (labWork * 0.60) + (attendance * 0.40);
        double labGrade = (labExam * 0.60) + (labClassStanding * 0.40);

        // Create stamp for lab grade
        String stamp;
        if (labGrade >= 75) {
            stamp = "<span style='display:inline-block; margin-left:10px; font-weight:bold; color:#28a745; border:2px solid #28a745; border-radius:5px; padding:5px;'>PASSED</span>";
        } else {
            stamp = "<span style='display:inline-block; margin-left:10px; font-weight:bold; color:#dc3545; border:2px solid #dc3545; border-radius:5px; padding:5px;'>FAILED</span>";
        }

        StringBuilder html = new StringBuilder("<html><body style='font-size:18pt; font-family: monospace;'>");
        html.append("<b>Lab Exam Calculation:</b><br>");
        html.append("Java 1 Contribution: ").append(String.format("%.2f", java1 * 0.20)).append("<br>");
        html.append("Java 2 Contribution: ").append(String.format("%.2f", java2 * 0.30)).append("<br>");
        html.append("JS 1 Contribution: ").append(String.format("%.2f", js1 * 0.20)).append("<br>");
        html.append("JS 2 Contribution: ").append(String.format("%.2f", js2 * 0.30)).append("<br>");
        html.append("Lab Exam = ").append(String.format("%.2f", labExam)).append("<br><br>");

        html.append("<b>Lab Work Calculation:</b><br>");
        html.append("Sum of MP Scores = ").append(String.format("%.2f", labWorkSum)).append("<br>");
        html.append("Average Lab Work = Sum / 4 = ").append(String.format("%.2f", labWork)).append("%<br><br>");

        html.append("<b>Attendance Calculation:</b><br>");
        html.append("Attendance = 100 - (").append(absences).append(" * 10) = ").append(String.format("%.2f", attendance)).append("%<br><br>");

        html.append("<b>Class Standing:</b><br>");
        html.append("= (Lab Work * 0.60) + (Attendance * 0.40)<br>");
        html.append("= (").append(String.format("%.2f", labWork)).append(" * 0.60) + (").append(String.format("%.2f", attendance))
            .append(" * 0.40) = ").append(String.format("%.2f", labClassStanding)).append("%<br><br>");

        html.append("<b>Final Lab Grade:</b><br>");
        html.append("= (Lab Exam * 0.60) + (Class Standing * 0.40)<br>");
        html.append("= (").append(String.format("%.2f", labExam)).append(" * 0.60) + (").append(String.format("%.2f", labClassStanding))
            .append(" * 0.40) = ").append(String.format("%.2f", labGrade)).append("%<br>");
        if (labGrade % 1 != 0) {
            int roundUp = (int) Math.ceil(labGrade);
            int roundDown = (int) Math.floor(labGrade);
            html.append("Rounded Up: ").append(roundUp).append(", Rounded Down: ").append(roundDown).append("<br>");
        }
        html.append(stamp);
        html.append("</body></html>");
        labStepPane.setText(html.toString());
        showNotification("calculate time!🤑");
    }

    private void setRandomValidLab() {
        Random rand = new Random();
        labJava1Field.setText(String.valueOf(rand.nextInt(101)));
        labJava2Field.setText(String.valueOf(rand.nextInt(101)));
        labJs1Field.setText(String.valueOf(rand.nextInt(101)));
        labJs2Field.setText(String.valueOf(rand.nextInt(101)));
        labMp1Field.setText(String.valueOf(rand.nextInt(101)));
        labMp2Field.setText(String.valueOf(rand.nextInt(101)));
        labMp3Field.setText(String.valueOf(rand.nextInt(101)));
        labMp3dField.setText(String.valueOf(rand.nextInt(101)));

        List<JCheckBox> boxes = Arrays.asList(labAttendanceJan, labAttendanceFeb3, labAttendanceFeb10, labAttendanceFeb17);
        Collections.shuffle(boxes);
        int absences = rand.nextInt(4);
        for (int i = 0; i < boxes.size(); i++) {
            boxes.get(i).setSelected(i >= absences);
        }
    }

    private void perfectLabGrade() {
        labJava1Field.setText("100");
        labJava2Field.setText("100");
        labJs1Field.setText("100");
        labJs2Field.setText("100");
        labMp1Field.setText("100");
        labMp2Field.setText("100");
        labMp3Field.setText("100");
        labMp3dField.setText("100");
        labAttendanceJan.setSelected(true);
        labAttendanceFeb3.setSelected(true);
        labAttendanceFeb10.setSelected(true);
        labAttendanceFeb17.setSelected(true);
        calculateLabGrade();
    }

    private void resetLabGrades() {
        labJava1Field.setText("");
        labJava2Field.setText("");
        labJs1Field.setText("");
        labJs2Field.setText("");
        labMp1Field.setText("");
        labMp2Field.setText("");
        labMp3Field.setText("");
        labMp3dField.setText("");
        labAttendanceJan.setSelected(true);
        labAttendanceFeb3.setSelected(true);
        labAttendanceFeb10.setSelected(true);
        labAttendanceFeb17.setSelected(true);
        labErrorLabel.setText(" ");
        labStepPane.setText("<html><body style='font-size:18pt; font-family: monospace;'></body></html>");
    }

    private void uploadLabCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String headerLine = br.readLine();
                String dataLine = br.readLine();
                if (headerLine == null || dataLine == null) {
                    JOptionPane.showMessageDialog(this, "CSV must have header and at least one data row.");
                    return;
                }
                String[] headers = headerLine.split(",");
                String[] data = dataLine.split(",");
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < headers.length && i < data.length; i++) {
                    map.put(headers[i].trim().toLowerCase(), data[i].trim());
                }
                if (!map.containsKey("java1") || !map.containsKey("java2") ||
                    !map.containsKey("js1") || !map.containsKey("js2") ||
                    !map.containsKey("mp1") || !map.containsKey("mp2") ||
                    !map.containsKey("mp3") || !map.containsKey("mp3d")) {
                    JOptionPane.showMessageDialog(this, "Missing one or more required headers.");
                    return;
                }
                labJava1Field.setText(map.get("java1"));
                labJava2Field.setText(map.get("java2"));
                labJs1Field.setText(map.get("js1"));
                labJs2Field.setText(map.get("js2"));
                labMp1Field.setText(map.get("mp1"));
                labMp2Field.setText(map.get("mp2"));
                labMp3Field.setText(map.get("mp3"));
                labMp3dField.setText(map.get("mp3d"));
                calculateLabGrade();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void downloadLabCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Lab CSV");
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            try (PrintWriter pw = new PrintWriter(file)) {
                String headers = "java1,java2,js1,js2,mp1,mp2,mp3,mp3d";
                String row = labJava1Field.getText() + "," +
                             labJava2Field.getText() + "," +
                             labJs1Field.getText() + "," +
                             labJs2Field.getText() + "," +
                             labMp1Field.getText() + "," +
                             labMp2Field.getText() + "," +
                             labMp3Field.getText() + "," +
                             labMp3dField.getText();
                pw.println(headers);
                pw.println(row);
                JOptionPane.showMessageDialog(this, "Lab CSV saved successfully.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // -------------------- Main Method --------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GradeCalculatorFrame frame = new GradeCalculatorFrame();
            frame.setVisible(true);
        });
    }
}
