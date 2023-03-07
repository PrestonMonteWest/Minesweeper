package minesweeper;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

/**
 *
 * @author Preston West
 */
public final class Board extends JPanel implements ActionListener {
    private final Piece[][] board;
    private final int size;
    private Piece[] mines;
    private static final int MIN_SIZE = 2;

    public static int sizeToNumOfMines(int size) {
        if (size < MIN_SIZE) {
            throw new IllegalArgumentException();
        }

        // Edge cases
        if (size < 4) {
            return size * size / 2;
        }

        // Quadratic equation for getting number of mines, dependent on size
        double value = 0.2265625 * size * size - 2.1875 * size + 13;

        return (int)Math.round(value);
    }

    public static Board Factory(int size) {
        Board boardInstance = new Board(size);
        for (Piece mine : boardInstance.mines) {
            mine.addActionListener(boardInstance);
        }

        return boardInstance;
    }

    private Board(int size) {
        board = new Piece[size][size];
        this.size = size;

        setLayout(new GridLayout(size, size));
        fillBoard();
        setAdjacentPieces();
        placeMines(sizeToNumOfMines(size));
    }

    private void fillBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int id = i * size + j;
                Piece temp = Piece.Factory(i, j, id);
                board[i][j] = temp;
                add(temp);
            }
        }
    }

    private void setAdjacentPieces() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int[][] positions = {
                    { i - 1, j - 1 },
                    { i, j - 1 },
                    { i + 1, j - 1 },
                    { i + 1, j },
                    { i + 1, j + 1 },
                    { i, j + 1 },
                    { i - 1, j + 1 },
                    { i - 1, j }
                };

                ArrayList<Piece> adjPieces = new ArrayList<>();

                for (int[] position : positions) {
                    int m = position[0];
                    int n = position[1];
                    boolean rowValid = 0 <= m && m < size;
                    boolean columnValid = 0 <= n && n < size;

                    if (rowValid && columnValid) {
                        adjPieces.add(board[m][n]);
                    }
                }

                board[i][j].setAdjacentPieces(adjPieces);
            }
        }
    }

    private void placeMines(int numOfMines) {
        int temp;
        int i, j;
        int numOfPieces = size * size;

        mines = new Piece[numOfMines];
        for (int k = 0; k < numOfMines; k++) {
            do {
                temp = (int)(Math.random() * numOfPieces);
                i = temp / size;
                j = temp % size;
            } while (board[i][j].isMine());

            board[i][j].setIsMine(true);
            mines[k] = board[i][j];
        }
    }

    public void reset() {
        for (Piece[] row : board) {
            for (Piece piece : row) {
                piece.reset();
            }
        }

        placeMines(sizeToNumOfMines(size));

        for (Piece mine : mines) {
            mine.addActionListener(this);
        }

        ((MainFrame)getTopLevelAncestor()).clock.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Piece[] row : board) {
            for (Piece piece : row) {
                if (piece.isMine() && piece.getState() != Piece.State.FLAGGED) {
                    try {
                        piece.setIcon("/assets/triggered-mine-35px.png");
                        piece.setState(Piece.State.CLICKED);
                    }
                    catch (IOException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if (!piece.isMine() && piece.getState() == Piece.State.FLAGGED) {
                    try {
                        piece.setIcon("/assets/wrong-flag-35px.png");
                    }
                    catch (IOException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                piece.setEnabled(false);
            }
        }

        URL soundFile = getClass().getResource("/assets/explosion.wav");
        try {
            AudioInputStream soundInput = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();

            // Open audio clip and load samples from the audio input stream
            clip.open(soundInput);
            clip.start();
        }
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }

        ((MainFrame)getTopLevelAncestor()).clock.stop();
    }
}
