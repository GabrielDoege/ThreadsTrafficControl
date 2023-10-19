package view;

import model.Road;
import model.Settings;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;

public class ViewMeshTable extends AbstractTableModel {

    private int lines;
    private int columns;
    private Road mesh[][];
    private static final String FILES_DIRECTORY =  Paths.get("").toAbsolutePath() +"/src/meshes/";
    private Settings settings;

    public ViewMeshTable(Settings settings) {
        this.settings = settings;
        this.newMatrix();
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public Road[][] getMesh() {
        return mesh;
    }

    public void setMesh(Road[][] mesh) {
        this.mesh = mesh;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    public int getRowCount() {
        return this.getLines();
    }

    @Override
    public int getColumnCount() {
        return this.getColumns();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return new ImageIcon(this.mesh[columnIndex][rowIndex].getIconDirectory());
    }

    public void newMatrix() {
        Scanner scanner = null;
        try {
            File meshFile = new File(FILES_DIRECTORY + this.settings.getMeshFileName());
            scanner = new Scanner(meshFile);
            while (scanner.hasNextInt()) {
                this.setLines(scanner.nextInt());
                this.setColumns(scanner.nextInt());
                this.mesh = new Road[this.columns][this.lines];
                for (int line = 0; line < this.lines; line++) {
                    for (int column = 0; column < this.columns; column++) {
                        int direction = scanner.nextInt();
                        Road road = new Road(column, line, direction, settings.getMultipleExclusionType());
                        if (road.isRoad()) {
                            road.setEntryOrExit(this);
                        }
                        this.mesh[column][line] = road;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}