package org.turbofinn.razorPayUtil;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.turbofinn.callback.model.PaymentAuthenticationModel;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entity {
    private String id;
    private String entity;
    private BigDecimal amount;
    private String currency;
    private String status;
    private BigDecimal amount_refunded;
    private BigDecimal amount_deducted;
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
    private BigDecimal fee;
    private BigDecimal service_tax;
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
    private String token_id; // Ensuring distinct naming
    private String customer_id;
    private String token;

    public PaymentAuthenticationModel.Notes getNotes() {
        if (notes instanceof PaymentAuthenticationModel.Notes)
            return (PaymentAuthenticationModel.Notes) notes;

        if (notes instanceof Map) {
            return new Gson().fromJson(new Gson().toJsonTree(notes).getAsJsonObject(), PaymentAuthenticationModel.Notes.class);
        }

        return null;
    }
}
