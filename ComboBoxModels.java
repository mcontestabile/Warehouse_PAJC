package it.unibs.pajc.warehouse;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class ComboBoxModels {

    private JComboBox comboBoxDouble;
    private JComboBox comboBoxInteger;
    private JComboBox comboBoxBoolean;
    private JComboBox comboBoxIcon;
    private JComboBox comboBoxDate;
    private JLabel label =  new JLabel();
    private Vector<Double> doubleVector = new Vector<Double>();
    private Vector<Integer> integerVector = new Vector<Integer>();
    private Vector<Boolean> booleanVector = new Vector<Boolean>();
    private Vector<Icon> iconVector = new Vector<Icon>();
    private Vector<Date> dateVector = new Vector<Date>();
    private Icon icon1 = ((UIManager.getIcon("OptionPane.errorIcon")));
    private Icon icon2 = (UIManager.getIcon("OptionPane.informationIcon"));
    private Icon icon3 = (UIManager.getIcon("OptionPane.warningIcon"));
    private Icon icon4 = (UIManager.getIcon("OptionPane.questionIcon"));
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public ComboBoxModels() {
        doubleVector.addElement(1.001);
        doubleVector.addElement(10.00);
        doubleVector.addElement(0.95);
        doubleVector.addElement(4.2);
        comboBoxDouble = new JComboBox(doubleVector);
        integerVector.addElement(1);
        integerVector.addElement(2);
        integerVector.addElement(3);
        integerVector.addElement(4);
        comboBoxInteger = new JComboBox(integerVector);
        booleanVector.add(Boolean.TRUE);
        booleanVector.add(Boolean.FALSE);
        comboBoxBoolean = new JComboBox(booleanVector);
        iconVector.addElement(icon1);
        iconVector.addElement(icon2);
        iconVector.addElement(icon3);
        iconVector.addElement(icon4);
        comboBoxIcon = new JComboBox(iconVector);
        comboBoxIcon.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Icon icon = (Icon) comboBoxIcon.getModel().getSelectedItem();
                    label.setIcon(icon);
                }
            }
        });
        dateVector.addElement(parseDate("25.01.2013"));
        dateVector.addElement(parseDate("01.02.2013"));
        dateVector.addElement(parseDate("03.03.2013"));
        dateVector.addElement(parseDate("18.04.2013"));
        comboBoxDate = new JComboBox(dateVector);
        comboBoxDate.setRenderer(new ComboBoxRenderer());
        JFrame frame = new JFrame("");
        frame.setLayout(new GridLayout(2, 2, 5, 5));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(comboBoxDouble);
        frame.add(comboBoxInteger);
        frame.add(comboBoxBoolean);
        frame.add(comboBoxIcon);
        frame.add(comboBoxDate);
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private Date parseDate(String str) {
        Date date = new Date();
        try {
            date = sdf.parse(str);
        } catch (ParseException ex) {
        }
        return date;
    }

    private class ComboBoxRenderer extends JLabel implements ListCellRenderer {

        private static final long serialVersionUID = 1L;

        public ComboBoxRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(1, 1, 1, 1));
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            if (!(value instanceof Date)) {
                return this;
            }
            setText(sdf.format((Date) value));
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ComboBoxModels comboBoxModel = new ComboBoxModels();
            }
        });
    }
}