import java.io.IOException;

public class Test {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {	
		// 使用getInstance方法获取各部件的唯一实例
		Memory   m = Memory.getInstance();
		
		// 将test.asm.bin载入内存0x1000处
		m.load("test.asm.bin");

		Keyboard k = Keyboard.getInstance();
		Display  d = Display.getInstance();
		Cpu      c = Cpu.getInstance();
		
		c.runCpu(10);
		c.debugRegs();
		
	/*
		// 测试内存,测试文本模式显示
		m.write(Memory.DISPMODE, Memory.TEXTMODE);
		
		m.writeString(Memory.TEXTADDR, "This is some text.\0");
		System.out.println(m.readString(Memory.TEXTADDR));
	
		m.writeTextMode(10, 6, 'c');
		
		// 测试图形模式显示
		//m.write(Memory.DISPMODE, Memory.GRAPHICMODE);
		for(int t = 0; t < 100; t += 10)
			for(int x = 10; x < 100; x ++)
				m.writeGraphicMode(x, x+t, 1, 0, 1);
		*/
	}

}
