package com.pimpbunnies.yowlow;

import com.pimpbunnies.yowlow.model.Guest;

public class DownloadRequest {
    private String fId;
    private Guest fGuest;
        
    public DownloadRequest(String id, Guest guest) {
      super();
      this.fId = id;
      this.fGuest = guest;
    }
    
    public String getId() {
      return fId;
    }
    public void setId(String fId) {
      this.fId = fId;
    }
    public Guest getGuest() {
      return fGuest;
    }
    public void setGuest(Guest fGuest) {
      this.fGuest = fGuest;
    }
    
    
}
