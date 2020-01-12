package adaptive_time;

import java.util.ArrayList;

public class Result
{
	public ArrayList<Double> exp_influence_result;
	public ArrayList<Double> var_influence_result;
	public ArrayList<Double> exp_budget_result;
	public ArrayList<Double> var_budget_result;
	public double computing_time=0.0;
	public double computing_num=0.0;
	
	Result()
	{
		exp_influence_result=new ArrayList<Double>();
    	var_influence_result=new ArrayList<Double>();
    	exp_budget_result=new ArrayList<Double>();
    	var_budget_result=new ArrayList<Double>();
	}
	
	void clear()
	{
		exp_influence_result.clear();
		var_influence_result.clear();
		exp_budget_result.clear();
		var_budget_result.clear();
		computing_time=0.0;
		computing_num=0.0;
	}
	
	void print(int round)
	{
		System.out.println("Influence result-------------------------------------------");
		Tools.printdoublelistln(exp_influence_result, round);
		Tools.printdoublelistln(var_influence_result, round);
		
		System.out.println("budget result-------------------------------------------");
		Tools.printdoublelistln(exp_budget_result, round);
		Tools.printdoublelistln(var_budget_result, round);
	}
}
