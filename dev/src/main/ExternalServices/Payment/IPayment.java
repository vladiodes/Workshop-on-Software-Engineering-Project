package main.ExternalServices.Payment;

import main.utils.PaymentInformation;

public interface IPayment {
    boolean makePayment(PaymentInformation pi, double amountToPay);
    void abort(PaymentInformation pi) throws Exception;
}
