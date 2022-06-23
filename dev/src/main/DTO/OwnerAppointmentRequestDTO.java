package main.DTO;

import main.Stores.OwnerAppointmentRequest;
import main.Users.User;

import java.util.LinkedList;
import java.util.List;

public class OwnerAppointmentRequestDTO {

    String requestedBy;
    String userToAppoint;
    List<String> approvedBy;  // TODO: check if needed
    public OwnerAppointmentRequestDTO(OwnerAppointmentRequest req) {
        this.requestedBy = req.getRequestedBy().getUserName();
        this.userToAppoint = req.getUserToAppoint().getUserName();
        approvedBy = new LinkedList<>();
        for(User u : req.getApprovedBy()) {
            this.approvedBy.add(u.getUserName());
        }
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getUserToAppoint() {
        return userToAppoint;
    }

    public List<String> getApprovedBy() {
        return approvedBy;
    }
}
