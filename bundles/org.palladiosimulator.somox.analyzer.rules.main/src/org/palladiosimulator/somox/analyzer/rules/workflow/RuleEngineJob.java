package org.palladiosimulator.somox.analyzer.rules.workflow;

import org.palladiosimulator.somox.analyzer.rules.blackboard.RuleEngineBlackboard;
import org.palladiosimulator.somox.analyzer.rules.configuration.RuleEngineConfiguration;
import org.palladiosimulator.somox.analyzer.rules.mocore.workflow.MoCoReJob;
import org.palladiosimulator.somox.analyzer.rules.mocore.workflow.PersistenceJob;
import org.palladiosimulator.somox.analyzer.rules.service.Analyst;
import org.palladiosimulator.somox.ast2seff.jobs.Ast2SeffJob;
import org.palladiosimulator.somox.discoverer.Discoverer;

import de.uka.ipd.sdq.workflow.extension.AbstractExtendableJob;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.ParallelJob;

public class RuleEngineJob extends AbstractExtendableJob<RuleEngineBlackboard> {

    public RuleEngineJob(RuleEngineConfiguration configuration) {
        super.setBlackboard(new RuleEngineBlackboard());

        super.add(createDiscoverersJob(configuration));

        super.add(new RuleEngineBlackboardInteractingJob(configuration, getBlackboard()));

        Ast2SeffJob ast2SeffJob = new Ast2SeffJob();
        ast2SeffJob.setBlackboard(getBlackboard());
        super.add(ast2SeffJob);

        super.add(new MoCoReJob(getBlackboard()));

        super.add(new PersistenceJob(getBlackboard(), configuration.getInputFolder(), configuration.getOutputFolder(),
                "repository", "system", "allocation", "resource"));

        super.add(createAnalystsJob(configuration));
    }

    private ParallelJob createDiscoverersJob(RuleEngineConfiguration configuration) {
        ParallelJob parentJob = new ParallelJob();
        for (Discoverer discoverer : configuration.getDiscovererConfig().getSelected()) {
            IBlackboardInteractingJob<RuleEngineBlackboard> discovererJob = discoverer.create(configuration,
                    myBlackboard);
            parentJob.add(discovererJob);
            logger.info("Adding discoverer job \"" + discovererJob.getName() + "\"");
        }
        return parentJob;
    }

    private ParallelJob createAnalystsJob(RuleEngineConfiguration configuration) {
        ParallelJob parentJob = new ParallelJob();
        for (Analyst analyst : configuration.getAnalystConfig().getSelected()) {
            IBlackboardInteractingJob<RuleEngineBlackboard> analystJob = analyst.create(configuration, myBlackboard);
            parentJob.add(analystJob);
            logger.info("Adding analyst job \"" + analystJob.getName() + "\"");
        }
        return parentJob;
    }
}
