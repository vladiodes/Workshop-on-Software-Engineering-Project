package main.Stores.PurchasePolicy.ProductPolicy;

import main.ExternalServices.Payment.IPayment;
import main.ExternalServices.Supplying.ISupplying;
import main.Publisher.PersonalNotification;
import main.Stores.Product;
import main.Stores.Store;
import main.Users.User;
import main.utils.Bid;
import main.utils.PaymentInformation;
import main.utils.SupplyingInformation;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Entity
public class BargainingPolicy extends TimedPolicy{


    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "approving_users_bids",
            joinColumns = {@JoinColumn(name = "policy_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "approved_list_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "id")
    private Map<Bid, bidApprovedByUserList> bidApprovedBy;
    @OneToOne
    private Store sellingStore;
    private Double originalPrice;
    @OneToOne
    private Product product;

    public BargainingPolicy(Store sellingStore, Double originalPrice, Product product) {
        this.bidApprovedBy = Collections.synchronizedMap(new HashMap<>());
        this.sellingStore = sellingStore;
        this.originalPrice = originalPrice;
        this.product = product;
    }

    public BargainingPolicy() {

    }

    @Override
    public boolean isPurchasable(Product product, Double costumePrice, int amount, User user) {
        return isApproved(bidApprovedBy.get(getUserBid(user)));
    }

    @Override
    public boolean isPurchasable(Product product, int amount) {
        return amount == 1 && product.getQuantity() >= 1;
    }

    @Override
    public boolean productPurchased(Product product, User user, Double costumePrice, int amount, ISupplying supplying, SupplyingInformation supplyingInformation, PaymentInformation paymentInformation, IPayment payment) {
        product.subtractQuantity(1);
        return true;
    }

    private Bid getUserBid(User user){
        for(Bid bid : bidApprovedBy.keySet())
            if(bid.getUser() == user)
                return bid;
        throw new IllegalArgumentException("User hasn't bid yet.");
    }

    private Bid getUserBid(String user){
        for(Bid bid : bidApprovedBy.keySet())
            if(bid.getUser().getUserName().equals(user))
                return bid;
        throw new IllegalArgumentException("User hasn't bid yet.");
    }

    private boolean isApproved(bidApprovedByUserList approvers) {
        for(User staff : sellingStore.getStoreStaff().keySet())
            if (staff.ShouldBeNotfiedForBargaining(sellingStore) && !approvers.getApprovedBy().contains(staff))
                return false;
        return true;
    }

    @Override
    public boolean bid(Bid bid) {
        for (Bid bidkey : bidApprovedBy.keySet()) {
            if (bid.getUser() == bidkey.getUser()) {
                bidApprovedBy.remove(bidkey);
                bidApprovedBy.put(bid, new bidApprovedByUserList());
                return true;
            }
        }
        bidApprovedBy.put(bid, new bidApprovedByUserList());
        return true;
    }

    @Override
    public List<Bid> getBids() {
        return new ArrayList<>(this.bidApprovedBy.keySet());
    }

    @Override
    public void approveBid(User user, User approvingUser,IPayment payment,ISupplying supplying) throws Exception {
        Bid bid = getUserBid(user.getUserName());
        bidApprovedByUserList approvers = bidApprovedBy.get(bid);
        approvers.add(approvingUser);
        if (isApproved(approvers)) {
            this.purchaseBid(sellingStore, getUserBid(user.getUserName()),payment,supplying);
            user.notifyObserver(new PersonalNotification(sellingStore.getName(),String.format("Your offer for %s has been accepted and product was successfully purchased.", bid.getProduct().getName())));
            bidApprovedBy.remove(getUserBid(user));
        }
    }

    @Override
    public void declineBid(User user) {
        Bid toDecline = getUserBid(user.getUserName());
        bidApprovedBy.remove(toDecline);
        user.notifyObserver(new PersonalNotification(sellingStore.getName(),String.format("Your offer for %s has been declined by store staff.", toDecline.getProduct().getName())));
    }

    @Override
    public void counterOfferBid(User user, Double offer) {
        Bid toDecline = getUserBid(user.getUserName());
        bidApprovedBy.remove(toDecline);
        user.notifyObserver(new PersonalNotification(sellingStore.getName(),
                String.format("Your offer for %s has been declined by store staff, a counter offer was suggested: %d", toDecline.getProduct().getName(), offer)));
    }

    @Override
    public void close() {
        for (Bid bidkey : bidApprovedBy.keySet()) {
            bidkey.getUser().notifyObserver(new PersonalNotification(sellingStore.getName(), String.format("%s is not up for bargaining anymore",product.getName())));
        }
    }

    @Override
    public double getCurrentPrice(User user) {
        return getUserBid(user).getCostumePrice();
    }

    @Override
    public double getOriginalPrice() {
        return this.originalPrice;
    }

    @Override
    public void setOriginalPrice(Double price) {
        this.originalPrice = price;
    }

    @Override
    public boolean isAddableToBasket() {
        return false;
    }

    @Override
    public boolean deliveredImmediately(User user){
        return isApproved(this.bidApprovedBy.get(getUserBid(user)));
    }
}
