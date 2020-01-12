package adaptive_time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tools {
    static NumberFormat formatter = new DecimalFormat("#0.00");
    //System.out.println(formatter.format(4.0));
    public static void printlistln(ArrayList<Integer> list)
    {
        for(int i=0;i<list.size();i++)
        {
            System.out.print(list.get(i)+" ");
        }
        System.out.println();
    }

    public static void printdoublelistln(ArrayList<Double> list)
    {
        for(int i=0;i<list.size();i++)
        {
            if(i>0 && (list.get(i)-list.get(i-1))<0.01)
            {
                System.out.print(formatter.format(list.get(i))+" ");
                System.out.println();
                return;
            }
            else
            {
                System.out.print(formatter.format(list.get(i))+" ");
            }

        }
        System.out.println();
    }

    public static void printdoublelistln(ArrayList<Double> list, int size)
    {
        if(size>list.size())
        {
            throw new ArithmeticException("printdoublelistln size>list.size()");

        }
        for(int i=0;i<size;i++)
        {

            if(i<list.size())
            {
                System.out.print(formatter.format(list.get(i))+" ");
            }


        }
        System.out.println();
    }

    public static void printintlistln(ArrayList<Integer> list, int size)
    {
        if(size>list.size())
        {
            throw new ArithmeticException("printintlistln size>list.size()");

        }
        for(int i=0;i<size;i++)
        {

            if(i<list.size())
            {
                System.out.print(list.get(i)+" ");
            }


        }
        System.out.println();
    }

    public static void printElapsedTime(long startTime, long endTime){
        long execTime = endTime - startTime;
        System.out.printf(">>> elapsed time: %02d:%02d:%02d.%03d\n", execTime/3600000, execTime/60000%60, execTime/1000%60, execTime%1000);
    }

    public static void printElapsedTime(long startTime, long endTime, String name){
        long execTime = endTime - startTime;
        System.out.printf(">>> %s elapsed time: %02d:%02d:%02d.%03d\n", name, execTime/3600000, execTime/60000%60, execTime/1000%60, execTime%1000);
    }
    
    public static double sqr(double input)
    {
    	return input*input;
    }
    
    public static void reddit_to_graph() throws FileNotFoundException, UnsupportedEncodingException
    {
    	String path="data/reddit_raw.txt";
    	File singleFile=new File(path);
        String inString = "";
        int node_1, node_2;
        int threadindex=0;
        int vnum=0;
        int vnum2=0;
        double weightnum=0;
        double edgenum=0;
        int threadnum=1000;
        int topreply=1000;
        int neighbornum=5;
        HashMap<Integer,  HashMap<Integer, Integer> > neighbor=new HashMap<Integer,  HashMap<Integer, Integer> >();
        HashMap<Integer, Integer > node_degree=new HashMap<Integer,  Integer>();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(singleFile));
            while((inString = reader.readLine())!= null && threadindex<threadnum){
                //System.out.println(inString);
                //System.out.println(inString);
                String[] tempString = null;
                tempString=inString.split(" ");
                ArrayList<Integer> nodes=new  ArrayList<Integer>();
                for(int i=0;i<tempString.length && i<topreply;i++)
                {
                	int node=Integer.parseInt(tempString[i]);
                	nodes.add(node);
                	if(!node_degree.containsKey(node))
                    {
                		//neighbor.put(node, new  HashMap<Integer,Integer> ());
                		//vnum++;
                		//System.out.println(vnum);
                		node_degree.put(node, 1);
                    }
                	else
                	{
                		int cdegree=node_degree.get(node);
                		node_degree.put(node, cdegree+1);
                		//System.out.println("****************");
                	}
                	
                }
                
               // System.out.println(weightnum);
                threadindex++;
                //probability.get(node_1).add(prob);
                //probability_reverse.get(node_2).add(prob);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(path+" The path of data is incorrect.");
        } catch (IOException ex) {
            System.out.println("Error in reading data.");
        }
        
        int index=0;
        HashMap<Integer, Integer >  nodeindex=new HashMap<Integer, Integer >();
        for (Map.Entry<Integer , Integer> entry : node_degree.entrySet())  
        {
        	if( entry.getValue()>1)
        	{
        		vnum2++;
        		neighbor.put(entry.getKey(), new  HashMap<Integer,Integer> ());
        		nodeindex.put(entry.getKey(), index);
        		index++;
        		System.out.println("vnum2 "+vnum2);
        	}
        		
        }
        
        threadindex=0;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(singleFile));
            while((inString = reader.readLine())!= null && threadindex<threadnum){
                //System.out.println(inString);
                //System.out.println(inString);
                String[] tempString = null;
                tempString=inString.split(" ");
                ArrayList<Integer> nodes=new  ArrayList<Integer>();
                for(int i=0;i<tempString.length && i<topreply;i++)
                {
                	int node=Integer.parseInt(tempString[i]);
                	nodes.add(node);
                	
                }
                
                for(int i=0;i<nodes.size();i++)
                {
                	for(int j=0;j<nodes.size();j++)
                    {
                		if(j!=i && node_degree.get(nodes.get(i))>1 && node_degree.get(nodes.get(j))>1)
                		{
                			if(neighbor.get(nodes.get(i)).containsKey(nodes.get(j)))
                			{
                				int c_weight=neighbor.get(nodes.get(i)).get(nodes.get(j));
                				neighbor.get(nodes.get(i)).put(nodes.get(j), c_weight+1);
                				
                			}
                			else
                			{
                				
                				if(neighbor.get(nodes.get(i)).size()<neighbornum)
            					{
                					neighbor.get(nodes.get(i)).put(nodes.get(j), 1);
                					edgenum++;
            					}
            				
                			}
                		}
                			
                    	
                    }
                	
                }
                
                System.out.println("edgenum "+edgenum);
                threadindex++;
                //probability.get(node_1).add(prob);
                //probability_reverse.get(node_2).add(prob);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(path+" The path of data is incorrect.");
        } catch (IOException ex) {
            System.out.println("Error in reading data.");
        }
            
        PrintWriter writer = new PrintWriter("data/reddit.txt", "UTF-8");	   
        for (Map.Entry<Integer ,  HashMap<Integer, Integer>> entry : neighbor.entrySet())  
        {
        	for (Map.Entry<Integer ,   Integer> entry2 : entry.getValue().entrySet())  
            {
            	if(entry2.getValue()>10)
            	{
            		writer.println(nodeindex.get(entry.getKey())+" "+nodeindex.get(entry2.getKey())+" "+0.1);
            	}
            	else if(entry2.getValue()>1)
            	{
            		writer.println(nodeindex.get(entry.getKey())+" "+nodeindex.get(entry2.getKey())+" "+0.05);
            	}
            	else
            	{
            		writer.println(nodeindex.get(entry.getKey())+" "+nodeindex.get(entry2.getKey())+" "+0.01);
            	}
            }
        		
        }
    }
    
    public static void live_trans() throws FileNotFoundException, UnsupportedEncodingException
    {
    	String path="data/live.txt";
    	int vnum=39977962;
    	File singleFile=new File(path);
        String inString = "";
        int node_1, node_2;
        
        PrintWriter writer = new PrintWriter("data/live_new.txt", "UTF-8");	
        
        int linenum=1;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(singleFile));
            while((inString = reader.readLine())!= null ){
                System.out.println(inString);
            	if(linenum>4)
            	{
                    String[] tempString = null;
                    tempString=inString.split("\t");
                    
                    node_1=Integer.parseInt(tempString[0]);
                    node_2=Integer.parseInt(tempString[1]);
                    System.out.println();
                    writer.println(node_1+" "+node_2);
            	}
                
               
                linenum++;
                //probability.get(node_1).add(prob);
                //probability_reverse.get(node_2).add(prob);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(path+" The path of data is incorrect.");
        } catch (IOException ex) {
            System.out.println("Error in reading data.");
        }
        System.out.println("reading done");
    }
    
    
    public static void wc_dis_print(String path, int vertexNum,String type, int bin_num)
    {
        //System.out.println("Fix "+prob);
    	int[] in_degree=new int[vertexNum];
        for (int i=0;i<vertexNum;i++)
        {
        	in_degree[i]=0;
        }

        File singleFile=new File(path);
        String inString = "";
        int node_1, node_2;
        int edgeindex=0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(singleFile));
            while((inString = reader.readLine())!= null){
                //System.out.println(inString);
            	edgeindex++;
            	if(edgeindex % 1000000 ==0)
            	{
            		System.out.println(edgeindex+" "+inString);
            	}
                
                String[] tempString = null;
                tempString=inString.split(" ");
                
                switch(type)
                {
                    case "VIC":

                        break;

                    default:
                    	
                		 node_1=Integer.parseInt(tempString[0]);
                         node_2=Integer.parseInt(tempString[1]);
                         in_degree[node_2]++;

                       
                }

                //probability.get(node_1).add(prob);
                //probability_reverse.get(node_2).add(prob);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(path+" The path of data is incorrect.");
        } catch (IOException ex) {
            System.out.println("Error in reading data.");
        }
        System.out.println("reading done");
        
        int[] probnum=new int[bin_num]; 
    	for(int i=0;i<bin_num;i++)
    	{
    		probnum[i]=0;
    	}
    	
    	double unit=1.0/bin_num;
    	int edgenum=0;
    	for(int i=0;i<vertexNum;i++)
        {
    		
    		for(int j=0;j<in_degree[i];j++)
    		{

    			int index=(int) ((1.0/in_degree[i])/unit);
            	//System.out.println(index);
            	if(index>=0 && index<bin_num)
            	{
            		probnum[index]=probnum[index]+1;
            		edgenum++;
            	}
            	else
            	{
            		//System.out.println(index+" "+bin_num);
            		//throw new ArithmeticException("print_prob_distribution out of range"); 
            	}
    		}
        }
    	double[] probculmunum=new double[bin_num]; 
    	probculmunum[0]=probnum[0];
    	for(int i=1;i<bin_num;i++)
    	{
    		probculmunum[i]=probculmunum[i-1]+probnum[i];
    	}
    	for(int i=0;i<bin_num;i++)
    	{
    		System.out.println(unit*i+" "+probculmunum[i]/edgenum);
    	}
    }
    
    
    
    public static void vic_dis_print(String path, int bin_num)
    {
        //System.out.println("Fix "+prob);

        File singleFile=new File(path);
        String inString = "";
        int node_1, node_2;
        int edgeindex=0;
        
        int[] probnum=new int[bin_num]; 
    	for(int i=0;i<bin_num;i++)
    	{
    		probnum[i]=0;
    	}
    	int edgenum=0;
    	double unit=1.0/bin_num;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(singleFile));
            while((inString = reader.readLine())!= null){
                //System.out.println(inString);
            	edgeindex++;
            	if(edgeindex % 1000000 ==0)
            	{
            		System.out.println(edgeindex+" "+inString);
            	}
                
                String[] tempString = null;
                tempString=inString.split(" ");
                
                int index=(int) (Double.parseDouble(tempString[2])/unit);
            	//System.out.println(index);
            	if(index>=0 && index<bin_num)
            	{
            		probnum[index]=probnum[index]+1;
            		edgenum++;
            	}
            	else
            	{
            		//System.out.println(index+" "+bin_num);
            		//throw new ArithmeticException("print_prob_distribution out of range"); 
            	}

                //probability.get(node_1).add(prob);
                //probability_reverse.get(node_2).add(prob);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(path+" The path of data is incorrect.");
        } catch (IOException ex) {
            System.out.println("Error in reading data.");
        }
        System.out.println("reading done");
        
    	double[] probculmunum=new double[bin_num]; 
    	probculmunum[0]=probnum[0];
    	for(int i=1;i<bin_num;i++)
    	{
    		probculmunum[i]=probculmunum[i-1]+probnum[i];
    	}
    	for(int i=0;i<bin_num;i++)
    	{
    		System.out.println(unit*i+" "+probculmunum[i]/edgenum);
    	}
    }
    
    public static void vic_to_cons(String path, double prob)
    {
        //System.out.println("Fix "+prob);

        File singleFile=new File(path);
        String inString = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(singleFile));
            while((inString = reader.readLine())!= null){
                //System.out.println(inString);
            	edgeindex++;
            	if(edgeindex % 1000000 ==0)
            	{
            		System.out.println(edgeindex+" "+inString);
            	}
                
                String[] tempString = null;
                tempString=inString.split(" ");
                
                int index=(int) (Double.parseDouble(tempString[2])/unit);
            	//System.out.println(index);
            	if(index>=0 && index<bin_num)
            	{
            		probnum[index]=probnum[index]+1;
            		edgenum++;
            	}
            	else
            	{
            		//System.out.println(index+" "+bin_num);
            		//throw new ArithmeticException("print_prob_distribution out of range"); 
            	}

                //probability.get(node_1).add(prob);
                //probability_reverse.get(node_2).add(prob);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(path+" The path of data is incorrect.");
        } catch (IOException ex) {
            System.out.println("Error in reading data.");
        }
        System.out.println("reading done");
        
    	double[] probculmunum=new double[bin_num]; 
    	probculmunum[0]=probnum[0];
    	for(int i=1;i<bin_num;i++)
    	{
    		probculmunum[i]=probculmunum[i-1]+probnum[i];
    	}
    	for(int i=0;i<bin_num;i++)
    	{
    		System.out.println(unit*i+" "+probculmunum[i]/edgenum);
    	}
    }
 
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        // TODO Auto-generated method stub
    	//live_trans();
    	String path="data/higgs_100.txt";
    	Tools.vic_dis_print(path, 1000);
    	
//    	String path="data/live_new.txt";
//    	int vnum=39977962;
//    	Tools.wc_dis_print(path, vnum, "WC", 1000);
    }
    
}