/*
 * Copyright (C) 2008, Uwe Schmidt 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE. 
 */
package co.edu.poli.medgraph.gui.transformer;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import org.apache.commons.collections15.Transformer;
import org.uweschmidt.dijkstravis.graph.INode;


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
