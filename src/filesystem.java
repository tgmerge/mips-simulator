import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;


public class filesystem {
	
	static final int blocksize = 1024;
	static int line0;
	static int currentline;
	static int startblock0=0;
	static int endblock0=0;
	static int endpos0=0;
	static int count[]={0,0,0,0,0};
	static int num[]={0,0,0,0,0};
	
	static int line;//the line number in the disk0.txt
	static int valid;//if the file is deleted
	static int level;//using for help locate the file with directory
	static char[] filename;//filename 
	static int disknumber;//stored in which disk
	static int startblock;
	static int endblock;
	static int endpos;
	
	static File f0 = new File("disk0.disk");
	static File f1 = new File("disk1.disk");
	static File f2 = new File("disk2.disk");
	static File f3 = new File("disk3.disk");
	static File f4 = new File("disk4.disk");
	
	static long currentFile = 0;
	static long endFile = 0;
	public void get_info(int l) throws IOException//l line
	{
		
		
		 
		 String s0,s1;
		RandomAccessFile rd;
		rd=new RandomAccessFile("disk0.disk","rw");
			
		
			
		
		int line1=rd.readInt();
		
		
		for(int i=0;i<line1;i++)
		{
		rd.readInt();
			
			rd.skipBytes(2);
			valid=rd.readInt();
			rd.skipBytes(2);
			level=rd.readInt();
			rd.skipBytes(2);
			filename=rd.readUTF().toCharArray();
			rd.skipBytes(2);
			disknumber=rd.readInt();
			rd.skipBytes(2);
			startblock=rd.readInt();
			rd.skipBytes(2);
			endblock=rd.readInt();
			rd.skipBytes(2);
			endpos=rd.readInt();
			rd.skipBytes(2);
			
			
			System.out.println("line"+line+"  valid"+valid+"  level"+level+"  filename"+filename+"/n");
			System.out.println("disknumber"+disknumber+"  startblock"+startblock+"  endblock"+endblock+"  endpostion"+endpos);
		}
		
	    rd.close();
		
	}

	public boolean init_system() throws IOException

