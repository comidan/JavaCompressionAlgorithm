package executablecompressiontest;
/**
 *
 * @author daniele
 */
public class CustomByte
{
    private int byte_count;
    private byte _byte;

    public CustomByte(int byte_count,byte _byte)
    {
        this.byte_count=byte_count;
        this._byte=_byte;
    }
        
    int getByteCount()
    {
        return byte_count;
    }
        
    byte getByte()
    {
        return _byte;
    }
}
