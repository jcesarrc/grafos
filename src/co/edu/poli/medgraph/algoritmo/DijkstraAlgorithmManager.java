package co.edu.poli.medgraph.algoritmo;

import java.util.HashSet;
import java.util.Set;

public class DijkstraAlgorithmManager {

    private static final Set<AlgorithmProgressListener<DijkstraStepChanges>> listeners =
            new HashSet<AlgorithmProgressListener<DijkstraStepChanges>>();
    private static DijkstraShortestPath algo;
    private static AlgorithmProgressListener<DijkstraStepChanges> lis;
    private static boolean algoRunning = false;
    private static DijkstraStepChanges changes = null;

    public static void setAlgorithm(DijkstraShortestPath algo) {
        if (DijkstraAlgorithmManager.algo != null && lis != null) {
            DijkstraAlgorithmManager.algo.removeProgressListener(lis);
        }

        DijkstraAlgorithmManager.algo = algo;
        DijkstraAlgorithmManager.algo.addProgressListener(lis = new AlgorithmProgressListener<DijkstraStepChanges>() {
            @Override
            public void initialized(int maxSteps) {
                algoRunning = true;
                changes = null;
                for (final AlgorithmProgressListener<DijkstraStepChanges> l : listeners) {
                    l.initialized(maxSteps);
                }
            }

            @Override
            public void reset() {
                algoRunning = false;
                changes = null;
                for (final AlgorithmProgressListener<DijkstraStepChanges> l : listeners) {
                    l.reset();
                }
            }

            @Override
            public void stepChanged(int step, DijkstraStepChanges changes) {
                DijkstraAlgorithmManager.changes = changes;
                for (final AlgorithmProgressListener<DijkstraStepChanges> l : listeners) {
                    l.stepChanged(step, changes);
                }
            }
        });
    }

    public static void addAlgorithmProgressListener(AlgorithmProgressListener<DijkstraStepChanges> l) {
        listeners.add(l);
    }

    public static void removeAlgorithmProgressListener(AlgorithmProgressListener<DijkstraStepChanges> l) {
        listeners.remove(l);
    }

    public static boolean isAlgoRunning() {
        return algoRunning;
    }

    public static DijkstraStepChanges getCurrentStepChanges() {
        return changes;
    }

    public static DijkstraShortestPath getAlgorithm() {
        return algo;
    }
}
