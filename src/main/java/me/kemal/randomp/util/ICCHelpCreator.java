package me.kemal.randomp.util;

import dan200.computercraft.api.peripheral.IPeripheral;

/***
 * 
 * Implement this to automaticly generate a computercraft help
 * function function
 * @author Kemal 
 *
 */
public interface ICCHelpCreator {
	/***
	 * Example: return new String[] {"parameter1,parameter2","parameter1"}; This
	 * will say that function one has to parameters, function two has one
	 * parameter
	 * 
	 * @return The arguments for each function
	 */
	public String[] getMethodValues();

	/***
	 * Example: return new String[] {"This function does nothing"} With this the
	 * description of function one is This function does nothing
	 * 
	 * @return The description of each function
	 */
	public String[] getFunctionDescriptions();
	
	/***
	 * 
	 * @return An description about what the function returns
	 */
	public String[] getFunctionReturns();
}
