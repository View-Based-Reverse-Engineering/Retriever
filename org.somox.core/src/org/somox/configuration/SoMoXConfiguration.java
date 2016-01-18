package org.somox.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.somox.filter.BlacklistFilter;

import de.uka.ipd.sdq.workflow.configuration.AbstractComposedJobConfiguration;
import de.uka.ipd.sdq.workflow.configuration.IJobConfiguration;

/**
 * SoMoX’ central configuration.
 * <h4>attribute map</h4> This configuration has a bijective representation as an
 * <em>attribute map</em>. This is a {@code Map<String, Object>} containing the class’ attribute key
 * constants as keys and the configuration’s attributes as values. The value’s type is the same as
 * the corresponding setter’s return type. A value of {@code null} is interpreted as “not defined”
 * just like if the map did not contain the key in question.
 * <p>
 * For a mapping of attribute constants to getters and setters, see their documentation. An
 * <em>attribute map</em> can be obtained by {@link #toMap()} and be converted into a
 * {@code SoMoXConfiguration} by {@link #SoMoXConfiguration(Map)}.
 *
 * @author Unknown
 * @author Joshua Gleitze
 *
 */
public class SoMoXConfiguration extends AbstractComposedJobConfiguration implements IJobConfiguration {

    private static Logger logger = Logger.getLogger(SoMoXConfiguration.class);

