package main.Publisher;


import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Notification {
    @Id
    @GeneratedValue
    private int id;
    public abstract String print();
}
