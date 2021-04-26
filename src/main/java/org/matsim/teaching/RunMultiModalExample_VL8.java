package org.matsim.teaching;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;

public class RunMultiModalExample_VL8 {

    public static void main(String[] args) {
        Config config = ConfigUtils.loadConfig("scenarios/equil/config.xml");

        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(3);
        {
            StrategyConfigGroup.StrategySettings stratSets = new StrategyConfigGroup.StrategySettings();
            stratSets.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeSingleTripMode);
            stratSets.setWeight(0.1);
            config.strategy().addStrategySettings(stratSets);

            config.changeMode().setModes(new String[]{"car", "pedelec"});
        }
        //config.plansCalcRoute().clearModeRoutingParams();
        {
            PlansCalcRouteConfigGroup.ModeRoutingParams pars = new PlansCalcRouteConfigGroup.ModeRoutingParams();
            pars.setMode("pedelec");
            pars.setTeleportedModeSpeed(10.);
            //pars.setBeelineDistanceFactor(1.);
            //pars.setTeleportedModeFreespeedFactor(1.); //?
            config.plansCalcRoute().addModeRoutingParams(pars);
        }
        {
            PlanCalcScoreConfigGroup.ModeParams pars = new PlanCalcScoreConfigGroup.ModeParams("pedelec");
            config.planCalcScore().addModeParams(pars);
        }
        //===
        Scenario scenario = ScenarioUtils.loadScenario(config);

        //===
        Controler controler = new Controler(scenario);

        controler.run();
    }
}
