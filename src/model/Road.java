package model;

import view.ViewMeshTable;

import java.nio.file.Paths;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Road {

    protected int type;
    protected boolean entry;
    protected boolean exit;
    protected String iconDirectory;
    protected Semaphore semaphore;
    protected Vehicle vehicle;
    protected int line;
    protected int column;
    private static final String ICONS_DIRECTORY = Paths.get("").toAbsolutePath() + "/src/icons/";

    public Road(int line, int column, int type) {
        this.vehicle= null;
        this.type = type;
        this.line = line;
        this.column = column;
        this.semaphore = new Semaphore(1);
        this.defineIcon();
    }
    
    public void setType(int type) {
        this.type = type;
    }

    public boolean isEntry() {
        return entry;
    }

    public void setEntry(boolean entry) {
        this.entry = entry;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public String getIconDirectory() {
        return iconDirectory;
    }

    public void setIconDirectory(String iconDirectory) {
        this.iconDirectory = iconDirectory;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    private void defineIcon() {
        if (this.getVehicle() != null) {
            this.setCarIconDirectory();
        } else {
            this.setIconDirectoryByType();
        }
    }

    private void setIconDirectoryByType() {
        this.setIconDirectory(ICONS_DIRECTORY + "mesh" + this.type + ".png");
    }

    private void setCarIconDirectory() {
        this.setIconDirectory(ICONS_DIRECTORY + "vehicle.png");
    }

    public boolean isCrossing() {
        return this.type > 4;
    }

    public void removeVehicle() {
        this.vehicle = null;
        this.defineIcon();
    }

    public void release() {
        this.semaphore.release();
    }

    public boolean tryAcquire() {
        boolean acquired = false;
        try {
            acquired = this.semaphore.tryAcquire(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return acquired;
    }

    public boolean isEmpty() {
        return this.vehicle == null;
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.defineIcon();
    }

    public boolean isExit() {
        return exit;
    }

    public boolean isUpperEntry() {
        return this.getColumn() - 1 < 0 && this.getType() == 3;
    }

    public boolean isUpperExit() {
        return this.getColumn() - 1 < 0 && this.getType() == 1;
    }

    public boolean isBottomEntry(ViewMeshTable meshTable) {
        return this.getColumn() + 1 >= meshTable.getLines() && this.getType() == 1;
    }

    public boolean isBottomExit(ViewMeshTable meshTable) {
        return this.getColumn() + 1 >= meshTable.getLines() && this.getType() == 3;
    }

    public boolean isLeftEntry() {
        return this.getLine() - 1 < 0 && this.getType() == 2;
    }

    public boolean isLeftExit() {
        return this.getLine() - 1 < 0 && this.getType() == 4;
    }

    public boolean isRightEntry(ViewMeshTable meshTable) {
        return this.getLine() + 1 >= meshTable.getColumns() && this.getType() == 4;
    }

    public boolean isRightExit(ViewMeshTable meshTable) {
        return this.getLine() + 1 >= meshTable.getColumns() && this.getType() == 2;
    }

    public boolean isRoad() {
        return this.getType() > 0;
    }

    public void setEntryOrExit(ViewMeshTable meshTable) {
        this.setEntry((this.isUpperEntry() || this.isBottomEntry(meshTable) || this.isLeftEntry() || this.isRightEntry(meshTable)));
        this.setExit((this.isUpperExit() || this.isBottomExit(meshTable) || this.isLeftExit() || this.isRightExit(meshTable)));
    }
}