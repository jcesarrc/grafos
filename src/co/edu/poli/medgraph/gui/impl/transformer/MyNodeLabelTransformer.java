package co.edu.poli.medgraph.gui.impl.transformer;

import co.edu.poli.medgraph.grafo.INode;
import org.apache.commons.collections15.Transformer;


public class MyNodeLabelTransformer implements Transformer<INode,String> {
	
	public String transform(INode v) {
		return v.isIntermediate() ? null : v.toString();
	}

}
