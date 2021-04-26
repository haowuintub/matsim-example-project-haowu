package org.matsim.teaching;

import com.google.inject.Inject;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.*;



public class RunScoringClass_VL7_Copy {

    private static class MyScoringFunctionFactory implements ScoringFunctionFactory {
        @Inject private Network network;
        @Inject private ScoringParametersForPerson pparams;

        @Override
        public ScoringFunction createNewScoringFunction(final Person person) {
            final ScoringParameters params = pparams.getScoringParameters(person);

            SumScoringFunction ssf = new SumScoringFunction();
            ssf.addScoringFunction(new CharyparNagelLegScoring(params, network ) ) ;
            ssf.addScoringFunction(new CharyparNagelActivityScoring(params));
            ssf.addScoringFunction(new CharyparNagelMoneyScoring(params));
            ssf.addScoringFunction(new CharyparNagelAgentStuckScoring(params));

            SumScoringFunction.BasicScoring aabb = new MyBasicScory();
            ssf.addScoringFunction(aabb);

            return ssf;
        }
        private class MyBasicScory implements SumScoringFunction.BasicScoring {
            @Override
            public void finish() {

            }

            @Override
            public double getScore() {
                return 10;
            }
        }
    }
    public static void main(String[] args) {


        Config config = ConfigUtils.loadConfig("scenarios/equil/config.xml");
        config.controler().setLastIteration(10);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Controler controler = new Controler(scenario);

        controler.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                ScoringFunctionFactory abc = new MyScoringFunctionFactory();
                this.bindScoringFunctionFactory().toInstance(abc);
                //throw new RuntimeException( "not implemented" );
            }
        });
        controler.run();

    }
}