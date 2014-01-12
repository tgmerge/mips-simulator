/**
 * 实例化CPU后使用runCpu()启动CPU
 * @author tgmerge
 *
 */
class Cpu {
	
	static private Cpu instance;			// 单例对象
	/**
	 * 单例方法
	 * @return 唯一的Cpu对象
	 */
	static public Cpu getInstance() {
		if(instance == null) {
			instance = new Cpu();
		}
		return instance;
	}
	
	/**
	 * 记录日志用
	 * @param s 日志信息
	 */
	static private void log(String s) {
		System.out.println("[Cpu]"+s);
	}
	
	/**
	 * 获取一个32bit数中的某几个bit
	 * @param value 32bit数据(一般是ir)
	 * @param start 高位
	 * @param end	低位
	 * @return		value[start:end]的值
	 */
	static private int getBit(int value, int start, int end) {
		return value << (31-start) >>> (31-start+end);
	}
	
	/**
	 * 将signed int解析为unsigned long
	 * @param signed
	 * @return unsigned long
	 */
	static private long getUnsigned(int signed) {
		return signed & (-1L >>> 32);
	}

	/** 寄存器,包括PC,EPC **/
	private RegisterFile regs;
	/** 是否已经停机 **/
	private boolean haltFlag;
	/** 内存对象 **/
	private Memory memory;
	/** DEBUG 已经执行的指令条数 **/
	private int count;
	
	/** 初始化 **/
	public Cpu() {
		regs     = new RegisterFile();
		haltFlag = false;
		count    = 0;
		
		//TODO 设置初始运行地址 
		regs.setReg(regs.PC, 0x1000);
		
		// 获取内存对象的实例
		memory = Memory.getInstance();

		log("CPU initialized");
	}
	
	/**
	 * 输出所有寄存器值到System.out
	 */
	public void debugRegs() {
		for(int i = 0; i < 32; i ++) {
			log("R[" + i + "]=" + regs.getReg(i));
		}
		log("R[PC]= " + regs.getReg(regs.PC));
		log("R[IR]= " + regs.getReg(regs.IR));
		log("R[EPC]=" + regs.getReg(regs.EPC));
		log("R[HI]= " + regs.getReg(regs.HI));
		log("R[LO]= " + regs.getReg(regs.LO));
	}
	
	/** 启动CPU **/
	public void runCpu() {
		runCpu(0);
	}
	
	/**
	 * 启动CPU并运行指定数量的指令后终止
	 * @param maxCount 指令数量。为0时将持续运行。
	 */
	public void runCpu(int maxCount) {
		log("CPU started");
		
		// 时钟循环
		try {
			while (haltFlag == false) {
				count ++;
				log("=====Instruction " + count + " =====");
				phaseFetch();
				phaseDecode();
				phaseInterrupt();
				if(maxCount != 0 && count >= maxCount) {
					break;
				}
			}
		} catch (Exception e) {
			log("[CPU ERROR]");
			e.printStackTrace();
		}
		
		debugRegs();
		log("CPU halted");
	}
	
	
	/** P1 取址 **/
	private void phaseFetch() {
		log("P1 fetch");
		
		// IR = mem[PC]
		int pc = regs.getReg(regs.PC);
		int ir = memory.read(pc);
		regs.setReg(regs.IR, ir);
		
		log("  PC=" + Integer.toHexString(pc));
		log("  IR=" + Integer.toHexString(ir));
		
		// PC = PC + 4
		regs.setReg(regs.PC, pc+4);
	}
	
	/** 译码中临时使用的变量 **/
	private int ss, tt, ii, dd, hh;
	
