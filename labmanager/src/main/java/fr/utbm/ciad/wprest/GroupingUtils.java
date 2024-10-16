package fr.utbm.ciad.wprest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Utility class for grouping elements in a list based on one or more attributes.
 * This class provides static methods to group a list of objects
 * using one, two, or three attributes, to
 * organize data in a list based on specific criteria.
 */
public class GroupingUtils {

    /**
     * Groups elements of a list by using a single attribute.
     *
     * @param dtos      the list of elements to be grouped
     * @param keyMapper1 a function that extracts the key from each element
     *                   used for grouping
     * @param <T>      the type of the elements in the list
     * @param <K1>     the type of the key produced by the keyMapper1 function
     * @return a map where the keys are the results of applying the
     *         keyMapper1 function, and the values are lists of elements
     *         that correspond to those keys
     */
    private static <T, K1> Map<K1, List<T>> groupBy1(
            List<T> dtos,
            Function<T, K1> keyMapper1) {

        return dtos.stream()
                .collect(Collectors.groupingBy(keyMapper1)); // Regroupement par un seul champ
    }


    /**
     * Groups elements of a list by using two attributes.
     *
     * @param dtos      the list of elements to be grouped
     * @param keyMapper1 a function that extracts the first key from each element
     *                   used for the first level of grouping
     * @param keyMapper2 a function that extracts the second key from each element
     *                   used for the second level of grouping
     * @param <T>      the type of the elements in the list
     * @param <K1>     the type of the first key produced by the keyMapper1 function
     * @param <K2>     the type of the second key produced by the keyMapper2 function
     * @return a map where the keys are the results of applying the
     *         keyMapper1 function, and the values are maps where the keys
     *         are the results of applying the keyMapper2 function, and
     *         the values are lists of elements that correspond to those keys
     */
    private static <T, K1, K2> Map<K1, Map<K2, List<T>>> groupBy2(
            List<T> dtos,
            Function<T, K1> keyMapper1,
            Function<T, K2> keyMapper2) {

        return dtos.stream()
                .collect(Collectors.groupingBy(keyMapper1,
                        Collectors.groupingBy(keyMapper2)));
    }


    /**
     * Groups elements of a list by using three attributes.
     *
     * @param dtos      the list of elements to be grouped
     * @param keyMapper1 a function that extracts the first key from each element
     *                   used for the first level of grouping
     * @param keyMapper2 a function that extracts the second key from each element
     *                   used for the second level of grouping
     * @param keyMapper3 a function that extracts the third key from each element
     *                   used for the third level of grouping
     * @param <T>      the type of the elements in the list
     * @param <K1>     the type of the first key produced by the keyMapper1 function
     * @param <K2>     the type of the second key produced by the keyMapper2 function
     * @param <K3>     the type of the third key produced by the keyMapper3 function
     * @return a map where the keys are the results of applying the
     *         keyMapper1 function, and the values are maps where the keys
     *         are the results of applying the keyMapper2 function, and
     *         the values are maps where the keys are the results of applying
     *         the keyMapper3 function, and the values are lists of elements
     *         that correspond to those keys
     */
    private static <T, K1, K2, K3> Map<K1, Map<K2, Map<K3, List<T>>>> groupBy3
            (List<T> dtos,
            Function<T, K1> keyMapper1,
            Function<T, K2> keyMapper2,
            Function<T, K3> keyMapper3) {

        return dtos.stream()
                .collect(Collectors.groupingBy(keyMapper1,
                        Collectors.groupingBy(keyMapper2,
                                Collectors.groupingBy(keyMapper3))));
    }
}
