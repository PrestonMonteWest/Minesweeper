package minesweeper;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author Preston West
 */
public final class Piece extends JButton implements MouseListener {
    public enum State {
        CLICKED, UNCLICKED, FLAGGED, UNKNOWN
    }

    private final int id;
    private ArrayList<Piece> adjPieces;
    private boolean enabled;
    private boolean isMine;
    private int mineCount;
    private State state;

    public static Piece Factory(int i, int j, int id) {
        Piece pieceInstance = new Piece(i, j, id);
        pieceInstance.addMouseListener(pieceInstance);

        return pieceInstance;
    }

    private Piece(int i, int j, int id) {
        this.id = id;
        this.adjPieces = null;
        isMine = false;
        enabled = true;
        mineCount = 0;
        state = State.UNCLICKED;

        int width;
        int height;
        try {
            ImageIcon icon = setIcon("/assets/empty-35px.png");

            width = icon.getIconWidth();
            height = icon.getIconHeight();
        }
        catch (IOException ex) {
            width = 35;
            height = 35;
            System.err.println(ex.getMessage());
        }
        setPreferredSize(new Dimension(width, height));

        // Disable JButton event functionality
        super.setEnabled(false);
    }

    public void setAdjacentPieces(ArrayList<Piece> pieces) {
        adjPieces = pieces;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean setIsMine(boolean newIsMine) {
        if (isMine == newIsMine) {
            return isMine;
        }
        isMine = newIsMine;

        adjPieces.forEach((piece) -> {
            if (isMine) {
                piece.mineCount++;
            }
            else {
                piece.mineCount--;
            }
        });

        return isMine;
    }

    public void setState(State state) {
        this.state = state;
    }

    public ImageIcon setIcon(String assetPath) throws IOException {
        BufferedImage image = ImageIO.read(getClass().getResource(assetPath));
        ImageIcon icon = new ImageIcon(image);

        setIcon(icon);
        setDisabledIcon(icon);

        return icon;
    }

    public State rightClick() {
        if (!enabled) {
            return state;
        }

        try {
            switch (state) {
                case FLAGGED:
                    state = State.UNKNOWN;
                    setIcon("/assets/question-mark-35px.png");
                    break;
                case UNKNOWN:
                    state = State.UNCLICKED;
                    setIcon("/assets/empty-35px.png");
                    break;
                case UNCLICKED:
                    state = State.FLAGGED;
                    setIcon("/assets/flag-35px.png");
                    break;
            }
        }
        catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return state;
    }

    public void leftClick() {
        // If clickable
        if (enabled && state == State.UNCLICKED) {
            if (isMine) {
                // Send action event(s) to board(s)
                ActionEvent event = new ActionEvent(this, id, "trigger");
                for (ActionListener listener : getActionListeners()) {
                    listener.actionPerformed(event);
                }
                return;
            }

            state = State.CLICKED;

            String assetPath = String.format("/assets/%d-35px.png", mineCount);
            try {
                setIcon(assetPath);
            }
            catch (IOException ex) {
                System.err.println(ex.getMessage());
            }

            if (mineCount == 0) {
                adjPieces.forEach((piece) -> {
                    if (piece.state == State.UNCLICKED) {
                        piece.leftClick();
                    }
                });
            }
        }
    }

    public void reset() {
        isMine = false;
        mineCount = 0;
        state = State.UNCLICKED;
        enabled = true;

        int width;
        int height;
        try {
            ImageIcon icon = setIcon("/assets/empty-35px.png");

            width = icon.getIconWidth();
            height = icon.getIconHeight();
        }
        catch (IOException ex) {
            width = 35;
            height = 35;
            System.err.println(ex.getMessage());
        }
        setPreferredSize(new Dimension(width, height));
    }

    public State getState() {
        return state;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int button = e.getButton();
        Clock mainClock = ((MainFrame)getTopLevelAncestor()).clock;
        switch (button) {
            case 1:
                leftClick();

                if (!mainClock.running) {
                    mainClock.start();
                }
                break;
            case 3:
                rightClick();

                if (!mainClock.running) {
                    mainClock.start();
                }
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
