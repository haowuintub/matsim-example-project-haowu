package org.matsim.teaching;

import org.locationtech.jts.util.CollectionUtil;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.CollectionUtils;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehiclesFactory;

import java.util.Objects;

public class RunMultiModalExampleLecture_VL10 {

    public static void main(String[] args) {
        Config config = ConfigUtils.loadConfig("scenarios/equil/config.xml");

        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(5);
        // mode change
        {
            StrategyConfigGroup.StrategySettings stratSets = new StrategyConfigGroup.StrategySettings();
            stratSets.setWeight(0.1);
            stratSets.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeSingleTripMode);
            config.strategy().addStrategySettings(stratSets);

            config.changeMode().setModes(new String[]{"car", "pedelec"});
        }
        //config.plansCalcRoute().clearModeRoutingParams();

        // routing:
/*        {
            PlansCalcRouteConfigGroup.ModeRoutingParams pars = new PlansCalcRouteConfigGroup.ModeRoutingParams();
            pars.setMode("pedelec");
            pars.setTeleportedModeSpeed(25./3.6);
            //pars.setBeelineDistanceFactor(1.);
            //pars.setTeleportedModeFreespeedFactor(1.); //?
            config.plansCalcRoute().addModeRoutingParams(pars);
        }
        {
            PlansCalcRouteConfigGroup.ModeRoutingParams pars = new PlansCalcRouteConfigGroup.ModeRoutingParams();
            pars.setMode("walk");
            pars.setTeleportedModeSpeed(4./3.6);
            //pars.setBeelineDistanceFactor(1.);
            //pars.setTeleportedModeFreespeedFactor(1.); //?
            config.plansCalcRoute().addModeRoutingParams(pars);
        }*/
        {
            config.plansCalcRoute().setNetworkModes(CollectionUtils.stringToSet("car,pedelec"));
        }

        // qsim:
        {
            config.qsim().setMainModes(CollectionUtils.stringToSet("car,pedelec"));
            config.qsim().setVehiclesSource(QSimConfigGroup.VehiclesSource.modeVehicleTypesFromVehiclesData);
            config.qsim().setLinkDynamics(QSimConfigGroup.LinkDynamics.PassingQ);
        }

        //scoring:
        {
            PlanCalcScoreConfigGroup.ModeParams params = new PlanCalcScoreConfigGroup.ModeParams("pedelec");
            params.setConstant(100.);
            config.planCalcScore().addModeParams(params);
        }
        //===
        Scenario scenario = ScenarioUtils.loadScenario(config);

        // make network multimodal
        for(Link link : scenario.getNetwork().getLinks().values()){
            link.setAllowedModes(CollectionUtils.stringToSet("car,pedelec"));
        }
        for ( int ii=2 ; ii<=19; ii++ ) {
            if ( ii==6 || ii==15 ) {
                continue;
            }
            Link link = scenario.getNetwork().getLinks().get( Id.createLinkId(ii) );
            Objects.requireNonNull( link );
            link.setAllowedModes( CollectionUtils.stringToSet("pedelec") );
        }

        // add vehicle types
        VehiclesFactory vf = scenario.getVehicles().getFactory();
        {
            VehicleType vehType = vf.createVehicleType(Id.create("pedelec",VehicleType.class));
            vehType.setMaximumVelocity( 25./3.6 );
            //vehType.setNetworkMode("pedelec");
            scenario.getVehicles().addVehicleType(vehType);
        }
        {   //我觉得已经有car的vehicleTyp了，为啥还要再加一次（不加这个会报错）
            VehicleType vehType = vf.createVehicleType(Id.create("car",VehicleType.class));
            scenario.getVehicles().addVehicleType(vehType);
        }

        // spread out departure times:
        for( Person person : scenario.getPopulation().getPersons().values() ){
            for( PlanElement planElement : person.getSelectedPlan().getPlanElements() ){
                if ( planElement instanceof Activity) {
                    ((Activity) planElement).setEndTime( 5*3600+1800+3600*Math.random() );
                }
            }
        }

        //===
        Controler controler = new Controler(scenario);

        controler.run();
    }
}
