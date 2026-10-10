import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

/** Hospital Appointment System - Java Swing. Data is saved in appointments.txt */
public class HospitalAppointmentSystem extends JFrame {
    static final String FILE = "appointments.txt";
    static final String[] DOCTORS = {"Dr. Sharma (Cardiology)", "Dr. Mehta (Orthopedics)",
            "Dr. Iyer (Pediatrics)", "Dr. Khan (General Medicine)", "Dr. Patil (Dermatology)"};
    static final String[] SLOTS = {"09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "14:00", "14:30", "15:00", "15:30", "16:00"};

    JTextField name = new JTextField(), age = new JTextField(), phone = new JTextField(),
            date = new JTextField(LocalDate.now().plusDays(1).toString()), search = new JTextField(15);
    JComboBox<String> doctor = new JComboBox<>(DOCTORS), slot = new JComboBox<>(SLOTS);
    JTextArea reason = new JTextArea(3, 20);
    DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Patient", "Age", "Phone", "Doctor", "Date", "Time", "Reason"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    JTable table = new JTable(model);
    int nextId = 1;

    public HospitalAppointmentSystem() {
        super("Hospital Appointment System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 560);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("  City Care Hospital - Appointment System", JLabel.LEFT);
        title.setOpaque(true);
        title.setBackground(new Color(0, 105, 148));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setPreferredSize(new Dimension(0, 50));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Book Appointment", bookingPanel());
        tabs.addTab("Manage Appointments", managePanel());

        add(title, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        load();
    }

    JPanel bookingPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        String[] labels = {"Patient Name", "Age", "Phone", "Doctor", "Date (YYYY-MM-DD)", "Time Slot", "Reason"};
        Component[] fields = {name, age, phone, doctor, date, slot, new JScrollPane(reason)};
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.2; p.add(new JLabel(labels[i]), g);
            g.gridx = 1; g.weightx = 0.8; p.add(fields[i], g);
        }
        JButton book = new JButton("Book Appointment");
        JButton clear = new JButton("Clear");
        JPanel b = new JPanel();
        b.add(book); b.add(clear);
        g.gridx = 1; g.gridy = labels.length; p.add(b, g);
        book.addActionListener(e -> book());
        clear.addActionListener(e -> { name.setText(""); age.setText(""); phone.setText(""); reason.setText(""); });
        return p;
    }

    JPanel managePanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton find = new JButton("Search"), all = new JButton("Show All"), cancel = new JButton("Cancel Selected");
        top.add(new JLabel("Patient / Doctor:")); top.add(search); top.add(find); top.add(all); top.add(cancel);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        find.addActionListener(e -> filter(search.getText().trim().toLowerCase()));
        all.addActionListener(e -> filter(""));
        cancel.addActionListener(e -> cancelSelected());
        return p;
    }

    void book() {
        String n = name.getText().trim(), a = age.getText().trim(), ph = phone.getText().trim(),
                d = date.getText().trim(), r = reason.getText().trim().replace("|", "/").replace("\n", " ");
        if (n.isEmpty() || a.isEmpty() || ph.isEmpty() || r.isEmpty()) { err("Please fill all fields."); return; }
        try { int ag = Integer.parseInt(a); if (ag < 0 || ag > 120) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { err("Enter a valid age."); return; }
        if (!ph.matches("\\d{10}")) { err("Phone must be 10 digits."); return; }
        try {
            if (LocalDate.parse(d).isBefore(LocalDate.now())) { err("Date cannot be in the past."); return; }
        } catch (Exception ex) { err("Invalid date. Use YYYY-MM-DD."); return; }
        String doc = (String) doctor.getSelectedItem(), t = (String) slot.getSelectedItem();
        for (int i = 0; i < model.getRowCount(); i++)
            if (model.getValueAt(i, 4).equals(doc) && model.getValueAt(i, 5).equals(d) && model.getValueAt(i, 6).equals(t)) {
                err("This doctor is already booked at that time. Choose another slot."); return;
            }
        model.addRow(new Object[]{nextId++, n, a, ph, doc, d, t, r});
        save();
        JOptionPane.showMessageDialog(this, "Appointment booked for " + n + "\n" + doc + "\n" + d + " at " + t);
        name.setText(""); age.setText(""); phone.setText(""); reason.setText("");
    }

    void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { err("Select an appointment first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Cancel this appointment?", "Confirm", JOptionPane.YES_NO_OPTION) == 0) {
            model.removeRow(table.convertRowIndexToModel(row));
            save();
        }
    }

    void filter(String q) {
        TableRowSorterHelper.apply(table, model, q);
    }

    void err(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }

    void save() {
        try (PrintWriter w = new PrintWriter(FILE)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < model.getColumnCount(); c++) sb.append(model.getValueAt(i, c)).append(c < 7 ? "|" : "");
                w.println(sb);
            }
        } catch (IOException e) { err("Could not save: " + e.getMessage()); }
    }

    void load() {
        Path p = Paths.get(FILE);
        if (!Files.exists(p)) return;
        try {
            List<String> lines = Files.readAllLines(p);
            for (String l : lines) {
                String[] s = l.split("\\|", 8);
                if (s.length == 8) { model.addRow(new Object[]{Integer.parseInt(s[0]), s[1], s[2], s[3], s[4], s[5], s[6], s[7]});
                    nextId = Math.max(nextId, Integer.parseInt(s[0]) + 1); }
            }
        } catch (Exception e) { err("Could not load data."); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalAppointmentSystem().setVisible(true));
    }
}

class TableRowSorterHelper {
    static void apply(JTable t, DefaultTableModel m, String q) {
        javax.swing.table.TableRowSorter<DefaultTableModel> s = new javax.swing.table.TableRowSorter<>(m);
        if (!q.isEmpty()) s.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(q), 1, 4));
        t.setRowSorter(s);
    }
}
