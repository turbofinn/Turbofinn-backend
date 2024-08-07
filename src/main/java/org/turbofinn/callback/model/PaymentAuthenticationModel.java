package org.turbofinn.callback.model;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

public class PaymentAuthenticationModel {
    private String event;
    private String entity;
    private String[] contains;
    private Payload payload;
    private String account_id;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Payload {
        private Payment payment;
        private Order order;
        private Dispute dispute;
        private Invoice invoice;
        private Token token;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Payment {
        private Entity entity;
        private String created_at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Entity {
        private String id;
        private String entity;
        private double amount;
        private String currency;
        private String status;
        private String amount_refunded;
        private String amount_deducted;
        private String refund_status;
        private String method;
        private String order_id;
        private String card_id;
        private String bank;
        private String captured;
        private String email;
        private String contact;
        private String description;
        private String error_code;
        private String internal_error_code;
        private String error_description;
        private String error_reason;
        private String error_step;
        private String error_source;
        private double fee;
        private double service_tax;
        private boolean international;
        private Object notes;
        private Object vpa;
        private String wallet;
        private String gateway_dispute_id;
        private String reason_code;
        private String respond_by;
        private String phase;
        private String created_at;
        private String amount_due;
        private String offer_id;
        private String token_id;
        private String customer_id;
        private String token;


        public Notes getNotes() {
            if (notes instanceof Notes)
                return (Notes) notes;

            if (notes instanceof Map) {
                return new Gson().fromJson(new Gson().toJsonTree(notes).getAsJsonObject(), Notes.class);
            }

            return null;
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Dispute {
        private Entity entity;
        private String created_at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Order {
        private Entity entity;
        private String created_at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Invoice {
        private Entity entity;
        private String created_at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Token {
        private Entity entity;
        private String created_at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Notes {
        private String userID;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
