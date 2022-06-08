package main.ExternalServices.Payment;

import main.ExternalServices.HttpRequestController;
import main.utils.PaymentInformation;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PaymentAdapter implements IPayment {

    private HttpRequestController httpReqCtrl;


    public  PaymentAdapter()
    {
        try
        {
            httpReqCtrl = new HttpRequestController();
        }
        catch (Exception e)
        {
            httpReqCtrl = null;
        }
    }


    @Override
    public boolean makePayment(PaymentInformation pi, double amountToPay) {
        try
        {
            if(httpReqCtrl == null)
            {
                return false;
            }
            this.httpReqCtrl = new HttpRequestController();
            if(!this.httpReqCtrl.handshake())
            {
                return false;
            }

            LocalDate expDate = pi.getExpDate();
            int expMonthNum = expDate.getMonthValue();
            int expYearNum = expDate.getYear();

            Map<String, String> params = new HashMap<>();
            params.put("action_type", "pay");
            params.put("card_number", pi.getCardNumber());
            params.put("month", String.valueOf(expMonthNum));
            params.put("year", String.valueOf(expYearNum));
            params.put("holder", pi.getName());
            params.put("ccv", String.valueOf(pi.getCvv()));
            params.put("id", pi.getId());

            this.httpReqCtrl = new HttpRequestController();
            String response = this.httpReqCtrl.sendRequest(params);

            int transactionId = Integer.parseInt(response);

            pi.setTransactionId(transactionId);

            if(isValidTransactionId(transactionId)) //Successful payment
            {
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    @Override
    public void abort(PaymentInformation pi)  throws Exception{

        if(httpReqCtrl == null)
        {
            throw new Exception("No connection established");
        }

        if(pi.getTransactionId() == 0) //No one called makePayment on this Payment Information
        {
            throw new Exception("Payment was never done, cant abort.");
        }
        if(pi.getTransactionId() == -1) //Call for makePayment already failed so do nothing
        {
            return;
        }
        this.httpReqCtrl = new HttpRequestController();
        if(!this.httpReqCtrl.handshake())
        {
            throw new Exception("Handshake failed. Cant abort payment.");
        }
        Map<String,String> params = new HashMap<>();
        params.put("action_type", "cancel_pay");
        params.put("transaction_id", String.valueOf(pi.getTransactionId()));
        this.httpReqCtrl = new HttpRequestController();
        String response = this.httpReqCtrl.sendRequest(params);
        int abortResult = Integer.parseInt(response);
        if(abortResult != 1)
        {
            throw new Exception("Abort failed");
        }
        //Success - do nothing
    }

    private boolean isValidTransactionId(int transId)
    {
        return (transId >= 10000) && (transId <= 100000);
    }
}
