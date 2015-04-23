
package co.edu.poli.medgraph.gui.impl.transformer;

import co.edu.poli.medgraph.grafo.INode;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import org.apache.commons.collections15.Transformer;


public class MyNodeShapeTransformer implements Transformer<INode, Shape> {
	
	public static final Ellipse2D.Float INTERMEDIATE = new Ellipse2D.Float(-4,-4,8,8);
	public static final Ellipse2D.Float DEFAULT = new Ellipse2D.Float(-10,-10,20,20);
	public static final GeneralPath DEFAULT_DOUBLE, INTERMEDIATE_DOUBLE;
	static {
		DEFAULT_DOUBLE = new GeneralPath();
		DEFAULT_DOUBLE.append(DEFAULT, false);
		DEFAULT_DOUBLE.append(new Ellipse2D.Float(-12,-12,24,24), false);
		INTERMEDIATE_DOUBLE = new GeneralPath();
		INTERMEDIATE_DOUBLE.append(INTERMEDIATE, false);
		INTERMEDIATE_DOUBLE.append(new Ellipse2D.Float(-6,-6,12,12), false);
	}
	
	public Shape transform(INode v) {
//		if (v.isHighlighted())
//			return v.isIntermediate() ? INTERMEDIATE_DOUBLE : DEFAULT_DOUBLE;
		
		switch (v.getAttribute()) {
			case CURRENTLY_SETTLED:
				return v.isIntermediate() ? INTERMEDIATE_DOUBLE : DEFAULT_DOUBLE;
			default:
				return v.isIntermediate() ? INTERMEDIATE : DEFAULT; 
		}
	}

}
