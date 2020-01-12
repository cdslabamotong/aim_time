package adaptive_time;

import java.util.ArrayList;

public class TAIM_start{
	
	static public int poolsize, simutimes, budget;
	
	

    public static void run(String name, int vnum, Network network)
    {
        //SeedingProcess_kd.sign_regret_ratio=true;
        long startTime, endTime;
        long launchTime, terminateTime;

        System.out.println("k d "+budget+" "+SeedingProcess_time.round);
        System.out.println("simutimes "+simutimes);
        System.out.println("samplingL "+Policy.samplingL);
        System.out.println("rrsets_size "+Policy.rrsets_size);

        //ArrayList<Double> record;
        //ArrayList<Double> record_budget;

        SeedingProcess_time.createThreadPool(poolsize);
        
        
        Result result=new Result();
        launchTime = System.currentTimeMillis();
//        

        
//        
         System.out.println("-------------------------------------------");
         System.out.println("-------------------------------------------");
         System.out.println("FOM "+ Policy.Fast_policy.theta);
         result.clear();
         startTime = System.currentTimeMillis();
         SeedingProcess_time.MultiGo(network, new Policy.Fast_policy(), simutimes, budget, result ,"FOM",-1);
         endTime = System.currentTimeMillis();
         Tools.printElapsedTime(startTime, endTime, "FOM");
         result.print(SeedingProcess_time.round);
         System.out.println("-------------------------------------------");
         System.out.println("-------------------------------------------");

       
       
        
////        
//        System.out.println("dynamic");
//        result.clear();
//        startTime = System.currentTimeMillis();
//        SeedingProcess_time.MultiGo(network, new Policy.Greedy_policy_dynamic(), simutimes, budget, result,"dynamic",-1);
//        endTime = System.currentTimeMillis();
//        Tools.printElapsedTime(startTime, endTime, "dynamic");
//        result.print(SeedingProcess_time.round);
//        System.out.println("-------------------------------------------");
//        System.out.println("-------------------------------------------");

        
        
        System.out.println("static");
        result.clear();
        startTime = System.currentTimeMillis();
        SeedingProcess_time.MultiGo(network, new Policy.Greedy_policy_kd(), simutimes, budget, result,"static",-1);
        endTime = System.currentTimeMillis();
        Tools.printElapsedTime(startTime, endTime, "static");
        result.print(SeedingProcess_time.round);
        System.out.println("-------------------------------------------");
        System.out.println("-------------------------------------------");


        System.out.println("uniform 1");
        result.clear();
        startTime = System.currentTimeMillis();
        SeedingProcess_time.MultiGo(network, new Policy.Greedy_policy_kd(), simutimes, budget, result,"uniform",1);
        endTime = System.currentTimeMillis();
        Tools.printElapsedTime(startTime, endTime, "uniform 1");
        result.print(SeedingProcess_time.round);
        System.out.println("-------------------------------------------");
        System.out.println("-------------------------------------------");

        
        System.out.println("uniform 2");
        result.clear();
        startTime = System.currentTimeMillis();
        SeedingProcess_time.MultiGo(network, new Policy.Greedy_policy_kd(), simutimes, budget, result,"uniform",2);
        endTime = System.currentTimeMillis();
        Tools.printElapsedTime(startTime, endTime, "uniform 2");
        result.print(SeedingProcess_time.round);
        System.out.println("-------------------------------------------");
        System.out.println("-------------------------------------------");
        
        
        System.out.println("uniform 5");
        result.clear();
        startTime = System.currentTimeMillis();
        SeedingProcess_time.MultiGo(network, new Policy.Greedy_policy_kd(), simutimes, budget, result,"uniform",5);
        endTime = System.currentTimeMillis();
        Tools.printElapsedTime(startTime, endTime, "uniform 5");
        result.print(SeedingProcess_time.round);
        System.out.println("-------------------------------------------");
        System.out.println("-------------------------------------------");
        
        System.out.println("full");
        result.clear();
        startTime = System.currentTimeMillis();
        SeedingProcess_time.MultiGo(network, new Policy.Greedy_policy_kd(), simutimes, budget, result,"full",-1);
        endTime = System.currentTimeMillis();
        Tools.printElapsedTime(startTime, endTime, "full");
        result.print(SeedingProcess_time.round);
        System.out.println("-------------------------------------------");
        System.out.println("-------------------------------------------");
//        

        /*
        terminateTime = System.currentTimeMillis();
        Tools.printElapsedTime(launchTime, terminateTime);*/

        SeedingProcess_time.shutdownThreadPool();
    }

