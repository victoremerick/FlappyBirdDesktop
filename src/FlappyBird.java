import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FlappyBird extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private static final double TIME_FRACTION = 5.0/60.0;
	private static final int HALF_SPACE_BETWEEN_PIPES = 100;
	private static final int PIPE_ENTER_HEIGHT = 50;
	private static final int OBSTACLES_VELOCITY = 2;
	private static final int UP_LIMIT = 150;
	private static final int DOWN_LIMIT = 500;
	private static final int BIRD_W = 50;
	private static final int BIRD_H = 35;
	private static final int BIRD_W_COLISION = 30;
	private static final int BIRD_H_COLISION = 21;
	private static final int TITLE_W = 400;
	private static final int TITLE_H = 100;
	private static final int IMPULSE_TIME = 1;
	private static final double IMPULSE_INTENSITY = 150.0;
	private static final int BACKGROUND_INTERVAL_MOVE = 1;
	private static int BACKGROUND_VELOCITY = 2;
	private static int LIMIT_MOVE_BACKGROUND = 0;

	
	private Font fontNumbers, fontLucidaConsole;
	
	JPanel p = new JPanel();
	
	JLabel background = new JLabel();
	JLabel background1 = new JLabel();
	
	JLabel bird = new JLabel();
	JLabel sair = new JLabel();
	JLabel info = new JLabel("PRESS ANY 'KEY' TO START");
	JLabel title = new JLabel();
	JLabel bird_colision = new JLabel();
	
	JLabel 	Obstacle_0_Top = new JLabel(),
			Obstacle_0_Bottom = new JLabel(),
			Obstacle_0_Top_Enter = new JLabel(),
			Obstacle_0_Bottom_Enter = new JLabel(),
			Obstacle_1_Top = new JLabel(), 
			Obstacle_1_Bottom = new JLabel(), 
			Obstacle_1_Top_Enter = new JLabel(), 
			Obstacle_1_Bottom_Enter = new JLabel(), 
			Obstacle_2_Top = new JLabel(),
			Obstacle_2_Bottom = new JLabel(),
			Obstacle_2_Top_Enter = new JLabel(),
			Obstacle_2_Bottom_Enter = new JLabel();
	
	Random r = new Random(300);
	
	SecureRandom secRandom = new SecureRandom();
	
	int point_of_generation = 200;
	
	int nextObstacle = 0, atualObstacle = 0;
	
	int count_time_impulse = 0;
	double impulso = 0.0;
	double gravity = 10.0;
	double velocity = 0.0;
	
	int count_mover_fundo = 0;
	boolean isbackground1 = true;

	boolean isReset = false;
	
	ImageIcon pipe, pipe_exit, bg, bg1, brd, tt, sairIcon;
	
	int pontos = 0, max_pontos = 0;
	JLabel lPontos = new JLabel("0");
	
	boolean jogo_iniciou = false;
	
	public FlappyBird(String titulo, int altura, int largura){
		this.setSize(largura, altura);
		this.setTitle(titulo);
		
		loadSprites();
		
		initPositioning();
		initPanel();
	}
	
	public static BufferedImage rasterToAlpha(BufferedImage sourceImage, Color... colorsBlackList) {

	    BufferedImage targetImage = new BufferedImage(sourceImage.getWidth(),
	                                                  sourceImage.getHeight(),
	                                                  BufferedImage.TYPE_4BYTE_ABGR);
	    
	    for(int i = 0; i<sourceImage.getWidth(); i++){
	    	for(int j = 0; j<sourceImage.getWidth(); j++){
	    		int pixel = sourceImage.getRGB(i,j);
                int red = (pixel>>16) &0xff;
                int green = (pixel>>8) &0xff;
                int blue = (pixel>>0) &0xff;
                int alpha = 255;
                // Settings opacity to 0 ("transparent color") if the pixel color is equal to a color taken from the "blacklist"
                for(Color color : colorsBlackList) {
                    if(color.getRGB() == pixel) alpha = 0;
                }
                targetImage.setRGB(i,j,(alpha&0x0ff)<<24 | red<<16 | green<<8 | blue);
		    }
	    }
	    
	    return targetImage;
	}
	
	public void loadSprites(){
		String basedir = System.getProperty("user.dir")+"/Imagens/";
		
		String fontes_dir = System.getProperty("user.dir")+"/Fontes/";
		
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(fontes_dir+"flappypassaro.ttf")));

			fontLucidaConsole = new Font("Lucida Console", Font.BOLD, 12);
			info.setFont(fontLucidaConsole);
			
			fontNumbers = new Font("FlappyPassaro", Font.PLAIN, 50);
			lPontos.setFont(fontNumbers);
			
			BufferedImage img1 = ImageIO.read(new File(basedir+"sair.png"));
			BufferedImage imgAlpha = rasterToAlpha(img1, Color.MAGENTA);
			sairIcon = new ImageIcon(new ImageIcon(imgAlpha).getImage().getScaledInstance(16, 16, Image.SCALE_AREA_AVERAGING));
			
			sair.setIcon(sairIcon);
			
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		pipe_exit = new ImageIcon(basedir+"PipeExit.png");
		pipe = new ImageIcon(basedir+"Pipe.png");
		bg = new ImageIcon(new ImageIcon(basedir+"Background.png").getImage().getScaledInstance(480, 640, Image.SCALE_AREA_AVERAGING));
		bg1 = new ImageIcon(new ImageIcon(basedir+"Background.png").getImage().getScaledInstance(480, 640, Image.SCALE_AREA_AVERAGING));
		brd = new ImageIcon(basedir+"Bird.png");
		tt = new ImageIcon(new ImageIcon(basedir+"flappy-bird.png").getImage().getScaledInstance(TITLE_W, TITLE_H, Image.SCALE_AREA_AVERAGING));
		
		title.setIcon(tt);
		bird.setIcon(brd);
		background.setIcon(bg);
		background1.setIcon(bg1);
		
		Obstacle_0_Top_Enter.setIcon(pipe_exit);
		Obstacle_0_Bottom_Enter.setIcon(pipe_exit);
		Obstacle_1_Top_Enter.setIcon(pipe_exit);
		Obstacle_1_Bottom_Enter.setIcon(pipe_exit);
		Obstacle_2_Top_Enter.setIcon(pipe_exit);
		Obstacle_2_Bottom_Enter.setIcon(pipe_exit);
	}
	
	public void initPanel(){
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.add(p);
		p.setLayout(null);
		
		p.add(sair);
		
		p.add(title);
		p.add(info);
		p.add(lPontos);
		p.add(bird_colision);
		p.add(bird);
		
		p.add(Obstacle_0_Bottom);
		p.add(Obstacle_0_Top);
		p.add(Obstacle_0_Bottom_Enter);
		p.add(Obstacle_0_Top_Enter);

		p.add(Obstacle_1_Bottom);
		p.add(Obstacle_1_Top);
		p.add(Obstacle_1_Bottom_Enter);
		p.add(Obstacle_1_Top_Enter);

		p.add(Obstacle_2_Bottom);
		p.add(Obstacle_2_Top);
		p.add(Obstacle_2_Bottom_Enter);
		p.add(Obstacle_2_Top_Enter);
		
		p.add(background);
		p.add(background1);
		
		sair.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				sair.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				sair.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			
			
		});
		
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				baterAsa();
				if(isbackground1){
					if(background.getLocation().x%2!=0){
						LIMIT_MOVE_BACKGROUND = 1;
					}
				}else{
					if(background1.getLocation().x%2!=0){
						LIMIT_MOVE_BACKGROUND = 1;
					}
				}
				BACKGROUND_VELOCITY = 2;
				jogo_iniciou = true;
				
				//lPontos.setVisible(true);
				title.setVisible(false);
				info.setVisible(false);
				
				if(!jogo_iniciou){
					lPontos.setText(pontos+"");
				}
				
				lPontos.setBounds(0, 75, 480, 50);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
	}
	
	public void initPositioning(){
		
		sair.setBounds(454, 10, 16, 16);
		
		lPontos.setHorizontalAlignment(JLabel.CENTER);
		lPontos.setVerticalAlignment(JLabel.CENTER);
		lPontos.setBounds(0, 75, 480, 250);
		lPontos.setForeground(Color.white);
		lPontos.setText(pontos+"");
		
		info.setHorizontalAlignment(JLabel.CENTER);
		info.setVerticalAlignment(JLabel.CENTER);
		info.setBounds(140, 500, 200, 50);
		info.setForeground(Color.gray);
		
		title.setBounds(40, 50, TITLE_W, TITLE_H);
		
		//lPontos.setVisible(false);
		title.setVisible(true);
		info.setVisible(true);
		
		bird.setBounds(240-(BIRD_W/2), 320-(BIRD_H/2), BIRD_W, BIRD_H);
		bird_colision.setBounds(240-(BIRD_W/2)+((BIRD_W-BIRD_W_COLISION)/2), 320-(BIRD_H/2)+((BIRD_H-BIRD_H_COLISION)/2), BIRD_W_COLISION, BIRD_H_COLISION);
		
		Obstacle_0_Bottom.setBounds(0, 0, 0, 0);

		Obstacle_0_Top.setBounds(0, 0, 0, 0);

		Obstacle_0_Bottom_Enter.setBounds(0, 0, 0, 0);
		
		Obstacle_0_Top_Enter.setBounds(0, 0, 0, 0);

		Obstacle_1_Bottom.setBounds(0, 0, 0, 0);
		
		Obstacle_1_Top.setBounds(0, 0, 0, 0);

		Obstacle_1_Bottom_Enter.setBounds(0, 0, 0, 0);
		
		Obstacle_1_Top_Enter.setBounds(0, 0, 0, 0);
		
		Obstacle_2_Bottom.setBounds(0, 0, 0, 0);
		
		Obstacle_2_Top.setBounds(0, 0, 0, 0);
		
		Obstacle_2_Bottom_Enter.setBounds(0, 0, 0, 0);
		
		Obstacle_2_Top_Enter.setBounds(0, 0, 0, 0);
		
		background.setOpaque(true);
		background.setBackground(Color.white);
		background.setBounds(0, 0, 480, 640);
		
		background1.setOpaque(true);
		background1.setBackground(Color.white);
		background1.setBounds(480, 0, 480, 640);
	}
	
	public void resetGame(){
		initPositioning();
		nextObstacle = 0;
		bird.setLocation(240-(BIRD_W/2), 320-(BIRD_H/2));
		velocity = 0.0;
		impulso = 0;
		count_time_impulse = 0;
		isReset = true;
		
		jogo_iniciou = false;
		BACKGROUND_VELOCITY = 1;
		isbackground1 = true;
		LIMIT_MOVE_BACKGROUND = 0;
		
		if(pontos > max_pontos){
			max_pontos = pontos;
		}
		
		pontos = 0;
		
		lPontos.setText(max_pontos+"");
		
		//lPontos.setVisible(false);
		title.setVisible(true);
		info.setVisible(true);

		lPontos.setBounds(0, 75, 480, 250);
		
	}
	
	public void Obstacles_Generator_Logic(){
		if(atualObstacle == 0 && Obstacle_0_Top.getLocation().x == point_of_generation){
			GenerateObstacle();
		}else if(atualObstacle == 1 && Obstacle_1_Top.getLocation().x == point_of_generation){
			GenerateObstacle();
		}else if(atualObstacle == 2 && Obstacle_2_Top.getLocation().x == point_of_generation){
			GenerateObstacle();
		}else if(isReset){
			isReset = false;
			GenerateObstacle();
		}
	}
	
	public void GenerateObstacle(){
		int height = r.nextInt(500);
		while(height < UP_LIMIT || height > DOWN_LIMIT){
			height = r.nextInt(500);
		}
		int max_bottom_pipe_top = height-HALF_SPACE_BETWEEN_PIPES-PIPE_ENTER_HEIGHT;
		int max_top_pipe_bottom = height+HALF_SPACE_BETWEEN_PIPES+PIPE_ENTER_HEIGHT;
		
		if(nextObstacle == 0){
			Obstacle_0_Top.setBounds(480, 0, 100, max_bottom_pipe_top);
			Obstacle_0_Bottom.setBounds(480, max_top_pipe_bottom, 100, 640-max_top_pipe_bottom);
			
			Obstacle_0_Top_Enter.setBounds(480, max_bottom_pipe_top, 100, PIPE_ENTER_HEIGHT);
			Obstacle_0_Bottom_Enter.setBounds(480, max_top_pipe_bottom-PIPE_ENTER_HEIGHT, 100, PIPE_ENTER_HEIGHT);
			
			Obstacle_0_Top.setIcon(new ImageIcon(pipe.getImage().getScaledInstance(100, max_bottom_pipe_top, java.awt.Image.SCALE_SMOOTH)));
			Obstacle_0_Bottom.setIcon(new ImageIcon(pipe.getImage().getScaledInstance(100, 640-max_top_pipe_bottom, java.awt.Image.SCALE_SMOOTH)));
		}else if(nextObstacle == 1){
			Obstacle_1_Top.setBounds(480, 0, 100, max_bottom_pipe_top);
			Obstacle_1_Bottom.setBounds(480, max_top_pipe_bottom, 100, 640-max_top_pipe_bottom);
			
			Obstacle_1_Top_Enter.setBounds(480, max_bottom_pipe_top, 100, PIPE_ENTER_HEIGHT);
			Obstacle_1_Bottom_Enter.setBounds(480, max_top_pipe_bottom-PIPE_ENTER_HEIGHT, 100, PIPE_ENTER_HEIGHT);

			Obstacle_1_Top.setIcon(new ImageIcon(pipe.getImage().getScaledInstance(100, max_bottom_pipe_top, java.awt.Image.SCALE_SMOOTH)));
			Obstacle_1_Bottom.setIcon(new ImageIcon(pipe.getImage().getScaledInstance(100, 640-max_top_pipe_bottom, java.awt.Image.SCALE_SMOOTH)));
		}else if(nextObstacle == 2){
			Obstacle_2_Top.setBounds(480, 0, 100, max_bottom_pipe_top);
			Obstacle_2_Bottom.setBounds(480, max_top_pipe_bottom, 100, 640-max_top_pipe_bottom);
			
			Obstacle_2_Top_Enter.setBounds(480, max_bottom_pipe_top, 100, PIPE_ENTER_HEIGHT);
			Obstacle_2_Bottom_Enter.setBounds(480, max_top_pipe_bottom-PIPE_ENTER_HEIGHT, 100, PIPE_ENTER_HEIGHT);

			Obstacle_2_Top.setIcon(new ImageIcon(pipe.getImage().getScaledInstance(100, max_bottom_pipe_top, java.awt.Image.SCALE_SMOOTH)));
			Obstacle_2_Bottom.setIcon(new ImageIcon(pipe.getImage().getScaledInstance(100, 640-max_top_pipe_bottom, java.awt.Image.SCALE_SMOOTH)));
		}
		atualObstacle = nextObstacle;
		nextObstacle++;
		if(nextObstacle == 3){
			nextObstacle = 0;
		}
	}
	
	public void MoverObstaculos(){
		Obstacle_0_Top.setLocation(Obstacle_0_Top.getLocation().x-OBSTACLES_VELOCITY, 0);
		Obstacle_0_Bottom.setLocation(Obstacle_0_Bottom.getLocation().x-OBSTACLES_VELOCITY, Obstacle_0_Bottom.getLocation().y);
		Obstacle_0_Top_Enter.setLocation(Obstacle_0_Top_Enter.getLocation().x-OBSTACLES_VELOCITY, Obstacle_0_Top_Enter.getLocation().y);
		Obstacle_0_Bottom_Enter.setLocation(Obstacle_0_Bottom_Enter.getLocation().x-OBSTACLES_VELOCITY, Obstacle_0_Bottom_Enter.getLocation().y);
		
		Obstacle_1_Top.setLocation(Obstacle_1_Top.getLocation().x-OBSTACLES_VELOCITY, 0);
		Obstacle_1_Bottom.setLocation(Obstacle_1_Bottom.getLocation().x-OBSTACLES_VELOCITY, Obstacle_1_Bottom.getLocation().y);
		Obstacle_1_Top_Enter.setLocation(Obstacle_1_Top_Enter.getLocation().x-OBSTACLES_VELOCITY, Obstacle_1_Top_Enter.getLocation().y);
		Obstacle_1_Bottom_Enter.setLocation(Obstacle_1_Bottom_Enter.getLocation().x-OBSTACLES_VELOCITY, Obstacle_1_Bottom_Enter.getLocation().y);
		
		Obstacle_2_Top.setLocation(Obstacle_2_Top.getLocation().x-OBSTACLES_VELOCITY, 0);
		Obstacle_2_Bottom.setLocation(Obstacle_2_Bottom.getLocation().x-OBSTACLES_VELOCITY, Obstacle_2_Bottom.getLocation().y);
		Obstacle_2_Top_Enter.setLocation(Obstacle_2_Top_Enter.getLocation().x-OBSTACLES_VELOCITY, Obstacle_2_Top_Enter.getLocation().y);
		Obstacle_2_Bottom_Enter.setLocation(Obstacle_2_Bottom_Enter.getLocation().x-OBSTACLES_VELOCITY, Obstacle_2_Bottom_Enter.getLocation().y);
	}
	
	public void MoverFundo(){
		//System.out.println("BG0: "+!isbackground1+" BG0 Px: "+background.getLocation().x+
		//				   " BG1: "+isbackground1+" BG1 Px: "+background1.getLocation().x);
		if(isbackground1 && background.getLocation().x==LIMIT_MOVE_BACKGROUND){
			if(LIMIT_MOVE_BACKGROUND == 1){
				background1.setLocation(479, 0);
			}else{
				background1.setLocation(480, 0);
			}
			isbackground1 = false;
		}else if(background1.getLocation().x==LIMIT_MOVE_BACKGROUND){
			if(LIMIT_MOVE_BACKGROUND == 1){
				background.setLocation(479, 0);
			}else{
				background.setLocation(480, 0);
			}
			isbackground1 = true;
		}
		
		count_mover_fundo++;
		if(count_mover_fundo == BACKGROUND_INTERVAL_MOVE){
			count_mover_fundo = -1;
			background.setLocation(background.getLocation().x-BACKGROUND_VELOCITY, 0);
			background1.setLocation(background1.getLocation().x-BACKGROUND_VELOCITY, 0);
		}
		
	}
	
	public void calcularfisica(){
		if(!colidiu()){
			velocity = velocity-(gravity-impulso)*TIME_FRACTION;
			bird.setLocation(bird.getLocation().x, new Long(bird.getLocation().y-Math.round(velocity)).intValue());
			bird_colision.setLocation(bird_colision.getLocation().x, new Long(bird_colision.getLocation().y-Math.round(velocity)).intValue());
			count_time_impulse++;
			if(count_time_impulse > IMPULSE_TIME){
				impulso = 0;
				count_time_impulse = 0;
			}
			
			if(bird.getLocation().y> 650){
				resetGame();
			}
			if(atualObstacle == 0 && Obstacle_0_Top.getLocation().x == point_of_generation){
				pontos++;
				lPontos.setText(pontos+"");
			}else if(atualObstacle == 1 && Obstacle_1_Top.getLocation().x == point_of_generation){
				pontos++;
				lPontos.setText(pontos+"");
			}else if(atualObstacle == 2 && Obstacle_2_Top.getLocation().x == point_of_generation){
				pontos++;
				lPontos.setText(pontos+"");
			}
		}
	}
	
	public boolean colidiu(){
			Rectangle TopRect = Obstacle_0_Top.getBounds().union(Obstacle_0_Top_Enter.getBounds());
			Rectangle BottomRect = Obstacle_0_Bottom.getBounds().union(Obstacle_0_Bottom_Enter.getBounds());
			Rectangle birdRect = bird_colision.getBounds();
			
			if(birdRect.intersects(TopRect)){
				resetGame();
				return true;
			}else if(birdRect.intersects(BottomRect)){
				resetGame();
				return true;
			}
			
			TopRect = Obstacle_1_Top.getBounds().union(Obstacle_1_Top_Enter.getBounds());
			BottomRect = Obstacle_1_Bottom.getBounds().union(Obstacle_1_Bottom_Enter.getBounds());
			
			if(birdRect.intersects(TopRect)){
				resetGame();
				return true;
			}else if(birdRect.intersects(BottomRect)){
				resetGame();
				return true;
			}

			TopRect = Obstacle_2_Top.getBounds().union(Obstacle_2_Top_Enter.getBounds());
			BottomRect = Obstacle_2_Bottom.getBounds().union(Obstacle_2_Bottom_Enter.getBounds());
			
			if(birdRect.intersects(TopRect)){
				resetGame();
				return true;
			}else if(birdRect.intersects(BottomRect)){
				resetGame();
				return true;
			}

		return false;
	}
	
	public void baterAsa(){
		if(velocity < 0){
			impulso = IMPULSE_INTENSITY;
			count_time_impulse = 0;
		}else{
			impulso = IMPULSE_INTENSITY/3;
		}
	}
	
	public void GenerateFrame(){
		
		if(jogo_iniciou){
			Obstacles_Generator_Logic();
			MoverObstaculos();
			calcularfisica();
			lPontos.setText(pontos+"");
		}else {
			lPontos.setText(max_pontos+"");
		}
		MoverFundo();
		p.repaint();
		this.repaint();
		
	}
	
	public static void main(String args[]){
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				
				Dimension t = Toolkit.getDefaultToolkit().getScreenSize();
				
				FlappyBird f = new FlappyBird("Flappy Bird", 640, 480);
				f.setLocation((t.width/2)-240, (t.height/2)-320);
				f.setUndecorated(true);
				f.setVisible(true);
				
				f.GenerateObstacle();
				
				while(true){
					try {
						Thread.sleep(16);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					f.GenerateFrame();
				}
				
			}
		});
		t.start();
	}
	
}