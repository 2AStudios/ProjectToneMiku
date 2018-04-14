import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.*;

public class MainWindow extends JFrame implements ActionListener {
    private JMenuBar menuBar;
    private JMenu fileM, editM, viewM;
    private JScrollPane scpane;
    private JPanel noteMatrix;
    private JMenuItem exitI, cutI, copyI, pasteI, selectI, newI, saveI, loadI, statusI;
    private JToolBar toolBar;

    private JButton playTrackButton, stopTrackButton;
    private JSlider volumeSlider, tempoSlider;
    private HashMap<String,ImageIcon> menuButtonImages = new HashMap<>();

    static int[] defaultScale = new int[]{48,95};//{36,95};
    static int scaleLength = 64;
    final HashMap<Main.Tuple<Integer,Integer>,JToggleButton> buttonHashMap = new HashMap<>();

    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public long tempo = 600;
    public float tempomodifier = 1.0f;
    public long trackLoc = 0;
    JToggleButton trackLocButton;

    public static MidiPlayer app;
    public static AudioPlayerThread playerThread;

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
        scpane = new JScrollPane(noteMatrix);

        pane.add(scpane, BorderLayout.CENTER);
        pane.add(toolBar, BorderLayout.NORTH);

        setVisible(true);
        app = new MidiPlayer("audio/blank.mid",this);
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
        newI = new JMenuItem("New");
        saveI = new JMenuItem("Save");
        loadI = new JMenuItem("Load");
        statusI = new JMenuItem("Status");

        setJMenuBar(menuBar);
        menuBar.add(fileM);
        menuBar.add(editM);
        menuBar.add(viewM);

        fileM.add(newI);
        fileM.add(saveI);
        fileM.add(loadI);
        fileM.add(exitI);

        editM.add(cutI);
        editM.add(copyI);
        editM.add(pasteI);
        editM.add(selectI);

        viewM.add(statusI);

