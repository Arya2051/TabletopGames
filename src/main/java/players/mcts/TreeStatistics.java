package players.mcts;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class TreeStatistics {

    final public int maxDepth = 100;
    final public int depthReached;
    final public int[] nodesAtDepth = new int[maxDepth];
    final public int[] leavesAtDepth = new int[maxDepth];
    final public int[] gameTerminalNodesAtDepth = new int[maxDepth];
    final public int totalNodes;
    final public int totalLeaves;
    final public int totalTerminalNodes;
    final public double[] nodeDistribution;
    final public double[] leafDistribution;

    public TreeStatistics(SingleTreeNode root) {
        Queue<SingleTreeNode> nodeQueue = new ArrayDeque<>();
        nodeQueue.add(root);
        int greatestDepth = 0;
        while (!nodeQueue.isEmpty()) {
            SingleTreeNode node = nodeQueue.poll();
            if (node.depth < maxDepth) {
                nodesAtDepth[node.depth]++;
                if (!node.getState().isNotTerminal())
                    gameTerminalNodesAtDepth[node.depth]++;
                for (SingleTreeNode child : node.children.values().stream()
                        .filter(Objects::nonNull)
                        .flatMap(Arrays::stream)
                        .filter(Objects::nonNull)
                        .collect(toList())) {
                    if (child != null)
                        nodeQueue.add(child);
                }
                if (node.children.values().stream().allMatch(Objects::isNull))
                    leavesAtDepth[node.depth]++;
            }
            if (node.depth > greatestDepth)
                greatestDepth = node.depth;
        }

        depthReached = greatestDepth;
        totalNodes = Arrays.stream(nodesAtDepth).sum();
        totalLeaves = Arrays.stream(leavesAtDepth).sum();
        totalTerminalNodes = Arrays.stream(gameTerminalNodesAtDepth).sum();
        nodeDistribution = Arrays.stream(nodesAtDepth, 0, Math.min(depthReached + 1, maxDepth)).asDoubleStream().map(i -> i / totalNodes).toArray();
        leafDistribution = Arrays.stream(leavesAtDepth, 0, Math.min(depthReached + 1, maxDepth)).asDoubleStream().map(i -> i / totalLeaves).toArray();
    }



    @Override
    public String toString() {
        StringBuilder retValue = new StringBuilder();
        retValue.append(String.format("%d nodes and %d leaves, with maximum depth %d\n", totalNodes, totalLeaves, depthReached));
        List<String> nodeDist = Arrays.stream(nodeDistribution).mapToObj(n -> String.format("%2.0f%%", n * 100.0)).collect(toList());
        List<String> leafDist = Arrays.stream(leafDistribution).mapToObj(n -> String.format("%2.0f%%", n * 100.0)).collect(toList());
        retValue.append(String.format("\tNodes  by depth: %s\n", String.join(", ", nodeDist)));
        retValue.append(String.format("\tLeaves by depth: %s\n", String.join(", ", leafDist)));

        return retValue.toString();
    }
}
