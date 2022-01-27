package org.palladiosimulator.somox.analyzer.rules.blackboard;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.emftext.language.java.containers.impl.CompilationUnitImpl;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.somox.analyzer.rules.engine.PCMDetectorSimple;
import org.somox.extractor.ExtractionResult;
import org.somox.gast2seff.jobs.SoMoXBlackboard;

import com.google.common.collect.Sets;

public class RuleEngineBlackboard extends SoMoXBlackboard {

    private Map<String, ExtractionResult> extractionResults;
    private Set<CompilationUnitImpl> compilationUnits;
    private Map<CompilationUnitImpl, Set<Path>> compilationUnitLocations;
    private Map<Entity, CompilationUnitImpl> entityLocations;
    private PCMDetectorSimple pcmDetector;

    public RuleEngineBlackboard() {
        extractionResults = new HashMap<>();
        compilationUnits = new HashSet<>();
        compilationUnitLocations = new HashMap<>();
        entityLocations = new HashMap<>();
    }

    public ExtractionResult putExtractionResult(String identifier, ExtractionResult extractionResult) {
        return extractionResults.put(identifier, extractionResult);
    }

    public Map<String, ExtractionResult> getExtractionResults() {
        return Collections.unmodifiableMap(extractionResults);
    }

    public void addCompilationUnitLocation(CompilationUnitImpl compilationUnit, Path path) {
        Set<Path> paths = compilationUnitLocations.get(compilationUnit);
        if (paths == null) {
            paths = new HashSet<>();
            compilationUnitLocations.put(compilationUnit, paths);
        }
        paths.add(path.normalize());
    }

    public Set<Path> getCompilationUnitLocations(CompilationUnitImpl compilationUnit) {
        Set<Path> paths = compilationUnitLocations.get(compilationUnit);
        if (paths == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(paths);
        }
    }

    public CompilationUnitImpl putEntityLocation(Entity entity, CompilationUnitImpl compilationUnit) {
        return entityLocations.put(entity, compilationUnit);
    }

    public Map<Entity, Set<Path>> getEntityPaths() {
        final Map<Entity, Set<Path>> entityPaths = new HashMap<>();

        for (Entity entity : entityLocations.keySet()) {
            CompilationUnitImpl compilationUnit = entityLocations.get(entity);
            if (compilationUnit == null)
                continue;
            Set<Path> path = compilationUnitLocations.get(compilationUnit);
            if (path == null)
                continue;
            entityPaths.put(entity, path);
        }

        return Collections.unmodifiableMap(entityPaths);
    }

    public void setPCMDetector(PCMDetectorSimple pcmDetector) {
        this.pcmDetector = pcmDetector;
    }

    public PCMDetectorSimple getPCMDetector() {
        return this.pcmDetector;
    }

    /**
     * Provides all CompilationUnits found at the specified path. For local files, this should
     * usually only be a single file. If {@code path == null}, all CompilationUnits that do not have
     * a path associated with them (e.g. standard library classes or other library classes) are
     * returned.
     * 
     * @param path
     *            the path to look for registered CompilationUnits at
     * @return the CompilationUnits or {@code null} if there was none at the {@code path}
     */
    public Set<CompilationUnitImpl> getCompilationUnitAt(Path path) {
        if (path == null) {
            // Return all registered CompilationUnits that are not associated with a path
            return Sets.difference(compilationUnits, compilationUnitLocations.keySet());
        }

        Set<CompilationUnitImpl> compUnit = new HashSet<>();
        for (Entry<CompilationUnitImpl, Set<Path>> entry : compilationUnitLocations.entrySet()) {
            // Path::equals is enough because the working directory does not change
            if (entry.getValue()
                .contains(path.normalize())) {
                compUnit.add(entry.getKey());
            }
        }

        return compUnit;
    }
    
    public void addCompilationUnit(CompilationUnitImpl compilationUnit) {
        compilationUnits.add(compilationUnit);
    }
    
    public void addCompilationUnits(Collection<CompilationUnitImpl> compilationUnits) {
        this.compilationUnits.addAll(compilationUnits);
    }

    public Set<CompilationUnitImpl> getCompilationUnits() {
        return Collections.unmodifiableSet(compilationUnits);
    }
}
