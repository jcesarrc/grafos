package co.edu.poli.medgraph;

import co.edu.poli.medgraph.gui.GraphPanel;
import co.edu.poli.medgraph.gui.StatusBar;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

public class MedGraph extends SingleFrameApplication {

    @Override
    @SuppressWarnings("serial")
    protected void startup() {
        JPanel foo = new JPanel(new BorderLayout());
        GraphPanel gr = GraphPanel.getInstance();
        try {
            gr.setGraphBackground(ImageIO.read(MedGraph.class.getResourceAsStream("mapa.png")));
        } catch (IOException ex) {
            Logger.getLogger(MedGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        foo.add(gr, BorderLayout.CENTER);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(foo, BorderLayout.CENTER);
        mainPanel.add(StatusBar.getInstance(), BorderLayout.SOUTH);
        StatusBar.getInstance().setText(StatusBar.EMPTY_TEXT);
        
        show(mainPanel);
    }

    @Action
    public void exitApp() {
        exit();
    }

    public static void main(String[] args) {
        Application.launch(MedGraph.class, args);
    }

}
