package model;

import controller.ControllerSimulation;

import java.util.ArrayList;
import java.util.Random;

public class Vehicle extends Thread {

    private Road actualRoad;
    private final int speed;
    private final Road[][] trackMesh;
    private final ArrayList<Road> route;
    private final ControllerSimulation controllerSimulation;
    private boolean ended = false;
    private final Random random = new Random();

    public Vehicle(ControllerSimulation controllerSimulation) {
        this.controllerSimulation = controllerSimulation;
        this.route = new ArrayList<>();
        this.trackMesh = controllerSimulation.getMeshRoad();
        this.speed = random.nextInt(100) + 500;
        this.actualRoad = null;
    }

    private Road getActualRoad() {
        return actualRoad;
    }

    private void setActualRoad(Road currentRoad) {
        this.actualRoad = currentRoad;
    }

    public void end() {
        this.ended = true;
        this.interrupt();
    }

    @Override
    public void run() {
        while (!this.ended) {
            while (!route.isEmpty()) {
                int nextRoadIndex = 0;
                if (route.get(nextRoadIndex).isCrossing()) {
                    //Se for um cruzamento precisamos saber para que lado ir
                    resolveCrossing();
                } else {
                    //Se for s óuma estrada apenas move o veiculo
                    Road road = this.route.remove(nextRoadIndex);
                    this.move(road, true);
                }
            }
            //Chegou ao fim do percurso?
            // - Remove o carro
            this.getActualRoad().removeVehicle();
            // - Libera a estrada
            this.getActualRoad().release();
            // - Tira da malha pois saiu da tela
            this.controllerSimulation.removeCarOnMesh(this);
            // - Atualização gráfica
            this.controllerSimulation.updateCell(this.getActualRoad());
            // - Fim da thread
            this.end();
        }
    }

    private void move(Road nextRoad, boolean reserve) {
        if (nextRoad.isEmpty()) {
            boolean reserved = false;
            if (reserve) {
                do {
                    //Tenta "reservar/adquirir" a estrada
                    if (nextRoad.tryAcquire()) {
                        reserved = true;
                    }
                } while (!reserved);
            }
            //Somente quando conseguiu a estrada, adiciona o carro na posição
            nextRoad.addVehicle(this);
            Road previousRoad = this.getActualRoad();
            if (previousRoad != null) {
                //Tira o carro da estrada que ele estava
                previousRoad.removeVehicle();
                //Libera a estrada
                previousRoad.release();
            }
            //Diz em qual estrada o carro está agora
            this.setActualRoad(nextRoad);
            //Atualização gráfica
            this.controllerSimulation.updateCell(nextRoad);
            this.delay();
        }
    }

    private void resolveCrossing() {
        this.delay();
        ArrayList<Road> reservationCrossings = this.loadNecessaryCrossingsForMovement();
        ArrayList<Road> reservedCrossings = this.tryReserveCrossings(reservationCrossings);
        //Tem todos os cruzamentos para passar?
        if (reservedCrossings.size() == reservationCrossings.size()) {
            //Move pelo cruzamento
            for (Road reservedCrossing : reservedCrossings) {
                this.route.remove(reservedCrossing);
                this.move(reservedCrossing, false);
            }
        }
    }

    private ArrayList<Road> loadNecessaryCrossingsForMovement() {
        ArrayList<Road> reserveCrossings = new ArrayList<>();
        for (int i = 0; i < this.route.size(); i++) {
            Road road = this.route.get(i);
            reserveCrossings.add(road);
            if (!road.isCrossing()) {
                break;
            }
        }
        return reserveCrossings;
    }

