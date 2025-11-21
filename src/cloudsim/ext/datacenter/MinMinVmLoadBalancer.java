package cloudsim.ext.datacenter;

import cloudsim.VirtualMachine;
import cloudsim.ext.InternetCloudlet;

/**
 * Min-Min Load Balancer implementation.
 * 
 * The Min-Min algorithm selects the VM with the minimum Estimated Time to 
 * Complete (ETC) for the current cloudlet and assigns it to that VM.
 * 
 * @author Implementation for CloudSim Load Balancing
 */
public class MinMinVmLoadBalancer extends VmLoadBalancer {
	
	private DatacenterController dcbLocal;
	private InternetCloudlet currentCloudlet;
	
	public MinMinVmLoadBalancer(DatacenterController dcb) {
		super();
		dcbLocal = dcb;
	}
	
	@Override
	public int getNextAvailableVm() {
		// Find VM with minimum ETC for load balancing
		double minETC = Double.MAX_VALUE;
		int selectedVmId = 0;
		
		for (int vmId = 0; vmId < dcbLocal.vmlist.size(); vmId++) {
			// Find VM with lowest allocation count (approximate load)
			Integer allocationCount = vmAllocationCounts.get(vmId);
			if (allocationCount == null) {
				allocationCount = 0;
			}
			
			// Simple ETC-like metric: allocation count / MIPS
			double vmMips = getVmMips(vmId);
			double loadMetric = allocationCount / vmMips;
			
			if (loadMetric < minETC) {
				minETC = loadMetric;
				selectedVmId = vmId;
			}
		}
		
		allocatedVm(selectedVmId);
		System.out.println("Min-Min: allocated to VM " + selectedVmId);
		
		return selectedVmId;
	}
	
	/**
	 * Get the MIPS (Million Instructions Per Second) of a VM
	 * 
	 * @param vmId The VM ID
	 * @return VM MIPS value
	 */
	private double getVmMips(int vmId) {
		if (vmId < 0 || vmId >= dcbLocal.vmlist.size()) {
			return 1000.0;
		}
		
		VirtualMachine vm = (VirtualMachine) dcbLocal.vmlist.get(vmId);
		int cpus = vm.getCpus();
		return cpus > 0 ? cpus * 1000.0 : 1000.0;
	}
}
