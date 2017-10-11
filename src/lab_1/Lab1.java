package lab_1;

import java.io.*;
import java.util.*;

public class Lab1 {
	private static DirectedGraph graph;
	private static String[] words;
	private static Scanner sc = new Scanner(System.in);
	private static boolean graphReady; // flag that graph has been build

	public static class DirectedGraph {
		private ArrayList<String> vertexList;
		private int[][] edges;

		public DirectedGraph(int n) {
			edges = new int[n][n];
			// fill the array
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++)
					if (i == j)
						edges[i][j] = 0;
					else
						edges[i][j] = Integer.MAX_VALUE;
			}
			vertexList = new ArrayList<String>(n);
			graphReady = false;
		}

		public DirectedGraph(String[] words) {
			this(words.length);
			for (int i = 0; i < words.length; i++) {
				if (locateVertex(words[i]) == -1)
					insertVertex(words[i]);
			}
			int v1, v2, newEdge;
			for (int i = 0; i < words.length - 1; i++) {
				v1 = locateVertex(words[i]);
				v2 = locateVertex(words[i + 1]);
				newEdge = (getWeight(v1, v2) == Integer.MAX_VALUE) ? 1 : getWeight(v1, v2) + 1;
				insertEdge(v1, v2, newEdge);
			}
		}

		public String getWord(int n) {
			return vertexList.get(n);
		}

		public void insertVertex(String name) {
			vertexList.add(name);
		}

		public void insertEdge(int v1, int v2, int weight) {
			edges[v1][v2] = weight;
		}

		public int getWeight(int v1, int v2) {
			return edges[v1][v2];
		}

		public boolean hasEdge(int v1, int v2) {
			if (v1 == v2)
				return edges[v1][v2] != 0;
			else
				return edges[v1][v2] != Integer.MAX_VALUE;
		}

		public int locateVertex(String vertexName) {
			return vertexList.indexOf(vertexName);
		}

		public ArrayList<String> findNext(String word) {
			ArrayList<String> NWords = new ArrayList<String>(getVertexSize());
			int v = locateVertex(word.toLowerCase());
			for (int k = 0; k < getVertexSize(); k++) {
				if (getWeight(v, k) >= 1 && v != k && getWeight(v, k) < Integer.MAX_VALUE) {
					NWords.add(getWord(k));
				}
			}
			return NWords;
		}

		public int getVertexSize() {
			return vertexList.size();
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer("Vertices:\n\t");
			for (String v : vertexList) {
				sb.append(v + ", ");
			}
			sb.setLength(sb.length() - 2);
			sb.append("\nAdjacency list: ");
			for (int i = 0; i < vertexList.size(); i++) {
				sb.append("\n\t" + getWord(i));
				for (int j = 0; j < getVertexSize(); j++) {
					if (edges[i][j] >= 1 && edges[i][j] < Integer.MAX_VALUE)
						sb.append(" --> " + getWord(j) + "(" + edges[i][j] + ")");
				}
			}
			return sb.toString();
		}

		public void printGraph() {
			GraphVisualization.printGraph(vertexList, edges);
		}
	}

	public static void main(String[] args) {
		System.out.println(
				"\t\t\t\t\t(๑•̀ㅂ•́) ✧Welcome  \n" + "\t\t\tPlease select the corresponding option as required : ");
		printMenu();
		System.out.print("\nCommand (Enter h for help): ");
		char choice;
		String word3 = null;
		String word4 = null;
		while (sc.hasNext()) {
			choice = sc.next().charAt(0);
			sc.nextLine();
			switch (choice) {
			case '1':
				System.out.print("Select and open the file: ");
				String fileName = sc.nextLine();
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(fileName));
				} catch (FileNotFoundException e) {
					System.out.println("ERROR: Can't access the file.");
					System.out.println(e);
					break;
				}
				System.out.print("OK: The target file is found.\n> > ");
				String fileLine;
				StringBuffer newText = new StringBuffer();
				try {
					while ((fileLine = br.readLine()) != null) {
						System.out.println('\t' + fileLine);
						for (int i = 0; i < fileLine.length(); i++) {
							char c = fileLine.charAt(i);
							if (c >= 'A' && c <= 'Z') {
								char b = Character.toLowerCase(c);
								newText.append(b);
							}
							if ((c >= 'a' && c <= 'z') || c == ' ') {
								newText.append(c);
							} else if (c == ',' || c == '.' || c == '!' || c == '?') {
								newText.append(' ');
							}
						}
						newText.append(' ');
					}
					br.close();
				} catch (IOException e) {
					System.out.println("ERROR: Can't read the content.");
					System.out.println(e);
				}
				words = newText.toString().trim().split("\\s+");
				graph = new DirectedGraph(words);
				graphReady = true; // Graph is gotten.
				break;
			case '2':
				if (!graphReady) {
					System.out.println("ERROR: Please open a file first.");
					break;
				}
				System.out.print("Do you want the Graph view (default) or the Text view? (g or t) ");
				String way = sc.nextLine();
				if (way.equals("t")) {
					showGraph(graph, 0);
				} else
					showGraph(graph, 1);
				break;
			case '3':
				if (!graphReady) {
					System.out.println("ERROR: Please open a file first.");
					break;
				}
				System.out.println(
						"Please enter two words(eg: apple tasty). The first two will be taken if there're more than two:");
				String[] a = (sc.nextLine()).split("\\s+");
				if (a.length < 2) {
					System.out.println("ERROR: No enough words. Check the input.");
					break;
				}
				word3 = a[0];
				word4 = a[1];
				System.out.print(queryBridgeWords(word3, word4));
				break;
			case '4':
				if (!graphReady) {
					System.out.println("ERROR: Please open a file first.");
					break;
				}
				System.out.println("Please enter a sentence(eg: many girls one two three) :");
				String line2 = sc.nextLine();
				if (line2.isEmpty()) {
					System.out.println("ERROR: Can't read the sentence. Check the input.");
					break;
				}
				System.out.println("> > " + generateNewText(line2));
				break;
			case '5':
				if (!graphReady) {
					System.out.println("ERROR: Please open a file first.");
					break;
				}
				System.out.println(
						"Please enter one or two words(eg: hungry / eat noodles). The first two will be taken if there're more than two:");
				String[] a1 = (sc.nextLine()).split("\\s+");
				if (a1.length >= 2) {
					word3 = a1[0];
					word4 = a1[1];
					System.out.print(calcShortestPath(word3, word4));
					break;
				} else {
					word3 = a1[0];
					if (word3.equals("")) {
						System.out.println("ERROR: No words found. Check the input.");
						break;
					}
					System.out.print(calcShortestPath(word3));
					break;
				}
			case '6':
				if (!graphReady) {
					System.out.println("ERROR: Please open a file first.");
					break;
				}
				System.out.println("Here comes the random walk; Enter 's' to force it stop.");
				String randomText = randomWalk();
				System.out
						.println("\nThe Random Path: \n> > " + randomText + "\nWhere do you want to save this text? ");
				String filename = sc.nextLine();
				if(filename.isEmpty())
					break;
				try {
					PrintWriter pw = new PrintWriter(filename);
					pw.println(randomText);
					pw.flush();
					pw.close();
				} catch (FileNotFoundException e) {
					System.out.println("ERROR: Can't save the file. ");
					System.out.println(e);
				}
				break;
			case '0':
				System.out.println("Welcome next time !");
				System.exit(0);
			case 'h':
				printMenu();
				break;
			default:
				System.out.println("ERROR: Can't make this choice.");
			}
			System.out.print("\nCommand (Enter h for help or else): ");
		}
		sc.close();
	}

	static void printMenu() {
		System.out.println("\t\t\t1.Enter the file path and name (eg.F:/test.txt)");
		System.out.println("\t\t\t2.Show the diagraph");
		System.out.println("\t\t\t3.Query the bridge words between two words (eg:hello world)");
		System.out.println("\t\t\t4.complete the sentence by bridge words(eg:hello world one two three)");
		System.out.println("\t\t\t5.calculate the shortest path by one word or two words(eg:hello world/hello)");
		System.out.println("\t\t\t6.Walk randomly");
		System.out.println("\t\t\t0.exit");
	}

	static void showGraph(DirectedGraph G, int way) {
		if (way == 0) {
			System.out.println(G.toString());
		} else
			G.printGraph();
	}

	static ArrayList<String> findBridgeWords(String word1, String word2) {
		ArrayList<String> bridgeWords = new ArrayList<String>(words.length);
		int v3 = graph.locateVertex(word1.toLowerCase());
		int v4 = graph.locateVertex(word2.toLowerCase());
		if (v3 == -1 || v4 == -1) {
			return bridgeWords;
		}
		for (int k = 0; k < words.length; k++) {
			if (graph.hasEdge(v3, k)) {
				if (graph.hasEdge(k, v4)) {
					bridgeWords.add(graph.getWord(k));
				}
			}
		}
		return bridgeWords;
	}

	static String queryBridgeWords(String word1, String word2) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream output = new PrintStream(bos);
		int v1 = graph.locateVertex(word1.toLowerCase());
		int v2 = graph.locateVertex(word2.toLowerCase());
		ArrayList<String> bridgeWords = findBridgeWords(word1.toLowerCase(), word2.toLowerCase());

		if (bridgeWords.isEmpty()) {
			if (v1 == -1 && v2 == -1)
				output.println("ERROR: No \"" + word1 + "\" and \"" + word2 + "\" in the graph!");
			else if (v1 == -1 && v2 != -1)
				output.println("ERROR: No \"" + word1 + "\" in the graph!");
			else if (v1 != -1 && v2 == -1)
				output.println("ERROR: No \"" + word2 + "\" in the graph!");
			else {
				output.println("ERROR: No bridge words form \"" + word1 + "\" to \"" + word2 + "\"!");
			}
		} else {
			output.print("The bridge words from \"" + word1 + "\" to \"" + word2 + "\" is: " + bridgeWords.get(0));
			for (int i = 1; i < bridgeWords.size(); i++)
				output.print(", " + bridgeWords.get(i));
		}
		return (bos.toString());
	}

	static String generateNewText(String inputText) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream output = new PrintStream(bos);
		String[] word1 = new String[words.length];
		word1 = inputText.split(" ");
		output.print(word1[0] + " ");
		for (int i = 0; i < word1.length - 1; i++) {
			ArrayList<String> bridgeWords = findBridgeWords(word1[i].toLowerCase(), word1[i + 1].toLowerCase());
			if (bridgeWords.isEmpty()) {
				output.print(word1[i + 1] + " ");
			} else {
				int n = bridgeWords.size();
				Random random = new Random();
				int s = random.nextInt(n);
				output.print(bridgeWords.get(s) + " ");
				output.print(word1[i + 1] + " ");
			}
		}
		return bos.toString();
	}

	static String calcShortestPath(String word1, String word2) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream output = new PrintStream(bos);
		int num = graph.getVertexSize();
		int v5 = graph.locateVertex(word1.toLowerCase());
		int v6 = graph.locateVertex(word2.toLowerCase());
		if (v5 == -1 && v6 == -1)
			return ("ERROR: No \"" + word1 + "\" and \"" + word2 + "\" in the graph!");
		else if (v5 == -1 && v6 != -1)
			return ("ERROR: No \"" + word1 + "\" in the graph!");
		else if (v5 != -1 && v6 == -1)
			return ("ERROR: No \"" + word2 + "\" in the graph!");
		int mindis;
		int mindisIndex = v5;
		int[] distance = new int[num];
		int[] path = new int[num];
		boolean[] sign1 = new boolean[num];
		for (int i = 0; i < num; i++) {
			distance[i] = graph.edges[v5][i];
			sign1[i] = false;
			path[i] = v5;
		}
		distance[v5] = 0;
		sign1[v5] = true;
		for (int i = 0; i < num; i++) {
			mindis = Integer.MAX_VALUE;
			for (int j = 0; j < num; j++) {
				if (sign1[j] == false && distance[j] < mindis) {
					mindis = distance[j];
					mindisIndex = j;
				}
			}
			sign1[mindisIndex] = true;
			for (int j = 0; j < num; j++) {
				if (sign1[j] == false && graph.edges[mindisIndex][j] != Integer.MAX_VALUE) {
					if (graph.edges[mindisIndex][j] + mindis < distance[j]) {
						distance[j] = graph.edges[mindisIndex][j] + mindis;
						path[j] = mindisIndex;
					}
				}
			}
		}
		for (int i = 0; i < num; i++) {
			if (i == v6) {
				output.print("from \"" + word1 + "\" to \"" + word2 + "\" : ");
				StringBuffer line2 = new StringBuffer();
				if (distance[i] == Integer.MAX_VALUE) {
					return ("from \"" + word1 + "\" to \"" + word2 + "\" : Unreachable\n");
				} else {
					line2.append(graph.getWord(i));
					int t = path[i];
					while (t != v5) {
						line2.insert(0, graph.getWord(t) + " --> ");
						t = path[t];
					}
					line2.insert(0, word1 + " --> ");
				}
				output.println(line2.toString());
			}
		}
		return bos.toString();
	}

	static String calcShortestPath(String word1) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream output = new PrintStream(bos);
		int v7 = graph.locateVertex(word1.toLowerCase());
		if (v7 == -1) {
			return ("ERROR: No \"" + word1 + "\" in the graph!");
		}
		for (int i = 0; i < graph.getVertexSize(); i++) {
			if (i != graph.locateVertex(word1)) {
				output.print(calcShortestPath(word1, graph.getWord(i)));
			}
		}
		return bos.toString();
	}

	static String randomWalk() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream output = new PrintStream(bos);
		ArrayList<String> nextWordsList;
		ArrayList<int[]> edgePairList = new ArrayList<int[]>(words.length);
		Random random = new Random();

		int s = random.nextInt(graph.getVertexSize()); // 随机选中第一个词
		String preWord = graph.getWord(s);
		String nextWord = null;
		System.out.println(preWord);
		output.append(preWord + " ");

		while (true) {
			String choice = sc.nextLine();
			if (choice.equals("s")) {
				System.out.println("DONE: User command is received.");
				return bos.toString();
			} else {
				nextWordsList = graph.findNext(preWord);
				if (nextWordsList.size() <= 0) {
					System.out.println("DONE: No word comes from this one.");
					return bos.toString();
				} else {
					int m = random.nextInt(nextWordsList.size());
					nextWord = nextWordsList.get(m);
					System.out.println(nextWord);
					output.append(nextWord + " ");
					int[] edgePair = new int[] { graph.locateVertex(preWord), m };
					for (int[] tempPair : edgePairList)
						if (tempPair[0] == edgePair[0] && tempPair[1] == edgePair[1]) {
							System.out.println("DONE: Have been to this edge.");
							return bos.toString();
						}
					edgePairList.add(edgePair);
					preWord = nextWord;
				}
			} // else
		} // while
	}

}
