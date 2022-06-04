package main.Users;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


public class Qna {

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
