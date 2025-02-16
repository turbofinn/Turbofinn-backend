package org.turbofinn.razorPayUtil;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.turbofinn.dbmappers.DB_Payments;
import org.turbofinn.invoices.GenerateInvoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class RazorpayCallbacks implements RequestHandler<Object, Object>{

    public static void main(String[] args) {
        System.out.println(new Gson().toJson(new RazorpayCallbacks().handleRequest(new Gson().fromJson("{\n" +
                "    \"entity\": \"event\",\n" +
                "    \"account_id\": \"acc_OXPV1QJc4bK9Dz\",\n" +
                "    \"event\": \"payment.captured\",\n" +
                "    \"contains\": [\n" +
                "        \"payment\"\n" +
                "    ],\n" +
                "    \"payload\": {\n" +
                "        \"payment\": {\n" +
                "            \"entity\": {\n" +
                "                \"id\": \"pay_PQ2yJ02EdeF7tW\",\n" +
                "                \"entity\": \"payment\",\n" +
                "                \"amount\": 1000,\n" +
                "                \"currency\": \"INR\",\n" +
                "                \"status\": \"captured\",\n" +
                "                \"order_id\": \"order_PwSiqF3Aqp3RXc\",\n" +
                "                \"international\": false,\n" +
                "                \"method\": \"upi\",\n" +
                "                \"amount_refunded\": 0,\n" +
                "                \"captured\": true,\n" +
                "                \"description\": \"A motherly hospitality\",\n" +
                "                \"vpa\": \"saurabhmaurya155@okaxis\",\n" +
                "                \"contact\": \"+918960880615\",\n" +
                "                \"notes\": [],\n" +
                "                \"fee\": 24,\n" +
                "                \"tax\": 4,\n" +
                "                \"acquirer_data\": {\n" +
                "                    \"rrn\": \"433201509528\"\n" +
                "                },\n" +
                "                \"created_at\": 1732648875,\n" +
                "                \"upi\": {\n" +
                "                    \"payer_account_type\": \"bank_account\",\n" +
                "                    \"vpa\": \"saurabhmaurya155@okaxis\"\n" +
                "                },\n" +
                "                \"base_amount\": 1000\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"created_at\": 1732648876\n" +
                "}",Request.class),null)));
    }
    @Override
    public Object handleRequest(Object request, Context context) {
        String requestString = new Gson().toJson(request);
        System.out.println("requestString "+ requestString);
        Request input = new Gson().fromJson(requestString, Request.class);
        System.out.println("VideoKycCallbackIdfyRequest "+ input);

        if(input!=null && input.payload!=null && input.payload.payment!=null&& input.payload.payment.entity!=null){
            Entity entity = input.payload.payment.entity;
            DB_Payments dbPayments = DB_Payments.fetchPaymentsByOrderID(entity.order_id);
            if (dbPayments!=null ) {
                dbPayments.setPaymentDate(LocalDate.now().toString());
                dbPayments.setPaymentMode(entity.method);
                dbPayments.setPaymentStatus(DB_Payments.PaymentStatus.PAID.toString());
                dbPayments.save();
            }
            GenerateInvoice.InvoiceModel invoiceModel =  createInvoiceFromJson(new Gson().toJson(input.payload.payment.entity));
            System.out.println(new Gson().toJson(invoiceModel));
            String url = GenerateInvoice.generateInvoicePDF(invoiceModel);
        }else {
            System.out.println("Something went wrong");
        }
        return null;
    }

    public static GenerateInvoice.InvoiceModel createInvoiceFromJson(String jsonString) {
        System.out.println(new Gson().toJson(jsonString));
        JSONObject payment = new JSONObject(jsonString);

        int totalAmount = payment.getInt("amount");
        String currency = payment.getString("currency");
        String serviceDescription = payment.getString("description");
        String referenceNumber = payment.getString("id");
        String date = String.valueOf(payment.getLong("created_at"));
        String grossAmount = String.valueOf(totalAmount);
        String companyName = "Algoflow AI Pvt. Ltd.";
        String companyAddress = "Basaratpur Adalpura Chunar Mirzapur, Uttar Pradesh 231304, India";
        String companyEmail = "info@turbofinn.com";
        String customerName = "";
        String customerCompanyName = "";
        String customerAddress = "";
        int igstPercent = 0;
        int igstAmount = 0;
        int sgstPercent = 0;
        int sgstAmount = 0;
        int cgstPercent = 0;
        int cgstAmount = 0;
        int payableAmountAfterTax = 0;
        int tdsAmount = 0;
        int tdsPercent = 0 ;
        boolean enableTds = true;
        int netAmount = 0;
        String bankDetails = "";
        String partnersSignature = "";

        JSONArray notes = payment.getJSONArray("notes");
        for (int i = 0; i < notes.length(); i++) {
            JSONObject note = notes.getJSONObject(i);
            if (note.has("customerName")) {
                customerName = note.getString("customerName");
            }
            if (note.has("customerCompanyName")) {
                customerCompanyName = note.getString("customerCompanyName");
            }
            if (note.has("customerAddress")) {
                customerAddress = note.getString("customerAddress");
            }
            if (note.has("igstPercent")) {
                igstPercent = note.getInt("igstPercent");
            }
            if (note.has("igstAmount")) {
                igstAmount = note.getInt("igstAmount");
            }
            if (note.has("sgstPercent")) {
                sgstPercent = note.getInt("sgstPercent");
            }
            if (note.has("sgstAmount")) {
                sgstAmount = note.getInt("sgstAmount");
            }
            if (note.has("cgstPercent")) {
                cgstPercent = note.getInt("cgstPercent");
            }
            if (note.has("cgstAmount")) {
                cgstAmount = note.getInt("cgstAmount");
            }
            if (note.has("payableAmountAfterTax")) {
                payableAmountAfterTax = note.getInt("payableAmountAfterTax");
            }
            if (note.has("tdsPercent")) {
                tdsPercent = note.getInt("tdsPercent");
            }
            if (note.has("tdsAmount")) {
                tdsAmount = note.getInt("tdsAmount");
            }
            if (note.has("netAmount")) {
                netAmount = note.getInt("netAmount");
            }
            if (note.has("bankDetails")) {
                bankDetails = note.getString("bankDetails");
            }
            if (note.has("partnersSignature")) {
                partnersSignature = note.getString("partnersSignature");
            }
        }

        return new GenerateInvoice.InvoiceModel(companyName, companyAddress, companyEmail, customerName, customerCompanyName, customerAddress,
                date, referenceNumber, serviceDescription, grossAmount, totalAmount, igstPercent, igstAmount, sgstPercent,
                sgstAmount, cgstPercent, cgstAmount, payableAmountAfterTax, tdsAmount, tdsPercent, enableTds, netAmount, bankDetails,
                partnersSignature, currency);
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    class Request {
        public String entity;
        public String account_id;
        public String event;
        public String[] contains;
        public Payload payload;
        public long created_at;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    class Payload {
        public Payment payment;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    class Payment {
        public Entity entity;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    class Entity {
        public String id;
        public String entity;
        public int amount;
        public String currency;
        public String status;
        public String order_id;
        public boolean international;
        public String method;
        public int amount_refunded;
        public boolean captured;
        public String description;
        public String vpa;
        public String contact;
        public String[] notes;
        public int fee;
        public int tax;
        public AcquirerData acquirer_data;
        public long created_at;
        public UPI upi;
        public int base_amount;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    class AcquirerData {
        public String rrn;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    class UPI {
        public String payer_account_type;
        public String vpa;
    }
}

