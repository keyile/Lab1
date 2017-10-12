package lab_1;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jgrapht.graph.*;
import org.jgrapht.*;
import org.jgrapht.event.*;

import com.mxgraph.model.*;
import com.mxgraph.view.*;

import com.mxgraph.layout.*;
import com.mxgraph.swing.*;


public class GraphVisualization
    extends JApplet implements ActionListener
{
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 600);
    private static JButton button;
    private static JFrame frame;
    private static BufferedImage  bi;

    private JGraphXAdapter<String, DefaultWeightedEdge> jgxAdapter;


    public static void printGraph(ArrayList<String> vertexList, int[][] edges)
    {
    	GraphVisualization applet = new GraphVisualization();
        applet.init(vertexList, edges);
        applet.initButton();

        frame = new JFrame();
        frame.setTitle("Visualization");
        frame.getContentPane().add(applet);
        frame.getContentPane().add(button, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    	bi = new BufferedImage(applet.getWidth(), applet.getHeight(), BufferedImage.TYPE_INT_ARGB);
    	Graphics2D  save = bi.createGraphics();
    	applet.paint(save);
    }
    
    public void initButton()
    {
        button = new JButton("保存");
        button.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e){	
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "JPG & PNG Images", "jpg", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	try {
    			ImageIO.write(bi, "PNG", chooser.getSelectedFile());
    		} catch (IOException e1) {
    			e1.printStackTrace();
    		}
        }
    }
    
    public void init(ArrayList<String> vertexList, int[][] edges)
    {
        // create a JGraphT graph
        ListenableDirectedWeightedGraph<String, DefaultWeightedEdge> g =
            new ListenableDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        
        // add some sample data (graph manipulated via JGraphX)
        for(String s: vertexList) 
        	g.addVertex(s);
        
        for(int i = 0; i < vertexList.size(); i++)
        	for(int j = 0; j < vertexList.size(); j++) {
        		if(edges[i][j] != Integer.MAX_VALUE && edges[i][j] != 0) {
        			String v1 = vertexList.get(i);
        			String v2 = vertexList.get(j);
        			g.addEdge(v1, v2);
        			g.setEdgeWeight(g.getEdge(v1, v2), edges[i][j]);
        		}
        	}
        
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(g);

        getContentPane().add(new mxGraphComponent(jgxAdapter));
        resize(DEFAULT_SIZE);
        
        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());

        // that's all there is to it!...
    }
}

