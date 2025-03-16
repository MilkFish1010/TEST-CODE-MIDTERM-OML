import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class GradeCalculatorSwing extends JFrame {

    public GradeCalculatorSwing() {
        setTitle("Programming 2 Prelim Grade Calculator 2025");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create tabs for Lecture and Lab calculators
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Lecture Calculator", new LecturePanel());
        tabbedPane.addTab("Lab Calculator", new LabPanel());

        add(tabbedPane);
    }

    // ----------------- LECTURE PANEL ----------------- //
    private class LecturePanel extends JPanel {
        private JTextField examField, essayField, pvmField, javaField, jsField;
        private JCheckBox attJan27, attFeb03, attFeb10, attFeb17, showSteps;
        private JPanel stepsPanel;
        private JLabel resultLabel;
        private Timer stepTimer;
        private String[] steps;
        private int currentStepIndex = 0;

        public LecturePanel() {
            setLayout(new BorderLayout());

            // Input Fields
            JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
            examField = new JTextField();
            essayField = new JTextField();
            pvmField = new JTextField();
            javaField = new JTextField();
            jsField = new JTextField();
            inputPanel.add(new JLabel("Exam Score (0-100):"));
            inputPanel.add(examField);
            inputPanel.add(new JLabel("Essay Score (0-100):"));
            inputPanel.add(essayField);
            inputPanel.add(new JLabel("PVM Score (0-60):"));
            inputPanel.add(pvmField);
            inputPanel.add(new JLabel("Java Score (0-40):"));
            inputPanel.add(javaField);
            inputPanel.add(new JLabel("JavaScript Score (0-40):"));
            inputPanel.add(jsField);

            // Attendance Checkboxes
            JPanel attendancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            attJan27 = new JCheckBox("Jan 27", true);
            attFeb03 = new JCheckBox("Feb 3", true);
            attFeb10 = new JCheckBox("Feb 10", true);
            attFeb17 = new JCheckBox("Feb 17", true);
            attendancePanel.add(new JLabel("Attendance:"));
            attendancePanel.add(attJan27);
            attendancePanel.add(attFeb03);
            attendancePanel.add(attFeb10);
            attendancePanel.add(attFeb17);

            // Toggle for step-by-step display
            showSteps = new JCheckBox("Show Step-by-Step Calculation", true);

            // Buttons Panel
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton uploadBtn = new JButton("Upload CSV");
            uploadBtn.addActionListener(e -> uploadLectureCSV());
            JButton randomBtn = new JButton("Random Grade Generator");
            randomBtn.addActionListener(e -> setRandomLecture());
            JButton perfectBtn = new JButton("I aced this course!");
            perfectBtn.addActionListener(e -> setPerfectLecture());
            JButton calculateBtn = new JButton("Calculate Lecture Grade");
            calculateBtn.addActionListener(e -> calculateLectureGrade());
            JButton resetBtn = new JButton("Reset Grades");
            resetBtn.addActionListener(e -> resetLecture());
            JButton downloadBtn = new JButton("Download CSV");
            downloadBtn.addActionListener(e -> downloadLectureCSV());
            buttonPanel.add(uploadBtn);
            buttonPanel.add(randomBtn);
            buttonPanel.add(perfectBtn);
            buttonPanel.add(calculateBtn);
            buttonPanel.add(resetBtn);
            buttonPanel.add(downloadBtn);

            // Steps Panel (for step-by-step output)
            stepsPanel = new JPanel();
            stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
            JScrollPane stepsScroll = new JScrollPane(stepsPanel);
            stepsScroll.setPreferredSize(new Dimension(800, 200));

            // Final result label
            resultLabel = new JLabel("");
            resultLabel.setFont(new Font("Arial", Font.BOLD, 16));

            // Combine panels
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(inputPanel, BorderLayout.NORTH);
            topPanel.add(attendancePanel, BorderLayout.CENTER);
            topPanel.add(showSteps, BorderLayout.SOUTH);

            add(topPanel, BorderLayout.NORTH);
            add(buttonPanel, BorderLayout.CENTER);
            add(stepsScroll, BorderLayout.SOUTH);
            add(resultLabel, BorderLayout.PAGE_END);
        }

        private void resetLecture() {
            examField.setText("");
            essayField.setText("");
            pvmField.setText("");
            javaField.setText("");
            jsField.setText("");
            attJan27.setSelected(true);
            attFeb03.setSelected(true);
            attFeb10.setSelected(true);
            attFeb17.setSelected(true);
            stepsPanel.removeAll();
            stepsPanel.revalidate();
            stepsPanel.repaint();
            resultLabel.setText("");
            if (stepTimer != null) stepTimer.stop();
        }

        private void setRandomLecture() {
            Random rnd = new Random();
            examField.setText(String.valueOf(rnd.nextInt(101)));
            essayField.setText(String.valueOf(rnd.nextInt(101)));
            pvmField.setText(String.valueOf(rnd.nextInt(61)));
            javaField.setText(String.valueOf(rnd.nextInt(41)));
            jsField.setText(String.valueOf(rnd.nextInt(41)));
            attJan27.setSelected(rnd.nextBoolean());
            attFeb03.setSelected(rnd.nextBoolean());
            attFeb10.setSelected(rnd.nextBoolean());
            attFeb17.setSelected(rnd.nextBoolean());
        }

        private void setPerfectLecture() {
            examField.setText("100");
            essayField.setText("100");
            pvmField.setText("60");
            javaField.setText("40");
            jsField.setText("40");
            attJan27.setSelected(true);
            attFeb03.setSelected(true);
            attFeb10.setSelected(true);
            attFeb17.setSelected(true);
        }

        private void uploadLectureCSV() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try (Scanner scanner = new Scanner(file)) {
                    if (scanner.hasNextLine()) {
                        String headerLine = scanner.nextLine();
                        String[] headers = headerLine.split(",");
                        if (scanner.hasNextLine()) {
                            String dataLine = scanner.nextLine();
                            String[] data = dataLine.split(",");
                            examField.setText(data[getIndex(headers, "exam")]);
                            essayField.setText(data[getIndex(headers, "essay")]);
                            pvmField.setText(data[getIndex(headers, "pvm")]);
                            javaField.setText(data[getIndex(headers, "java")]);
                            jsField.setText(data[getIndex(headers, "js")]);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error reading CSV: " + ex.getMessage());
                }
            }
        }

        private int getIndex(String[] headers, String key) throws Exception {
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(key)) {
                    return i;
                }
            }
            throw new Exception("Missing header: " + key);
        }

        private void downloadLectureCSV() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(file)) {
                    String header = "exam,essay,pvm,java,js";
                    String row = examField.getText() + "," + essayField.getText() + "," + pvmField.getText() + ","
                            + javaField.getText() + "," + jsField.getText();
                    writer.println(header);
                    writer.println(row);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error writing CSV: " + ex.getMessage());
                }
            }
        }

        private void calculateLectureGrade() {
            stepsPanel.removeAll();
            resultLabel.setText("");
            if (stepTimer != null) stepTimer.stop();

            try {
                double exam = Double.parseDouble(examField.getText().trim());
                double essay = Double.parseDouble(essayField.getText().trim());
                double pvm = Double.parseDouble(pvmField.getText().trim());
                double java = Double.parseDouble(javaField.getText().trim());
                double js = Double.parseDouble(jsField.getText().trim());

                // Validate ranges
                if (exam < 0 || exam > 100 || essay < 0 || essay > 100 || pvm < 0 || pvm > 60 ||
                        java < 0 || java > 40 || js < 0 || js > 40) {
                    JOptionPane.showMessageDialog(this, "One or more fields are out of the expected range.");
                    return;
                }

                int absences = 0;
                if (!attJan27.isSelected()) absences++;
                if (!attFeb03.isSelected()) absences++;
                if (!attFeb10.isSelected()) absences++;
                if (!attFeb17.isSelected()) absences++;

                if (absences >= 4) {
                    resultLabel.setText("FAIL: Absences are 4 or more.");
                    return;
                }

                double essayPercent = (essay / 100) * 100;
                double pvmPercent = (pvm / 60) * 100;
                double javaPercent = (java / 40) * 100;
                double jsPercent = (js / 40) * 100;
                double prelimQuizzes = (essayPercent + pvmPercent + javaPercent + jsPercent) / 4.0;
                double attendanceVal = Math.max(100 - (absences * 10), 0);
                double prelimClassStanding = (prelimQuizzes * 0.60) + (attendanceVal * 0.40);
                double prelimGrade = (exam * 0.60) + (prelimClassStanding * 0.40);

                if (!showSteps.isSelected()) {
                    resultLabel.setText(String.format("Your Final Lecture Grade: %.1f", prelimGrade));
                    return;
                }

                // Build LaTeX steps
                steps = new String[]{
                        String.format("\\( \\text{Essay: } \\frac{%s}{100} \\times 100 = %.2f\\%% \\)", essay, essayPercent),
                        String.format("\\( \\text{PVM: } \\frac{%s}{60} \\times 100 = %.2f\\%% \\)", pvm, pvmPercent),
                        String.format("\\( \\text{Java: } \\frac{%s}{40} \\times 100 = %.2f\\%% \\)", java, javaPercent),
                        String.format("\\( \\text{JS: } \\frac{%s}{40} \\times 100 = %.2f\\%% \\)", js, jsPercent),
                        String.format("\\( \\text{Average Quizzes: } \\frac{%.2f + %.2f + %.2f + %.2f}{4} = %.2f\\%% \\)",
                                essayPercent, pvmPercent, javaPercent, jsPercent, prelimQuizzes),
                        String.format("\\( \\text{Attendance: } 100 - (%d \\times 10) = %.2f\\%% \\)", absences, attendanceVal),
                        String.format("\\( \\text{Prelim Class Standing: } (%.2f \\times 0.60) + (%.2f \\times 0.40) = %.2f\\%% \\)",
                                prelimQuizzes, attendanceVal, prelimClassStanding),
                        String.format("\\( \\text{Final Grade: } (%s \\times 0.60) + (%.2f \\times 0.40) = %.2f \\)",
                                exam, prelimClassStanding, prelimGrade)
                };

                currentStepIndex = 0;
                stepTimer = new Timer(1500, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (currentStepIndex < steps.length) {
                            JLabel stepLabel = createLatexLabel(steps[currentStepIndex]);
                            stepsPanel.add(stepLabel);
                            stepsPanel.revalidate();
                            currentStepIndex++;
                        } else {
                            stepTimer.stop();
                            resultLabel.setText(String.format("Your Final Lecture Grade: %.1f", prelimGrade));
                        }
                    }
                });
                stepTimer.start();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please fill all fields with valid numbers.");
            }
        }

        // Uses JLaTeXMath to render LaTeX in a JLabel
        private JLabel createLatexLabel(String latex) {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 18);
            JLabel label = new JLabel(icon);
            return label;
        }
    }

    // ----------------- LAB PANEL ----------------- //
    private class LabPanel extends JPanel {
        private JTextField java1Field, java2Field, js1Field, js2Field, mp1Field, mp2Field, mp3Field, mp3dField;
        private JCheckBox attJan27, attFeb03, attFeb10, attFeb17, showSteps;
        private JPanel stepsPanel;
        private JLabel resultLabel;
        private Timer stepTimer;
        private String[] steps;
        private int currentStepIndex = 0;

        public LabPanel() {
            setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel(new GridLayout(9, 2, 10, 10));
            java1Field = new JTextField();
            java2Field = new JTextField();
            js1Field = new JTextField();
            js2Field = new JTextField();
            mp1Field = new JTextField();
            mp2Field = new JTextField();
            mp3Field = new JTextField();
            mp3dField = new JTextField();
            inputPanel.add(new JLabel("Java 1 (0-100):"));
            inputPanel.add(java1Field);
            inputPanel.add(new JLabel("Java 2 (0-100):"));
            inputPanel.add(java2Field);
            inputPanel.add(new JLabel("JS 1 (0-100):"));
            inputPanel.add(js1Field);
            inputPanel.add(new JLabel("JS 2 (0-100):"));
            inputPanel.add(js2Field);
            inputPanel.add(new JLabel("MP1 (0-100):"));
            inputPanel.add(mp1Field);
            inputPanel.add(new JLabel("MP2 (0-100):"));
            inputPanel.add(mp2Field);
            inputPanel.add(new JLabel("MP3 (0-100):"));
            inputPanel.add(mp3Field);
            inputPanel.add(new JLabel("MP3 Documentation (0-100):"));
            inputPanel.add(mp3dField);

            JPanel attendancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            attJan27 = new JCheckBox("Jan 27", true);
            attFeb03 = new JCheckBox("Feb 3", true);
            attFeb10 = new JCheckBox("Feb 10", true);
            attFeb17 = new JCheckBox("Feb 17", true);
            attendancePanel.add(new JLabel("Attendance:"));
            attendancePanel.add(attJan27);
            attendancePanel.add(attFeb03);
            attendancePanel.add(attFeb10);
            attendancePanel.add(attFeb17);

            showSteps = new JCheckBox("Show Step-by-Step Calculation", true);

            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton uploadBtn = new JButton("Upload CSV");
            uploadBtn.addActionListener(e -> uploadLabCSV());
            JButton randomBtn = new JButton("Random Grade Generator");
            randomBtn.addActionListener(e -> setRandomLab());
            JButton perfectBtn = new JButton("I aced this course!");
            perfectBtn.addActionListener(e -> setPerfectLab());
            JButton calculateBtn = new JButton("Calculate Lab Grade");
            calculateBtn.addActionListener(e -> calculateLabGrade());
            JButton resetBtn = new JButton("Reset Grades");
            resetBtn.addActionListener(e -> resetLab());
            JButton downloadBtn = new JButton("Download CSV");
            downloadBtn.addActionListener(e -> downloadLabCSV());
            buttonPanel.add(uploadBtn);
            buttonPanel.add(randomBtn);
            buttonPanel.add(perfectBtn);
            buttonPanel.add(calculateBtn);
            buttonPanel.add(resetBtn);
            buttonPanel.add(downloadBtn);

            stepsPanel = new JPanel();
            stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
            JScrollPane stepsScroll = new JScrollPane(stepsPanel);
            stepsScroll.setPreferredSize(new Dimension(800, 200));

            resultLabel = new JLabel("");
            resultLabel.setFont(new Font("Arial", Font.BOLD, 16));

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(inputPanel, BorderLayout.NORTH);
            topPanel.add(attendancePanel, BorderLayout.CENTER);
            topPanel.add(showSteps, BorderLayout.SOUTH);

            add(topPanel, BorderLayout.NORTH);
            add(buttonPanel, BorderLayout.CENTER);
            add(stepsScroll, BorderLayout.SOUTH);
            add(resultLabel, BorderLayout.PAGE_END);
        }

        private void resetLab() {
            java1Field.setText("");
            java2Field.setText("");
            js1Field.setText("");
            js2Field.setText("");
            mp1Field.setText("");
            mp2Field.setText("");
            mp3Field.setText("");
            mp3dField.setText("");
            attJan27.setSelected(true);
            attFeb03.setSelected(true);
            attFeb10.setSelected(true);
            attFeb17.setSelected(true);
            stepsPanel.removeAll();
            stepsPanel.revalidate();
            stepsPanel.repaint();
            resultLabel.setText("");
            if (stepTimer != null) stepTimer.stop();
        }

        private void setRandomLab() {
            Random rnd = new Random();
            java1Field.setText(String.valueOf(rnd.nextInt(101)));
            java2Field.setText(String.valueOf(rnd.nextInt(101)));
            js1Field.setText(String.valueOf(rnd.nextInt(101)));
            js2Field.setText(String.valueOf(rnd.nextInt(101)));
            mp1Field.setText(String.valueOf(rnd.nextInt(101)));
            mp2Field.setText(String.valueOf(rnd.nextInt(101)));
            mp3Field.setText(String.valueOf(rnd.nextInt(101)));
            mp3dField.setText(String.valueOf(rnd.nextInt(101)));
            attJan27.setSelected(rnd.nextBoolean());
            attFeb03.setSelected(rnd.nextBoolean());
            attFeb10.setSelected(rnd.nextBoolean());
            attFeb17.setSelected(rnd.nextBoolean());
        }

        private void setPerfectLab() {
            java1Field.setText("100");
            java2Field.setText("100");
            js1Field.setText("100");
            js2Field.setText("100");
            mp1Field.setText("100");
            mp2Field.setText("100");
            mp3Field.setText("100");
            mp3dField.setText("100");
            attJan27.setSelected(true);
            attFeb03.setSelected(true);
            attFeb10.setSelected(true);
            attFeb17.setSelected(true);
        }

        private void uploadLabCSV() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try (Scanner scanner = new Scanner(file)) {
                    if (scanner.hasNextLine()) {
                        String headerLine = scanner.nextLine();
                        String[] headers = headerLine.split(",");
                        if (scanner.hasNextLine()) {
                            String dataLine = scanner.nextLine();
                            String[] data = dataLine.split(",");
                            java1Field.setText(data[getIndex(headers, "java1")]);
                            java2Field.setText(data[getIndex(headers, "java2")]);
                            js1Field.setText(data[getIndex(headers, "js1")]);
                            js2Field.setText(data[getIndex(headers, "js2")]);
                            mp1Field.setText(data[getIndex(headers, "mp1")]);
                            mp2Field.setText(data[getIndex(headers, "mp2")]);
                            mp3Field.setText(data[getIndex(headers, "mp3")]);
                            mp3dField.setText(data[getIndex(headers, "mp3d")]);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error reading CSV: " + ex.getMessage());
                }
            }
        }

        private int getIndex(String[] headers, String key) throws Exception {
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(key)) {
                    return i;
                }
            }
            throw new Exception("Missing header: " + key);
        }

        private void downloadLabCSV() {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(file)) {
                    String header = "java1,java2,js1,js2,mp1,mp2,mp3,mp3d";
                    String row = java1Field.getText() + "," + java2Field.getText() + "," + js1Field.getText() + "," +
                            js2Field.getText() + "," + mp1Field.getText() + "," + mp2Field.getText() + "," +
                            mp3Field.getText() + "," + mp3dField.getText();
                    writer.println(header);
                    writer.println(row);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error writing CSV: " + ex.getMessage());
                }
            }
        }

        private void calculateLabGrade() {
            stepsPanel.removeAll();
            resultLabel.setText("");
            if (stepTimer != null) stepTimer.stop();

            try {
                double java1 = Double.parseDouble(java1Field.getText().trim());
                double java2 = Double.parseDouble(java2Field.getText().trim());
                double js1 = Double.parseDouble(js1Field.getText().trim());
                double js2 = Double.parseDouble(js2Field.getText().trim());
                double mp1 = Double.parseDouble(mp1Field.getText().trim());
                double mp2 = Double.parseDouble(mp2Field.getText().trim());
                double mp3 = Double.parseDouble(mp3Field.getText().trim());
                double mp3d = Double.parseDouble(mp3dField.getText().trim());

                if (java1 < 0 || java1 > 100 || java2 < 0 || java2 > 100 ||
                        js1 < 0 || js1 > 100 || js2 < 0 || js2 > 100 ||
                        mp1 < 0 || mp1 > 100 || mp2 < 0 || mp2 > 100 ||
                        mp3 < 0 || mp3 > 100 || mp3d < 0 || mp3d > 100) {
                    JOptionPane.showMessageDialog(this, "One or more fields are out of the expected range.");
                    return;
                }

                int absences = 0;
                if (!attJan27.isSelected()) absences++;
                if (!attFeb03.isSelected()) absences++;
                if (!attFeb10.isSelected()) absences++;
                if (!attFeb17.isSelected()) absences++;

                if (absences >= 4) {
                    resultLabel.setText("FAIL: Absences are 4 or more.");
                    return;
                }

                double labExam = (java1 * 0.20) + (java2 * 0.30) + (js1 * 0.20) + (js2 * 0.30);
                double labWork = (mp1 + mp2 + mp3 + mp3d) / 4.0;
                double attendanceVal = Math.max(100 - (absences * 10), 0);
                double labClassStanding = (labWork * 0.60) + (attendanceVal * 0.40);
                double labGrade = (labExam * 0.60) + (labClassStanding * 0.40);

                if (!showSteps.isSelected()) {
                    resultLabel.setText(String.format("Your Final Lab Grade: %.1f", labGrade));
                    return;
                }

                steps = new String[]{
                        String.format("\\( \\text{Prelim Exam: } (%.2f\\times0.20) + (%.2f\\times0.30) + (%.2f\\times0.20) + (%.2f\\times0.30) = %.2f \\)", java1, java2, js1, js2, labExam),
                        String.format("\\( \\text{Lab Work: } \\frac{%.2f+%.2f+%.2f+%.2f}{4} = %.2f\\%% \\)", mp1, mp2, mp3, mp3d, labWork),
                        String.format("\\( \\text{Attendance: } 100 - (%d\\times10) = %.2f \\)", absences, attendanceVal),
                        String.format("\\( \\text{Class Standing: } (%.2f\\times0.60) + (%.2f\\times0.40) = %.2f \\)", labWork, attendanceVal, labClassStanding),
                        String.format("\\( \\text{Final Grade: } (%.2f\\times0.60) + (%.2f\\times0.40) = %.2f \\)", labExam, labClassStanding, labGrade)
                };

                currentStepIndex = 0;
                stepTimer = new Timer(1500, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (currentStepIndex < steps.length) {
                            JLabel stepLabel = createLatexLabel(steps[currentStepIndex]);
                            stepsPanel.add(stepLabel);
                            stepsPanel.revalidate();
                            currentStepIndex++;
                        } else {
                            stepTimer.stop();
                            resultLabel.setText(String.format("Your Final Lab Grade: %.1f", labGrade));
                        }
                    }
                });
                stepTimer.start();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please fill all fields with valid numbers.");
            }
        }

        private JLabel createLatexLabel(String latex) {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 18);
            JLabel label = new JLabel(icon);
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GradeCalculatorSwing frame = new GradeCalculatorSwing();
            frame.setVisible(true);
        });
    }
}
