MIPS-Simulator
==============

一个java实现的mips汇编模拟器。

2013秋冬《汇编与接口》课程作业的某部分

Memory, Display, Keyboard by tgmerge

## 结构
        
            [Keyboard]
               ↑
    [CPU] ↔ [Memory] ↔ [Disk]
               ↓
            [Display]

## 内存

#### class Memory

Memory是单例的。使用时通过```Memory.getInstance()```获取实例。

| 常量			| 意义					|
|---------------|-----------------------|
| MEMSIZE		| 内存大小				|
| DISPMODE		| 显示模式的内存地址		|
| TEXTMODE		| 显示模式的“文本模式”值	|
| GRAPHICMODE	| 显示模式的“图形模式”值	|
| DISPADDR		| 图形模式显存基地址		|
| TEXTADDR		| 文本模式显存基地址		|
| KEYBADDR		| 键盘输入地址			|

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
 * TODO __load__(String fileName):void  
   从文件载入内存，尚未实现
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