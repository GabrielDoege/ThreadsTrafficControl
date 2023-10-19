package model;

public class Settings {

    private final int vehicesQnt;
    private final int insertionInteraval;
    private final String meshFileName;
    private final int vehiclesOnMeshQnt;
    private final int multipleExclusionType;

    public Settings(int vehicesQnt, int insertionInteraval, String meshFileName, int vehiclesOnMeshQnt, int multipleExclusionType) {
        this.vehicesQnt = vehicesQnt;
        this.insertionInteraval = insertionInteraval;
        this.meshFileName = meshFileName;
        this.vehiclesOnMeshQnt = vehiclesOnMeshQnt;
        this.multipleExclusionType = multipleExclusionType;
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

    public int getMultipleExclusionType() {
        return multipleExclusionType;
    }
}