    private void delay() {
        try {
            //Tempo de espera a cada movimento para definir a velocidade de cada carro
            Thread.sleep(this.speed);
        } catch (InterruptedException e) {
            //Nada, só encerrou a execução
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Road> tryReserveCrossings(ArrayList<Road> reserveCrossings) {
        //Tenta reservar todos os cruzamentos necessários
        ArrayList<Road> reservedCrossings = new ArrayList<>();
        for (Road crossingTryReserve : reserveCrossings) {
            if (crossingTryReserve.tryAcquire()) {
                reservedCrossings.add(crossingTryReserve);
            } else {
                //Não conseguiu reservar todos os cruzamentos para passar?
                //Vamos liberar os que tinhamos conseguidos reservar
                this.releaseRoadList(reservedCrossings);
                break;
            }
        }
        return reservedCrossings;
    }

    private void releaseRoadList(ArrayList<Road> roads) {
        for (Road road : roads) {
            road.release();
        }
    }

    public void setRoute(Road entry) throws Exception {
        boolean exitFound = false;
        Road nextRoad = entry;
        route.add(nextRoad);
        //Controla os cruzamentos encontrados para não ocorrer de andar em circulos
        int foundedCrossings = 0;
        while (!exitFound) {
            int direction = nextRoad.getType();
            boolean oneDirectionRoad = direction <= 4;
            if (oneDirectionRoad) {
                nextRoad = this.chooseRoadByDirection(direction, nextRoad.getLine(), nextRoad.getColumn());
            } else {
                nextRoad = this.chooseCrossingByDirection(direction, nextRoad.getLine(), nextRoad.getColumn(), foundedCrossings);
                if (nextRoad.isCrossing()) {
                    foundedCrossings++;
                } else {
                    foundedCrossings = 0;
                }
            }
            route.add(nextRoad);
            exitFound = nextRoad.isExit();
        }
    }

    private Road chooseRoadByDirection(int direction, int actualLine, int actualColumn) throws Exception {
        switch (direction) {
            case 1:
                return this.trackMesh[actualLine][actualColumn - 1];
            case 2:
                return this.trackMesh[actualLine + 1][actualColumn];
            case 3:
                return this.trackMesh[actualLine][actualColumn + 1];
            case 4:
                return this.trackMesh[actualLine - 1][actualColumn];
            default:
                throw new Exception("Erro na montagem da malha");
        }
    }

    private Road chooseCrossingByDirection(int direction, int actualLine, int actualColumn, int foundedCrossings) throws Exception {
        int side = random.nextInt(2);
        switch (direction) {
            case 5: {
                return this.trackMesh[actualLine][actualColumn - 1];
            }
            case 6: {
                return this.trackMesh[actualLine + 1][actualColumn];
            }
            case 7: {
                return this.trackMesh[actualLine][actualColumn + 1];
            }
            case 8: {
                return this.trackMesh[actualLine - 1][actualColumn];
            }
            case 9: {
                if (foundedCrossings == 3) {
                    return this.trackMesh[actualLine + 1][actualColumn];
                } else {
                    if (side == 0) {
                        return this.trackMesh[actualLine][actualColumn - 1];
                    } else {
                        return this.trackMesh[actualLine + 1][actualColumn];
                    }
                }
            }
            case 10: {
                if (foundedCrossings == 3) {
                    return this.trackMesh[actualLine][actualColumn - 1];
                } else {
                    if (side == 0) {
                        return this.trackMesh[actualLine][actualColumn - 1];
                    } else {
                        return this.trackMesh[actualLine - 1][actualColumn];
                    }
                }
            }
            case 11: {
                if (foundedCrossings == 3) {
                    return this.trackMesh[actualLine][actualColumn + 1];
                } else {
                    if (side == 0) {
                        return this.trackMesh[actualLine + 1][actualColumn];
                    } else {
                        return this.trackMesh[actualLine][actualColumn + 1];
                    }
                }
            }
            case 12: {
                if (foundedCrossings == 3) {
                    return this.trackMesh[actualLine - 1][actualColumn];
                } else {
                    if (side == 0) {
                        return this.trackMesh[actualLine][actualColumn + 1];
                    } else {
                        return this.trackMesh[actualLine - 1][actualColumn];
                    }
                }
            }
            default:
                throw new Exception("Erro na montagem da malha");
        }
    }
}