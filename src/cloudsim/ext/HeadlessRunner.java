package cloudsim.ext;

import cloudsim.ext.Constants;
import cloudsim.ext.event.CloudSimEvent;
import cloudsim.ext.event.CloudSimEventListener;

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
        
        // Reduce simulation time for quick check (e.g. 60 seconds)
        sim.setSimulationTime(60 * 1000.0); 
        
        sim.runSimulation();
    }

    @Override
    public void cloudSimEventFired(CloudSimEvent e) {
        // System.out.println("Event: " + e.getId());
    }
}
