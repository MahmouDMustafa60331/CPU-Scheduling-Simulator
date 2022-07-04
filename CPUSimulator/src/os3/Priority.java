package os3;

import java.awt.Color;
import java.util.ArrayList;

public class Priority {
	ArrayList<Process> Processes = new ArrayList<Process>();
	ArrayList<Process> WaitingQueue = new ArrayList<Process>();
	ArrayList<Process> executedProcesses = new ArrayList<Process>(); 
	ArrayList<Process> copy ;

	GUI gui ;
	
	int CurrentTime = 0 ;
	int agingVal = 3 ;
	int Context;
	
	Priority(ArrayList<Process> Q, int ContextSwitching){
		for(Process i : Q)
		{
			Processes.add(new Process(i));
		}	
        
		
		gui = new GUI(Processes);
		copy = new ArrayList<Process>(Processes);
		
		Processes.sort(null);
		CurrentTime = Processes.get(0).getArrivalTime();
		ConstructWaitingQueue(CurrentTime);
		this.Context = ContextSwitching;

	}
	
	public void setAgingValue(int value) {
		agingVal = value;
	}
	
	public ArrayList<Process> startScheduling() {
		Process currentProcess = new Process();
		
		gui.AddColor( 1 , copy.indexOf(currentProcess), new Color(255,255,255), CurrentTime-1);
		
		while(Processes.size() > 0 ) {
			if(MaxPriority()==null) {
				CurrentTime++;
				gui.AddColor( CurrentTime ,copy.indexOf(currentProcess) , new Color(255,255,255)); 	
				
				ConstructWaitingQueue(CurrentTime);	
			}
			else {
			currentProcess = MaxPriority();
			gui.AddColor(CurrentTime+1 , copy.indexOf(currentProcess) , currentProcess.getColor(), currentProcess.getBurstTime());

			currentProcess.setStartTime(CurrentTime);
			CurrentTime += currentProcess.getBurstTime();
			int end = CurrentTime + currentProcess.getBurstTime();

			currentProcess.setWaitingTime( currentProcess.getStartTime() - currentProcess.getArrivalTime());
			currentProcess.setTurnaroundTime(currentProcess.getWaitingTime() + currentProcess.getBurstTime() );
			
			executedProcesses.add(currentProcess);
			Processes.remove(currentProcess);
			
			ConstructWaitingQueue(CurrentTime);
			AgingProcess(end,agingVal);
			currentProcess.Execute();
			AddContextBlocks(currentProcess, CurrentTime ,Context );
			CurrentTime += Context;

			}
		}
		return executedProcesses;
	}
	
    private Process LeastPriority() {
		Process LeastPri = new Process();
		if (WaitingQueue.size()>0) {
			
			LeastPri = WaitingQueue.get(0);
		
			for(int i = 1 ; i < WaitingQueue.size() ; i++ ) {
				if (WaitingQueue.get(i).getPriority() > LeastPri.getPriority()) {
					LeastPri = WaitingQueue.get(i);
				}
			}
		}
		return LeastPri;
	}
	
	private void AgingProcess(int end, int aging) {
		Process Least = LeastPriority();
		int multi = 1;
		if (end >= (aging * multi))
		{
			int x = Least.getPriority();
			x--;
			Least.setPriority(x);
			multi++;
			
		}
	}
	
	public double getAverageWaiting() {
		double sumOfWaiting = 0.0;
		for(Process p : executedProcesses) {
			sumOfWaiting+=p.getWaitingTime();
		}
		return sumOfWaiting / executedProcesses.size();
	}
	public double getAverageTurnAround() {
		double sumOfTurnAround = 0.0;
		for(Process p : executedProcesses) {
			sumOfTurnAround+=p.getTurnaroundTime();
		}
		return sumOfTurnAround / executedProcesses.size();
	}
	private Process MaxPriority() {
		Process maxPriority = null;
		if(WaitingQueue.size()>0) {
			maxPriority = WaitingQueue.get(0);
			for(int i = 1 ; i< WaitingQueue.size() ; i++) {
				if(maxPriority.getPriority() >= WaitingQueue.get(i).getPriority()) {
					if(maxPriority.getPriority() == WaitingQueue.get(i).getPriority())
						maxPriority =(maxPriority.getArrivalTime() > WaitingQueue.get(i).getArrivalTime() ?WaitingQueue.get(i) :maxPriority);
					else
						maxPriority = WaitingQueue.get(i);
				}
			}
		}
		return maxPriority ;
	}
	
	private void ConstructWaitingQueue(int currentTime) {
		WaitingQueue = new ArrayList<Process>();
		for(int i = 0 ; i < Processes.size() ; i++ )
			if(Processes.get(i).getArrivalTime() <= currentTime) {
				WaitingQueue.add(Processes.get(i));
			}
			else break;
	}	
    private void AddContextBlocks(Process currProcess, int currentTime, int CS) 
    {
        Process p = new Process();
        p.setColor("black");
        gui.AddColor( currentTime+1 , GetCopyProcess(currProcess.getName()) , p.getColor(),CS);
        
    }
    int GetCopyProcess(String ProcessName)
    {
        Process temp = null;
        int idx=0;
        for (int j = 0; j < copy.size(); j++) 
        {
            temp = copy.get(j);
            if((temp.getName()).equals(ProcessName))
                return j;
        }
        return idx;
    }

	
}