package designpatterns;

/**
 * The Class SystemState.
 */
public class SystemState {
	
	/** The state ID. */
	int stateID; 
	
	/** The state name. */
	String stateName; 
	
	/** The max state duration. */
	double maxStateDuration; 
	
	/** The min state duration. */
	double minStateDuration; 
	
	/** The follower states. */
	int[] followerStates;
	
	/** The latency. */
	int latency; 
	
	/** The min power. */
	double minPower; 
	
	/** The max power. */
	double maxPower; 
	
	/** The max power output. */
	double maxPowerOutput = Double.MAX_VALUE;
	
	/** The min ramp input. */
	double minRampInput;
	
	/** The max ramp input. */
	double maxRampInput = Double.MAX_VALUE;

/** The min ramp output. */
//	double maxRamp = 90;
	double minRampOutput;
	
	/** The max ramp output. */
	double maxRampOutput = Double.MAX_VALUE;
	
	/**
	 * Instantiates a new system state.
	 *
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @param d the d
	 */
	public SystemState(int a, double b, double c, int[] d) {
		stateID = a;
		maxStateDuration = b; 
		minStateDuration = c; 
		followerStates = d; 
	}
	
	/**
	 * Instantiates a new system state.
	 */
	public SystemState() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gets the state ID.
	 *
	 * @return the state ID
	 */
	public int getStateID() {
		return stateID;
	}
	
	/**
	 * Sets the state ID.
	 *
	 * @param stateID the new state ID
	 */
	public void setStateID(int stateID) {
		this.stateID = stateID;
	}
	
	/**
	 * Gets the max state duration.
	 *
	 * @return the max state duration
	 */
	public double getMaxStateDuration() {
		return maxStateDuration;
	}
	
	/**
	 * Sets the max state duration.
	 *
	 * @param maxStateDuration the new max state duration
	 */
	public void setMaxStateDuration(double maxStateDuration) {
		this.maxStateDuration = maxStateDuration;
	}
	
	/**
	 * Gets the min state duration.
	 *
	 * @return the min state duration
	 */
	public double getMinStateDuration() {
		return minStateDuration;
	}
	
	/**
	 * Sets the min state duration.
	 *
	 * @param minStateDuration the new min state duration
	 */
	public void setMinStateDuration(double minStateDuration) {
		this.minStateDuration = minStateDuration;
	}
	
	/**
	 * Gets the follower states.
	 *
	 * @return the follower states
	 */
	public int[] getFollowerStates() {
		return followerStates;
	}
	
	/**
	 * Sets the follower states.
	 *
	 * @param followerStates the new follower states
	 */
	public void setFollowerStates(int[] followerStates) {
		this.followerStates = followerStates;
	}

	/**
	 * Gets the state name.
	 *
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}

	/**
	 * Sets the state name.
	 *
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	/**
	 * Gets the min power.
	 *
	 * @return the minPower
	 */
	public double getMinPower() {
		return minPower;
	}

	/**
	 * Sets the min power.
	 *
	 * @param minPower the minPower to set
	 */
	public void setMinPower(double minPower) {
		this.minPower = minPower;
	}

	/**
	 * Gets the max power.
	 *
	 * @return the maxPower
	 */
	public double getMaxPower() {
		return maxPower;
	}

	/**
	 * Sets the max power.
	 *
	 * @param maxPower the maxPower to set
	 */
	public void setMaxPower(double maxPower) {
		this.maxPower = maxPower;
	}

	/**
	 * Gets the max power output.
	 *
	 * @return the maxPowerOutput
	 */
	public double getMaxPowerOutput() {
		return maxPowerOutput;
	}

	/**
	 * Sets the max power output.
	 *
	 * @param maxPowerOutput the maxPowerOutput to set
	 */
	public void setMaxPowerOutput(double maxPowerOutput) {
		this.maxPowerOutput = maxPowerOutput;
	}

	/**
	 * Gets the min ramp input.
	 *
	 * @return the minRamp
	 */
	public double getMinRampInput() {
		return minRampInput;
	}

	/**
	 * Sets the min ramp input.
	 *
	 * @param minRamp the minRamp to set
	 */
	public void setMinRampInput(double minRamp) {
		this.minRampInput = minRamp;
	}

	/**
	 * Gets the max ramp input.
	 *
	 * @return the maxRamp
	 */
	public double getMaxRampInput() {
		return maxRampInput;
	}

	/**
	 * Sets the max ramp input.
	 *
	 * @param maxRamp the maxRamp to set
	 */
	public void setMaxRampInput(double maxRamp) {
		this.maxRampInput = maxRamp;
	}

	/**
	 * Gets the min ramp output.
	 *
	 * @return the minRampOutput
	 */
	public double getMinRampOutput() {
		return minRampOutput;
	}

	/**
	 * Sets the min ramp output.
	 *
	 * @param minRampOutput the minRampOutput to set
	 */
	public void setMinRampOutput(double minRampOutput) {
		this.minRampOutput = minRampOutput;
	}

	/**
	 * Gets the max ramp output.
	 *
	 * @return the maxRampOutput
	 */
	public double getMaxRampOutput() {
		return maxRampOutput;
	}

	/**
	 * Sets the max ramp output.
	 *
	 * @param maxRampOutput the maxRampOutput to set
	 */
	public void setMaxRampOutput(double maxRampOutput) {
		this.maxRampOutput = maxRampOutput;
	}

	/**
	 * @return the latency
	 */
	public int getLatency() {
		return latency;
	}

	/**
	 * @param latency the latency to set
	 */
	public void setLatency(int latency) {
		this.latency = latency;
	}

}
