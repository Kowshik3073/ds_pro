package cloudsim.ext.datacenter;

import cloudsim.VirtualMachine;

/**
 * Max-Min Load Balancer implementation.
 * 
 * The Max-Min algorithm is similar to Min-Min but prioritizes VMs with 
 * higher allocation counts to balance load more aggressively.
 * 
 * @author Implementation for CloudSim Load Balancing
 */
public class MaxMinVmLoadBalancer extends VmLoadBalancer {
	
	private DatacenterController dcbLocal;
	
	public MaxMinVmLoadBalancer(DatacenterController dcb) {
		super();
		dcbLocal = dcb;
	}
	
	@Override
	public int getNextAvailableVm() {
		// Max-Min: Find VM with MINIMUM allocation count (least loaded)
		// This is the "Min" part - selecting the minimum loaded VM
		// The "Max" part refers to task selection (largest task first), 
		// which happens in the scheduler, not here
		
		int minAllocationCount = Integer.MAX_VALUE;
		int selectedVmId = 0;
		
		for (int vmId = 0; vmId < dcbLocal.vmlist.size(); vmId++) {
			Integer allocationCount = vmAllocationCounts.get(vmId);
			if (allocationCount == null) {
				allocationCount = 0;
			}
			
			// Select VM with minimum allocation count
			// If tied, prefer lower VM ID
			if (allocationCount < minAllocationCount || 
			    (allocationCount == minAllocationCount && vmId < selectedVmId)) {
				minAllocationCount = allocationCount;
				selectedVmId = vmId;
			}
		}
		
		allocatedVm(selectedVmId);
		System.out.println("Max-Min: allocated to VM " + selectedVmId);
		
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
