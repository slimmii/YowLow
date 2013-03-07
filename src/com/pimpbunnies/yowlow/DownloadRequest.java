package com.pimpbunnies.yowlow;

import com.pimpbunnies.yowlow.model.Image;

public class DownloadRequest {
    private String fId;
    private Image fGuest;
        
    public DownloadRequest(String id, Image guest) {
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
    public Image getGuest() {
      return fGuest;
    }
    public void setGuest(Image fGuest) {
      this.fGuest = fGuest;
    }
    
    
}
