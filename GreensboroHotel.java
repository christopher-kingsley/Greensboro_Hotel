// Ahmad Khader
// Christopher Kingsley
// COMP 360
// November 30 Due Date
// Project 3 Greensboro Hotel

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.ArrayList;


// Custom Exception for NoRoom scenario
class NoRoomException extends Exception {
    private static final long serialVersionUID = 1L;

    public NoRoomException(String message) {
        super(message);
    }
}

// Greensboro Hotel Reservation System
public class GreensboroHotel {

    private final Map<Integer, List<Reservation>> reservations; // Map of room numbers to their reservations
    private final Map<Integer, String> pins; // Map to store pins for rooms
    private final String ADMIN_PIN = "0000"; // Admin PIN code for verification
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Reservation class to store guest name, PIN, and date range
    class Reservation {
        String name;
        String pin;
        Date startDate;
        Date endDate;

        public Reservation(String name, String pin, Date startDate, Date endDate) {
            this.name = name;
            this.pin = pin;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public boolean overlaps(Date start, Date end) {
            return !(end.before(startDate) || start.after(endDate));
        }
    }

    // Constructor to initialize rooms
    public GreensboroHotel() {
        reservations = new HashMap<>();
        pins = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 10; j++) {
                int roomNumber = i * 100 + j;
                reservations.put(roomNumber, new ArrayList<>());
                pins.put(roomNumber, null); // Pins are initially null
            }
        }
    }

    // Method to reserve a room
    public void reserveRoom(int roomNumber, String name, String pin, Date startDate, Date endDate) throws NoRoomException {
        if (!reservations.containsKey(roomNumber)) {
            throw new NoRoomException("Room number " + roomNumber + " does not exist.");
        }
        if (pin == null || pin.length() != 4 || !pin.matches("\\d{4}")) {
            throw new NoRoomException("Invalid PIN. PIN must be a 4-digit number.");
        }

        // Check for overlapping reservations
        for (Reservation r : reservations.get(roomNumber)) {
            if (r.overlaps(startDate, endDate)) {
                throw new NoRoomException("Room number " + roomNumber + " is already reserved for the selected dates.");
            }
        }

        // Add reservation
        reservations.get(roomNumber).add(new Reservation(name, pin, startDate, endDate));
        JOptionPane.showMessageDialog(null, "Room " + roomNumber + " reserved for " + name + " from " + dateFormat.format(startDate) + " to " + dateFormat.format(endDate) + ".");
    }

    // Method to cancel a reservation
    public void cancelReservation(int roomNumber, String pin) {
        if (!reservations.containsKey(roomNumber)) {
            JOptionPane.showMessageDialog(null, "Room number " + roomNumber + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Find and remove reservation
        Iterator<Reservation> iterator = reservations.get(roomNumber).iterator();
        while (iterator.hasNext()) {
            Reservation r = iterator.next();
            if (pin.equals(ADMIN_PIN) || pin.equals(r.pin)) {
                iterator.remove();
                JOptionPane.showMessageDialog(null, "Reservation for room " + roomNumber + " cancelled.");
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "Incorrect PIN. Reservation not cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean hotelFull() {
        for (Map.Entry<Integer, List<Reservation>> entry : reservations.entrySet()) {
            // If any room has space for a reservation, the hotel is not full
            if (entry.getValue().size() == 0) {
                return false;
            }
        }
        return true;
    }
    
    // GUI Method to interact with the user
    public void startGUI() {
        JFrame frame = new JFrame("Greensboro Hotel Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(152, 251, 152)); // Light pine green color

        JLabel titleLabel = new JLabel("Greensboro Hotel");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(150, 10, 300, 30);
        panel.add(titleLabel);

        JLabel roomLabel = new JLabel("Select Room:");
        roomLabel.setBounds(50, 70, 100, 25);
        panel.add(roomLabel);

        JComboBox<Integer> roomDropdown = new JComboBox<>();
        for (int room : reservations.keySet()) {
            roomDropdown.addItem(room);
        }
        roomDropdown.setBounds(150, 70, 100, 25);
        panel.add(roomDropdown);

        JLabel nameLabel = new JLabel("Enter Name:");
        nameLabel.setBounds(50, 110, 100, 25);
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(150, 110, 200, 25);
        panel.add(nameField);

        JLabel pinLabel = new JLabel("Enter PIN (4 digits):");
        pinLabel.setBounds(50, 150, 150, 25);
        panel.add(pinLabel);

        JTextField pinField = new JTextField();
        pinField.setBounds(200, 150, 100, 25);
        panel.add(pinField);

        JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
        startDateLabel.setBounds(50, 190, 200, 25);
        panel.add(startDateLabel);

        JTextField startDateField = new JTextField();
        startDateField.setBounds(250, 190, 100, 25);
        panel.add(startDateField);

        JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
        endDateLabel.setBounds(50, 230, 200, 25);
        panel.add(endDateLabel);

        JTextField endDateField = new JTextField();
        endDateField.setBounds(250, 230, 100, 25);
        panel.add(endDateField);

        JButton reserveButton = new JButton("Reserve Room");
        reserveButton.setBounds(150, 270, 150, 30);
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int roomNumber = (int) roomDropdown.getSelectedItem();
                    String name = nameField.getText();
                    String pin = pinField.getText();
                    Date startDate = dateFormat.parse(startDateField.getText());
                    Date endDate = dateFormat.parse(endDateField.getText());

                    if (endDate.before(startDate)) {
                        throw new IllegalArgumentException("End date cannot be before start date.");
                    }

                    reserveRoom(roomNumber, name, pin, startDate, endDate);

                    // Check if hotel is full after the reservation
                    if (hotelFull()) {
                        JOptionPane.showMessageDialog(frame, "The hotel is now fully booked!", "Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (NoRoomException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(reserveButton);

        frame.add(panel);
        frame.setVisible(true);

        // Check if hotel is full at startup
        if (hotelFull()) {
            JOptionPane.showMessageDialog(frame, "The hotel is fully booked at the moment!", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        GreensboroHotel hotel = new GreensboroHotel();
        hotel.startGUI();
    }
}
