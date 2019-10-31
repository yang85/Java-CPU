import java.io.*;
import java.util.*;

public class CPU {

	// debug
	public static void de() {
		System.out.printf("PC:" + PC + "\n");
		System.out.printf("AC:" + AC + "\n");
		System.out.printf("IR:" + IR + "\n");
		System.out.printf("X:" + X + "\n");
		System.out.printf("Y:" + Y + "\n");
		System.out.printf("SP:" + SP + "\n");

	}

	// constructor for each CPU entity
	// each passed variables were
	static int PC, SP, IR, AC, X, Y, timer, sml, smh, uml, umh,us,ss;
	static boolean mode, interrupt;
	static int x;
	public static InputStream inp;
	public static OutputStream ous;
	public static Scanner fet;
	public static PrintWriter pri;
	public static Process pro;
	// zeros to all variables

	public static void starter(int usermem, int sysmem) {
		mode = true;// mode = false >> user mode off >> kernel mode
		// Default all variable to 0
		PC = 0;
		SP = 999;
		IR = 0;
		AC = 0;
		X = 0;
		Y = 0;
		// stack pointer
		ss = sysmem+1;
		us = usermem+1;
		timer = 0;
		interrupt = false;
		// user memory = usermem [0-999]
		// system memory = [1000-1999]
		sml = usermem + 1;
		smh = sysmem;
		uml = 0;
		umh = usermem;
	}

