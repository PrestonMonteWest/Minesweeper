// Name: Preston West
// Section: MCIS5103
// Student ID: 909556994

package minesweeper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.WindowConstants;

/**
 *
 * @author Preston West
 */
public final class MainFrame extends JFrame implements ActionListener
{
    Clock clock;
    private String mode;
    private Board board = null;
    private static final String EASY = "Easy";
    private static final String MEDIUM = "Medium";
    private static final String HARD = "Hard";
    
    public MainFrame(String title)
    {
        super(title);
        setLayout(new BorderLayout());
        mode = EASY;
        setBoard();
        
        // One second intervals
        clock = Clock.Factory(1000);
        add(clock, BorderLayout.NORTH);
    }

    public static void main(String[] args)
    {
        MainFrame frame = new MainFrame("MineSweeper"); 
        
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        // Setting up the options menu
        JMenu options = new JMenu("Options");
        JMenuItem reset = new JMenuItem("Reset");
        reset.getAccessibleContext().setAccessibleDescription(
                "Reset the game"
        );
        reset.addActionListener(frame);
        options.add(reset);
        
        options.addSeparator();
        
        // A group of radio button menu items
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem easy = new JRadioButtonMenuItem(EASY);
        JRadioButtonMenuItem medium = new JRadioButtonMenuItem(MEDIUM);
        JRadioButtonMenuItem hard = new JRadioButtonMenuItem(HARD);
        
        easy.addActionListener(frame);
        group.add(easy);
        options.add(easy);
        
        medium.addActionListener(frame);
        group.add(medium);
        options.add(medium);
        
        hard.addActionListener(frame);
        group.add(hard);
        options.add(hard);
        
        easy.setSelected(true);
        menuBar.add(options);
        frame.pack();
        
        // Centers JFrame window
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dim.width - frame.getSize().width) / 2;
        int y = (dim.height - frame.getSize().height) / 2;
        frame.setLocation(x, y);
        
        // Exit app on close
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        frame.setResizable(false);
        
        // Show window/components
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        String command = event.getActionCommand();
        
        if (command.equals("Reset"))
        {
            board.reset();
            return;
        }
        
        mode = command;
        // TODO: run setBoard on separate thread
        setBoard();
        // TODO: recenter frame
        pack();
    }
    
    private void setBoard()
    {
        if (board != null)
        {
            remove(board);
        }
        
        int size;
        switch (mode)
        {
            case EASY:
                size = 8;
                break;
            case MEDIUM:
                size = 16;
                break;
            case HARD:
                size = 24;
                break;
            default:
                size = 8;
        }
        
        board = Board.Factory(size);
        add(board, BorderLayout.CENTER);
    }
}