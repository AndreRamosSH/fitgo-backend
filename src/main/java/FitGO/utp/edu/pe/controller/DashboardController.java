package FitGO.utp.edu.pe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @GetMapping("/dashboard/admin")
    public String adminDashboard() {
        return "Admin Dashboard";
    }
}
