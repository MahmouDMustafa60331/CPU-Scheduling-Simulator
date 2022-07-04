package os3;

import java.awt.Color;
import java.util.ArrayList;

public class SJF {
   private ArrayList<Process> processes;
   private ArrayList<Integer> doneProc = new ArrayList<Integer>();
   GUI gui ;
   
    public SJF(ArrayList<Process> processes) 
    {
        this.processes = processes;
        gui = new GUI(processes);
    }
    
    public void SJFStartScheduling(){
        int currentTime = 0 , currentProcess = 0;
        for (int i = 0; i <processes.size() ; i++)
        {
            int temp = currentProcess;
            do {
                currentProcess = getNextProcessNumber(currentTime);
                if (currentProcess==-1) {
                    currentTime++;
                    gui.AddColor( currentTime , temp, new Color(255,255,255));    
                }
                }while (currentProcess==-1);
            
            processes.get(currentProcess).Execute();
            
            gui.AddColor( currentTime+1 , currentProcess, processes.get(currentProcess).getColor() ,processes.get(currentProcess).getBurstTime() );
            
            processes.get(currentProcess).setWaitingTime(currentTime - processes.get(currentProcess).getArrivalTime());
            
            processes.get(currentProcess).setTurnaroundTime(processes.get(currentProcess).getWaitingTime()+processes.get(currentProcess).getBurstTime());
            
            currentTime = currentTime + processes.get(currentProcess).getBurstTime();
            
            doneProc.add(currentProcess);

        }
    }
    
    private int getNextProcessNumber(int currentTime){
        int nextProcess=-1;
        for(int i=0;i<processes.size();i++)
        {
            if(!doneProc.contains(i) && processes.get(i).getArrivalTime()<=currentTime){ 
                
               if(nextProcess==-1) // base case for first process
               { 
                   nextProcess=i; 
               } 
               else if(processes.get(i).getBurstTime()<processes.get(nextProcess).getBurstTime())
               {
                   nextProcess=i;
               }
            }
        }
        return nextProcess;
    }
    
    public double getAverageTurnaroundTime() {
        double sumTA = 0.0;
        for(int i =0 ; i < processes.size() ; i++) {
            sumTA = sumTA + processes.get(i).getTurnaroundTime();
        }
        double avg = sumTA / processes.size();

        return avg;
    }
    
    public double getAverageWaitingTime() {
        double waitingSum = 0.0;
        for(int i =0 ; i < processes.size() ; i++) {
            waitingSum = waitingSum + processes.get(i).getWaitingTime();
        }
        double avg = waitingSum / processes.size();
        return avg;
    }
}