package cloudsim.ext.datacenter;

import java.util.Random;
import cloudsim.VirtualMachine;

public class AntColonyVmLoadBalancer extends VmLoadBalancer {
	
	private double[][] pheromones;
	static final double alpha = 1.0;
	static final double beta = 2.0;
	static final double ONE_UNIT_PHEROMONE = 1.0;
	static final double EVAPORATION_FACTOR = 0.5; // Changed from 2 to 0.5 (multiply, not divide)
	private final int NUM_ANTS = 5; // Reduced from 10 for performance

	Ant[] ants;
	DatacenterController dcbLocal;
	int counter = 0;

	public AntColonyVmLoadBalancer(DatacenterController dcb) {
		super();
		dcbLocal = dcb;
	}

	@Override
	public int getNextAvailableVm() {
		if(counter == 0) {
			pheromones = new double[dcbLocal.vmlist.size()][dcbLocal.vmlist.size()];
			// Initialize pheromones based on VM capacity (higher capacity = more initial pheromone)
			for (int i = 0; i < pheromones.length; i++) {
				for (int j = 0; j < pheromones.length; j++) {
					double vmCapacity = getVmCapacity(j);
					pheromones[i][j] = vmCapacity / 1000.0; // Initialize based on capacity
				}
			}
			counter++;
			ants = new Ant[NUM_ANTS];
			for (int i = 0; i < ants.length; i++) {
				ants[i] = new Ant(pheromones);
			}
		}
		
		// Find VM with best score (pheromone + capacity - load)
		int bestVm = 0;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		for (int vmId = 0; vmId < dcbLocal.vmlist.size(); vmId++) {
			Integer allocationCount = vmAllocationCounts.get(vmId);
			if (allocationCount == null) {
				allocationCount = 0;
			}
			
			// Calculate pheromone strength for this VM
			double pheromoneScore = 0.0;
			for (int j = 0; j < pheromones.length; j++) {
				pheromoneScore += pheromones[j][vmId];
			}
			
			double vmCapacity = getVmCapacity(vmId);
			double loadPenalty = allocationCount * 0.5; // Penalize heavily loaded VMs
			
			// ACO score: high pheromone + high capacity - load penalty
			double score = (pheromoneScore * alpha * 2.0) + (vmCapacity * beta * 3.0) - loadPenalty;
			
			if (score > bestScore) {
				bestScore = score;
				bestVm = vmId;
			}
		}
		
		// Strongly reinforce pheromones for selected VM (faster learning)
		for (int i = 0; i < pheromones.length; i++) {
			pheromones[i][bestVm] = pheromones[i][bestVm] * (1.0 - EVAPORATION_FACTOR) + (ONE_UNIT_PHEROMONE * 5.0);
		}
		
		allocatedVm(bestVm);
		return bestVm;
	}

	private double getVmCapacity(int vmId) {
		if (vmId < 0 || vmId >= dcbLocal.vmlist.size()) {
			return 1.0;
		}
		
		VirtualMachine vm = (VirtualMachine) dcbLocal.vmlist.get(vmId);
		int cpus = vm.getCpus();
		long bw = vm.getBw();
		
		// Estimate capacity based on CPUs and bandwidth
		return cpus * 1000.0 + (bw / 1000.0);
	}

	public void Evaporation() {
		for (int i = 0; i < pheromones.length; i++) {
			for (int j = 0; j < pheromones.length; j++) {
				pheromones[i][j] *= EVAPORATION_FACTOR;
				// Ensure minimum pheromone level
				if (pheromones[i][j] < 0.01) {
					pheromones[i][j] = 0.01;
				}
			}
		}
	}

	public class Ant {
		private int fakeVmId;

		public Ant(double[][] ph) {
			fakeVmId = ph.length - 1;
		}

		public int SendAnt() {
			return ProcessAnt(true);
		}

		public int FetchFinalVm() {
			return ProcessAnt(false);
		}

		public int ProcessAnt(boolean updatePheromones) {
			// Simplified: just select a random VM with pheromone bias
			int vmId = selectVmByPheromone();
			
			if (updatePheromones && vmId >= 0 && vmId < pheromones.length) {
				UpdatePheromone(0, vmId);
			}
			
			return vmId;
		}

		private int selectVmByPheromone() {
			double[] probability = new double[pheromones.length];
			double sum = 0.0;
			
			for (int i = 0; i < probability.length; i++) {
				double pheromoneSum = 0.0;
				for (int j = 0; j < pheromones.length; j++) {
					pheromoneSum += pheromones[j][i];
				}
				probability[i] = Math.pow(pheromoneSum, alpha);
				sum += probability[i];
			}
			
			// Normalize
			if (sum > 0) {
				for (int i = 0; i < probability.length; i++) {
					probability[i] /= sum;
				}
			}
			
			// Select based on probability
			Random rand = new Random();
			double randomValue = rand.nextDouble();
			double cumulative = 0.0;
			
			for (int i = 0; i < probability.length; i++) {
				cumulative += probability[i];
				if (randomValue <= cumulative) {
					return i;
				}
			}
			
			return 0; // Fallback
		}

		public void UpdatePheromone(int prevId, int newId) {
			if (prevId >= 0 && prevId < pheromones.length && 
			    newId >= 0 && newId < pheromones.length) {
				pheromones[prevId][newId] += ONE_UNIT_PHEROMONE;
			}
		}
	}
}
