package com.mindtree.controller;

import com.mindtree.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @PostMapping("/add")
    public void addNewEmployee(@RequestBody Map<String, String> requestBody) {

        String name = requestBody.get("name");
        String email = requestBody.get("email");
        String mobile = requestBody.get("mobile");

        employeeRepository.addNewEmployee(name, email, mobile);

        System.out.println("My name is " + name);
        System.out.println("My email is " + email);
        System.out.println("My mobile is " + mobile);

    }


    @PutMapping("/edit")
    public Map<String, Object> editEmpByManger(@RequestBody Map<String, String> requestBody) {

        String managerEmail = requestBody.get("manager_email");
        String managerPassword = requestBody.get("manager_password");
        //Check manger is exists in our database or not
        Map<String, Object> response = employeeRepository.getMangerDetails(managerEmail, managerPassword);

        if (Integer.parseInt(response.get("code").toString()) != 200) {
            return response;
        } else {
            String name = requestBody.get("name");
            String email = requestBody.get("email");
            String mobile = requestBody.get("mobile");
            Integer employeeId = Integer.valueOf(requestBody.get("employee_id"));
            String mangerDepartment = response.get("department").toString();
            //Update Employee Id
            response = employeeRepository.updateEmp(name, email, mobile, employeeId, mangerDepartment);
            return response;
        }


    }
}
