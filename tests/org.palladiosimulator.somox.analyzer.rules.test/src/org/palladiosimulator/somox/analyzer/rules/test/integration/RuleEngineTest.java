package org.palladiosimulator.somox.analyzer.rules.test.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.reliability.FailureType;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.somox.analyzer.rules.all.DefaultRule;
import org.palladiosimulator.somox.analyzer.rules.blackboard.RuleEngineBlackboard;
import org.palladiosimulator.somox.analyzer.rules.configuration.RuleEngineConfiguration;
import org.palladiosimulator.somox.analyzer.rules.service.ServiceConfiguration;
import org.palladiosimulator.somox.analyzer.rules.workflow.RuleEngineJob;
import org.palladiosimulator.somox.discoverer.Discoverer;
import org.palladiosimulator.somox.discoverer.DiscovererCollection;

import com.google.common.collect.Sets;

import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

abstract class RuleEngineTest {
    public static final URI TEST_DIR = CommonPlugin
        .asLocalURI(URI.createFileURI(URI.decode(new File("res").getAbsolutePath())));

    public static final URI OUT_DIR = TEST_DIR.appendSegment("out");

    public static void validate(EObject eObject) {
        EcoreUtil.resolveAll(eObject);
        assertEquals(Diagnostic.OK, Diagnostician.INSTANCE.validate(eObject)
            .getSeverity());
    }

    // Seperate instances for every child test
    private final Logger logger = Logger.getLogger(this.getClass());

    private final RuleEngineConfiguration config = new RuleEngineConfiguration();
    private final RuleEngineJob ruleEngine;
    private final boolean executedSuccessfully;
    private Repository repository;
    private System system;
    private ResourceEnvironment resourceEnvironment;
    private Allocation allocation;
    private final Set<DefaultRule> rules;

    /**
     * Tests the basic functionality of the RuleEngineAnalyzer. Requires it to execute without an
     * exception and produce an output file.
     *
     * @param projectDirectory
     *            the name of the project directory that will be analyzed
     * @param rules
     *            the rules to execute
     */
    protected RuleEngineTest(String projectDirectory, DefaultRule... rules) {
        this.rules = Set.of(rules);

        config.setInputFolder(TEST_DIR.appendSegments(projectDirectory.split("/")));
        config.setOutputFolder(OUT_DIR);
        config.setSelectedRules(this.rules);

        // Enable all discoverers.
        ServiceConfiguration<Discoverer> discovererConfig = config.getDiscovererConfig();
        try {
            for (Discoverer discoverer : new DiscovererCollection().getServices()) {
                discovererConfig.setSelected(discoverer, true);
            }
        } catch (InvalidRegistryObjectException | CoreException e) {
            logger.error(e);
        }

        ruleEngine = new RuleEngineJob(config);

        boolean executedSuccessfully;
        try {
            ruleEngine.execute(new NullProgressMonitor());
            executedSuccessfully = true;
        } catch (JobFailedException | UserCanceledException e) {
            logger.error(e);
            executedSuccessfully = false;
        }
        this.executedSuccessfully = executedSuccessfully;
    }

    // Assertion utilities

    private void assertSuccessfulExecution() {
        assertTrue(executedSuccessfully, "Failed to run RuleEngine!");
    }

    public void assertMaxParameterCount(int expectedMaxParameterCount, String interfaceName, String signatureName) {
        assertInterfaceExists(interfaceName);
        assertOperationExists(interfaceName, signatureName);
        assertEquals(expectedMaxParameterCount, getSignatureMaxParameterCount(interfaceName, signatureName));
    }

    public void assertComponentExists(String name) {
        assertTrue(getComponents().stream()
            .anyMatch(x -> x.getEntityName()
                .equals(name)), "component \"" + name + "\" must exist");
    }

    public void assertInterfaceExists(String name) {
        assertTrue(getInterfaces().stream()
            .filter(OperationInterface.class::isInstance)
            .anyMatch(x -> x.getEntityName()
                .equals(name)), "interface \"" + name + "\" must exist");
    }

    public void assertOperationExists(String interfaceName, String signatureName) {
        assertFalse(getOperationSignature(interfaceName, signatureName).isEmpty(),
                "interface \"" + interfaceName + "\" must contain operation \"" + signatureName + "\"");
    }

