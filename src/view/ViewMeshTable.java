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
            //Cria uma instancia do arquivo pelo nome
            File meshFile = new File(FILES_DIRECTORY + this.settings.getMeshFileName());
            scanner = new Scanner(meshFile);
            //Enquanto tiver valores continua lendo
            while (scanner.hasNextInt()) {
                //Primeira linha é a quantidade de linhas
                this.setLines(scanner.nextInt());
                //Segunda linha é a quantidade de colunas
                this.setColumns(scanner.nextInt());
                //Cria uma matriz que representa a malha com os tamanhos fornecidos
                this.mesh = new Road[this.columns][this.lines];
                //Percorre cada uma das linhas do arquivo
                for (int line = 0; line < this.lines; line++) {
                    for (int column = 0; column < this.columns; column++) {
                        //Cada valor inteiro do arquivo representa uma celula da malha e é
                        //representada por um valor que indica sua direção
                        int direction = scanner.nextInt();
                        Road road = new Road(column, line, direction);
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