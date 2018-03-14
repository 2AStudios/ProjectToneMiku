import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JPanel implements ActionListener {

    private JButton buttons[];
    private String names[] = { "Hide North", "Hide South",
            "Hide East"," Hide West", "Hide Center"};
    private BorderLayout layout;
    private JPanel bigPanel,buttonPanel,scrollPanel;
    private JFrame frame;
    private JSlider slider;
    private JScrollBar scrollBar;

    public MainWindow()
    {
        frame=new JFrame("Border Layout Demo");
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        layout = new BorderLayout (5,5);
        bigPanel=new JPanel();
        buttonPanel=new JPanel();


        buttons = new JButton [names.length];

        for (int count = 0; count<names.length; count++)
        {
            buttons[count] = new JButton(names[count]);
            buttons[count].addActionListener(this);
        }
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(buttons[0], BorderLayout.NORTH);
        buttonPanel.add(buttons[1], BorderLayout.SOUTH);
        buttonPanel.add(buttons[2], BorderLayout.EAST);
        buttonPanel.add(buttons[3], BorderLayout.WEST);
        buttonPanel.add(buttons[4], BorderLayout.CENTER);

        bigPanel.setLayout(new BorderLayout());
        bigPanel.add(buttonPanel,BorderLayout.CENTER);
        scrollPanel=new JPanel();
        slider=new JSlider(JSlider.HORIZONTAL,0,100,0);
        scrollBar=new JScrollBar(JScrollBar.HORIZONTAL, (int)((200+75)/2), 0, 75, 200);
        scrollPanel.add(slider);
        scrollPanel.add(scrollBar);
        scrollPanel.setLayout(new GridLayout(2,1));
        frame.setLayout(new BorderLayout());
        frame.add(buttonPanel,BorderLayout.CENTER);
        frame.add(scrollPanel,BorderLayout.SOUTH);
        frame.setSize(1280,720);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent event)
    {
        for (int count=0; count<buttons.length; count++)
            if (event.getSource() == buttons[count])
                buttons[count].setVisible(false);
            else
                buttons[count].setVisible(true);
        frame.invalidate();
        frame.validate();
    }
}