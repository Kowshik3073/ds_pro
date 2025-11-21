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
		// Find VM with maximum allocation that still has good MIPS
		// This prioritizes already-busy VMs for aggressive load balancing
		double maxScore = -1;
		int selectedVmId = 0;
		
		for (int vmId = 0; vmId < dcbLocal.vmlist.size(); vmId++) {
			Integer allocationCount = vmAllocationCounts.get(vmId);
			if (allocationCount == null) {
				allocationCount = 0;
			}
			
			double vmMips = getVmMips(vmId);
			
			// Score = allocation count * MIPS (favor busy but fast VMs)
			// If no allocations, give it lowest VM ID
			double score;
			if (allocationCount == 0) {
				score = vmId; // Prefer lowest VM ID for new allocations
			} else {
				score = allocationCount * vmMips;
			}
			
			if (score > maxScore || (score == maxScore && vmId < selectedVmId)) {
				maxScore = score;
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
