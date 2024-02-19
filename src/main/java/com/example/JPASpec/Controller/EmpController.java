package com.example.JPASpec.Controller;

import com.example.JPASpec.Entity.EmpSearch;
import com.example.JPASpec.Entity.Employee;
import com.example.JPASpec.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
public class EmpController {
    @Autowired
    EmployeeService employeeService;

    @PostMapping("/spec")
    public ResponseEntity<?> findByEmpSpec(@RequestBody EmpSearch empSearch) {
        return ResponseEntity.ok(employeeService.findByEmpSpec(empSearch));
    }

    @PostMapping("/genExcel")
    public ResponseEntity<?> findAllByEmpSpec(@RequestBody EmpSearch empSearch) throws IOException {

        ByteArrayOutputStream excelFile = employeeService.generateExcel(empSearch);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Employee.csv"); // Set the file name

        return new ResponseEntity<>(excelFile.toByteArray(), headers, 200);

//        employeeService.generateExcel(empSearch);
//        return ResponseEntity.ok().body("Done");
    }

//    @PostMapping("/pageExcel")
//    public ResponseEntity<?> findAllByEmpSpecPage(@RequestBody EmpSearch empSearch, Pageable pageable) throws IOException {
//        employeeService.pageGenerateExcel(empSearch, pageable, i);
//        return ResponseEntity.ok().body("Done");
//    }

//    @PostMapping("/asyncPage")
//    public CompletableFuture<?> findAllBySpecPageAsync(@RequestBody EmpSearch empSearch, Pageable pageable) throws IOException {
//        CompletableFuture<Page<Employee>> employees = employeeService.pageGenerateExcelThread(empSearch, pageable);
//        return CompletableFuture.completedFuture(employees);
//    }

    @PostMapping("/asyncTest")
    public CompletableFuture<?> findAllBySpecPageThread(@RequestBody EmpSearch empSearch, Pageable pageable) {
        CompletableFuture<Void> future = employeeService.pageGenerateExcelAsync(empSearch, pageable);
        future.join();
        return CompletableFuture.completedFuture(future);
    }
}
