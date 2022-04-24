package main.ExternalServices.Payment;

import main.utils.PaymentInformation;

public class PaymentAdapter implements IPayment {

    @Override
    public boolean validateCard(PaymentInformation pi) {
        return true;
    }

    @Override
    public boolean makePayment(PaymentInformation pi, double amountToPay) {
        return true;
    }

    @Override
    public void abort(PaymentInformation pi) {

    }
}
