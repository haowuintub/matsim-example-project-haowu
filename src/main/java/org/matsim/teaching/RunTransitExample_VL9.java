package org.matsim.teaching;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.utils.TransitScheduleValidator;

public class RunTransitExample_VL9 {

    public static void main(String[] args) {
        Config config = ConfigUtils.loadConfig("/Users/haowu/Workspace/git/matsim-example-project-haowu/scenarios/pt-tutorial/0.config.xml");
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(0);

        Scenario scenario = ScenarioUtils.loadScenario(config);

        TransitScheduleValidator.ValidationResult result = TransitScheduleValidator.validateAll(scenario.getTransitSchedule(),scenario.getNetwork());
/*        TransitScheduleValidator.printResult(result);

        System.exit(-1);*/

        Controler controler = new Controler(scenario);

        //controler.addOverridingModule(new SwissRailRaptorModule());

        controler.run();

    }
}