    public static void main(String[] args){
        // TODO Auto-generated method stub
        //double d=0.5;


        //SeedingProcess_time.round=5;
        
        
        //int ratio_times=100;

        //SeedingProcess_kd.round=(k+1)*d;
        
        /*
        String name=args[0];
        String type=args[1];
        int vnum=Integer.parseInt(args[2]);
        int simutimes=Integer.parseInt(args[3]);
        SeedingProcess_time.round=Integer.parseInt(args[4]);
        Policy.simurest_times=Integer.parseInt(args[5]);
        int k=Integer.parseInt(args[6]);*/
    	 String type,name;
    	 int vnum;
    	 
       
        

        //String name="youtube";
        //int vnum=1157900;
        
        
//        String name="dblp";
//        int vnum=430000;
//        String type="WC";
        
//        simutimes=Integer.parseInt(args[0]);
//        poolsize=Integer.parseInt(args[1]);
//        budget=Integer.parseInt(args[2]);
//        SeedingProcess_time.round=Integer.parseInt(args[3]);
//        type=args[4];
//        Policy.samplingL=Integer.parseInt(args[5]);
//        Policy.rrsets_size=Integer.parseInt(args[6]);
//        name=args[7];
//		  vnum=Integer.parseInt(args[8]);
//		  Policy.Fast_policy.theta=Double.parseDouble(args[9]);
    	 
    	 
		  //Policy.Fast_policy.theta=Double.parseDouble(args[10]);
    	 
//    	name="power2500"; 
// 		vnum=2500;
//        		
//		name="wiki"; 
//		vnum=8300;
		
//		name="reddit"; 
//		vnum=125000;
//		
		name="youtube";
        vnum=1157900;
//        
		simutimes=1;
		poolsize=1;
		budget=10;
		SeedingProcess_time.round=10;
		type="IC";
		Policy.samplingL=500;
		Policy.rrsets_size=300000;
		
		
		
		//Policy.Fast_policy.theta=0.2;
    	 
    	 
    	 
        //String name="higgs";
        //int vnum=10000;
        //String type="VIC";

        //String name="hepph";
        //int vnum=35000;
        //String type="WC";

        //String name="hepth";
        //int vnum=27770;
        //String type="WC";

       



        String path="data/"+name+".txt";
        Network network=new Network(path, type , vnum);
        //network.set_ic_prob(Double.parseDouble(args[10]));
        network.set_ic_prob(0.005);
        
       Policy.Fast_policy.theta=0.6;
       TAIM_start.run(name, vnum, network);
//        
//       Policy.Fast_policy.theta=0.4;
//       TAIM_start.run(name, vnum, network);
//        
//       Policy.Fast_policy.theta=0.5;
//       TAIM_start.run(name, vnum, network);
//////        
//       Policy.Fast_policy.theta=0.6;
//       TAIM_start.run(name, vnum, network);
//       
//       Policy.Fast_policy.theta=0.7;
//       TAIM_start.run(name, vnum, network);
//       
//       Policy.Fast_policy.theta=0.8;
//       TAIM_start.run(name, vnum, network);
//       
//       Policy.Fast_policy.theta=0.9;
//       TAIM_start.run(name, vnum, network);
//       
//       Policy.Fast_policy.theta=0.95;
//       TAIM_start.run(name, vnum, network);
//       
//       Policy.Fast_policy.theta=0.98;
//       TAIM_start.run(name, vnum, network);




    }

}