        newI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        saveI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        loadI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        cutI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        copyI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        pasteI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        selectI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        newI.addActionListener(this);
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
        menuButtonImages.put("playbtn_icon", new ImageIcon(new ImageIcon("img/playbtn_icon.png").getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT)));
        menuButtonImages.put("pausebtn_icon", new ImageIcon(new ImageIcon("img/pausebtn_icon.png").getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT)));
        menuButtonImages.put("stopbtn_icon", new ImageIcon(new ImageIcon("img/stopbtn_icon.png").getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT)));

        playTrackButton = new JButton(menuButtonImages.get("playbtn_icon"));
        stopTrackButton = new JButton(menuButtonImages.get("stopbtn_icon"));

        JPanel volumePanel = new JPanel(new GridBagLayout());
        volumePanel.setSize(128,32);
        JLabel volumeLabel = new JLabel("Volume", JLabel.CENTER);
        volumeSlider = new JSlider(0,100,100);
        JLabel tempoLabel = new JLabel("Tempo", JLabel.CENTER);
        tempoSlider = new JSlider(0,100,50);
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        volumePanel.add(tempoLabel);
        volumePanel.add(tempoSlider);
        volumePanel.setBorder(BorderFactory.createTitledBorder("Controls"));


        toolBar.add(playTrackButton);
        toolBar.add(stopTrackButton);
        toolBar.add(volumePanel);

        playTrackButton.addActionListener(this);
        stopTrackButton.addActionListener(this);
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(app != null){
                    app.setVolume((volumeSlider.getValue()+0.0f)/volumeSlider.getMaximum());
                }
            }
        });
        tempoSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(app != null){
                    app.setTempo((tempoSlider.getValue()+0.0f)/tempoSlider.getMaximum()*2.0f);
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() instanceof JMenuItem) {
            JMenuItem choice = (JMenuItem) e.getSource();
            if(choice == newI){
                app = new MidiPlayer("audio/blank.mid",this);
            }else if (choice == saveI) {
                saveFile();
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
                    if(!app.sequencer.isRunning()) {
                        playTrackButton.setIcon(menuButtonImages.get("pausebtn_icon"));
                        app.playTrack(trackLoc, tempomodifier);
                        playerThread = new AudioPlayerThread();
                        playerThread.start();
                    }else{
                        playTrackButton.setIcon(menuButtonImages.get("playbtn_icon"));
                        app.pauseTrack();
                        playerThread.interrupt();
                    }
                }
                //addAudioColumn();
            }else if (choice == stopTrackButton){
                if(app != null){
                    if (app.sequencer.isRunning()){
                        playTrackButton.setIcon(menuButtonImages.get("playbtn_icon"));
                        playerThread.interrupt();
                    }
                    app.pauseTrack();
                }
                trackLoc = 0;
                if(trackLocButton != null) {
                    trackLocButton.setBackground(new Color(142, 219, 216));
                    trackLocButton.setContentAreaFilled(false);
                    trackLocButton.setOpaque(true);
                    trackLocButton = null;
                }
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
            app.pauseTrack();
            app = new MidiPlayer(selectedFile.getAbsolutePath(),this);
        }
    }

    public void saveFile(){
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        jfc.setDialogTitle("Save MIDI file");
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MIDI Audio Data Files", "mid");
        jfc.addChoosableFileFilter(filter);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            System.out.println("Saving: " + selectedFile.getAbsolutePath());
            app.MidiWriter(selectedFile.getAbsolutePath()+".mid");
        }
    }

    private JToggleButton getGridButton(int r, int c) {
        //int index = r * scaleLength + c;
        return buttonHashMap.get(new Main().new Tuple<>(r,c));//list.get(index);
    }

    private JToggleButton createGridButton(final int row, final int col) {
        String noteName = NOTE_NAMES[row % 12];
        final JToggleButton b;
        if(row == defaultScale[1]-defaultScale[0]+2) {
            b = new JToggleButton(String.valueOf("\u2022"));
            b.setBackground(new Color(142,219,216));
            b.setContentAreaFilled(false);
            b.setOpaque(true);
            //b.setFocusable(false);

            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JToggleButton gb = MainWindow.this.getGridButton(row, col);
                    if(trackLocButton != null){
                        trackLocButton.setBackground(new Color(142, 219, 216));
                        trackLocButton.setContentAreaFilled(false);
                        trackLocButton.setOpaque(true);
                    }
                    if (!gb.getBackground().equals(new Color(59, 91, 90))) {
                        gb.setBackground(new Color(59, 91, 90));
                        gb.setContentAreaFilled(false);
                        gb.setOpaque(true);
                        trackLocButton = gb;
                        app.sequencer.setTickPosition((col-1)*tempo);
                        trackLoc = app.sequencer.getMicrosecondPosition();
                    } else {
                        gb.setBackground(new Color(142, 219, 216));
                        gb.setContentAreaFilled(false);
                        gb.setOpaque(true);
                        trackLocButton = null;
                        trackLoc = 0;
                    }
                    //System.out.println("r" + row + ",c" + col + " " + (b == gb) + " " + (b.equals(gb)));
                    //System.out.println(gb.getText());
                }
            });
        }else if(row == defaultScale[1]-defaultScale[0]+1){
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

            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JToggleButton gb = MainWindow.this.getGridButton(row, col);
                    if(!gb.isOpaque()) {
                        gb.setBackground(new Color(140, 35, 103));
                        gb.setContentAreaFilled(false);
                        gb.setOpaque(true);
                        app.addNote(row+defaultScale[0],(col-1)*tempo);
                    }else{
                        gb.setContentAreaFilled(true);
                        gb.setOpaque(false);
                        app.removeNote(row+defaultScale[0],(col-1)*tempo);
                    }
                    //System.out.println("r" + row + ",c" + col + " " + (b == gb) + " " + (b.equals(gb)));
                    //System.out.println(gb.getText());
                }
            });
        }

        return b;
    }

    private JPanel createGridPanel() {
        JPanel p = new JPanel(new GridLayout(defaultScale[1]-defaultScale[0]+2, scaleLength+1));
        for (int row = defaultScale[1]-defaultScale[0]+2; row > 0 ; row--) {
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
        scaleLength = newScaleLength;
        noteMatrix = new JPanel(new GridLayout(defaultScale[1]-defaultScale[0]+2, scaleLength+1));

        JDialog dlg = new JDialog(this, "Progress Dialog", true);
        JProgressBar dpb = new JProgressBar(0, noteMap.size());
        JLabel jl = new JLabel("Loading Notes: 0 of " + noteMap.size());
        dlg.add(BorderLayout.CENTER, dpb);
        dlg.add(BorderLayout.NORTH, jl);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setSize(300, 75);
        dlg.setLocationRelativeTo(this);

        Thread t = new Thread(new Runnable() {
            public void run() {
                int noteCount = 0;
                for (int row = defaultScale[1]-defaultScale[0]+2; row > 0 ; row--) {
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
                            noteCount++;
                            jl.setText("Loading Notes: " + noteCount +" of " + noteMap.size());
                            dpb.setValue(noteCount);
                            //System.out.println("Setting Note at: " + keyValue.x + ", " + keyValue.y);
                        }else{
                            if(gb.getText().equals(String.valueOf("\u266A"))) {
                                gb.setContentAreaFilled(true);
                                gb.setOpaque(false);
                                gb.setSelected(false);
                            }
                        }
                        noteMatrix.add(gb);
                    }
                }
                scpane = new JScrollPane(noteMatrix);
                scpane.getVerticalScrollBar().setUnitIncrement(16);

                if(app != null){
                    if (!app.sequencer.isRunning())
                        app.pauseTrack();
                }
                trackLoc = 0;
                if(trackLocButton != null) {
                    trackLocButton.setBackground(new Color(142, 219, 216));
                    trackLocButton.setContentAreaFilled(false);
                    trackLocButton.setOpaque(true);
                    trackLocButton = null;
                }

                pane.add(scpane);
                pane.revalidate();
                validate();
                dlg.setVisible(false);
                dlg.dispose();
            }
        });
        t.start();
        dlg.setVisible(true);
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

    class AudioPlayerThread extends Thread{

        public AudioPlayerThread(){

        }
        public void run(){
            while(app.sequencer.isRunning()) {
                scpane.getHorizontalScrollBar().setValue((int)(scpane.getHorizontalScrollBar().getMaximum()*((double)(app.sequencer.getMicrosecondPosition()+tempo)/app.sequencer.getMicrosecondLength())));
                Main.Tuple<Integer, Integer> keyValue = new Main().new Tuple<>(defaultScale[1]-defaultScale[0]+2,(int)(app.sequencer.getTickPosition()/tempo)+1);
                JToggleButton tempLine = buttonHashMap.get(keyValue);
                if(trackLocButton != null && trackLocButton != tempLine){
                    tempLine.setSelected(true);
                    tempLine.setBackground(new Color(59,91,90));
                    tempLine.setContentAreaFilled(false);
                    tempLine.setOpaque(true);
                    trackLocButton.setBackground(new Color(142,219,216));
                    trackLocButton.setContentAreaFilled(false);
                    trackLocButton.setOpaque(true);
                    trackLocButton = tempLine;
                }else{
                    tempLine.setSelected(true);
                    tempLine.setBackground(new Color(59,91,90));
                    tempLine.setContentAreaFilled(false);
                    tempLine.setOpaque(true);
                    trackLocButton = tempLine;
                }

                //System.out.println("Moving Pane: " + scpane.getHorizontalScrollBar().getValue());
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) { }
            }
        }
    }
}