    /**
     * attribute key for {@link #getAdditionalWildcards()} / {@link #setAdditionalWildcards(String)}
     */
    public static final String BLACKLIST_CONFIGURATION_WILDCARDS_ADDITIONAL = "org.somox.metrics.wildcards.additional";
    /**
     * attribute key for {@link #getFileLocations()}.{@code getAnalyserInputFile()} /
     * {@link #getFileLocations()}.{@code setAnalyserInputFile(String)}
     */
    public static final String SOMOX_ANALYZER_INPUT_FILE = "org.somox.analyzer.inputfile";
    /**
     * attribute key for {@link #isReverseEngineerInterfacesNotAssignedToComponent()} /
     * {@link #setReverseEngineerInterfacesNotAssignedToComponent(boolean)}
     */
    public static final String SOMOX_ANALYZER_REVERSE_ENGINEER_INTERFACES_NOT_ASSIGNED_TO_INTERFACES =
            "org.somox.analyzer.ReverseEngineerInterfacesNotAssignedToComponent";
    /**
     * attribute key for {@link #getWildcardKey()} / {@link #setWildcardKey(String)}
     */
    public static final String SOMOX_ANALYZER_WILDCARD_KEY = "org.somox.metrics.wildcards";
    /**
     * attribute key for {@link #getExcludedPrefixesForNameResemblance()} /
     * {@link #setExcludedPrefixesForNameResemblance(String)}
     */
    public static final String SOMOX_EXCLUDED_PREFIXES = "org.somox.metrics.nameResemblance.excludedPrefixes";
    /**
     * attribute key for {@link #getExcludedSuffixesForNameResemblance()} /
     * {@link #setExcludedSuffixesForNameResemblance(String)}
     */
    public static final String SOMOX_EXCLUDED_SUFFIXES = "org.somox.metrics.nameResemblance.excludedSuffixes";
    /**
     * attribute key for {@link #getFileLocations()}.{@code getOutputFolder()} /
     * {@link #getFileLocations()}.{@code setOutputFolder(String)}
     */
    public static final String SOMOX_OUTPUT_FOLDER = "org.somox.outputfile";
    /**
     * attribute key for {@link #getFileLocations()}.{@code getProjectName()} /
     * {@link #getFileLocations()}.{@code setProjectName(String)}
     */
    public static final String SOMOX_PROJECT_NAME = "org.somox.project";
    /**
     * attribute key for {@link #getClusteringConfig()}.
     * {@code getClusteringComposeThresholdDecrement()} / {@link #getClusteringConfig()}.
     * {@code setClusteringComposeThresholdDecrement(double)}
     */
    public static final String SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_COMPOSE =
            "org.somox.clusteringThresholdDecrement.Compose";
    /**
     * attribute key for {@link #getClusteringConfig()}.
     * {@code getClusteringMergeThresholdDecrement()} / {@link #getClusteringConfig()}.
     * {@code setClusteringMergeThresholdDecrement(double)}
     */
    public static final String SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_MERGE =
            "org.somox.clusteringThresholdDecrement.Merge";
    /**
     * attribute key for {@link #getClusteringConfig()}. {@code getMaxComposeClusteringThreshold()}
     * / {@link #getClusteringConfig()}. {@code setMaxComposeClusteringThreshold(double)}
     */
    public static final String SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_COMPOSE =
            "org.somox.clusteringThresholdMax.Compose";
    /**
     * attribute key for {@link #getClusteringConfig()}. {@code getMaxMergeClusteringThreshold()} /
     * {@link #getClusteringConfig()}. {@code setMaxMergeClusteringThreshold(double)}
     */
    public static final String SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_MERGE = "org.somox.clusteringThresholdMax.Merge";
    /**
     * attribute key for {@link #getClusteringConfig()}. {@code getMinComposeClusteringThreshold()}
     * / {@link #getClusteringConfig()}. {@code setMinComposeClusteringThreshold(double)}
     */
    public static final String SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_COMPOSE =
            "org.somox.clusteringThresholdMin.Compose";
    /**
     * attribute key for {@link #getClusteringConfig()}. {@code getMinMergeClusteringThreshold()} /
     * {@link #getClusteringConfig()}. {@code setMinMergeClusteringThreshold(double)}
     */
    public static final String SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_MERGE = "org.somox.clusteringThresholdMin.Merge";
    /**
     * attribute key for {@link #getWeightDirectoryMapping()} /
     * {@link #setWeightDirectoryMapping(double)}
     */
    public static final String SOMOX_WEIGHT_DIRECTORY_MAPPING = "org.somox.directoryMapping.weightDirectoryMapping";
    /**
     * attribute key for {@link #getWeightDMS()} / {@link #setWeightDMS(double)}
     */
    public static final String SOMOX_WEIGHT_DMS = "org.somox.dms.weightDMS";
    /**
     * attribute key for {@link #getWeightHighCoupling()} / {@link #setWeightHighCoupling(double)}
     */
    public static final String SOMOX_WEIGHT_HIGH_COUPLING = "org.somox.nameResemblance.weightHighCoupling";
    /**
     * attribute key for {@link #getWeightHighNameResemblance()} /
     * {@link #setWeightHighNameResemblance(double)}
     */
    public static final String SOMOX_WEIGHT_HIGH_NAME_RESEMBLANCE =
            "org.somox.nameResemblance.weightHighNameResemblance";
    /**
     * attribute key for {@link #getWeightHighSLAQ()} / {@link #setWeightHighSLAQ(double)}
     */
    public static final String SOMOX_WEIGHT_HIGH_SLAQ = "org.somox.subsystemComponent.weightHighSLAQ";
    /**
     * attribute key for {@link #getWeightHighestNameResemblance()} /
     * {@link #setWeightHighestNameResemblance(double)}
     */
    public static final String SOMOX_WEIGHT_HIGHEST_NAME_RESEMBLANCE =
            "org.somox.nameResemblance.weightHighestNameResemblance";
    /**
     * attribute key for {@link #getWeightInterfaceViolationIrrelevant()} /
     * {@link #setWeightInterfaceViolationIrrelevant(double)}
     */
    public static final String SOMOX_WEIGHT_INTERFACE_VIOLATION_IRRELEVANT =
            "org.somox.interfaceViolation.weightInterfaceViolationIrrelevant";
    /**
     * attribute key for {@link #getWeightInterfaceViolationRelevant()} /
     * {@link #setWeightInterfaceViolationRelevant(double)}
     */
    public static final String SOMOX_WEIGHT_INTERFACE_VIOLATION_RELEVANT =
            "org.somox.interfaceViolation.weightInterfaceViolationRelevant";
    /**
     * attribute key for {@link #getWeightLowCoupling()} / {@link #setWeightLowCoupling(double)}
     */
    public static final String SOMOX_WEIGHT_LOW_COUPLING = "org.somox.nameResemblance.weightLowCoupling";
    /**
     * attribute key for {@link #getWeightLowNameResemblance()} /
     * {@link #setWeightLowNameResemblance(double)}
     */
    public static final String SOMOX_WEIGHT_LOW_NAME_RESEMBLANCE = "org.somox.nameResemblance.weightLowNameResemblance";
    /**
     * attribute key for {@link #getWeightLowSLAQ()} / {@link #setWeightLowSLAQ(double)}
     */
    public static final String SOMOX_WEIGHT_LOW_SLAQ = "org.somox.subsystemComponent.weightLowSLAQ";
    /**
     * attribute key for {@link #getWeightMidNameResemblance()} /
     * {@link #setWeightMidNameResemblance(double)}
     */
    public static final String SOMOX_WEIGHT_MID_NAME_RESEMBLANCE = "org.somox.nameResemblance.weightMidNameResemblance";
    /**
     * attribute key for {@link #getWeightPackageMapping()} /
     * {@link #setWeightPackageMapping(double)}
     */
    public static final String SOMOX_WEIGHT_PACKAGE_MAPPING = "org.somox.packageMapping.weightPackageMapping";
    public static final String SOMOX_WILDCARD_DELIMITER = "§";

