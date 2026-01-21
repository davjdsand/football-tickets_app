import java.util.Random;

public class TestLoginFlow {

    public static void main(String[] args) {
        System.out.println("🧪 STARTING INTEGRATION TEST: Database Login Flow...");

        // 1. Generate a random user so we don't get "Duplicate User" errors
        Random rand = new Random();
        String testUser = "Tester_" + rand.nextInt(10000);
        String testPass = "secret123";

        System.out.println("   Attempting to register: " + testUser);

        // 2. Test Registration
        User newUser = Database.signUp(testUser, testPass);

        if (newUser == null) {
            System.out.println("❌ CRITICAL FAIL: Database.signUp returned null.");
            return;
        } else {
            System.out.println("   Registration OK. User ID: " + newUser.toString());
        }

        // 3. Test Login with the same credentials
        System.out.println("   Attempting to login with new credentials...");
        User loggedUser = Database.checkLogin(testUser, testPass);

        if (loggedUser != null && loggedUser.getUsername().equals(testUser)) {
            System.out.println("✅ DATABASE LOGIN TEST PASSED!");
            System.out.println("   (User was created and retrieved successfully from PostgreSQL)");
        } else {
            System.out.println("❌ FAIL: Login returned null or wrong user.");
        }
    }
}