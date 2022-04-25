package main.ExternalServices.Payment;

import main.utils.PaymentInformation;

public class PaymentAdapter implements IPayment {

    @Override
    public boolean validateCard(PaymentInformation pi) {
        return pi.getOutput();
    }

    @Override
    public boolean makePayment(PaymentInformation pi, double amountToPay) {
        return pi.getOutput();
    }

    @Override
    public void abort(PaymentInformation pi) {

    }
}
