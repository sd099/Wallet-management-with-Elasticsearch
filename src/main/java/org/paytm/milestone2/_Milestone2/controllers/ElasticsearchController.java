//package org.paytm.milestone2._Milestone2.controllers;
//
//import org.paytm.milestone2._Milestone2.DTO.Response.ElasticsearchTransactionBody;
//import org.paytm.milestone2._Milestone2.services.ElasticsearchService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//public class ElasticsearchController {
//
//    ElasticsearchService elasticsearchService;
//
//    @RequestMapping(value = "/elasticsearch/transaction/{phoneNumber}", method = RequestMethod.GET)
//    public List<ElasticsearchTransactionBody> getTransactionByPhoneNumber(@PathVariable String phoneNumber){
//        return elasticsearchService.getTransactionByPhoneNumber(phoneNumber);
//    }
//
//}
