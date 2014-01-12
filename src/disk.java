import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;


public class disk {
	public boolean readdisk(int disknumber,int startblock,int endblock,int endpos,int mem_address)
	{
	
		String dfilename = "disk"+((char)disknumber)+".disk";
		RandomAccessFile rd = null;
		try {
			rd = new   RandomAccessFile(dfilename,"rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		int blocksize = filesystem.blocksize;
		try {
			rd.seek((startblock*blocksize));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//for(count=startdisk;count<enddisk;count++)
		//{
			//for(int i = 0;i<blocksize;i++)
			//{
			//	mem[address+i]=rd.readByte();
				
			//}
			byte[] b = null;
		
				rd.read(b, 0, ((endblock-startblock)*blocksize)+endpos+1);
			
			
			memory.writeString(mem_address,b.toString());
			
			
		//}
		
		try {
			rd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}
	

	public boolean writedisk(int disknumber, int startblock,int endblock,int endpos,int mem_address) throws IOException
	{
		
		String dfilename = "disk"+((char)disknumber)+".disk";
		RandomAccessFile rd = null;
		byte[] b =null;
		byte[] a = null;
		rd = new   RandomAccessFile(dfilename,"rw");
		
		int blocksize = filesystem.blocksize;
		rd.seek((long)(startblock*blocksize));
	
		for(int i = startblock;i<endblock;i++)
		{
			for(int j = 0;j<blocksize;j++)
			{
				a=mem[mem_address].readByte();
			}
			rd.write(a, 0, blocksize);
			
		}
		for(int j =0;j<endpos;j++)
		{
			//mem_address=mem_address+(endblock-startblock)*blocksize]
			a=mem[mem_address].readByte;
			rd.write(a, 0, endpos+1);
			
		}
		
				
		
	
		
	rd.close();
		

		return true;
	}
	
	


	public boolean createfile(byte disknumber, int startblock,int endblock,int endpos) throws IOException
	{
		
		String dfilename = "disk"+((char)disknumber)+".disk";
		RandomAccessFile rd = null;
		try {
			rd = new   RandomAccessFile(dfilename,"rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int blocksize = filesystem.blocksize;
		try {
			rd.seek((long)(startblock*blocksize));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
	
		byte[] b =null;
		//b=memory.readString(mem_address);
	    file_struct fs = new file_struct();
		rd.write(b);
		int length = (int) rd.length();
		int wline = file_struct.getline();
		int wvalid = file_struct.getvalid();
		int wlevel = file_struct.getlevel();
		byte[] wfilename = file_struct.getfilename();
		int wdisknumber = file_struct.getdisknumber();
		int wstartblock = file_struct.getstartblock();
		int wendblock = (file_struct.getstartblock()+length/blocksize);
		int wendpos = (int) (length%blocksize-1);
		//setfile_struct.valid(1);
		wvalid = 1;
		
		
		try {
			rd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream fos=new FileOutputStream("disk0.txt",true);
		fos.write( wline);fos.write('$');
		fos.write(wvalid);fos.write('$');
		fos.write( wlevel);fos.write('$');
		fos.write(wfilename,0,wfilename.length);fos.write('$');
		fos.write( wdisknumber);fos.write('$');
		fos.write( wstartblock);fos.write('$');
		fos.write( wendblock);fos.write('$');
		fos.write( wendpos);fos.write('$');
		fos.close();
		String s = null;
		
		
		
		
		
		
		
		
		
		return true;
	}

   
}
