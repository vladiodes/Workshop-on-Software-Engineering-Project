package test.testUtils;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Service.IService;
import main.Service.Service;
import main.Stores.Product;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class testsFactory {
    public static PaymentInformation getSomePI(){
        return new PaymentInformation("123", LocalDate.now(), 123 , "hi", "hi" );
    }

    public static SupplyingInformation getSomeSI(){
        return new SupplyingInformation("Wingate BeerSheba", LocalDate.now());
    }

    public static IPayment alwaysSuccessPayment(){
        return  new IPayment() {
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
        };
    }

    public static ISupplying alwaysSuccessSupplyer(){
        return new ISupplying() {
            @Override
            public boolean bookDelivery(SupplyingInformation si) {
                return true;
            }

            @Override
            public boolean supply(SupplyingInformation si, Map<Product, Integer> productToSupply) {
                return true;
            }

            @Override
            public void abort(SupplyingInformation si) {

            }
        };
    }
}
