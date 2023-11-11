package Assignment2;

public class JasonBotNN extends AdvancedRobot {
    private String weightsFilename = getClass().getSimpleName() + "-weights.txt";
    private String logFilename = getClass().getSimpleName() + "-" + new Date().toString() + ".log";
    static int nnNumHidden = 10;
    static double nnRho = 0.001;
    static double nnAlpha = 0.8;

    static private NeuralNet q = new NeuralNet(
            9,
            nnNumHidden,
            nnRho,
            nnAlpha,
            -1,
            1,
            true);

    static int totalNumRounds = 0;
    static int numRoundsTo100 = 0;
    static int numWins = 0;

    public enum enumOperationalMOde {scan, performAction};

    private State currentState = new State(100, 500, 100, 500);
    private Action currentAction = Action.values()[0];
    private State previousState = new State(10, 500, 100, 500);
    private Action previousAciton = Action.values()[0];
}
