package assignsShifts.exceptions;

public class ValidationParamException extends Exception {
  public ValidationParamException(String paramName) {
    super("The " + paramName + " param is missing.");
  }
}
