import java.io.*;
public class Cpu{
	
	public final static int  KEYadr = 2;
    public final static int SYSCALLADDR = 0x100;
    public final static int ABORTADDR = 0x1000;
    public final static int WID = 80;
    public final static int HEI = 15;   
	
    int REGNUM = 36, REG_HI = 32, REG_LO = 33, CAUSE=34,EPC=35,
               WIDTH = 80, HEIGHT = 25,
               END_MEM = 0x10000, // 64Kb
               KERNEL_MEM = 0, USER_MEM = 0x2000, STATIC_MEM = 0x5000,
               MAIN_MEM = 0x7000, DISP_MEM = END_MEM - WIDTH*HEIGHT ;

    boolean  Remark=false;
    int PC_Remark=0;
    int PC,IR, MDR;
    //int endpoint;
    int Reg[] = new int[REGNUM];
    //int  CurrSize;
    //boolean INFflag;
    byte Memory[] = new byte[END_MEM];
    //boolean loadedflag;
    boolean mem_modified_flag;
    long mem_modified_addr;

    public int getIR(int PC) {
        int ir;
        ir = (Memory[PC+0] << 24) |
             (Memory[PC+1] << 16) |
             (Memory[PC+2] << 8) |
             (Memory[PC+3]);
        return ir;
    } 
    public int getPC() {
    return PC;
    }

    public int getIC() {
    return (PC - USER_MEM) / 4;
    }

    public boolean boot(String fn)
    {
       try{
           File file = new File(fn);
           if(!file.exists())
               return false;
          DataInputStream in = 
          new DataInputStream(
          new FileInputStream(
          new File(fn)));
          in.read(Memory);
          in.close();
       }catch(Exception ex){  }
       PC=0;
       return true;
    }

