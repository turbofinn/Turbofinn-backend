package org.turbofinn.razorPayUtil;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

public class PaymentAuthenticationModel {
    private String event;
    private String entity;
    private String[] contains;
    private Payload payload;
    private String account_id;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String[] getContains() {
        return contains;
    }

    public void setContains(String[] contains) {
        this.contains = contains;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public boolean validate() {
        return (StringUtils.isNoneBlank(entity, event, account_id) && contains !=null && contains.length > 0 && payload != null);
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }



    public class Payload {
        private Payment payment;
        public Payment getPayment() {
            return payment;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    public class Payment {
        private Entity entity;
        private String created_at;

        public Entity getEntity() {
            return entity;
        }

        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }


    public class RazorPayPaymentUpdateInput {

        private String body;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public RazorPayPaymentUpdateInput(String body) {
            super();
            this.body = body;
        }

        public RazorPayPaymentUpdateInput() {
            super();
            // TODO Auto-generated constructor stub
        }

        @Override
        public String toString() {
            return "RazorPayPaymentUpdateInput [body=" + body + "]";
        }


    }



}

