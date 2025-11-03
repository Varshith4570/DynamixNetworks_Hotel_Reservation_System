# 🏨 Hotel Reservation System - GUI Application

A feature-rich Java Swing application for managing hotel bookings, room inventory, and customer information.

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-orange)
![License](https://img.shields.io/badge/License-MIT-green)

## 🌟 Features

### Customer Portal
- ✅ **Customer Registration** - Register new customers
- 🏠 **Browse Rooms** - View available rooms with details
- 📅 **Book Reservations** - Make room bookings with date selection
- 🔍 **Search Availability** - Check room availability for specific dates
- ❌ **Cancel Bookings** - Cancel existing reservations

### Admin Panel
- 🔐 **Secure Access** - Password-protected admin panel
- 🏠 **Room Management** - Add/Remove rooms dynamically
- 👥 **Customer Management** - View all registered customers
- 📊 **Booking Reports** - Generate comprehensive statistics
- 📅 **Reservation Management** - View and manage all bookings


## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher
- Eclipse IDE (or any Java IDE)

### Installation


1. Clone the repository:
    git clone https://github.com/Varshith4570/DynamixNetworks_Hotel_Reservation_System.git

2. Open in Eclipse:
   - File → Import → Existing Projects into Workspace
   - Select the cloned folder

3. Run the application:
   - Right-click `HotelReservationGUI.java`
   - Run As → Java Application

## 🔑 Admin Credentials

- **Password**: `admin123`

## 📖 Usage

### For Customers

1. **Register**: Click "✍️ Register/Login" and fill in details
2. **Browse Rooms**: View available rooms in the "View Rooms" tab
3. **Book Room**: Click "📖 Book Room", enter Customer ID and select dates
4. **Check Availability**: Use "🔍 Search Availability" to find rooms for specific dates

### For Administrators

1. **Login**: Go to "Admin Panel" tab, enter password
2. **Manage Rooms**: Add new rooms or remove existing ones
3. **View Bookings**: See all reservations with filtering options
4. **Generate Reports**: Get statistics on bookings and revenue

## 🏗️ Project Structure

  HotelReservationGUI/
├── src/
│ └── com/dynamix/hotelreservation/
│ ├── Room.java # Room model
│ ├── Customer.java # Customer model
│ ├── Reservation.java # Reservation model
│ ├── HotelReservationModel.java # Business logic
│ └── HotelReservationGUI.java # Main GUI application
├── README.md
└── .gitignore


## 🔧 Technologies Used

- **Language**: Java
- **GUI Framework**: Swing
- **Data Storage**: Serialization (.dat files)
- **Date/Time**: Java Time API (LocalDate)
- **Collections**: ArrayList, HashMap, Streams

## 🏠 Room Types

| Type    | Capacity | Starting Price |
|---------|----------|----------------|
| Single  | 1 Guest  | ₹1,500/night   |
| Double  | 2 Guests | ₹2,500/night   |
| Suite   | 3 Guests | ₹4,000/night   |
| Deluxe  | 4 Guests | ₹5,500/night   |

## 📊 System Features

- **Date Validation** - Prevents invalid booking dates
- **Conflict Prevention** - Checks room availability before booking
- **Auto-calculation** - Calculates total cost based on nights
- **Status Tracking** - Confirmed/Cancelled reservation status
- **Data Persistence** - Auto-save/load functionality

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License.

## 👤 Author

**Your Name**
- GitHub: [@Varshith4570](https://github.com/Varshith4570)
- LinkedIn: [Varshith Reddy](www.linkedin.com/in/varshith-reddy-)

## 🙏 Acknowledgments

- Developed as part of Dynamix Networks Internship Program
- Special thanks to Dynamix Networks for the opportunity

## 📞 Contact

For any queries or suggestions, reach out at: varshithreddy4570@gmail.com

---

⭐ **If you like this project, please give it a star!** ⭐

