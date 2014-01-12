MIPS-Simulator
==============

一个java实现的mips汇编模拟器。

2013秋冬《汇编与接口》课程作业的某部分

Memory, Display, Keyboard, Cpu by tgmerge
其他不是我写的 \'_>'

## 结构
		  
				[Keyboard]
					↑
	 [CPU] ↔ [Memory] ↔ [Disk]
					↓
				[Display]

## 测试说明

第一步，使用mips-assembler编译MIPS。汇编程序的第一行必须是一条direct imm指令，指示该程序载入内存的位置（不含这一行）。例如：

	direct 4096
	addi $t1, $zero, 10
	addi $t2, $zero, 12
	add  $t3, $t1, $t2

汇编后得到文件内容为

	0000 1000
	2009 000a
	200a 000c
	012a 5820

后三行(2009 000a 200a 000c 012a 5820)的内容将被载入0x1000开始的内存地址。

第二步，使用Memory.load(String fileName)载入刚才的.asm.bin文件。例如```m.load('test.asm.bin')```。

之后即可测试。Cpu.debugRegs()方法可以将所有寄存器的值输出到System.out作为调试用。

Cpu.runCpu()可使CPU持续运行，Cpu.runCpu(int maxCount)将使CPU执行maxCount条指令后终止。
	
## CPU

#### class Cpu

CPU是单例的。使用时通过```Cpu.getInstance()```获取实例即可。

CPU开始运行于内存地址0x1000处。

公共方法：

 * ___getInstance()___:Cpu 
	获取唯一的Cpu对象。如果不存在则会创建一个。
 * runCpu():void
	启动Cpu并持续运行。
 * runCpu(int maxCount):void
    启动Cpu,最多执行maxCount条指令后终止。

## 内存

#### class Memory

Memory是单例的。使用时通过```Memory.getInstance()```获取实例。

| 常量			| 意义					| 初始值	   |
|---------------|-----------------------|----------|
| MEMSIZE		| 内存大小				| 0x400000 |
| DISPMODE		| 显示模式的内存地址		| 0x1FFFFC |
| TEXTMODE		| 显示模式的“文本模式”值	|        0 |
| GRAPHICMODE	| 显示模式的“图形模式”值	|        1 |
| DISPADDR		| 图形模式显存基地址		| 0x200000 |
| TEXTADDR		| 文本模式显存基地址		| 0x380000 |
| KEYBADDR		| 键盘输入地址			| 0x390000 |
|---------------|-----------------------|----------|

公共方法：

 * ___getInstance()___:Memory  
	获取唯一的Memory对象。如果不存在则会创建一个。
 * __read__(int address):int  
	读指定地址。返回32bit。
 * __write__(int address, int value):void  
	写指定地址。写32bit。
 * __writeTextMode__(int x, int y, char c):void  
	向文本模式显存写一个字符c。位置(x,y)。
 * __writeGraphicMode__(int x, int y, int r, int g, int b):void  
	向图形模式显存写一个点c。位置(x,y)，颜色(r,g,b)。r,g,b只能为0或1。
 * __getKeyboardKey__():char  
	读取键盘按键值。若键盘正在按下某键，返回其ASCII码，再次调用将返回0。如果没有键被按下，直接返回0。  
	如果不使用该方法，直接读KEYBADDR处的值也是可以的。
 * __load__(String fileName):void  
	从文件载入内存。载入到的地址是文件的头4byte决定的。
 * __readString__(int address):String  
	从指定地址读出一个字符串(每位8bit)，以'\0'结尾
 * __writeString__(int address, String data):void  
	向指定地址写一个字符串，如果data没有用'\0'结尾，将添加一个

## 显示

#### class Display extends JFrame

Display是单例的。使用时通过```Display.getInstance()```获取实例。

Display是一个由定时器控制重绘的JFrame。

重绘时，根据内存DISPMODE处的值决定图形/文字模式，读取相应位置的内存并绘图。

| 常量			| 意义				|
|---------------|-------------------|
| DISPWIDTH		| 图形模式宽度		|
| DISPHEIGHT	| 图形模式高度		|
| TEXTXNUM		| 文本模式宽度		|
| TEXTYNUM		| 文本模式高度		|
| TEXTWIDTH		| 文本模式字符高度	|
| TEXTHEIGHT	| 文本模式字符高度	|
| TIMERTIME		| 刷新时钟的时钟周期	|

公共方法：

 * ___getInstance()___:Display  
	获取唯一的Display对象。如果不存在则会创建一个。

## 键盘

#### class Keyboard implements KeyListener

Keyboard是单例的。使用时通过```Keyboard.getInstance()```获取实例。

Keyboard作为KeyListener监听按键事件，并将按键码写入内存的KEYBADDR处。

公共方法：

 * ___getInstance()___:Keyboard  
	获取唯一的Keyboard对象。如果不存在则会创建一个。