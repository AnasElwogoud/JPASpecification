package com.example.JPASpec.Service;

import com.example.JPASpec.Entity.EmpSearch;
import com.example.JPASpec.Entity.Employee;
import com.example.JPASpec.Repository.EmployeeRepo;
import com.example.JPASpec.Repository.EmployeeSpec;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepo employeeRepo;

    Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    public List<Employee> findByEmpSpec(EmpSearch empSearch) {
        EmployeeSpec eSpec = new EmployeeSpec(empSearch);
        return employeeRepo.findAll(eSpec);
    }

    public ByteArrayOutputStream generateExcel(EmpSearch empSearch) throws IOException {
        EmployeeSpec eSpec = new EmployeeSpec(empSearch);
        List<Employee> employees = employeeRepo.findAll(eSpec);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Employees");
        HSSFRow row = sheet.createRow(0);
//        Header Row
        row.createCell(0).setCellValue("ID");
        row.createCell(1).setCellValue("First Name");
        row.createCell(2).setCellValue("Last Name");
        row.createCell(3).setCellValue("Department Id");
        row.createCell(4).setCellValue("Department Name");
        row.createCell(5).setCellValue("Phone Number");
//        Data Row
        int dataRowIndex = 1;
        for (Employee employee : employees) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(employee.getId());
            dataRow.createCell(1).setCellValue(employee.getFirstName());
            dataRow.createCell(2).setCellValue(employee.getLastName());
            dataRow.createCell(3).setCellValue(employee.getDepartment().getId());
            dataRow.createCell(4).setCellValue(employee.getDepartment().getDepName());
            dataRow.createCell(5).setCellValue(employee.getPhoneNumber());
            dataRowIndex++;
        }
//        OutputStream stream = new FileOutputStream("E:/Work's-Tasks/JPASpecification/Output/Employee.csv");
//        workbook.write(stream);
//        workbook.close();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream;
    }

    public CompletableFuture<Void> pageGenerateExcelAsync(EmpSearch empSearch, Pageable pageable) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int totalPages = getTotalPages(pageable.getPageSize(), empSearch);
        System.out.println("total:" + totalPages);
        CompletableFuture[] futuresArr = new CompletableFuture[totalPages];
        for (int i = 0; i < totalPages; i++) {
            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    pageGenerateExcel(empSearch, pageable, finalI);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, executorService);
            futuresArr[i] = future;
        }
        return CompletableFuture.allOf(futuresArr);
    }

    public int getTotalPages(int pageSize, EmpSearch empSearch) {
        EmployeeSpec eSpec = new EmployeeSpec(empSearch);
        long totalCount = employeeRepo.count(eSpec);
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    public void pageGenerateExcel(EmpSearch empSearch, Pageable pageable, int i) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        EmployeeSpec eSpec = new EmployeeSpec(empSearch);
        int pageSize = pageable.getPageSize();
        Pageable pageRequest = PageRequest.of(i, pageSize);
        Page<Employee> employees = employeeRepo.findAll(eSpec, pageRequest);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Employees");
        HSSFRow row = sheet.createRow(0);
//        Header Row
        row.createCell(0).setCellValue("ID");
        row.createCell(1).setCellValue("First Name");
        row.createCell(2).setCellValue("Last Name");
        row.createCell(3).setCellValue("Department Id");
        row.createCell(4).setCellValue("Department Name");
        row.createCell(5).setCellValue("Phone Number");
//        Data Row
        int dataRowIndex = 1;
        for (Employee employee : employees) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex);
            dataRow.createCell(0).setCellValue(employee.getId());
            dataRow.createCell(1).setCellValue(employee.getFirstName());
            dataRow.createCell(2).setCellValue(employee.getLastName());
            dataRow.createCell(3).setCellValue(employee.getDepartment().getId());
            dataRow.createCell(4).setCellValue(employee.getDepartment().getDepName());
            dataRow.createCell(5).setCellValue(employee.getPhoneNumber());
            dataRowIndex++;
        }
        OutputStream stream = new FileOutputStream("E:/Work's-Tasks/JPASpecification/Output/Employee_" + i + ".csv");
        workbook.write(stream);
        workbook.close();
        stopWatch.stop();
        System.out.println("Done");
        logger.info("{}, Time: {}s", Thread.currentThread().getName(), stopWatch.getTotalTimeSeconds());
    }


    @Async("myThreadPool")
    public CompletableFuture<Page<Employee>> pageGenerateExcelThread(EmpSearch empSearch, Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                EmployeeSpec eSpec = new EmployeeSpec(empSearch);
                Page<Employee> employees = employeeRepo.findAll(eSpec, pageable);
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("Employees");
                HSSFRow row = sheet.createRow(0);
                // Header Row
                row.createCell(0).setCellValue("ID");
                row.createCell(1).setCellValue("First Name");
                row.createCell(2).setCellValue("Last Name");
                row.createCell(3).setCellValue("Department Id");
                row.createCell(4).setCellValue("Department Name");
                row.createCell(5).setCellValue("Phone Number");
                // Data Row
                int dataRowIndex = 1;
                for (Employee employee : employees) {
                    HSSFRow dataRow = sheet.createRow(dataRowIndex);
                    dataRow.createCell(0).setCellValue(employee.getId());
                    dataRow.createCell(1).setCellValue(employee.getFirstName());
                    dataRow.createCell(2).setCellValue(employee.getLastName());
                    dataRow.createCell(3).setCellValue(employee.getDepartment().getId());
                    dataRow.createCell(4).setCellValue(employee.getDepartment().getDepName());
                    dataRow.createCell(5).setCellValue(employee.getPhoneNumber());
                    dataRowIndex++;
                }
                OutputStream stream = new FileOutputStream("E:/Work's-Tasks/JPASpecification/Output/Employee.csv");
                workbook.write(stream);
                workbook.close();

                return employees;
            } catch (IOException e) {
                throw new RuntimeException("Error generating Excel", e);
            }
        });
    }
}
