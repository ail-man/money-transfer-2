package com.ail.home.algorithms;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;

class DFSTest {

	@Test
	void testDFS() {
		final Graph graph = new Graph(4);

		graph.addEdge(0, 1);
		graph.addEdge(0, 2);
		graph.addEdge(1, 2);
		graph.addEdge(2, 0);
		graph.addEdge(2, 3);
		graph.addEdge(3, 3);

		System.out.println("Following is Depth First Traversal (starting from vertex 2)");

		graph.dfs(3);
	}

	static class Graph {
		private final int vertexCount;
		private final LinkedList<Integer>[] adjacencyList;

		@SuppressWarnings("unchecked")
		Graph(final int vertexCount) {
			this.vertexCount = vertexCount;
			this.adjacencyList = new LinkedList[vertexCount];
			for (int i = 0; i < vertexCount; ++i) {
				this.adjacencyList[i] = new LinkedList<>();
			}
		}

		void addEdge(final int vertexIndex, final int adjacentVertexIndex) {
			adjacencyList[vertexIndex].add(adjacentVertexIndex);
		}

		void dfs(final int vertexIndex, final boolean[] visitedArray) {
			visitedArray[vertexIndex] = true;
			System.out.print(vertexIndex + " ");
			for (final int adjacentVertexIndex : adjacencyList[vertexIndex]) {
				if (!visitedArray[adjacentVertexIndex]) {
					dfs(adjacentVertexIndex, visitedArray);
				}
			}
		}

		void dfs(final int startingVertexIndex) {
			final boolean[] visitedArray = new boolean[this.vertexCount];
			dfs(startingVertexIndex, visitedArray);
		}
	}
}
