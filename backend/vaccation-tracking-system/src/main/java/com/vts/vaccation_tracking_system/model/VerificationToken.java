package com.vts.vaccation_tracking_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;

import java.sql.Timestamp;

public abstract class VerificationToken {
   public abstract String getToken();

   public abstract void setToken(String token);

    public abstract Timestamp getCreatedTimeStamp();

    public abstract void setCreatedTimeStamp(Timestamp createdTimeStamp);

    public abstract AbstractUser getUser();

}
