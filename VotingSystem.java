import java.sql.*;
import java.util.Scanner;

public class VotingSystem {

    static final String URL = "jdbc:mysql://localhost:3306/voting_db";
    static final String USER = "root";
    static final String PASS = "your_mysql_password";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n=== ONLINE VOTING SYSTEM ===");
                System.out.println("1. Register Voter");
                System.out.println("2. Add Candidate");
                System.out.println("3. Vote");
                System.out.println("4. Show Results");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();
                sc.nextLine();

                switch (ch) {
                    case 1: registerVoter(sc); break;
                    case 2: addCandidate(sc); break;
                    case 3: castVote(sc); break;
                    case 4: showResults(); break;
                    case 5: System.exit(0);
                    default: System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1) Register voter
    static void registerVoter(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter voter name: ");
            String name = sc.nextLine();
            System.out.print("Enter voter ID number: ");
            String voterId = sc.nextLine();

            String sql = "INSERT INTO voters(name, voter_id) VALUES(?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, voterId);
            ps.executeUpdate();

            System.out.println("Voter registered successfully!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 2) Add candidate
    static void addCandidate(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.print("Enter candidate name: ");
            String name = sc.nextLine();
            System.out.print("Enter party: ");
            String party = sc.nextLine();

            String sql = "INSERT INTO candidates(name, party, votes) VALUES(?, ?, 0)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, party);
            ps.executeUpdate();

            System.out.println("Candidate added successfully!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 3) Cast vote
    static void castVote(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

            System.out.print("Enter your Voter ID: ");
            String voterId = sc.nextLine();

            // Check if already voted
            String check = "SELECT has_voted FROM voters WHERE voter_id=?";
            PreparedStatement ps = con.prepareStatement(check);
            ps.setString(1, voterId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Voter not found!");
                return;
            }
            if (rs.getInt("has_voted") == 1) {
                System.out.println("You already voted!");
                return;
            }

            // Show candidates
            System.out.println("\nCandidates:");
            Statement st = con.createStatement();
            ResultSet rs2 = st.executeQuery("SELECT * FROM candidates");
            while (rs2.next()) {
                System.out.println(rs2.getInt("id") + ". " + rs2.getString("name") + " (" + rs2.getString("party") + ")");
            }

            System.out.print("Enter candidate ID to vote: ");
            int cid = sc.nextInt();
            sc.nextLine();

            // Add vote
            String voteSql = "UPDATE candidates SET votes = votes + 1 WHERE id=?";
            PreparedStatement ps2 = con.prepareStatement(voteSql);
            ps2.setInt(1, cid);
            ps2.executeUpdate();

            // Mark voter as voted
            String updateVoter = "UPDATE voters SET has_voted=1 WHERE voter_id=?";
            PreparedStatement ps3 = con.prepareStatement(updateVoter);
            ps3.setString(1, voterId);
            ps3.executeUpdate();

            System.out.println("Vote successfully cast!");

        } catch (Exception e) { e.printStackTrace(); }
    }

    // 4) Show results
    static void showResults() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM candidates ORDER BY votes DESC");

            System.out.println("\n=== RESULTS ===");
            while (rs.next()) {
                System.out.println(rs.getString("name") + " (" + rs.getString("party") + ") - Votes: " + rs.getInt("votes"));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
