package adaptive_time;

import java.util.ArrayList;
import java.util.Random;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import adaptive_time.Policy.Command;

public class SeedingProcess_time{

    public static int round=-1;
    //public static int poolsize=1;
    private static ExecutorService pool = null;
    private static ArrayList<Future<Double>> results = new ArrayList<Future<Double>>();

    public static void createThreadPool(int poolsize){
        pool = Executors.newFixedThreadPool(poolsize);
    }

    public static void shutdownThreadPool(){
        pool.shutdown();
    }

    public static void MultiGo(
            Network network,
            Command command,
            int simutimes,
            int budget,
            Result allresult,
            String type, int d)
    {
        //ArrayList<Double> c_result=new ArrayList<Double>();
        System.out.println("MultiGo");
        if(round==-1)
        {
            throw new ArithmeticException("round = -1 ");
        }
        for(int i=0; i<round; i++)
        {
        	allresult.exp_influence_result.add(0.0);
            allresult.exp_budget_result.add(0.0);
            
            allresult.var_influence_result.add(0.0);
            allresult.var_budget_result.add(0.0);
        }
        double result=0;
        ArrayList<ArrayList<Double>> records = new ArrayList<>();
        ArrayList<ArrayList<Double>> records_budget = new ArrayList<>();
        for(int i=0; i<simutimes; i++)
        {
            //c_result.clear();
            //System.out.println("Simulation number "+i);
            ArrayList<Double> _record = new ArrayList<Double>(allresult.exp_influence_result);
            ArrayList<Double> _record_budget = new ArrayList<Double>(allresult.exp_budget_result);
            records.add(_record);
            records_budget.add(_record_budget);
            Callable<Double> callObj = null;
            switch(type)
            {
            case "FOM":
                callObj = new Callable<Double>(){
                    @Override
                    public Double call(){
                        return Go_fast(network, command, round, budget, _record, _record_budget);
                    }
                };
                break;    
            case "dynamic":
                    callObj = new Callable<Double>(){
                        @Override
                        public Double call(){
                            return Go_dynamic(network, command, round, budget, _record, _record_budget);
                        }
                    };
                    break;
                case "static":
                    callObj = new Callable<Double>(){
                        @Override
                        public Double call(){
                            return Go_static(network, command, round, budget, _record, _record_budget);
                        }
                    };
                    break;
                case "uniform":
                    callObj = new Callable<Double>(){
                        @Override
                        public Double call(){
                            return Go_uniform_d(network, command, round, d, budget, _record, _record_budget);
                        }
                    };
                    break;
                case "full":
                    callObj = new Callable<Double>(){
                        @Override
                        public Double call(){
                            return Go_full(network, command, round, budget, _record, _record_budget);
                        }
                    };
                    break;
                default:
                    System.out.print("Invalid model");
            }
            results.add(pool.submit(callObj));
        }

        for(Future<Double> future: results){
            try{
                result += future.get();
            }
            catch(InterruptedException e){
                System.out.println("Interrupted");
            }
            catch(ExecutionException e){
                System.out.println("Fail to get the result");
                e.printStackTrace();
            }
        }

        for(int i = 0;i < round;i++){
            double record_val = 0.0;
            double record_budget_val = 0.0;
            for(int j = 0;j < simutimes;j++){
                record_val += records.get(j).get(i);
                record_budget_val += records_budget.get(j).get(i);
            }
            allresult.exp_influence_result.set(i, record_val/simutimes);
            allresult.exp_budget_result.set(i, record_budget_val/simutimes);
            
            
            record_val = 0.0;
            record_budget_val = 0.0;
            for(int j = 0;j < simutimes;j++){
                record_val += Tools.sqr(records.get(j).get(i)-allresult.exp_influence_result.get(i));
                record_budget_val += Tools.sqr(records_budget.get(j).get(i)-allresult.exp_budget_result.get(i));
            }
            allresult.var_influence_result.set(i, Math.sqrt(record_val/simutimes));
            allresult.var_budget_result.set(i, Math.sqrt(record_budget_val/simutimes));
        }
        
        
        for(int j = 0;j < Math.min(10, simutimes);j++){
        	Tools.printdoublelistln(records_budget.get(j), round);
        }

        System.out.println(result/simutimes);
    }

    public static double Go_dynamic(Network network, Command command, int round, int budget, ArrayList<Double> record, ArrayList<Double> record_budget)
    {
        //System.out.println("Go");
        Random rand = new Random();
        DiffusionState diffusionState=new DiffusionState(network, round, budget);
        double influence=0;
        double computing_time=0;
        double computing_num=0;
        for(int i=0; i<round; i++)
        {
            //System.out.println("Round "+i+"-"+round);
            ArrayList<Integer> seed_set=new ArrayList<Integer>();
            long startTime = System.currentTimeMillis();
            seed_set=command.compute_seed_set(network, diffusionState,0, rand);
            computing_time=computing_time+ System.currentTimeMillis()-startTime;
        	computing_num++;
            //Tools.printlistln(seed_set);
            //System.out.println("seed set size "+seed_set.size());
            diffusionState.seed(seed_set);
            influence=diffusionState.diffuse(network, 1, rand);
            record.set(i, record.get(i)+diffusionState.aNum);
            record_budget.set(i, record_budget.get(i)+seed_set.size());
        }
        System.out.println("seed time "+computing_time/computing_num+" "+computing_time);
        return influence;

    }