    private String additionalWildcards = "";

    private BlacklistFilter blacklistFilter = null;

    private final ClusteringConfiguration clusteringConfig = new ClusteringConfiguration();
    private String excludedPrefixesForNameResemblance = "";
    private String excludedSuffixesForNameResemblance = "";
    private final FileLocationConfiguration locations = new FileLocationConfiguration();
    private boolean reverseEngineerInterfacesNotAssignedToComponent;
    private double weightDirectoryMapping;
    private double weightDMS;
    private double weightHighCoupling;
    private double weightHighestNameResemblance;
    private double weightHighNameResemblance;
    private double weightHighSLAQ;
    private double weightInterfaceViolationIrrelevant;
    private double weightInterfaceViolationRelevant;
    private double weightLowCoupling;
    private double weightLowNameResemblance;
    private double weightLowSLAQ;
    private double weightMidNameResemblance;
    private double weightPackageMapping;
    private String wildcardKey = "";

    /**
     * Creates a new SoMoX configuration initialized with default values.
     */
    public SoMoXConfiguration() {
        getFileLocations().setOutputFolder("/model");
        updateBlacklistFilter();
    }

    /**
     * Creates a new SoMoX configuration initialized with the {@code attributeMap}’s values.
     * Attributes not defined in the map will be set to their default values.
     *
     * @param attributeMap
     *            An <em>attribute map</em>, as defined in the class description. It does not need
     *            to contain all attributes.
     */
    public SoMoXConfiguration(final Map<String, Object> attributeMap) {
        this();
        applyAttributeMap(attributeMap);
    }

