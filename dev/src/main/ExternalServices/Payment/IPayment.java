package main.ExternalServices.Payment;

import main.utils.PaymentInformation;

public interface IPayment {
    boolean validateCard(PaymentInformation pi);
    boolean makePayment(PaymentInformation pi, double amountToPay);
    void abort(PaymentInformation pi);
}
