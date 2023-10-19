package view;

import controller.ControllerSimulation;
import model.Settings;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewSimulation extends JFrame{
    private JPanel jpPainel;
    private JTable tbMalha;
    private JButton btnPausar;
    private JButton btnEncerrar;
    private JLabel lbVeiculosMalha;
    private JLabel lbVeiculosFila;
    private JPanel jpPainelCampos;
    private Settings settings;
    private final ControllerSimulation controllerSimulation;

    public ViewSimulation(Settings settings) {
        this.settings = settings;
        this.formatView();
        this.meshTableRender();
        super.setVisible(true);
        this.controllerSimulation = new ControllerSimulation(settings, this);
        this.controllerSimulation.start();
    }

    public JPanel getJpPainel() {
        return jpPainel;
    }

    public JTable getTbMalha() {
        return tbMalha;
    }

    public JButton getBtnPausar() {
        return btnPausar;
    }

    public JButton getBtnEncerrar() {
        return btnEncerrar;
    }

    public JLabel getLbVeiculosMalha() {
        return lbVeiculosMalha;
    }

    public JLabel getLbVeiculosFila() {
        return lbVeiculosFila;
    }

    public Settings getSettings() {
        return settings;
    }

    public ControllerSimulation getControllerSimulation() {
        return controllerSimulation;
    }

    private void formatView() {
        super.setExtendedState(JFrame.MAXIMIZED_BOTH);
        super.setUndecorated(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setContentPane(this.jpPainel);
        btnEncerrar.addActionListener((ActionEvent e) -> {
            this.controllerSimulation.end();
            new ViewMainMenu();
            super.dispose();
        });
    }

    private void meshTableRender() {
        tbMalha.setModel(new ViewMeshTable(this.settings));
        tbMalha.setRowHeight(25);
        tbMalha.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbMalha.setIntercellSpacing(new Dimension(0, 0));
        tbMalha.setDefaultRenderer(Object.class, new ViewMeshCellRender());
        TableColumnModel columnModel = tbMalha.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setMaxWidth(25);
        }
    }
}