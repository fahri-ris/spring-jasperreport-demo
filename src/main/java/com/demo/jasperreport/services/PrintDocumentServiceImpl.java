package com.demo.jasperreport.services;

import com.demo.jasperreport.models.Products;
import com.demo.jasperreport.repositories.ProductsRepository;
import net.sf.jasperreports.components.items.Item;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRJpaDataSource;
import net.sf.jasperreports.engine.util.JRSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrintDocumentServiceImpl {
    ProductsRepository productsRepository;

    @Autowired
    public PrintDocumentServiceImpl(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public byte[] getProductReport(String format) {
        try{
            List<Products> products = productsRepository.findAll();
            File file = ResourceUtils.getFile("classpath:product-report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRSaver.saveObject(jasperReport, "product-report.jrxml");

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(products);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("title", "Item Report");

            //Fill report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            byte[] reportContent = JasperExportManager.exportReportToPdf(jasperPrint);
            return reportContent;
        } catch(FileNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        } catch(JRException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

}
