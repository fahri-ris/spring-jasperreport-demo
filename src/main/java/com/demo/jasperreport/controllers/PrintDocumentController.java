package com.demo.jasperreport.controllers;

import com.demo.jasperreport.services.PrintDocumentServiceImpl;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/print")
public class PrintDocumentController {
    PrintDocumentServiceImpl printDocumentService;

    @Autowired
    public PrintDocumentController(PrintDocumentServiceImpl printDocumentService) {
        this.printDocumentService = printDocumentService;
    }

    @GetMapping("/{pdf}")
    public ResponseEntity<Resource> getProductReport(@PathVariable String pdf) throws JRException, IOException{
        byte[] reportContent = printDocumentService.getProductReport(pdf);

        ByteArrayResource resource = new ByteArrayResource(reportContent);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("product-report." + pdf)
                                .build().toString())
                .body(resource);
    }
}
