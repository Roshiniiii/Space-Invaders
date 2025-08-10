import javax.swing.*;
public class App {
    public static void main(String[] args) throws Exception{
        int tilesize=32;
        int rows=16;
        int cols=16;
        int boardwidth=tilesize*cols; //32x16=512 px
        int boardheight=tilesize*rows; //32*16=512 px

        JFrame frame= new JFrame("Space Invaders");
        frame.setVisible(true);
        frame.setSize(boardwidth,boardheight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Space_Invaders spaceinvaders =new Space_Invaders();
        frame.add(spaceinvaders);
        frame.pack();
        spaceinvaders.requestFocus();
        frame.setVisible(true);
    }

}
