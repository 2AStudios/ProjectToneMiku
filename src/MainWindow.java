import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;

public class MainWindow extends JFrame implements ActionListener {
    private JMenuBar menuBar;
    private JMenu fileM, editM, viewM;
    private JScrollPane scpane;
    private JPanel noteMatrix;
    private JMenuItem exitI, cutI, copyI, pasteI, selectI, saveI, loadI, statusI;
    private JToolBar toolBar;

    JButton playTrackButton;

    static int[] defaultScale = new int[]{48,95};//{36,95};
    static int scaleLength = 64;
    final HashMap<Main.Tuple<Integer,Integer>,JToggleButton> buttonHashMap = new HashMap<>();

    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public long tempo = 600;
    public float tempomodifier = 1.0f;

    public static MidiPlayer app;

    public MainWindow() {
        super("Project TM");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        createMenuBar();
        createToolBar();

        noteMatrix = createGridPanel();
        scpane = new JScrollPane(noteMatrix);

        pane.add(scpane, BorderLayout.CENTER);
        pane.add(toolBar, BorderLayout.NORTH);

        setVisible(true);
    }

    private void createMenuBar(){
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

        saveI.addActionListener(this);
        loadI.addActionListener(this);
        exitI.addActionListener(this);
        cutI.addActionListener(this);
        copyI.addActionListener(this);
        pasteI.addActionListener(this);
        selectI.addActionListener(this);
        statusI.addActionListener(this);
    }

    private void createToolBar(){
        toolBar = new JToolBar();
        ImageIcon icon = new ImageIcon(new ImageIcon("img/playbtn_icon.png").getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));

        playTrackButton = new JButton(icon);
        toolBar.add(playTrackButton);

        playTrackButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JMenuItem) {
            JMenuItem choice = (JMenuItem) e.getSource();
            if (choice == saveI) {
                //not yet implmented
            } else if (choice == loadI) {
                loadFile();
            } else if (choice == exitI) {
                System.exit(0);
            }
        }else
        if(e.getSource() instanceof JButton) {
            JButton choice = (JButton) e.getSource();
            if (choice == playTrackButton) {
                if (app != null) {
                    app.playTrack(0, tempomodifier);
                }
                //addAudioColumn();
            }
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

        jfc.setDialogTitle("Select an MIDI file");
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MIDI Audio Data Files", "mid");
        jfc.addChoosableFileFilter(filter);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            System.out.println("Loading: " + selectedFile.getAbsolutePath());
            app = new MidiPlayer(selectedFile.getAbsolutePath(),this);
        }
    }

    private JToggleButton getGridButton(int r, int c) {
        //int index = r * scaleLength + c;
        return buttonHashMap.get(new Main().new Tuple<>(r,c));//list.get(index);
    }

    private JToggleButton createGridButton(final int row, final int col) {
        String noteName = NOTE_NAMES[row % 12];
        final JToggleButton b;
        if(row == 0) {
            b = new JToggleButton(col + "");
            b.setBackground(new Color(19,122,127));
            b.setContentAreaFilled(false);
            b.setOpaque(true);
            b.setFocusable(false);
        }else if(col == 0) {
            b = new JToggleButton(noteName);
            b.setBackground(new Color(134,206,203));
            b.setContentAreaFilled(false);
            b.setOpaque(true);
            b.setFocusable(false);
        }else {
            b = new JToggleButton(String.valueOf("\u266A"));
        }
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton gb = MainWindow.this.getGridButton(row, col);
                if(!gb.isOpaque()) {
                    gb.setBackground(new Color(140, 35, 103));
                    gb.setContentAreaFilled(false);
                    gb.setOpaque(true);
                }else{
                    gb.setContentAreaFilled(true);
                    gb.setOpaque(false);
                }
                System.out.println("r" + row + ",c" + col
                        + " " + (b == gb)
                        + " " + (b.equals(gb)));
                //System.out.println(gb.getText());
            }
        });
        return b;
    }

    private JPanel createGridPanel() {
        JPanel p = new JPanel(new GridLayout(defaultScale[1]-defaultScale[0], scaleLength));
        for (int row = 0; row < defaultScale[1]-defaultScale[0]; row++) {
            for(int col = 0; col<scaleLength;col++) {
                JToggleButton gb = createGridButton(row, col);
                buttonHashMap.put(new Main().new Tuple<>(row,col),gb);
                p.add(gb);
            }
        }
        return p;
    }
    
    public int updateGridPanel(HashMap<Main.Tuple<Integer,Integer>, Main.Tuple<Integer,Long>> noteMap, int newScaleLength){
        Container pane = getContentPane();
        pane.remove(scpane);
        System.out.println("Updating");
        scaleLength = newScaleLength + 32;
        noteMatrix = new JPanel(new GridLayout(defaultScale[1]-defaultScale[0], scaleLength));
        for (int row = 0; row < defaultScale[1]-defaultScale[0]; row++) {
            for(int col = 0; col<scaleLength;col++) {
                JToggleButton gb;
                Main.Tuple<Integer, Integer> keyValue = new Main().new Tuple<>(row,col);
                if(buttonHashMap.containsKey(keyValue)){
                    gb = buttonHashMap.get(keyValue);
                }else {
                    gb = createGridButton(row, col);
                    buttonHashMap.put(keyValue, gb);
                }
                if(noteMap.containsKey(keyValue)){
                    gb.setSelected(true);
                    gb.setBackground(new Color(140,35,103));
                    gb.setContentAreaFilled(false);
                    gb.setOpaque(true);
                    System.out.println("Setting Note at: " + keyValue.x + ", " + keyValue.y);
                }
                noteMatrix.add(gb);
            }
        }
        scpane = new JScrollPane(noteMatrix);
        pane.add(scpane);
        pane.revalidate();
        validate();
        return 0;
    }

    /*private int addAudioColumn(){
        noteMatrix.
        for (int row = 0; row < defaultScale[1]-defaultScale[0]; row++) {
            JToggleButton gb = createGridButton(row, scaleLength);
            list.add(gb);
            noteMatrix.add(gb);
        }
        noteMatrix.revalidate();
        validate();
        scaleLength++;
        return scaleLength;
    }*/
}
