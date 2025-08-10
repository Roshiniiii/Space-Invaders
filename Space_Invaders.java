import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Space_Invaders extends JPanel implements ActionListener, KeyListener{
    class block{
        int x,y,width,height;
        Image img;
        boolean alive= true; //for the aliens
        boolean used= false; //for the bullets

        block(int x, int y, int width, int height,Image img){
            this.x=x;
            this.y=y;
            this.height=height;
            this.width=width;
            this.img=img;
        }
    }
    //BOARD
    int tilesize=32;
    int rows=16;
    int cols=16;
    int boardwidth=tilesize*cols;
    int boardheight=tilesize*rows;

    Image shipImg;
    Image alienWhiteImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    //ship
    int shipwidth= tilesize*2; //two squares 64px
    int shipheight= tilesize; //one square 32 px
    int shipX=tilesize*cols/2-tilesize;
    int shipY=boardheight-tilesize*2;
    int shipvelocityX=tilesize;

    //aliens
    ArrayList<block> alienarray;
    int alienwidth=tilesize*2;
    int alienheight=tilesize;
    int alienX=tilesize;
    int alienY=tilesize;

    int alienrows=2;
    int aliencols=3;
    int aliencount=0; //number of aliens to be defeated
    int alienvelocityX=1; //moving the alien 1px at a time

    //bullets
    ArrayList<block> bulletarray;
    int bulletwidth=tilesize/8;
    int bulletheight=tilesize/2;
    int bulletvelocityY= -10;

    block ship;

    Timer gameloop;
    int score=0;
    boolean gameover=false;

    Space_Invaders(){
        setPreferredSize(new Dimension(boardwidth,boardheight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        //load images
        shipImg= new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alienWhiteImg= new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienCyanImg= new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienMagentaImg= new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alienYellowImg= new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();

        alienImgArray= new ArrayList<Image>();
        alienImgArray.add(alienWhiteImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship=new block(shipX, shipY,shipwidth,shipheight,shipImg);
        alienarray= new ArrayList<block>();
        bulletarray=new ArrayList<block>();

        //game timer
        gameloop=new Timer(1000/60, this); //60 framers per sec
        createaliens();
        gameloop.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        //drawing the ship
        g.drawImage(ship.img,ship.x,ship.y,ship.width,ship.height,null);

        //drawing the aliens
        for(int i=0;i<alienarray.size();i++){
            block alien= alienarray.get(i);
            if(alien.alive){
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        //bullets
        g.setColor(Color.white);
        for(int i=0;i<bulletarray.size();i++){
            block bullet=bulletarray.get(i);
            if(!bullet.used){
                //g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height); //hollow rectangle bullets
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height); //filled rectangle bullets
            }
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if(gameover){
            g.drawString("GAME OVER: "+String.valueOf(score), 10,35);
        }
        else{
            g.drawString(String.valueOf(score),10, 35);
        }
    }

    public void move(){
        //aliens
        for(int i=0;i<alienarray.size();i++){
            block alien=alienarray.get(i);
            if(alien.alive){
                alien.x+=alienvelocityX;

                //when the aliens touch the borders
                if(alien.x+alien.width>=boardwidth || alien.x<=0){
                    alienvelocityX*=-1;
                    alien.x+=alienvelocityX*2;

                    //move the aliens down by one row
                    for(int j=0;j<alienarray.size();j++){
                        alienarray.get(j).y+=alienheight;
                    }
                }
                //check for game over
                if(alien.y>=ship.y){
                    gameover=true;
                }
            }
        }

        //bullets
        for(int i=0;i<bulletarray.size();i++){
            block bullet = bulletarray.get(i);
            bullet.y+=bulletvelocityY;

            //bullet collision with aliens
            for(int j=0;j<alienarray.size();j++){
                block alien=alienarray.get(j);
                if(!bullet.used && alien.alive && detectcollision(bullet, alien)){
                    bullet.used=true;
                    alien.alive=false;
                    aliencount--;
                    score+=100;
                }
            }
        }
        //clear the bullets in the array
        while(bulletarray.size()>0 && (bulletarray.get(0).used || bulletarray.get(0).y<0)){
            bulletarray.remove(0); //emoves the first element
        }

        //next level
        if(aliencount==0){
            score+=aliencols*alienrows+100; //bonus points for clearing a level
            //increase the aliens by one row and column
            aliencols=Math.min(aliencols+1,cols/2-2); //cap column=6
            alienrows=Math.min(alienrows+1,rows-6); //cap row=10
            alienarray.clear();
            bulletarray.clear();
            alienvelocityX=1;
            createaliens();
        }
    }
    public void createaliens(){
        Random random= new Random();
        for(int i=0;i< alienrows;i++){
            for(int j=0;j<aliencols;j++){
                int randomImgIndex=random.nextInt(alienImgArray.size());
                block alien= new block(
                    alienX+j*alienwidth,
                    alienY+i*alienheight,
                    alienwidth,
                    alienheight,
                    alienImgArray.get(randomImgIndex)
                );
                alienarray.add(alien);
            }
        }
        aliencount=alienarray.size();
    }
    public boolean detectcollision(block a, block b){
        return a.x<b.x+b.width && a.x+a.width> b.x && a.y<b.y+b.height && a.y+a.height>b.y;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameover){
            gameloop.stop();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        if(gameover){ //resetting the game
            ship.x=shipX;
            alienarray.clear();
            bulletarray.clear();
            score=0;
            alienvelocityX=1;
            aliencols=3;
            alienrows=2;
            gameover=false;
            createaliens();
            gameloop.start();
        }
        else if(e.getKeyCode()==KeyEvent.VK_LEFT && ship.x - shipvelocityX>=0){
            ship.x-=shipvelocityX; //move left by one tile within the board
        }
        else if(e.getKeyCode()==KeyEvent.VK_RIGHT && ship.x+shipwidth+shipvelocityX<=boardwidth){
            ship.x+=shipvelocityX; //move right by one tile within the board
        }
        else if(e.getKeyCode()==KeyEvent.VK_SPACE){
            block bullet= new block(ship.x+shipwidth*15/32, ship.y, bulletwidth, bulletheight, null);
            bulletarray.add(bullet);
        }
    }
}
