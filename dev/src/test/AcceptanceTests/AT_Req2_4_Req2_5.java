package test.AcceptanceTests;

import main.DTO.ProductDTO;
import main.DTO.StoreDTO;
import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.testUtils.testsFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

public class AT_Req2_4_Req2_5 {

    Response<String> manager1token, manager2token, founder1token, founder2token, owner1token, user1token;
    IService service = new Service(testsFactory.alwaysSuccessPayment(), testsFactory.alwaysSuccessSupplyer());
    int threadCount;

    public AT_Req2_4_Req2_5() throws Exception {
    }

    @Before
    public void setUp() {
        manager1token = service.guestConnect();
        manager2token = service.guestConnect();
        founder1token = service.guestConnect();
        founder2token = service.guestConnect();
        owner1token = service.guestConnect();
        user1token = service.guestConnect();

        service.register("manager1", "12345678");
        service.register("manager2", "12345678");
        service.register("founder1", "12345678");
        service.register("founder2", "12345678");
        service.register("owner1", "12345678");
        service.register("user1", "12345678");

        service.login(manager1token.getResult(), "manager1", "12345678");
        service.login(manager2token.getResult(), "manager2", "12345678");
        service.login(founder1token.getResult(), "founder1", "12345678");
        service.login(founder2token.getResult(), "founder2", "12345678");
        service.login(owner1token.getResult(), "owner1", "12345678");
        service.login(user1token.getResult(), "user1", "12345678");

        service.openStore(founder1token.getResult(), "MyStore1");
        service.openStore(founder2token.getResult(), "MyStore2");
        service.appointStoreOwner(founder1token.getResult(), "owner1", "MyStore1");
        service.appointStoreManager(founder1token.getResult(), "manager1", "MyStore1");
        service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6);

