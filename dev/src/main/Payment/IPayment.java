package main.Payment;

public interface IPayment {
    boolean validateCard(String cardNum, int expMonth, int expYear, int securityCode);
    boolean makePayment(String cardNum, int expMonth, int expYear, int securityCode, double amountToPay);

}
