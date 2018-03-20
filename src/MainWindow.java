import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class MainWindow extends JFrame implements ActionListener {
    private JMenuBar menuBar;
    private JMenu fileM, editM, viewM;
    private JScrollPane scpane;
    private JMenuItem exitI, cutI, copyI, pasteI, selectI, saveI, loadI, statusI;
    private String pad;
    private JToolBar toolBar;

    private static final int N = 100;
    private final List<JToggleButton> list = new ArrayList<>();

    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public MainWindow() {
        super("Document");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());


        menuBar = new JMenuBar();
        fileM = new JMenu("File");
        editM = new JMenu("Edit");
        viewM = new JMenu("View");
        exitI = new JMenuItem("Exit");
        cutI = new JMenuItem("Cut");
        copyI = new JMenuItem("Copy");
        pasteI = new JMenuItem("Paste");
        selectI = new JMenuItem("Select All");
        saveI = new JMenuItem("Save");
        loadI = new JMenuItem("Load");
        statusI = new JMenuItem("Status");
        toolBar = new JToolBar();


        scpane = new JScrollPane(createGridPanel());

        setJMenuBar(menuBar);
        menuBar.add(fileM);
        menuBar.add(editM);
        menuBar.add(viewM);

        fileM.add(saveI);
        fileM.add(loadI);
        fileM.add(exitI);

        editM.add(cutI);
        editM.add(copyI);
        editM.add(pasteI);
        editM.add(selectI);

        viewM.add(statusI);

        saveI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        loadI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        cutI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        copyI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        pasteI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        selectI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        pane.add(scpane, BorderLayout.CENTER);
        pane.add(toolBar, BorderLayout.SOUTH);

        saveI.addActionListener(this);
        loadI.addActionListener(this);
        exitI.addActionListener(this);
        cutI.addActionListener(this);
        copyI.addActionListener(this);
        pasteI.addActionListener(this);
        selectI.addActionListener(this);
        statusI.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        JMenuItem choice = (JMenuItem) e.getSource();
        if (choice == saveI) {
            //not yet implmented
        }else if (choice == loadI) {
            loadFile();
        }else if (choice == exitI){
            System.exit(0);
        }


        /*else if (choice == cutI) {
            pad = ta.getSelectedText();
            ta.replaceRange("", ta.getSelectionStart(), ta.getSelectionEnd());
        } else if (choice == copyI)
            pad = ta.getSelectedText();
        else if (choice == pasteI)
            ta.insert(pad, ta.getCaretPosition());
        else if (choice == selectI)
            ta.selectAll();
        else if (e.getSource() == statusI) {
            //not yet implmented
        }*/
    }

    public void loadFile(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            System.out.println("Loading: " + selectedFile.getAbsolutePath());
            MidiPlayer app = new MidiPlayer(selectedFile.getAbsolutePath());
        }
    }

    private JToggleButton getGridButton(int r, int c) {
        int index = r * N + c;
        return list.get(index);
    }

    private JToggleButton createGridButton(final int row, final int col) {
        final JToggleButton b = new JToggleButton("r" + row + ",c" + col);
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton gb = MainWindow.this.getGridButton(row, col);
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
}
