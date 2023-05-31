package com.omeganessy.payroll.exception;

public class EmployeeNotFoundException extends RuntimeException{
    public EmployeeNotFoundException(Long id){
        super("Could not retrieve employee with id " + id);
    }
}
