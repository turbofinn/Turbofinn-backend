package org.turbofinn.invoices;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import lombok.Data;
import org.turbofinn.aws.AWSCredentials;
import org.turbofinn.components.GetPresignedUrl;

public class GenerateInvoice {


    private static final String[] belowTen = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
    private static final String[] belowTwenty = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
    private static final String[] thousands = {"", "Thousand", "Million", "Billion"};
    private static final int invoiceCounter = 1;

    public static String generateInvoicePDF( GenerateInvoice.InvoiceModel model) {
        String fileName = "/tmp/"+ LocalDate.now()+"_"+UUID.randomUUID()+".pdf";

        String invoiceNumber = generateInvoiceId(invoiceCounter);
        String amountInWords = convertNumberToWords(model.totalAmount);
        String url=null;
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph companyName = new Paragraph(model.companyName, titleFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);
            Paragraph address = new Paragraph(model.companyAddress+"\n"+model.companyEmail+"\nTAX INVOICE", new Font(Font.FontFamily.HELVETICA, 12));
            address.setAlignment(Element.ALIGN_CENTER);
            document.add(address);
            document.add(new Paragraph("\n"));
            // Invoice Details Table
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.addCell(getCell("Customer Details", Font.BOLD));
            detailsTable.addCell(getCell("Invoice No: " + invoiceNumber, Font.BOLD));
            detailsTable.addCell(getCell("To:\n" + model.customerName + "\n" + model.customerAddress, Font.NORMAL));
            detailsTable.addCell(getCell("Date: Tuesday, February 28, 2023\nRef No.: Monthly Professional Charges", Font.NORMAL));
            document.add(detailsTable);
            document.add(new Paragraph("\n"));

            // Service Table
            PdfPTable serviceTable = new PdfPTable(3);
            serviceTable.setWidthPercentage(100);
            serviceTable.addCell(getCell("Service", Font.BOLD));
            serviceTable.addCell(getCell("Gross Amount", Font.BOLD));
            serviceTable.addCell(getCell("Net Total", Font.BOLD));
            serviceTable.addCell(getCell(model.serviceDescription, Font.NORMAL));
            serviceTable.addCell(getCell(model.grossAmount, Font.NORMAL));
            serviceTable.addCell(getCell(Integer.toString(model.totalAmount), Font.NORMAL));
            document.add(serviceTable);
            document.add(new Paragraph("\n"));

            // Tax and Total Amount Table
            PdfPTable taxTable = new PdfPTable(3);
            taxTable.setWidthPercentage(100);
            taxTable.addCell(getCell("Add:", Font.BOLD));
            taxTable.addCell(getCell("Tax Type", Font.BOLD));
            taxTable.addCell(getCell("Amount", Font.BOLD));
            taxTable.addCell(getCell("", Font.NORMAL));
            taxTable.addCell(getCell("IGST @ "+model.igstPercent, Font.NORMAL));
            taxTable.addCell(getCell(String.valueOf(model.igstAmount), Font.NORMAL));
            taxTable.addCell(getCell("", Font.NORMAL));
            taxTable.addCell(getCell("SGST @ "+model.sgstPercent, Font.NORMAL));
            taxTable.addCell(getCell(String.valueOf(model.sgstAmount), Font.NORMAL));
            taxTable.addCell(getCell("", Font.NORMAL));
            taxTable.addCell(getCell("CGST @ "+model.cgstPercent, Font.NORMAL));
            taxTable.addCell(getCell(String.valueOf(model.cgstAmount), Font.NORMAL));
            taxTable.addCell(getCell("Amount Payable after Tax", Font.BOLD));
            taxTable.addCell(getCell("", Font.NORMAL));
            taxTable.addCell(getCell(Integer.toString(model.totalAmount), Font.NORMAL));
            taxTable.addCell(getCell("Less: TDS @ "+model.tdsPercent, Font.BOLD));
            taxTable.addCell(getCell("", Font.NORMAL));
            taxTable.addCell(getCell(Integer.toString(model.tdsAmount), Font.NORMAL));
            taxTable.addCell(getCell("Net Amount Payable", Font.BOLD));
            taxTable.addCell(getCell("", Font.NORMAL));
            taxTable.addCell(getCell(model.netAmount + model.currency, Font.NORMAL));
            document.add(taxTable);
            document.add(new Paragraph("\nAmount in words: "+amountInWords+"\n"));

            // Bank Details
            PdfPTable bankTable = new PdfPTable(2);
            bankTable.setWidthPercentage(100);
            bankTable.addCell(getCell("BANK WIRE TRANSFER PARTICULARS", Font.BOLD));
            bankTable.addCell(getCell("For "+ model.customerCompanyName + " CONSULTANCY SERVICES", Font.BOLD));
            bankTable.addCell(getCell(model.bankDetails, Font.NORMAL));
            bankTable.addCell(getCell("\n\nPartners/Authorised Sign.", Font.NORMAL));
            document.add(bankTable);
            document.close();
            System.out.println("Invoice generated successfully in /tmp folder!");

            url = uploadFileToS3(readFileFromPath(fileName), "turbo-treats", "Invoice");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static File readFileFromPath(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file;
        } else {
            System.out.println("The file does not exist or is not a valid file.");
            return null;
        }
    }

