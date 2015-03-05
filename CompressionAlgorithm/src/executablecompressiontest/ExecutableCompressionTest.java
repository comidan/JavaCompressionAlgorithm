package executablecompressiontest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author daniele
 */
public class ExecutableCompressionTest
{     
    static String[] hexParts;
    static final String FILE_NAME="ffun_c.pdf";
    
    public static String binaryFileToHexString(final String path,int maxBytes,int gap) throws FileNotFoundException,IOException
    {
        final int bufferSize=1024*1024 ;
        final byte[] buffer=new byte[bufferSize];
        final StringBuilder sb=new StringBuilder();
        FileInputStream stream=new FileInputStream(path);
        stream.skip(gap);
        int bytesRead,bytesCounter=0;
        while((bytesRead=stream.read(buffer))>0&&bytesCounter<maxBytes)
            for(int i=0;i<bytesRead;i++)
            {
                sb.append(String.format("%02x",buffer[i]));
                bytesCounter++;
                if(bytesCounter>=maxBytes)
                    break;
            }
        stream.close();
        return sb.toString();
    }
    
    public static byte[] binaryFileToHexByte(final String path) throws FileNotFoundException,IOException
    {
        RandomAccessFile randomAccessFile=new RandomAccessFile(path,"r");
        FileChannel inChannel=randomAccessFile.getChannel();
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        byte[] b=new byte[new FileManagement<byte[]>("J:\\"+FILE_NAME).getFileSizeAbsolutePath()];
        int index=0;
        while(inChannel.read(buffer) > 0)
        {
            buffer.flip();
            for(int i=0;i<buffer.limit();i++,index++)
                b[index]=buffer.get();
            buffer.clear();
        }
        inChannel.close();
        randomAccessFile.close();
        return b;
    }
    
    public static void hexByteToBinaryFile(final byte[] hex,String FILE_NAME) throws IOException
    {
        FileOutputStream stream=new FileOutputStream(FILE_NAME);
        stream.write(hex);
        stream.close();
    }
    
    public static void main(String[] args)
    {   
        String hex="";
        byte[] b=new byte[1];
        int lenght=0;
        try
        {
            b=binaryFileToHexByte("J:\\"+FILE_NAME);
            lenght=b.length;
        }
        catch(FileNotFoundException exc)
        {
            System.out.println("FILE NOT FOUND");
        }
        catch(IOException exc)
        {
            
        }
        /*FileManagement<byte[]> fileManagement=new FileManagement<>(FILE_NAME);
        int divisor=getMostSuitbaleThreadNumber(fileManagement.getFileSize());
        Thread[] readHexArray=new Thread[fileManagement.getFileSize()/divisor];   //multithread read-improve read too slow.
        hexParts=new String[readHexArray.length];
        for(int i=0;i<readHexArray.length;i++)
        {
            readHexArray[i]=new Thread(new ReadHexThread(divisor,divisor*i,i));
            readHexArray[i].start();
        }
        for(int i=0;i<readHexArray.length;i++)
           try
           {
                readHexArray[i].join();
           }
           catch(InterruptedException exc)
           {
               
           }
        for(int i=0;i<hexParts.length;i++)
            hex+=hexParts[i];*/
        Compresser compresser=new Compresser(b);                                 //multithread compression-improve algorithm 
        ArrayList<CustomByte>[] customBytes=compresser.startByteHexCompression();//worse than winzip
        ArrayList<CustomByte> customByte=new ArrayList<>();
        for(int i=0;i<customBytes.length;i++)
            for(int j=0;j<customBytes[i].size();j++)
                customByte.add(customBytes[i].get(j));
        System.out.println("Compressed\nStarting decompression...");
        Decompresser decompresser=new Decompresser(customBytes);
        ArrayList<CustomByte>[] decompressedByte=decompresser.startByteDecompression(lenght);
        byte[] decompressed=new byte[lenght];
        for(int i=0,j=0;i<decompressedByte.length;i++)
            for(int k=0;k<decompressedByte[i].size();k++,j++)
                if(b[j]!=decompressedByte[i].get(k).getByte())
                    System.out.println("Incorrect Data");
                else
                    decompressed[j]=decompressedByte[i].get(k).getByte();
        System.out.println("Compressed Lenght : "+customByte.size()+"\nOriginal Lenght : "+b.length+"\n"
                          +"Equal Check (Original To Decompressed)..."+Arrays.equals(b,decompressed)+"\nCompression Ratio : "
                          +(100-(customByte.size()/((double)b.length)*100))+" %");
        try
        {
            hexByteToBinaryFile(decompressed,FILE_NAME);
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
    }
    
    private static class ReadHexThread implements Runnable
    {
        private int maxBytes,gap,threadIndex;
        
        public ReadHexThread(int maxBytes,int gap,int threadIndex)
        {
            this.maxBytes=maxBytes;
            this.gap=gap;
            this.threadIndex=threadIndex;
        }
        
        @Override
        public void run()
        {
            try
            {
                hexParts[threadIndex]=binaryFileToHexString(FILE_NAME,maxBytes,gap);
            }
            catch(FileNotFoundException exc)
            {
                
            }
            catch(IOException exc)
            {
                
            }
        }
        
    }
}
