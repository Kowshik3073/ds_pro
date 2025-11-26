# Experimental Setup

## Simulation Environment

**Framework:** CloudSim  
**Execution Mode:** Headless (Command-line)  
**Simulation Duration:** 5 minutes (300,000 ms)  
**VM Allocation Policy:** Time Shared  
**Service Broker Policy:** Closest Data Center (Proximity-based)

---

## Scenario Configuration

### Heterogeneous Multi-Datacenter Environment

This setup simulates a realistic cloud environment with **heterogeneous hosts** having different processing capabilities, forcing load balancing algorithms to make intelligent decisions.

### Data Centers

#### **DC1 (High Power) - Region 0**

**Hosts:** 3 heterogeneous hosts
- Host 1: 5 CPUs × 5,000 MIPS = **25,000 MIPS** (Fast)
- Host 2: 5 CPUs × 3,000 MIPS = **15,000 MIPS** (Medium)
- Host 3: 5 CPUs × 2,000 MIPS = **10,000 MIPS** (Slow)

**Virtual Machines:** 20 VMs
- RAM: 512 MB per VM
- Bandwidth: 1,000 Mbps per VM
- Storage: 10,000 MB image size per VM

**Cost Model:**
- VM Processing: $0.10/hour
- Memory: $0.05/GB
- Storage: $0.10/GB
- Bandwidth: $0.10/GB

---

#### **DC2 (Medium Power) - Region 2**

**Hosts:** 3 heterogeneous hosts
- Host 1: 4 CPUs × 3,000 MIPS = **12,000 MIPS** (Medium-Fast)
- Host 2: 4 CPUs × 2,000 MIPS = **8,000 MIPS** (Medium)
- Host 3: 3 CPUs × 1,500 MIPS = **4,500 MIPS** (Slow)

**Virtual Machines:** 12 VMs
- RAM: 512 MB per VM
- Bandwidth: 1,000 Mbps per VM
- Storage: 10,000 MB image size per VM

**Cost Model:** Same as DC1

---

#### **DC3 (Low Power) - Region 4**

**Hosts:** 2 heterogeneous hosts
- Host 1: 3 CPUs × 2,500 MIPS = **7,500 MIPS** (Medium)
- Host 2: 2 CPUs × 1,500 MIPS = **3,000 MIPS** (Slow)

**Virtual Machines:** 8 VMs
- RAM: 512 MB per VM
- Bandwidth: 1,000 Mbps per VM
- Storage: 10,000 MB image size per VM

**Cost Model:** Same as DC1

---

### User Bases

#### **UB1 - Region 0 (Heavy Load)**
- Peak Users: 3,500
- Off-Peak Users: 200
- Requests per Hour: 150
- Data Size per Request: 10,000 bytes
- Peak Hours: 03:00 - 09:00 GMT

#### **UB2 - Region 2 (Medium Load)**
- Peak Users: 2,000
- Off-Peak Users: 150
- Requests per Hour: 140
- Data Size per Request: 9,000 bytes
- Peak Hours: 06:00 - 18:00 GMT

#### **UB3 - Region 4 (Variable Load)**
- Peak Users: 2,500
- Off-Peak Users: 150
- Requests per Hour: 100
- Data Size per Request: 7,000 bytes
- Peak Hours: 12:00 - 20:00 GMT

---

## Load Balancing Algorithms Tested

### 1. Min-Min Scheduling
- **Strategy:** Assigns smallest tasks to fastest available resources
- **Behavior:** Greedy approach, may cause load imbalance
- **Best for:** Homogeneous environments with uniform tasks

### 2. Max-Min Scheduling
- **Strategy:** Assigns largest tasks to fastest available resources
- **Behavior:** Better fairness, prevents task starvation
- **Best for:** Mixed workloads with varying task sizes

### 3. Honey Bee Foraging
- **Strategy:** Bio-inspired algorithm mimicking bee foraging behavior
- **Behavior:** Adaptive, discovers and exploits high-performance VMs
- **Best for:** Dynamic environments with heterogeneous resources

### 4. Ant Colony Optimization
- **Strategy:** Probabilistic technique using pheromone trails
- **Behavior:** Learns optimal paths over time
- **Best for:** Complex environments requiring global optimization

---

## Performance Metrics Collected

### 1. Response Time
- Average response time per user base
- Minimum response time
- Maximum response time

### 2. VM Allocation Distribution
- Number of cloudlets allocated to each VM
- Load balance across VMs
- Utilization patterns

### 3. Processing Time
- Average processing time per data center
- Maximum processing time
- Service time statistics

### 4. Cost
- Total cost per data center
- VM processing costs
- Data transfer costs

---

## Key Features of This Setup

### Heterogeneous Environment
- **Different VM Speeds:** VMs inherit performance from their host (5000, 3000, 2000, 1500 MIPS)
- **Algorithm Differentiation:** Forces algorithms to make intelligent decisions
- **Realistic Scenario:** Mirrors real-world cloud environments

### Variable Load
- **Peak Hours:** Different user bases have different peak times
- **User Count Variation:** 3,500 to 2,000 peak users across bases
- **Request Patterns:** 100-150 requests per hour

### Geographic Distribution
- **3 Regions:** Region 0, 2, and 4
- **Network Latency:** Proximity-based routing
- **Distributed Workload:** Simulates global cloud deployment

---

## Why This Setup Tests Algorithms Effectively

1. **Heterogeneity:** Different VM speeds force smart allocation decisions
2. **Scale:** 40 total VMs across 3 data centers
3. **Load Variation:** Different user bases with varying patterns
4. **Realistic Costs:** Economic factors influence optimization
5. **Time-Based Patterns:** Peak hours create dynamic conditions

This experimental setup provides a comprehensive environment to evaluate and compare load balancing algorithm performance in heterogeneous cloud scenarios.