	// main function
	public static void main(String args[]) {
		starter(999, 1999);
		// importing code from file and loading to memory
		if (args.length != 2) {
			System.out.println("Command failed");
			System.exit(0);
		}
		String codename = args[0];
		int timer_lim = Integer.parseInt(args[1]);
		try {
			// initialize runtime and process and streams for communication to memory
			// create a process using exec
			Runtime rt = Runtime.getRuntime();
			Process pro = rt.exec("java Memory " + codename);
			// IO stream between CPU and memory
			inp = pro.getInputStream();
			ous = pro.getOutputStream();
			fet = new Scanner(inp);
			pri = new PrintWriter(ous);
			// process the instructions from memory
			while (true) {
				// check timer and interrupter
				if (timer != 0 && interrupt != true && timer == timer_lim) {
					interrupt = true;
					outt();
				}

				// read instruction from memory
				inp = pro.getInputStream();
				// fetching memory
				int impoval = fetmem(PC);
				// breaks if done
				if (impoval != -1) {
					processswitch(impoval);
				} else {
					break;
				}
			}

			pro.waitFor();
			System.out.println("Process has exited at:" + pro.exitValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// return memory fetched from memory.java
	public static int fetmem(int addr) {
		// check if memory address is validated
		if (mode == true && PC >= 1000) {
			System.out.println("Memory out of Bound, Process Ended.");
			System.exit(0);
		}
		// send IO command
		String rcom = "r," + addr + "\n";
		pri.printf(rcom);
		pri.flush();

		if (fet.hasNext()) {
			String temp = fet.next();
			if (temp.isEmpty() != true) {
				int temp2 = Integer.parseInt(temp);
				return (temp2);
			}
		}
		return -1;
	}

	// write into memory
	public static void wrimem(int data, int addr) {
		pri.printf("w," + data + "," + addr + "\n");
		pri.flush();
	}

	// push stack
	public static void pushstack(int data) {
		SP--;
		wrimem(data, SP);
	}

	// pop stack
	public static int popstack() {
		int poped = fetmem(SP);
		wrimem(0, SP);
		SP++;
		return poped;
	}

	// interrupt checker
	public static void iint(boolean interrupt, int timer) {
		if (interrupt == false) {
			timer++;
		}
	}

	// time handler
	public static void outt() {
		mode = false;
		// system
		pushstack(SP);
		SP = smh;
		// user
		pushstack(PC);
		PC = sml;

	}

	public static void processswitch(int impoval) {

		int temp;
		IR = impoval;
		switch (IR) {
		// Load the value into AC
		case 1:
			PC++;
			temp = fetmem(PC);
			AC = temp;
			iint(interrupt, timer);
			PC++;
			break;

		// Load the value at the address into the AC
		case 2:
			PC++;
			// addressof an "address"
			temp = fetmem(PC);
			AC = fetmem(temp);
			iint(interrupt, timer);
			PC++;
			break;

		// Load the value from the address found in the given address into the AC
		case 3:
			PC++;
			int temp2;
			temp = fetmem(PC);
			temp2 = fetmem(temp);
			AC = fetmem(temp2);
			iint(interrupt, timer);
			PC++;
			break;

		// Load the value at (address+X) into the AC
		case 4:
			PC++;
			temp = fetmem(PC);
			AC = fetmem(temp + X);
			iint(interrupt, timer);
			PC++;
			break;

		// Load the value at (address+Y) into the AC
		case 5:
			PC++;
			temp = fetmem(PC);
			AC = fetmem(temp + Y);
			iint(interrupt, timer);
			PC++;
			break;

		// Load from (Sp+X) into the AC
		case 6:
			PC++;
			temp = SP+X;
			AC = fetmem(temp);
			iint(interrupt, timer);
			PC++;
			break;

		// Store the value in the AC into the address
		case 7:
			PC++;
			temp = fetmem(PC);
			wrimem(AC, temp);
			iint(interrupt, timer);
			PC++;
			break;

		// Gets a random int from 1 to 100 into the AC
		case 8:
			Random rand = new Random();
			AC = rand.nextInt(100) + 1;
			iint(interrupt, timer);
			PC++;
			break;

		// If port=1, writes AC as an int to the screen
		// If port=2, writes AC as a char to the screen
		case 9:
			PC++;
			temp = fetmem(PC);
			if (temp == 1) {
				System.out.print(AC);
				iint(interrupt, timer);
				PC++;
				break;
			}
			if (temp == 2) {
				System.out.print((char) AC);
				iint(interrupt, timer);
				PC++;
				break;
			} else {
				System.out.println("Error occurs");
				PC++;
				System.exit(0);
				break;
			}

			// Add the value in X to the AC
		case 10:
			AC = AC + X;
			iint(interrupt, timer);
			PC++;
			break;

		// Add the value in Y to the AC
		case 11:
			AC = AC + Y;
			iint(interrupt, timer);
			PC++;
			break;

		// Subtract the value in X from the AC
		case 12:
			AC = AC - X;
			iint(interrupt, timer);
			PC++;
			break;

		// Subtract the value in Y from the AC
		case 13:
			AC = AC - Y;
			iint(interrupt, timer);
			PC++;
			break;

		// Copy the value in the AC to X
		case 14:
			X = AC;
			iint(interrupt, timer);
			PC++;
			break;

		// Copy the value in X to the AC
		case 15:
			AC = X;
			iint(interrupt, timer);
			PC++;
			break;

		// Copy the value in the AC to Y
		case 16:
			Y = AC;
			iint(interrupt, timer);
			PC++;
			break;

		// Copy the value in Y to the AC
		case 17:
			AC = Y;
			iint(interrupt, timer);
			PC++;
			break;

		// Copy the value in AC to the SP
		case 18:
			SP = AC;
			iint(interrupt, timer);
			PC++;
			break;

		// Copy the value in SP to the AC
		case 19:
			AC = SP;
			iint(interrupt, timer);
			PC++;
			break;

		// Jump to the address
		case 20:
			PC++;
			PC = fetmem(PC);
			iint(interrupt, timer);
			break;

		// Jump to the address only if the value in the AC is zero
		case 21:
			PC++;
			if (AC == 0) {
				PC = fetmem(PC);
				iint(interrupt, timer);
				break;
			}
			iint(interrupt, timer);
			PC++;
			break;

		// Jump to the address only if the value in the AC is not zero
		case 22:
			PC++;
			if (AC != 0) {
				PC = fetmem(PC);
				iint(interrupt, timer);
				break;
			}
			iint(interrupt, timer);
			PC++;
			break;

		// Push return address onto stack, jump to the address
		case 23:
			PC++;
			temp = fetmem(PC);
			pushstack(PC++);
			us = SP;
			PC = temp;
			iint(interrupt, timer);
			break;

		// Pop return address from the stack, jump to the address
		case 24:
			PC = popstack();
			iint(interrupt, timer);
			break;

		// Increment the value in X
		case 25:
			PC++;
			X++;
			iint(interrupt, timer);
			break;

		// Decrement the value in X
		case 26:
			PC++;
			X--;
			iint(interrupt, timer);
			break;

		// Push AC onto stack
		case 27:
			PC++;
			pushstack(AC);
			iint(interrupt, timer);
			break;

		// Pop from stack into AC
		case 28:
			PC++;
			popstack();
			iint(interrupt, timer);
			break;

		// Perform system call
		case 29:
			mode = false;
			interrupt = true;
			temp =SP;
			SP = 2000;
			pushstack(SP);
			temp = PC+1;
			PC = 1500;
			pushstack(temp);
			iint(interrupt, timer);
			break;
			
		// Return from system call
		case 30:
			PC = popstack();
			SP = popstack();
			interrupt = false;
			mode = true;
			timer++;
			break;

		// End execution
		case 50:
			iint(interrupt, timer);
			System.out.println("All instruction processed");
			System.exit(0);
			break;

		// other not listed instructions
		default:
			System.out.println("Instructions not supported");
			System.exit(1);
			break;
		}

	}

}