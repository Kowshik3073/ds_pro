package cloudsim.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cloudsim.ext.Constants;
import cloudsim.ext.gui.utils.SimMeasure;
import cloudsim.ext.event.CloudSimEvent;
import cloudsim.ext.event.CloudSimEventListener;
import cloudsim.ext.gui.DataCenterUIElement;
import cloudsim.ext.gui.MachineUIElement;
import cloudsim.ext.gui.UserBaseUIElement;
import cloudsim.ext.gui.VmAllocationUIElement;

public class HeadlessRunner implements CloudSimEventListener {

    public static void main(String[] args) {
        try {
            new HeadlessRunner().run(args);
        } catch (Exception e) {
            System.out.println("CRITICAL ERROR:");
            e.printStackTrace(System.out);
        }
    }

    public void run(String[] args) throws Exception {
        System.out.println("Starting Headless Simulation...");
        
        String algo = Constants.LOAD_BALANCE_MIN_MIN;
        if (args.length > 0) {
            algo = args[0];
        }
        
        System.out.println("\n--------------------------------------------------");
        System.out.println("Testing Algorithm: " + algo);
        System.out.println("--------------------------------------------------");
        
        Simulation sim = new Simulation(this);
        sim.setLoadBalancePolicy(algo);
        sim.setSimulationTime(5 * 60 * 1000.0); // 5 Minute Simulation for verification

        // Clear default configuration
        sim.getDataCenters().clear();
        sim.getUserBases().clear();

        // --- Configure Data Centers ---

        // DC1 (High Power) - Region 0
        // Host0: 25000 MIPS (5 CPUs * 5000 MIPS)
        // Host1: 15000 MIPS (3 CPUs * 5000 MIPS)
        // VMs: 20
        DataCenterUIElement dc1 = new DataCenterUIElement("DC1", 0, "x86", "Linux", "Xen", 0.1, 0.05, 0.1, 0.1);
        List<MachineUIElement> machines1 = new ArrayList<MachineUIElement>();
        machines1.add(new MachineUIElement(204800, 100000000, 1000000, 5, 5000, MachineUIElement.VmAllocationPolicy.TIME_SHARED)); // Host 0
        machines1.add(new MachineUIElement(204800, 100000000, 1000000, 3, 5000, MachineUIElement.VmAllocationPolicy.TIME_SHARED)); // Host 1
        dc1.setMachineList(machines1);
        dc1.setVmAllocation(new VmAllocationUIElement(dc1, 20, 10000, 512, 1000));
        sim.getDataCenters().add(dc1);

        // DC2 (Medium Power) - Region 2
        // Host0: 8000 MIPS (4 CPUs * 2000 MIPS)
        // Host1: 6000 MIPS (3 CPUs * 2000 MIPS)
        // VMs: 12
        DataCenterUIElement dc2 = new DataCenterUIElement("DC2", 2, "x86", "Linux", "Xen", 0.1, 0.05, 0.1, 0.1);
        List<MachineUIElement> machines2 = new ArrayList<MachineUIElement>();
        machines2.add(new MachineUIElement(204800, 100000000, 1000000, 4, 2000, MachineUIElement.VmAllocationPolicy.TIME_SHARED)); // Host 0
        machines2.add(new MachineUIElement(204800, 100000000, 1000000, 3, 2000, MachineUIElement.VmAllocationPolicy.TIME_SHARED)); // Host 1
        dc2.setMachineList(machines2);
        dc2.setVmAllocation(new VmAllocationUIElement(dc2, 12, 10000, 512, 1000));
        sim.getDataCenters().add(dc2);

        // DC3 (Low Power) - Region 4
        // Host0: 4000 MIPS (2 CPUs * 2000 MIPS)
        // VMs: 8
        DataCenterUIElement dc3 = new DataCenterUIElement("DC3", 4, "x86", "Linux", "Xen", 0.1, 0.05, 0.1, 0.1);
        List<MachineUIElement> machines3 = new ArrayList<MachineUIElement>();
        machines3.add(new MachineUIElement(204800, 100000000, 1000000, 2, 2000, MachineUIElement.VmAllocationPolicy.TIME_SHARED)); // Host 0
        dc3.setMachineList(machines3);
        dc3.setVmAllocation(new VmAllocationUIElement(dc3, 8, 10000, 512, 1000));
        sim.getDataCenters().add(dc3);


        // --- Configure User Bases ---

        // UB1: Region 0, Peak: 3000, Req/hr: 120, Data: 10000, Peak: 3-9
        sim.getUserBases().add(new UserBaseUIElement("UB1", 0, 120, 10000, new int[]{3, 9}, 3000, 150));

        // UB2: Region 2, Peak: 1500, Req/hr: 120, Data: 9000, Peak: 6-18
        sim.getUserBases().add(new UserBaseUIElement("UB2", 2, 120, 9000, new int[]{6, 18}, 1500, 100));

        // UB3: Region 4, Peak: 2000, Req/hr: 80, Data: 7000, Peak: 12-20
        sim.getUserBases().add(new UserBaseUIElement("UB3", 4, 80, 7000, new int[]{12, 20}, 2000, 100));

        
        sim.runSimulation();

        // --- Print Results ---
        Map<String, Object> results = sim.getResults();
        
        System.out.println("\n========== SCENARIO default RESULTS ==========");
        System.out.println("Algorithm: " + algo);
        System.out.println();

        // 2. Response Time (UB Stats)
        @SuppressWarnings("unchecked")
        Map<String, SimMeasure> ubStats = (Map<String, SimMeasure>) results.get(Constants.UB_STATS);
        if (ubStats != null) {
            List<String> ubNames = new ArrayList<>();
            for (String key : ubStats.keySet()) {
                String ubName = key.split("\\|\\|")[0];
                if (!ubNames.contains(ubName)) {
                    ubNames.add(ubName);
                }
            }
            java.util.Collections.sort(ubNames);

            for (String ubName : ubNames) {
                SimMeasure measure = null;
                for (SimMeasure m : ubStats.values()) {
                    if (m.getEntityName().equals(ubName) && m.getName().equals(Constants.UB_RESPONSE_TIME)) {
                        measure = m;
                        break;
                    }
                }
                
                if (measure != null) {
                    System.out.println("Response Time (" + ubName + "):");
                    System.out.printf("Avg = %.2f ms%n", measure.getAvg());
                    System.out.printf("Min = %.2f ms%n", measure.getMin());
                    System.out.printf("Max = %.2f ms%n", measure.getMax());
                    System.out.println();
                }
            }
        }
        System.out.println("----------------------------------------");
        System.out.println();

        // 3. Processing Time (DC Stats)
        @SuppressWarnings("unchecked")
        Map<String, SimMeasure> dcStats = (Map<String, SimMeasure>) results.get(Constants.DC_PROCESSING_TIME_STATS);
        if (dcStats != null) {
            List<String> dcNames = new ArrayList<>();
            for (String key : dcStats.keySet()) {
                String dcName = key.split("\\|\\|")[0];
                if (!dcNames.contains(dcName)) {
                    dcNames.add(dcName);
                }
            }
            java.util.Collections.sort(dcNames);

            for (String dcName : dcNames) {
                SimMeasure measure = null;
                for (SimMeasure m : dcStats.values()) {
                    if (m.getEntityName().equals(dcName) && m.getName().equals(Constants.DC_SERVICE_TIME)) {
                        measure = m;
                        break;
                    }
                }

                if (measure != null) {
                    System.out.println("Processing Time (" + dcName + "):");
                    System.out.printf("Avg = %.2f ms%n", measure.getAvg());
                    System.out.printf("Max = %.2f ms%n", measure.getMax());
                    System.out.println();
                }
            }
        }
        System.out.println("----------------------------------------");
        System.out.println();

        // 4. Costs
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Double>> costs = (Map<String, Map<String, Double>>) results.get(Constants.COSTS);
        if (costs != null) {
            System.out.println("Cost:");
            List<String> dcNames = new ArrayList<>(costs.keySet());
            java.util.Collections.sort(dcNames);
            
            for (String dcName : dcNames) {
                Map<String, Double> dcCost = costs.get(dcName);
                System.out.printf("%s Total = $%.2f%n", dcName, dcCost.get(Constants.TOTAL_COST));
            }
        }

        System.out.println();
        System.out.println("========================================");
    }

    @Override
    public void cloudSimEventFired(CloudSimEvent e) {
        // System.out.println("Event: " + e.getId());
    }
}
