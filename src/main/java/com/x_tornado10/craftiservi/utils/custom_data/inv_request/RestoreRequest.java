package com.x_tornado10.craftiservi.utils.custom_data.inv_request;

import java.util.Objects;
import java.util.UUID;

public class RestoreRequest {

    private final UUID Requester;
    private UUID Reviewer;
    private final String InvName;
    boolean approved = false;
    boolean reviewed = false;

    public RestoreRequest(UUID requester, String name) {
        Requester = requester;
        InvName = name;
    }
    public UUID getRequester() {
        return Requester;
    }
    public UUID getReviewer() {
        return Reviewer;
    }
    public String getInvName() {
        return InvName;
    }
    public boolean isApproved() {return approved;}
    public void setApproved(boolean approved) {this.approved = approved;}
    public void setReviewer(UUID reviewer) {
        Reviewer = reviewer;
    }
    public boolean isReviewed() {return reviewed;}
    public void setReviewed(boolean reviewed) {this.reviewed = reviewed;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoreRequest that = (RestoreRequest) o;
        if (that.getInvName().equals("*")) {
            return Objects.equals(Requester, that.Requester);
        } else {
            return Objects.equals(Requester, that.Requester) &&
                    Objects.equals(InvName, that.InvName);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(Requester, InvName);
    }
}
