package view;

import model.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewMainMenu extends JFrame {
    private JPanel jpPainel;
    private JLabel lbConfiguracoes;
    private JTextField tfIntervaloInsercao;
    private JTextField tfQntTotalVeiculos;
    private JTextField tfQntVeiculosSimultaneos;
    private JRadioButton rbMalha1;
    private JButton btnIniciar;
    private JRadioButton rbMalha2;
    private JRadioButton rbMalha3;
    private JLabel lbIntervalo;
    private JLabel lbQntTotal;
    private JLabel lbQntSimultanea;
    private JRadioButton rbSemaforo;
    private JLabel lbTipoExcluao;
    private JRadioButton rbMonitor;

    public ViewMainMenu() {
        super("Threads Traffic Control");
        super.setSize(new Dimension(800, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(this.jpPainel);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        btnIniciar.addActionListener((ActionEvent e) -> {
            new ViewSimulation(new Settings(
                    Integer.parseInt(tfQntTotalVeiculos.getText()),
                    Integer.parseInt(tfIntervaloInsercao.getText()),
                    this.getSelectedMesh(),
                    Integer.parseInt(this.tfQntVeiculosSimultaneos.getText()),
                    this.getMultipleExclusionType()
            ));
            super.dispose();
        });
        super.setVisible(true);
    }

    public String getSelectedMesh() {
        if (this.rbMalha1.isSelected()) {
            return "mesh1.txt";
        }
        if (this.rbMalha2.isSelected()) {
            return "mesh2.txt";
        }
        if (this.rbMalha3.isSelected()) {
            return "mesh3.txt";
        }
        return null;
    }

    public int getMultipleExclusionType() {
        if (this.rbSemaforo.isSelected()) {
            return 1;
        }
        if (this.rbMonitor.isSelected()) {
            return 2;
        }
        return 0;
    }
}