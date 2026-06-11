package com.resolum.intiva.platform.household.application.internal.outboundservices;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
public class QrCodeGeneratorService {

    public String generateQrBase64(String content, int width, int height) {
        try {
            var qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            var pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngBytes = pngOutputStream.toByteArray();
            log.debug("QR code generated successfully for content length: {}", content.length());
            return Base64.getEncoder().encodeToString(pngBytes);
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code for content: {}", content, e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}
