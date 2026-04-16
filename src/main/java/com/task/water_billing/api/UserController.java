package com.task.water_billing.api;

import com.task.water_billing.property.entity.Apartment;
import com.task.water_billing.telemetry.entity.DailyLog;
import com.task.water_billing.telemetry.repository.DailyLogRepository;
import com.task.water_billing.user.entity.User;
import com.task.water_billing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final DailyLogRepository logRepo;

    @GetMapping("/bills/{username}")
    public List<DailyLog> getUserBills(@PathVariable String username) {

        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Apartment apartment = user.getApartment();

        return logRepo.findByApartmentId(apartment.getId());
    }
}