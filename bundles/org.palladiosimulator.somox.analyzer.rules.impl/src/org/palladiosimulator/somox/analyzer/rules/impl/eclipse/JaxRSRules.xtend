package org.palladiosimulator.somox.analyzer.rules.impl.eclipse

import org.palladiosimulator.somox.analyzer.rules.engine.IRule

import static org.palladiosimulator.somox.analyzer.rules.engine.EclipseRuleHelper.*
import org.palladiosimulator.somox.analyzer.rules.blackboard.RuleEngineBlackboard
import java.nio.file.Path;
import org.eclipse.jdt.core.dom.CompilationUnit

class JaxRSRules extends IRule{
	
	new(RuleEngineBlackboard blackboard) {
		super(blackboard)
	}
	
	override boolean processRules(Path path) {
		val units = blackboard.getCompilationUnitAt(path)
		
		var containedSuccessful = false
		for (unit : units) {
			if (unit.isEclipseCompilationUnit()) {
				val eclipseUnit = unit.getEclipseCompilationUnit
				containedSuccessful = processRuleForCompUnit(eclipseUnit) || containedSuccessful
			}
		}
		
		return containedSuccessful
	}
	
	def boolean processRuleForCompUnit(CompilationUnit unit) {
		val pcmDetector = blackboard.getEclipsePCMDetector()
		if (pcmDetector === null) {
		return false
		}

		// technology based and general recognition
		val isConverter = isUnitAnnotatedWithName(unit, "Converter")
		if(isConverter){
			detectDefault(unit)
		return true
		}
		
		// detect controller component	
		val isUnitController = isUnitAnnotatedWithName(unit, "Path")
		if(isUnitController){
			pcmDetector.detectComponent(unit) 
			getMethods(unit).forEach[m|
			if(isMethodAnnotatedWithName(m,"DELETE","GET","HEAD","PUT","POST","OPTIONS")) pcmDetector.detectProvidedOperation(unit,m.resolveBinding)]
			getFields(unit).forEach[f|if(isFieldAbstract(f)) pcmDetector.detectRequiredInterface(unit, f)]
		return true
		} 
		
		val isWebListener = isUnitAnnotatedWithName(unit, "WebListener","WebServlet")
		if(isWebListener){
			pcmDetector.detectComponent(unit)
			getMethods(unit).forEach[m|
			if(isMethodModifiedExactlyWith(m,"public") || isMethodModifiedExactlyWith(m,"protected")) pcmDetector.detectProvidedOperation(unit,m.resolveBinding)]
			getFields(unit).forEach[f|if(isFieldAbstract(f)) pcmDetector.detectRequiredInterface(unit, f)]
		return true
		}
		
		// detect implementing component
		val isUnit = isClassImplementing(unit)
		if(isUnit && !isUnitController && !isWebListener && getAllInterfaces(unit).size() > 0){
			pcmDetector.detectComponent(unit)
			val firstIn = getAllInterfaces(unit).get(0)
			getMethods(firstIn).forEach[m|pcmDetector.detectProvidedOperation(unit, firstIn.resolveBinding, m)]
			getFields(unit).forEach[f|if(isFieldAbstract(f)) pcmDetector.detectRequiredInterface(unit, f)]
			return true
		}
		
		// detect normal components
		val classModified = isClassModifiedExactlyWith(unit, "public","final");
		if(!isUnit && !isUnitController && !isWebListener && classModified){
			pcmDetector.detectComponent(unit)
			detectDefault(unit)
			return true
		} 
		return false
		
	}
	
	def detectDefault(CompilationUnit unit) {
		val pcmDetector = blackboard.getEclipsePCMDetector()

		pcmDetector.detectComponent(unit)
		getAllPublicMethods(unit).forEach[m|pcmDetector.detectProvidedOperation(unit,m.resolveBinding)]
		getFields(unit).forEach[f|if(isFieldAbstract(f)) pcmDetector.detectRequiredInterface(unit, f)]
	}
	
}
