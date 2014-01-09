
/**
 * 寄存器文件
 * 访问PC和EPC等特殊寄存器的话请使用常量
 * @author tgmerge
 */
public class RegisterFile {
	
	/**
	 * 记录日志用
	 * @param s 日志信息
	 */
	static private void log(String s) {
		//System.out.println("[RegFile]"+s);
	}
	
	/** regs[0-31] : 通用寄存器
	 *  regs[32]   : PC
	 *  regs[33]   : EPC
	 *  regs[34]   : IR
	 *  regs[35]   : HI
	 *  regs[36]   : LO
	 **/
	private int[] regs;
	
	/** 特殊寄存器号常量 **/
	final int ZERO = 0;
	final int PC   = 32;
	final int EPC  = 33;
	final int IR   = 34;
	final int HI   = 35;
	final int LO   = 36;
	
	public RegisterFile() {
		regs = new int[37];
		for (int i = 0; i < regs.length; i ++) {
			regs[i] = 0;
		}
		log("register file initialized.");
	}
	
	public int getReg(int no) {
		log("get reg[" + no + "] = " + regs[no]);
		return regs[no];
	}
	
	public void setReg(int no, int value) {
		if(no != 0) {
			log("set reg[" + no + "] <= " + value);
			regs[no] = value;
		}
	}
}
