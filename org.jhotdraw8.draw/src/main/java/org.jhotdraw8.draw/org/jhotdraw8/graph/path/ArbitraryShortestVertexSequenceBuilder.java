package org.jhotdraw8.graph.path;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This {@link VertexSequenceBuilder} uses Diijkstra's 'shortest path' algorithm.
 * If more than one shortest path is possible, the builder returns an arbitrary
 * one.
 */
public class ArbitraryShortestVertexSequenceBuilder<V, C extends Number & Comparable<C>> extends AbstractShortestVertexSequenceBuilder<V, C> {

    public ArbitraryShortestVertexSequenceBuilder(@NonNull C zero, @NonNull C positiveInfinity, @NonNull C maxCost, @NonNull Function<V, Iterable<V>> nextVerticesFunction, @NonNull BiFunction<V, V, C> costFunction, @NonNull BiFunction<C, C, C> sumFunction) {
        super(zero, positiveInfinity, maxCost, nextVerticesFunction, costFunction, sumFunction);
    }


    @Override
    protected @Nullable BackLink<V, C> search(
            @NonNull Iterable<V> starts,
            @NonNull Predicate<V> goalPredicate,
            @NonNull C zero,
            @NonNull C positiveInfinity,
            @NonNull C maxCost,
            @NonNull Function<V, Iterable<V>> nextf,
            @NonNull BiFunction<V, V, C> costf,
            @NonNull BiFunction<C, C, C> sumf) {
        // Priority queue: back-links with shortest distance from start come first.
        PriorityQueue<BackLink<V, C>> queue = new PriorityQueue<>();

        // Map with best known costs from start to a specific vertex. If an entry is missing, we assume infinity.
        Map<V, C> costMap = new HashMap<>();

        // Insert start itself in priority queue and initialize its cost as 0.
        for (V start : starts) {
            queue.add(new BackLink<>(start, null, zero));
            costMap.put(start, zero);
        }

        // Loop until we have reached the goal, or queue is exhausted.
        while (!queue.isEmpty()) {
            BackLink<V, C> node = queue.remove();
            final V u = node.getVertex();
            if (goalPredicate.test(u)) {
                return node;
            }
            C costToU = node.getCost();

            for (V v : nextf.apply(u)) {
                C bestKnownCost = costMap.getOrDefault(v, positiveInfinity);
                C costThroughU = sumf.apply(costToU, costf.apply(u, v));

                // If there is a shorter path to v through u.
                if (costThroughU.compareTo(bestKnownCost) < 0 && costThroughU.compareTo(maxCost) <= 0) {
                    // Update cost to v.
                    costMap.put(v, costThroughU);
                    queue.add(new BackLink<>(v, node, costThroughU));
                }
            }
        }

        return null;
    }
}
