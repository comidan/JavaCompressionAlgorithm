package executablecompressiontest;

import java.util.ArrayList;
/**
 *
 * @author daniele
 */
public class Decompresser
{
    private ArrayList<CustomByte>[] customBytes,decompressedBytes;
    
    public Decompresser(ArrayList<CustomByte>[] customBytes)
    {
        this.customBytes=customBytes;
    }
    
    ArrayList<CustomByte>[] startByteDecompression(int originalLenght)
    {
        Thread[] threadArray=new Thread[customBytes.length];
        decompressedBytes=new ArrayList[customBytes.length];
        for(int i=0;i<threadArray.length;i++)
        {
            threadArray[i]=new Thread(new DecompressHexPart(i));
            threadArray[i].start();
        }
        for(int i=0;i<threadArray.length;i++)
            try
            {
                threadArray[i].join();
                System.out.println("Decompression Thread "+i+" has terminated correctly its execution.");
            }
            catch(InterruptedException exc)
            {
                
            }
        return decompressedBytes;
    }
    
    
    private void decompressHexByte(int index)
    {
        decompressedBytes[index]=new ArrayList<>();
        for(int i=0,j=0;i<customBytes[index].size();i++)
        {
            if(customBytes[index].get(i).getByteCount()>1)
            {
                byte temp=customBytes[index].get(i).getByte();
                int counter=0,tempLength=customBytes[index].get(i).getByteCount();
                while(counter<tempLength)
                {
                    decompressedBytes[index].add(new CustomByte(1,temp));
                    counter++;
                    j++;
                }
            }
            else if(i<customBytes[index].size())
            {
                decompressedBytes[index].add(new CustomByte(1,customBytes[index].get(i).getByte()));
                j++;
            }
        }
    }
    
    private class DecompressHexPart implements Runnable
    {
        private final int index;
        
        public DecompressHexPart(int index)
        {
            this.index=index;
        }
        
        @Override
        public void run()
        {
            decompressHexByte(index);
        }
    }
}
