package com.codegym.controller;

import com.codegym.model.Department;
import com.codegym.model.Employee;
import com.codegym.model.EmployeeForm;
import com.codegym.service.DepartmentService;
import com.codegym.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    Environment env;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @ModelAttribute("departments")
    public Page<Department> findAll(Pageable pageable) {
        return departmentService.findAll(pageable);
    }


    @GetMapping("/list")
    public ModelAndView listEmployee(@PageableDefault(sort = "salary", size = 5, direction = Sort.Direction.DESC) Pageable pageable){
        Page<Employee> employees =employeeService.findAll(pageable);
        ModelAndView modelAndView = new ModelAndView("/employee/list");
        modelAndView.addObject("employees", employees);
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/employee/create");
        modelAndView.addObject("employeeForm", new EmployeeForm());
        return modelAndView;
    }

    @PostMapping("/save")
    public ModelAndView saveEmployee(@ModelAttribute("employee") EmployeeForm employeeForm) {
        MultipartFile multipartFile = employeeForm.getAvatar();
        String fileName = multipartFile.getOriginalFilename();
        String fileUpload = env.getProperty("upload_file");
        try {
            FileCopyUtils.copy(employeeForm.getAvatar().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Employee employee = new Employee(employeeForm.getName(), employeeForm.getBirthDate()
                , employeeForm.getAddress(), employeeForm.getSalary(), fileName);
        employee.setDepartment(employeeForm.getDepartment());
        employeeService.save(employee);
        ModelAndView modelAndView = new ModelAndView("/employee/create");
        modelAndView.addObject("message", "created new employee");
        modelAndView.addObject("employeeForm", new EmployeeForm(employee.getId(), employee.getName(), employee.getBirthDate(), employee.getAddress(), employee.getSalary(), employee.getDepartment(), null));
        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditForm(@PathVariable Long id) {
        Employee employee = employeeService.findById(id);
        if (employee != null) {
            EmployeeForm employeeForm = new EmployeeForm(employee.getId(), employee.getName(),
                    employee.getBirthDate(), employee.getAddress(), employee.getSalary(), employee.getDepartment(), null);
            ModelAndView modelAndView = new ModelAndView("/employee/edit");
            modelAndView.addObject("employeeForm", employeeForm);
            modelAndView.addObject("employee", employee);
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("/error-404");
            return modelAndView;
        }
    }

    @PostMapping("/update")
    public ModelAndView updateEmployee(@ModelAttribute EmployeeForm employeeForm) {
        MultipartFile multipartFile = employeeForm.getAvatar();
        String fileName = multipartFile.getOriginalFilename();
        String fileUpload = env.getProperty("file_upload");

        try {
            FileCopyUtils.copy(employeeForm.getAvatar().getBytes(), new File(fileUpload + fileName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Employee employee = new Employee(employeeForm.getName(),
                employeeForm.getBirthDate(), employeeForm.getAddress(), employeeForm.getSalary(), fileName);
        employee.setId(employeeForm.getId());
        employee.setDepartment(employeeForm.getDepartment());
        employeeService.save(employee);
        ModelAndView modelAndView = new ModelAndView("/employee/edit");
        modelAndView.addObject("employeeForm", employeeForm);
        modelAndView.addObject("message", "Updated new employee information successfully");
        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id) {
        Employee employee = employeeService.findById(id);
        if (employee != null) {
            EmployeeForm employeeForm = new EmployeeForm(employee.getId(), employee.getName(), employee.getBirthDate(),
                    employee.getAddress(), employee.getSalary(), employee.getDepartment(), null);
            ModelAndView modelAndView = new ModelAndView("/employee/delete");
            modelAndView.addObject("employeeForm", employeeForm);
            modelAndView.addObject("employee", employee);
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("/error-404");
            return modelAndView;
        }
    }

    @PostMapping("/remove")
    public String removeCustomer(@ModelAttribute("employee") Employee employee) {
        employeeService.remove(employee.getId());
        return "redirect:/employee/list";
    }
}