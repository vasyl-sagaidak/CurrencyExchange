package exception;

public class NegativeExchangeAmountException extends RuntimeException {
    public NegativeExchangeAmountException(String message) {
        super(message);
    }
}
