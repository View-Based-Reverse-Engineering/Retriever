package org.palladiosimulator.retriever.extraction.discoverers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.palladiosimulator.retriever.extraction.blackboard.RuleEngineBlackboard;
import org.palladiosimulator.retriever.extraction.engine.RuleEngineConfiguration;

import de.uka.ipd.sdq.workflow.jobs.AbstractBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

public class JavaDiscoverer implements Discoverer {

    public static final String DISCOVERER_ID = "org.palladiosimulator.retriever.extraction.discoverer.java";

    @Override
    public IBlackboardInteractingJob<RuleEngineBlackboard> create(final RuleEngineConfiguration configuration,
            final RuleEngineBlackboard blackboard) {
        return new AbstractBlackboardInteractingJob<>() {

            @Override
            public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {
            }

            @Override
            public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
                final Path root = Paths.get(CommonPlugin.asLocalURI(configuration.getInputFolder())
                    .devicePath());
                setBlackboard(Objects.requireNonNull(blackboard));
                final Map<Path, CompilationUnit> compilationUnits = new HashMap<>();
                final ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
                parser.setKind(ASTParser.K_COMPILATION_UNIT);
                parser.setResolveBindings(true);
                parser.setBindingsRecovery(true);
                parser.setStatementsRecovery(true);
                final String latestJavaVersion = JavaCore.latestSupportedJavaVersion();
                parser.setCompilerOptions(
                        Map.of(JavaCore.COMPILER_SOURCE, latestJavaVersion, JavaCore.COMPILER_COMPLIANCE,
                                latestJavaVersion, JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, latestJavaVersion));
                final String[] classpathEntries = Discoverer.find(root, ".jar", logger)
                    .map(Path::toString)
                    .toArray(String[]::new);
                final String[] sourceFilePaths = Discoverer.find(root, ".java", logger)
                    .map(Path::toString)
                    .toArray(String[]::new);
                try {
                    parser.setEnvironment(classpathEntries, new String[0], new String[0], true);
                    parser.createASTs(sourceFilePaths, new String[sourceFilePaths.length], new String[0],
                            new FileASTRequestor() {
                                @Override
                                public void acceptAST(final String sourceFilePath, final CompilationUnit ast) {
                                    compilationUnits.put(Path.of(sourceFilePath), ast);
                                }
                            }, monitor);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    logger.error(String.format("No Java files in %s could be transposed.", root), e);
                }
                getBlackboard().putDiscoveredFiles(DISCOVERER_ID, compilationUnits);
            }

            @Override
            public String getName() {
                return "Java Discoverer Job";
            }
        };
    }

    @Override
    public Set<String> getConfigurationKeys() {
        return Collections.emptySet();
    }

    @Override
    public String getID() {
        return DISCOVERER_ID;
    }

    @Override
    public String getName() {
        return "Java Discoverer";
    }
}
