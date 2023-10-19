package model;

public class Settings {

    private final int vehicesQnt;
    private final int insertionInteraval;
    private final String meshFileName;
    private final int vehiclesOnMeshQnt;

    public Settings(int vehicesQnt, int insertionInteraval, String meshFileName, int vehiclesOnMeshQnt) {
        this.vehicesQnt = vehicesQnt;
        this.insertionInteraval = insertionInteraval;
        this.meshFileName = meshFileName;
        this.vehiclesOnMeshQnt = vehiclesOnMeshQnt;
    }

    public int getVehicesQnt() {
        return vehicesQnt;
    }

    public int getInsertionInteraval() {
        return insertionInteraval;
    }

    public String getMeshFileName() {
        return meshFileName;
    }

    public int getVehiclesOnMeshQnt() {
        return vehiclesOnMeshQnt;
    }
}