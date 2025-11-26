# How to Run Load Balancing Simulations

## Prerequisites

- Java JDK installed
- CloudSim libraries in `jars/` directory
- Source code compiled

---

## Quick Start

### Step 1: Compile the Project

```powershell
javac -cp "jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" -d bin -sourcepath src src/cloudsim/ext/HeadlessRunner.java src/cloudsim/ext/Simulation.java
```

### Step 2: Run a Simulation

```powershell
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "<ALGORITHM>" default
```

Replace `<ALGORITHM>` with one of the supported algorithms (see below).

---

## Supported Algorithms

### 1. Min-Min Scheduling
```powershell
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "Min-Min Scheduling" default
```

### 2. Max-Min Scheduling
```powershell
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "Max-Min Scheduling" default
```

### 3. Honey Bee Foraging
```powershell
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "Honey Bee Foraging loadbalancer" default
```

### 4. Ant Colony Optimization
```powershell
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "Ant Colony LB" default
```

---

## Save Output to File

To save simulation results to a file:

```powershell
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "Honey Bee Foraging loadbalancer" default | Out-File -Encoding utf8 results_hbf.txt
```

---

## Run All Algorithms

To run all four algorithms and save results:

```powershell
# Min-Min
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "Min-Min Scheduling" default | Out-File -Encoding utf8 results_minmin.txt

# Max-Min
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "Max-Min Scheduling" default | Out-File -Encoding utf8 results_maxmin.txt

# Honey Bee Foraging
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "Honey Bee Foraging loadbalancer" default | Out-File -Encoding utf8 results_hbf.txt

# Ant Colony Optimization
java -cp "bin;jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" cloudsim.ext.HeadlessRunner "Ant Colony LB" default | Out-File -Encoding utf8 results_aco.txt
```

---

## Understanding the Output

### VM Allocation
Shows how many cloudlets were assigned to each VM:
```
VM Allocation (DC1):
VM0 -> 3546
VM1 -> 1898
...
```

### Response Time
Average, minimum, and maximum response times for each user base:
```
Response Time (UB1):
Avg = 51.48 ms
Min = 40.05 ms
Max = 62.10 ms
```

### Processing Time
Average and maximum processing times for each data center:
```
Processing Time (DC1):
Avg = 0.87 ms
Max = 1.60 ms
```

### Cost
Total cost incurred by each data center:
```
Cost:
DC1 Total = $33.91
DC2 Total = $21.30
DC3 Total = $11.97
```

---

## Troubleshooting

### Compilation Errors

If you get compilation errors, ensure all source files are compiled:

```powershell
javac -cp "jars/gridsim.jar;jars/simjava2.jar;jars/iText-2.1.5.jar;jars/cloudanalyst.jar" -d bin -sourcepath src src/cloudsim/ext/*.java src/cloudsim/ext/datacenter/*.java src/cloudsim/ext/gui/*.java
```

### Runtime Errors

1. **ClassNotFoundException:** Check that all JAR files are in the `jars/` directory
2. **NullPointerException:** Ensure the algorithm name is spelled exactly as shown above
3. **OutOfMemoryError:** Increase Java heap size: `java -Xmx2048m -cp ...`

---

## Modifying the Scenario

To modify the simulation scenario, edit:
```
src/cloudsim/ext/HeadlessRunner.java
```

Look for the `setupScenarioDefault()` method to change:
- Number of data centers
- Number of VMs per data center
- Host specifications (MIPS, CPUs)
- User base configurations
- Simulation duration

After modifications, recompile the project.

---

## Performance Tips

1. **Faster Compilation:** Only recompile changed files
2. **Reduce Output:** Comment out debug print statements
3. **Shorter Simulations:** Reduce simulation time for testing
4. **Parallel Runs:** Run different algorithms in separate terminal windows

---

## Expected Runtime

- **Min-Min:** ~30-45 seconds
- **Max-Min:** ~30-45 seconds
- **Honey Bee Foraging:** ~45-60 seconds
- **Ant Colony:** ~45-60 seconds

*Times may vary based on system performance*

---

## Project Structure

```
Load-Balancing-Algorithms-CloudSim/
├── bin/                          # Compiled classes
├── jars/                         # CloudSim libraries
│   ├── gridsim.jar
│   ├── simjava2.jar
│   ├── iText-2.1.5.jar
│   └── cloudanalyst.jar
├── src/                          # Source code
│   └── cloudsim/
│       ├── ext/
│       │   ├── HeadlessRunner.java
│       │   ├── Simulation.java
│       │   └── datacenter/
│       │       ├── MinMinVmLoadBalancer.java
│       │       ├── MaxMinVmLoadBalancer.java
│       │       ├── HoneyBeeForagingVmLoadBalancer.java
│       │       └── AntColonyVmLoadBalancer.java
│       └── ...
├── RESULTS.md                    # Simulation results
├── EXPERIMENTAL_SETUP.md         # Setup documentation
└── INSTRUCTIONS.md               # This file
```

---

## Additional Resources

- **CloudSim Documentation:** http://www.cloudbus.org/cloudsim/
- **Results Analysis:** See `RESULTS.md`
- **Experimental Setup:** See `EXPERIMENTAL_SETUP.md`

---

## Contact & Support

For issues or questions about this simulation:
1. Check the troubleshooting section above
2. Review the CloudSim documentation
3. Verify all prerequisites are installed correctly

Happy simulating! 🚀
