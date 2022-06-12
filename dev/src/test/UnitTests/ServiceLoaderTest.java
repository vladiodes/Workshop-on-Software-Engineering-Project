package test.UnitTests;

import main.DTO.UserDTO;
import main.Service.IService;
import main.Service.ServiceLoader;
import main.utils.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.Null;
import org.mockito.internal.stubbing.defaultanswers.ReturnsSmartNulls;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class ServiceLoaderTest {

    @Mock
    IService service;
    String dummyToken = "Some token";
    @BeforeEach
    void setUp() {
        service = mock(IService.class);
        when(service.guestConnect()).thenReturn(new Response<>(dummyToken));
        when(service.logout(any(String.class))).thenReturn(new Response<>(true));
        when(service.register(any(String.class), any(String.class))).thenReturn(new Response<>(true));
        when(service.login(any(String.class), any(String.class), any(String.class))).thenReturn(new Response<>(null));
        when(service.openStore(any(String.class), any(String.class))).thenReturn(new Response<>(null));
        when(service.addProductToStore(any(String.class), any(String.class),any(String.class), any(List.class), any(String.class), any(String.class),any(int.class), any(double.class))).thenReturn(new Response<>(null));
        when(service.appointStoreManager(any(String.class), any(String.class),any(String.class))).thenReturn(new Response<>(null));
        when(service.appointStoreOwner(any(String.class), any(String.class),any(String.class))).thenReturn(new Response<>(null));
        when(service.allowManagerUpdateProducts(any(String.class), any(String.class),any(String.class))).thenReturn(new Response<>(null));
    }

    @Test
    void BarRecordingloadFromFile(){
        Assertions.assertDoesNotThrow(()->ServiceLoader.loadFromFile("BarRecording.json", service));
        verify(service, times(6)).guestConnect();
        verify(service, times(6)).register(any(String.class), any(String.class));
        verify(service, times(6)).login(any(String.class), any(String.class), any(String.class));
        verify(service, times(1)).openStore(dummyToken, "s1");
        verify(service, times(1)).addProductToStore(dummyToken,"Bamba", "Snack", new ArrayList<>(), "Nut snack", "s1", 20, 30.0);
        verify(service, times(1)).appointStoreManager(dummyToken, "u3", "s1");
        verify(service, times(1)).allowManagerUpdateProducts(dummyToken, "u3", "s1");
        verify(service, times(1)).appointStoreOwner(dummyToken, "u4", "s1");
        verify(service, times(1)).appointStoreOwner(dummyToken, "u5", "s1");
    }

    @Test
    void ThrowExceptionWhenFail(){
        when(service.guestConnect()).thenReturn(new Response<>(new Exception(), false));
        Assertions.assertThrows(IllegalArgumentException.class, () ->ServiceLoader.loadFromFile("BarRecording.json", service));
    }
}