    public int execute() {
    int op, rs, rt, rd, dat, udat, adr, shmt, fun;
    long t;
    IR = getIR(PC);//IR?

    /*if (PC == endpoint) {
        return 2;
    }*/

    boolean mem_modified_flag = false;
    
    PC += 4;
    op = (IR >> 26) & 0x3F;
    rs = (IR >> 21) & 0x1F;
    rt = (IR >> 16) & 0x1F;
    rd = (IR >> 11) & 0x1F;
    shmt = (IR >> 6) & 0x1F;
    fun = IR & 0x3F;
    dat = (short)(IR & 0xFFFF);
    udat = IR & 0xFFFF;
    adr = (IR & 0x3FFFFFF) << 2;

    int i,j;
    if(Remark==true&&(PC-4)==PC_Remark){
    	//Runtime.getRuntime().exec("cmd /c start cls "); ???????????  	
    	//system("cls");//??
           	for(i=0;i<HEI;i++){                   //HEI=15?
           			for(j=0;j<WID;j++)            //WID=80?
           				//printf("%c",Memory[DISP_MEM+i*WID+j]);
           			    //printf("\n");
           			    System.out.println(Memory[DISP_MEM+i*WID+j]);
           		}
           	Remark=false;
           }

    switch (op) {
        case 0:
            switch (fun) {
                case 12:
                	 //Reg[31] = PC + 4; //enable delay slot
                	 Reg[31] = PC; //disable delay slot
                	 PC_Remark=PC;
                	 Remark=true;
                	 PC =SYSCALLADDR;
                	 break;
                case 16:    //mfhi
                    Reg[rd] = Reg[REG_HI];
                    break;

                case 18:    //mflo
                    Reg[rd] = Reg[REG_LO];
                    break;

                case 24:    //mult
                    t = Reg[rs] * Reg[rt];
                    Reg[REG_HI] = (int) t >> 32;
                    Reg[REG_LO] = (int) t & 0xFFFFFFFF;
                    break;

                case 25:    //multu
                    t = Reg[rs] * Reg[rt];
                    Reg[REG_HI] = (int) t >> 32;
                    Reg[REG_LO] = (int) t & 0xFFFFFFFF;
                    break;

                case 26:    //div
                    Reg[REG_HI] = Reg[rs] % Reg[rt];
                    Reg[REG_LO] = Reg[rs] / Reg[rt];
                    break;

                case 27:    //divu
                    Reg[REG_HI] = Reg[rs] % Reg[rt];
                    Reg[REG_LO] = Reg[rs] / Reg[rt];
                    break;

                case 32:    //add
                    Reg[rd] = Reg[rs] + Reg[rt];
                    break;

                case 33:    //addu
                    Reg[rd] = Reg[rs] + Reg[rt];
                    break;

                case 34:    //sub
                    Reg[rd] = Reg[rs] - Reg[rt];
                    break;

                case 35:    //subu
                    Reg[rd] = Reg[rs] - Reg[rt];
                    break;

                case 36:    //and
                    Reg[rd] = Reg[rs] & Reg[rt];
                    break;

                case 37:    //or
                    Reg[rd] = Reg[rs] | Reg[rt];
                    break;

                case 38:    //xor
                    Reg[rd] = Reg[rs] ^ Reg[rt];
                    break;

                case 39:    //nor
                    Reg[rd] = ~(Reg[rs] | Reg[rt]);
                    break;

                case 42:    //slt
                    Reg[rd] = Reg[rs] < Reg[rt] ? 1 : 0;
                    break;

                case 43:    //sltu
                    Reg[rd] = Reg[rs] < Reg[rt] ? 1 : 0;
                    break;

                case 0:    //sll
                    Reg[rd] = Reg[rt] << shmt;
                    break;
                   
                case 2:    //srl
                    Reg[rd] = Reg[rt] >> shmt;
                    break;

                case 3:    //sra
                    Reg[rd] = Reg[rt] >> shmt;
                    System.out.println(shmt);
                    //cout << shmt;
                    break;

                case 4:    //sllv
                    Reg[rd] = Reg[rt] << Reg[rs];
                    break;

                case 6:    //srlv
                    Reg[rd] = Reg[rt] >> Reg[rs];
                    break;

                case 8:    //jr
                    PC =(int) Reg[rs];
                    break;
            }
            break;

        case 1:
            switch (rt) {
                case 0:     //bltz
                    if (rs < 0)
                        PC += (dat << 2);
                    break;

                case 1:     //bgez
                    if (rs >= 0)
                        PC += (dat << 2);
                    break;

                case 16:    //bltzal
                    if (rs < 0) {
                        //Reg[31] = PC + 4; //enable delay slot
                        Reg[31] = PC; //disable delay slot
                        PC += (dat << 2);
                    }
                    break;

                case 17:    //bgezal
                    if (rs >= 0) {
                        //Reg[31] = PC + 4; //enable delay slot
                        Reg[31] = PC; //disable delay slot
                        PC += (dat << 2);
                    }
                    break;
            }
            break;

        case 4:     //beq
            if (Reg[rs] == Reg[rt])
                PC += (dat << 2);
            break;

        case 5:     //bne
            if (Reg[rs] != Reg[rt])
                PC += (dat << 2);
            break;

        case 6:     //blez
            if (Reg[rs] <= 0)
                PC += (dat << 2);
            break;

        case 7:     //bgtz
            if (Reg[rs] > 0)
                PC += (dat << 2);
            break;

        case 8:     //addi
            Reg[rt] = Reg[rs] + dat;
            break;

        case 9:     //addiu
            Reg[rt] = Reg[rs] + dat;
            //System.out.println(dat);
            break;

        case 10:     //slti
            Reg[rt] = Reg[rs] < dat ? 1 : 0;
            //System.out.println(dat);
            break;
            
        case 11:     //sltiu
            Reg[rt] = Reg[rs] < dat ? 1 : 0;
            //System.out.println(dat);
            break;
            
        case 12:     //andi
            Reg[rt] = Reg[rs] & udat;
            break;

        case 13:     //ori
            Reg[rt] = Reg[rs] | udat;
            break;

        case 14:     //xori
            Reg[rt] = Reg[rs] ^ udat;
            break;

        case 15:     //lui
            Reg[rt] = dat << 16;
            break;
		case 16:     //mfc0
		    if(rd==0)
				Reg[rt]=Reg[CAUSE];
			else if(rd==1)
				Reg[rt]=Reg[EPC];
			break;
        case 32:    //lb
            Reg[rt] =Memory[Reg[rs]+dat+0];
            break;

        case 33:    //lh
            Reg[rt] = (((Memory[Reg[rs]+dat+0]) << 8) |
                      Memory[Reg[rs]+dat+1]);
            break;

        case 35:    //lw
            /*
            printf("%X %X %X %X\n",
                   Memory[Reg[rs]+dat+0], Memory[Reg[rs]+dat+1], Memory[Reg[rs]+dat+2], Memory[Reg[rs]+dat+3]);
            */
            Reg[rt] = (Memory[Reg[rs]+dat+0] << 24);
            Reg[rt] |= (Memory[Reg[rs]+dat+1] << 16);
            Reg[rt] |= (Memory[Reg[rs]+dat+2] << 8);
            Reg[rt] |= (Memory[Reg[rs]+dat+3]);
            break;

        case 36:    //lbu
            Reg[rt] = Memory[Reg[rs]+dat+0];
            break;

        case 37:    //lhu
            Reg[rt] = ((Memory[Reg[rs]+dat+0]) << 8) |
                      Memory[Reg[rs]+dat+1];
            break;

        case 40:    //sb
            Memory[Reg[rs]+dat+0] = (byte)(Reg[rt] & 0xFF);
            mem_modified_flag = true;
            mem_modified_addr = Reg[rs] + dat;
            break;

        case 41:    //sh
            Memory[Reg[rs]+dat+0] = (byte)((Reg[rt] >> 8) & 0xFF);
            Memory[Reg[rs]+dat+1] = (byte)(Reg[rt] & 0xFF);
            mem_modified_flag = true;
            mem_modified_addr = Reg[rs] + dat;
            break;

        case 43:    //sw
            Memory[Reg[rs]+dat+0] = (byte)((Reg[rt] >> 24) & 0xFF);
            Memory[Reg[rs]+dat+1] = (byte)((Reg[rt] >> 16) & 0xFF);
            Memory[Reg[rs]+dat+2] = (byte)((Reg[rt] >> 8) & 0xFF);
            Memory[Reg[rs]+dat+3] = (byte)(Reg[rt] & 0xFF);
            mem_modified_flag = true;
            mem_modified_addr = Reg[rs] + dat;
            break;

        case 2:     //j
            PC = adr;
            break;

        case 3:     //jal
            //Reg[31] = PC + 4; //enable delay slot
            Reg[31] = PC; //disable delay slot
            PC = adr;
            break;
        
        default:
            System.out.println("Instrution Error!");
            return 1;
            //break;            
    }
    return 0;
}


/*protected void syscall(){
        if(Reg[2] == 10){
            //10鍙蜂腑鏂紝绋嬪簭缁撴潫
            System.out.println("syscall 2 ");
            parent.running = false;
            parent.cString = "";
        }
        else if(Reg[2] == 1){
            //1鍙蜂腑鏂紝鏄剧ずint锛岃鏄剧ず鐨勬暟鍦╝0
            System.out.println("syscall 1 ");
            String intString = (new Integer(Reg[4])).toString();
            printString(intString);
        }
        else if(Reg[2] == 4){
            //4鍙蜂腑鏂紝鏄剧ず瀛楃涓诧紝瑕佹樉绀虹殑瀛楃涓查鍦板潃鍦╝0
            System.out.println("syscall 4 ");
            String s = "";
            int i = Reg[4];
            while(this.Memory[i] != '@'){
                s += (char)this.Memory[i];
                i++;
            }
            printString(s);
        }
        else if(Reg[2] == 5){
            //5鍙蜂腑鏂紝杈撳叆涓�釜瀛楃
            //int now = parent.num;
            System.out.println("syscall 5 ");
            if(parent.cString.length()<1)  parent.cpuwait=true;
            else{
                char c = parent.cString.charAt(parent.cString.length()-1);
                Reg[2] = Integer.valueOf(c)-48;
                //PC-=4;
                parent.cString=parent.cString.substring(0,parent.cString.length()-1);
                parent.cpuwait=false;
            }
            
        }*/


}