	{
		if(f0.exists()==true&&f1.exists()==true&&f2.exists()==true&&f3.exists()==true&&f4.exists()==true)
			return true;
		if(f0.exists()==false)
		{
			System.out.println("error:read disk failed£¬cannot load filesystem infomation");
		
			try
			{
				boolean b0 = f0.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		{
			RandomAccessFile rd;
			rd=new RandomAccessFile("disk0.disk","rw");
			int line1=rd.readInt();
			line0=line1;
			if(line1==0)
			{
				count[0]=0;
				count[1]=0;
				count[2]=0;
				count[3]=0;
				count[4]=0;
				System.out.println("no file exists now!");
				
			}
		
				
				for(int i=0;i<line1;i++)
				{
				
					line=rd.readInt();
					rd.skipBytes(2);
					valid=rd.readInt();
					rd.skipBytes(2);
					level=rd.readInt();
					rd.skipBytes(2);
					filename=rd.readUTF().toCharArray();
					rd.skipBytes(2);
					disknumber=rd.readInt();
					rd.skipBytes(2);
					startblock=rd.readInt();
					rd.skipBytes(2);
					endblock=rd.readInt();
					rd.skipBytes(2);
					endpos=rd.readInt();
					rd.skipBytes(2);
					
					
					System.out.println("line"+line+"  valid"+valid+"  level"+level+"  filename"+filename+"/n");
					System.out.println("disknumber"+disknumber+"  startblock"+startblock+"  endblock"+endblock+"  endpostion"+endpos);
				
			;
			if(valid==0)
				continue;
			
			count[0]++;
			switch(disknumber)
			{
			case 1:
				count[1]=endblock+1;
				num[1]++;
				break;
			case 2:
				count[2]=endblock+1;
				num[2]++;
				break;
			case 3:
				count[3]=endblock+1;
				num[3]++;
				break;
			case 4:
				count[4]=endblock+1;
				num[4]++;
				break;
				default:
			}
				
			
		}
		
		
		
		
	    rd.close();
		}
		
		
		if(f1.exists()==false)
		{
			System.out.println("warning: disk1 is not detected");
		
			try
			{
				boolean b1 = f1.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(f2.exists()==false)
		{
			System.out.println("warning: disk2 is not detected");
		
			try
			{
				boolean b2 = f2.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();			
			}
		}
		
		if(f3.exists()==false)
		{
			System.out.println("warning: disk0 is not detected");
		
			try
			{
				boolean b3 = f3.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(f4.exists()==false)
		{
			System.out.println("warning: disk0 is not detected");
		
			try
			{
				boolean b4 = f4.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();			
			}
		}
		
						
				return true;
		}

	public long rename(String oldname,String newname) throws IOException
	{
		RandomAccessFile rd = null;
	     rd = new   RandomAccessFile("disk0.disk","rw");
		String filename1;	
		int line1=rd.readInt();
		int v;
		for(int i=0;i<line1;i++)
		{
			currentFile=rd.getFilePointer();
			rd.skipBytes(6);
			v=rd.readInt();
			rd.skipBytes(8);
			filename1=rd.readUTF();
			
			
			if(v==0)
				continue;
			if(filename1.equals(oldname))
			{
				rd.seek(currentFile);
				rd.skipBytes(18);
				rd.writeUTF(newname);
				return currentFile;
				
			}
			
			
			
		}

	    rd.close();
	    return -1;
}
	
	public long delete(String filename) throws IOException
	{
		RandomAccessFile rd = null;
		
		rd = new   RandomAccessFile("disk0.disk","rw");
		int count = 0,v;
		String filename1;
		long currentpos=0;
			
			
        int line1=rd.readInt();
		for(int i=0;i<line1;i++)
		{
		
			currentFile=rd.getFilePointer();
			rd.skipBytes(6);
			v=rd.readInt();
			rd.skipBytes(8);
			
			filename1=rd.readUTF();
			rd.skipBytes(26);
			
			if(filename1.equals(filename)&&v==1)
			{
				
				rd.skipBytes(6);
				rd.seek(currentFile);
				rd.writeInt(0);
				
				
				return currentFile;
				
			}
			
		}
		
		
		rd.close();
		
		
			
			return -1;
			
		
	}
	public boolean reloacte(int disknumber)
	{
		return true;
	}
	public static long readdisk(String filename,int mem_address) throws IOException
	{
		BufferedReader reader = null;
		 String s0,s1 = null;
		
		 RandomAccessFile rd = null;
			rd = new   RandomAccessFile("disk0.disk","rw");
			s0=rd.readLine();
			
			int disknumber ;
			int startblock ;
			int endblock ;
			int endpos ;
			String filename1;
			
		int line1=rd.readInt();	
		for(int i=0;i<line1;i++)
		{
					
				disk d = new disk();
				currentFile=rd.getFilePointer();
				rd.skipBytes(18);
				filename1=rd.readUTF();
				if(filename.equals(filename1))
				{
					rd.skipBytes(2);
					disknumber=rd.readInt();
					rd.skipBytes(2);
					startblock=rd.readInt();
					rd.skipBytes(2);
					endblock=rd.readInt();
					rd.skipBytes(2);
					endpos=rd.readInt();
					

					d.readdisk(disknumber, startblock, endblock, endpos, mem_address);
					rd.close();
					return currentFile;
				}
				
				
			
			
			file_struct.readfs(s1);
			s0 = reader.readLine();
			
		}

	    reader.close();
	    return -1;
		
		
	}
	public static boolean writedisk(String filename,int mem_address,int length) throws IOException
	{
		 String filename1 = null;
		int v;
		RandomAccessFile rd = null;
	    rd = new   RandomAccessFile("disk0.disk","rw");
	    int line1=rd.readInt();	
	    for(int i=0;i<line1;i++)
		{
		
			currentFile=rd.getFilePointer();
			rd.skipBytes(6);
		    v=rd.readInt();
			rd.skipBytes(8);
			filename1=rd.readUTF();
			
			if(filename.equals(filename1)&&v==1)
			{
				rd.skipBytes(2);
				int disknumber1=rd.readInt();
				rd.skipBytes(2);
				int startblock1=rd.readInt();
				rd.skipBytes(2);
				int endblock1=rd.readInt();
				rd.skipBytes(2);
				int endpos1=rd.readInt();
				rd.skipBytes(2);
				endblock1=(int) (startblock+length/blocksize);
				endpos1=length%blocksize;
				rd.seek(currentFile);
				rd.skipBytes(18);
				int temp=rd.readShort();
				rd.skipBytes(temp+14);
				rd.writeInt(endblock1);
				rd.skipBytes(2);
				rd.writeInt(endpos1);
				disk d = new disk();
				d.writedisk(disknumber1, startblock1, endblock1, endpos1, mem_address);
				rd.close();
				return true;
				
				
			}
		}
			
		rd.close();
		return false;
			
		}
	    public static boolean writedisk(String filename,int mem_address) throws IOException
		{
			 String filename1 = null;
			int v;
			RandomAccessFile rd = null;
		    rd = new   RandomAccessFile("disk0.disk","rw");
		    int line1=rd.readInt();	
		    for(int i=0;i<line1;i++)
			{
			
				currentFile=rd.getFilePointer();
				rd.skipBytes(6);
			    v=rd.readInt();
				rd.skipBytes(8);
				filename1=rd.readUTF();
				
				if(filename.equals(filename1)&&v==1)
				{
					rd.skipBytes(2);
					int disknumber1=rd.readInt();
					rd.skipBytes(2);
					int startblock1=rd.readInt();
					rd.skipBytes(2);
					int endblock1=rd.readInt();
					rd.skipBytes(2);
					int endpos1=rd.readInt();
					rd.skipBytes(2);
					disk d = new disk();
					
					d.writedisk(disknumber1, startblock1, endblock1, endpos1, mem_address);
					return true;
					
					
				}
				
			
				
			}


	    rd.close();
	    return false;
	}
	
    public long open(String filename) throws IOException
    {
    
		 String s0 = null,s1 = null,filename1;
		
		 RandomAccessFile rd = null;
	     rd = new   RandomAccessFile("disk0.disk","rw");
		int v;	
		while(s0 != null)
		{
			file_struct fs = new file_struct();
			currentFile=rd.getFilePointer();
			disk d = new disk();
			rd.skipBytes(6);
			v=rd.readInt();
			rd.skipBytes(8);
			filename1=rd.readUTF();
			if(filename.equals(filename1)&&v==1)
			{
				rd.skipBytes(2);
				disknumber=rd.readInt();
				rd.skipBytes(2);
				startblock=rd.readInt();
				rd.skipBytes(2);
				endblock=rd.readInt();
				rd.skipBytes(2);
				endpos=rd.readInt();
				rd.skipBytes(2);
			    break;	
				
				
			}		
		}
		rd.close();
		return currentFile;
    }
    	
    

	public boolean close(String filename)
	{
		currentFile=0;
		return true;
		
	}

	
	
	
	public long creat(String filename,int length) throws IOException
	{
		int disknumber=(int) Math.round(Math.random()*4);
		int line =count[0];
		int level=0;
		int valid=1;
		int endblock=-1;
		String s0;
		int v;
		
		if(count[disknumber]>3*count[1]||count[disknumber]>3*count[2]||count[disknumber]>3*count[3]||count[disknumber]>3*count[4])
		{
			disknumber=1;
			for(int i=1;i<5;i++)
			{
				if (count[i]<count[disknumber])
					disknumber=i;
			}
		}
		int startblock=count[disknumber];
		int endpos=-1;
		int i;
		RandomAccessFile rd = null;
		char[] filename1 = filename.toCharArray();
		rd = new   RandomAccessFile("disk0.disk","rw");
		int line1= rd.readInt();
		for( i =0;i<line1;i++)
		{
			rd.skipBytes(6);
			v=rd.readInt();
			if(v==1)
				continue;
			else 
				if(v==0)
				{
					currentFile=rd.getFilePointer();
					break;
				}
		
			
		}
		if(i>=line1)
		{
			line0++;
			line1++;
			rd.seek(0);
			rd.writeInt(line1);
		}
		
		startblock=count[disknumber];
		endblock=(int) (startblock+length/blocksize);
		endpos=length%blocksize;
		rd.seek(currentFile);
		rd.writeInt(line);
		rd.writeChar('$');
		rd.writeInt(valid);rd.writeChar('$');
		rd.writeInt( level);rd.writeChar('$');
		rd.writeUTF(filename1.toString());
		rd.writeChar('$');
		rd.writeInt( disknumber);rd.writeChar('$');
		rd.writeInt( startblock);rd.writeChar('$');
		rd.writeInt( endblock);rd.writeChar('$');
		rd.writeInt( endpos);rd.writeChar('$');
		rd.close();
		
		
	
		
		
		
		
		
		
		return currentFile;
		
	}
	
	
	
	public long creat(String filename) throws IOException
	{
		int disknumber=(int) Math.round(Math.random()*4);
		int line =count[0];
		int level=0;
		int valid=1;
		int endblock=-1;
		String s0;
		int v;
		
		if(count[disknumber]>3*count[1]||count[disknumber]>3*count[2]||count[disknumber]>3*count[3]||count[disknumber]>3*count[4])
		{
			disknumber=1;
			for(int i=1;i<5;i++)
			{
				if (count[i]<count[disknumber])
					disknumber=i;
			}
		}
		int startblock=count[disknumber];
		int endpos=-1;
		int i;
		RandomAccessFile rd = null;
		char[] filename1 = filename.toCharArray();
		rd = new   RandomAccessFile("disk0.disk","rw");
		int line1= rd.readInt();
		for( i =0;i<line1;i++)
		{
			rd.skipBytes(6);
			v=rd.readInt();
			if(v==1)
				continue;
			else 
				if(v==0)
				{
					currentFile=rd.getFilePointer();
					break;
				}
		
			
		}
		if(i>=line1)
		{
			line0++;
			line1++;
			rd.seek(0);
			rd.writeInt(line1);
		}
		
		startblock=count[disknumber];
		rd.seek(currentFile);
		rd.writeInt(line);
		rd.writeChar('$');
		rd.writeInt(valid);rd.writeChar('$');
		rd.writeInt( level);rd.writeChar('$');
		rd.writeUTF(filename1.toString());
		rd.writeChar('$');
		rd.writeInt( disknumber);rd.writeChar('$');
		rd.writeInt( startblock);rd.writeChar('$');
		rd.writeInt( endblock);rd.writeChar('$');
		rd.writeInt( endpos);rd.writeChar('$');
		rd.close();
		
		
	
		
		
		
		
		
		
		return currentFile;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

		
		
}
	

	
	
	
	
	
	

	



