package main.ExternalServices.Supplying;


import main.ExternalServices.HttpRequestController;
import main.Stores.Product;
import main.utils.SupplyingInformation;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.Map;

public class SupplyingAdapter implements ISupplying {

    private HttpRequestController httpReqCtrl;

    public SupplyingAdapter()
    {
        try
        {
            this.httpReqCtrl = new HttpRequestController();
        }
        catch (Exception e)
        {
            this.httpReqCtrl = null;
        }

    }

    @Override
    public boolean supply(SupplyingInformation si, Map<Product, Integer> productToSupply) {
        try
        {
            if(this.httpReqCtrl == null)
            {
                return false;
            }
            this.httpReqCtrl = new HttpRequestController();
            if(!this.httpReqCtrl.handshake())
            {
                return false;
            }
            Map<String,String> params = new HashMap<>();
            params.put("action_type", "supply");
            params.put("name", si.getName());
            params.put("address", si.getAddress());
            params.put("city", si.getCity());
            params.put("country", si.getCountry());
            params.put("zip", si.getZip());
            this.httpReqCtrl = new HttpRequestController();
            String response = this.httpReqCtrl.sendRequest(params);
            int transId = Integer.parseInt(response);

            si.setTransactionId(transId);

            if(isValidTransactionId(transId))
            {
                return true;
            }
            return false;

        }
        catch(Exception e)
        {
            return false;
        }

    }

    @Override
    public void abort(SupplyingInformation si) throws Exception{
        if(this.httpReqCtrl == null)
        {
            throw new Exception("No connection established");
        }
        if(si.getTransactionId()==0)
        {
            throw new Exception("Cant abort a supply before supplying it");
        }
        if(si.getTransactionId()==-1)
        {
            return; //Do nothing the supply already failed
        }
        this.httpReqCtrl = new HttpRequestController();
        if(!this.httpReqCtrl.handshake())
        {
            throw new Exception("Handshake failed, Cant abort supply");
        }

        Map<String,String> params = new HashMap<>();
        params.put("action_type", "cancel_pay");
        params.put("transaction_id", String.valueOf(si.getTransactionId()));
        this.httpReqCtrl = new HttpRequestController();
        String response = this.httpReqCtrl.sendRequest(params);
        int abortResult = Integer.parseInt(response);
        if(abortResult != 1)
        {
            throw new Exception("Supply abort failed");
        }
        //Success - do nothing
    }

    private boolean isValidTransactionId(int transId)
    {
        return (transId >= 10000) && (transId <= 100000);
    }

}
