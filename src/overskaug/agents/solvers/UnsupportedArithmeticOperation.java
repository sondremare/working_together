package overskaug.agents.solvers;

public class UnsupportedArithmeticOperation extends Exception {

    public UnsupportedArithmeticOperation() {
    }

    public UnsupportedArithmeticOperation(String message) {
        super(message);
    }

    public UnsupportedArithmeticOperation(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedArithmeticOperation(Throwable cause) {
        super(cause);
    }

    public UnsupportedArithmeticOperation(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