        threadCount = 10000;
    }

    /***
     * use case: Product Management req 4.1:
     */
    @Test
    public void addProduct() {
        int size = service.getStoreProducts("MyStore1").getResult().size();
        assertTrue(service.addProductToStore(founder1token.getResult(), "Pepsi Cola", "Drinks", null, "less tasty drink", "MyStore1", 50, 5).getResult());
        assertEquals(size + 1, service.getStoreProducts("MyStore1").getResult().size());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1").getResult();
        boolean searchFlag = false;
        for (ProductDTO product : MyStore1Products)
            searchFlag |= product.getProductName().equals("Pepsi Cola");
        assertTrue(searchFlag);
    }

    /***
     * use case: Product Management req 4.1:
     */
    @Test
    public void addProductTwice() {
        assertTrue(service.addProductToStore(founder1token.getResult(), "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6).isWas_expected_error());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1").getResult();
        int counter = 0;
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                counter++;
        assertEquals(1, counter);
    }

    /***
     * use case: Product Management req 4.1:
     */
    @Test
    public void notManagerAddProduct() {
        int size = service.getStoreProducts("MyStore1").getResult().size();
        assertTrue(service.updateProduct(user1token.getResult(), "Crystal Cola", "Crystal Cola", "Drinks", null, "ew", "MyStore1", 100, 6).isWas_expected_error());
        assertEquals(size, service.getStoreProducts("MyStore1").getResult().size());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1").getResult();
        boolean searchFlag = false;
        for (ProductDTO product : MyStore1Products)
            searchFlag |= product.getProductName().equals("Crystal Cola");
        assertFalse(searchFlag);
    }

    /***
     * use case: Product Management req 4.1:
     */
    @Test
    public void updateProductInfo() {
        assertTrue(service.updateProduct(founder1token.getResult(), "Coca Cola", "Coca Cola", "Drinks", null, "very tasty drink", "MyStore1", 200, 6).getResult());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1").getResult();
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertEquals("very tasty drink", product.getDescription());
    }

    /***
     * use case: Product Management req 4.1:
     */
    @Test
    public void notManagerUpdateProductInfo() {
        assertTrue(service.updateProduct(user1token.getResult(), "Coca Cola", "Coca Cola", "Drinks", null, "bad drink", "MyStore1", 100, 6).isWas_expected_error());
        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1").getResult();
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertNotEquals(product.getDescription(), "bad drink");
    }

    /***
     * use case: Product Management req 4.1:
     */
    @Test
    public void removeProduct() {
        int size = service.getStoreProducts("MyStore1").getResult().size();
        assertTrue(service.removeProductFromStore(founder1token.getResult(), "Coca Cola", "MyStore1").getResult());
        assertEquals(size - 1, service.getStoreProducts("MyStore1").getResult().size());
    }

    /***
     * use case: Product Management req 4.1:
     */
    @Test
    public void removeProductTwice() {
        assertTrue(service.removeProductFromStore(founder1token.getResult(), "Coca Cola", "MyStore1").getResult());
        assertTrue(service.removeProductFromStore(founder1token.getResult(), "Coca Cola", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Product Management req 4.1:
     */
    @Test
    public void notManagerRemoveProduct() {
        int size = service.getStoreProducts("MyStore1").getResult().size();
        assertTrue(service.removeProductFromStore(user1token.getResult(), "Coca Cola", "MyStore1").isWas_expected_error());
        assertEquals(service.getStoreProducts("MyStore1").getResult().size(), size);
    }

    /***
     * use case: Appointing New Store Owner req 4.4:
     */
    @Test
    public void appointStoreOwner() {
        assertTrue(service.appointStoreOwner(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.appointStoreOwner(founder1token.getResult(), "manager2", "MyStore1").getResult());
    }

    /***
     * use case: Appointing New Store Owner req 4.4:
     */
    @Test
    public void notOwnerAppointStoreOwner() {
        assertTrue(service.appointStoreOwner(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.appointStoreOwner(manager2token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Appointing New Store Owner req 4.4:
     */
    @Test
    public void appointStoreOwnerAgain() {
        service.appointStoreOwner(founder1token.getResult(), "manager1", "MyStore1");
        assertTrue(service.appointStoreOwner(owner1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Appointing New Store Owner req 4.4:
     */
    @Test
    public void appointStoreOwnerToDifferentStore() {
        assertTrue(service.appointStoreOwner(founder1token.getResult(), "manager1", "MyStore2").isWas_expected_error());
    }

    /***
     * use case: Removing Store Owner Appointment req 4.5:
     */
    @Test
    public void removeStoreOwnerAppointment() {
        assertTrue(service.removeStoreOwnerAppointment(founder1token.getResult(), "owner1", "MyStore1").getResult());
    }


    /***
     * use case: Removing Store Owner Appointment req 4.5:
     */
    @Test
    public void chainRemoveStoreOwnerAppointment() {
        service.appointStoreOwner(founder1token.getResult(), "manager2", "MyStore1");
        service.approveOwnerAppointment(owner1token.getResult(),"manager2","MyStore1");

        service.appointStoreOwner(manager2token.getResult(), "user1", "MyStore1");
        service.approveOwnerAppointment(owner1token.getResult(),"user1","MyStore1");
        service.approveOwnerAppointment(founder1token.getResult(),"user1","MyStore1");

        assertTrue(service.removeStoreOwnerAppointment(founder1token.getResult(), "manager2", "MyStore1").getResult());

        // this should throw an expected error since user1 was appointed by manager2 and it shouldn't be a manager anymore!
        assertTrue(service.updateProduct(user1token.getResult(), "Coca Cola", "Coca Cola", "Something", null, "Something", "MyStore1", 100000, 20000.5).isWas_expected_error());
    }

    /***
     * use case: Removing Store Owner Appointment req 4.5:
     */
    @Test
    public void notOwnerRemoveStoreOwnerAppointment() {
        assertTrue(service.removeStoreOwnerAppointment(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(manager2token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(user1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(manager2token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Removing Store Owner Appointment req 4.5:
     */
    @Test
    public void otherOwnerRemoveStoreOwnerAppointment() {
        // The owner_to_remove was not appointed by Owner
        service.appointStoreOwner(owner1token.getResult(), "manager1", "MyStore1");
        assertTrue(service.removeStoreOwnerAppointment(manager1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Removing Store Owner Appointment req 4.5:
     */
    @Test
    public void removeStoreOwnerAppointmentFromDifferentStore() {
        assertTrue(service.removeStoreOwnerAppointment(founder1token.getResult(), "manager1", "MyStore2").isWas_expected_error());
        assertTrue(service.removeStoreOwnerAppointment(founder2token.getResult(), "manager1", "MyStore2").isWas_expected_error());
    }

    /***
     * use case: Appointing New Store Manager req 4.6:
     */
    @Test
    public void appointStoreManager() {
        assertTrue(service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.appointStoreManager(founder1token.getResult(), "manager2", "MyStore1").getResult());

        // A new manager can only receive questions from buyers and get store purchase history
        assertNotNull(service.receiveQuestionsFromBuyers(user1token.getResult(), "MyStore1").getResult());
        assertTrue(service.sendRespondToBuyers(user1token.getResult(), "MyStore1", "manager1", "check").getResult());
        assertEquals("A new notification from:MyStore1, Content:check", service.receiveMessages(manager1token.getResult()).getResult().get(0));

        assertNotNull(service.getStorePurchaseHistory(manager2token.getResult(), "MyStore1").getResult());
        assertTrue(service.updateProduct(manager2token.getResult(), "Crystal Cola", "Crystal Cola", "Drinks", null, "ew", "MyStore1", 100, 6).isWas_expected_error());
    }

    /***
     * use case: Appointing New Store Manager req 4.6:
     */
    @Test
    public void notOwnerAppointStoreManager() {
        assertTrue(service.appointStoreManager(user1token.getResult(), "user1", "MyStore1").isWas_expected_error());
        assertTrue(service.appointStoreManager(manager2token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Appointing New Store Manager req 4.6:
     */
    @Test
    public void appointStoreManagerAgain() {
        service.appointStoreManager(founder1token.getResult(), "user1", "MyStore1");
        assertTrue(service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Appointing New Store Manager req 4.6:
     */
    @Test
    public void appointOwnerAsStoreManager() {
        assertTrue(service.appointStoreManager(owner1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Appointing New Store Manager req 4.6:
     */
    @Test
    public void appointStoreManagerToDifferentStore() {
        assertTrue(service.appointStoreManager(founder1token.getResult(), "manager1", "MyStore2").isWas_expected_error());
    }

    /***
     * use case: Removing Store Manager Appointment req 4.8:
     */
    @Test
    public void removeStoreManagerAppointment() {
        assertTrue(service.removeStoreManagerAppointment(founder1token.getResult(), "manager1", "MyStore1").getResult());
    }

    /***
     * use case: Removing Store Manager Appointment req 4.8:
     */
    @Test
    public void notOwnerRemoveStoreManagerAppointment() {
        assertTrue(service.removeStoreManagerAppointment(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreManagerAppointment(manager2token.getResult(), "manager1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreManagerAppointment(user1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
        assertTrue(service.removeStoreManagerAppointment(manager2token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Removing Store Manager Appointment req 4.8:
     */
    @Test
    public void otherOwnerRemoveStoreManagerAppointment() {
        // The manager_to_remove was not appointed by Owner
        service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1");
        assertTrue(service.removeStoreManagerAppointment(user1token.getResult(), "owner1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Removing Store Manager Appointment req 4.8:
     */
    @Test
    public void removeStoreManagerAppointmentFromDifferentStore() {
        // this should fail - manager1 was appointed to manager by founder1 in store1 and not store 2
        assertTrue(service.removeStoreManagerAppointment(founder1token.getResult(), "manager1", "MyStore2").isWas_expected_error());

        // this should fail aswell - manager 1 isn't a manager in store 2
        assertTrue(service.removeStoreManagerAppointment(founder2token.getResult(), "manager1", "MyStore2").isWas_expected_error());
    }

    /***
     * use case: Closing A Store req 4.9:
     */
    @Test
    public void closeStore() {
        int messagesSize = service.receiveMessages(manager1token.getResult()).getResult().size();
        assertTrue(service.closeStore(founder1token.getResult(), "MyStore1").getResult());
        StoreDTO store = service.getStoreInfo("MyStore1").getResult();
        assertFalse(store.getIsActive());
        assertEquals(messagesSize + 1, service.receiveMessages(manager1token.getResult()).getResult().size());
    }

    /***
     * use case: Closing A Store req 4.9:
     */
    @Test
    public void closeInvalidStore() {
        assertTrue(service.closeStore(founder1token.getResult(), "MyStore2").isWas_expected_error());
        StoreDTO store = service.getStoreInfo("MyStore2").getResult();
        assertTrue(store.getIsActive());
    }

    /***
     * use case: Closing A Store req 4.9:
     */
    @Test
    public void closeInactiveStore() {
        service.closeStore(founder1token.getResult(), "MyStore1");
        assertTrue(service.closeStore(founder1token.getResult(), "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Closing A Store req 4.9:
     */
    @Test
    public void closeNotRealStore() {
        assertTrue(service.closeStore(founder1token.getResult(), "NotARealStore").isWas_expected_error());
    }

    /***
     * use case: Reopening A Store req 4.10:
     */
    @Test
    public void reopenStore() {
        int messagesSize = service.receiveMessages(manager1token.getResult()).getResult().size();
        service.closeStore(founder1token.getResult(), "MyStore1");
        assertTrue(service.reopenStore(founder1token.getResult(), "MyStore1").getResult());
        StoreDTO store = service.getStoreInfo("MyStore1").getResult();
        assertTrue(store.getIsActive());
        assertEquals(messagesSize + 2, service.receiveMessages(manager1token.getResult()).getResult().size());
    }

    /***
     * use case: Reopening A Store req 4.10:
     */
    @Test
    public void reopenActiveStore() {
        assertTrue(service.reopenStore(founder1token.getResult(), "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Reopening A Store req 4.10:
     */
    @Test
    public void reopenInvalidStore() {
        service.closeStore(founder1token.getResult(), "MyStore1");
        assertTrue(service.reopenStore(founder2token.getResult(), "MyStore1").isWas_expected_error());
        StoreDTO store = service.getStoreInfo("MyStore1").getResult();
        assertFalse(store.getIsActive());
    }

    /***
     * use case: Reopening A Store req 4.10:
     */
    @Test
    public void reopenNotRealStore() {
        assertTrue(service.reopenStore(founder1token.getResult(), "NotARealStore").isWas_expected_error());
    }

    /***
     * use case: Getting Store Staff Info req 4.11:
     */
    @Test
    public void getStoreStaff() {
        assertNotNull(service.getStoreStaff(founder1token.getResult(), "MyStore1").getResult());
    }

    /***
     * use case: Getting Store Staff Info req 4.11:
     */
    @Test
    public void getStoreStaffNotOwner() {
        assertTrue(service.getStoreStaff(user1token.getResult(), "MyStore1").isWas_expected_error());
        assertTrue(service.getStoreStaff(manager1token.getResult(), "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Getting Store Staff Info req 4.11:
     */
    @Test
    public void getStoreStaffFromDifferentStore() {
        assertTrue(service.getStoreStaff(founder1token.getResult(), "MyStore2").isWas_expected_error());
    }

    /***
     * use case: Changing Manager's Permissions req 4.7, 5:
     */
    @Test
    public void allowAndDisallowManagerUpdateProducts() {
        // this test fails because getStoreProducts wasn't implemented yet
        service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1");

        // default permissions include doesn't include update products permission
        assertTrue(service.allowManagerUpdateProducts(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.updateProduct(user1token.getResult(), "Coca Cola", "Coca Cola", "Drinks", null, "very tasty drink", "MyStore1", 200, 6).getResult());

        List<ProductDTO> MyStore1Products = service.getStoreProducts("MyStore1").getResult();
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertEquals("very tasty drink", product.getDescription());

        assertTrue(service.disAllowManagerUpdateProducts(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.updateProduct(user1token.getResult(), "Coca Cola", "Coca Cola", "Drinks", null, "tasty drink", "MyStore1", 100, 6).isWas_expected_error());
        MyStore1Products = service.getStoreProducts("MyStore1").getResult();
        for (ProductDTO product : MyStore1Products)
            if (product.getProductName().equals("Coca Cola"))
                assertNotEquals(product.getDescription(), "tasty drink");
    }

    /***
     * use case: Changing Manager's Permissions req 4.7, 5:
     */
    @Test
    public void allowManagerUpdateProductsToNotManager() {
        assertTrue(service.allowManagerUpdateProducts(owner1token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Changing Manager's Permissions req 4.7, 5:
     */
    @Test
    public void notOwnerAllowManagerUpdateProducts() {
        assertTrue(service.allowManagerUpdateProducts(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Changing Manager's Permissions req 4.7, 5:
     * use case: Getting Purchase History req 4.13
     */
    @Test
    public void allowAndDisallowManagerGetHistory() {
        service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1");
        assertTrue(service.allowManagerGetHistory(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertNotNull(service.getStorePurchaseHistory(user1token.getResult(), "MyStore1").getResult());

        assertTrue(service.disAllowManagerGetHistory(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.getStorePurchaseHistory(user1token.getResult(), "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Changing Manager's Permissions req 4.7, 5:
     * use case: Getting Purchase History req 4.13
     */
    @Test
    public void allowManagerGetHistoryToNotManager() {
        assertTrue(service.allowManagerGetHistory(owner1token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Changing Manager's Permissions req 4.7, 5:
     * use case: Getting Purchase History req 4.13
     */
    @Test
    public void notOwnerAllowManagerGetHistory() {
        assertTrue(service.allowManagerGetHistory(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Changing Manager's Permissions req 4.7, 5:
     * use case: Taking And Answering Clients' Requests req 4.12
     */
    @Test
    public void allowManagerAnswerAndTakeRequests() {
        service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1");
        assertTrue(service.allowManagerAnswerAndTakeRequests(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertNotNull(service.receiveQuestionsFromBuyers(user1token.getResult(), "MyStore1").getResult());
        assertTrue(service.sendRespondToBuyers(user1token.getResult(), "MyStore1", "manager1", "check").getResult());
        assertEquals("A new notification from:MyStore1, Content:check", service.receiveMessages(manager1token.getResult()).getResult().get(0));

        assertTrue(service.disAllowManagerAnswerAndTakeRequests(owner1token.getResult(), "user1", "MyStore1").getResult());
        assertTrue(service.receiveQuestionsFromBuyers(user1token.getResult(), "MyStore1").isWas_expected_error());
        assertTrue(service.sendRespondToBuyers(user1token.getResult(), "MyStore1", "manager1", "check").isWas_expected_error());
    }

    /***
     * use case: Changing Manager's Permissions req 4.7, 5:
     * use case: Taking And Answering Clients' Requests req 4.12
     */
    @Test
    public void allowManagerAnswerAndTakeRequestsToNotManager() {
        assertTrue(service.allowManagerAnswerAndTakeRequests(owner1token.getResult(), "user1", "MyStore1").isWas_expected_error());
    }

    /***
     * use case: Changing Manager's Permissions req 4.7, 5:
     * use case: Taking And Answering Clients' Requests req 4.12
     */
    @Test
    public void notOwnerAllowManagerAnswerAndTakeRequests() {
        assertTrue(service.allowManagerAnswerAndTakeRequests(user1token.getResult(), "manager1", "MyStore1").isWas_expected_error());
    }

    /***
     * concurrent test on req 4.6:
     */
    @Test
    public void concurrentAppointStoreManager() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
//        Runnable founder1AppointsUser1 = () -> {
//            Response<Boolean> resp = service.appointStoreManager(founder1token.getResult(), "user1", "MyStore1");
//            if (!resp.isError_occured())
//                counter.incrementAndGet();
//        };

        Runnable owner1AppointsUser1 = () -> {
            Response<Boolean> resp = service.appointStoreManager(owner1token.getResult(), "user1", "MyStore1");
            if (!resp.isError_occured())
                counter.incrementAndGet();
        };
//        Thread founder1AppointsUser1Thread = new Thread(founder1AppointsUser1);


        Thread[] owner1AppointsUser1Threads = new Thread[threadCount];
        for(int i=0; i < owner1AppointsUser1Threads.length;i++){
            owner1AppointsUser1Threads[i] = new Thread(owner1AppointsUser1);
        }

//        founder1AppointsUser1Thread.start();


        for(int i=0; i < owner1AppointsUser1Threads.length;i++){
            owner1AppointsUser1Threads[i].start();
        }
//        founder1AppointsUser1Thread.join();
        for(int i=0; i < owner1AppointsUser1Threads.length;i++){
            owner1AppointsUser1Threads[i].join();
        }

        assertEquals(1, counter.get());
    }

    /***
     * concurrent test on req 4.1:
     */
    @Test
    public void concurrentRemoveAndEditProduct() throws InterruptedException {
        AtomicInteger num_of_errors = new AtomicInteger(0);
        AtomicBoolean should_not_happen = new AtomicBoolean(false);
        AtomicInteger num_of_ops_until_delete = new AtomicInteger(0);
        AtomicBoolean deleted = new AtomicBoolean(false);
        Runnable founder1RemoveProduct = () -> {
            Response<Boolean> resp = service.removeProductFromStore(founder1token.getResult(), "Coca Cola", "MyStore1");
            if (resp.isError_occured()) {
                should_not_happen.set(true);
                num_of_errors.incrementAndGet();
            }
            deleted.getAndSet(true);
        };
        Runnable manager1UpdateProduct = () -> {
            Response<Boolean> resp = service.updateProduct(manager1token.getResult(), "Coca Cola", "Coca Cola", "Drinks", null, "very tasty drink", "MyStore1", 200, 6);
            if(!deleted.get()) {
                num_of_ops_until_delete.incrementAndGet();
            }
            else if (resp.isWas_expected_error()){
                num_of_errors.incrementAndGet();
            }
        };

        Thread founder1RemoveProductThread = new Thread(founder1RemoveProduct);
        Thread[] managerUpdateProductThreads = new Thread[threadCount];
        for(int i=0; i < managerUpdateProductThreads.length;i++){
            managerUpdateProductThreads[i] = new Thread(manager1UpdateProduct);
        }
        for(int i=0; i < managerUpdateProductThreads.length;i++){
            managerUpdateProductThreads[i].start();
        }
        founder1RemoveProductThread.start();

        founder1RemoveProductThread.join();
        for(int i=0; i < managerUpdateProductThreads.length;i++){
            managerUpdateProductThreads[i].join();
        }

        assertFalse(should_not_happen.get());
        assertTrue(num_of_errors.get() == threadCount-num_of_ops_until_delete.get());
    }


    @After
    public void tearDown() throws Exception {
        service = new Service(testsFactory.alwaysSuccessPayment(), testsFactory.alwaysSuccessSupplyer());
    }
}
