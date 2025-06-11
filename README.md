# Ethiopian Tourism Management System

A comprehensive Java-based Tourism Management System designed to streamline the operations of tourism agencies in Ethiopia. This system facilitates the management of tourists, tour guides, hotels, tour packages, bookings, and payments.

## 🌟 Features

- **Tourist Management**
  - Register and manage tourist information
  - Track tourist bookings and history
  - Manage passport and contact details

- **Guide Management**
  - Manage tour guides with their specializations
  - Track guide availability and assignments
  - Monitor guide performance and ratings

- **Hotel Management**
  - Maintain hotel information and amenities
  - Track hotel bookings and availability
  - Manage hotel ratings and reviews

- **Package Management**
  - Create and manage tour packages
  - Set package prices and durations
  - Track package popularity and bookings

- **Booking System**
  - Process tour bookings
  - Manage booking status and confirmations
  - Handle multiple tourist bookings

- **Payment Processing**
  - Process payments for bookings
  - Support multiple payment methods
  - Track payment status and history

## 🛠️ Technologies Used

- Java
- MySQL Database
- JDBC for database connectivity
- Swing for GUI interface

## 📋 Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- Maven (for dependency management)

## 🚀 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/ethiopian-tourism-system.git
   ```

2. Set up the database:
   ```bash
   # Run the database creation script
   mysql -u your_username -p < tourism_db.sql
   
   # Import sample data (optional)
   mysql -u your_username -p < sample_data.sql
   ```

3. Configure database connection:
   - Update the database connection properties in `src/main/resources/database.properties`
   - Set your MySQL username and password

4. Build the project:
   ```bash
   mvn clean install
   ```

## 💻 Usage

1. Run the application:
   ```bash
   java -jar target/tourism-system.jar
   ```

2. Login with default credentials:
   - Username: admin
   - Password: admin123

## 📁 Project Structure

```
JAVA-PROJECT-main/
├── com/
│   └── tourism/
│       ├── dao/           # Data Access Objects
│       ├── model/         # Entity classes
│       ├── ui/            # User Interface components
│       └── util/          # Utility classes
├── lib/                   # External libraries
├── tourism_db.sql         # Database schema
├── sample_data.sql        # Sample data
└── README.md             # This file
```

## 📊 Database Schema

The system uses the following main tables:
- `tourist` - Stores tourist information
- `guide` - Manages tour guide details
- `hotel` - Contains hotel information
- `package` - Stores tour package details
- `booking` - Manages booking information
- `payment` - Handles payment records

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Ethiopian Tourism Organization
- All contributors who have helped shape this project

## 📞 Support

For support, email support@ethiotourism.com or create an issue in the repository.

## 🔄 Updates

### Version 1.0.0
- Initial release
- Basic CRUD operations for all entities
- User authentication
- Booking management
- Payment processing

## 📸 Screenshots

[Add screenshots of your application here]

## 🔜 Roadmap

- [ ] Mobile application development
- [ ] Online payment gateway integration
- [ ] Multi-language support
- [ ] Advanced reporting features
- [ ] Guide rating system
- [ ] Tourist feedback system
