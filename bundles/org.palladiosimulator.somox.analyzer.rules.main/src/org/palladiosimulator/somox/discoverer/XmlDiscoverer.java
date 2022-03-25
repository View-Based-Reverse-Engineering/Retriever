package org.palladiosimulator.somox.discoverer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.palladiosimulator.somox.analyzer.rules.blackboard.RuleEngineBlackboard;
import org.palladiosimulator.somox.analyzer.rules.configuration.RuleEngineConfiguration;

import de.uka.ipd.sdq.workflow.jobs.AbstractBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

public class XmlDiscoverer implements Discoverer {

    @Override
    public IBlackboardInteractingJob<RuleEngineBlackboard> create(final RuleEngineConfiguration configuration,
            final RuleEngineBlackboard blackboard) {
        return new AbstractBlackboardInteractingJob<>() {

            @Override
            public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {
                // TODO Auto-generated method stub
            }

            @Override
            public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
                final Path root = Paths.get(configuration.getInputFolder().devicePath()).toAbsolutePath().normalize();
                setBlackboard(Objects.requireNonNull(blackboard));
                final Map<String, Document> xmls = new HashMap<>();
                Discoverer.find(root, ".xml", logger).forEach(p -> {
                    try (Reader reader = new FileReader(p)) {
                        xmls.put(p, new SAXBuilder().build(reader));
                    } catch (IOException | JDOMException e) {
                        logger.error(String.format("%s could not be read correctly.", p), e);
                    }
                });

                final Map<String, Document> poms = new HashMap<>();
                xmls.keySet().stream().filter(p -> p.toLowerCase().endsWith("pom.xml")).forEach(k -> poms.put(k, xmls.get(k)));
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    @Override
    public Set<String> getConfigurationKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

}
