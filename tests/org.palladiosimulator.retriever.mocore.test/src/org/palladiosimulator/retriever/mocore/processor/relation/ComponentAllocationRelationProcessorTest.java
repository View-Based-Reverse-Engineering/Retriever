package org.palladiosimulator.retriever.mocore.processor.relation;

import org.palladiosimulator.retriever.mocore.surrogate.PcmSurrogate;
import org.palladiosimulator.retriever.mocore.surrogate.element.Component;
import org.palladiosimulator.retriever.mocore.surrogate.element.Deployment;
import org.palladiosimulator.retriever.mocore.surrogate.relation.ComponentAllocationRelation;
import org.palladiosimulator.retriever.mocore.utility.ElementFactory;

import tools.mdsd.mocore.framework.processor.RelationProcessorTest;

public class ComponentAllocationRelationProcessorTest extends
        RelationProcessorTest<ComponentAllocationRelationProcessor, PcmSurrogate, ComponentAllocationRelation, Component<?>, Deployment> {
    @Override
    protected ComponentAllocationRelation createRelation(Component<?> source, Deployment destination,
            boolean isPlaceholder) {
        return new ComponentAllocationRelation(source, destination, isPlaceholder);
    }

    @Override
    protected Component<?> getUniqueNonPlaceholderSourceEntity() {
        return ElementFactory.createUniqueComponent(false);
    }

    @Override
    protected Component<?> getPlaceholderOfSourceEntity(Component<?> source) {
        return new Component<>(source.getValue(), true);
    }

    @Override
    protected Deployment getUniqueNonPlaceholderDestinationEntity() {
        return ElementFactory.createUniqueDeployment(false);
    }

    @Override
    protected Deployment getPlaceholderOfDestinationEntity(Deployment destination) {
        return new Deployment(destination.getValue(), true);
    }

    @Override
    protected ComponentAllocationRelationProcessor createProcessor(PcmSurrogate model) {
        return new ComponentAllocationRelationProcessor(model);
    }

    @Override
    protected PcmSurrogate createEmptyModel() {
        return new PcmSurrogate();
    }
}
