import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class GridButtonPanel {

    private static final int N = 5;
    private final List<JToggleButton> list = new ArrayList<JToggleButton>();

    private JToggleButton getGridButton(int r, int c) {
        int index = r * N + c;
        return list.get(index);
    }

    private JToggleButton createGridButton(final int row, final int col) {
        final JToggleButton b = new JToggleButton("r" + row + ",c" + col);
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton gb = GridButtonPanel.this.getGridButton(row, col);
                System.out.println("r" + row + ",c" + col
                        + " " + (b == gb)
                        + " " + (b.equals(gb)));
            }
        });
        return b;
    }

    private JPanel createGridPanel() {
        JPanel p = new JPanel(new GridLayout(N, N));
        for (int i = 0; i < N * N; i++) {
            int row = i / N;
            int col = i % N;
            JToggleButton gb = createGridButton(row, col);
            list.add(gb);
            p.add(gb);
        }
        return p;
    }

    private void display() {
        JFrame f = new JFrame("GridButton");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(createGridPanel());
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new GridButtonPanel().display();
            }
        });
    }
}