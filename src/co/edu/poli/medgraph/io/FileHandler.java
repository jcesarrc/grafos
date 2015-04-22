
package co.edu.poli.medgraph.io;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.collections15.Transformer;
import org.jdesktop.application.Application;
import co.edu.poli.medgraph.DijkstraVisApp;
import co.edu.poli.medgraph.grafo.GraphManager;
import co.edu.poli.medgraph.grafo.IEdge;
import co.edu.poli.medgraph.grafo.IGraph;
import co.edu.poli.medgraph.grafo.INode;
import co.edu.poli.medgraph.grafo.impl.MyEdgeFactory;
import co.edu.poli.medgraph.grafo.impl.MyGraphFactory;
import co.edu.poli.medgraph.grafo.impl.MyNode;
import co.edu.poli.medgraph.grafo.impl.MyNodeFactory;
import co.edu.poli.medgraph.gui.GraphPanel;
import co.edu.poli.medgraph.util.SC;
import com.thoughtworks.xstream.XStream;

import edu.uci.ics.jung.graph.Graph;

public class FileHandler {

	private static final FileFilter GRAPH_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".zip");
		}

		@Override
		public String getDescription() {
			return SC.t("graph_files");
		}
	};
	
	private FileHandler() {		
	}

	private static final XStream xstream;

	static {
		xstream = new XStream();
		xstream.alias("list", LinkedList.class);
		xstream.alias("point", Point2D.Double.class);
	}
	
	public static boolean saveGraphToFile(IGraph graph, Transformer<INode, Point2D> layout, boolean dirty) {
		if (dirty && graph != null && graph.getNumberOfNodes() > 0) {
			int response = JOptionPane.showConfirmDialog(Application.getInstance(DijkstraVisApp.class).getMainFrame(), SC.t("save_graph_question"), null, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			switch (response) {
				case JOptionPane.YES_OPTION:
					saveFile(graph, layout);
					break;
				case JOptionPane.NO_OPTION:
					break;
				case JOptionPane.CANCEL_OPTION:
					return false;
			}
		}
		return true;
	}
	
	public static void openFile(IGraph oldGraph, Transformer<INode, Point2D> oldLayout, boolean dirty) {
		openFile(oldGraph, oldLayout, null, dirty);
	}


	@SuppressWarnings("unchecked")
	public static void openFile(IGraph oldGraph, Transformer<INode, Point2D> oldLayout, File file, boolean dirty) { 
		if (!saveGraphToFile(oldGraph, oldLayout, dirty)) return;

		final String dir = System.getProperty("user.dir");
		JFileChooser jfc = new JFileChooser(dir);
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileFilter(GRAPH_FILE_FILTER);
		
		if (file == null) {
			switch (jfc.showOpenDialog(Application.getInstance(DijkstraVisApp.class).getMainFrame())) {
				case JFileChooser.APPROVE_OPTION:
					file = jfc.getSelectedFile();
					break;
				case JFileChooser.CANCEL_OPTION:
				case JFileChooser.ERROR_OPTION:
				default:
					return;
			}
		}

		ZipFile zip = null;

		try {
			zip = new ZipFile(file);

			final IGraph graph = (IGraph) MyGraphFactory.getInstance().create();
			
			final Map<Integer, List<Integer>> connections = (Map<Integer, List<Integer>>) xstream.fromXML(zip.getInputStream(zip.getEntry("NodesAndConnections.xml")));
			final Map<Integer, Point2D> nodeLoc = (Map<Integer, Point2D>) xstream.fromXML(zip.getInputStream(zip.getEntry("NodeLocations.xml")));

			int startNode = Integer.MIN_VALUE;
			if (zip.getEntry("StartNode.xml") != null) {
				startNode = (Integer)xstream.fromXML(zip.getInputStream(zip.getEntry("StartNode.xml")));
			}

			MyEdgeFactory.getInstance().reset();
			MyNodeFactory.getInstance().reset();

			final Map<Integer, INode> nodes = new HashMap<Integer, INode>();
			int max = 0;
			for (final Integer v : connections.keySet()) {
				if (v > max)
					max = v;
				final MyNode node = new MyNode(v);
				nodes.put(v, node);
				graph.addNode(node);
				if (v == startNode) {
					node.setAttribute(INode.Attribute.START_NODE);
					graph.setStart(node);
				}
			}
			MyNodeFactory.getInstance().setIdCounter(max + 1);

			for (final Integer v : connections.keySet()) {
				for (final Integer u : connections.get(v)) {
					graph.addEdge(MyEdgeFactory.getInstance().create(), nodes.get(v), nodes.get(u));
				}
			}

			Transformer<INode, Point2D> layout = GraphPanel.getInstance().newLayout((Graph<INode, IEdge>)graph);
			for (final INode v : graph.getNodes()) {
				final Point2D p = nodeLoc.get(v.getId());
				layout.transform(v).setLocation(p);
			}
			
			if (zip.getEntry("IntermediateNodes.xml") != null) {
				final List<Integer> intermediate = (List<Integer>) xstream.fromXML(zip.getInputStream(zip.getEntry("IntermediateNodes.xml")));
				for (final Integer v : intermediate) {
					nodes.get(v).setIntermediate(true);
				}
			}
			
			if (zip.getEntry("NodeNames.xml") != null) {
				final Map<Integer, String> names = (Map<Integer, String>) xstream.fromXML(zip.getInputStream(zip.getEntry("NodeNames.xml")));
				for (final Integer v : names.keySet()) {
					nodes.get(v).setName(names.get(v));
				}
			}

			boolean foundBG = false;
			Enumeration<? extends ZipEntry> e = zip.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();
				if (ze.getName().endsWith(".png")) {
					GraphPanel.getInstance().setGraphBackground(ImageIO.read(zip.getInputStream(ze)));
					foundBG = true;
					break;
				}
			}
			if (!foundBG)
				GraphPanel.getInstance().setGraphBackground(null);
			
			GraphManager.setGraphAndLayout(graph, layout);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Application.getInstance(DijkstraVisApp.class).getMainFrame(), SC.t("error_file_load"), SC.t("error"), JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				if (zip != null) zip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean saveFile(IGraph graph, Transformer<INode, Point2D> layout) {
		
		final String dir = System.getProperty("user.dir");;
		JFileChooser jfc = new JFileChooser(dir);
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileFilter(GRAPH_FILE_FILTER);
		
		File file = null;
		switch (jfc.showSaveDialog(Application.getInstance(DijkstraVisApp.class).getMainFrame())) {
			case JFileChooser.APPROVE_OPTION:
				file = jfc.getSelectedFile();
				break;
			case JFileChooser.CANCEL_OPTION:
			case JFileChooser.ERROR_OPTION:
			default:
				return false;
		}
		
		if (!file.getName().endsWith(".zip")) {
			file = new File(file.getAbsoluteFile()+".zip");
		}
		
		ZipOutputStream out = null;

		try {
			out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

			final Map<Integer, Point2D> nodeLoc = new HashMap<Integer, Point2D>();
			final Map<Integer, Collection<Integer>> connections = new HashMap<Integer, Collection<Integer>>();
			final List<Integer> intermediate = new LinkedList<Integer>();
			final Map<Integer, String> names = new HashMap<Integer, String>();
			Collection<Integer> foo;
			
			for (final INode v : graph.getNodes()) {
				if (v.isIntermediate())
					intermediate.add(v.getId());
				if (v.getName() != null)
					names.put(v.getId(), v.getName());
				nodeLoc.put(v.getId(), layout.transform(v));
				connections.put(v.getId(), foo = new LinkedList<Integer>());
				for (INode u : graph.getSuccessors(v)) {
					foo.add(u.getId());
				}
			}

			out.putNextEntry(new ZipEntry("NodesAndConnections.xml"));
			xstream.toXML(connections, out);
			
			out.putNextEntry(new ZipEntry("NodeLocations.xml"));
			xstream.toXML(nodeLoc, out);
			
			if (!intermediate.isEmpty()) {
				out.putNextEntry(new ZipEntry("IntermediateNodes.xml"));
				xstream.toXML(intermediate, out);
			}
			
			if (graph.getStart() != null) {
				out.putNextEntry(new ZipEntry("StartNode.xml"));
				xstream.toXML(graph.getStart().getId(), out);
			}
			
			if (!names.isEmpty()) {
				out.putNextEntry(new ZipEntry("NodeNames.xml"));
				xstream.toXML(names, out);
			}
			
			if (GraphPanel.getInstance().isGraphBackground()) {
				BufferedImage bg = GraphPanel.getInstance().getGraphBackground();
				out.putNextEntry(new ZipEntry("GraphBackground.png"));
				ImageIO.write(bg, "png", out);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Application.getInstance(DijkstraVisApp.class).getMainFrame(), SC.t("error_file_save"), SC.t("error"), JOptionPane.ERROR_MESSAGE);
			return false;
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
    /* 
	 * Copies a file out of the jar to a physical location.  
	 *    Doesn't need to be private, uses a resource stream, so may have
	 *    security errors if ran from webstart application
	 * 
	 * FROM: http://forum.java.sun.com/thread.jspa?threadID=5154854&messageID=9585048 
	 */
	public static boolean copyFromJar(Class<?> clazz, String sResource, File fDest) {
		if (sResource == null || fDest == null)
			return false;
		InputStream sIn = null;
		OutputStream sOut = null;
		try {
			fDest.getParentFile().mkdirs();
			new File(sResource);
		} catch (Exception e) {
		}
		try {
			int nLen = 0;
			sIn = clazz.getResourceAsStream(sResource);
			if (sIn == null)
				throw new IOException("Error copying from jar" + "(" + sResource + " to " + fDest.getPath() + ")");
			sOut = new FileOutputStream(fDest);
			byte[] bBuffer = new byte[1024];
			while ((nLen = sIn.read(bBuffer)) > 0)
				sOut.write(bBuffer, 0, nLen);
			sOut.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (sIn != null)
					sIn.close();
				if (sOut != null)
					sOut.close();
			} catch (IOException eError) {
				eError.printStackTrace();
			}
		}
		return fDest.exists();
	}

}
