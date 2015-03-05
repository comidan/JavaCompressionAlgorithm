package executablecompressiontest;

import java.util.ArrayList;
/**
 *
 * @author daniele
 */
public class Compresser
{
    private byte[] _hex;
    private ArrayList<CustomByte>[] customBytes;
    
    public Compresser(byte[] hex)
    {
        _hex=hex;
    }
    
    ArrayList<CustomByte>[] startByteHexCompression()
    {
        int hex_dimension=_hex.length;
        int divisor=getMostSuitbaleThreadNumber(hex_dimension);
        Thread[] threadArray=new Thread[hex_dimension/divisor];
        customBytes=new ArrayList[hex_dimension/divisor];
        for(int i=0;i<threadArray.length;i++)
        {
            threadArray[i]=new Thread(new CompressHexPart(i,i*divisor,(i+1)*divisor));
            threadArray[i].start();
        }
        for(int i=0;i<threadArray.length;i++)
            try
            {
                threadArray[i].join();
            }
            catch(InterruptedException exc)
            {
                
            }
        return customBytes;
    }
    
    void compressHexByte(int start,int end,int index)
    {
        customBytes[index]=new ArrayList<>(end-start);
        int byte_count=0;
        for(int i=start;i<end;i++)
        {
            byte temp=_hex[i];
            while(i<end&&_hex[i]==temp)
            {
                byte_count++;
                i++; //Ciao Daniele 
            }
            i--;
            customBytes[index].add(new CustomByte(byte_count,temp));
            byte_count=0;
        }
    }
    
    private int getMostSuitbaleThreadNumber(int lenght)
    {
        ArrayList<Integer> divisors=new ArrayList();
        for(int i=2;i<lenght;i++)
        {
            long temp=lenght%i;
            if(temp==0)
               divisors.add(i);
        }
        return divisors.get((divisors.size()/2));
    }
    
    private class CompressHexPart implements Runnable
    {
        private final int index,start,end;
        
        public CompressHexPart(int index,int start,int end)
        {
            this.index=index;
            this.start=start;
            this.end=end;
        }
        
        @Override
        public void run()
        {
            compressHexByte(start,end,index);
        }
    }
}