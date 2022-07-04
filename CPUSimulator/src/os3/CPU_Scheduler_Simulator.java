package os3;

import java.util.ArrayList;
import java.util.Scanner;

public class CPU_Scheduler_Simulator {
	
	static public void main (String[] arg)
	{
		int ProcessesNumber;
		int contextSwitching;
		
		ArrayList<Process> Processes = new ArrayList<Process>();

		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter n of processes :");
		ProcessesNumber = input.nextInt();
		
		System.out.println("Enter context Switching Time :");
		contextSwitching = input.nextInt();
		
		for(int i = 0 ; i < ProcessesNumber ;i++)
		{
			input = new Scanner(System.in);
			Process InputProcess = new Process();
			System.out.println( (i+1) + " Enter Process Name :");
			InputProcess.setName(input.nextLine());
		
			System.out.println("Enter Process Burst Time :");
			InputProcess.setBurstTime(input.nextInt())  ;
		
			System.out.println("Enter Process Arrival Time :");
			InputProcess.setArrivalTime(input.nextInt()) ;
			
			System.out.println("Enter Process priority :");
			InputProcess.setPriority(input.nextInt());
			
			input = new Scanner(System.in);
			System.out.println( "Enter Process Color :");
			InputProcess.setColor(input.nextLine());
			
			Processes.add(InputProcess);	
		}
		
		System.out.println("\nSelect the Scheduler you want to use : "+ "\n1-Non-Preemptive Priority Scheduling"+ "\n2-Non-Preemptive Shortest- Job First (SJF)"+ "\n3-Shortest-Remaining Time First (SRTF)"+ "\n4-AGAT Scheduling");
		int selection = input.nextInt();
		switch (selection) {
			case 1:{
				Priority PriorityScheduling = new Priority(Processes, contextSwitching);
				Processes = PriorityScheduling.startScheduling(); 
		
				System.out.println( "\nAverage Waiting Time :  " + PriorityScheduling.getAverageWaiting());
				System.out.println("Average Turnaround Time :" + PriorityScheduling.getAverageTurnAround() + "\n");
				break;
			}
			case 2:{
				SJF SJFScheduling= new SJF (Processes);
				SJFScheduling.SJFStartScheduling();
				
				System.out.println( "\nAverage Waiting Time :  " + SJFScheduling.getAverageWaitingTime());
				System.out.println("Average Turnaround Time :" + SJFScheduling.getAverageTurnaroundTime() + "\n");
				break;
			}
			case 3:{
				SRTF SRTFScheduling = new SRTF(Processes, contextSwitching);
				Processes = SRTFScheduling.startScheduling();
				break;
			}

			case 4:{
				for(int i = 0 ; i < ProcessesNumber ;i++) {
					
					System.out.println( (i+1) + " Enter Process Quantum :");
					Processes.get(i).setQuantumTime(input.nextInt());
				}
				AGAT AGATScheduling = new AGAT(Processes);
				Processes = AGATScheduling.startScheduling();
				
				AGATScheduling.showQuantumTimeHistory();
				AGATScheduling.showAGATHistory();
				System.out.println( "\nAverage Waiting Time :  " + AGATScheduling.getAverageWaitingTime());
				System.out.println("Average Turnaround Time :" + AGATScheduling.getAverageTurnaroundTime() + "\n");
				break;
			}
			default:{
				System.out.println("You Entered WRONG CHOICE!");
				break;
			}
		}
		
		for(int i = 0 ; i < Processes.size();i++)
		{
			Processes.get(i).printProcess();
		}
	}

}