    /**
     * Sets the attributes specified in the {@code attributeMap} on this configuration. Attributes
     * not specified in the map will be left untouched.
     *
     * @param attributeMap
     *            An <em>attribute map</em>, as defined in the class description. It does not need
     *            to contain all attributes.
     */
    public void applyAttributeMap(final Map<String, Object> attributeMap) {
        if (attributeMap == null) {
            return;
        }

        // Debug output
        logger.debug("SoMoX configuration extended by these attributes:");
        for (final Object key : attributeMap.keySet()) {
            final String keyname = key.toString();

            if (keyname.contains("org.somox")) {
                logger.debug(key + "=" + attributeMap.get(key));
            }

        }
        final FileLocationConfiguration fileLocations = getFileLocations();
        if (attributeMap.get(SOMOX_PROJECT_NAME) != null) {
            fileLocations.setProjectName((String) attributeMap.get(SOMOX_PROJECT_NAME));
        }

        if (attributeMap.get(SOMOX_ANALYZER_INPUT_FILE) != null) {
            fileLocations.setAnalyserInputFile((String) attributeMap.get(SOMOX_ANALYZER_INPUT_FILE));
        }

        if (attributeMap.get(SOMOX_OUTPUT_FOLDER) != null) {
            fileLocations.setOutputFolder((String) attributeMap.get(SOMOX_OUTPUT_FOLDER));
        }

        if (attributeMap.get(SOMOX_ANALYZER_REVERSE_ENGINEER_INTERFACES_NOT_ASSIGNED_TO_INTERFACES) != null) {
            final boolean allInterfacesStrategy =
                    (Boolean) attributeMap.get(SOMOX_ANALYZER_REVERSE_ENGINEER_INTERFACES_NOT_ASSIGNED_TO_INTERFACES);
            this.setReverseEngineerInterfacesNotAssignedToComponent(allInterfacesStrategy);
        }

        if (attributeMap.get(SOMOX_ANALYZER_WILDCARD_KEY) != null) {
            setWildcardKey((String) attributeMap.get(SOMOX_ANALYZER_WILDCARD_KEY));
        }

        if (attributeMap.get(BLACKLIST_CONFIGURATION_WILDCARDS_ADDITIONAL) != null) {
            setAdditionalWildcards((String) attributeMap.get(BLACKLIST_CONFIGURATION_WILDCARDS_ADDITIONAL));
        }

        if (attributeMap.get(SOMOX_EXCLUDED_PREFIXES) != null) {
            setExcludedPrefixesForNameResemblance((String) attributeMap.get(SOMOX_EXCLUDED_PREFIXES));
        }

        if (attributeMap.get(SOMOX_EXCLUDED_SUFFIXES) != null) {
            setExcludedSuffixesForNameResemblance((String) attributeMap.get(SOMOX_EXCLUDED_SUFFIXES));
        }

        if (attributeMap.get(SOMOX_WEIGHT_DIRECTORY_MAPPING) != null) {
            setWeightDirectoryMapping((Double) attributeMap.get(SOMOX_WEIGHT_DIRECTORY_MAPPING));
        }

        if (attributeMap.get(SOMOX_WEIGHT_DMS) != null) {
            setWeightDMS((Double) attributeMap.get(SOMOX_WEIGHT_DMS));
        }

        if (attributeMap.get(SOMOX_WEIGHT_HIGH_COUPLING) != null) {
            setWeightHighCoupling((Double) attributeMap.get(SOMOX_WEIGHT_HIGH_COUPLING));
        }

        if (attributeMap.get(SOMOX_WEIGHT_HIGHEST_NAME_RESEMBLANCE) != null) {
            setWeightHighestNameResemblance((Double) attributeMap.get(SOMOX_WEIGHT_HIGHEST_NAME_RESEMBLANCE));
        }

        if (attributeMap.get(SOMOX_WEIGHT_HIGH_NAME_RESEMBLANCE) != null) {
            setWeightHighNameResemblance((Double) attributeMap.get(SOMOX_WEIGHT_HIGH_NAME_RESEMBLANCE));
        }

        if (attributeMap.get(SOMOX_WEIGHT_HIGH_SLAQ) != null) {
            setWeightHighSLAQ((Double) attributeMap.get(SOMOX_WEIGHT_HIGH_SLAQ));
        }

        if (attributeMap.get(SOMOX_WEIGHT_INTERFACE_VIOLATION_IRRELEVANT) != null) {
            setWeightInterfaceViolationIrrelevant(
                    (Double) attributeMap.get(SOMOX_WEIGHT_INTERFACE_VIOLATION_IRRELEVANT));
        }

        if (attributeMap.get(SOMOX_WEIGHT_INTERFACE_VIOLATION_RELEVANT) != null) {
            setWeightInterfaceViolationRelevant((Double) attributeMap.get(SOMOX_WEIGHT_INTERFACE_VIOLATION_RELEVANT));
        }

        if (attributeMap.get(SOMOX_WEIGHT_LOW_COUPLING) != null) {
            setWeightLowCoupling((Double) attributeMap.get(SOMOX_WEIGHT_LOW_COUPLING));
        }

        if (attributeMap.get(SOMOX_WEIGHT_LOW_NAME_RESEMBLANCE) != null) {
            setWeightLowNameResemblance((Double) attributeMap.get(SOMOX_WEIGHT_LOW_NAME_RESEMBLANCE));
        }

        if (attributeMap.get(SOMOX_WEIGHT_LOW_SLAQ) != null) {
            setWeightLowSLAQ((Double) attributeMap.get(SOMOX_WEIGHT_LOW_SLAQ));
        }

        if (attributeMap.get(SOMOX_WEIGHT_MID_NAME_RESEMBLANCE) != null) {
            setWeightMidNameResemblance((Double) attributeMap.get(SOMOX_WEIGHT_MID_NAME_RESEMBLANCE));
        }

        if (attributeMap.get(SOMOX_WEIGHT_PACKAGE_MAPPING) != null) {
            setWeightPackageMapping((Double) attributeMap.get(SOMOX_WEIGHT_PACKAGE_MAPPING));
        }

        final ClusteringConfiguration clusteringConfiguration = getClusteringConfig();
        if (attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_COMPOSE) != null) {
            clusteringConfiguration.setMaxComposeClusteringThreshold(
                    (Double) attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_COMPOSE));
        }

        if (attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_COMPOSE) != null) {
            clusteringConfiguration.setMinComposeClusteringThreshold(
                    (Double) attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_COMPOSE));
        }
        if (attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_COMPOSE) != null) {
            clusteringConfiguration.setClusteringComposeThresholdDecrement(
                    (Double) attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_COMPOSE));
        }
        if (attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_MERGE) != null) {
            clusteringConfiguration.setMaxMergeClusteringThreshold(
                    (Double) attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_MERGE));
        }

        if (attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_MERGE) != null) {
            clusteringConfiguration.setMinMergeClusteringThreshold(
                    (Double) attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_MERGE));
        }

        if (attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_MERGE) != null) {
            clusteringConfiguration.setClusteringMergeThresholdDecrement(
                    (Double) attributeMap.get(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_MERGE));
        }
    }

    public String getAdditionalWildcards() {
        return additionalWildcards;
    }

    /**
     * Create a new list of blacklist specifications
     *
     * @return A set of blacklist entries
     */
    public Set<String> getBlacklist() {
        final String wildcardString = this.wildcardKey;

        final StringTokenizer tokenizer = new StringTokenizer(wildcardString, SOMOX_WILDCARD_DELIMITER);

        final Set<String> blacklist = new HashSet<String>();
        while (tokenizer.hasMoreElements()) {
            blacklist.add(tokenizer.nextToken());
        }
        return blacklist;
    }

    /**
     * @return the {@link BlacklistFilter} or {@code null} if
     *         {@link #setWildcardKey(String, String)} has not been called yet.
     */
    public BlacklistFilter getBlacklistFilter() {
        return this.blacklistFilter;
    }

    /**
     * @return the clusteringConfig
     */
    public ClusteringConfiguration getClusteringConfig() {
        return this.clusteringConfig;
    }

    @Override
    public String getErrorMessage() {
        return "An error in SoMoX occured";
    }

    public String getExcludedPrefixesForNameResemblance() {
        return this.excludedPrefixesForNameResemblance;
    }

    public String getExcludedSuffixesForNameResemblance() {
        return this.excludedSuffixesForNameResemblance;
    }

    /**
     * @return the locations
     */
    public FileLocationConfiguration getFileLocations() {
        return this.locations;
    }

    public double getWeightDirectoryMapping() {
        return this.weightDirectoryMapping;
    }

    public double getWeightDMS() {
        return this.weightDMS;
    }

    public double getWeightHighCoupling() {
        return this.weightHighCoupling;
    }

    public double getWeightHighestNameResemblance() {
        return this.weightHighestNameResemblance;
    }

    public double getWeightHighNameResemblance() {
        return this.weightHighNameResemblance;
    }

    public double getWeightHighSLAQ() {
        return this.weightHighSLAQ;
    }

    public double getWeightInterfaceViolationIrrelevant() {
        return this.weightInterfaceViolationIrrelevant;
    }

    public double getWeightInterfaceViolationRelevant() {
        return this.weightInterfaceViolationRelevant;
    }

    public double getWeightLowCoupling() {
        return this.weightLowCoupling;
    }

    public double getWeightLowNameResemblance() {
        return this.weightLowNameResemblance;
    }

    public double getWeightLowSLAQ() {
        return this.weightLowSLAQ;
    }

    public double getWeightMidNameResemblance() {
        return this.weightMidNameResemblance;
    }

    public double getWeightPackageMapping() {
        return this.weightPackageMapping;
    }

    public String getWildcardKey() {
        return wildcardKey;
    }

    /**
     * Switch for interface reverse engineering. Serves for debugging-like use of SoMoX.
     *
     * @return
     */
    public boolean isReverseEngineerInterfacesNotAssignedToComponent() {
        return this.reverseEngineerInterfacesNotAssignedToComponent;
    }

    public void setAdditionalWildcards(final String additionalWildcards) {
        this.additionalWildcards = additionalWildcards;
        updateBlacklistFilter();
    }

    public void setExcludedPrefixesForNameResemblance(final String excludedPrefixesForNameResemblance) {
        this.excludedPrefixesForNameResemblance = excludedPrefixesForNameResemblance;
    }

    public void setExcludedSuffixesForNameResemblance(final String excludedSuffixesForNameResemblance) {
        this.excludedSuffixesForNameResemblance = excludedSuffixesForNameResemblance;
    }

    /**
     * Switch for interface reverse engineering. Serves for debugging-like use of SoMoX.
     *
     * @param reverseEngineerInterfacesNotAssignedToComponent
     */
    public void setReverseEngineerInterfacesNotAssignedToComponent(
            final boolean reverseEngineerInterfacesNotAssignedToComponent) {
        this.reverseEngineerInterfacesNotAssignedToComponent = reverseEngineerInterfacesNotAssignedToComponent;
    }

    public void setWeightDirectoryMapping(final double weightDirectoryMapping) {
        this.weightDirectoryMapping = weightDirectoryMapping;
    }

    public void setWeightDMS(final double weightDMS) {
        this.weightDMS = weightDMS;
    }

    public void setWeightHighCoupling(final double weightHighCoupling) {
        this.weightHighCoupling = weightHighCoupling;
    }

    public void setWeightHighestNameResemblance(final double weightHighestNameResemblance) {
        this.weightHighestNameResemblance = weightHighestNameResemblance;
    }

    public void setWeightHighNameResemblance(final double weightHighNameResemblance) {
        this.weightHighNameResemblance = weightHighNameResemblance;
    }

    public void setWeightHighSLAQ(final double weightHighSLAQ) {
        this.weightHighSLAQ = weightHighSLAQ;
    }

    public void setWeightInterfaceViolationIrrelevant(final double weightInterfaceViolationIrrelevant) {
        this.weightInterfaceViolationIrrelevant = weightInterfaceViolationIrrelevant;
    }

    public void setWeightInterfaceViolationRelevant(final double weightInterfaceViolationRelevant) {
        this.weightInterfaceViolationRelevant = weightInterfaceViolationRelevant;
    }

    public void setWeightLowCoupling(final double weightLowCoupling) {
        this.weightLowCoupling = weightLowCoupling;
    }

    public void setWeightLowNameResemblance(final double weightLowNameResemblance) {
        this.weightLowNameResemblance = weightLowNameResemblance;
    }

    public void setWeightLowSLAQ(final double weightLowSLAQ) {
        this.weightLowSLAQ = weightLowSLAQ;
    }

    public void setWeightMidNameResemblance(final double weightMidNameResemblance) {
        this.weightMidNameResemblance = weightMidNameResemblance;
    }

    public void setWeightPackageMapping(final double weightPackageMapping) {
        this.weightPackageMapping = weightPackageMapping;
    }

    public void setWildcardKey(final String wildcardKey) {
        this.wildcardKey = wildcardKey;
    }

    /**
     * Converts this configuration into an <em>attribute map</em>.
     *
     * @return an <em>attribute map</em>, such that for any {@code SoMoXConfiguration c},
     *         {@code new SoMoXConfiguration(c.toMap())} will behave exactly like {@code c}.
     */
    public Map<String, Object> toMap() {
        final Map<String, Object> result = new HashMap<String, Object>();

        result.put(SOMOX_PROJECT_NAME, getFileLocations().getProjectName());
        result.put(SOMOX_ANALYZER_INPUT_FILE, getFileLocations().getAnalyserInputFile());
        result.put(SOMOX_ANALYZER_REVERSE_ENGINEER_INTERFACES_NOT_ASSIGNED_TO_INTERFACES,
                isReverseEngineerInterfacesNotAssignedToComponent());
        result.put(SOMOX_OUTPUT_FOLDER, getFileLocations().getOutputFolder());
        result.put(SOMOX_ANALYZER_WILDCARD_KEY, getWildcardKey());
        result.put(BLACKLIST_CONFIGURATION_WILDCARDS_ADDITIONAL, getAdditionalWildcards());
        result.put(SOMOX_EXCLUDED_PREFIXES, getExcludedPrefixesForNameResemblance());
        result.put(SOMOX_EXCLUDED_SUFFIXES, getExcludedSuffixesForNameResemblance());

        result.put(SOMOX_WEIGHT_DIRECTORY_MAPPING, getWeightDirectoryMapping());
        result.put(SOMOX_WEIGHT_DMS, getWeightDMS());
        result.put(SOMOX_WEIGHT_HIGH_COUPLING, getWeightHighCoupling());

        result.put(SOMOX_WEIGHT_HIGHEST_NAME_RESEMBLANCE, getWeightHighestNameResemblance());
        result.put(SOMOX_WEIGHT_HIGH_NAME_RESEMBLANCE, getWeightHighNameResemblance());
        result.put(SOMOX_WEIGHT_HIGH_SLAQ, getWeightHighSLAQ());
        result.put(SOMOX_WEIGHT_INTERFACE_VIOLATION_IRRELEVANT, getWeightInterfaceViolationIrrelevant());
        result.put(SOMOX_WEIGHT_INTERFACE_VIOLATION_RELEVANT, getWeightInterfaceViolationRelevant());
        result.put(SOMOX_WEIGHT_LOW_COUPLING, getWeightLowCoupling());
        result.put(SOMOX_WEIGHT_LOW_NAME_RESEMBLANCE, getWeightLowNameResemblance());
        result.put(SOMOX_WEIGHT_LOW_SLAQ, getWeightLowSLAQ());
        result.put(SOMOX_WEIGHT_MID_NAME_RESEMBLANCE, getWeightMidNameResemblance());
        result.put(SOMOX_WEIGHT_PACKAGE_MAPPING, getWeightPackageMapping());

        final ClusteringConfiguration clusteringConfiguration = getClusteringConfig();
        result.put(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_COMPOSE,
                clusteringConfiguration.getMaxComposeClusteringThreshold());
        result.put(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_COMPOSE,
                clusteringConfiguration.getMinComposeClusteringThreshold());
        result.put(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_COMPOSE,
                clusteringConfiguration.getClusteringComposeThresholdDecrement());
        result.put(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_MERGE,
                clusteringConfiguration.getMaxMergeClusteringThreshold());
        result.put(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_MERGE,
                clusteringConfiguration.getMinMergeClusteringThreshold());
        result.put(SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_MERGE,
                clusteringConfiguration.getClusteringMergeThresholdDecrement());

        return result;
    }

    private void updateBlacklistFilter() {
        final Set<String> wildCardList = this.getBlacklist();
        if (additionalWildcards != null && additionalWildcards.length() > 0) {
            wildCardList.add(additionalWildcards);
        }
        this.blacklistFilter = new BlacklistFilter(wildCardList);
    }

}
