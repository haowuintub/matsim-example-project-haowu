package org.matsim.teaching_advancedMATSim;

public class RunSimulation_VL3 {
    public static void main( String[] args ){
        Helper helper = new HelperDefaultImpl() ;
        Simulation simulation = new SimulationDefaultImpl( helper );
        // ^^^^^^^
        // (this is where the dependency on Helper is injected!)

        simulation.doStep() ;
    }
    interface Helper {
        void help() ;
    }
    interface Simulation {
        void doStep();
    }
    static class HelperDefaultImpl implements Helper {
        @Override
        public void help() {
            System.out.println( this.getClass().getSimpleName() + " is helping");
        }
    }
    // ...
    static class SimulationDefaultImpl implements Simulation {
        private final Helper helper;
        SimulationDefaultImpl( Helper helper ){
            this.helper = helper;
        }
        @Override
        public void doStep() {
            System.out.println( "entering " + this.getClass().getSimpleName());
            helper.help();
            System.out.println( "leaving " + this.getClass().getSimpleName());
        }
    }
}
