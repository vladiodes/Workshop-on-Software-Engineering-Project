package main.Payment;

public class PaymentAdapter implements IPayment {

    @Override
    public boolean validateCard(String cardNum, int expMonth, int expYear, int securityCode) {
        return true;
    }

    @Override
    public boolean makePayment(String cardNum, int expMonth, int expYear, int securityCode, double amountToPay) {
        return true;
    }
}
