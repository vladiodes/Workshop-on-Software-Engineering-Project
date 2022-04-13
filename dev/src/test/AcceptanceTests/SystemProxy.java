package test.AcceptanceTests;

public class SystemProxy implements SystemService {
    private SystemService real;
    public SystemProxy(){
        real=Driver.getRealService();
    }
}
