import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Keyboard implements KeyListener {
	
	static private Keyboard instance;			// 单例对象
	
	/**
	 * 单例方法
	 * @return 唯一的Keyboard对象
	 */
	static public Keyboard getInstance() {
		
		if(instance == null) {
			instance = new Keyboard();
		}
		return instance;
	}
	
	static private void log(String s) {
		
		System.out.println("[Keyboard]"+s);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		Memory m = Memory.getInstance();
		m.write(Memory.KEYBADDR, e.getKeyCode());
		
		m.writeTextMode(10, 10, (char)e.getKeyCode());
		
		log("[keyPressed]"+e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {

		Memory m = Memory.getInstance();
		m.write(Memory.KEYBADDR, 0);
		
		log("[keyReleased]");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
