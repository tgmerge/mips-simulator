public class TestMemory {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		// 使用getInstance方法获取各部件的唯一实例
		Memory m = Memory.getInstance();
		Keyboard k = Keyboard.getInstance();
		Display d = Display.getInstance();
	
		// 测试内存,测试文本模式显示
		m.write(Memory.DISPMODE, Memory.TEXTMODE);
		
		m.writeString(Memory.TEXTADDR, "This is some text.\0");
		System.out.println(m.readString(Memory.TEXTADDR));
	
		m.writeTextMode(10, 6, 'c');
		
		// 测试图形模式显示
		//m.write(Memory.DISPMODE, Memory.GRAPHICMODE);
		for(int xx = 0; xx < 100; xx += 10)
			for(int x = 10; x < 100; x ++)
				m.writeGraphicMode(x, x+xx, 1, 0, 1);
	}

}
