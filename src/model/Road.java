package model;

import view.ViewMeshTable;

import java.nio.file.Paths;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Road {

    protected int type;
    protected boolean entry;
    protected boolean exit;
    protected String iconDirectory;
    protected Semaphore semaphore;
    protected Lock lock;
    protected Vehicle vehicle;
    protected int line;
    protected int column;
    protected int multipleExclusionType;
    private static final String ICONS_DIRECTORY = Paths.get("").toAbsolutePath() + "/src/icons/";

    public Road(int line, int column, int type, int multipleExclusionType) {
        this.vehicle= null;
        this.type = type;
        this.line = line;
        this.column = column;
        this.semaphore = new Semaphore(1);
        this.lock = new ReentrantLock();
        this.multipleExclusionType = multipleExclusionType;
        this.defineIcon();
    }
    
    public void setType(int type) {
        this.type = type;
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

    private void setIconDirectoryByType() {
        this.setIconDirectory(ICONS_DIRECTORY + "mesh" + this.type + ".png");
    }

    private void setCarIconDirectory() {
        this.setIconDirectory(ICONS_DIRECTORY + "vehicle.png");
    }

    public void setEntryOrExit(ViewMeshTable meshTable) {
        this.setEntry((this.isUpperEntry() || this.isBottomEntry(meshTable) || this.isLeftEntry() || this.isRightEntry(meshTable)));
        this.setExit((this.isUpperExit() || this.isBottomExit(meshTable) || this.isLeftExit() || this.isRightExit(meshTable)));
    }

    private void defineIcon() {
        if (this.getVehicle() != null) {
            this.setCarIconDirectory();
        } else {
            this.setIconDirectoryByType();
        }
    }

    public void removeVehicle() {
        this.vehicle = null;
        this.defineIcon();
    }

    public void release() {
        if (this.multipleExclusionType == 1)
            this.releaseSemaphore();
        else
            this.releaseMonitor();
    }

    private void releaseMonitor() {
        try {
            this.lock.unlock();
        } catch (Exception e) { }
    }

    private void releaseSemaphore() {
        try {
            this.semaphore.release();
        } catch(Exception e) { }
    }

    public boolean tryAcquire() {
        if (this.vehicle != null)
            return false;
        if (this.multipleExclusionType == 1)
            return this.tryAcquireSemaphore();
        else
            return this.tryAcquireMonitor();
    }

    public boolean tryAcquireMonitor(){
        try{
            return this.lock.tryLock(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    public boolean tryAcquireSemaphore(){
        try{
            return this.semaphore.tryAcquire(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.defineIcon();
    }

    public boolean isEmpty() {
        return this.vehicle == null;
    }

    public boolean isCrossing() {
        return this.type > 4;
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

    public boolean isEntry() {
        return entry;
    }
}