    private static String uploadFileToS3(File file,String bucketName, String folderName) {
        System.out.println("BucketName = " + bucketName + "\nFileName = " + file.getName());
        AWSCredentials.s3Client().putObject(bucketName, folderName+"/"+file.getName(), file);
        String url = AWSCredentials.s3Client().getUrl(bucketName, folderName + "/" + file.getName()).toString();
        return url;
    }


    private static PdfPCell getCell(String text, int fontStyle) {
        Font font = new Font(Font.FontFamily.HELVETICA, 10, fontStyle);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    public static String generateInvoiceId(int invoiceCounter) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        String invoiceId = String.format("AFINV%s%03dXYZ", date, invoiceCounter);
        return invoiceId;
    }

    public static String convertNumberToWords(int num) {
        if (num == 0) {
            return "Zero";
        }

        int i = 0;
        String words = "";
        while (num > 0) {
            if (num % 1000 != 0) {
                words = convertThreeDigits(num % 1000) + thousands[i] + " " + words;
            }
            num /= 1000;
            i++;
        }
        return words.trim();
    }

    private static String convertThreeDigits(int num) {
        String str = "";
        if (num >= 100) {
            str += belowTen[num / 100] + " Hundred ";
            num %= 100;
        }
        if (num >= 20) {
            str += tens[num / 10] + " ";
            num %= 10;
        }
        if (num >= 10) {
            str += belowTwenty[num - 10] + " ";
        } else if (num > 0) {
            str += belowTen[num] + " ";
        }
        return str;
    }

    @Data
    public static class InvoiceModel {
        private String companyName;
        private String companyAddress;
        private String companyEmail;
        private String customerName;
        private String customerCompanyName;
        private String customerAddress;
        private String date;
        private String referenceNumber;
        private String serviceDescription;
        private String grossAmount;
        private int totalAmount;
        private int igstPercent;
        private int igstAmount;
        private int sgstPercent;
        private int sgstAmount;
        private int cgstPercent;
        private int cgstAmount;
        private int payableAmountAfterTax;
        private int tdsAmount;
        private int tdsPercent;
        private boolean enableTds;
        private int netAmount;
        private String bankDetails;
        private String partnersSignature;
        private String currency;

        public InvoiceModel(String companyName, String companyAddress, String companyEmail,
                            String customerName, String customerCompanyName, String customerAddress, String date,
                            String referenceNumber, String serviceDescription, String grossAmount, int totalAmount,
                            int igstPercent, int igstAmount, int sgstPercent, int sgstAmount, int cgstPercent, int cgstAmount, int payableAmountAfterTax,
                            int tdsAmount, int tdsPercent, boolean enableTds, int netAmount, String bankDetails,
                            String partnersSignature, String currency) {

            this.companyName = companyName;
            this.companyAddress = companyAddress;
            this.companyEmail = companyEmail;
            this.customerName = customerName;
            this.customerCompanyName = customerCompanyName;
            this.customerAddress = customerAddress;
            this.date = date;
            this.referenceNumber = referenceNumber;
            this.serviceDescription = serviceDescription;
            this.grossAmount = grossAmount;
            this.totalAmount = totalAmount;
            this.igstAmount = igstAmount;
            this.igstPercent = igstPercent;
            this.sgstAmount = sgstAmount;
            this.sgstPercent = sgstPercent;
            this.cgstAmount = cgstAmount;
            this.cgstPercent = cgstPercent;
            this.payableAmountAfterTax = payableAmountAfterTax;
            this.tdsAmount = tdsAmount;
            this.tdsPercent = tdsPercent;
            this.enableTds = enableTds;
            this.netAmount = netAmount;
            this.bankDetails = bankDetails;
            this.partnersSignature = partnersSignature;
            this.currency = currency;
        }
    }

}