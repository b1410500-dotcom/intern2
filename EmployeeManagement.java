import java.sql.*;
import java.util.Scanner;

public class EmployeeManagement {

    static final String URL = "jdbc:mysql://localhost:3306/employee_db";
    static final String USER = "root";
    static final String PASS = "your_mysql_password";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");   // Load JDBC Driver
        } catch (Exception e) {
            System.out.println("MySQL Driver not found! Add mysql-connector-j.jar");
            return;
        }

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== EMPLOYEE MANAGEMENT SYSTEM ===");
            System.out.println("1. Admin Login");
            System.out.println("2. User Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) adminMenu(sc);
            else if (choice == 2) userMenu(sc);
            else if (choice == 3) break;
            else System.out.println("Invalid choice!");
        }
    }

    // ---------------------- ADMIN MENU ----------------------
    static void adminMenu(Scanner sc) {
        System.out.print("Enter admin password: ");
        String pass = sc.nextLine();

        if (!pass.equals("admin123")) {
            System.out.println("Invalid admin password!");
            return;
        }

        while (true) {
            System.out.println("\n=== ADMIN PANEL ===");
            System.out.println("1. Add Employee");
            System.out.println("2. Update Employee");
            System.out.println("3. Delete Employee");
            System.out.println("4. View All Employees");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1: addEmployee(sc); break;
                case 2: updateEmployee(sc); break;
                case 3: deleteEmployee(sc); break;
                case 4: viewEmployees(); break;
                case 5: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    // ---------------------- USER MENU ----------------------
    static void userMenu(Scanner sc) {
        System.out.print("Enter employee ID to login: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (!employeeExists(id)) {
            System.out.println("Employee not found!");
            return;
        }

        System.out.println("\n=== USER PANEL ===");
        System.out.println("Your details:");
        viewSingleEmployee(id);
    }

    // ---------------------- CRUD OPERATIONS ----------------------

    // ADD EMPLOYEE
    static void addEmployee(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            System.out.print("Enter name: ");
            String name = sc.nextLine();

            System.out.print("Enter role: ");
            String role = sc.nextLine();

            System.out.print("Enter salary: ");
            double salary = sc.nextDouble();
            sc.nextLine();

            String sql = "INSERT INTO employees(name, role, salary) VALUES(?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, role);
            ps.setDouble(3, salary);
            ps.executeUpdate();

            System.out.println("Employee Added Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UPDATE EMPLOYEE
    static void updateEmployee(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            System.out.print("Enter Employee ID to update: ");
            int id = sc.nextInt();
            sc.nextLine();

            if (!employeeExists(id)) {
                System.out.println("Employee not found!");
                return;
            }

            System.out.print("Enter new role: ");
            String role = sc.nextLine();

            System.out.print("Enter new salary: ");
            double salary = sc.nextDouble();
            sc.nextLine();

            String sql = "UPDATE employees SET role=?, salary=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, role);
            ps.setDouble(2, salary);
            ps.setInt(3, id);
            ps.executeUpdate();

            System.out.println("Employee Updated Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DELETE EMPLOYEE
    static void deleteEmployee(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            System.out.print("Enter Employee ID to delete: ");
            int id = sc.nextInt();
            sc.nextLine();

            String sql = "DELETE FROM employees WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            System.out.println("Employee Deleted Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // VIEW ALL EMPLOYEES
    static void viewEmployees() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM employees");

            System.out.println("\nID | Name | Role | Salary");
            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("role") + " | " +
                    rs.getDouble("salary")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // VIEW SINGLE EMPLOYEE
    static void viewSingleEmployee(int id) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            String sql = "SELECT * FROM employees WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("\nEmployee ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Role: " + rs.getString("role"));
                System.out.println("Salary: " + rs.getDouble("salary"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CHECK IF EMPLOYEE EXISTS
    static boolean employeeExists(int id) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "SELECT * FROM employees WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }
}
