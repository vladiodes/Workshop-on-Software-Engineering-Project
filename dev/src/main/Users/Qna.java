package main.Users;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Qna {
    @Id
    @GeneratedValue
    private int id;
    private String question;
    private String answer;

    public Qna(){

    }
    public Qna(String question,String answer){
        this.question=question;
        this.answer=answer;
    }
}
