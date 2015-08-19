import java.util.BitSet;

/**
 * Created by Steve on 2/8/2015.
 */
public class ByteTest {

	public static void main (String[] args){
		byte b = 8;
		BitSet bits = BitSet.valueOf( new byte[] {b});

		System.out.println(b + " = " + bits.toString());
	}
}
