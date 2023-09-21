package designpatterns;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * The Class OptimizationModel.
 * @author Lukas Wagner
 */
public class OptimizationModel {
	/** The nolimit. */
	static final double NOLIMIT = 9999;

	/** The Constant INPUT. */
	static final String INPUT = "Input";

	/** The Constant OUTPUT. */
	static final String OUTPUT = "Output";

	/** The Constant SOC. */
	static final String SOC = "SOC";

	/** The Constant POWER. */
	static final String POWER = "Power";

	/** The Constant BINARY. */
	static final String BINARY = "Binary";

	/** The Constant SEGMENT. */
	static final String SEGMENT = "Segment";

	/** The Constant STATE. */
	static final String STATE = "State";

	public static void main(String[] args) throws IloException {
		setOptimizationParameters();
		optimizationModel();
	}

	/**
	 * Sets the optimization parameters, primarily in ArrayList<ResourceParameters> resourceParameters.
	 */
	public static void setOptimizationParameters () {

		designpatterns.DesignPatterns.setOptimalityGap(0.001); // default 10e-4
		designpatterns.DesignPatterns.setTimeInterval(0.25); // 0.05 = 3Minutes, 0.125 = 7.5 Minutes
		designpatterns.DesignPatterns.setArrayLength(96); // set arrayLength in # of time steps

		// parameters
		// new object for resource to set parameters
		ResourceParameters resource1 = new  ResourceParameters();
		resource1.setName("Name");
		// ...
		designpatterns.DesignPatterns.getResourceParameters().add(resource1);


		// set parameters for additional energy resources
	}


	/**
	 * Optimization model
	 *
	 * @throws IloException the ilo exception
	 */
	public static double[][] optimizationModel () throws IloException {
		String nameOfModel = "baseModel-";
		try {
			//additional parameters for system
			double maxPowerSystem = 100; 
			double constantHydrogenDemand = 2*1000*designpatterns.DesignPatterns.getTimeInterval();

			//-------------------------------------------------------------------- Create Decision Variables --------------------------------------------------------------------
			designpatterns.DesignPatterns.creationOfDecisionVariables(maxPowerSystem);

			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				designpatterns.DesignPatterns.getCplex().addEq(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, POWER)[i], constantHydrogenDemand);
			}

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------
			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {}, // Decision variables Output Side Dependency
					new IloNumVar[][] {} // Decision variables Input Side dependency
					);

			designpatterns.DesignPatterns.generateRestrictiveDependency(
					new IloNumVar[][] {}, // Decision variables Output Side Dependency
					new IloNumVar[][] {} // Decision variables Input Side dependency
					);

			designpatterns.DesignPatterns.generateInputOutputRelationship("Name");
			designpatterns.DesignPatterns.generateInputOutputRelationship("Name");
			designpatterns.DesignPatterns.generateSystemStateSelectionByPowerLimits("Name");
			designpatterns.DesignPatterns.generateStateSequencesAndHoldingDuration("Name");
			designpatterns.DesignPatterns.generateRampLimits("Name", INPUT);
			
			designpatterns.DesignPatterns.generateEnergyBalanceForStorageSystem("Name");

			designpatterns.DesignPatterns.getCplex().exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = designpatterns.DesignPatterns.getCplex().linearNumExpr();
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				objective.addTerm(designpatterns.DesignPatterns.getTimeInterval()*designpatterns.DesignPatterns.getElectricityPrice()[i], designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)[i]);
			}
			designpatterns.DesignPatterns.getCplex().addMinimize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			designpatterns.DesignPatterns.getCplex().setParam(IloCplex.Param.MIP.Tolerances.MIPGap, designpatterns.DesignPatterns.getOptimalityGap());
			long start = System.currentTimeMillis();
			System.out.println("cplex solve");
			if (designpatterns.DesignPatterns.getCplex().solve()) {
				long end = System.currentTimeMillis();
				long solvingTime = 	(end - start);
				System.out.println("obj = "+designpatterns.DesignPatterns.getCplex().getObjValue());
				System.out.println(designpatterns.DesignPatterns.getCplex().getCplexStatus());

				int sizeOfResultsMatrix = designpatterns.DesignPatterns.getDecisionVariablesMatrix().size()*40+designpatterns.DesignPatterns.getDecisionVariablesVector().size(); 
				int counter = 0; 
				double [][] optimizationResults = new double [designpatterns.DesignPatterns.getArrayLength()+1][sizeOfResultsMatrix];
				// export any results to file
				//optimizationResults[i-1][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Name",Energycarrier,type)[i-1]
				// counter++;
				String headerOptimizationResults = ""; 
				
				designpatterns.DesignPatterns.writeResultsToFile(optimizationResults, "designpatterns_from-method"+nameOfModel, headerOptimizationResults);
				return optimizationResults;
			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
			
		}
		finally {
			if (designpatterns.DesignPatterns.getCplex()!=null)  {
				designpatterns.DesignPatterns.getCplex().close();
				designpatterns.DesignPatterns.globalCplex=null;
			}
		}
		return null;
	}
}