    public void assertComponentRequiresComponent(String requiringName, String providingName) {
        Optional<RepositoryComponent> requiringComponent = getComponents().stream()
            .filter(x -> x.getEntityName()
                .equals(requiringName))
            .findFirst();
        assertTrue(requiringComponent.isPresent(), "\"" + requiringName + "\" must exist");

        Optional<RepositoryComponent> providingComponent = getComponents().stream()
            .filter(x -> x.getEntityName()
                .equals(providingName))
            .findFirst();
        assertTrue(providingComponent.isPresent(), "\"" + providingName + "\" must exist");

        List<Interface> interfaces = getInterfaces();
        assertFalse(interfaces.isEmpty(), "an interface must exist in order for a component to require another");

        Set<EObject> requiredObjects = requiringComponent.get()
            .getRequiredRoles_InterfaceRequiringEntity()
            .stream()
            .flatMap(x -> x.eCrossReferences()
                .stream())
            .collect(Collectors.toSet());
        assertFalse(requiredObjects.isEmpty(), "\"" + requiringName + "\" must require something");

        Set<Interface> requiredInterfaces = interfaces.stream()
            .filter(requiredObjects::contains)
            .collect(Collectors.toSet());
        assertFalse(requiredInterfaces.isEmpty(), "\"" + requiringName + "\" must require an interface");

        Set<EObject> providedObjects = providingComponent.get()
            .getProvidedRoles_InterfaceProvidingEntity()
            .stream()
            .flatMap(x -> x.eCrossReferences()
                .stream())
            .collect(Collectors.toSet());
        assertFalse(providedObjects.isEmpty(), "\"" + providingName + "\" must provide something");

        Set<Interface> providedInterfaces = interfaces.stream()
            .filter(providedObjects::contains)
            .collect(Collectors.toSet());
        assertFalse(providedInterfaces.isEmpty(), "\"" + providingName + "\" must provide an interface");

        assertTrue(requiredInterfaces.stream()
            .anyMatch(providedInterfaces::contains),
                "\"" + requiringName + "\" must require an interface that \"" + providingName + "\" provides");
    }

    // Getters

    public RuleEngineConfiguration getConfig() {
        assertSuccessfulExecution();
        return config;
    }

    public List<RepositoryComponent> getComponents() {
        assertSuccessfulExecution();
        return Collections.unmodifiableList(repository.getComponents__Repository());
    }

    public List<DataType> getDatatypes() {
        assertSuccessfulExecution();
        return Collections.unmodifiableList(repository.getDataTypes__Repository());
    }

    public List<FailureType> getFailuretypes() {
        assertSuccessfulExecution();
        return Collections.unmodifiableList(repository.getFailureTypes__Repository());
    }

    public List<Interface> getInterfaces() {
        assertSuccessfulExecution();
        return Collections.unmodifiableList(repository.getInterfaces__Repository());
    }

    public Repository getRepository() {
        assertSuccessfulExecution();
        assertNotNull(repository);
        return repository;
    }

    public System getSystem() {
        assertSuccessfulExecution();
        assertNotNull(system);
        return system;
    }

    public ResourceEnvironment getResourceEnvironment() {
        assertSuccessfulExecution();
        assertNotNull(resourceEnvironment);
        return resourceEnvironment;
    }

    public Allocation getAllocation() {
        assertSuccessfulExecution();
        assertNotNull(allocation);
        return allocation;
    }

    private Set<OperationSignature> getOperationSignature(String interfaceName, String signatureName) {
        return getInterfaces().stream()
            .filter(OperationInterface.class::isInstance)
            .map(OperationInterface.class::cast)
            .filter(x -> x.getEntityName()
                .equals(interfaceName))
            .map(x -> x.getSignatures__OperationInterface()
                .stream()
                .filter(y -> {
                    // Ignore uniqueness postfix
                    String name = y.getEntityName();
                    int postfixStart = name.indexOf('$');
                    if (postfixStart > -1) {
                        return name.substring(0, postfixStart)
                            .equals(signatureName);
                    }
                    return name.equals(signatureName);
                })
                .collect(Collectors.toSet()))
            .reduce(new HashSet<>(), Sets::union);
    }

