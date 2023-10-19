package controller;

import model.Road;
import model.Settings;
import model.Vehicle;
import view.ViewMeshTable;
import view.ViewSimulation;

import javax.swing.*;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;

public class ControllerSimulation extends Thread {

    private final LinkedList<Vehicle> vehiclesOnQueue;
    private final ArrayList<Vehicle> vehiclesOnMesh;
    private final Settings settings;
    private final ViewSimulation viewSimulation;
    private boolean ended = false;

    public ControllerSimulation(Settings settings, ViewSimulation viewSimulation) {
        this.settings = settings;
        this.viewSimulation = viewSimulation;
        this.vehiclesOnMesh = new ArrayList<>();
        this.vehiclesOnQueue = this.loadVehicles();
    }

    public LinkedList<Vehicle> getVehiclesOnQueue() {
        return vehiclesOnQueue;
    }

    public ArrayList<Vehicle> getVehiclesOnMesh() {
        return vehiclesOnMesh;
    }

    public Settings getSettings() {
        return settings;
    }

    public ViewSimulation getViewSimulation() {
        return viewSimulation;
    }

    public Road[][] getMeshRoad() {
        return this.getMeshTable().getMesh();
    }

    public ViewMeshTable getMeshTable() {
        return (ViewMeshTable) this.getViewSimulation().getTbMalha().getModel();
    }

    public boolean isEnded() {
        return ended;
    }

    @Override
    public void run() {
        while (!this.ended) {
            this.runQueue();
        }
    }

    private void runQueue() {
        while (!this.vehiclesOnQueue.isEmpty()) {
            for (int lines = 0; lines < this.getViewSimulation().getTbMalha().getRowCount(); lines++) {
                for (int columns = 0; columns < this.getViewSimulation().getTbMalha().getColumnCount(); columns++) {
                    Road entry = this.getMeshRoad()[columns][lines];
                    if (entry.isEntry() && entry.isEmpty() && !this.vehiclesOnQueue.isEmpty() && this.vehiclesOnMesh.size() < this.getSettings().getVehiclesOnMeshQnt()) {
                        try {
                            Vehicle vehicle = this.vehiclesOnQueue.remove();
                            vehicle.setRoute(entry);
                            this.addVehicleOnMesh(vehicle);
                            vehicle.start();
                            this.sleepNextVehicle();
                        } catch (InterruptedException e) {
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    private void sleepNextVehicle() throws InterruptedException {
        int sleepTime = 10;
        if (this.getSettings().getInsertionInteraval() > 0) {
            sleepTime = this.getSettings().getInsertionInteraval() * 1000;
        }
        sleep(sleepTime);
    }

    private void addVehicleOnMesh(Vehicle vehicle) {
        this.vehiclesOnMesh.add(vehicle);
    }

    private LinkedList<Vehicle> loadVehicles() {
        LinkedList<Vehicle> vehicles = new LinkedList<>();
        for (int i = 0; i < this.getSettings().getVehicesQnt(); i++) {
            vehicles.add(new Vehicle(this));
        }
        return vehicles;
    }

    public void removeCarOnMesh(Vehicle vehicle) {
        this.getVehiclesOnMesh().remove(vehicle);
        if (this.getVehiclesOnMesh().isEmpty() && this.getVehiclesOnQueue().isEmpty() && !this.ended) {
            this.end();
            JOptionPane.showMessageDialog(this.getViewSimulation(), "Simulação finalizada, todos os veículos percorreram a malha");
        }
    }

    public void updateCell(Road road) {
        this.getViewSimulation().getLbVeiculosFila().setText("Veículos na fila: " + this.vehiclesOnQueue.size());
        this.getViewSimulation().getLbVeiculosMalha().setText("Veículos na malha: " + this.vehiclesOnMesh.size());
        this.getMeshTable().fireTableCellUpdated(road.getLine(), road.getColumn());
        this.getMeshTable().fireTableDataChanged();
    }

    public void end() {
        this.ended = true;
        for (Vehicle vehicleOnQueue : this.vehiclesOnQueue) {
            vehicleOnQueue.end();
        }
        for (Vehicle vehicleOnMesh : this.vehiclesOnMesh) {
            vehicleOnMesh.end();
        }
        this.interrupt();
    }
}