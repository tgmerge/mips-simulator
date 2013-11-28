import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Display extends JFrame implements ActionListener {
	
	static private Display instance;			// 单例对象
	
	/**
	 * 单例方法
	 * @return 唯一的Display对象
	 */
	static public Display getInstance() {
		
		if(instance == null) {
			instance = new Display();
		}
		return instance;
	}
	
	static private void log(String s) {
		
		//System.out.println("[Display]"+s);
	}
	
	public static final int
		DISPWIDTH  = 640,
		DISPHEIGHT = 480,
		TEXTXNUM   = 80,
		TEXTYNUM   = 25,
		TEXTWIDTH  = 8,
		TEXTHEIGHT = 19,
		TIMERTIME  = 100;
	
	private Memory memory;
	private Timer timer;
	private JPanel panel;
	private Keyboard keyboard;
	
	public Display() {
		memory = Memory.getInstance();
		timer = new Timer(TIMERTIME, this);
		panel = new JPanel();
		keyboard = Keyboard.getInstance();

		this.setLayout(new FlowLayout());
		this.setSize(DISPWIDTH+30, DISPHEIGHT+50);
		this.setResizable(false);
		
		panel.setPreferredSize(new Dimension(DISPWIDTH, DISPHEIGHT));
		panel.setBackground(Color.BLACK);
		this.add(panel);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addKeyListener(keyboard);
		
		timer.setRepeats(true);
		timer.start();
		
		this.setVisible(true);
	
	}

	private void redraw() {
		
		log("[redraw]");
		
		int mode = memory.read(Memory.DISPMODE);
		if(mode == Memory.TEXTMODE) {
			redrawText();
		} else if(mode == Memory.GRAPHICMODE) {
			redrawGraphic();
		}
	}
	
	private void redrawText() {
		
		log("[redrawText]");
		
		int x, y;
		char c[] = new char[1];
		Graphics g = panel.getGraphics();
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setFont(new Font("Consolas", Font.PLAIN, 14));
		
		g.setColor(Color.WHITE);
		
		for(y = 0; y < TEXTYNUM; y ++ ) {
			for(x = 0; x < TEXTXNUM; x ++) {
				c[0] = (char)(memory.readByte(Memory.TEXTADDR + y*TEXTXNUM + x));
				g.drawChars(c, 0, 1, x*TEXTWIDTH, (y+1)*TEXTHEIGHT);
			}
		}
	}
	
	private void redrawGraphic() {
		
		log("[redrawGraphic]");
		BufferedImage image = new BufferedImage(DISPWIDTH, DISPHEIGHT, BufferedImage.TYPE_INT_RGB);
		
		int x, y, rgb;
		byte b;
		Graphics g = panel.getGraphics();
		
		for(y = 0; y < DISPHEIGHT; y ++) {
			for(x = 0; x < DISPWIDTH; x += 2) {
				b = memory.readByte(Memory.DISPADDR + (y*DISPWIDTH + x)/2);
				rgb = 0x00000000;
				if((b & 0x80) != 0) rgb |= 0x00FF0000;
				if((b & 0x40) != 0) rgb |= 0x0000FF00;
				if((b & 0x20) != 0) rgb |= 0x000000FF;
				image.setRGB(x, y, rgb);
				rgb = 0x00000000;
				if((b & 0x08) != 0) rgb |= 0x00FF0000;
				if((b & 0x04) != 0) rgb |= 0x0000FF00;
				if((b & 0x02) != 0) rgb |= 0x000000FF;
				image.setRGB(x+1, y, rgb);
			}
		}
		
		g.drawImage(image, 0, 0, this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timer) {
			redraw();
		}
	}
	
}
