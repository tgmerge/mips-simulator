## CPU

* 通用寄存器×32
* EPC寄存器
* PC, IR, 状态寄存器

### 运行流程

1. 取指令
	读mem[PC]到IR
	PC = PC + 1

2. 分析指令
	解码IR ==> opcode, desc, src, imm, etc...

3. 执行指令
	执行指令，使用opcode, desc, etc...

4. 处理中断
	