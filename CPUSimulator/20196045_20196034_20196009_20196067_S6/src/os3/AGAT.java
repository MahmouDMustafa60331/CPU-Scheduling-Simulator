package os3;

import java.util.ArrayList;
import java.awt.Color;
import java.lang.Math;

public class AGAT {
	private ArrayList<Process> Processes = new ArrayList<Process>();
    private ArrayList<Process> ReadyQueue = new ArrayList<>();
    private ArrayList<Process> DiedProcesses = new ArrayList<>();
    
    GUI gui ;
    
    int CurrentTime = 0;
    
	Process CurrentProcess = new Process(); 
	
    public AGAT (ArrayList<Process> P)
    {
    	for(Process i : P)
		{
			Processes.add(new Process(i));
		}	
    	gui = new GUI(Processes);
    	
    	CurrentProcess = Processes.get(0);
    	for(int i = 0 ; i < Processes.size() ; i++) {
    		if(Processes.get(i).getArrivalTime() < CurrentProcess.getArrivalTime()) {
				CurrentProcess  = Processes.get(i);
			}
    		
    	}
    	for (int x = 0; x<Processes.size(); x++) {
    		
    		Processes.get(x).setRemainingBurstTime(Processes.get(x).getBurstTime());
    		Processes.get(x).setQuantumTime(Processes.get(x).getQuantumTime());
    	}
    	
    }
    
    public ArrayList<Process> startScheduling(){
    	
    	double v1 = calcV1();

    	while(!Finished()) {
    		Process temp = CurrentProcess;
    		
    		for (int i =0 ; i<Processes.size(); i++) {
    			if (!DiedProcesses.contains(Processes.get(i))) {
    			AGATfactor(Processes.get(i), v1);
    			}
    		}
    		
    		if (CurrentProcess.getArrivalTime() > CurrentTime) {
    			do {
                	CurrentTime++;
                	gui.AddColor( CurrentTime , Processes.indexOf(temp), new Color(255,255,255));	
    			}while (CurrentProcess.getArrivalTime()!= CurrentTime);
    		}
    		
    		int NonPreemptiveAGAT = (int) Math.ceil((float)CurrentProcess.getQuantumTime()/2.5);
    		int PreempriveAGAT = CurrentProcess.getQuantumTime() - NonPreemptiveAGAT;
    		int TempPreempriveAGAT = PreempriveAGAT;
    		
    		boolean interrupt = false , Die = false;
    		
    		CurrentProcess.Execute();
    		for(int j = 0; j<NonPreemptiveAGAT ; j++) {
    			if (CurrentProcess.getRemainingBurstTime() <= 0) {
    				Die = true;
    				break;
    			}
    			gui.AddColor(CurrentTime+1 , Processes.indexOf(CurrentProcess) , CurrentProcess.getColor());
    			CurrentTime++;
    			CurrentProcess.setRemainingBurstTime(CurrentProcess.getRemainingBurstTime()-1);
    		}
    		
    		temp = MinimumAGATfactor();
    		
    		if(!Die)
				for(int i = 0 ; i < PreempriveAGAT ; i++)
				{
					AddToReady(CurrentProcess);
					temp = MinimumAGATfactor();
					if(CurrentProcess.getRemainingBurstTime() <= 0) {
						Die = true;
					}
					if(temp != CurrentProcess)
					{
						interrupt = true;
					}
					if(Die || interrupt)
						break;
	    			gui.AddColor(CurrentTime+1 , Processes.indexOf(CurrentProcess) , CurrentProcess.getColor());
	    			CurrentTime++;
					TempPreempriveAGAT--;
					CurrentProcess.setRemainingBurstTime(CurrentProcess.getRemainingBurstTime()-1);
				}
    		
    		if(CurrentProcess.getRemainingBurstTime() == 0) {
    			//AddToReady(CurrentProcess);
				Die = true;
			}
    		
    		if(interrupt && !Die)
			{
				CurrentProcess.setQuantumTime(CurrentProcess.getQuantumTime() + TempPreempriveAGAT);
				ReadyQueue.add(CurrentProcess);
			}
    		
    		else
			{
				if(Die)
				{
					
					CurrentProcess.setTurnaroundTime(CurrentTime -CurrentProcess.getArrivalTime());
					
					CurrentProcess.setWaitingTime(CurrentProcess.getTurnaroundTime() - CurrentProcess.getBurstTime());
				
					Process Died = CurrentProcess;
					CurrentProcess.setQuantumTime(0);
					CurrentProcess.setAGATFactor(-1);
					ReadyQueue.remove(Died);
					DiedProcesses.add(Died);
				}
				else
				{
					ReadyQueue.add(CurrentProcess);
					CurrentProcess.setQuantumTime(CurrentProcess.getQuantumTime() + 2);
				}
								
			}

			if(Finished())
				break;
			if(interrupt && !Die) {
				CurrentProcess = temp;
			}
			else {
				if(ReadyQueue.size() == 0) {
					break;
				}
				else
					CurrentProcess = ReadyQueue.get(0);
			}
			ReadyQueue.remove(CurrentProcess);
				
    		
    		
    		
    	}
    	return DiedProcesses;
    }
    
