package com.example.imageloader;

import android.net.Uri;

public class Request {
    private String address;

    public String getAddress() {
        return address;
    }

    private Request(Builder builder) {
        this.address=builder.address;
    }

    public static final class Builder {
        private String address;
        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder() {
        }

        public Request build() {
            return new Request(this);
        }
    }
}