    public RuleEngineBlackboard getBlackboard() {
        assertSuccessfulExecution();
        return ruleEngine.getBlackboard();
    }

    public Set<DefaultRule> getRules() {
        return Collections.unmodifiableSet(rules);
    }

    public int getSignatureMaxParameterCount(String interfaceName, String signatureName) {
        final Set<OperationSignature> sigs = getOperationSignature(interfaceName, signatureName);
        return sigs.stream()
            .map(OperationSignature::getParameters__OperationSignature)
            .map(List::size)
            .reduce(0, Math::max);
    }

    // Resource loading

    protected enum Artifacts {
        RULEENGINE, MOCORE,
    }

    @SuppressWarnings("unchecked")
    private static <T extends EObject> T loadResource(URI uri, Class<T> targetType) {
        Resource resource = new ResourceSetImpl().getResource(uri, true);
        if (resource == null) {
            return null;
        }

        final List<EObject> contents = resource.getContents();

        assertEquals(1, contents.size());
        EObject content = contents.get(0);
        assertTrue(targetType.isInstance(content));

        // TODO activate this again when SEFF is included
        // validate(contents.get(0));

        return (T) content;
    }

    @SuppressWarnings("unused")
    protected void loadArtifacts(Artifacts artifacts) {
        assertSuccessfulExecution();

        switch (artifacts) {
        case RULEENGINE:
            repository = loadResource(OUT_DIR.appendSegment("pcm.repository"), Repository.class);
            system = null;
            resourceEnvironment = null;
            allocation = null;
            break;
        case MOCORE:
            // TODO: Depends on MoCoRe merge.
            if (false) {
                URI mocoreBase = OUT_DIR.appendSegment("mocore");
                repository = loadResource(mocoreBase.appendFileExtension("repository"), Repository.class);
                system = loadResource(mocoreBase.appendFileExtension("system"), System.class);
                resourceEnvironment = loadResource(mocoreBase.appendFileExtension("resourceenvironment"),
                        ResourceEnvironment.class);
                allocation = loadResource(mocoreBase.appendFileExtension("allocation"), Allocation.class);
            }
            break;
        default:
            throw new IllegalArgumentException("Unhandled artifact type!");
        }
    }

    // Template methods

    void testRuleEngineRepository() {
    }

    void testRuleEngineSeff() {
    }

    void testMoCoReRepository() {
    }

    void testMoCoReSeff() {
    }

    void testMoCoReSystem() {
    }

    void testMoCoReResourceEnvironment() {
    }

    void testMoCoReAllocation() {
    }

    // Tests

    @Test
    void ruleEngineRepository() {
        loadArtifacts(Artifacts.RULEENGINE);
        testRuleEngineRepository();
    }

    @Test
    void ruleEngineSeff() {
        loadArtifacts(Artifacts.RULEENGINE);
        testRuleEngineSeff();
    }

    @Test
    void moCoReRepository() {
        loadArtifacts(Artifacts.MOCORE);
        testMoCoReRepository();
    }

    @Test
    void moCoReSeff() {
        loadArtifacts(Artifacts.MOCORE);
        testMoCoReSeff();
    }

    @Test
    void moCoReSystem() {
        loadArtifacts(Artifacts.MOCORE);
        testMoCoReSystem();
    }

    @Test
    void moCoReResourceEnvironment() {
        loadArtifacts(Artifacts.MOCORE);
        testMoCoReResourceEnvironment();
    }

    @Test
    void moCoReAllocation() {
        loadArtifacts(Artifacts.MOCORE);
        testMoCoReAllocation();
    }

    @AfterEach
    void saveResults() {
        final File target = new File(OUT_DIR.devicePath(), this.getClass()
            .getSimpleName());

        try {
            Files.walkFileTree(target.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Could not delete \"" + target.getAbsolutePath() + "\"!", e);
        }

        if (!new File(OUT_DIR.devicePath()).renameTo(target)) {
            logger.error("Could not save artifacts to \"" + target.getAbsolutePath() + "\"!");
        }
    }
}