    public static double Go_uniform_d(Network network, Command command, int round, int d, int budget, ArrayList<Double> record,ArrayList<Double> record_budget)
    {
        // System.out.println("Go uniform");
        Random rand = new Random();
        DiffusionState diffusionState=new DiffusionState(network, round, budget);
        double influence=0;
        double computing_time=0;
        double computing_num=0;
        for(int i=0; i<round; i++)
        {
            //System.out.println("Round "+i+"-"+round);
            ArrayList<Integer> seed_set=new ArrayList<Integer>();
            if(i==round-1 && diffusionState.budget_left>0)
            {

            	long startTime = System.currentTimeMillis();
            	seed_set=command.compute_seed_set(network, diffusionState,  diffusionState.budget_left, rand);
            	computing_time=computing_time+ System.currentTimeMillis()-startTime;
            	computing_num++;
                diffusionState.seed(seed_set);
            }
            else if(i % d==0 && diffusionState.budget_left>0)
            {
            	long startTime = System.currentTimeMillis();
                seed_set=command.compute_seed_set(network, diffusionState, Math.min(budget/(round/d), diffusionState.budget_left), rand);
                computing_time=computing_time+ System.currentTimeMillis()-startTime;
                computing_num++;
                diffusionState.seed(seed_set);
            }

            //ArrayList<Integer> seed_set=new ArrayList<Integer>();
            //seed_set=command.compute_seed_set(network, diffusionState);
            //Tools.printlistln(seed_set);
            //System.out.println("seed set size "+seed_set.size());

            influence=diffusionState.diffuse(network, 1, rand);
            record.set(i, record.get(i)+diffusionState.aNum);
            record_budget.set(i, record_budget.get(i)+seed_set.size());
            //System.out.println("record_budget.get(i) "+record_budget.get(i));
        }
        System.out.println("seed time "+computing_time/computing_num+" "+computing_time);
        return influence;

    }

    public static double Go_static(Network network, Command command, int round, int budget, ArrayList<Double> record,ArrayList<Double> record_budget)
    {
        //System.out.println("Go");
        Random rand = new Random();
        DiffusionState diffusionState=new DiffusionState(network, round, budget);
        double influence=0;
        double computing_time=0;
        double computing_num=0;
        for(int i=0; i<round; i++)
        {

            ArrayList<Integer> seed_set=new ArrayList<Integer>();
            if(i==0)
            {
            	long startTime = System.currentTimeMillis();
            	seed_set=command.compute_seed_set(network, diffusionState, budget, rand);
            	computing_time=computing_time+ System.currentTimeMillis()-startTime;
            	computing_num++;
                diffusionState.seed(seed_set);
            }


            influence=diffusionState.diffuse(network, 1, rand);
            record.set(i, record.get(i)+diffusionState.aNum);
            record_budget.set(i, record_budget.get(i)+seed_set.size());
        }
        //System.out.println();
        System.out.println("seed time "+computing_time/computing_num+" "+computing_time);
        return influence;

    }

    public static double Go_full(Network network, Command command, int round, int budget, ArrayList<Double> record,ArrayList<Double> record_budget)
    {
        //System.out.println("Go");
        Random rand = new Random();
        DiffusionState diffusionState=new DiffusionState(network, round, budget);
        double influence=0;
        double computing_time=0;
        double computing_num=0;
        for(int i=0; i<round; i++)
        {

            ArrayList<Integer> seed_set=new ArrayList<Integer>();
            if(i==round-1 && diffusionState.budget_left>0)
            {
            	long startTime = System.currentTimeMillis();
            	seed_set=command.compute_seed_set(network, diffusionState, diffusionState.budget_left, rand);
            	computing_time=computing_time+ System.currentTimeMillis()-startTime;
            	computing_num++;
                diffusionState.seed(seed_set);
            }
            if(i<round-1 && diffusionState.newActive.size()==0)
            {
            	long startTime = System.currentTimeMillis();
            	seed_set=command.compute_seed_set(network, diffusionState, 1, rand);
            	computing_time=computing_time+ System.currentTimeMillis()-startTime;
            	computing_num++;
                diffusionState.seed(seed_set);
            }


            influence=diffusionState.diffuse(network, 1, rand);
            record.set(i, record.get(i)+diffusionState.aNum);
            record_budget.set(i, record_budget.get(i)+seed_set.size());
        }
        System.out.println("seed time "+computing_time/computing_num+" "+computing_time);
        return influence;

    }

    public static double Go_fast(Network network, Command command, int round, int budget, ArrayList<Double> record,ArrayList<Double> record_budget)
    {
        //System.out.println("Go");
        Random rand = new Random();
        DiffusionState diffusionState=new DiffusionState(network, round, budget);
        double influence=0;
        double computing_time=0;
        double computing_num=0;
        for(int i=0; i<round; i++)
        {

			//System.out.println("Round "+i+"-"+round); 
			ArrayList<Integer> seed_set=new ArrayList<Integer>();
			long startTime = System.currentTimeMillis();
			seed_set=command.compute_seed_set(network, diffusionState, 0, rand);
			computing_time=computing_time+ System.currentTimeMillis()-startTime;
			computing_num++;
			//Tools.printlistln(seed_set);
			//System.out.println("seed set size "+seed_set.size());
			diffusionState.seed(seed_set);
			influence=diffusionState.diffuse(network, 1, rand);
			record.set(i, record.get(i)+diffusionState.aNum);
			record_budget.set(i, record_budget.get(i)+seed_set.size());
        }
        System.out.println("seed time "+computing_time/computing_num+" "+computing_time);
        return influence;

    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub




    }

}
