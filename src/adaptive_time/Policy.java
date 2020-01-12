package adaptive_time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Random;

public class Policy{
    public static int rrsets_size;
    //public static int simurest_times;
    public static int samplingL;
    public static String simumethod;

    public interface Command
    {
        public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int k, Random rand);
        //public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int k, Random rand);
    }

    //public interface Command_k
    //{
    //    public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int k, Random rand);
    //}

    public static class Greedy_policy_kd implements Command
    {
        public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int k, Random rand)
        {
            ArrayList<Integer> result=new ArrayList<Integer>();
            reverse_greedy_k_lazy(network, diffusionState, result, k, rand);
            //simu_greedy_1(network, diffusionState, result);
            return result;
        }
    }

    public static class Greedy_time implements Command
    {
        public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int k, Random rand)
        {
            ArrayList<Integer> result=new ArrayList<Integer>();
            reverse_greedy_k_lazy_time(network, diffusionState, result, k, rand);
            //simu_greedy_1(network, diffusionState, result);
            return result;
        }
    }

    public static class Localgreedy_policy_kd implements Command
    {
        public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int k, Random rand)
        {
            ArrayList<Integer> result=new ArrayList<Integer>();
            reverse_greedy_k_lazy(network, diffusionState, result, k, rand);
            //simu_greedy_1(network, diffusionState, result);
            return result;
        }
    }

    public static class Random_policy_kd implements Command
    {
        public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int k, Random rand)
        {
            ArrayList<Integer> result=new ArrayList<Integer>();
            while(result.size()<k)
            {
                int index=(int) (rand.nextFloat()*network.vertexNum);
                if(!diffusionState.state[index] && !result.contains(index))
                {
                    result.add(index);
                }
            }
            return result;
        }
    }

    public static class Degree_policy_kd implements Command
    {
        public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int k, Random rand)
        {
            ArrayList<Integer> result=new ArrayList<Integer>();
            for(int i=0;i<network.vertexNum;i++)
            {
                if(!diffusionState.state[network.sort_by_degree.get(i)])
                {
                    result.add(network.sort_by_degree.get(i));
                    if(result.size()==k)
                    {
                        return result;
                    }
                    break;
                }

            }
            return result;
        }
    }
    
    public static class Fast_policy implements Command
    {
        static double alpha, theta;
        
        
    	public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int k, Random rand)
        {
    		ArrayList<Integer> result=new ArrayList<Integer>();
    		int time_remain=diffusionState.round_left;
    		int budget_remain=diffusionState.budget_left;
    		
    		//System.out.println("reverse_greedy_k.............  ");
        	ArrayList<Integer> S_k=new ArrayList<Integer>();
        	reverse_greedy_k_lazy(network, diffusionState, S_k, budget_remain, rand);
        	
        	//for Ma
        	//System.out.println("Ma.............  ");
        	double[] gsi=new double[budget_remain+1];
        	for(int i=0;i<budget_remain+1;i++)
        	{
        		gsi[i]=0;
        	}
        	//double g_empty;
        	ArrayList<Integer> tempseed=new ArrayList<Integer>();
        	for(int i=0; i<samplingL; i++)
        	{
        		int round_left=diffusionState.round_left;
        		DiffusionState temp_state=new DiffusionState(diffusionState);
        		temp_state.round_limit=false;
        		gsi[0]=gsi[0]+temp_state.diffuse(network, round_left, rand);
        		for(int j=1; j<budget_remain+1; j++)
                {
        			tempseed.clear();
        			if(!temp_state.state[S_k.get(j-1)])
        			{
        				tempseed.add(S_k.get(j-1));
            			temp_state.seed(tempseed);
        			}
        			gsi[j]=gsi[j]+temp_state.diffuse(network, round_left, rand);
                }
            	
        	}
        	for(int i=0;i<budget_remain+1;i++)
        	{
        		gsi[i]=gsi[i]/samplingL;
        		//System.out.println("gsi[i]  "+gsi[i]);
        	}
        	//gsi[0]=diffusionState.exp_influence_complete(network, samplingL, rand);
        	
        	double[] gi=new double[budget_remain];
        	tempseed.clear();
        	//g_empty=diffusionState.exp_influence_complete(network, samplingL, rand);
        	//System.out.println("Margin.............  ");
        	for(int i=0; i<budget_remain; i++)
            {
        		tempseed.clear();
        		tempseed.add(S_k.get(i));
        		DiffusionState temp_state=new DiffusionState(diffusionState);
        		temp_state.seed(tempseed);
        		gi[i]=temp_state.exp_influence_complete(network, samplingL, rand);
        		//System.out.println("gi[i]  "+gi[i]);
            }
        	
        	//for Mt
        	//System.out.println("Mt.............  ");
        	double[] hi=new double[budget_remain];
        	double[] hi_1=new double[budget_remain];
        	for(int i=0; i<budget_remain; i++)
            {
        		hi[i]=gsi[i+1]-gsi[i];
                
        		
            }
        	tempseed.clear();
        	
        	
        	ArrayList<Integer> seed_1=new ArrayList<Integer>();
        	ArrayList<Integer> seed_2=new ArrayList<Integer>();
        	for(int i=0; i<budget_remain; i++)
            {
        		hi_1[i]=0;
        		seed_2.add(S_k.get(i));
        		for(int j=0; j<samplingL; j++)
            	{
            		DiffusionState temp_state=new DiffusionState(diffusionState);
            		temp_state.seed(seed_1);
            		temp_state.diffuse(network, time_remain, rand);
            		
            		double atemp=temp_state.aNum;
            		if(!temp_state.state[S_k.get(i)])
            		{
            			temp_state.seed(seed_2);
            			temp_state.diffuse(network, time_remain-1, rand);
            		}
            		
            		hi_1[i]=hi_1[i]+temp_state.aNum-atemp;
            	}
        		hi_1[i]=hi_1[i]/samplingL;
        		
        		seed_1.add(S_k.get(i));
        		seed_2.clear();
            }
        	
        	
        	alpha=1-1.0/diffusionState.round_left;
        	//System.out.println("round_left "+diffusionState.round_left);
            for(int i=0; i<budget_remain; i++)
            {
               double Ma=(gsi[i+1]-gsi[i])/(gi[i]-gsi[0]);
               double Mt=(hi[i]-hi_1[i])/hi[i];
               //System.out.println(Ma+ "  "+Mt);
               Ma=Math.min(1, Math.max(Ma, 0));
               Mt=Math.min(1, Math.max(Mt, 0));
               double indi;
               if(Ma<0.0001)
               {
            	   indi=Mt;
               }
               else if(Ma>0.9999)
               {
            	   indi=Ma;
               }
               else if (Mt<0.0001)
               {
            	   indi=Ma;
               }
               else if (Mt>0.9999)
               {
            	   indi=Mt;
               }
               else
               {
            	   indi=alpha*Ma+(1-alpha)*Mt;
               }
               //System.out.println(Ma+ "  "+Mt+" "+alpha+" "+indi+" "+theta);
               //double indi=Math.max(Ma, Mt);
              // System.out.println("indi "+indi);
               if(indi < theta)
               {
            	   break;
               }
               else
               {
            	   result.add(S_k.get(i));
               }

            }
            return result;
        }
    	
 
    }


    public static class Greedy_policy_dynamic implements Command
    {
        public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int nothing, Random rand)
        {
            //System.out.println("Greedy_policy");
            ArrayList<Integer> result=new ArrayList<Integer>();
            if(diffusionState.budget_left==0)
            {
                return result;
            }

            if(!diffusionState.round_limit)
            {
                select_k(network, diffusionState, result, 1, rand);
                return result;
            }

            if(diffusionState.round_left==1)
            {
                select_k(network, diffusionState, result, diffusionState.budget_left, rand);
                return result;
            }

            double profit=Double.MIN_VALUE;
            for(int k=1;k<=diffusionState.budget_left;k++)
            {
                //System.out.println(diffusionState.aNum);

                ArrayList<Integer> temp_result=new ArrayList<Integer>();

                double temp=select_k(network, diffusionState, temp_result, k, rand);
                //System.out.println(temp+" "+k);
                if(temp>profit)
                {
                    profit=temp;
                    result=new ArrayList<Integer>(temp_result);
                }
            }
            //Tools.printlistln(result);
            //System.out.println(result.size());
            return result;
        }


        private double select_k(Network network, DiffusionState diffusionState, ArrayList<Integer> result, int k, Random rand)
        {
            reverse_greedy_k_lazy(network, diffusionState, result, k, rand);
            //System.out.println(diffusionState.aNum);
            double influence=0;
            //int simureset_times=10000;
            for(int i=0;i<samplingL;i++)
            {
                DiffusionState temp=new DiffusionState(diffusionState);
                //System.out.println("simurest_times "+i);
                //System.out.print(diffusionState.aNum+" ");
                //System.out.println(temp.state[result.get(0)]);
                temp.seed(result);
                temp.diffuse(network, 1, rand);
                //System.out.print(temp.aNum+" ");
                ArrayList<Integer> tempseed=new ArrayList<Integer>();
                reverse_greedy_k_lazy(network, temp, tempseed, temp.budget_left, rand);
                temp.seed(tempseed);
                temp.diffuse(network, temp.round_left, rand);
                influence=influence+temp.aNum;
                //influence=influence+ reverse_greedy_k(network, temp, tempseed, temp.budget_left);
            }
            //System.out.println();
            return influence/samplingL;
        }
    }
    
    
    public static class Greedy_policy_dynamic_fast implements Command
    {
        public ArrayList<Integer> compute_seed_set(Network network, DiffusionState diffusionState, int nothing, Random rand)
        {
            //System.out.println("Greedy_policy");
            ArrayList<Integer> result=new ArrayList<Integer>();
            if(diffusionState.budget_left==0)
            {
                return result;
            }

            if(!diffusionState.round_limit)
            {
                select_k_fast(network, diffusionState, result, 1, rand);
                return result;
            }

            if(diffusionState.round_left==1)
            {
                select_k_fast(network, diffusionState, result, diffusionState.budget_left, rand);
                return result;
            }

            double profit=Double.MIN_VALUE;
            for(int k=1;k<=diffusionState.budget_left;k++)
            {
                //System.out.println(diffusionState.aNum);

                ArrayList<Integer> temp_result=new ArrayList<Integer>();

                double temp=select_k_fast(network, diffusionState, temp_result, k, rand);
                //System.out.println(temp+" "+k);
                if(temp>profit)
                {
                    profit=temp;
                    result=new ArrayList<Integer>(temp_result);
                }
                else
                {
                	break;
                }
            }
            //Tools.printlistln(result);
            //System.out.println(result.size());
            return result;
        }


        private double select_k_fast(Network network, DiffusionState diffusionState, ArrayList<Integer> result, int k, Random rand)
        {
            reverse_greedy_k_lazy(network, diffusionState, result, k, rand);
            //System.out.println(diffusionState.aNum);
            double influence=0;
            //int simureset_times=10000;
            for(int i=0;i<samplingL;i++)
            {
                DiffusionState temp=new DiffusionState(diffusionState);
                //System.out.println("simurest_times "+i);
                //System.out.print(diffusionState.aNum+" ");
                //System.out.println(temp.state[result.get(0)]);
                temp.seed(result);
                temp.diffuse(network, 1, rand);
                //System.out.print(temp.aNum+" ");
                ArrayList<Integer> tempseed=new ArrayList<Integer>();
                reverse_greedy_k_lazy(network, temp, tempseed, temp.budget_left, rand);
                temp.seed(tempseed);
                temp.diffuse(network, temp.round_left, rand);
                influence=influence+temp.aNum;
                //influence=influence+ reverse_greedy_k(network, temp, tempseed, temp.budget_left);
            }
            //System.out.println();
            return influence/samplingL;
        }
    }



    public static double get_rrsets(Network network, ArrayList<ArrayList<Integer>> rrsets, double size, DiffusionState diffusionState, Random rand)
    {
        //ArrayList<ArrayList<Integer>> re_neighbor;
        double t_set=0;
        for(int i=0;i<size;i++)
        {
            ArrayList<Integer> rrset=new ArrayList<Integer>();
            //long startTime = System.currentTimeMillis();

            //t_set=t_set+
            //seed.add((int) Math.round(Math.random()*network.vertexNum));
            //network.spread(seed, 1);

            //long endTime = System.currentTimeMillis();
            //long searchTime = endTime - startTime;
            //System.out.println("time "+searchTime*0.001);
            if(get_rrset(network,rrset,diffusionState, rand)==0)
                rrsets.add(rrset);
            if(i % 100000 ==0)
            {
                //System.out.println(i);
            }

        }
        return t_set;
        //System.out.println(size+ " rrsets generated.");
    }

    public static void simu_greedy_1(Network network, DiffusionState diffusionState, ArrayList<Integer> result, Random rand)
    {
        int c_index=-1;
        double c_profit=Double.MIN_VALUE;
        for(int i=0;i<network.vertexNum; i++)
        {

            DiffusionState temp=new DiffusionState(diffusionState);
            //System.out.println("simurest_times "+i);
            //System.out.print(diffusionState.aNum+" ");
            //System.out.println(temp.state[result.get(0)]);
            ArrayList<Integer> seedset=new ArrayList<Integer>();
            seedset.add(i);
            temp.seed(seedset);
            double t_profit=temp.exp_influence_complete(network, 1000, rand);
            if(t_profit>c_profit)
            {
                t_profit=c_profit;
                c_index=i;
            }
        }
        result.add(c_index);
    }
    public static double reverse_greedy_k_lazy(Network network, DiffusionState diffusionState, ArrayList<Integer> result, int k, Random rand)
    {
        //System.out.println("reverse_greedy_k_lazy starts");
        double profit=0;
        //int rr_size=100000;
        ArrayList<ArrayList<Integer>> rrsets=new ArrayList<ArrayList<Integer>>();
        get_rrsets(network, rrsets, rrsets_size, diffusionState, rand);
        //greedy rest
        //greedy_k(network, diffusionState, result, k);
        HashMap<Integer, ArrayList<Integer>> nodes_cover_sets = new HashMap<Integer, ArrayList<Integer>>();
        boolean[] nodes_cover_sets_key = new boolean[network.vertexNum];
        boolean[] coverred_rrsets=new boolean[rrsets.size()];
        for(int i=0; i<rrsets.size(); i++)
        {
            ArrayList<Integer> rrset_i = rrsets.get(i);
            for(int j=0;j<rrset_i.size();j++)
            {
                int index=rrset_i.get(j);
                if(nodes_cover_sets_key[index])
                {
                    nodes_cover_sets.get(index).add(i);
                }
                else
                {
                    ArrayList<Integer> temp=new ArrayList<Integer>();
                    temp.add(i);
                    nodes_cover_sets.put(index, temp);
                    nodes_cover_sets_key[index] = true;
                }
            }
            coverred_rrsets[i]=false;
        }

        Map<Integer, Integer> tempmap = new HashMap<>();
        for (HashMap.Entry<Integer, ArrayList<Integer>> entry : nodes_cover_sets.entrySet())
        {
            tempmap.put(entry.getKey(), entry.getValue().size());
            //System.out.println(entry.getKey()+" "+entry.getValue().size());
        }

        Map<Integer, Integer> sortedMap = tempmap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        MySortedMap mymap=new MySortedMap();
        for (HashMap.Entry<Integer, Integer> entry : sortedMap.entrySet())
        {
            mymap.push_back(entry.getKey(), entry.getValue());
            //System.out.println(entry.getKey()+" "+entry.getValue().size());
        }

        //--------------
        /*
        for(int i=0;i<mymap.size;i++)
        {
            int index=mymap.node_order.get(i);
            ArrayList<Integer> seedset=new ArrayList<Integer>(result);
            seedset.add(index);
            DiffusionState temp=new DiffusionState(diffusionState);
            temp.seed(seedset);

            System.out.println(index+" "+mymap.valueList.get(index)+" "+temp.exp_influence_complete(network, 1000));

        }*/
        //--------------
        //System.out.println("start selecting");
        int count=0;
        for(int i=0;i<k;i++)
        {
        	 //System.out.println(i+" i");
        	boolean sign=false;
            int c_bound=mymap.size;
            while(c_bound >0)
            {
                int c_seed=mymap.get(0);
                ///update
                ArrayList<Integer> c_seed_cover=nodes_cover_sets.get(c_seed);
                for(int j=0; j<c_seed_cover.size();j++)
                {
                    if(coverred_rrsets[c_seed_cover.get(j)])
                    {
                        c_seed_cover.remove((Integer) c_seed_cover.get(j));
                        j--;
                    }
                }

                int t_bound=mymap.update(c_seed, c_seed_cover.size());

                //System.out.println(t_bound+" "+c_bound+" "+count);
                if(t_bound==0)
                {
                    result.add(c_seed);
                    //System.out.println(c_seed+" "+mymap.valueList.get(c_seed));
                    sign=true;
                    for(int j=0; j<c_seed_cover.size();j++)
                    {
                        if(coverred_rrsets[c_seed_cover.get(j)])
                        {
                            throw new ArithmeticException("greedy update may wrong");
                        }
                        else
                        {
                            coverred_rrsets[c_seed_cover.get(j)]=true;
                        }

                    }
                    break;
                }
                if(t_bound<=c_bound-1)
                {
                    c_bound=t_bound;
                    count=0;
                }
                else
                {
                	count++;
                }

                //System.out.println("lazy greedy may wrong");
            }
            if(!sign)
            {
                throw new ArithmeticException("greedy lazy: no node selected");
            }
        }
        //System.out.println("reverse_greedy_k_lazy ends");
        //System.out.println();
        return profit*(network.vertexNum-diffusionState.aNum)/rrsets_size;
    }

    public static double reverse_greedy_k_lazy_time(Network network, DiffusionState diffusionState, ArrayList<Integer> result, int k, Random rand)
    {
        //System.out.println("reverse_greedy_k_lazy starts");
        double profit=0;
        //int rr_size=100000;
        ArrayList<ArrayList<Integer>> rrsets=new ArrayList<ArrayList<Integer>>();
        get_rrsets(network, rrsets, rrsets_size, diffusionState, rand);
        //greedy rest
        //greedy_k(network, diffusionState, result, k);
        HashMap<Integer, ArrayList<Integer>> nodes_cover_sets = new HashMap<Integer, ArrayList<Integer>>();
        boolean[] nodes_cover_sets_key = new boolean[network.vertexNum];
        boolean[] coverred_rrsets=new boolean[rrsets.size()];
        for(int i=0; i<rrsets.size(); i++)
        {
            ArrayList<Integer> rrset_i = rrsets.get(i);
            for(int j=0;j<rrset_i.size();j++)
            {
                int index=rrset_i.get(j);
                if(nodes_cover_sets_key[index])
                {
                    nodes_cover_sets.get(index).add(i);
                }
                else
                {
                    ArrayList<Integer> temp=new ArrayList<Integer>();
                    temp.add(i);
                    nodes_cover_sets.put(index, temp);
                    nodes_cover_sets_key[index] = true;
                }
            }
            coverred_rrsets[i]=false;
        }

        Map<Integer, Integer> tempmap = new HashMap<>();
        for (HashMap.Entry<Integer, ArrayList<Integer>> entry : nodes_cover_sets.entrySet())
        {
            tempmap.put(entry.getKey(), entry.getValue().size());
            //System.out.println(entry.getKey()+" "+entry.getValue().size());
        }

        Map<Integer, Integer> sortedMap = tempmap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        MySortedMap mymap=new MySortedMap();
        for (HashMap.Entry<Integer, Integer> entry : sortedMap.entrySet())
        {
            mymap.push_back(entry.getKey(), entry.getValue());
            //System.out.println(entry.getKey()+" "+entry.getValue().size());
        }



        for(int i=0;i<k;i++)
        {
            boolean sign=false;
            int c_bound=mymap.size;
            while(c_bound >0)
            {
                int c_seed=mymap.get(0);
                ///update
                ArrayList<Integer> c_seed_cover=nodes_cover_sets.get(c_seed);
                for(int j=0; j<c_seed_cover.size();j++)
                {
                    if(coverred_rrsets[c_seed_cover.get(j)])
                    {
                        c_seed_cover.remove((Integer) c_seed_cover.get(j));
                        j--;
                    }
                }
                int t_bound=mymap.update(c_seed, c_seed_cover.size());
                if(t_bound==0)
                {
                    result.add(c_seed);
                    //System.out.println(c_seed+" "+mymap.valueList.get(c_seed));
                    sign=true;
                    for(int j=0; j<c_seed_cover.size();j++)
                    {
                        if(coverred_rrsets[c_seed_cover.get(j)])
                        {
                            throw new ArithmeticException("greedy update may wrong");
                        }
                        else
                        {
                            coverred_rrsets[c_seed_cover.get(j)]=true;
                        }

                    }
                    break;
                }
                if(t_bound<=c_bound-1)
                {
                    c_bound=t_bound;
                }
                //System.out.println("lazy greedy may wrong");
            }
            if(!sign)
            {
                throw new ArithmeticException("greedy lazy: no node selected");
            }
        }
        //System.out.println("reverse_greedy_k_lazy ends");
        //System.out.println();
        return profit*(network.vertexNum-diffusionState.aNum)/rrsets_size;
    }

    public static double get_rrset(Network network ,ArrayList<Integer> rrset, DiffusionState diffusionState, Random rand)
    {

        int centerIndex;

        while(true)
        {
            centerIndex = (int)(Math.floor(rand.nextFloat()*network.vertexNum));
            if(!diffusionState.state[centerIndex])
            {
                break;
            }
        }

        //long startTime = System.currentTimeMillis();
        //System.out.println("centerIndex "+centerIndex);
        switch(network.type)
        {
            case "IC":
                return re_spreadOnce(network,centerIndex,rrset,diffusionState, rand);
            case "VIC":
                return re_spreadOnce(network,centerIndex,rrset,diffusionState, rand);
            case "WC":
                return re_spreadOnce(network,centerIndex,rrset,diffusionState, rand);
            case "LT":
                //re_spreadOnceLT(re_neighbor,centerIndex,rrset);
                return 0;
            default:
                System.out.print("Invalid model");
                return 0;
        }


    }

    public static int re_spreadOnce(Network network,int cindex, ArrayList<Integer> rrset, DiffusionState diffusionState, Random rand)
    {

        boolean[] state =diffusionState.state.clone();

        ArrayList<Integer> newActive =new ArrayList<Integer>();


        state[cindex]=true;
        rrset.add(cindex);
        newActive.add(cindex);

        int round=0;
        while(newActive.size()>0 && round<diffusionState.round_left)
        {
            if(re_spreadOneRound(network, newActive, state, rrset, diffusionState, rand)==1)
            {
                return 1;
            }
            round++;
        }
        return 0;
    }

    public static int re_spreadOneRound(Network network, ArrayList<Integer> newActive, boolean[] state, ArrayList<Integer> rrset, DiffusionState diffusionState, Random rand)
     {
             ArrayList<Integer> newActiveTemp=new ArrayList<Integer>();
             ArrayList<ArrayList<Integer>> re_neighbor=network.neighbor_reverse;

            for(int i=0;i<newActive.size();i++)
            {

                int cseed=newActive.get(i);
                ArrayList<Integer> cseed_neighbor=re_neighbor.get(cseed);

                for(int j=0;j<cseed_neighbor.size();j++)
                {
                    //a++;
                    int cseede=cseed_neighbor.get(j);

                    double prob=network.get_prob(cseede,cseed);

                    if(network.isSuccess(prob, rand))
                    {
                        newActiveTemp.add(cseede);
                    }
                }
            }

            newActive.clear();
            for(int i=0;i<newActiveTemp.size();i++)
            {
                int newActive_i = newActiveTemp.get(i);
                if(diffusionState.newActive.contains(newActive_i))
                {
                    return 1;
                }
                if(!state[newActive_i])
                {
                    newActive.add(newActive_i);
                    rrset.add(newActive_i);
                    state[newActive_i]=true;
                }
            }
            return 0;
     }
    /*
    public static class gmis1Callable implements Callable<Double>
    {
        private
        Network network;
        ArrayList<Integer> newActive;
        boolean[] state;
        int c_seed;

        public gmis1Callable(Network network, ArrayList<Integer> newActive, boolean[] state,int c_seed) {
        this.network = network;
        this.newActive = newActive;
        this.state = state;
        this.c_seed = c_seed;

        }
        public Double call() {
            return get_marginal_influence_spreadOnce(network,newActive,state,c_seed);
        }
    }
    public static void main(String[] args){
        // TODO Auto-generated method stub

    }*/
}