	private void phaseDecode() throws Exception {
		log("P2 decode and execute");
		
		// IR
		int ir = regs.getReg(regs.IR);
		
		// 译码，op = ir[31:26]
		int op    = getBit(ir, 31, 26);
		int aluop = 0;
		log("  OP=" + Integer.toBinaryString(op) + "b");
		
		// 默认进行最多数情况的译码(s, t, i)
		ss = getBit(ir, 25, 21);
		tt = getBit(ir, 20, 16);
		ii = getBit(ir, 15, 0);
		
		// NOOP被解释为SLL $zero, $zero, 0。
		switch (op) {
			
			case 0x00:
				// get aluop = ir[5:0]
				aluop = getBit(ir, 5, 0);
				ss    = getBit(ir, 25, 21);
				tt    = getBit(ir, 20, 16);
				dd    = getBit(ir, 15, 11);
				hh    = getBit(ir, 10, 6);				
				switch (aluop) {
					case 0x20: ADD();     break;
					case 0x21: ADDU();    break;
					case 0x24: AND();     break;
					case 0x1A: DIV();     break;
					case 0x1B: DIVU();    break;
					case 0x08: JR();      break;
					case 0x10: MFHI();    break;
					case 0x12: MFLO();    break;
					case 0x18: MULT();    break;
					case 0x19: MULTU();   break; 
					case 0x25: OR();      break;
					case 0x00: SLL();     break;
					case 0x04: SLLV();    break;
					case 0x2A: SLT();     break;
					case 0x2B: SLTU();    break;
					case 0x03: SRA();     break;
					case 0x02: SRL();     break;
					case 0x06: SRLV();    break;
					case 0x22: SUB();     break;
					case 0x23: SUBU();    break;
					case 0x0C: SYSCALL(); break;
					case 0x26: XOR();     break;
					default:
						throw new Exception("[Cpu ERROR] unknown aluop: " + aluop);
				}
				break;
				
			case 0x01:
				// get aluop = ir[20:16]
				aluop = getBit(ir, 20, 16);
				ss    = getBit(ir, 25, 21);
				ii    = getBit(ir, 15, 0);				
				switch (aluop) {
					case 0x01: BGEZ();   break;
					case 0x11: BGEZAL(); break;
					case 0x00: BLTZ();   break;
					case 0x10: BLTZAL(); break;
					default:
						throw new Exception("[Cpu ERROR] unknown aluop: " + aluop);
				}
				break;
				
			case 0x02: ii = getBit(ir, 25, 0); J(); break;
			case 0x03: ii = getBit(ir, 25, 0); JAL(); break;
			case 0x04: BEQ();   break;
			case 0x05: BNE();   break;
			case 0x06: BLEZ();  break;
			case 0x07: BGTZ();  break;
			case 0x08: ADDI();  break;
			case 0x09: ADDIU(); break;
			case 0x0A: SLTI();  break;
			case 0x0B: SLTIU(); break;
			case 0x0C: ANDI();  break;
			case 0x0D: ORI();   break;
			case 0x0E: XORI();  break;
			case 0x0F: LUI();   break;
			case 0x20: LB();    break;
			case 0x23: LW();    break;
			case 0x28: SB();    break;
			case 0x2B: SW();    break;
			default:
				log("  ERR invalid opcode=" + Integer.toBinaryString(op) + "b");
		}
		
		log("  ^ aluop=" + Integer.toBinaryString(aluop) + "b");
		log("  ^ s=" + ss + " t=" + tt + " d=" + dd + " h=" + hh + " i=" + ii);
	}
	
	private void phaseInterrupt() {
		log("P3 interrupt");
	}
	
	
	// 以下、各种计算
	
	
	private void SW() {
		log(" > LB");
		memory.write(regs.getReg(ss)+ii, regs.getReg(tt));
	}


	private void SB() {
		log(" > SB");
		memory.writeByte(regs.getReg(ss)+ii, (byte) (regs.getReg(tt)&0xFF));
	}


	private void LW() {
		log(" > LW");
		regs.setReg(tt, memory.read(regs.getReg(ss)+ii));
	}


	private void LB() {
		log(" > LB");
		regs.setReg(tt, memory.readByte(regs.getReg(ss)+ii));
	}


	private void LUI() {
		log("  > LUI");
		regs.setReg(tt, ii << 16);
	}


	private void XORI() {
		log("  > XORI");
		regs.setReg(dd, regs.getReg(ss) ^ ii);
	}


	private void ORI() {
		log("  > ORI");
		regs.setReg(dd, regs.getReg(ss) | ii);
	}


	private void ANDI() {
		log("  > ANDI");
		regs.setReg(dd, regs.getReg(ss) & ii);
	}


	private void SLTIU() {
		log("  > SLTIU");
		if (getUnsigned(regs.getReg(ss)) < getUnsigned(ii)) {
			regs.setReg(dd, 1);
		} else {
			regs.setReg(dd, 0);
		}
	}


	private void SLTI() {
		log("  > SLTI");
		if (regs.getReg(ss) < ii) {
			regs.setReg(dd, 1);
		} else {
			regs.setReg(dd, 0);
		}
	}


	private void ADDIU() {
		log("  > ADDIU");
		regs.setReg(tt, regs.getReg(ss) + ii);
	}


	private void ADDI() {
		log("  > ADDI");
		regs.setReg(tt, regs.getReg(ss) + ii);
	}


	private void BGTZ() {
		log("  > BGTZ");
		if (regs.getReg(ss) > 0) {
			regs.setReg(regs.PC, regs.getReg(regs.PC) + (ii << 2));
		}
	}


	private void BLEZ() {
		log("  > BLEZ");
		if (regs.getReg(ss) <= 0) {
			regs.setReg(regs.PC, regs.getReg(regs.PC) + (ii << 2));
		}
	}


	private void BNE() {
		log("  > BNE");
		if (regs.getReg(ss) != regs.getReg(tt)) {
			regs.setReg(regs.PC, regs.getReg(regs.PC) + (ii << 2));
		}
	}


	private void BEQ() {
		log("  > BEQ");
		if (regs.getReg(ss) == regs.getReg(tt)) {
			regs.setReg(regs.PC, regs.getReg(regs.PC) + (ii << 2));
		}
	}


