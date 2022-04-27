package com.mindtree.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class EmployeeRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void addNewEmployee(String name, String email, String mobile) {

        String SQL = "INSERT INTO employee (name,email,mobile) VALUES (:name,:email,:mobile)";

        try {
            SqlParameterSource parameterSource = new MapSqlParameterSource()
                    .addValue("name", name)
                    .addValue("email", email)
                    .addValue("mobile", mobile);
            Integer rowAffected = jdbcTemplate.update(SQL, parameterSource);
            System.out.println(rowAffected);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Map<String, Object> getMangerDetails(String managerEmail, String managerPassword) {

        String SQL = "SELECT department FROM manager WHERE email = :email  AND password = :password";

        try {
            SqlParameterSource parameterSource = new MapSqlParameterSource()
                    .addValue("email", managerEmail)
                    .addValue("password", managerPassword);
            String department = jdbcTemplate.queryForObject(SQL, parameterSource, String.class);

            Map<String, Object> responseMap = new LinkedHashMap<>();
            responseMap.put("code", 200);
            responseMap.put("department", department);

            return responseMap;

        } catch (EmptyResultDataAccessException e) {
            Map<String, Object> errorMap = new LinkedHashMap<>();
            errorMap.put("code", 404);
            errorMap.put("message", "No records found for the emailId " + managerEmail);
            return errorMap;
        } catch (Exception e) {
            Map<String, Object> errorMap = new LinkedHashMap<>();
            errorMap.put("code", 500);
            errorMap.put("message", "Something went wrong while getting manger details");
            return errorMap;
        }


    }

    public Map<String, Object> updateEmp(String name, String email, String mobile, Integer employeeId, String managerDepartment) {

        //Before Updating check manger and employee department matching or not

        String SQL = "SELECT department FROM employee WHERE id = :employeeId";

        try {

            SqlParameterSource parameterSource = new MapSqlParameterSource()
                    .addValue("employeeId", employeeId);
            String empDepartment = jdbcTemplate.queryForObject(SQL, parameterSource, String.class);

            if (empDepartment.equalsIgnoreCase(managerDepartment)) {
                //Update query

                String updateSQL = "UPDATE employee SET name = :name, email = :email,mobile = :mobile " +
                        "WHERE id = :id";
                try {

                    SqlParameterSource param = new MapSqlParameterSource()
                            .addValue("name", name)
                            .addValue("email", email)
                            .addValue("mobile", mobile)
                            .addValue("id", employeeId);
                    Integer rowAffected = jdbcTemplate.update(updateSQL, param);
                    if (rowAffected == 1) {
                        Map<String, Object> responseMap = new LinkedHashMap<>();
                        responseMap.put("code", 200);
                        responseMap.put("message", "Employee updated successfully.");
                        return responseMap;

                    }
                } catch (Exception e) {
                    Map<String, Object> responseMap = new LinkedHashMap<>();
                    responseMap.put("code", 200);
                    responseMap.put("message", "Something went wrong while updating employee record " + e.getMessage());
                    return responseMap;
                }

            } else {
                Map<String, Object> responseMap = new LinkedHashMap<>();
                responseMap.put("code", 400);
                responseMap.put("message", "You can not update this employee, because department mismatch");
                return responseMap;
            }

        } catch (EmptyResultDataAccessException e) {
            Map<String, Object> responseMap = new LinkedHashMap<>();
            responseMap.put("code", 404);
            responseMap.put("message", "No employee record found");
        } catch (Exception e) {
            Map<String, Object> responseMap = new LinkedHashMap<>();
            responseMap.put("code", 500);
            responseMap.put("message", "Something went wrong while getting employee details");
            return responseMap;
        }


        return null;
    }
}