class JGraphXAdapter<V, E>
extends mxGraph
implements GraphListener<V, E>
{
/**
 * The graph to be drawn. Has vertices "V" and edges "E".
 */
private Graph<V, E> graphT;

/**
 * Maps the JGraphT-Vertices onto JGraphX-mxICells. {@link #cellToVertexMap} is for the opposite
 * direction.
 */
private HashMap<V, mxICell> vertexToCellMap = new HashMap<>();

/**
 * Maps the JGraphT-Edges onto JGraphX-mxICells. {@link #cellToEdgeMap} is for the opposite
 * direction.
 */
private HashMap<E, mxICell> edgeToCellMap = new HashMap<>();

/**
 * Maps the JGraphX-mxICells onto JGraphT-Edges. {@link #edgeToCellMap} is for the opposite
 * direction.
 */
private HashMap<mxICell, V> cellToVertexMap = new HashMap<>();

/**
 * Maps the JGraphX-mxICells onto JGraphT-Vertices. {@link #vertexToCellMap} is for the opposite
 * direction.
 */
private HashMap<mxICell, E> cellToEdgeMap = new HashMap<>();

/**
 * Constructs and draws a new ListenableGraph. If the graph changes through as ListenableGraph,
 * the JGraphXAdapter will automatically add/remove the new edge/vertex as it implements the
 * GraphListener interface. Throws a IllegalArgumentException if the graph is null.
 *
 * @param graph casted to graph
 */
public JGraphXAdapter(ListenableGraph<V, E> graph)
{
    // call normal constructor with graph class
    this((Graph<V, E>) graph);

    graph.addGraphListener(this);
}

/**
 * Constructs and draws a new mxGraph from a jGraphT graph. Changes on the jgraphT graph will
 * not edit this mxGraph any further; use the constructor with the ListenableGraph parameter
 * instead or use this graph as a normal mxGraph. Throws an IllegalArgumentException if the
 * parameter is null.
 *
 * @param graph is a graph
 */
public JGraphXAdapter(Graph<V, E> graph)
{
    super();

    // Don't accept null as jgrapht graph
    if (graph == null) {
        throw new IllegalArgumentException();
    } else {
        this.graphT = graph;
    }

    // generate the drawing
    insertJGraphT(graph);

    setAutoSizeCells(true);
}

/**
 * Returns Hashmap which maps the vertices onto their visualization mxICells.
 *
 * @return {@link #vertexToCellMap}
 */
public HashMap<V, mxICell> getVertexToCellMap()
{
    return vertexToCellMap;
}

/**
 * Returns Hashmap which maps the edges onto their visualization mxICells.
 *
 * @return {@link #edgeToCellMap}
 */
public HashMap<E, mxICell> getEdgeToCellMap()
{
    return edgeToCellMap;
}

/**
 * Returns Hashmap which maps the visualization mxICells onto their edges.
 *
 * @return {@link #cellToEdgeMap}
 */
public HashMap<mxICell, E> getCellToEdgeMap()
{
    return cellToEdgeMap;
}

/**
 * Returns Hashmap which maps the visualization mxICells onto their vertices.
 *
 * @return {@link #cellToVertexMap}
 */
public HashMap<mxICell, V> getCellToVertexMap()
{
    return cellToVertexMap;
}

@Override
public void vertexAdded(GraphVertexChangeEvent<V> e)
{
    addJGraphTVertex(e.getVertex());
}

@Override
public void vertexRemoved(GraphVertexChangeEvent<V> e)
{
    mxICell cell = vertexToCellMap.remove(e.getVertex());
    removeCells(new Object[] { cell });

    // remove vertex from hashmaps
    cellToVertexMap.remove(cell);
    vertexToCellMap.remove(e.getVertex());

    // remove all edges that connected to the vertex
    ArrayList<E> removedEdges = new ArrayList<>();

    // first, generate a list of all edges that have to be deleted
    // so we don't change the cellToEdgeMap.values by deleting while
    // iterating
    // we have to iterate over this because the graphT has already
    // deleted the vertex and edges so we can't query what the edges were
    for (E edge : cellToEdgeMap.values()) {
        if (!graphT.edgeSet().contains(edge)) {
            removedEdges.add(edge);
        }
    }

    // then delete all entries of the previously generated list
    for (E edge : removedEdges) {
        removeEdge(edge);
    }
}

@Override
public void edgeAdded(GraphEdgeChangeEvent<V, E> e)
{
    addJGraphTEdge(e.getEdge());
    System.out.println("changed!");
}

@Override
public void edgeRemoved(GraphEdgeChangeEvent<V, E> e)
{
    removeEdge(e.getEdge());
}

/**
 * Removes a jgrapht edge and its visual representation from this graph completely.
 *
 * @param edge The edge that will be removed
 */
private void removeEdge(E edge)
{
    mxICell cell = edgeToCellMap.remove(edge);
    removeCells(new Object[] { cell });

    // remove edge from hashmaps
    cellToEdgeMap.remove(cell);
    edgeToCellMap.remove(edge);
}

/**
 * Draws a new vertex into the graph.
 *
 * @param vertex vertex to be added to the graph
 */
private void addJGraphTVertex(V vertex)
{
    getModel().beginUpdate();

    try {
        // create a new JGraphX vertex at position 0
        mxICell cell = (mxICell) insertVertex(defaultParent, null, vertex, 0, 0, 0, 0, "shape=ellipse;perimeter=ellipsePerimeter");

        // update cell size so cell isn't "above" graph
        updateCellSize(cell);

        // Save reference between vertex and cell
        vertexToCellMap.put(vertex, cell);
        cellToVertexMap.put(cell, vertex);
    } finally {
        getModel().endUpdate();
    }
}

/**
 * Draws a new egde into the graph.
 *
 * @param edge edge to be added to the graph. Source and target vertices are needed.
 */
private void addJGraphTEdge(E edge)
{
    getModel().beginUpdate();

    try {
        // find vertices of edge
        V sourceVertex = graphT.getEdgeSource(edge);
        V targetVertex = graphT.getEdgeTarget(edge);

        // if the one of the vertices is not drawn, don't draw the edge
        if (!(vertexToCellMap.containsKey(sourceVertex)
            && vertexToCellMap.containsKey(targetVertex)))
        {
            return;
        }

        // get mxICells
        Object sourceCell = vertexToCellMap.get(sourceVertex);
        Object targetCell = vertexToCellMap.get(targetVertex);

        // add edge between mxICells
        mxICell cell = (mxICell) insertEdge(defaultParent, null, (int)graphT.getEdgeWeight(edge), sourceCell, targetCell);

        // update cell size so cell isn't "above" graph
        updateCellSize(cell);

        // Save reference between vertex and cell
        edgeToCellMap.put(edge, cell);
        cellToEdgeMap.put(cell, edge);
    } finally {
        getModel().endUpdate();
    }
}

/**
 * Draws a given graph with all its vertices and edges.
 *
 * @param graph the graph to be added to the existing graph.
 */
private void insertJGraphT(Graph<V, E> graph)
{
    for (V vertex : graph.vertexSet()) {
        addJGraphTVertex(vertex);
    }

    for (E edge : graph.edgeSet()) {
        addJGraphTEdge(edge);
    }
}
}

//End JGraphXAdapter.java