	private void JAL() {
		log("  > JAL");
		regs.setReg(31, regs.getReg(regs.PC) + 4);
		regs.setReg(regs.PC, (regs.getReg(regs.PC) & 0x0F0000000) | (ii << 2));
	}


	private void J() {
		log("  > J");
		regs.setReg(regs.PC, (regs.getReg(regs.PC) & 0x0F0000000) | (ii << 2));
	}


	private void BLTZAL() {
		log("  > BLTZAL");
		if (regs.getReg(ss) < 0) {
			regs.setReg(31, regs.getReg(regs.PC) + 4);
			regs.setReg(regs.PC, regs.getReg(regs.PC)+ (ii << 2));
		}
	}


	private void BLTZ() {
		log("  > BLTZ");
		if (regs.getReg(ss) < 0) {
			regs.setReg(regs.PC, regs.getReg(regs.PC) + (ii << 2));
		}
	}


	private void BGEZAL() {
		log("  > BGEZAL");
		if (regs.getReg(ss) >= 0) {
			regs.setReg(31, regs.getReg(regs.PC) + 4);
			regs.setReg(regs.PC, regs.getReg(regs.PC)+ (ii << 2));
		}
	}


	private void BGEZ() {
		log("  > BGEZ");
		if (regs.getReg(ss) >= 0) {
			regs.setReg(regs.PC, regs.getReg(regs.PC) + (ii << 2));
		}
	}


	private void XOR() {
		log("  > XOR");
		regs.setReg(dd,  regs.getReg(ss) ^ regs.getReg(tt));
	}


	private void SYSCALL() {
		// TODO syscall
	}


	private void SUBU() {
		log("  > SUB");
		regs.setReg(dd, (int) ((getUnsigned(regs.getReg(ss)) - getUnsigned(regs.getReg(tt))) & 0x0FFFFFFFF) );
	}


	private void SUB() {
		log("  > SUB");
		regs.setReg(dd, regs.getReg(ss) - regs.getReg(tt));
	}


	private void SRLV() {
		log("  > SRLV");
		regs.setReg(dd, regs.getReg(tt) >>> regs.getReg(hh));
	}


	private void SRL() {
		log("  > SRL");
		regs.setReg(dd, regs.getReg(tt) >>> hh);
	}


	private void SRA() {
		log("  > SRA");
		regs.setReg(dd, regs.getReg(tt) >> hh);
	}


	private void SLTU() {
		log("  > SLTU");
		if ( getUnsigned(regs.getReg(ss)) < getUnsigned(regs.getReg(tt)) ) {
			regs.setReg(dd, 1);
		} else {
			regs.setReg(dd, 0);
		}
	}


	private void SLT() {
		log("  > SLT");
		if (regs.getReg(ss) < regs.getReg(tt)) {
			regs.setReg(dd, 1);
		} else {
			regs.setReg(dd, 0);
		}
	}


	private void SLLV() {
		log("  > SLLV");
		regs.setReg(dd, regs.getReg(tt) << regs.getReg(ss));
	}


	private void SLL() {
		log("  > SLL");
		regs.setReg(dd, regs.getReg(tt) << hh);
	}


	private void OR() {
		log("  > OR");
		regs.setReg(dd, regs.getReg(ss) | regs.getReg(tt));
	}


	private void MULTU() {
		log("  > MULT");
		regs.setReg(regs.LO, regs.getReg(ss) * regs.getReg(tt));
	}


	private void MULT() {
		log("  > MULT");
		regs.setReg(regs.LO, regs.getReg(ss) * regs.getReg(tt));
		// TODO 添加溢出trap
	}


	private void MFLO() {
		log("  > MFLO");
		regs.setReg(dd, regs.getReg(regs.LO));
	}


	private void MFHI() {
		log("  > MFHI");
		regs.setReg(dd, regs.getReg(regs.HI));
	}


	private void JR() {
		log("  > JR");
		regs.setReg(regs.PC, regs.getReg(ss));
	}


	private void DIVU() {
		log("  > DIV");
		regs.setReg(regs.LO, regs.getReg(ss) / regs.getReg(tt));
		regs.setReg(regs.HI, regs.getReg(ss) % regs.getReg(tt));
	}


	private void DIV() {
		log("  > DIV");
		regs.setReg(regs.LO, regs.getReg(ss) / regs.getReg(tt));
		regs.setReg(regs.HI, regs.getReg(ss) % regs.getReg(tt));
		// TODO 添加溢出trap，情况和ADD,ADDU类似
	}


	private void AND() {
		log("  > AND");
		regs.setReg(dd, regs.getReg(ss) & regs.getReg(tt));
	}


	private void ADDU() {
		log("  > ADDU");
		regs.setReg(dd, regs.getReg(ss) + regs.getReg(tt));
	}


	private void ADD() {
		log("  > ADD");
		regs.setReg(dd, regs.getReg(ss) + regs.getReg(tt));
		// TODO 添加溢出trap
		// ADD和ADDU结果总是相同的。只有溢出的情况不同。
	}
	
}
