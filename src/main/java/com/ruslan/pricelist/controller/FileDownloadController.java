package com.ruslan.pricelist.controller;

import com.ruslan.pricelist.service.FileStorageService;
import com.ruslan.pricelist.service.NomenclatureService;
import com.ruslan.pricelist.service.PriceComparisonService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ruslan on 1/2/2017.
 */
@Controller
@RequestMapping("/download")
public class FileDownloadController {
    @Autowired
    PriceComparisonService comparisonService;
    @Autowired
    NomenclatureService nomenclatureService;
    @Autowired
    FileStorageService fileStorageService;

    @RequestMapping(value = "/{file_name}", method = RequestMethod.GET)
    public void getFile(
            @PathVariable("file_name") String fileName, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            StringBuffer dataDirectory = new StringBuffer(request.getServletContext().getRealPath("/WEB-INF/downloads/"));
            File file = new File(dataDirectory.append(fileName).append(".xlsx").toString());
            //check whether the request for comparison sheet
            if(fileName.equals("comparisonResult")){
                nomenclatureService.generateNomenclaturesByPriceLists(fileStorageService.getAllDistributors());
                comparisonService.generateDistributorsPriceListComparison(nomenclatureService.getNomenclatures(),fileStorageService.getAllDistributors(),file);
            }
            // get your file as InputStream
            if(file.exists()){
                InputStream is = new FileInputStream(file);;
                // copy it to response's OutputStream
                org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

    }

}
