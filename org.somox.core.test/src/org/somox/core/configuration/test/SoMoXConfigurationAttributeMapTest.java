package org.somox.core.configuration.test;

import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.somox.configuration.SoMoXConfiguration;

/**
 * Asserts correct behavior of {@link SoMoXConfiguration#SoMoXConfiguration(java.util.Map)},
 * {@link SoMoXConfiguration#toMap()} and
 * {@link SoMoXConfiguration#applyAttributeMap(java.util.Map)}. The behavior of a
 * {@link SoMoXConfiguration} is compared by recursively comparing getter return values.
 *
 * @author Joshua Gleitze
 */
public class SoMoXConfigurationAttributeMapTest {
    /**
     * maps attribute keys to setters. Setters can be executed by providing the instance and the
     * value to set to the {@link BiConsumer}.
     */
    private static final Map<String, BiConsumer<SoMoXConfiguration, Object>> SETTERS = getKeyToSettersMapping();

    /**
     * {@link Supplier}s for values for all attribute keys.
     */
    private static final Map<String, Supplier<Object>> VALUE_SUPPLIERS = getValueSuppliers();

    /**
     * @return sets of attribute keys that should be tested.
     */
    private static Set<Set<String>> getAttributeKeySets() {
        final Set<String> allKeys = SETTERS.keySet();
        final Set<Set<String>> attributeKeySetsToCheck = new HashSet<>();

        // all attributes
        attributeKeySetsToCheck.add(allKeys);
        // each single attribute
        for (final String attributeKey : allKeys) {
            attributeKeySetsToCheck.add(new HashSet<>(Arrays.asList(attributeKey)));
        }
        // no attributes
        attributeKeySetsToCheck.add(new HashSet<>());

        // 10 random combinations
        final int minCount = 2;
        final String[] allKeysArray = allKeys.toArray(new String[allKeys.size()]);
        for (int i = 0; i < 10; i++) {
            final int numberOfElements = Math.round((float) Math.random() * (allKeys.size() - minCount) + minCount);
            final Set<String> nextSet = new HashSet<>(numberOfElements);
            for (int j = 0; j < numberOfElements; j++) {
                final int nextElementIndex = Math.round((float) Math.random() * (allKeys.size() - 1));
                nextSet.add(allKeysArray[nextElementIndex]);
            }
            attributeKeySetsToCheck.add(nextSet);
        }

        return attributeKeySetsToCheck;
    }

    /**
     * Asserts that converting the default {@link SoMoXConfiguration} into an attribute map and back
     * does not change its behavior.
     */
    @Test
    public void testEqualityWithDefaultValues() {
        final SoMoXConfiguration defaultConfiguration = new SoMoXConfiguration();
        final Map<String, Object> attributeMap = defaultConfiguration.toMap();
        SoMoXConfiguration afterConversion = new SoMoXConfiguration(attributeMap);
        assertThat("The default configuration is changed on conversion!", afterConversion,
                behavesLike(defaultConfiguration));

        afterConversion = new SoMoXConfiguration();
        afterConversion.applyAttributeMap(attributeMap);
        assertThat("The default configuration is changed on conversion!", afterConversion,
                behavesLike(defaultConfiguration));
    }

    /**
     * Asserts that setting values through setters or through an attribute map has the same effect
     * for various combinations of attributes.
     */
    @Test
    public void testEquivalenceOfSetterAndAttributeValue() {
        // for each prepared set of attribut keys: get correct values from the suppliers and use
        // them to set
        for (final Set<String> keySet : getAttributeKeySets()) {
            final SoMoXConfiguration settersConfiguration = new SoMoXConfiguration();
            final Map<String, Object> attributeMap = new HashMap<>();

            for (final String attributeKey : keySet) {
                final Object attributeValue = VALUE_SUPPLIERS.get(attributeKey).get();
                attributeMap.put(attributeKey, attributeValue);
                SETTERS.get(attributeKey).accept(settersConfiguration, attributeValue);
            }

            SoMoXConfiguration mapGeneratedConfiguration = new SoMoXConfiguration(attributeMap);
            assertThat(
                    "The configuration generated by an attribute map doesn’t behave like the one generated with setters",
                    mapGeneratedConfiguration, behavesLike(settersConfiguration));

            mapGeneratedConfiguration = new SoMoXConfiguration();
            mapGeneratedConfiguration.applyAttributeMap(attributeMap);
            assertThat(
                    "The configuration generated by an attribute map doesn’t behave like the one generated with setters",
                    mapGeneratedConfiguration, behavesLike(settersConfiguration));
        }
    }

    /**
     * Asserts that conversion to a map works expected.
     */
    @Test
    public void testToMap() {
        // for each prepared set of attribut keys: get correct values from the suppliers and use
        // them to set.
        for (final Set<String> keySet : getAttributeKeySets()) {
            final SoMoXConfiguration settersConfiguration = new SoMoXConfiguration();
            final Map<String, Object> attributeMap = new HashMap<>();

            for (final String attributeKey : keySet) {
                final Object attributeValue = VALUE_SUPPLIERS.get(attributeKey).get();
                attributeMap.put(attributeKey, attributeValue);
                SETTERS.get(attributeKey).accept(settersConfiguration, attributeValue);
            }

            assertThat("A bad attribute map was created!", settersConfiguration.toMap(),
                    containsAllEntriesOf(attributeMap));
        }
    }

