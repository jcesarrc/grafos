package co.edu.poli.medgraph.gui.transformer;

import org.apache.commons.collections15.Transformer;
import co.edu.poli.medgraph.grafo.INode;


public class MyNodeLabelTransformer implements Transformer<INode,String> {
	
	public String transform(INode v) {
		return v.isIntermediate() ? null : v.toString();
	}

}
