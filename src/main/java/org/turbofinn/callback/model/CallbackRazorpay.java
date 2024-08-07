//package org.turbofinn.callback.model;
//
//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
//import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
//import com.google.gson.Gson;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.hc.core5.http.HttpStatus;
//
//import java.util.ArrayList;
//
//public class CallbackRazorpay implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
//    @Override
//    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
//        System.out.println(new Gson().toJson(event));
//
//        try {
//            PaymentAuthenticationModel request = TFApiRequest.from(event, PaymentAuthenticationModel.class);
//
//            handlePayment(request);
//
//            return TFApiResponse.ok();
//        } catch (Exception e) {
//            TFLog.error.log(e);
//            return TFApiResponse.error(HttpStatus.SC_INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    private void handlePayment(PaymentAuthenticationModel paymentAuthenticationModel) {
//        if (paymentAuthenticationModel != null && StringUtils.isNotBlank(paymentAuthenticationModel.getEvent())) {
//
//            switch (paymentAuthenticationModel.getEvent()) {
//                case RazorPayServerCall.PAYMENT_FAILED:
//                    break;
//                case RazorPayServerCall.PAYMENT_AUTHORIZED:
//                    break;
//
//                case RazorPayServerCall.PAYMENT_CAPTURED:
//                    addMoney(paymentAuthenticationModel);
//
//
//                case RazorPayServerCall.ORDER_PAID:
//                case RazorPayServerCall.INVOICE_PAID:
//                case RazorPayServerCall.INVOICE_EXPIRED:
//                case RazorPayServerCall.SETTLEMENT_PROCESSED:
//                case RazorPayServerCall.PAYMENT_DISPUTE_CREATED:
//                case RazorPayServerCall.PAYMENT_DISPUTE_WON:
//                case RazorPayServerCall.PAYMENT_DISPUTE_LOST:
//                case RazorPayServerCall.PAYMENT_DISPUTE_CLOSED:
//                case RazorPayServerCall.SUBSCRIPTION_CHARGED:
//                    break;
//            }
//        }
//    }
//
//    private void addMoney(PaymentAuthenticationModel paymentAuthenticationModel) {
//        String userID = paymentAuthenticationModel.getPayload().getPayment().getEntity().getNotes().getUserID();
//        long amount = (long) (paymentAuthenticationModel.getPayload().getPayment().getEntity().getAmount() / 100); //in rupees
//
//        DB_User dbUser = DB_User.fetchByUserID(userID);
//
//        DB_Wallet dbWallet = DB_Wallet.fetchByUserID(dbUser.getUserID());
//        if (dbWallet == null)
//            dbWallet = new DB_Wallet(userID, 0.0);
//
//        double currentWallet = dbWallet.getWalletAmount();
//
//        dbWallet.setWalletAmount(currentWallet + amount);
//        dbWallet.save();
//
//
//        DB_TransactionHistory dbTransactionHistory = new DB_TransactionHistory(paymentAuthenticationModel.getPayload().getPayment().getEntity().getId(),
//                dbUser.getUserID(), amount, paymentAuthenticationModel.getPayload().getPayment().getEntity().getStatus());
//        dbTransactionHistory.save();
//
//        DB_Transactions dbTransactions = new DB_Transactions(userID, amount, DB_Transactions.TransactionType.CREDIT.toString(), paymentAuthenticationModel.getPayload().getPayment().getEntity().getMethod());
//        dbTransactions.save();
//
//        ArrayList<SendOTPSms.RecipientsClass> recipientsClasses = new ArrayList<>();
//        SendOTPSms.RecipientsClass receipient = new SendOTPSms.RecipientsClass().builder()
//                .mobiles("91" + dbUser.getMobileNo())
//                .amount(String.valueOf(amount))
//                .balance(String.valueOf(dbWallet.getWalletAmount()))
//                .build();
//        recipientsClasses.add(receipient);
//        var responseStatus = SendOTPSms.sendSMS(new Gson().toJson(new SendOTPSms.SMSRequest(SMSConstants.PROD_AMOUNT_TEMPLATE_ID, "0", recipientsClasses)));
//    }
//
//    class RazorPayServerCall {
//        public static final String PAYMENT = "payment";
//        public static final String ORDER = "order";
//        public static final String INVOICE = "invoice";
//        public static final String DISPUTE = "dispute";
//        public static final String TOKEN = "token";
//        public static final String PAYMENT_FAILED = "payment.failed";
//        public static final String PAYMENT_AUTHORIZED = "payment.authorized";
//        public static final String PAYMENT_CAPTURED = "payment.captured";
//        public static final String ORDER_PAID = "order.paid";
//        public static final String INVOICE_PAID = "invoice.paid";
//        public static final String INVOICE_EXPIRED = "invoice.expired";
//        public static final String SETTLEMENT_PROCESSED = "settlement.processed";
//        public static final String PAYMENT_DISPUTE_CREATED = "payment.dispute.created";
//        public static final String PAYMENT_DISPUTE_WON = "payment.dispute.won";
//        public static final String PAYMENT_DISPUTE_LOST = "payment.dispute.lost";
//        public static final String PAYMENT_DISPUTE_CLOSED = "payment.dispute.closed";
//        public static final String SUBSCRIPTION_CHARGED = "subscription.charged";
//        public static final String TOKEN_CONFIRMED = "token.confirmed";
//    }
//}
