import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

//1block = 1024byte
//endlength = the length-1 of the file on the end block
public class file_struct extends filestruct {
	
	public boolean setline(int t)
	{
		line=t;
		return true;
	}
	
	public boolean setvalid(int t)
	{
		valid=t;
		return true;
	}
	
	public boolean setlevel(int t)
	{
		level=t;
		return true;
	}
	
	public boolean setfilename(byte[] b)
	{
		//filename = b;
		return true;
	}
	
	public static boolean setdisknumber(int t)
	{
		disknumber=t;
		return true;
	}
	
	public static boolean setstartblock(int t)
	{
		startblock=t;
		return true;
	}
	
	public static boolean setendblock(int t)
	{
		endblock=t;
		return true;
	}
	
	public static boolean setendpos(int t)
	{
		endpos=t;
		return true;
	}

	public static int getline()
	{
		return line;
	}
	
	public static int getvalid()
	{
		return valid;
	}
	
	public static int getlevel()
	{
		return level;
	}
	
	public static byte[] getfilename()
	{
		return filename.toString().getBytes();
	}
	
	public static int getdisknumber()
	{
		return disknumber;
	}
	
	public static int getstartblock()
	{
		return startblock;
	}
	
	public static int getendblock()
	{
		return endblock;
	}
	public static int getendpos()
	{
		return endpos;
	}
   
	public static boolean savetoend(int type) throws IOException
	{
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("disk0.disk",true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fos.write( line);fos.write('$');
		fos.write(valid);fos.write('$');
		fos.write( level);fos.write('$');
		//fos.write(filename.toString(),0,filename.length);fos.write('$');
		fos.write( disknumber);fos.write('$');
		fos.write( startblock);fos.write('$');
		fos.write( endblock);fos.write('$');
		fos.write( endpos);fos.write('$');
		fos.close();
		return true;
	}
	

	
	static void main(String args[]) throws IOException
	{
		String filename = "disktest";
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("disktest.disk",true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int line =1,level=3,startblock=7,endblock=8,endlength=9;
		int valid = 4,disknumber=6;
		writefs1();
		
		
	
	}
	public static void readfs(String string) throws IOException
	{
		
		int first=0,second;
		BufferedReader reader = null;
		 String s0,s1 = null;
		
			reader = new BufferedReader(new InputStreamReader( 
			         new FileInputStream("disk0.disk")));
			s0=reader.readLine();
	   
		second=string.indexOf("$");
		line=Integer.parseInt(string.substring(first, second));
		first = second+1;
		second=string.indexOf("$");
		valid=Integer.parseInt(string.substring(first, second));
		first = second+1;
		second=string.indexOf("$");
		level=(byte)Integer.parseInt(string.substring(first, second));
		first = second+1;
		second=string.indexOf("$");
		//filename = string.substring(first, second).getBytes();
		first = second+1;
		second=string.indexOf("$");
		disknumber=Integer.parseInt(string.substring(first, second));
		first = second+1;
		second=string.indexOf("$");
		startblock=Integer.parseInt(string.substring(first, second));
		first = second+1;
		second=string.indexOf("$");
		endblock=Integer.parseInt(string.substring(first, second));
		first = second+1;
		second=string.indexOf("$");
		endpos=Integer.parseInt(string.substring(first, second));
		
		
	}
	
	public void writefs() throws IOException
	{
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("disk0.txt",true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fos.write( line);fos.write('$');
		fos.write(valid);fos.write('$');
		fos.write( level);fos.write('$');
		//fos.write(filename,0,filename.length);fos.write('$');
		fos.write( disknumber);fos.write('$');
		fos.write( startblock);fos.write('$');
		fos.write( endblock);fos.write('$');
		fos.write( endpos);fos.write('$');
		fos.write('\n');
	}
	
	
	public static void writefs1() throws IOException
	{

		RandomAccessFile rd = null;
		int line =6,level=3,startblock=7,endblock=8,endlength=9;
		int valid = 4,disknumber=6;
		char[] filename = "filename1".toCharArray();
		
		rd = new   RandomAccessFile("disk0.disk","rw");
		rd.seek(rd.length());
		long currentoffset=rd.getFilePointer();
		rd.seek(currentoffset);
		rd.writeInt( line);
		rd.writeChar('$');
		rd.writeInt(valid);rd.writeChar('$');
		rd.writeInt( level);rd.writeChar('$');
		rd.writeUTF(filename.toString());
		rd.writeChar('$');
		rd.writeInt( disknumber);rd.writeChar('$');
		rd.writeInt( startblock);rd.writeChar('$');
		rd.writeInt( endblock);rd.writeChar('$');
		rd.writeInt( endpos);rd.writeChar('$');
		rd.close();
		
	
		
	}
    
    
}

