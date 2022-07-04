package os3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class SRTF extends Process {
    private ArrayList<Process> Processes = new ArrayList<>(); //list of processes
    private ArrayList<Process> TempProcesses = new ArrayList<>();//list to save the state of processes

    private ArrayList<Process> doneProc = new ArrayList<>(); //list of executed processes
    private HashMap<String, Integer> ProcessesName_BurstTime = new HashMap<>();
    private int ContextSwitching;
    GUI gui ;
    
    public SRTF(ArrayList<Process> processes, int ContextSwitching) {
        this.TempProcesses = processes;
        this.ContextSwitching = ContextSwitching;
        for(int i = 0 ; i < processes.size() ;i++) 
        {
            ProcessesName_BurstTime.put(TempProcesses.get(i).getName(),TempProcesses.get(i).getBurstTime()); //to put the name and the bursttime of the process in the map
        }
        for(Process o: processes) 
        {
            Processes.add(new Process(o));
        }

        gui = new GUI(processes);
    }

    public ArrayList<Process> startScheduling(){ //method for scheduling
        
        Process CurrentProcess, TempProcess;
        int CurrentTime = 0;
        int BurstTime;
        int count =0;
        double TotalWaitingTime = 0.0;
        double TotalTurnaroundTime = 0.0;
        double AverageWatingTime = 0.0;
        double AverageTurnaroundTime = 0.0;
        //Loop in process arraylist to execute all processes

        while(!Processes.isEmpty()){
            int i = 0;
			CurrentProcess = GetArrivedProcess(Processes.get(i), CurrentTime); //the arrived process is the current process
            if(CurrentProcess.getArrivalTime() > CurrentTime){
                do{
                    CurrentTime++; // increase the current time 
                    gui.AddColor( CurrentTime , GetTempProcess(CurrentProcess.getName()) , new Color(255,255,255));
        
                }while(CurrentProcess.getArrivalTime() != CurrentTime);
            }
            do{
                TempProcess = LeastBurstTime(CurrentProcess, CurrentTime); //get the process with least burst and put it in temp
                if(CurrentProcess != TempProcess && count >0)  //check if process in temp is not the current make it the current 
                {
                    AddContextBlocks(CurrentProcess ,CurrentTime , ContextSwitching);
                    CurrentTime += ContextSwitching;
                }
                CurrentProcess = TempProcess; //make the temp current 

                gui.AddColor( CurrentTime+1 ,  GetTempProcess(CurrentProcess.getName()) , CurrentProcess.getColor());
                
                BurstTime = CurrentProcess.getBurstTime(); //get the burst time then decrease it by one 
                BurstTime--;
                CurrentTime++; //increase the currenttime by 1
               
                CurrentProcess.setBurstTime(BurstTime);
                count++;
            }while(BurstTime != 0);
            CurrentProcess.Execute(); //execute the process

            AddContextBlocks(CurrentProcess ,CurrentTime , ContextSwitching);

            CurrentTime += ContextSwitching;


            CurrentProcess.setTurnaroundTime(CurrentTime - CurrentProcess.getArrivalTime());
            CurrentProcess.setWaitingTime(CurrentProcess.getTurnaroundTime() - ProcessesName_BurstTime.get(CurrentProcess.getName()));

//****************************
            TempProcesses.get(GetTempProcess(CurrentProcess.getName())).setWaitingTime(CurrentProcess.getWaitingTime());
            TempProcesses.get(GetTempProcess(CurrentProcess.getName())).setTurnaroundTime(CurrentProcess.getTurnaroundTime());
//***************************

            System.out.println("Process Waiting Time : " + CurrentProcess.getWaitingTime());
            System.out.println("Process TurnAround Time : " + CurrentProcess.getTurnaroundTime());
            Processes.remove(CurrentProcess);
            doneProc.add(CurrentProcess);
            count =0;
        }

        for (Process o : doneProc){
            TotalTurnaroundTime += o.getTurnaroundTime();
            TotalWaitingTime += o.getWaitingTime();
        }
        AverageTurnaroundTime = TotalTurnaroundTime/doneProc.size();
        AverageWatingTime = TotalWaitingTime/doneProc.size();
        System.out.println("Average TurnAround Time is : " + AverageTurnaroundTime);
        System.out.println("Average Waiting Time is : " + AverageWatingTime);

        return TempProcesses;
    }

    private void AddContextBlocks(Process currProcess, int currentTime, int CS) 
    {
        Process p = new Process();
        p.setColor("black");
        gui.AddColor( currentTime+1 , GetTempProcess(currProcess.getName()) , p.getColor(),CS);
        
    }

    Process LeastBurstTime(Process Current, int CurrentTime) //method to get the process with the least burst time and make it the current process
    {
        for(int j=0; j<Processes.size(); j++)
        {
            if(Processes.get(j).getArrivalTime() <= CurrentTime && Processes.get(j).getBurstTime() < Current.getBurstTime())
                Current = Processes.get(j);
        }
        return Current;
    }
    
    Process GetArrivedProcess(Process Curr, int CurrentTime) // compare the arriving process time with the current process time if the arriving process time less than current process time make the arriving process current 
    {
        for(int j=0; j<Processes.size(); j++)
        {
            if(Processes.get(j).getArrivalTime() < Curr.getArrivalTime())
                Curr = Processes.get(j);
        }
        return Curr;
    }

    int GetTempProcess(String ProcessName) // to save the state of the process
    {
        Process temp = null;
        int idx=0;
        for (int j = 0; j < TempProcesses.size(); j++) 
        {
            temp = TempProcesses.get(j);
            if((temp.getName()).equals(ProcessName))
                return j;
        }
        return idx;
    }
}