    public void AGATfactor(Process p, double V1){ 
    	double v1 = V1;
    	double v2 = calcV2();
    	
    	p.setAGATFactor(10-p.getPriority() + Math.ceil(p.getArrivalTime()/v1) + Math.ceil(p.getRemainingBurstTime()/v2));
    }
    
    public Process MinimumAGATfactor(){
    	Process minAGAT = new Process();
    	minAGAT = CurrentProcess;
    	if (ReadyQueue.size() >0) {
	    	for (int i =0; i<ReadyQueue.size(); i++) {
	    		if(minAGAT.getAGATFactor() > ReadyQueue.get(i).getAGATFactor()) {
	    			minAGAT = ReadyQueue.get(i);
	    		}
	    	}
    	}
    	return minAGAT;
    }
    
    public int MaxRemainingBurstTime(){
    	int MRBT=0;
    	if (ReadyQueue.size() >0) {
    		MRBT = ReadyQueue.get(0).getRemainingBurstTime();
	    	for (int i =1; i<ReadyQueue.size(); i++) {
	    		if(MRBT<ReadyQueue.get(i).getRemainingBurstTime()) {
	    			MRBT = ReadyQueue.get(i).getRemainingBurstTime();
	    		}
	    	}
    	}
    	return MRBT;
    }
    
    public int MaxArrivalTime(){
    	int MAT=0;
    	if (Processes.size() >0) {
    		MAT = Processes.get(0).getArrivalTime();
	    	for (int i =1; i<Processes.size(); i++) {
	    		if(MAT<Processes.get(i).getArrivalTime()) {
	    			MAT = Processes.get(i).getArrivalTime();
	    		}
	    	}
    	}
    	return MAT;
    }
    
    public double calcV2() {
    	double mrbt = MaxRemainingBurstTime();
    	double V2;
    	if (mrbt > 10)
    		V2 = mrbt/10;
    	else
    		V2 = 1;
    	return V2;
    }
    
    public double calcV1() {
    	double mat = MaxArrivalTime();
    	double V1;
    	if (mat > 10)
    		V1 = mat/10;
    	else
    		V1 = 1;
    	return V1;
    }
    
    private void AddToReady(Process p) {
		for(int i = 0 ; i < Processes.size() ; i++)
		{
			if(CurrentTime >= Processes.get(i).getArrivalTime()  
					&& !ReadyQueue.contains(Processes.get(i)) && !DiedProcesses.contains(Processes.get(i))
					&& i != Processes.indexOf(p))
			{
				ReadyQueue.add(Processes.get(i));
			}
		}
	}
    
    private boolean Finished() {
		// TODO Auto-generated method stub
    	for(int i = 0 ; i < Processes.size() ; i++)
		{
			if(Processes.get(i).getQuantumTime() != 0) {
				return false;
			}
		}
		return false;
	}
    
    
	public double getAverageWaitingTime() {
		double sumOfWaiting = 0.0;
		for(Process p : DiedProcesses) {
			sumOfWaiting+=p.getWaitingTime();
		}
		return sumOfWaiting / DiedProcesses.size();
	}
	public double getAverageTurnaroundTime() {
		double sumOfTurnAround = 0.0;
		for(Process p : DiedProcesses) {
			sumOfTurnAround+=p.getTurnaroundTime();
		}

		return sumOfTurnAround / DiedProcesses.size();
	}
	
	public void showQuantumTimeHistory()
	{
		for(int i = 0 ; i < DiedProcesses.size() ;i++)
		{
			System.out.println("History Of Quantum for process : "+ DiedProcesses.get(i).getName());
			for(int j = 1 ; j < DiedProcesses.get(i).QuantumHistory.size() ; j++)
			{
				System.out.print(DiedProcesses.get(i).QuantumHistory.get(j) + "   ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void showAGATHistory()
	{
		for(int i = 0 ; i < DiedProcesses.size() ;i++)
		{
			System.out.println("History Of AGAT for process : "+ DiedProcesses.get(i).getName());
			for(int j = 1 ; j < DiedProcesses.get(i).AGATHistory.size() ; j++)
			{
				System.out.print(DiedProcesses.get(i).AGATHistory.get(j) + "   ");
			}
			System.out.println();
		}
		System.out.println();
	}
}