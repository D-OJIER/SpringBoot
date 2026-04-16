package task.water_management.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import task.water_management.config.GlobalConfig;
import task.water_management.models.ApartmentType;
import task.water_management.models.BillReceipt;
import task.water_management.models.Guest;
import task.water_management.models.WaterAllocation;
import task.water_management.pricing.WaterSource;
import task.water_management.service.WaterBillService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The @Route("") annotation tells Spring Boot to load this page when you visit http://localhost:8080/
@Route("")
public class MainView extends VerticalLayout {

    private final WaterBillService waterBillService;
    private final GlobalConfig globalConfig;

    // We will store our dynamic UI inputs here so we can read them when the button is clicked
    private final List<IntegerField> guestDaysFields = new ArrayList<>();
    private final Map<WaterSource, IntegerField> ratioFields = new HashMap<>();

    // Spring injects our Backend Service and Global Config
    public MainView(WaterBillService waterBillService, GlobalConfig globalConfig) {
        this.waterBillService = waterBillService;
        this.globalConfig = globalConfig;

        // UI Styling
        setAlignItems(Alignment.CENTER);

        // 1. Header
        add(new H1("Apartment Water Billing System"));

        // 2. Apartment Details Section
        ComboBox<ApartmentType> apartmentDropdown = new ComboBox<>("Select Apartment Type");
        apartmentDropdown.setItems(globalConfig.getApartmentTypes());
        apartmentDropdown.setItemLabelGenerator(ApartmentType::getName); // Show the name in the dropdown
        apartmentDropdown.setRequiredIndicatorVisible(true);

        IntegerField residentsField = new IntegerField("Number of Permanent Residents");
        residentsField.setValue(0); // Default value
        residentsField.setMin(0);

        HorizontalLayout apartmentLayout = new HorizontalLayout(apartmentDropdown, residentsField);
        add(new H3("1. Apartment Details"), apartmentLayout);

        // 3. Water Ratio Section (Dynamically loads all sources EXCEPT Tanker)
        add(new H3("2. Base Water Ratio"));
        HorizontalLayout ratiosLayout = new HorizontalLayout();

        for (WaterSource source : globalConfig.getAvailableWaterSources()) {
            // We don't put Tanker in the base ratio, Tanker is only for guests!
            if (!source.getName().equals("Tanker Water")) {
                IntegerField ratioInput = new IntegerField(source.getName() + " Ratio");
                ratioInput.setValue(0);
                ratioInput.setMin(0);

                ratiosLayout.add(ratioInput);
                ratioFields.put(source, ratioInput); // Save the field so we can read it later
            }
        }
        add(ratiosLayout);

        // 4. Guests Section (Dynamic rows)
        add(new H3("3. Additional Guests"));
        VerticalLayout guestLayout = new VerticalLayout();
        guestLayout.setPadding(false);

        Button addGuestButton = new Button("Add a Guest", e -> {
            IntegerField daysStayed = new IntegerField("Days Stayed in Month (Max 30)");
            daysStayed.setMin(1);
            daysStayed.setMax(30);
            daysStayed.setValue(1);

            guestDaysFields.add(daysStayed);
            guestLayout.add(daysStayed);
        });
        add(addGuestButton, guestLayout);

        // 5. Calculate Button & Receipt Output
        add(new H3("4. Generate Bill"));
        Button calculateBtn = new Button("Calculate Final Bill");
        calculateBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // Makes the button blue

        calculateBtn.addClickListener(e -> {
            // Validate inputs
            if (apartmentDropdown.getValue() == null) {
                Notification.show("Please select an apartment type!");
                return;
            }

            // A. Gather Guests
            List<Guest> guests = new ArrayList<>();
            for (IntegerField guestField : guestDaysFields) {
                guests.add(new Guest(guestField.getValue()));
            }

            // B. Gather Ratios
            List<WaterAllocation> allocations = new ArrayList<>();
            for (Map.Entry<WaterSource, IntegerField> entry : ratioFields.entrySet()) {
                if (entry.getValue().getValue() > 0) {
                    allocations.add(new WaterAllocation(entry.getKey(), entry.getValue().getValue()));
                }
            }

            // C. Send to our Backend Engine!
            BillReceipt finalReceipt = waterBillService.generateBill(
                    apartmentDropdown.getValue(),
                    residentsField.getValue(),
                    allocations,
                    guests
            );

            // D. Show the result on screen
            Notification.show("Total Water: " + String.valueOf(finalReceipt.getTotalLitres()) + "L | Total Cost: Rs. " + finalReceipt.getTotalCost(), 5000, Notification.Position.MIDDLE);
        });

        add(calculateBtn);
    }
}