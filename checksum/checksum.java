import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class checksum 
{
	public static void main(String[] args) 
	{
		File inFile = new File(args[0]);
		int base = Integer.parseInt(args[1]);
		int counter = 0;
		String inStream = "";
		String[] outStream;
		int ch;
		
		if(base != 8 && base != 16 && base != 32)
		{
			System.err.println("Valid checksum sizes are 8, 16, or 32");
		}
		
		try
		{
			BufferedReader read = new BufferedReader(new FileReader(inFile));
			while((ch = read.read()) != -1)
			{
				// Strips the carriage return from windows line endings
				if(ch == '\r')
				{
					continue;
				}
				
				inStream = inStream + (char)ch;
				counter++;
			}
			read.close();
			
			// Padding
			if(base == 16 && inStream.length() % 2 == 1)
			{
				inStream = inStream + "X";
				counter = counter + 1;
			}
			if(base == 32 && inStream.length() % 4 !=0)
			{
				if(inStream.length() % 4 == 3)
				{
					inStream = inStream + "X";
					counter = counter + 1;
				}
				if(inStream.length() % 4 == 2)
				{
					inStream = inStream + "XX";
					counter = counter + 2;
				}
				if(inStream.length() % 4 == 1)
				{
					inStream = inStream + "XXX";
					counter = counter + 3;
				}
			}
			System.out.println();
			
			if(inStream.length() <= 80)
			{
				System.out.println(inStream);
			}
			else
			{
				for(int count = 0; count < inStream.length(); count ++)
				{
					if (count % 80 == 0 && count != 0)
					{
						System.out.println();
					}
					System.out.print(inStream.charAt(count));
				}
				System.out.println();
			}
			System.out.printf("%2d bit checksum is ", base);
			outStream = stringConverter(inStream);
			
			if(base == 8)
			{
				//System.out.printf("%8s", checksum8("", inStream.length()));
				System.out.printf("%8s", checksum8(outStream, inStream.length()));
			}
			else if(base == 16)
			{
				System.out.printf("%8s", checksum16(outStream, inStream.length()));
			}
			else if(base == 32)
			{
				System.out.printf("%8s", checksum32(outStream, inStream.length()));
			}
			System.out.printf(" for all %4d chars\n", counter);
		}
		catch(IOException e)
		{
			System.out.println("FILE NOT FOUND!");
		}
	}
	
	public static String binaryAdd(String in1, String in2)
	{
		char[] ch1 = new char[in1.length()];
		char[] ch2 = new char[in2.length()];
		
		char[] temp;
		String output = "";
		
		for(int x = 0; x < in1.length(); x++)
		{
			ch1[x] = in1.charAt(x);
		}
		for(int y = 0; y < in1.length(); y++)
		{
			ch2[y] = in2.charAt(y);
		}
		
		temp = binaryAdd(ch1, ch2);
		for(int x = 0; x < temp.length; x++)
		{
			output = output + temp[x];
		}
		return output;		
	}
	
	
	// Doesn't matter how long, Must be the same length! Will not add a carry over digit at the end!
	public static char[] binaryAdd(char[] in1, char[] in2)
	{
		boolean carry = false;
		char temp1;
		char temp2;
		char[] output = new char[in1.length];
		
		for(int i = in1.length - 1; i >= 0; i--)
		{
			temp1 = in1[i];
			temp2 = in2[i];
			
			if(temp1 == '1' && temp2 == '1')
			{
				if(carry == true)
				{
					output[i] = '1';
					carry = true;
				}
				else
				{
					output[i] = '0';
					carry = true;
				}
			}
			
			else if(temp1 == '1' && temp2 == '0')
			{
				if(carry == true)
				{
					output[i] = '0';
					carry = true;
				}
				else
				{
					output[i] = '1';
					carry = false;
				}
			}	
			else if(temp1 == '0' && temp2 == '1')
			{
				if(carry == true)
				{
					output[i] = '0';
					carry = true;
				}
				else
				{
					output[i] = '1';
					carry = false;
				}
			}	
			else if(temp1 == '0' && temp2 == '0')
			{
				if(carry == true)
				{
					output[i] = '1';
					carry = false;
				}
				else
				{
					output[i] = '0';
					carry = false;
				}
			}
		}
		return output;
	}

	// Takes the input stream and goes char by char converting to binary (ensuring its 8-bit) and storing each
	// in a string array.
	public static String[] stringConverter(String inStream)
	{
		String[] outStream = new String[inStream.length()];
		String temp;
		
		for(int i = 0; i < inStream.length(); i++)
		{
			temp = Integer.toBinaryString(inStream.charAt(i));
			
			while(temp.length() < 8)
			{
				temp = "0" + temp;
			}
			outStream[i] = temp;
		}
		return outStream;
	}

	public static String checksum8(String outStream[], int length)
	{
		String biOut = "00000000";
		String hexOut = "";
		
		for(int i = 0; i < length; i++)
		{
			biOut = binaryAdd(biOut, outStream[i]);
		}

		hexOut = toHex(biOut);
		return hexOut;
	}
	
	public static String checksum16(String outStream[], int length)
	{
		String biOut = "0000000000000000";
		String hexOut = "";
		String[] splitOut = new String[(length / 2)];
		for(int x = 0, y = 0; x < outStream.length; x = x + 2, y++)
		{
			splitOut[y] = outStream[x] + outStream[x + 1];
		}
		
		for(int i = 0; i < splitOut.length; i++)
		{
			biOut = binaryAdd(biOut, splitOut[i]);
		}

		hexOut = toHex(biOut);
		return hexOut;
	}
	
	public static String checksum32(String outStream[], int length)
	{
		String biOut = "00000000000000000000000000000000";
		String hexOut = "";
		String[] splitOut = new String[(length / 4)];
		String biTemp1;
		String biTemp2;
		String hexOut1;
		String hexOut2;
		
		for(int x = 0, y = 0; x < outStream.length; x = x + 4, y++)
		{
			splitOut[y] = outStream[x] + outStream[x + 1] + outStream[x + 2] + outStream[x + 3];
		}
		
		for(int i = 0; i < splitOut.length; i++)
		{
			biOut = binaryAdd(biOut, splitOut[i]);
		}
		biTemp1 = biOut.substring(0, 16);
		biTemp2 = biOut.substring(16, 32);
		hexOut1 = toHex(biTemp1);
		hexOut2 = toHex(biTemp2);
		
		while(hexOut2.length() < 4)
		{
			hexOut2 = "0" + hexOut2;
		}
		
		hexOut = hexOut1 + hexOut2;
		return hexOut;
	}
	
	// Takes a binary String and converts it to hex
	public static String toHex(String input)
	{
		int in;
		String buffer;
		int temp;
		String output = "";
		
		buffer = input.trim();
		
		if(buffer.length() == 0)
		{
			in = 0;
		}
		else
		{
			in = new BigInteger(buffer, 2).intValue();
		}
		
		if(in < 0)
		{
			in = in * -1;
		}
		
		char hex[] = {'0', '1', '2', '3', '4', '5', '6', '7',
					  '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		
		while(in > 0)
		{
			temp = in % 16;
			output = hex[temp] + output;
			in = in / 16;
		}
		
		boolean flag = true;
		
		if(output.length() == 0)
		{
			output = "0";
			flag = false;
		}
		
		while(flag && output.charAt(0) == '0')
		{
			try
			{
				output = output.substring(1);
			}
			catch(java.lang.IndexOutOfBoundsException e)
			{
				output = "0";
				flag = false;
			}
		}
		return output;
	}
}
