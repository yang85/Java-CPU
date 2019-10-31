import java.io.*;
import java.util.*;

public class Memory {

	static int[] mem = new int[2000];
	// scanner for filename
	static Scanner CPUmes=null;
	// scanner for instruction
	static Scanner instscan = null;
	static File instfile = null;

	// main
	public static void main(String args[]) {
		// open file using recieved filename
		instfile = new File(args[0]);
		// start fetching when the file is opened
		try {
			instscan = new Scanner(instfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String temp;
		int i = 0;
		// saving memory to array
		while (instscan.hasNext() == true) {
			// temp = instscan.nextLine();
			// fetch instructions
			if (instscan.hasNextInt() == true) {
				mem[i] = instscan.nextInt();
				i++;
			}
			// fetch "." and ignore else
			else {
				temp = instscan.next();
				if (temp.contains(".") == true) {
					// indicates the end of user memory
					// move the pointer to system header
					i = Integer.parseInt(temp.substring(1));
				} else {
					instscan.nextLine();
				}
			}
		}

		CPUmes = new Scanner(System.in);
		// listener for CPU
		try{
			CPUmes = new Scanner(System.in);
			while (true) {
			String inst = CPUmes.nextLine();
					// CPU command
					// splitter for combined instructions
					String[] mes = inst.split(",");

					// read and write for CPU
					if (mes[0].equals("r")) {
						// r for read
						// pass the data back to CPU
						int addr = Integer.parseInt(mes[1]);
						System.out.println(mem[addr]);
					} else {
						// w for write, pass the info back to CPU
						// execute
						//data
						int a = Integer.parseInt(mes[1]);
						//addr
						int b = Integer.parseInt(mes[2]);
						mem[b] = a;
					} 
		}}catch(Exception e){
		System.exit(0);
		}
	}
}