    /**
     * Creates a matcher to compare a {@link SoMoXConfiguration}.
     *
     * @param configuration
     *            The reference configuration.
     * @return A matcher that will match if all return values of all getters of the reference
     *         configuration and the examined configuration are equal or behave equally.
     */
    private static Matcher<SoMoXConfiguration> behavesLike(final SoMoXConfiguration configuration) {
        return new TypeSafeDiagnosingMatcher<SoMoXConfiguration>() {
            /**
             * Getters that return values that need to be examined on their own. Note that this list
             * applies for any examination level.
             */
            private final Set<String> gettersToExamine = new HashSet<>(
                    Arrays.asList(new String[] { "getFileLocations", "getClusteringConfig", "getBlacklistFilter" }));

            @Override
            public void describeTo(final Description description) {
                description.appendText("all methods should return the same values");
            }

            @Override
            protected boolean matchesSafely(final SoMoXConfiguration item, final Description mismatchDescription) {
                return matches(configuration, item, mismatchDescription, "");
            }

            protected boolean matches(final Object reference, final Object examined,
                    final Description mismatchDescription, final String getterPrefix) {
                // find all getters using reflection. This ensures that future modifications will be
                // included
                final List<Method> getters = new ArrayList<>();
                for (final Method method : reference.getClass().getMethods()) {
                    if (method.getName().startsWith("get")) {
                        getters.add(method);
                    }
                }

                // execute all getters on all objects and compare the results
                for (final Method getter : getters) {
                    Object referenceValue;
                    Object examinedValue;

                    final String getterName = getterPrefix + getter.getName() + "()";

                    try {
                        referenceValue = getter.invoke(reference);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        mismatchDescription.appendText("unable to invoke getter ").appendValue(getterName)
                                .appendText(" on the reference configuration\n\n").appendText(getStackString(e));
                        return false;
                    }

                    try {
                        examinedValue = getter.invoke(examined);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        mismatchDescription.appendText("unable to invoke getter ").appendValue(getterName)
                                .appendText(" on the examined configuration\n\n").appendText(getStackString(e));
                        return false;
                    }

                    if (gettersToExamine.contains(getter.getName())) {
                        if (!matches(referenceValue, examinedValue, mismatchDescription, getter.getName() + "().")) {
                            return false;
                        }
                    } else if (!(referenceValue == null ? examinedValue == null
                            : referenceValue.equals(examinedValue))) {
                        mismatchDescription.appendValue(getterName).appendText(" returned ").appendValue(examinedValue)
                                .appendText(" instead of ").appendValue(referenceValue);
                        return false;
                    }
                }

                return true;
            }

            /**
             * Get the stack trace of an Exception as a String.
             *
             * @param e
             *            An exception
             */
            private String getStackString(final Exception e) {
                final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(byteStream));
                return byteStream.toString();
            }
        };
    }

    /**
     * Creates a matcher that matches if the examined map contains all entries {@code subsetMap}
     * does. This means that the examined map’s key set is a superset of {@code subsetMap}’s key set
     * and that for any key {@code k} in {@code subsetKey.keySet()}, the examined map’s mapping for
     * {@code k} equals {@code subsetMap}’s mapping.
     *
     * @param subsetMap
     *            A containing all mappings the examined map must contain.
     * @return A matcher as described above.
     *
     * @param <K>
     *            The subset map’s key type.
     * @param <V>
     *            The subset map’s value type.
     */
    private static <K, V> Matcher<Map<? extends K, ? extends V>> containsAllEntriesOf(final Map<K, V> subsetMap) {
        return new TypeSafeDiagnosingMatcher<Map<? extends K, ? extends V>>() {

            @Override
            public void describeTo(final Description description) {
                description.appendText("A map containing the entries ").appendValueList("[", ", ", "]",
                        subsetMap.entrySet());

            }

            @Override
            protected boolean matchesSafely(final Map<? extends K, ? extends V> item,
                    final Description mismatchDescription) {
                for (final Entry<K, V> entry : subsetMap.entrySet()) {
                    if (!item.containsKey(entry.getKey())) {
                        mismatchDescription.appendText("Did not find a mapping for ").appendValue(entry.getKey());
                        return false;
                    }

                    if (!item.get(entry.getKey()).equals(entry.getValue())) {
                        mismatchDescription.appendText("The mapping for ").appendValue(entry.getKey()).appendText(", ")
                                .appendValue(item.get(entry.getKey())).appendText(" is not equal to the expected one, ")
                                .appendValue(entry.getValue());
                        return false;
                    }
                }
                return true;
            }
        };

    }

    /**
     * @return suppliers for all attributes
     */
    private static Map<String, Supplier<Object>> getValueSuppliers() {
        final Map<String, Supplier<Object>> valueSuppliers = new HashMap<>();
        final Supplier<Object> stringSupplier = () -> {
            return UUID.randomUUID().toString();
        };
        final Supplier<Object> booleanSupplier = () -> {
            return Math.random() < 0.5;
        };
        final Supplier<Object> double100Supplier = () -> {
            return Math.random() * 100;
        };
        final Supplier<Object> double1Supplier = () -> {
            return Math.random();
        };
        valueSuppliers.put(SoMoXConfiguration.BLACKLIST_CONFIGURATION_WILDCARDS_ADDITIONAL, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_ANALYZER_INPUT_FILE, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_ANALYZER_REVERSE_ENGINEER_INTERFACES_NOT_ASSIGNED_TO_INTERFACES,
                booleanSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_ANALYZER_WILDCARD_KEY, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_EXCLUDED_PREFIXES, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_EXCLUDED_SUFFIXES, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_OUTPUT_FOLDER, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_PROJECT_NAME, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_COMPOSE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_MERGE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_COMPOSE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_MERGE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_COMPOSE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_MERGE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_DIRECTORY_MAPPING, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_DMS, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_COUPLING, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_NAME_RESEMBLANCE, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_SLAQ, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGHEST_NAME_RESEMBLANCE, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_INTERFACE_VIOLATION_IRRELEVANT, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_INTERFACE_VIOLATION_RELEVANT, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_COUPLING, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_NAME_RESEMBLANCE, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_SLAQ, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_MID_NAME_RESEMBLANCE, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_PACKAGE_MAPPING, double100Supplier);

        return valueSuppliers;
    }

    /**
     * @return a mapping from attribute keys to setters
     */
    private static Map<String, BiConsumer<SoMoXConfiguration, Object>> getKeyToSettersMapping() {
        final Map<String, BiConsumer<SoMoXConfiguration, Object>> keysToSetters = new HashMap<>();

        keysToSetters.put(SoMoXConfiguration.BLACKLIST_CONFIGURATION_WILDCARDS_ADDITIONAL,
                (final SoMoXConfiguration c, final Object s) -> {
                    c.setAdditionalWildcards((String) s);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_ANALYZER_REVERSE_ENGINEER_INTERFACES_NOT_ASSIGNED_TO_INTERFACES,
                (final SoMoXConfiguration c, final Object b) -> {
                    c.setReverseEngineerInterfacesNotAssignedToComponent((Boolean) b);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_ANALYZER_INPUT_FILE,
                (final SoMoXConfiguration c, final Object s) -> {
                    c.getFileLocations().setAnalyserInputFile((String) s);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_ANALYZER_WILDCARD_KEY,
                (final SoMoXConfiguration c, final Object s) -> {
                    c.setWildcardKey((String) s);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_EXCLUDED_PREFIXES, (final SoMoXConfiguration c, final Object s) -> {
            c.setExcludedPrefixesForNameResemblance((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_EXCLUDED_SUFFIXES, (final SoMoXConfiguration c, final Object s) -> {
            c.setExcludedSuffixesForNameResemblance((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_OUTPUT_FOLDER, (final SoMoXConfiguration c, final Object s) -> {
            c.getFileLocations().setOutputFolder((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_PROJECT_NAME, (final SoMoXConfiguration c, final Object s) -> {
            c.getFileLocations().setProjectName((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_COMPOSE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.getClusteringConfig().setClusteringComposeThresholdDecrement((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_MERGE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.getClusteringConfig().setClusteringMergeThresholdDecrement((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_COMPOSE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.getClusteringConfig().setMaxComposeClusteringThreshold((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_MERGE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.getClusteringConfig().setMaxMergeClusteringThreshold((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_COMPOSE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.getClusteringConfig().setMinComposeClusteringThreshold((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_MERGE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.getClusteringConfig().setMinMergeClusteringThreshold((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_DIRECTORY_MAPPING,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightDirectoryMapping((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_DMS, (final SoMoXConfiguration c, final Object d) -> {
            c.setWeightDMS((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_COUPLING,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightHighCoupling((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_NAME_RESEMBLANCE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightHighNameResemblance((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_SLAQ, (final SoMoXConfiguration c, final Object d) -> {
            c.setWeightHighSLAQ((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGHEST_NAME_RESEMBLANCE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightHighestNameResemblance((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_INTERFACE_VIOLATION_IRRELEVANT,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightInterfaceViolationIrrelevant((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_INTERFACE_VIOLATION_RELEVANT,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightInterfaceViolationRelevant((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_COUPLING,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightLowCoupling((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_NAME_RESEMBLANCE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightLowNameResemblance((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_SLAQ, (final SoMoXConfiguration c, final Object d) -> {
            c.setWeightLowSLAQ((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_MID_NAME_RESEMBLANCE,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightMidNameResemblance((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_PACKAGE_MAPPING,
                (final SoMoXConfiguration c, final Object d) -> {
                    c.setWeightPackageMapping((Double) d);
                });

        return keysToSetters;
    }
}
