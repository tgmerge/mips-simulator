import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;


public class Memory {
	
	static private Memory instance;			// 单例对象
	
	/**
	 * 单例方法
	 * @return 唯一的Memory对象
	 */
	static public Memory getInstance() {
		
		if(instance == null) {
			instance = new Memory();
		}
		return instance;
	}
	
	static private void log(String s) {
		
		//System.out.println("[Memory]"+s);
	}

	
	
	static public final int
		MEMSIZE  = 4194304,
		DISPMODE = 0x1FFFFC,
			TEXTMODE    = 0,
			GRAPHICMODE = 1,
		DISPADDR = 0x200000,
		TEXTADDR = 0x380000,
		KEYBADDR = 0x390000;
	
	byte[] mem;
	
	public Memory() {
		
		mem = new byte[MEMSIZE];
		log("[Memory]Memory created.");
	}
	
	/**
	 * 从指定内存地址读32bit整数
	 * @param address	地址
	 * @return	地址处的值
	 */
	public int read(int address) {
		
		int v0 = (readByte(address+3) & 0xff) << 24;
		int v1 = (readByte(address+2) & 0xff) << 16;
		int v2 = (readByte(address+1) & 0xff) << 8;
		int v3 = (readByte(address)   & 0xff);
		int v  = v0+v1+v2+v3;
		
		log("[read]"+Integer.toHexString(address)+"h=>"+Integer.toHexString(v) + "h");
		
		return v;
	}
	
	/**
	 * 从指定内存读一个byte
	 * @param address	地址
	 * @return 地质处的值
	 */
	public byte readByte(int address) {
		
		if(address < 0 || address >= MEMSIZE) {
			log("[readByte]Error: invalid memory address: " + Integer.toHexString(address) + "h");
			return 0;
		}
		
		return mem[address];
	}
	
	/**
	 * 向指定内存地址写一个32bit整数
	 * @param address	地址
	 * @param value		要写入的整数
	 */
	public void write(int address, int value) {
		
		if(address < 0 || address > MEMSIZE-4) {
			log("[write]Error: invalid memory address: " + Integer.toHexString(address) + "h");
			return;
		}

		writeByte(address  , (byte)(value)       );
		writeByte(address+1, (byte)(value >>> 8 ));
		writeByte(address+2, (byte)(value >>> 16));
		writeByte(address+3, (byte)(value >>> 24));
		
		log("[write]"+Integer.toHexString(address)+"h<="+Integer.toHexString(value) + "h");
	}
	
	/**
	 * 向指定内存写一个byte
	 * @param address	地址
	 * @param value     值
	 */
	public void writeByte(int address, byte value) {
		
		if(address < 0 || address >= MEMSIZE) {
			log("[writeByte]Error: invalid memory address: " + Integer.toHexString(address) + "h");
			return;
		}
		
		mem[address] = value;
	}
	
	/**
	 * 向文本模式显存的指定位置写一个字符
	 * @param x 横坐标
	 * @param y 纵坐标
	 * @param c 字符
	 */
	public void writeTextMode(int x, int y, char c) {
		writeByte(TEXTADDR + y*Display.TEXTXNUM + x, (byte)c);
	}
	
	/**
	 * 向图形模式显存的指定位置画一个点
	 * @param x 横坐标
	 * @param y 纵坐标
	 * @param r 可以为0/1
	 * @param g 可以为0/1
	 * @param b 可以为0/1
	 */
	public void writeGraphicMode(int x, int y, int r, int g, int b) {
		int addr = DISPADDR + (y*Display.DISPWIDTH + x)/2;
		byte t = readByte(addr);
		if(x % 2 == 0) {
			t &= 0x0F;
			if(r != 0) t |= 0x80;
			if(g != 0) t |= 0x40;
			if(b != 0) t |= 0x20;
		} else {
			t &= 0xF0;
			if(r != 0) t |= 0x08;
			if(g != 0) t |= 0x04;
			if(b != 0) t |= 0x02;
		}
		writeByte(addr, t);
	}
	
	/**
	 * 读取键盘按键值
	 * @return 若键盘正在按下某键，返回其ASCII码，再次调用将返回0。如果没有键被按下，直接返回0。
	 */
	public char getKeyboardKey() {
		
		char c = (char)readByte(KEYBADDR);
		writeByte(KEYBADDR, (byte) 0);
		
		log("[getKeyboardKey]=>" + c);
		
		return c;
	}
	
	/**
	 * 从文件载入内存。文件开头的4byte指定了要载入的地址。
	 * @param fileName
	 * @throws IOException 
	 */
	public void load(String fileName) throws IOException {

		DataInputStream in = new DataInputStream(new FileInputStream(fileName));
		int addr = in.readInt();
		int data = 0;
		
		try {
			while (true) {
				data = in.readInt();
				write(addr, data);
				addr += 4;
			}
		} catch (EOFException e) {
			in.close();
			return;
		}
	}
	
	/**
	 * 从address开始的内存地址开始读一个字符串，不含结尾的\0
	 * @param address 起始地址
	 * @return 读出的字符串。
	 */
	public String readString(int address) {
		
		String s = "";
		for(int a = address; readByte(a) != 0; a ++) {
			s += (char)readByte(a);
		}
		
		log("[readString]"+Integer.toHexString(address)+"h=>"+s);
		
		return s;
	}
	
	/**
	 * 向address写入一个字符串，如果串末尾不是\0，将在最后写入一个\0
	 * @param address 地址
	 * @param data    要写的字符串
	 */
	public void writeString(int address, String data) {
		
		byte[] s = data.getBytes();
		int i;
		
		for(i = 0; i < s.length; i ++) {
			writeByte(address+i, s[i]);
		}
		
		if(s[i-1] != 0) {
			writeByte(address+i, (byte)0);
			log("[writeString]added 0 at the end.");
		}
		
		log("[writeString]"+Integer.toHexString(address)+"h<="+data);
		
		return;
	}

}
