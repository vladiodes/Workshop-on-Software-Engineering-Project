package test.AcceptanceTests;


import main.Service.IService;
import main.Service.Service;
import main.utils.Response;
import org.apache.xpath.operations.Bool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import test.testUtils.testsFactory;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AT_Req2_4_4_Owner_appointment {
    private Response<String> founder,owner1,owner2,owner3,owner4,manager;
    private String storeName,password;
    private int threadCount;
    IService service;
    @Before
    public void setUp() throws Exception {
        threadCount = 10000;
        service = new Service(testsFactory.alwaysSuccessPayment(),testsFactory.alwaysSuccessSupplyer());
        storeName = "testingStore";
        founder = service.guestConnect();
        owner1 = service.guestConnect();
        owner2 = service.guestConnect();
        owner3 = service.guestConnect();
        owner4 = service.guestConnect();
        manager = service.guestConnect();
        password = "123456";

        service.register("founder",password);
        service.register("owner1",password);
        service.register("owner2",password);
        service.register("owner3",password);
        service.register("owner4",password);

        service.register("manager",password);

        service.login(founder.getResult(),"founder",password);
        service.login(owner1.getResult(),"owner1",password);
        service.login(owner2.getResult(),"owner2",password);
        service.login(owner3.getResult(),"owner3",password);
        service.login(owner4.getResult(),"owner4",password);

        service.login(manager.getResult(),"manager",password);

        service.openStore(founder.getResult(), storeName);
        service.appointStoreManager(founder.getResult(),"manager",storeName);
    }

    @Test
    public void appointOwnerWithoutAnyExtraApprovalNeeded() {
        service.appointStoreOwner(founder.getResult(),"owner1",storeName);
        Assertions.assertTrue(service.getAllStoresOfUser(owner1.getResult()).getResult().size() == 1);
    }

    @Test
    public void appointOwnerWithOneOwnerApprovalNeeded() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        service.appointStoreOwner(founder.getResult(),"owner2",storeName);
        Assertions.assertTrue(service.getAllStoresOfUser(owner2.getResult()).getResult().size() == 0);
        service.approveOwnerAppointment(owner1.getResult(), "owner2", storeName);
        Assertions.assertTrue(service.getAllStoresOfUser(owner2.getResult()).getResult().size() == 1);
    }

    @Test
    public void appointStoreOwnerAfterRemainingOwnerToApproveWasRemoved() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        service.appointStoreOwner(founder.getResult(),"owner2",storeName);
        Assertions.assertTrue(service.getAllStoresOfUser(owner2.getResult()).getResult().size() == 0);
        service.removeStoreOwnerAppointment(founder.getResult(),"owner1",storeName);
        Assertions.assertTrue(service.getAllStoresOfUser(owner2.getResult()).getResult().size() == 1);
    }

    @Test
    public void verifyAppointmentFailedAfterOneDeclinedVote() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        appointOwnerWithOneOwnerApprovalNeeded();
        service.appointStoreOwner(founder.getResult(),"owner3",storeName);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner1.getResult(), storeName).getResult().size() == 1);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner2.getResult(), storeName).getResult().size() == 1);

        service.approveOwnerAppointment(owner1.getResult(), "owner3",storeName);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner1.getResult(), storeName).getResult().size() == 0);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner2.getResult(), storeName).getResult().size() == 1);
        Assertions.assertTrue(service.getAllStoresOfUser(owner3.getResult()).getResult().size() == 0);

        service.declineOwnerAppointment(owner2.getResult(), "owner3", storeName);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner1.getResult(), storeName).getResult().size() == 0);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner2.getResult(), storeName).getResult().size() == 0);

        Assertions.assertTrue(service.getAllStoresOfUser(owner3.getResult()).getResult().size() == 0);
    }

    @Test
    public void testOwnerMustApproveRequestsThatWereMadeBeforeHisAppointment() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        service.appointStoreOwner(founder.getResult(),"owner2",storeName);
        service.appointStoreOwner(founder.getResult(),"owner3",storeName);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner1.getResult(), storeName).getResult().size() == 2);
        service.approveOwnerAppointment(owner1.getResult(), "owner2", storeName);
        Assertions.assertTrue(service.getAllStoresOfUser(owner2.getResult()).getResult().size() == 1);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner2.getResult(), storeName).getResult().size() == 1);
        Assertions.assertTrue(service.getAllStoresOfUser(owner3.getResult()).getResult().size() == 0);
        service.approveOwnerAppointment(owner1.getResult(), "owner3", storeName);
        Assertions.assertTrue(service.getAllStoresOfUser(owner3.getResult()).getResult().size() == 0);
        service.approveOwnerAppointment(owner2.getResult(), "owner3", storeName);
        Assertions.assertTrue(service.getAllStoresOfUser(owner3.getResult()).getResult().size() == 1);

        // verify no more requests exist
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner1.getResult(), storeName).getResult().size() == 0);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner2.getResult(), storeName).getResult().size() == 0);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(owner3.getResult(), storeName).getResult().size() == 0);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(founder.getResult(), storeName).getResult().size() == 0);
    }

    @Test
    public void testApprovedOwnerCanRequestToAppointNewOwners() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        service.appointStoreOwner(owner1.getResult(),"owner2",storeName);
        Assertions.assertTrue(service.getOwnerAppointmentRequests(founder.getResult(), storeName).getResult().size() == 1);
        service.approveOwnerAppointment(founder.getResult(), "owner2",storeName);
        Assertions.assertTrue(service.getAllStoresOfUser(owner2.getResult()).getResult().size() == 1);
    }

    @Test
    public void testManagersCannotVoteOnOwnerAppointmentRequests() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        service.appointStoreOwner(founder.getResult(), "owner2",storeName);
        Response<String> response = service.approveOwnerAppointment(manager.getResult(), "owner2",storeName);
        Assertions.assertTrue(response.isError_occured() && response.isWas_expected_error());
    }

    @Test
    public void appointOwnerCircularity() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        service.appointStoreOwner(owner1.getResult(), "owner2", storeName);
        service.approveOwnerAppointment(founder.getResult(), "owner2",storeName);
        service.appointStoreOwner(owner2.getResult(), "owner3", storeName);
        service.approveOwnerAppointment(founder.getResult(), "owner3",storeName);
        service.approveOwnerAppointment(owner1.getResult(), "owner3",storeName);
        Response<Boolean> response =  service.appointStoreOwner(owner3.getResult(), "owner1",storeName);
        Assertions.assertTrue(response.isError_occured() && response.isWas_expected_error());
    }


    @Test
    public void removeOwnerAppointmentGoodNoChain() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        Response<Boolean> response = service.removeStoreOwnerAppointment(founder.getResult(),"owner1",storeName);
        Assertions.assertTrue(response.getResult());
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner1.getResult()).getResult().size());
    }

    @Test
    public void removeOwnerAppointmentGoodWithChain() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        service.appointStoreOwner(owner1.getResult(), "owner2",storeName);
        service.approveOwnerAppointment(founder.getResult(),"owner2",storeName);
        service.appointStoreOwner(owner2.getResult(), "owner3",storeName);
        service.approveOwnerAppointment(founder.getResult(),"owner3",storeName);
        service.approveOwnerAppointment(owner1.getResult(),"owner3",storeName);
        service.appointStoreOwner(owner3.getResult(), "owner4",storeName);
        service.approveOwnerAppointment(founder.getResult(),"owner4",storeName);
        service.approveOwnerAppointment(owner1.getResult(),"owner4",storeName);
        service.approveOwnerAppointment(owner2.getResult(),"owner4",storeName);
        service.removeStoreOwnerAppointment(founder.getResult(),"owner1",storeName);
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner1.getResult()).getResult().size());
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner2.getResult()).getResult().size());
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner3.getResult()).getResult().size());
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner4.getResult()).getResult().size());
    }

    @Test
    public void removeOwnerAppointmentGoodWithChainIncludingManagers() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        service.appointStoreOwner(owner1.getResult(), "owner2",storeName);
        service.approveOwnerAppointment(founder.getResult(), "owner2",storeName);
        service.appointStoreManager(owner2.getResult(), "owner3",storeName);
        Response<Boolean> response = service.removeStoreOwnerAppointment(founder.getResult(), "owner1",storeName);
        Assertions.assertTrue(response.getResult());
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner1.getResult()).getResult().size());
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner2.getResult()).getResult().size());
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner3.getResult()).getResult().size());

    }

    @Test
    public void removeOwnerAppointmentGoodWithForkedChain() {
        appointOwnerWithoutAnyExtraApprovalNeeded();
        service.appointStoreOwner(founder.getResult(), "owner2",storeName);
        service.approveOwnerAppointment(owner1.getResult(), "owner2",storeName);
        service.appointStoreOwner(owner1.getResult(), "owner3",storeName);
        service.approveOwnerAppointment(founder.getResult(), "owner3",storeName);
        service.approveOwnerAppointment(owner2.getResult(), "owner3",storeName);
        service.appointStoreOwner(owner2.getResult(), "owner4",storeName);
        service.approveOwnerAppointment(founder.getResult(), "owner4",storeName);
        service.approveOwnerAppointment(owner1.getResult(), "owner4",storeName);
        service.approveOwnerAppointment(owner3.getResult(), "owner4",storeName);
        Response<Boolean> response = service.removeStoreOwnerAppointment(founder.getResult(), "owner1",storeName);
        Assertions.assertTrue(response.getResult());
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner1.getResult()).getResult().size());
        Assertions.assertEquals(1,service.getAllStoresOfUser(owner2.getResult()).getResult().size());
        Assertions.assertEquals(0,service.getAllStoresOfUser(owner3.getResult()).getResult().size());
        Assertions.assertEquals(1,service.getAllStoresOfUser(owner4.getResult()).getResult().size());
    }

    @Test
    public void ConcurrentAppointSameUser() throws InterruptedException{
        AtomicInteger successCounter = new AtomicInteger(0);
        Runnable appointStoreOwner = () -> {
            Response<Boolean> response = service.appointStoreOwner(founder.getResult(), "owner1", storeName);
            if(!response.isError_occured()) {
                successCounter.getAndIncrement();
            }
        };
        Thread[] appointStoreOwnerThreads = new Thread[threadCount];
        for(int i=0;i<threadCount;i++) {
            appointStoreOwnerThreads[i] = new Thread(appointStoreOwner);
        }
        for(int i=0;i<threadCount;i++) {
            appointStoreOwnerThreads[i].start();
        }
        for(int i=0;i<threadCount;i++) {
            appointStoreOwnerThreads[i].join();
        }
        assertEquals(1,successCounter.get());
    }


    @After
    public void tearDown() throws Exception {
        service = new Service(testsFactory.alwaysSuccessPayment(), testsFactory.alwaysSuccessSupplyer());
    }
}
